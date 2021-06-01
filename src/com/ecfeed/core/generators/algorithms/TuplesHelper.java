package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;
import com.google.common.collect.Maps;

final public class TuplesHelper 
{

	public static <E>  boolean tupleIsComplete(List<E> tuple) {

		for (int index = 0; index < tuple.size(); index++) {

			if (tuple.get(index) == null) {
				return false;
			}
		}

		return true;
	}

	public static <E> boolean isDimensionsMatch(List<E> tuple, List<Integer> dimensions) {

		int dimensionSize = dimensions.size();


		if (tuple.size()  != dimensionSize) {
			return false;
		}

		for (int index = 0; index < dimensionSize; index++)  {

			E choice = tuple.get(index);
			Integer dimension = dimensions.get(index);

			if (choice == null && dimension != null) {
				return false;
			}

			if (choice != null && dimension == null) {
				return false;
			}
		}

		return true;
	}


	public static <E> List<E> createCloneOfTuple(List<E> sourceTuple) {

		List<E> cloneTuple = new ArrayList<>();

		for (int index = 0; index < sourceTuple.size(); index++) {

			cloneTuple.add(sourceTuple.get(index));
		}

		return cloneTuple;

	}

	public static <E> List<E> mergeTuples(List<E> mainTuple, List<E> tupleToMerge) {

		if (mainTuple == null) {
			ExceptionHelper.reportRuntimeException("Empty main tuple when merging tuples.");
		}

		if (tupleToMerge == null) {
			ExceptionHelper.reportRuntimeException("Empty tuple to be merged.");
		}

		if (mainTuple.size() != tupleToMerge.size()) {
			ExceptionHelper.reportRuntimeException("Invalid sizes of main and merged tuples.");
		}

		List<E> mergedTuple = new ArrayList<>();

		for (int index = 0; index < mainTuple.size(); index++) {

			E choice = createChoiceToMerge(mainTuple, tupleToMerge, index);
			mergedTuple.add(choice);
		}

		return mergedTuple;
	}	

	private static <E> E createChoiceToMerge(List<E> mainTuple, List<E> tupleToMerge, int index) {

		E choiceToMerge = tupleToMerge.get(index);
		E mainChoice = mainTuple.get(index);

		if (choiceToMerge == null) {
			return mainChoice;
		}

		if (mainChoice != null) {
			ExceptionHelper.reportRuntimeException("Attempt to overwrite tuple value during tuple merging.");
		}

		return choiceToMerge;
	}

	public static <E> List<SortedMap<Integer, E>> getAllValidNTuples(
			List<List<E>> input, int argN, int maxTuples, IConstraintEvaluator<E> fConstraintEvaluator) {

		int dimensionCount = input.size();

		List<SortedMap<Integer, E>> allValidNTuples = new ArrayList<>();
		allValidNTuples.add(Maps.newTreeMap());

		for (int tupleSize = 0; tupleSize < argN; tupleSize++) {

			List<SortedMap<Integer, E>> newValidTuples = new ArrayList<>();

			for (SortedMap<Integer, E> tuple : allValidNTuples) {

				Integer maxDimension = -1;

				if (!tuple.isEmpty()) {
					maxDimension = tuple.lastKey();
				}

				addValidTuples(input, argN, dimensionCount, tupleSize, maxDimension, tuple, newValidTuples, fConstraintEvaluator);

				if (newValidTuples.size() > maxTuples) {
					ExceptionHelper.reportRuntimeException(
							"The number of tuples is limited to " + maxTuples + ". " +
									"The current value is: " + newValidTuples.size() + ". " +
									"To fix this issue, limit the number of arguments, choices or include additional constraint."
							);
				}
			}

			allValidNTuples = newValidTuples;
		}

		return allValidNTuples;
	}

	private static <E> void addValidTuples(
			List<List<E>> input,
			int argN, int dimensionCount,
			int tupleSize,
			Integer maxDimension,
			SortedMap<Integer, E> tuple,
			List<SortedMap<Integer, E>> inOutValidTuples,
			IConstraintEvaluator<E> fConstraintEvaluator) {

		for (int dimension = maxDimension + 1; dimension < dimensionCount - (argN - 1 - tupleSize); dimension++) {

			final List<E> inputForOneDimension = input.get(dimension);

			addTuplesForOneDimension(dimension, inputForOneDimension, tuple, dimensionCount, inOutValidTuples, fConstraintEvaluator);

			tuple.remove(dimension);
		}
	}

	private static <E> void addTuplesForOneDimension(
			int dimension,
			List<E> inputForOneDimension,
			SortedMap<Integer, E> tuple,
			int dimensionCount,
			List<SortedMap<Integer, E>> inOutTuples, 
			IConstraintEvaluator<E> fConstraintEvaluator) {

		for (E v : inputForOneDimension) {

			tuple.put(dimension, v);

			if (checkConstraints(AlgorithmHelper.uncompressTuple(tuple, dimensionCount), fConstraintEvaluator) == EvaluationResult.TRUE) {
				SortedMap<Integer, E> newTuple = new TreeMap<>(tuple);
				inOutTuples.add(newTuple);
			}
		}
	}

	private static <E> EvaluationResult checkConstraints(List<E> test, IConstraintEvaluator<E> fConstraintEvaluator) {

		if (fConstraintEvaluator == null) {
			return EvaluationResult.TRUE;
		}

		return fConstraintEvaluator.evaluate(test);
	}

	public static <E> List<E> convertSortedMapTupleToTupleWithoutDimensions(SortedMap<Integer, E> tuple) {

		List<E> tupleWithoutDimensions = new ArrayList<>();

		for (Map.Entry<Integer, E> entry : tuple.entrySet()) {
			
			E choice = entry.getValue();
			tupleWithoutDimensions.add(choice);
		}

		return tupleWithoutDimensions;
	}

	public static <E> SortedMap<Integer, E> convertExtendedTupleToSortedMapTuple(List<E> extendeTuple) {

		SortedMap<Integer, E> sortedMapTuple = new TreeMap<>(); 

		for (int index = 0; index < extendeTuple.size(); index++) {
			
			E choice = extendeTuple.get(index);
			
			if (choice != null) {
				sortedMapTuple.put(index, choice);
			}
		}

		return sortedMapTuple;
	}
	

}