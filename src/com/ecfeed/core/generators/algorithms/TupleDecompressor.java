package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TupleDecompressor<E> {
	
	private Map<E, Integer> fChoicesToDimensions;
	int fDimensionsCount;
	
	public void initialize(List<List<E>> testDomain) {
		
		fChoicesToDimensions = new HashMap<>();
		
		fDimensionsCount = testDomain.size();
		
		int dimension = 0;
		
		for (List<E> choicesForOneParam : testDomain) {
			
			for (E choice : choicesForOneParam) {
			
				addChoiceWithDimension(choice, dimension);
				
			}
			
			dimension++;
		}
	}

	private void addChoiceWithDimension(E choice, int dimension) {

		fChoicesToDimensions.put(choice, dimension);
	}

	public List<E> decompressTuple(List<E> compressedTuple) {
		
		List<E> decompressedTuple = initDecompressedTuple(fDimensionsCount);
		
		for (E choice : compressedTuple) {
			
			int dimension = fChoicesToDimensions.get(choice);
			
			decompressedTuple.set(dimension, choice);
		}
		
		return decompressedTuple;
	}

	private List<E> initDecompressedTuple(int decompressedTupleLength) {
		
		List<E> decompressedTuple = new ArrayList<>(); 
		
		for (int counter = 0; counter < decompressedTupleLength; counter++) {
			decompressedTuple.add(null);
		}
		
		return decompressedTuple;
	}
}
