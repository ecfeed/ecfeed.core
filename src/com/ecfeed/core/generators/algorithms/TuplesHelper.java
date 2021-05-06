package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.utils.ExceptionHelper;

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

}