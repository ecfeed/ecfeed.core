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

    private Multiset<SortedMap<Integer, E>> fPartialTuples = null;

    private List<DimensionedItem<E>> fAllDimensionedItems = null;

    private IntegerHolder fIgnoreCount;

    private IntegerHolder fRemainingTuplesCount;

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
            fAllDimensionedItems = createAllDimensionedItems(getInput());

            List<SortedMap<Integer, E>> remainingTuples = getAllNTuples(getInput(), N);

            fRemainingTuplesCount = new IntegerHolder(remainingTuples.size());

            fPartialTuples = createPartialTuples(remainingTuples);

            fIgnoreCount.set(fRemainingTuplesCount.get() * (100 - getCoverage()) / 100);

        } catch (Exception e) {

            SystemLogger.logCatch(e);

            ExceptionHelper.reportRuntimeException("Generator reset failed.", e);
        }

        setTaskBegin(fRemainingTuplesCount.get() * getCoverage() / 100); // TODO - repeated setTaskBegin

        super.reset();
    }

    @Override
    public List<E> getNext() throws GeneratorException {

        IEcfProgressMonitor generatorProgressMonitor = getGeneratorProgressMonitor();

        return getBestTuple(generatorProgressMonitor);
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

    private List<DimensionedItem<E>> createAllDimensionedItems(List<List<E>> input) {

        int dimCount = getInput().size();

        List<DimensionedItem<E>> result = new ArrayList<>();

        for (int dimension = 0; dimension < dimCount; dimension++) {
            for (E value : input.get(dimension)) {
                result.add(new DimensionedItem<>(dimension, value));
            }
        }

        return result;
    }

    private List<E> getBestTuple(IEcfProgressMonitor generatorProgressMonitor) {

        while (true) {

            SortedMap<Integer, E> bestTuple = null;
            int bestTupleScore = -1;

            for (int repetition = 0; repetition < NUM_OF_REPETITIONS; repetition++) {

                if (isGenerationCancelled(generatorProgressMonitor)) {
                    return null;
                }

                if (fRemainingTuplesCount.get() <= fIgnoreCount.get()) {
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

            removeAffectedTuples(bestTuple, fPartialTuples, fRemainingTuplesCount);
            incrementProgress(bestTupleScore);  // TODO - by score ?

            return AlgorithmHelper.Uncompress(bestTuple, fDimCount);
        }
    }

    private SortedMap<Integer, E> createNTuple() {

        SortedMap<Integer, E> nTuple = createTuple();

        List<Integer> filledDimensions = createTupleDimensions(nTuple);

        List<Integer> randomDimensions = createRandomDimensions(fDimCount);

        todo(fPartialTuples, nTuple, filledDimensions, randomDimensions);

        return nTuple;
    }

    List<Integer> createTupleDimensions(SortedMap<Integer, E> nTuple) {

        List<Integer> dimensions = new ArrayList<>();

        for (Map.Entry<Integer, E> entry : nTuple.entrySet()) {

            dimensions.add(entry.getKey());
        }

        return dimensions;
    }

    private void todo(
            Multiset<SortedMap<Integer, E>> fPartialTuples,
            SortedMap<Integer, E> outNTuple,
            List<Integer> outFilledDimensions,
            List<Integer> randomDimensions) {

        for (Integer d : randomDimensions) {

            if (outNTuple.containsKey(d))
                continue;

            List<E> currentDimInput = new ArrayList<>(getInput().get(d));
            Collections.shuffle(currentDimInput);

            Set<List<Integer>> dimensionsToCountScore =
                    (new Tuples<>(outFilledDimensions, Math.min(outFilledDimensions.size(), N - 1))).getAll();

            int bestScore = -1;
            E bestElement = null;

            for (E val : currentDimInput) {

                outNTuple.put(d, val);

                if (checkConstraints(AlgorithmHelper.Uncompress(outNTuple, fDimCount)) == EvaluationResult.TRUE) {
                    int score = 0;

                    for (List<Integer> dScore : dimensionsToCountScore) {
                        SortedMap<Integer, E> tmpObject = Maps.newTreeMap();

                        for (Integer sD : dScore)
                            tmpObject.put(sD, outNTuple.get(sD));

                        tmpObject.put(d, val);

                        if (fPartialTuples.contains(tmpObject))
                            score++;
                    }

                    if (score > bestScore) {
                        bestScore = score;
                        bestElement = val;
                    }
                }
            }

            outNTuple.put(d, bestElement);
            outFilledDimensions.add(d);
        }
    }

    private SortedMap<Integer, E> createTuple() {

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

                final int tupleScore = fPartialTuples.count(tuple);

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

            if (fPartialTuples.contains(dTuple))
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

                for (int dimension = maxDimension + 1; dimension < dimensionCount - (argN - 1 - tupleSize); dimension++) {

                    final List<E> inputForOneDimension = input.get(dimension);

                    for (E v : inputForOneDimension) {

                        tuple.put(dimension, v);

                        if (checkConstraints(AlgorithmHelper.Uncompress(tuple, dimensionCount)) == EvaluationResult.TRUE) {
                            SortedMap<Integer, E> newTuple = new TreeMap<>(tuple);
                            newValidTuples.add(newTuple);
                        }
                    }

                    tuple.remove(dimension);
                }

                incrementProgress(1); // TODO - repeated progress
            }

            allValidTuples = newValidTuples;
        }

        return allValidTuples;
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