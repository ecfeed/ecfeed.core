/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

package com.ecfeed.core.generators.algorithms;

import java.util.*;

import com.ecfeed.core.generators.DimensionedItem;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.utils.*;
import com.google.common.collect.*;


public class AwesomeNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E> {

    private Multiset<SortedMap<Integer, E>> fPartialNTuples = null;

    private List<DimensionedItem<E>> fAllDimensionedItems = null;

    private IntegerHolder fIgnoreCount;

    private IntegerHolder fPartialNTuplesCount;

    private int fDimCount;

    static final int NUM_OF_REPETITIONS = 100;

    public AwesomeNWiseAlgorithm(int n, int coverage) {
        super(n, coverage);
    }

    @Override
    public void reset() {

        fIgnoreCount = new IntegerHolder(0);
        fDimCount = getInput().size();

        try {
            fAllDimensionedItems = createDimensionedItems(getInput());

            List<SortedMap<Integer, E>> remainingTuples = getAllNTuples(getInput(), N);

            fPartialNTuplesCount = new IntegerHolder(remainingTuples.size());

            fPartialNTuples = createPartialTuples(remainingTuples);

            fIgnoreCount.set(fPartialNTuplesCount.get() * (100 - getCoverage()) / 100);

        } catch (Exception e) {

            SystemLogger.logCatch(e);

            ExceptionHelper.reportRuntimeException("Generator reset failed.", e);
        }

        setTaskBegin(fPartialNTuplesCount.get() * getCoverage() / 100); // TODO - repeated setTaskBegin

        super.reset();
    }

    @Override
    public List<E> getNext() throws GeneratorException {

        IEcfProgressMonitor generatorProgressMonitor = getGeneratorProgressMonitor();

        return getBestMaxTuple(generatorProgressMonitor);
    }

    private Multiset<SortedMap<Integer, E>> createPartialTuples(List<SortedMap<Integer, E>> remainingTuples) {

        Multiset<SortedMap<Integer, E>> fPartialTuplesCounter = HashMultiset.create();

        for (SortedMap<Integer, E> remainingTuple : remainingTuples) {

            final List<List<Map.Entry<Integer, E>>> allSublists =
                    AlgorithmHelper.getAllSublists(new ArrayList<>(remainingTuple.entrySet()));

            for (List<Map.Entry<Integer, E>> sublist : allSublists) {

                fPartialTuplesCounter.add(createOneCounter((List<Map.Entry<Integer, E>>) sublist));
            }
        }

        return fPartialTuplesCounter;
    }

    private List<DimensionedItem<E>> createDimensionedItems(List<List<E>> input) {

        int dimCount = getInput().size();

        List<DimensionedItem<E>> result = new ArrayList<>();

        for (int dimension = 0; dimension < dimCount; dimension++) {
            for (E value : input.get(dimension)) {
                result.add(new DimensionedItem<>(dimension, value));
            }
        }

        return result;
    }

    private List<E> getBestMaxTuple(IEcfProgressMonitor generatorProgressMonitor) {

        SortedMap<Integer, E> bestTuple = null;
        int bestTupleScore = -1;

        for (int repetition = 0; repetition < NUM_OF_REPETITIONS; repetition++) { // TODO - limit number of repetitions for small tuples table

            if (isGenerationCancelled(generatorProgressMonitor)) {
                return null;
            }

            if (fPartialNTuplesCount.get() <= fIgnoreCount.get()) {
                setTaskEnd();
                return null;
            }

            SortedMap<Integer, E> nTuple = createNTuple();

            int nTupleScore = calculateScoreForNTuple(nTuple);

            if (nTupleScore > bestTupleScore) {
                bestTupleScore = nTupleScore;
                bestTuple = nTuple;
            }
        }

        removeAffectedTuples(bestTuple, fPartialNTuples, fPartialNTuplesCount);
        incrementProgress(bestTupleScore);  // TODO - by score ?

        return AlgorithmHelper.uncompressTuple(bestTuple, fDimCount);
    }

    private SortedMap<Integer, E> createNTuple() {

        SortedMap<Integer, E> nTuple = createNTupleWithBestScores();

        SortedMap<Integer, E> maxTuple = findBestMaxTupleAfterConstraints(nTuple);

        return maxTuple;
    }

    private SortedMap<Integer, E> findBestMaxTupleAfterConstraints(SortedMap<Integer, E> nTuple) {

        SortedMap<Integer, E> resultMaxTuple = nTuple;

        List<Integer> tupleDimensions = createTupleDimensions(resultMaxTuple);

        List<Integer> randomDimensions = createRandomDimensions(fDimCount);

        for (Integer dimension : randomDimensions) {

            if (resultMaxTuple.containsKey(dimension))
                continue;

            E bestElement = findBestItemWithConstraintCheck(dimension, resultMaxTuple, tupleDimensions);

            resultMaxTuple.put(dimension, bestElement);
            tupleDimensions.add(dimension);
        }

        return resultMaxTuple;
    }

    private E findBestItemWithConstraintCheck(
            Integer dimension,
            SortedMap<Integer, E> nTuple,
            List<Integer> tupleDimensions) {

        List<E> inputForOneDimension = new ArrayList<>(getInput().get(dimension));
        Collections.shuffle(inputForOneDimension);

        Set<List<Integer>> dimensionsToCountScores =
                (new Tuples<>(tupleDimensions, Math.min(tupleDimensions.size(), N - 1))).getAll();

        int bestScore = -1;
        E bestItem = null;

        for (E item : inputForOneDimension) {

            nTuple.put(dimension, item);

            if (checkConstraints(AlgorithmHelper.uncompressTuple(nTuple, fDimCount)) == EvaluationResult.TRUE) {

                int score = calculateTupleScoreForOneDimension(nTuple, dimension, dimensionsToCountScores, item);

                if (score > bestScore) {
                    bestScore = score;
                    bestItem = item;
                }
            }
        }

        return bestItem;
    }

    private int calculateTupleScoreForOneDimension(
            SortedMap<Integer, E> nTuple,
            Integer dimension,
            Set<List<Integer>> dimensionsToCountScores,
            E item) {

        int score = 0;

        for (List<Integer> dimensionScores : dimensionsToCountScores) {

            SortedMap<Integer, E> tmpObject = Maps.newTreeMap();

            for (Integer sD : dimensionScores) // TODO - names ?
                tmpObject.put(sD, nTuple.get(sD));

            tmpObject.put(dimension, item);

            if (fPartialNTuples.contains(tmpObject))
                score++;
        }

        return score;
    }

    private SortedMap<Integer, E> createNTupleWithBestScores() {

        SortedMap<Integer, E> tuple = Maps.newTreeMap();

        final int countOfDimensions = Math.min(N, fDimCount);

        for (int dim = 0; dim < countOfDimensions; dim++) {

            Collections.shuffle(fAllDimensionedItems);
            DimensionedItem<E> bestItem = null;

            int bestTupleScore = -1;

            for (DimensionedItem<E> dItem : fAllDimensionedItems) {

                Integer dimension = dItem.getDimension();

                if (tuple.containsKey(dimension))
                    continue;

                tuple.put(dimension, dItem.getItem());

                final int tupleScore = fPartialNTuples.count(tuple);

                if (tupleScore > bestTupleScore) {
                    bestItem = dItem;
                    bestTupleScore = tupleScore;
                }

                tuple.remove(dimension);
            }

            Integer dimension = bestItem.getDimension();

            tuple.put(dimension, bestItem.getItem());
        }

        return tuple;
    }

    List<Integer> createTupleDimensions(SortedMap<Integer, E> nTuple) {

        List<Integer> dimensions = new ArrayList<>();

        for (Map.Entry<Integer, E> entry : nTuple.entrySet()) {

            dimensions.add(entry.getKey());
        }

        return dimensions;
    }

    private List<Integer> createRandomDimensions(int fDimCount) {

        List<Integer> randomDimensions = new ArrayList<>();

        for (int i = 0; i < fDimCount; i++)
            randomDimensions.add(i);

        Collections.shuffle(randomDimensions);
        return randomDimensions;
    }

    private void removeAffectedTuples(
            SortedMap<Integer, E> affectingTuple,
            Multiset<SortedMap<Integer, E>> outPartialTuples,
            IntegerHolder outRemainingTuplesCount) {

        for (List<Integer> dimCombinations : getAllDimensionCombinations(fDimCount, N)) {

            SortedMap<Integer, E> dTuple = Maps.newTreeMap();

            for (Integer dimension : dimCombinations)
                dTuple.put(dimension, affectingTuple.get(dimension));

            if (outPartialTuples.contains(dTuple)) {
                outRemainingTuplesCount.decrement();

                for (List<Map.Entry<Integer, E>> sublist : AlgorithmHelper.getAllSublists(new ArrayList<>(dTuple.entrySet())))
                    outPartialTuples.remove(createOneCounter(sublist), 1);
            }
        }
    }

    private ImmutableSortedMap<Integer, E> createOneCounter(List<Map.Entry<Integer, E>> sublist) {

        return new ImmutableSortedMap.Builder<Integer, E>(Ordering.natural()).putAll(sublist).build();
    }

    private int calculateScoreForNTuple(SortedMap<Integer, E> nTuple) {

        int score = 0;

        final Set<List<Integer>> allDimensionCombinations = getAllDimensionCombinations(fDimCount, N);

        for (List<Integer> combinationOfDimensions : allDimensionCombinations) {

            SortedMap<Integer, E> dTuple = Maps.newTreeMap();

            for (Integer dimension : combinationOfDimensions)
                dTuple.put(dimension, nTuple.get(dimension));

            if (fPartialNTuples.contains(dTuple))
                score++;
        }

        return score;
    }

    private Set<List<Integer>> getAllDimensionCombinations(int dimensionCount, int argN) {

        List<Integer> dimensions = new ArrayList<>();

        for (int i = 0; i < dimensionCount; i++)
            dimensions.add(i);

        return (new Tuples<>(dimensions, Math.min(dimensionCount, argN))).getAll();
    }

    private List<SortedMap<Integer, E>> getAllNTuples(List<List<E>> input, int argN) {

        int dimensionCount = getInput().size();

        List<SortedMap<Integer, E>> allValidTuples = new ArrayList<>();
        allValidTuples.add(Maps.newTreeMap());

        for (int tupleSize = 0; tupleSize < argN; tupleSize++) {

            List<SortedMap<Integer, E>> newValidTuples = new ArrayList<>();

            if (tupleSize == argN - 1) {
                setTaskBegin(allValidTuples.size()); // TODO - repeated setTaskBegin
            }

            for (SortedMap<Integer, E> tuple : allValidTuples) {

                Integer maxDimension = -1;

                if (!tuple.isEmpty()) {
                    maxDimension = tuple.lastKey();
                }

                addValidTuples(input, argN, dimensionCount, tupleSize, maxDimension, tuple, newValidTuples);

                incrementProgress(1); // TODO - repeated progress
            }

            allValidTuples = newValidTuples; // TODO - do we need 2 variables ? why do we assign (what for did we calculate previous result ?)
        }

        return allValidTuples;
    }

    private void addValidTuples(
            List<List<E>> input,
            int argN, int dimensionCount,
            int tupleSize,
            Integer maxDimension,
            SortedMap<Integer, E> tuple,
            List<SortedMap<Integer, E>> inOutValidTuples) {

        for (int dimension = maxDimension + 1; dimension < dimensionCount - (argN - 1 - tupleSize); dimension++) {

            final List<E> inputForOneDimension = input.get(dimension);

            addTuplesForOneDimension(dimension, inputForOneDimension, tuple, dimensionCount, inOutValidTuples);

            tuple.remove(dimension);
        }
    }

    private void addTuplesForOneDimension(
            int dimension,
            List<E> inputForOneDimension,
            SortedMap<Integer, E> tuple,
            int dimensionCount,
            List<SortedMap<Integer, E>> inOutTuples) {

        for (E v : inputForOneDimension) {

            tuple.put(dimension, v);

            if (checkConstraints(AlgorithmHelper.uncompressTuple(tuple, dimensionCount)) == EvaluationResult.TRUE) {
                SortedMap<Integer, E> newTuple = new TreeMap<>(tuple);
                inOutTuples.add(newTuple);
            }
        }
    }

    private boolean isGenerationCancelled(IEcfProgressMonitor generatorProgressMonitor) {

        if (generatorProgressMonitor == null) {
            return false;
        }

        if (generatorProgressMonitor.isCanceled()) {
            return true;
        }

        return false;
    }

}