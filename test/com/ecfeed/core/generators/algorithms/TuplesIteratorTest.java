package com.ecfeed.core.generators.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.ecfeed.core.generators.DimensionedItem;

public class TuplesIteratorTest {

	private static List<List<Integer>> iteratorArguments2D;
	private static List<Integer> itartatorArguments1D;
	
	private static int defaultOrder;

	private TuplesIterator<Integer> iteratorInstance2D;
	private TuplesIterator<Integer> iteratorInstance1D;
	
	private final static String fieldReferenceArguments = "fReferenceArguments";
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@BeforeClass
	public static void beforeAll() {
		defaultOrder = 2;
		
		itartatorArguments1D = new ArrayList<>();
		for (int i = 0 ; i < 5 ; i++) {
			itartatorArguments1D.add(i);
		}
		
		iteratorArguments2D = new ArrayList<>();
		for (int i = 1 ; i < 6 ; i++) {
			ArrayList<Integer> localList = new ArrayList<>();
			for (int j = 0 ; j < i ; j++) {
				localList.add(j);
			}
			iteratorArguments2D.add(localList);
		}
		
	}
	
	@Before
	public void beforeTest() {
		iteratorInstance1D = TuplesIterator.createFromList(itartatorArguments1D, defaultOrder);
		iteratorInstance2D = TuplesIterator.create(iteratorArguments2D, defaultOrder);
	}
	
	@Test
	public void initializationTest() {
		assertEquals("Internal structure (arguments) is incorrect (1D)", "0 \n1 \n2 \n3 \n4", printTuplesIterator(iteratorInstance1D));
		assertEquals("Internal structure (arguments) is incorrect (2D)", "0 \n0 1 \n0 1 2 \n0 1 2 3 \n0 1 2 3 4", printTuplesIterator(iteratorInstance2D));
	}
	
	@Test
	public void initializationNullArgumentsList2DTest() {
		exception.expect(NullPointerException.class);
		TuplesIterator.create(null, defaultOrder);
	}
	
	@Test
	public void initializationNullArgumentsList1DTest() {
		exception.expect(NullPointerException.class);
		TuplesIterator.createFromList(null, defaultOrder);
	}
	
	@Test
	public void initializationEmptyArgumentsList2DTest() {
		List<List<Integer>> testIteratorArguments = new ArrayList<>();
		
		exception.expect(IllegalArgumentException.class);
		TuplesIterator.create(testIteratorArguments, defaultOrder);
	}
	
	@Test
	public void initializationEmptyArgumentsList1DTest() {
		List<List<Integer>> testIteratorArguments = new ArrayList<>();
		
		exception.expect(IllegalArgumentException.class);
		TuplesIterator.createFromList(testIteratorArguments, defaultOrder);
	}
	
	@Test
	public void initializationEmptyArgumentsListSingle2DTest() {
		List<List<Integer>> testIteratorArguments = new ArrayList<>();
		testIteratorArguments.add(new ArrayList<Integer>());
		
		exception.expect(IllegalArgumentException.class);
		TuplesIterator.create(testIteratorArguments, defaultOrder);
	}
	
	@Test
	public void initializationCorrectOrder2DTest() {
		for (int i = 1 ; i < iteratorArguments2D.size() ; i++) {
			TuplesIterator.create(iteratorArguments2D, i);
		}
	}
	
	@Test
	public void initializationCorrectOrder1DTest() {
		for (int i = 1 ; i < itartatorArguments1D.size() ; i++) {
			TuplesIterator.createFromList(itartatorArguments1D, i);
		}
	}
	
	@Test
	public void initializationTooLowOrder2DTest() {
		exception.expect(IllegalArgumentException.class);
		TuplesIterator.create(iteratorArguments2D, 0);
	}
	
	@Test
	public void initializationTooLowOrder1DTest() {
		exception.expect(IllegalArgumentException.class);
		TuplesIterator.createFromList(itartatorArguments1D, 0);
	}
	
	@Test
	public void initializationTooHighOrder2DTest() {
		exception.expect(IllegalArgumentException.class);
		TuplesIterator.create(iteratorArguments2D, iteratorArguments2D.size() + 1);
	}
	
	@Test
	public void initializationTooHighOrder1DTest() {
		exception.expect(IllegalArgumentException.class);
		TuplesIterator.createFromList(itartatorArguments1D, itartatorArguments1D.size() + 1);
	}
	
	@Test(timeout = 1000)
	public void iterationMultipleForLoopTest() {
		for (int i = 0 ; i < 3 ; i++) {
			int testIteration = 0;
			for (List<DimensionedItem<Integer>> tuple : iteratorInstance2D) {
				assertEquals("The tuple index is not correct", testIteration++, iteratorInstance2D.getIndex());
				assertNotNull("A null tuple was generated (2D)", tuple);
			}
		}
	}
	
	@Test
	public void iterationUniqueElementsTest() {
		Set<List<DimensionedItem<Integer>>> testSet = new HashSet<>();
		
		for (List<DimensionedItem<Integer>> testElement : iteratorInstance2D) {
			assertFalse("A duplicated tuple was generated (2D)", isTupleInSet(testSet, testElement));
			testSet.add(testElement);
		}	
	}
	
	@Test
	public void iterationMementoTest() {
		List<List<DimensionedItem<Integer>>> testTuples1 = new ArrayList<>();
		List<List<DimensionedItem<Integer>>> testTuples2 = new ArrayList<>();
		int testTupleIndex1 = 0;
		int testTupleIndex2 = 0;
		
		int testIndex = 0;
		for (List<DimensionedItem<Integer>> tuple : iteratorInstance2D) {
			testIndex++;
			
			if (testIndex == 5) {
				testTupleIndex1 = iteratorInstance2D.getIndex();
				iteratorInstance2D.mementoSave();
			} else if (testIndex > 5) {
				testTuples1.add(tuple);
			}
		}
		
		iteratorInstance2D.mementoLoad();
		testTupleIndex2 = iteratorInstance2D.getIndex();
		
		for (List<DimensionedItem<Integer>> tuple : iteratorInstance2D) {
			testTuples2.add(tuple);
		}
		
		assertEquals("The tuple value after the restart (memento) is incorrect (2D)", testTuples1, testTuples2);
		assertEquals("The tuple index after the restart (memento) is incorrect (2D)", testTupleIndex1, testTupleIndex2);
	}
 	
	@Test
	public void iterationCorrectValues() {
		Set<List<DimensionedItem<Integer>>> testTuples = new HashSet<>();
		
		ArrayList<DimensionedItem<Integer>> tuples0 = new ArrayList<>();
		tuples0.add(new DimensionedItem<Integer>(0, 0)); tuples0.add(new DimensionedItem<Integer>(1, 1));
		ArrayList<DimensionedItem<Integer>> tuples1 = new ArrayList<>();
		tuples1.add(new DimensionedItem<Integer>(0, 0)); tuples1.add(new DimensionedItem<Integer>(2, 2));
		ArrayList<DimensionedItem<Integer>> tuples2 = new ArrayList<>();
		tuples2.add(new DimensionedItem<Integer>(0, 0)); tuples2.add(new DimensionedItem<Integer>(3, 3));
		ArrayList<DimensionedItem<Integer>> tuples3 = new ArrayList<>();
		tuples3.add(new DimensionedItem<Integer>(0, 0)); tuples3.add(new DimensionedItem<Integer>(4, 4));
		ArrayList<DimensionedItem<Integer>> tuples4 = new ArrayList<>();
		tuples4.add(new DimensionedItem<Integer>(1, 1)); tuples4.add(new DimensionedItem<Integer>(2, 2));
		ArrayList<DimensionedItem<Integer>> tuples5 = new ArrayList<>();
		tuples5.add(new DimensionedItem<Integer>(1, 1)); tuples5.add(new DimensionedItem<Integer>(3, 3));
		ArrayList<DimensionedItem<Integer>> tuples6 = new ArrayList<>();
		tuples6.add(new DimensionedItem<Integer>(1, 1)); tuples6.add(new DimensionedItem<Integer>(4, 4));
		ArrayList<DimensionedItem<Integer>> tuples7 = new ArrayList<>();
		tuples7.add(new DimensionedItem<Integer>(2, 2)); tuples7.add(new DimensionedItem<Integer>(3, 3));
		ArrayList<DimensionedItem<Integer>> tuples8 = new ArrayList<>();
		tuples8.add(new DimensionedItem<Integer>(2, 2)); tuples8.add(new DimensionedItem<Integer>(4, 4));
		ArrayList<DimensionedItem<Integer>> tuples9 = new ArrayList<>();
		tuples9.add(new DimensionedItem<Integer>(3, 3)); tuples9.add(new DimensionedItem<Integer>(4, 4));
		
		testTuples.add(tuples0); testTuples.add(tuples1); testTuples.add(tuples2); testTuples.add(tuples3); testTuples.add(tuples4);
		testTuples.add(tuples5); testTuples.add(tuples6); testTuples.add(tuples7); testTuples.add(tuples8); testTuples.add(tuples9);
		
		for (List<DimensionedItem<Integer>> tuple : iteratorInstance1D) {
			if (isTupleInSet(testTuples, tuple)) {
				continue;
			}
			fail("A tuple was not found in the reference set (1D)");
		}
	
	}
	
	@Test
	public void numberOfCombinationsTest() {
		assertEquals("The number of argument combinations is incorrect (1D)", 10, iteratorInstance1D.getNumberOfCombinations());
		assertEquals("The number of argument combinations is incorrect (2D)", 10, iteratorInstance2D.getNumberOfCombinations());
	}
	
	@Test
	public void numberOfTuplesTest() {
		assertEquals("The total number of tuples is incorrect (1D)", 10, iteratorInstance1D.getNumberOfTuples());
		assertEquals("The total number of tuples is incorrect (2D)", 85, iteratorInstance2D.getNumberOfTuples());
	}
	
	@SuppressWarnings("unchecked")
	private String printTuplesIterator(TuplesIterator<Integer> iterator) {
		List<List<Integer>> testReferenceArguments = null;
		StringBuilder responseBuilder = new StringBuilder();
		
		for (Field field : TuplesIterator.class.getDeclaredFields()) {
			if (field.getName().equals(fieldReferenceArguments)) {
				field.setAccessible(true);
				try {
					testReferenceArguments = (List<List<Integer>>) field.get(iterator);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException("Could not extract the field value (reference arguments)");
				}
			}		
		}
		
		if (testReferenceArguments == null) {
			throw new RuntimeException("Could not extract the field value (reference arguments)");
		}
		
		for (List<Integer> testRow : testReferenceArguments) {
			for (Integer testRowSingleValue : testRow) {
				responseBuilder.append(testRowSingleValue + " ");
			}
			responseBuilder.append("\n");
		}

		return responseBuilder.toString().trim();
	}
	
	private boolean isTupleInSet(Set<List<DimensionedItem<Integer>>> referenceSet, List<DimensionedItem<Integer>> referenceTuple) {
		for (List<DimensionedItem<Integer>> tuple : referenceSet) {
			if (compareTuples(tuple, referenceTuple)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean compareTuples(List<DimensionedItem<Integer>> referenceTupleA, List<DimensionedItem<Integer>> referenceTupleB) {
		if (referenceTupleA.size() != referenceTupleB.size()) {
			return false;
		}
		
		for (DimensionedItem<Integer> referenceTupleAValue : referenceTupleA) {
			boolean isValueCommonToBothTuples = false;
			for (DimensionedItem<Integer> referenceTupleBValue : referenceTupleB) {
				if (referenceTupleAValue.equals(referenceTupleBValue)) {
					isValueCommonToBothTuples = true;
					break;
				}
			}
			if (!isValueCommonToBothTuples) {
				return false;
			}
		}
		
		return true;
	}
	
}
