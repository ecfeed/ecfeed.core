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

import com.ecfeed.core.evaluator.DummyEvaluator;
import com.ecfeed.core.generators.DimensionedItem;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IEcfProgressMonitor;
import com.google.common.collect.*;


public class AwesomeNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E> {

//    final private int RANDOM_TEST_TRIES = 10;

    private Set<List<Integer>> allDimCombs = null;

    private Set<List<DimensionedItem<E>>> fPotentiallyRemainingTuples = null;

    // The set of all n-tuples for which none of the constraints fail (this set
    // includes both the n-tuples for which all the constraints can be evaluated
    // and pass, as well as the constraints for which at least one of
    // constraints cannot be evaluated).
    private Set<List<DimensionedItem<E>>> fRemainingTuples = null;

    private Multiset<List<DimensionedItem<E>>> fPartialTuplesCounter = null;

    private List<DimensionedItem<E>> fAllValues = null;

    private int fIgnoreCount = 0;

//    final private int CONSISTENCY_LOOP_LIM = 10;

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

            fRemainingTuples = getAllNTuples();
            System.out.println(fRemainingTuples.size());

            fPartialTuplesCounter = HashMultiset.create();
            for(List<DimensionedItem<E>> it : fRemainingTuples) {
                List<List<DimensionedItem<E>>> allSublists = AlgorithmHelper.AllSublists(it);
                for (List<DimensionedItem<E>> sublist : allSublists) {
                    Collections.sort(sublist);
                    fPartialTuplesCounter.add(sublist);
                }
            }

            fIgnoreCount = fRemainingTuples.size() * (100 - getCoverage()) / 100;

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

            if (fRemainingTuples.size() <= fIgnoreCount)
                return null;

            int dimCount = getInput().size();

            List<DimensionedItem<E>> nTuple = new ArrayList<>();
            List<E> fullTuple = new ArrayList<>(Collections.nCopies(getInput().size(), null));

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
                for(E val : currentDimInput) {
                    fullTuple.set(d, val);
                    EvaluationResult check = checkConstraints(fullTuple);
                   if (check == EvaluationResult.TRUE) {
                       break;
                    }
                }
            }


            for(List<Integer> dimComb : getAllDimensionCombinations()) {
                List<DimensionedItem<E>> dTuple = new ArrayList<>();
                for (Integer d : dimComb)
                    dTuple.add(new DimensionedItem<>(d, fullTuple.get(d)));
                if (fRemainingTuples.contains(dTuple)) {
                    fRemainingTuples.remove(dTuple);
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

        return (new Tuples<Integer>(dimensions, Math.min(dimCount,N))).getAll();
    }

    private Set<List<DimensionedItem<E>>> getAllNTuples() throws GeneratorException { //copied from RandomizedNWiseAlgorithm + few simplifications

        Set<List<Integer>> allCombs = getAllDimCombs();
        Set<List<DimensionedItem<E>>> validNTuple = new HashSet<>();



        for (List<Integer> comb : allCombs) {

            List<List<DimensionedItem<E>>> tempIn = new ArrayList<>();
            for (Integer d : comb){
                List<DimensionedItem<E>> values = new ArrayList<>();
                for (E e : getInput().get(d))
                    values.add(new DimensionedItem<E>(d, e));
                tempIn.add(values);
            }

            CartesianProductAlgorithm<DimensionedItem<E>> cartAlg = new CartesianProductAlgorithm<>();
            cartAlg.initialize(tempIn, new DummyEvaluator<>(), getGeneratorProgressMonitor());
            List<DimensionedItem<E>> tuple = null;
            while ((tuple = cartAlg.getNext()) != null) {

                // Generate a full tuple from this nTuple to make sure that it
                // is consistent with the constraints
                ArrayList<E> fullTuple = new ArrayList<>(Collections.nCopies(getInput().size(), null));
                for (DimensionedItem<E> var : tuple)
                    fullTuple.set(var.getDimension(), var.getItem());

                EvaluationResult check = checkConstraints(fullTuple);

                if (check == EvaluationResult.INSUFFICIENT_DATA) {
     //               unevaluableNTuples.add(tuple); FIXME
                } else if (check == EvaluationResult.TRUE) {
                    validNTuple.add(tuple);
                }
            }
        }

        return validNTuple;
    }

    protected Set<List<Integer>> getAllDimCombs() {

        if (allDimCombs == null)
            allDimCombs = getAllDimensionCombinations();

        return allDimCombs;
    }
}


