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

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.utils.*;
import com.google.common.collect.*;


public class AwesomeNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E> {

    private Multiset<SortedMap<Integer,E>> fPartialTuplesCounter = null;

    private List<Pair<Integer,E>> fAllValues = null;

    private int fIgnoreCount = 0;

    private int fLeftTuplesCount; // TODO - check name

    private int fDimCount;

    static final int NUM_OF_REPETITIONS = 100;

    public AwesomeNWiseAlgorithm(int n, int coverage) {
        super(n, coverage);
    }

    @Override
    public void reset() {

        fDimCount = getInput().size();

        try {
            fAllValues = createAllValues(fDimCount, getInput());

            List<SortedMap<Integer,E>> remainingTuples = getAllNTuples();

            fLeftTuplesCount = remainingTuples.size();

            fPartialTuplesCounter = createPartialTuplesCounter(remainingTuples);

            fIgnoreCount = fLeftTuplesCount * (100 - getCoverage()) / 100;

        } catch (GeneratorException e) {

            SystemLogger.logCatch(e);

            ExceptionHelper.reportRuntimeException("Generator reset failed.", e);
        }

        setTaskBegin(fLeftTuplesCount *getCoverage()/100);
        super.reset();
    }

    private Multiset<SortedMap<Integer,E>> createPartialTuplesCounter(List<SortedMap<Integer, E>> remainingTuples) {

        Multiset<SortedMap<Integer,E>> fPartialTuplesCounter = HashMultiset.create();

        for (SortedMap<Integer,E> remainingTuple : remainingTuples) {

            for (List<Map.Entry<Integer, E>> sublist : AlgorithmHelper.getAllSublists(new ArrayList<>(remainingTuple.entrySet()))) {

                fPartialTuplesCounter.add(createOneCounter((List<Map.Entry<Integer, E>>) sublist));
            }
        }

        return fPartialTuplesCounter;
    }

    private List<Pair<Integer,E>> createAllValues(int dimCount, List<List<E>> input) {

        List<Pair<Integer,E>> result = new ArrayList<>();

        for (int dimension = 0; dimension < dimCount; dimension++) {
            for (E value : input.get(dimension)) {
                result.add(new Pair<>(dimension, value));
            }
        }

        return result;
    }

    @Override
    public List<E> getNext() throws GeneratorException {

        IEcfProgressMonitor generatorProgressMonitor = getGeneratorProgressMonitor();

        return getBestTuple(generatorProgressMonitor);
    }

    private List<E> getBestTuple(IEcfProgressMonitor generatorProgressMonitor) {

        while (true) {

            SortedMap<Integer, E> bestTuple = null;
            int bestTupleScore = -1;

            for (int dummy=0;dummy<NUM_OF_REPETITIONS;dummy++) {

                if (generatorProgressMonitor != null) {
                    if (generatorProgressMonitor.isCanceled()) {
                        return null;
                    }
                }

                if (fLeftTuplesCount <= fIgnoreCount) {
                    setTaskEnd();
                    return null;
                }

                SortedMap<Integer, E> nTuple = Maps.newTreeMap();

                List<Integer> filledDimensions = new ArrayList<>();

                fillDimensionsAndTuples(nTuple, filledDimensions);


                List<Integer> randomDimension = createRandomDimensions();

                todo(nTuple, filledDimensions, randomDimension);

                int nTupleScore = countAffectedTuples(nTuple);

                if(nTupleScore > bestTupleScore)
                {
                    bestTupleScore = nTupleScore;
                    bestTuple = nTuple;
                }

            }

            removeAffectedTuples(bestTuple, fPartialTuplesCounter);
            incrementProgress(bestTupleScore);

            return AlgorithmHelper.Uncompress(bestTuple, fDimCount);
        }
    }

    private void todo(SortedMap<Integer, E> outNTuple, List<Integer> filledDimensions, List<Integer> randomDimension) {

        for (Integer d : randomDimension) {

            if (outNTuple.containsKey(d))
                continue;
            List<E> currentDimInput = new ArrayList<>(getInput().get(d));
            Collections.shuffle(currentDimInput);

            Set<List<Integer>> dimensionsToCountScore =
                    (new Tuples<>(filledDimensions, Math.min(filledDimensions.size(), N - 1))).getAll();

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

                        if (fPartialTuplesCounter.contains(tmpObject))
                            score++;
                    }

                    if (score > bestScore) {
                        bestScore = score;
                        bestElement = val;
                    }
                }
            }

            outNTuple.put(d, bestElement);
            filledDimensions.add(d);
        }
    }

    private void fillDimensionsAndTuples(SortedMap<Integer, E> nTuple, List<Integer> filledDimensions) {

        for (int i = 0; i < Math.min(N, fDimCount); i++) {
            Collections.shuffle(fAllValues);
            Pair<Integer, E> bestItem = null;
            int bestItemScore = -1;
            for (Pair<Integer, E> dItem : fAllValues) {
                Integer d = dItem.getFirst();
                if (nTuple.containsKey(d))
                    continue;

                nTuple.put(d, dItem.getSecond());
                if(fPartialTuplesCounter.count(nTuple)>bestItemScore)
                {
                    bestItem = dItem;
                    bestItemScore = fPartialTuplesCounter.count(nTuple);
                }
                nTuple.remove(d);
            }
            Integer d = bestItem.getFirst();
            filledDimensions.add(d);
            nTuple.put(d, bestItem.getSecond());
        }
    }

    private List<Integer> createRandomDimensions() {

        List<Integer> randomDimension = new ArrayList<>();

        for (int i = 0; i < fDimCount; i++)
            randomDimension.add(i);

        Collections.shuffle(randomDimension);
        return randomDimension;
    }

    private void removeAffectedTuples(
            SortedMap<Integer,E> nTuple,
            Multiset<SortedMap<Integer,E>> outPartialTuplesCounter
        )
    {
        for(List<Integer> dimCombinations : getAllDimensionCombinations()) {

            SortedMap<Integer,E> dTuple = Maps.newTreeMap();

            for (Integer dimension : dimCombinations)
                dTuple.put(dimension, nTuple.get(dimension));

            if (outPartialTuplesCounter.contains(dTuple)) {
                fLeftTuplesCount--;

                for ( List<Map.Entry<Integer,E>> sublist : AlgorithmHelper.getAllSublists(new ArrayList<>(dTuple.entrySet())))
                    outPartialTuplesCounter.remove(createOneCounter(sublist), 1);
            }
        }
    }

    private ImmutableSortedMap<Integer, E> createOneCounter(List<Map.Entry<Integer, E>> sublist) {

        return new ImmutableSortedMap.Builder<Integer, E>(Ordering.natural()).putAll(sublist).build();
    }

    private int countAffectedTuples(SortedMap<Integer,E> nTuple)
    {
        int score=0;
        for(List<Integer> dimComb : getAllDimensionCombinations()) {
            SortedMap<Integer,E> dTuple = Maps.newTreeMap();
            for (Integer d : dimComb)
                dTuple.put(d, nTuple.get(d));
            if (fPartialTuplesCounter.contains(dTuple))
                score++;
        }
        return score;
    }


    private Set<List<Integer>> getAllDimensionCombinations() {
        List<Integer> dimensions = new ArrayList<>();
        for (int i = 0; i < fDimCount; i++)
            dimensions.add(i);

        return (new Tuples<>(dimensions, Math.min(fDimCount,N))).getAll();
    }

    private List<SortedMap<Integer,E>> getAllNTuples() throws GeneratorException {

        List<SortedMap<Integer,E>> allValidTuples = new ArrayList<>();
        allValidTuples.add(Maps.newTreeMap());

        for(int c = 0; c<N; c++)
        {
            List<SortedMap<Integer,E>> newValidTuples = new ArrayList<>();
            if(c==N-1)
                setTaskBegin(allValidTuples.size());
            for(SortedMap<Integer,E> tuple : allValidTuples)
            {
                Integer maxD = -1;
                if(!tuple.isEmpty())
                    maxD = tuple.lastKey();
                for(int d = maxD+1; d < fDimCount - (N-1-c); d++)
                {
                    for(E v : getInput().get(d))
                    {
                        tuple.put(d,v);
                        if(checkConstraints(AlgorithmHelper.Uncompress(tuple, fDimCount)) == EvaluationResult.TRUE)
                        {
                            SortedMap<Integer,E> newTuple = new TreeMap<>(tuple);
                            newValidTuples.add(newTuple);
                        }
                    }
                    tuple.remove(d);
                }
                incrementProgress(1);
            }
            allValidTuples = newValidTuples;
        }

        return allValidTuples;
    }
}


