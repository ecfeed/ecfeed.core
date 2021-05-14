package com.ecfeed.core.generators.algorithms;

import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class IteratorForSubTuples2<E> implements Iterator<SortedMap<Integer, E>> {
	
	// create list of sub-tuples of given length from tuple
	// the tuple may be incomplete e.g { null, VALUE1, null, VALUE2 }

	private List<E> fTuple;

	private List<Integer> fCurrentSubTupleDimensions;

	IteratorForSubTuplePositions fIteratorForPositions;

	public IteratorForSubTuples2(List<E> tuple, int subTupleSize) {

		fTuple = tuple;
		
		int usedDimensions = TuplesHelper.countUsedDimensions(tuple);
		
		fIteratorForPositions = new IteratorForSubTuplePositions(usedDimensions, subTupleSize);
	}

	@Override
	public boolean hasNext() {

		if (fIteratorForPositions.hasNext()) {
			return true;
		}

		return false;
	}

	@Override

	public SortedMap<Integer, E> next() {
		
		fCurrentSubTupleDimensions = fIteratorForPositions.next();

		SortedMap<Integer, E> subTuple = createSubTuple(fTuple, fCurrentSubTupleDimensions);

		return subTuple;
	}

	private SortedMap<Integer, E> createSubTuple(List<E> tuple, List<Integer> subTupleDimensions) {
		
		SortedMap<Integer, E> result = new TreeMap<Integer, E>();
		
		for (int index = 0; index < subTupleDimensions.size(); index++) {

			Integer dimension = subTupleDimensions.get(index);
			E choice = tuple.get(dimension);
			
			result.put(dimension, choice);
		}

		return result;
	}

}
