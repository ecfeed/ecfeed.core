package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorForSubTuples<E> implements Iterator<List<E>> { // TODO - remove and use version 2
	
	// create list of sub-tuples of given length from tuple

	private List<E> fTuple;

	private List<Integer> fSubTuplePositions;

	IteratorForSubTuplePositions fIterator;

	public IteratorForSubTuples(List<E> tuple, int subTupleSize) {

		fTuple = tuple;

		fIterator = new IteratorForSubTuplePositions(tuple.size(), subTupleSize);
	}

	@Override
	public boolean hasNext() {

		if (fIterator.hasNext()) {
			return true;
		}

		return false;
	}

	@Override

	public List<E> next() {
		
		fSubTuplePositions = fIterator.next();

		List<E> subTuple = createSubTuple(fTuple, fSubTuplePositions);

		return subTuple;
	}

	private List<E> createSubTuple(List<E> fTuple, List<Integer> fSubTuplePositions) {

		List<E> subTuple = new ArrayList<>();

		for (int index = 0; index < fTuple.size(); index++) {

			int position = fSubTuplePositions.get(index);

			if (position == IteratorForSubTuplePositions.SELECTED) {

				E value = fTuple.get(index);
				subTuple.add(value);
			}
		}

		return subTuple;
	}

}
