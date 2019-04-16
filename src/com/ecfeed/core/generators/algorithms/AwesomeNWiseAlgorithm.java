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

    private Multiset<List<DimensionedItem<E>>> fPartialTuplesCounter = null;

    private List<DimensionedItem<E>> fAllValues = null;

    private int fIgnoreCount = 0;

    private int fLeftTuples;

    public AwesomeNWiseAlgorithm(int n, int coverage) {
        super(n, coverage);
    }

    @Override
    public void reset() {
        try {
            fAllValues = new ArrayList<>();
            for (int i = 0; i < getInput().size(); i++)
                for (E v : getInput().get(i))
                    fAllValues.add(new DimensionedItem<E>(i, v));

            List<List<DimensionedItem<E>>> remainingTuples = getAllNTuples();
            fLeftTuples = remainingTuples.size();
            System.out.println(fLeftTuples);

            fPartialTuplesCounter = HashMultiset.create();
            for(List<DimensionedItem<E>> it : remainingTuples) {
                List<List<DimensionedItem<E>>> allSublists = AlgorithmHelper.AllSublists(it);
                for (List<DimensionedItem<E>> sublist : allSublists) {
                    Collections.sort(sublist);
                    fPartialTuplesCounter.add(sublist);
                }
            }

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

            int dimCount = getInput().size();

            List<DimensionedItem<E>> nTuple = new ArrayList<>();
            List<E> fullTuple = new ArrayList<>(Collections.nCopies(getInput().size(), null));

            List<Integer> filledDimensions = new ArrayList<>();

            for(int i=0;i< Math.min(N,dimCount); i++)
            {
                Collections.shuffle(fAllValues);
                for(DimensionedItem<E> dItem : fAllValues) {
                    Integer d = dItem.getDimension();
                    if(fullTuple.get(d)!=null)
                        continue;

                    List<DimensionedItem<E>> nCopy = new ArrayList<>(nTuple);
                    nCopy.add(dItem);
                    Collections.sort(nCopy);
                    if (fPartialTuplesCounter.contains(nCopy)) {
                        nTuple = nCopy;
                        fullTuple.set(d, dItem.getItem());
                        filledDimensions.add(d);

                        break;
                    }
                }
            }


            List<Integer> randomDimension = new ArrayList<>();
            for(int i=0;i<dimCount;i++)
                randomDimension.add(i);
            Collections.shuffle(randomDimension);

            for(Integer d : randomDimension)
            {
                if(fullTuple.get(d)!=null)
                    continue;
                List<E> currentDimInput = new ArrayList<>(getInput().get(d));
                Collections.shuffle(currentDimInput);
                Set<List<Integer>> dimensionsToCountScore = (new Tuples<>(filledDimensions, Math.min(filledDimensions.size(),N-1))).getAll();
                List<Pair<Integer, E>> scoreAndElements = new ArrayList<>();
                for(E val : currentDimInput) {
                    fullTuple.set(d, val);
                    EvaluationResult check = checkConstraints(fullTuple);
                   if (check == EvaluationResult.TRUE) {
                       int score = 0;
                       for(List<Integer> dScore : dimensionsToCountScore)
                       {
                           List<DimensionedItem<E>> tmpObject = new ArrayList<>();
                           tmpObject.add(new DimensionedItem<>(d,val));
                           for(Integer sD : dScore)
                               tmpObject.add(new DimensionedItem<>(sD,fullTuple.get(sD)));
                           Collections.sort(tmpObject);
                           if(fPartialTuplesCounter.contains(tmpObject))
                               score++;
                       }
                       scoreAndElements.add(new Pair<>(score, val));
                    }
                }

                int bestval = 0;
                for(Pair<Integer, E> p : scoreAndElements)
                {
                    if(p.getFirst() > bestval)
                    {
                        bestval = p.getFirst();
                        fullTuple.set(d, p.getSecond());
                    }
                }
                filledDimensions.add(d);
            }


            for(List<Integer> dimComb : getAllDimensionCombinations()) {
                List<DimensionedItem<E>> dTuple = new ArrayList<>();
                for (Integer d : dimComb)
                    dTuple.add(new DimensionedItem<>(d, fullTuple.get(d)));
                if (fPartialTuplesCounter.contains(dTuple)) {
                    fLeftTuples--;
                    for (List<DimensionedItem<E>> sublist : AlgorithmHelper.AllSublists(dTuple)) {
                        Collections.sort(sublist);
                        fPartialTuplesCounter.remove(sublist, 1);
                    }
                }
            }



            return fullTuple;
        }
    }

    private Set<List<Integer>> getAllDimensionCombinations() {
        int dimCount = getInput().size();
        List<Integer> dimensions = new ArrayList<>();
        for (int i = 0; i < dimCount; i++)
            dimensions.add(i);

        return (new Tuples<>(dimensions, Math.min(dimCount,N))).getAll();
    }

    private List<List<DimensionedItem<E>>> getAllNTuples() throws GeneratorException {

        List<List<DimensionedItem<E>>> allValidTuples = new ArrayList<>();
        allValidTuples.add(new ArrayList<>());

        int dimCount = getInput().size();
        for(int c = 0; c<N; c++)
        {
            List<List<DimensionedItem<E>>> newValidTuples = new ArrayList<>();
            for(List<DimensionedItem<E>> tuple : allValidTuples)
            {
                ArrayList<E> fullTuple = new ArrayList<>(Collections.nCopies(getInput().size(), null));
                for (DimensionedItem<E> var : tuple)
                    fullTuple.set(var.getDimension(), var.getItem());
                Integer maxD = -1;
                if(!tuple.isEmpty())
                    maxD = tuple.get(tuple.size()-1).getDimension();
                for(int d = maxD+1; d < dimCount - (N-1-c); d++)
                {
                    for(E v : getInput().get(d))
                    {
                        fullTuple.set(d,v);
                        if(checkConstraints(fullTuple) == EvaluationResult.TRUE)
                        {
                            List<DimensionedItem<E>> newTuple = new ArrayList<>(tuple);
                            newTuple.add(new DimensionedItem<>(d,v));
                            newValidTuples.add(newTuple);
                        }
                    }
                    fullTuple.set(d, null);
                }
            }
            allValidTuples = newValidTuples;
        }

        return allValidTuples;
    }
}


