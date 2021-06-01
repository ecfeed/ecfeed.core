package com.ecfeed.core.generators.algorithms;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class IteratorForSubTuplePositionsTest {

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

		List<List<Integer>> expectedPositions = new ArrayList<>();

		expectedPositions.add(createPositions(0, 1, 1, 1, 1));
		expectedPositions.add(createPositions(1, 0, 1, 1, 1));
		expectedPositions.add(createPositions(1, 1, 0, 1, 1));
		expectedPositions.add(createPositions(1, 1, 1, 0, 1));
		expectedPositions.add(createPositions(1, 1, 1, 1, 0));

		IteratorForSubTuplePositions iterator = new IteratorForSubTuplePositions(5, 4);

		for (int counter = 0; ; counter++) {

			if (!iterator.hasNext()) {
				break;
			}

			List<Integer> subTuplePositions =  iterator.next();

			List<Integer> expected = expectedPositions.get(counter);

			if (!subTuplePositions.equals(expected)) {
				fail();
			}
		}
	}

	private static List<Integer> createPositions(Integer value1, Integer value2, Integer value3, Integer value4, Integer value5) {

		List<Integer> result = new ArrayList<>();

		result.add(value1);
		result.add(value2);
		result.add(value3);
		result.add(value4);
		result.add(value5);

		return result;
	}

	private void checkSubTuples1(List<String> tuple) {

		List<List<Integer>> expectedSubTuples = new ArrayList<>();
		expectedSubTuples.add(createPositions(0, 0, 0, 0, 1));
		expectedSubTuples.add(createPositions(0, 0, 0, 1, 0));
		expectedSubTuples.add(createPositions(0, 0, 1, 0, 0));
		expectedSubTuples.add(createPositions(0, 1, 0, 0, 0));
		expectedSubTuples.add(createPositions(1, 0, 0, 0, 0));

		IteratorForSubTuplePositions iterator = new IteratorForSubTuplePositions(5, 1);

		for (int counter = 0; ; counter++) {

			if (!iterator.hasNext()) {
				break;
			}

			List<Integer> subTuplePositions = iterator.next();

			if (!subTuplePositions.equals(expectedSubTuples.get(counter))) {
				fail();
			}
		}
	}

}
