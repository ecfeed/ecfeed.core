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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.LogHelperCore;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;


public class NWiseAwesomeScoreEvaluator<E> implements IScoreEvaluator<E> {

	static final int MAX_TUPLES = 250000;	

	private Multiset<SortedMap<Integer, E>> fPartialTuples = null;
	protected int N;

	private int fDimCount;
	Set<List<Integer>> fAllDimensionCombinations;

	private int fInitialNTupleCount;
	private int fCurrentNTupleCount;

	static final int fLogLevel = 0;

	public NWiseAwesomeScoreEvaluator(int argN) {
		N = argN;
	}

	@Override
	public void initialize(
			List<List<E>> input, 
			IConstraintEvaluator<E> constraintEvaluator) {

		fDimCount = input.size();

		List<SortedMap<Integer, E>> allValidNTuples = 
				TuplesHelper.getAllValidNTuples(input, N, MAX_TUPLES, constraintEvaluator);

		fInitialNTupleCount = allValidNTuples.size();
		fCurrentNTupleCount = fInitialNTupleCount;

		fPartialTuples = createPartialTuples(allValidNTuples);

		fAllDimensionCombinations = getAllDimensionCombinations(fDimCount, N);
	}

	public int getCountOfInitialNTuples() {
		return fInitialNTupleCount;
	}

	public int getCountOfRemainingNTuples() {
		return fCurrentNTupleCount;
	}

	private Multiset<SortedMap<Integer, E>> createPartialTuples(List<SortedMap<Integer, E>> remainingTuples) {

		Multiset<SortedMap<Integer, E>> result = HashMultiset.create();

		for (SortedMap<Integer, E> remainingTuple : remainingTuples) {

			final List<List<Map.Entry<Integer, E>>> allSublists =
					AlgorithmHelper.getAllSublists(new ArrayList<>(remainingTuple.entrySet()));

			for (List<Map.Entry<Integer, E>> sublist : allSublists) {

				result.add(createOneCounter((List<Map.Entry<Integer, E>>) sublist));
			}
		}

		LogHelperCore.log("partialNTo0Tuples", result);
		return result;
	}

	private Set<List<Integer>> getAllDimensionCombinations(int dimensionCount, int argN) {

		List<Integer> dimensions = new ArrayList<>();

		for (int i = 0; i < dimensionCount; i++)
			dimensions.add(i);

		return (new Tuples<>(dimensions, Math.min(dimensionCount, argN))).getAll();
	}

	private ImmutableSortedMap<Integer, E> createOneCounter(List<Map.Entry<Integer, E>> sublist) {

		return new ImmutableSortedMap.Builder<Integer, E>(Ordering.natural()).putAll(sublist).build();
	}

	@Override
	public void update(SortedMap<Integer, E> affectingTuple) {

		for (List<Integer> dimCombinations : fAllDimensionCombinations) {

			SortedMap<Integer, E> dTuple = Maps.newTreeMap();

			for (Integer dimension : dimCombinations)
				dTuple.put(dimension, affectingTuple.get(dimension));

			if (fPartialTuples.contains(dTuple)) {

				fCurrentNTupleCount--;

				for (List<Map.Entry<Integer, E>> sublist : AlgorithmHelper.getAllSublists(new ArrayList<>(dTuple.entrySet())))
					fPartialTuples.remove(createOneCounter(sublist), 1);
			}
		}

		LogHelperCore.log("partialNTo0Tuples after removal of best tuple", fPartialTuples);
	}

	@Override
	public boolean contains(SortedMap<Integer, E> tmpTuple) {
		return fPartialTuples.contains(tmpTuple);
	}

	@Override
	public int getCountOfTuples(SortedMap<Integer, E> tuple) {

		return fPartialTuples.count(tuple);
	}

	@Override
	public int getScoreForTestCase(SortedMap<Integer, E> fullTuple) {

		int score = 0;

		for (List<Integer> combinationOfDimensions : fAllDimensionCombinations) {

			SortedMap<Integer, E> nTuple = Maps.newTreeMap();

			for (Integer dimension : combinationOfDimensions)
				nTuple.put(dimension, fullTuple.get(dimension));

			if (fPartialTuples.contains(nTuple))
				score++;
		}

		return score;
	}
	
	@Override
	public int getScore(SortedMap<Integer, E> tuple) {
		
		if (tuple.size() == fDimCount) {
			int score = getScoreForTestCase(tuple);
			return score;
		}
		
		if (tuple.size() <= N) {
			
			if (fPartialTuples.contains(tuple)) {
				return 1;
			}
			
			return 0;
		}
		
		ExceptionHelper.reportRuntimeException("Calculating score of tuple of this size is not implemented.");
		return 0;
	}

}