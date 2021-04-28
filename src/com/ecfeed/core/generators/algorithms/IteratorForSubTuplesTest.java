package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.List;

public class IteratorForSubTuplesTest<E> {
	
  // TODO convert into tests
	
	public static void main(String[] args) {

		List<String> tuple = new ArrayList<String>();
		tuple.add("V1");
		tuple.add("V2");
		tuple.add("V3");
		tuple.add("V4");
		tuple.add("V5");
		
		printSubTuples(tuple, 4);
		printSubTuples(tuple, 3);
		printSubTuples(tuple, 2);
		printSubTuples(tuple, 1);
	}

private static void printSubTuples(List<String> tuple, int subTupleSize) {
	
	IteratorForSubTuples<String> iterator = new IteratorForSubTuples<String>(tuple, subTupleSize);
	
	System.out.println("\nGenerating sub tuples from tuple: " + tuple);
	
	for (;;) {
		if (!iterator.hasNext()) {
			break;
		}
		
		@SuppressWarnings("unchecked")
		List<String> subTuple = (List<String>) iterator.next();
		
		System.out.println("Sub tuple: " + subTuple);
	}
	
	System.out.println("End.");
}

	
}
