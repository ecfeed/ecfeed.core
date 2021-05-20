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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import com.ecfeed.core.generators.DimensionedItem;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.utils.AlgoLogger;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IEcfProgressMonitor;
import com.ecfeed.core.utils.IntegerHolder;
import com.ecfeed.core.utils.SystemLogger;
import com.google.common.collect.Maps;


public class AwesomeNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E> {

	static final int MAX_REPETITIONS = 2; // TODO - calculate ? could be smaller for small number of dimensions or N ?
	static final int MAX_TUPLES = 250000;

	private List<DimensionedItem<E>> fAllDimensionedItems = null;

	private IntegerHolder fCoverageIgnoreCount;

	private int fDimCount;

	static final int fLogLevel = 0;

	IAwesomeScoreEvaluator<E> fAwesomeScoreEvaluator = null;

	public AwesomeNWiseAlgorithm(int n, int coverage) {
		super(n, coverage);

		fAwesomeScoreEvaluator = new AwesomeScoreEvaluator<>(getN());
	}

	@Override
	public void reset() {

		fCoverageIgnoreCount = new IntegerHolder(0);
		fDimCount = getInput().size();
		try {
			fAllDimensionedItems = createDimensionedItems(getInput());

			fAwesomeScoreEvaluator.initialize(getInput(), getConstraintEvaluator());
			
			fCoverageIgnoreCount.set(calculateIgnoreCount());
			
		} catch (Exception e) {

			SystemLogger.logCatch(e);

			ExceptionHelper.reportRuntimeException("Generator reset failed.", e);
		}

		super.reset(fAwesomeScoreEvaluator.getCurrentNTupleCount() * getCoverage() / 100);
	}

	private int calculateIgnoreCount() {

		int result = fAwesomeScoreEvaluator.getCurrentNTupleCount() * (100 - getCoverage()) / 100;
		AlgoLogger.log("Ignore count", result, 1, fLogLevel);

		return result;
	}

	@Override
	public List<E> getNext() throws GeneratorException {

		AlgoLogger.log("========== getNext test case ==========", 1, fLogLevel);

		IEcfProgressMonitor generatorProgressMonitor = getGeneratorProgressMonitor();

		List<E> tuple = getBestMaxTuple(generatorProgressMonitor);

		if (tuple == null) {
			AlgoLogger.log("Tuple is null", 1, fLogLevel);
		}

		return tuple;
	}

	private List<DimensionedItem<E>> createDimensionedItems(List<List<E>> input) {

		int dimCount = getInput().size();

		List<DimensionedItem<E>> result = new ArrayList<>();

		for (int dimension = 0; dimension < dimCount; dimension++) {
			for (E value : input.get(dimension)) {
				result.add(new DimensionedItem<>(dimension, value));
			}
		}

		AlgoLogger.log("Dimensioned items", result, 1, fLogLevel);
		return result;
	}

	private List<E> getBestMaxTuple(IEcfProgressMonitor generatorProgressMonitor) {

		SortedMap<Integer, E> bestTuple = null;
		int bestTupleScore = -1;

		for (int repetition = 0; repetition < MAX_REPETITIONS; repetition++) {

			if (isGenerationCancelled(generatorProgressMonitor)) {
				return null;
			}

			if (fAwesomeScoreEvaluator.getCurrentNTupleCount() <= fCoverageIgnoreCount.get()) {
				setTaskEnd();
				return null;
			}

			SortedMap<Integer, E> nTuple = createNTuple();

			int nTupleScore = fAwesomeScoreEvaluator.calculateScoreForNTuple(nTuple);

			if (nTupleScore > bestTupleScore) {
				bestTupleScore = nTupleScore;
				bestTuple = nTuple;
			}
		}

		AlgoLogger.log("Best max tuple", bestTuple, 1, fLogLevel);

		fAwesomeScoreEvaluator.update(bestTuple);
		
		incrementProgress(bestTupleScore);  // score == number of covered tuples, so its accurate progress measure

		final List<E> result = AlgorithmHelper.uncompressTuple(bestTuple, fDimCount);

		AlgoLogger.log("Result of getNext - best max tuple", result, 1, fLogLevel);
		return result;
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

			if (fAwesomeScoreEvaluator.contains(tmpTuple))
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

				//				final int tupleScore = fPartialTuples.count(tuple);
				final int tupleScore = fAwesomeScoreEvaluator.getCountOfTuple(tuple);

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

	//	private Set<List<Integer>> getAllDimensionCombinations(int dimensionCount, int argN) {
	//
	//		List<Integer> dimensions = new ArrayList<>();
	//
	//		for (int i = 0; i < dimensionCount; i++)
	//			dimensions.add(i);
	//
	//		return (new Tuples<>(dimensions, Math.min(dimensionCount, argN))).getAll();
	//	}

	//	private List<SortedMap<Integer, E>> getAllValidNTuples(List<List<E>> input, int argN) {
	//
	//		int dimensionCount = getInput().size();
	//
	//		List<SortedMap<Integer, E>> allValidTuples = new ArrayList<>();
	//		allValidTuples.add(Maps.newTreeMap());
	//
	//		for (int tupleSize = 0; tupleSize < argN; tupleSize++) {
	//
	//			List<SortedMap<Integer, E>> newValidTuples = new ArrayList<>();
	//
	//			for (SortedMap<Integer, E> tuple : allValidTuples) {
	//
	//				Integer maxDimension = -1;
	//
	//				if (!tuple.isEmpty()) {
	//					maxDimension = tuple.lastKey();
	//				}
	//
	//				addValidTuples(input, argN, dimensionCount, tupleSize, maxDimension, tuple, newValidTuples);
	//
	//				if (newValidTuples.size() > MAX_TUPLES) {
	//					ExceptionHelper.reportRuntimeException(
	//							"The number of tuples is limited to " + MAX_TUPLES + ". " +
	//									"The current value is: " + newValidTuples.size() + ". " +
	//									"To fix this issue, limit the number of arguments, choices or include additional constraint."
	//							);
	//				}
	//			}
	//
	//			allValidTuples = newValidTuples;
	//		}
	//
	//		AlgoLogger.log("All N tuples", allValidTuples, 1, fLogLevel);
	//		return allValidTuples;
	//	}

	//	private void addValidTuples(
	//			List<List<E>> input,
	//			int argN, int dimensionCount,
	//			int tupleSize,
	//			Integer maxDimension,
	//			SortedMap<Integer, E> tuple,
	//			List<SortedMap<Integer, E>> inOutValidTuples) {
	//
	//		for (int dimension = maxDimension + 1; dimension < dimensionCount - (argN - 1 - tupleSize); dimension++) {
	//
	//			final List<E> inputForOneDimension = input.get(dimension);
	//
	//			addTuplesForOneDimension(dimension, inputForOneDimension, tuple, dimensionCount, inOutValidTuples);
	//
	//			tuple.remove(dimension);
	//		}
	//	}

	//	private void addTuplesForOneDimension(
	//			int dimension,
	//			List<E> inputForOneDimension,
	//			SortedMap<Integer, E> tuple,
	//			int dimensionCount,
	//			List<SortedMap<Integer, E>> inOutTuples) {
	//
	//		for (E v : inputForOneDimension) {
	//
	//			tuple.put(dimension, v);
	//
	//			if (checkConstraints(AlgorithmHelper.uncompressTuple(tuple, dimensionCount)) == EvaluationResult.TRUE) {
	//				SortedMap<Integer, E> newTuple = new TreeMap<>(tuple);
	//				inOutTuples.add(newTuple);
	//			}
	//		}
	//	}

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