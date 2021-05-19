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

import com.ecfeed.core.utils.AlgoLogger;
import com.ecfeed.core.utils.IntegerHolder;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;


public class AwesomeScoreEvaluator<E> implements IAwesomeScoreEvaluator<E> {

	
	private Multiset<SortedMap<Integer, E>> fPartialTuples = null;
	protected int N;

	private int fDimCount;
	Set<List<Integer>> fAllDimensionCombinations;
	static final int fLogLevel = 0;

	@Override
	public void initialize(List<SortedMap<Integer, E>> allNTuples, int N, int dimCount) {
		fPartialTuples = createPartialTuples(allNTuples);
		fDimCount = dimCount;
		fAllDimensionCombinations = getAllDimensionCombinations(fDimCount, N);
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

		AlgoLogger.log("partialNTo0Tuples", result, 1, fLogLevel);
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
	public void update(
			SortedMap<Integer, E> affectingTuple,
			IntegerHolder outRemainingTuplesCount) {

		for (List<Integer> dimCombinations : fAllDimensionCombinations) {

			SortedMap<Integer, E> dTuple = Maps.newTreeMap();

			for (Integer dimension : dimCombinations)
				dTuple.put(dimension, affectingTuple.get(dimension));

			if (fPartialTuples.contains(dTuple)) {
				outRemainingTuplesCount.decrement();

				for (List<Map.Entry<Integer, E>> sublist : AlgorithmHelper.getAllSublists(new ArrayList<>(dTuple.entrySet())))
					fPartialTuples.remove(createOneCounter(sublist), 1);
			}
		}

		AlgoLogger.log("partialNTo0Tuples after removal of best tuple", fPartialTuples, 1, fLogLevel);
	}


	@Override
	public int calculateTupleScoreForOneDimension(
			SortedMap<Integer, E> nTuple,
			Integer dimension,
			Set<List<Integer>> dimensionsToCountScores,
			E item) {

		int score = 0;

		for (List<Integer> dimensionScores : dimensionsToCountScores) {

			SortedMap<Integer, E> tmpTuple = Maps.newTreeMap();

			for (Integer dimensionScore : dimensionScores) // TODO - names ?
				tmpTuple.put(dimensionScore, nTuple.get(dimensionScore));

			tmpTuple.put(dimension, item);

			if (fPartialTuples.contains(tmpTuple))
				score++;
		}

		return score;
	}

	@Override
	public int getCountOfTuple(SortedMap<Integer, E> tuple) {

		return fPartialTuples.count(tuple);
	}

	@Override
	public int calculateScoreForNTuple(SortedMap<Integer, E> nTuple) {

		int score = 0;

		for (List<Integer> combinationOfDimensions : fAllDimensionCombinations) {

			SortedMap<Integer, E> dTuple = Maps.newTreeMap();

			for (Integer dimension : combinationOfDimensions)
				dTuple.put(dimension, nTuple.get(dimension));

			if (fPartialTuples.contains(dTuple))
				score++;
		}

		return score;
	}

}