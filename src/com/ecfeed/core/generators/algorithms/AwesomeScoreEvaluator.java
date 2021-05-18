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


public class AwesomeScoreEvaluator<E> {

	private Multiset<SortedMap<Integer, E>> fPartialTuples = null;
	private int fDimCount;
	protected int N;

	static final int fLogLevel = 0;

	public void reset(List<SortedMap<Integer, E>> allNTuples, int dimCount, int N) {
		fPartialTuples = createPartialTuples(allNTuples);
		fDimCount = dimCount;
	}

	public Multiset<SortedMap<Integer, E>> createPartialTuples(List<SortedMap<Integer, E>> remainingTuples) {

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

	private ImmutableSortedMap<Integer, E> createOneCounter(List<Map.Entry<Integer, E>> sublist) {

		return new ImmutableSortedMap.Builder<Integer, E>(Ordering.natural()).putAll(sublist).build();
	}

	public void removeAffectedTuples(
			SortedMap<Integer, E> affectingTuple,
			IntegerHolder outRemainingTuplesCount,
			int N,
			Set<List<Integer>> allDimensionCombinations) {

		for (List<Integer> dimCombinations : allDimensionCombinations) {

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

	public int getCountOfTuple(SortedMap<Integer, E> tuple) {

		return fPartialTuples.count(tuple);
	}
	
	public int calculateScoreForNTuple(SortedMap<Integer, E> nTuple, Set<List<Integer>> allDimensionCombinations) {

		int score = 0;

//		final Set<List<Integer>> allDimensionCombinations = getAllDimensionCombinations(fDimCount, N);

		for (List<Integer> combinationOfDimensions : allDimensionCombinations) {

			SortedMap<Integer, E> dTuple = Maps.newTreeMap();

			for (Integer dimension : combinationOfDimensions)
				dTuple.put(dimension, nTuple.get(dimension));

			if (fPartialTuples.contains(dTuple))
				score++;
		}

		return score;
	}
	
}