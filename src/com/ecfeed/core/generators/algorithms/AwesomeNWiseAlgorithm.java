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

	static final int MAX_REPETITIONS = 2; // TODO - calculate ? could be smaller for small number of dimensions or N ?
	static final int MAX_TUPLES = 250000;

	// multi set which contains tuples of length N to 0 (do we need tuples with 0 length?)
	// each tuple contains sorted map of pairs dimension+value
	private Multiset<SortedMap<Integer, E>> fPartialTuples = null;

	private List<DimensionedItem<E>> fAllDimensionedItems = null;

	private IntegerHolder fCoverageIgnoreCount;

	private IntegerHolder fNTuplesCount;

	private int fCountOfDimensions;

	static final int fLogLevel = 0;

	public AwesomeNWiseAlgorithm(int n, int coverage) {
		super(n, coverage);
	}

	@Override
	public void reset() {

		fCoverageIgnoreCount = new IntegerHolder(0);
		fCountOfDimensions = getInput().size();

		try {
			fAllDimensionedItems = createDimensionedItems(getInput());

			List<SortedMap<Integer, E>> allNTuples = findAllNTuplesWithConstraintCheck(getInput(), getN());
			fNTuplesCount = new IntegerHolder(allNTuples.size());

			fPartialTuples = createSetOfPartialTuples(allNTuples);

			fCoverageIgnoreCount.set(calculateCoverageIgnoreCount());

		} catch (Exception e) {

			SystemLogger.logCatch(e);

			ExceptionHelper.reportRuntimeException("Generator reset failed.", e);
		}

		super.reset(fNTuplesCount.get() * getCoverage() / 100);
	}

	private int calculateCoverageIgnoreCount() {

		int result = fNTuplesCount.get() * (100 - getCoverage()) / 100;
		AlgoLogger.log("Ignore count", result, 1, fLogLevel);

		return result;
	}

	@Override
	public List<E> getNext() throws GeneratorException {

		IEcfProgressMonitor generatorProgressMonitor = getGeneratorProgressMonitor();

		List<E> goodFullTuple = null;

		if (getGoodTupleNew()) {
			goodFullTuple =	getFullTupleWithHighScoreNew(generatorProgressMonitor);
		} else {
			goodFullTuple =	getFullTupleWithHighScoreOld(generatorProgressMonitor);
		}

		if (goodFullTuple == null) {
			AlgoLogger.log("Tuple is null", 1, fLogLevel);
		}

		return goodFullTuple;
	}

	private boolean getGoodTupleNew() {
		return true;
	}

	private Multiset<SortedMap<Integer, E>> createSetOfPartialTuples(List<SortedMap<Integer, E>> remainingTuples) {

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

	private List<E> getFullTupleWithHighScoreNew(IEcfProgressMonitor generatorProgressMonitor) {

		printPartialTuples("before getting tuple");
		
		if (isGenerationCancelled(generatorProgressMonitor)) {
			return null;
		}

		if (fNTuplesCount.get() <= fCoverageIgnoreCount.get()) {
			setTaskEnd();
			return null;
		}
		
		List<E> accumulatingTuple = TuplesHelper.createTuple(fCountOfDimensions, null);

		List<Integer> shuffledDimensions = createShuffledIndicesToDimensions(); 

		for (int dimension : shuffledDimensions) { 

			addBestChoiceToTuple(dimension, accumulatingTuple);
		}

		SortedMap<Integer, E> compressedTuple = TuplesHelper.compressTuple(accumulatingTuple);
		removeAffectedTuples(compressedTuple, fPartialTuples, fNTuplesCount);

		printPartialTuples("after getting tuple");
		
		return accumulatingTuple;
	}

	private void addBestChoiceToTuple(int dimension, List<E> accumulatingTuple) {

		List<E> tmpTuple = TuplesHelper.createCloneOfTuple(accumulatingTuple);

		E bestChoice = null;
		int bestScore = 0;

		List<E> listOfChoicesForParameter = getListOfChoicesForParameter(dimension);

		for (E choice : listOfChoicesForParameter) {

			tmpTuple.set(dimension, choice); 

			if (evaluateConstraint(tmpTuple) == EvaluationResult.FALSE) {
				continue;                   
			}

			int score = getScoreForCombinedTuple(accumulatingTuple, choice, dimension);

			if (score > bestScore) {                  
				bestScore = score;
				bestChoice = choice;
			}
		}

		if (bestScore == 0) {
			ExceptionHelper.reportRuntimeException("Can not find best choice.");
		}

		accumulatingTuple.set(dimension, bestChoice);
	}

	private int getScoreForCombinedTuple(List<E> baseTuple, E choiceToAdd, int dimensionOfChoice) {

		int usedBaseDimensions = TuplesHelper.countUsedDimensions(baseTuple);

		if (usedBaseDimensions == 0) {
			int score = getScoreForTupleWithOneChoice(choiceToAdd, dimensionOfChoice);
			return score;
		}

		int subTupleSize = Math.max(getN()-1, usedBaseDimensions);

		if (subTupleSize ==  1) {

			SortedMap<Integer, E> baseSubTuple = TuplesHelper.compressTuple(baseTuple);
			baseSubTuple.put(dimensionOfChoice, choiceToAdd);

			int score = getScoreForOneTuple(baseSubTuple);
			return score;
		}

		IteratorForSubTuples2<E> iteratorForSubTuples = 
				new IteratorForSubTuples2<>(baseTuple, subTupleSize);

		int totalScore = 0;

		while(iteratorForSubTuples.hasNext()) {

			SortedMap<Integer, E> baseSubTuple = iteratorForSubTuples.next();

			baseSubTuple.put(dimensionOfChoice, choiceToAdd);

			int partialScore = getScoreForOneTuple(baseSubTuple);
			totalScore += partialScore;

		}

		return totalScore;
	}

	private int getScoreForTupleWithOneChoice(E choiceToAdd, int dimensionOfChoice) {
		SortedMap<Integer, E> singleChoiceTuple = new TreeMap<Integer, E>();

		singleChoiceTuple.put(dimensionOfChoice, choiceToAdd);

		return getScoreForOneTuple(singleChoiceTuple);
	}

	private int getScoreForOneTuple(SortedMap<Integer, E> tuple) {

		return fPartialTuples.count(tuple);
	}

	private EvaluationResult evaluateConstraint(List<E> tuple) {

		return getConstraintEvaluator().evaluate(tuple);
	}

	private List<Integer> createShuffledIndicesToDimensions() {

		List<Integer> dimensions = new ArrayList<>();

		for (int dimension = 0; dimension < fCountOfDimensions; dimension++) {
			dimensions.add(dimension);
		}

		Collections.shuffle(dimensions);
		return dimensions;
	}

	private List<E> getListOfChoicesForParameter(int indexOfParameter) {

		return getInput().get(indexOfParameter);
	}

	private List<E> getFullTupleWithHighScoreOld(IEcfProgressMonitor generatorProgressMonitor) {

		SortedMap<Integer, E> bestFullTuple = null;
		int bestFullTupleScore = -1;

		for (int repetition = 0; repetition < MAX_REPETITIONS; repetition++) {

			if (isGenerationCancelled(generatorProgressMonitor)) {
				return null;
			}

			if (fNTuplesCount.get() <= fCoverageIgnoreCount.get()) {
				setTaskEnd();
				return null;
			}

			SortedMap<Integer, E> fullTuple = createFullTupleWithConstraintChecks();

			int nTupleScore = countSumOfOccurencesOfSubTuples(fullTuple);

			if (nTupleScore > bestFullTupleScore) {
				bestFullTupleScore = nTupleScore;
				bestFullTuple = fullTuple;
			}
		}

		AlgoLogger.log("Best max tuple", bestFullTuple, 1, fLogLevel);

		printPartialTuples("before removeAffectedTupples");
		removeAffectedTuples(bestFullTuple, fPartialTuples, fNTuplesCount);
		printPartialTuples("after removeAffectedTupples");

		incrementProgress(bestFullTupleScore);  // score == number of covered tuples, so its accurate progress measure

		final List<E> result = AlgorithmHelper.uncompressTuple(bestFullTuple, fCountOfDimensions);

		AlgoLogger.log("Result of getNext - best max tuple", result, 1, fLogLevel);

		return result;
	}

	public void printPartialTuples(String message) {

		System.out.println("Printing N to 0 tuples BEG, message: " + message);

		for (SortedMap<Integer, E> map: fPartialTuples) {
			System.out.println(map);
		}

		System.out.println("Printing N to 0 tuples END");
	}

	private SortedMap<Integer, E> createFullTupleWithConstraintChecks() {

		SortedMap<Integer, E> nTuple = createNTupleWithBestScores();

		SortedMap<Integer, E> maxTuple = findBestMaxTupleWithConstraintChecks(nTuple);

		return maxTuple;
	}

	private SortedMap<Integer, E> findBestMaxTupleWithConstraintChecks(SortedMap<Integer, E> nTuple) {

		SortedMap<Integer, E> resultMaxTuple = nTuple;

		List<Integer> tupleDimensions = createTupleDimensions(resultMaxTuple);

		List<Integer> randomDimensions = createRandomDimensions(fCountOfDimensions);

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
				(new Tuples<>(tupleDimensions, Math.min(tupleDimensions.size(), getN() - 1))).getAll();

		int bestScore = -1;
		E bestItem = null;

		for (E item : inputForOneDimension) {

			nTuple.put(dimension, item);

			if (checkConstraints(AlgorithmHelper.uncompressTuple(nTuple, fCountOfDimensions)) == EvaluationResult.TRUE) {

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

			SortedMap<Integer, E> tmpTuple = Maps.newTreeMap();

			for (Integer dimensionScore : dimensionScores) // TODO - names ?
				tmpTuple.put(dimensionScore, nTuple.get(dimensionScore));

			tmpTuple.put(dimension, item);

			if (fPartialTuples.contains(tmpTuple))
				score++;
		}

		return score;
	}

	private SortedMap<Integer, E> createNTupleWithBestScores() {

		SortedMap<Integer, E> tuple = Maps.newTreeMap();

		final int countOfDimensions = Math.min(getN(), fCountOfDimensions);

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
			Multiset<SortedMap<Integer, E>> outPartialNTo0Tuples,
			IntegerHolder outRemainingTuplesCount) {

		for (List<Integer> dimCombinations : getAllDimensionCombinations(fCountOfDimensions, getN())) {

			SortedMap<Integer, E> dTuple = Maps.newTreeMap();

			for (Integer dimension : dimCombinations)
				dTuple.put(dimension, affectingTuple.get(dimension));

			if (outPartialNTo0Tuples.contains(dTuple)) {
				outRemainingTuplesCount.decrement();

				for (List<Map.Entry<Integer, E>> sublist : AlgorithmHelper.getAllSublists(new ArrayList<>(dTuple.entrySet())))
					outPartialNTo0Tuples.remove(createOneCounter(sublist), 1);
			}
		}

		AlgoLogger.log("partialNTo0Tuples after removal of best tuple", outPartialNTo0Tuples, 1, fLogLevel);
	}

	private ImmutableSortedMap<Integer, E> createOneCounter(List<Map.Entry<Integer, E>> sublist) {

		return new ImmutableSortedMap.Builder<Integer, E>(Ordering.natural()).putAll(sublist).build();
	}

	private int countSumOfOccurencesOfSubTuples(SortedMap<Integer, E> fullTuple) {

		int score = 0;

		final Set<List<Integer>> combinationsOfAllcimension = getAllDimensionCombinations(fCountOfDimensions, getN());

		for (List<Integer> combinationOfDimensions : combinationsOfAllcimension) {

			SortedMap<Integer, E> dTuple = Maps.newTreeMap();

			for (Integer dimension : combinationOfDimensions)
				dTuple.put(dimension, fullTuple.get(dimension));

			if (fPartialTuples.contains(dTuple))
				score++; // incremented only once even if there is more than one tuple in multi set
		}

		return score;
	}

	private Set<List<Integer>> getAllDimensionCombinations(int dimensionCount, int argN) {

		List<Integer> dimensions = new ArrayList<>();

		for (int i = 0; i < dimensionCount; i++)
			dimensions.add(i);

		return (new Tuples<>(dimensions, Math.min(dimensionCount, argN))).getAll();
	}

	private List<SortedMap<Integer, E>> findAllNTuplesWithConstraintCheck(List<List<E>> input, int argN) {

		int dimensionCount = getInput().size();

		List<SortedMap<Integer, E>> allValidTuples = new ArrayList<>();
		allValidTuples.add(Maps.newTreeMap());

		for (int tupleSize = 0; tupleSize < argN; tupleSize++) {

			List<SortedMap<Integer, E>> newValidTuples = new ArrayList<>();

			for (SortedMap<Integer, E> tuple : allValidTuples) {

				Integer maxDimension = -1;

				if (!tuple.isEmpty()) {
					maxDimension = tuple.lastKey();
				}

				addTuplesWithConstraintChecks(input, argN, dimensionCount, tupleSize, maxDimension, tuple, newValidTuples);

				if (newValidTuples.size() > MAX_TUPLES) {
					ExceptionHelper.reportRuntimeException(
							"The number of tuples is limited to " + MAX_TUPLES + ". " +
									"The current value is: " + newValidTuples.size() + ". " +
									"To fix this issue, limit the number of arguments, choices or include additional constraint."
							);
				}
			}

			allValidTuples = newValidTuples;
		}

		AlgoLogger.log("All N tuples", allValidTuples, 1, fLogLevel);
		return allValidTuples;
	}

	private void addTuplesWithConstraintChecks(
			List<List<E>> input,
			int argN, int dimensionCount,
			int tupleSize,
			Integer maxDimension,
			SortedMap<Integer, E> tuple,
			List<SortedMap<Integer, E>> inOutValidTuples) {

		for (int dimension = maxDimension + 1; dimension < dimensionCount - (argN - 1 - tupleSize); dimension++) {

			final List<E> inputForOneDimension = input.get(dimension);

			addTuplesForOneDimensionWithConstraintChecks(dimension, inputForOneDimension, tuple, dimensionCount, inOutValidTuples);

			tuple.remove(dimension);
		}
	}

	private void addTuplesForOneDimensionWithConstraintChecks(
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