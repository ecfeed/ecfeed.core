package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.ecfeed.core.utils.ExceptionHelper;
import com.google.common.collect.Maps;

final public class TuplesHelper 
{
	public static <E> List<E> createTuple(int size, E initialValue) {
		
		List<E> result = new ArrayList<>();
		
		for (int index = 0; index < size; index++) {
			
			result.add(initialValue);
		}
		
		return result;
	}

	public static <E> SortedMap<Integer, E> compressTuple(List<E> longTuple) {
	
		SortedMap<Integer, E> compressedTuple = Maps.newTreeMap();
		
		for (int index = 0; index < longTuple.size(); index++) {
			
			E choice = longTuple.get(index);
			
			if (choice == null) {
				continue;
			}
			
			compressedTuple.put(index, choice);
		}
		
		return compressedTuple;
	}

	public static <E> List<E> decompressTuple(SortedMap<Integer, E> compressedTuple, int decompressedTupleSize) {
		
		List<E> decompressedTuple = createTuple(decompressedTupleSize, null);

		for (Map.Entry<Integer, E> entry : compressedTuple.entrySet()) {

			int index = entry.getKey();
			E value = entry.getValue();
			
			decompressedTuple.set(index, value);
		}

		return decompressedTuple;
	}
	
	public static <E>  boolean tupleIsComplete(List<E> tuple) {

		for (int index = 0; index < tuple.size(); index++) {

			if (tuple.get(index) == null) {
				return false;
			}
		}

		return true;
	}
	
	public static <E> int countUsedDimensions(List<E> tuple) {
		
		int count = 0;
		
		for (int index = 0; index < tuple.size(); index++) {

			if (tuple.get(index) != null) {
				count++;
			}
		}

		return count;
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

}