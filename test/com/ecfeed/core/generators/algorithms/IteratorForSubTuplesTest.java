package com.ecfeed.core.generators.algorithms;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class IteratorForSubTuplesTest {

	@Test
	public void basicTest() {

		List<String> tuple = new ArrayList<String>();
		tuple.add("V1");
		tuple.add("V2");
		tuple.add("V3");
		tuple.add("V4");
		tuple.add("V5");

		checkSubTuples1(tuple);
		checkSubTuples4(tuple);
	}

	private static void checkSubTuples4(List<String> tuple) {

		List<List<String>> expectedSubTuples = new ArrayList<>();
		expectedSubTuples.add(createTuple4("V2", "V3", "V4", "V5"));
		expectedSubTuples.add(createTuple4("V1", "V3", "V4", "V5"));
		expectedSubTuples.add(createTuple4("V1", "V2", "V4", "V5"));
		expectedSubTuples.add(createTuple4("V1", "V2", "V3", "V5"));
		expectedSubTuples.add(createTuple4("V1", "V2", "V3", "V4"));

		IteratorForSubTuples<String> iterator = new IteratorForSubTuples<String>(tuple, 4);

		for (int counter = 0; ; counter++) {
			
			if (!iterator.hasNext()) {
				break;
			}

			@SuppressWarnings("unchecked")
			List<String> subTuple = (List<String>) iterator.next();

			if (!subTuple.equals(expectedSubTuples.get(counter))) {
				fail();
			}
		}
	}
	
	private static List<String> createTuple4(String value1, String value2, String value3, String value4) {
		
		List<String> result = new ArrayList<>();
		
		result.add(value1);
		result.add(value2);
		result.add(value3);
		result.add(value4);
		
		return result;
	}
	
	private void checkSubTuples1(List<String> tuple) {
		
		List<List<String>> expectedSubTuples = new ArrayList<>();
		expectedSubTuples.add(createTuple1("V5"));
		expectedSubTuples.add(createTuple1("V4"));
		expectedSubTuples.add(createTuple1("V3"));
		expectedSubTuples.add(createTuple1("V2"));
		expectedSubTuples.add(createTuple1("V1"));
		
		IteratorForSubTuples<String> iterator = new IteratorForSubTuples<String>(tuple, 1);

		for (int counter = 0; ; counter++) {
			
			if (!iterator.hasNext()) {
				break;
			}

			@SuppressWarnings("unchecked")
			List<String> subTuple = (List<String>) iterator.next();

			if (!subTuple.equals(expectedSubTuples.get(counter))) {
				fail();
			}
		}
	}

	private List<String> createTuple1(String value1) {
		
		List<String> result = new ArrayList<>();
		
		result.add(value1);
		
		return result;
	}

}
