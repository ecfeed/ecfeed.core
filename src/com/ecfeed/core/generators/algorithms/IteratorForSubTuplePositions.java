package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ecfeed.core.utils.ExceptionHelper;

public class IteratorForSubTuplePositions implements Iterator<List<Integer>> {

	public final static Integer NOT_SELECTED = 0;
	public final static Integer SELECTED = 1;

	private int fTupleSize;
	private int fSubTupleSize;
	private List<Integer> fSubTuplePositions;

	public IteratorForSubTuplePositions(int tupleSize, int subTupleSize) {

		if (subTupleSize >= tupleSize) {
			ExceptionHelper.reportRuntimeException("Invalid sub tuple size in iterator for sub tuples.");
		}

		fTupleSize = tupleSize;
		fSubTupleSize = subTupleSize;

		fSubTuplePositions = new ArrayList<Integer>();
	}

	@Override
	public boolean hasNext() {

		if (fSubTuplePositions.size() == 0) {
			return true;
		}

		for (int index = fTupleSize - 1; index >= 1; index--) {

			int currentPosition = fSubTuplePositions.get(index);
			int previousPosition = fSubTuplePositions.get(index - 1);

			if (previousPosition == NOT_SELECTED  && currentPosition == SELECTED) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<Integer> next() {

		fSubTuplePositions = prepareTuplePositions(fSubTuplePositions, fTupleSize, fSubTupleSize);

		return fSubTuplePositions;
	}

	private static List<Integer> prepareTuplePositions(List<Integer> subTuplePositions, int tupleSize, int subTupleSize) {

		if (subTuplePositions.size() == 0) {
			subTuplePositions = createInitialSubTuplePositions(tupleSize, subTupleSize);
		} else {
			subTuplePositions = incrementSubTuplePositions(subTuplePositions);
		}

		return subTuplePositions;
	}

	private static List<Integer> createInitialSubTuplePositions(int tupleSize, int subTupleSize) { // TODO rename

		List<Integer> subTuplePositions = new ArrayList<>();

		int notSelectedCount = tupleSize - subTupleSize;

		for (int counter = 0; counter < notSelectedCount; counter++) {

			subTuplePositions.add(NOT_SELECTED);
		}

		for (int counter = 0; counter < subTupleSize; counter++) {

			subTuplePositions.add(SELECTED);
		}

		return subTuplePositions;
	}

	private static List<Integer> incrementSubTuplePositions(List<Integer> subTuplePositions) {

		int index = findPositionToIncrement(subTuplePositions);

		if (index == 0) {
			ExceptionHelper.reportRuntimeException("Invalid position to increment.");
		}

		subTuplePositions.set(index - 1, SELECTED);
		subTuplePositions.set(index, NOT_SELECTED);

		moveRemainingPositionsRight(subTuplePositions, index);

		return subTuplePositions;
	}

	private static int findPositionToIncrement(List<Integer> subTuplePositions) {

		for (int index = subTuplePositions.size() - 1; index > 0; index--) {

			int currentPosition = subTuplePositions.get(index);

			if (currentPosition != SELECTED) {
				continue;
			}

			int previousPosition = subTuplePositions.get(index - 1);

			if (previousPosition == NOT_SELECTED)  {
				return index;
			}
		}

		return 0;
	}

	private static void moveRemainingPositionsRight(
			List<Integer> subTuplePositions, int indexOfCurrentPosition) {

		int countOfRemainingPositions = 
				countSelectedPositionsOnTheRightOfIndex(subTuplePositions, indexOfCurrentPosition);

		if (countOfRemainingPositions == 0) {
			return;
		}

		for (int index = indexOfCurrentPosition; index < subTuplePositions.size(); index++) {

			subTuplePositions.set(index, NOT_SELECTED);
		}

		for (int index = subTuplePositions.size() - 1; index > 0; index--) {

			if (countOfRemainingPositions == 0) {
				break;
			}

			subTuplePositions.set(index, SELECTED);

			countOfRemainingPositions--;
		}
	}

	private static int countSelectedPositionsOnTheRightOfIndex(List<Integer> fSubTuplePositions2, int startIndex) {

		int count = 0;

		for (int index = startIndex; index < fSubTuplePositions2.size(); index++) {

			int position = fSubTuplePositions2.get(index);

			if (position == SELECTED) {
				count++;
			}
		}

		return count;
	}

}
