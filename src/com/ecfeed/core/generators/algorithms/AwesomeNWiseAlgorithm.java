package com.ecfeed.core.generators.algorithms;
/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

import java.util.*;

import javax.management.RuntimeErrorException;

import com.ecfeed.core.generators.DimensionedItem;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IEcfProgressMonitor;
import com.ecfeed.core.utils.Pair;
import com.google.common.collect.*;


public class AwesomeNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E> {

    private Multiset<SortedMap<Integer,E>> fPartialTuplesCounter = null;

    private List<Pair<Integer,E>> fAllValues = null;

    private int fIgnoreCount = 0;

    private int fLeftTuples;

    private int fDimCount;

    public AwesomeNWiseAlgorithm(int n, int coverage) {
        super(n, coverage);
    }

    @Override
    public void reset() {
        fDimCount = getInput().size();
        try {
            fAllValues = new ArrayList<>();
            for (int i = 0; i < fDimCount; i++)
                for (E v : getInput().get(i))
                    fAllValues.add(new Pair<>(i, v));

            List<SortedMap<Integer,E>> remainingTuples = getAllNTuples();
            fLeftTuples = remainingTuples.size();
            System.out.println(fLeftTuples);

            fPartialTuplesCounter = HashMultiset.create();
            for(SortedMap<Integer,E> it : remainingTuples)
                for ( List<Map.Entry<Integer,E>> sublist : AlgorithmHelper.AllSublists(new ArrayList<>(it.entrySet())))
                    fPartialTuplesCounter.add(new ImmutableSortedMap.Builder<Integer, E>(Ordering.natural()).putAll(sublist).build() );


            fIgnoreCount = fLeftTuples * (100 - getCoverage()) / 100;

        } catch (GeneratorException e) {
            e.printStackTrace();
            throw new RuntimeErrorException(new Error(e));
        }
        super.reset();
    }



    @Override
    public List<E> getNext() throws GeneratorException {

        IEcfProgressMonitor generatorProgressMonitor = getGeneratorProgressMonitor();

        while (true) {

            if (generatorProgressMonitor != null) {
                if (generatorProgressMonitor.isCanceled()) {
                    return null;
                }
            }

            if (fLeftTuples <= fIgnoreCount)
                return null;

            SortedMap<Integer,E> nTuple = Maps.newTreeMap();
//            List<E> fullTuple = AlgorithmHelper.Uncompress(nTuple, fDimCount);

            List<Integer> filledDimensions = new ArrayList<>();

            for(int i=0;i< Math.min(N,fDimCount); i++)
            {
                Collections.shuffle(fAllValues);
                for(Pair<Integer,E> dItem : fAllValues) {
                    Integer d = dItem.getFirst();
                    if(nTuple.containsKey(d))
                        continue;

                    nTuple.put(d,dItem.getSecond());
                    if (fPartialTuplesCounter.contains(nTuple)) {
                        filledDimensions.add(d);
                        break;
                    }
                    nTuple.remove(d);
                }
            }


            List<Integer> randomDimension = new ArrayList<>();
            for(int i=0;i<fDimCount;i++)
                randomDimension.add(i);
            Collections.shuffle(randomDimension);

            for(Integer d : randomDimension)
            {
                if(nTuple.containsKey(d))
                    continue;
                List<E> currentDimInput = new ArrayList<>(getInput().get(d));
                Collections.shuffle(currentDimInput);
                Set<List<Integer>> dimensionsToCountScore = (new Tuples<>(filledDimensions, Math.min(filledDimensions.size(),N-1))).getAll();
                int bestScore = -1;
                E bestElement = null;
                for(E val : currentDimInput) {
                    nTuple.put(d,val);
                   if (checkConstraints(AlgorithmHelper.Uncompress(nTuple, fDimCount)) == EvaluationResult.TRUE) {
                       int score = 0;
                       for(List<Integer> dScore : dimensionsToCountScore)
                       {
                           SortedMap<Integer,E> tmpObject = Maps.newTreeMap();
                           for(Integer sD : dScore)
                               tmpObject.put(sD,nTuple.get(sD));
                           tmpObject.put(d,val);
                           if(fPartialTuplesCounter.contains(tmpObject))
                               score++;
                       }
                       if(score>bestScore)
                       {
                           bestScore = score;
                           bestElement = val;
                       }
                    }
                }
                nTuple.put(d, bestElement);
                filledDimensions.add(d);
            }


            for(List<Integer> dimComb : getAllDimensionCombinations()) {
                SortedMap<Integer,E> dTuple = Maps.newTreeMap();
                for (Integer d : dimComb)
                    dTuple.put(d, nTuple.get(d));
                if (fPartialTuplesCounter.contains(dTuple)) {
                    fLeftTuples--;
                    for ( List<Map.Entry<Integer,E>> sublist : AlgorithmHelper.AllSublists(new ArrayList<>(dTuple.entrySet())))
                        fPartialTuplesCounter.remove(new ImmutableSortedMap.Builder<Integer, E>(Ordering.natural()).putAll(sublist).build(), 1);
                }
            }

            return AlgorithmHelper.Uncompress(nTuple, fDimCount);
        }
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
            }
            allValidTuples = newValidTuples;
        }

        return allValidTuples;
    }
}


