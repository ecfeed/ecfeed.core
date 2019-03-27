/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ecfeed.core.generators.DimensionedItem;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public final class IteratedRandomizedNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E> {

	private final static int GENERATE_TEST_TRIES = 10;
	private final static int GENERATE_TEST_USING_TUPLE_TRIES = 10;
	
	private TuplesIterator<E> iteratorTuples;
	private long numberOfRemainingTuples = 0;
	private long numberOfIgnoredTuples = 0;
	
	private Set<List<E>> generatedTests;

	public IteratedRandomizedNWiseAlgorithm(int n, int coverage) {
		super(n, coverage);
	}

	@Override
	public void reset() {

		iteratorTuples = TuplesIterator.create(getInput(), getN());
		generatedTests = new HashSet<>();

		numberOfRemainingTuples = iteratorTuples.getNumberOfTuples();
		numberOfIgnoredTuples = numberOfRemainingTuples * (100 - getCoverage()) / 100;

		super.reset();
	}

	@Override
	public List<E> getNext() throws GeneratorException {

		IEcfProgressMonitor generatorProgressMonitor = getGeneratorProgressMonitor();

		while (true) {

			if (generatorProgressMonitor != null) {
				if (generatorProgressMonitor.isCanceled()) {
					return null;
				}
			}

			if (numberOfRemainingTuples <= numberOfIgnoredTuples) {
				return null;
			}

			List<DimensionedItem<E>> nextTuple = iteratorTuples.next();
			numberOfRemainingTuples--;
			incrementProgress(1);
			
			if (isTupleInTestSet(nextTuple)) {
				continue;
			}
			
			if (checkConstraints(fillEmptyArgumentsWithNull(nextTuple)) == EvaluationResult.FALSE) {
				continue;
			}
			
			List<E> randomTest = generateTest(nextTuple);

			if (randomTest != null) {
				if (numberOfRemainingTuples <= numberOfIgnoredTuples) {
					return randomTest;
				}
				List<E> improvedTest = decreaseCoverage(randomTest, nextTuple);
				generatedTests.add(improvedTest);
				return improvedTest;
			} else {
				//System.out.println("Cannot generate test for" + toString(nTuple) + "!!! " + fRemainingTuples.size());
			}
		}
	}
	
	private List<E> fillEmptyArgumentsWithNull(List<DimensionedItem<E>> referenceTuple) {
		List<E> generatedResults = new ArrayList<>();
		
		for (int i = 0 ; i < getInput().size() ; i++) {
			generatedResults.add(null);
		}
		
		for (DimensionedItem<E> tuple : referenceTuple) {
			generatedResults.set(tuple.getDimension(), tuple.getItem());
		}
		
		return generatedResults;
	}

	private boolean isTupleInTestSet(List<DimensionedItem<E>> referenceTuple) {
		
		for (List<E> test : generatedTests) {
			if (isTupleInTestVector(test, referenceTuple)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isTupleInTestVector(List<E> referenceVector, List<DimensionedItem<E>> referenceTuple) {
		
		for (DimensionedItem<E> tuple : referenceTuple) {
			for (int i = 0 ; i < referenceVector.size() ; i++) {
				if (i == tuple.getDimension()) {
					if (!referenceVector.get(i).equals(tuple.getItem())) {
						return false;
					} else {
						break;
					}
				}
			}
		}
		
		return true;
	}

	private List<E> decreaseCoverage(List<E> randomTest, List<DimensionedItem<E>> nTuple) {
		boolean continueIteration;

		do {
			continueIteration = false;

			List<Integer> numberOfArguments = getListOfMissingArguments(nTuple);
			Collections.shuffle(numberOfArguments);
			
			for (int i = 0; i < numberOfArguments.size(); i++) {
				int coverageCurrent;
				int coverageWorst = getCoverage(randomTest);
				List<E> bestsArguments = new ArrayList<>();
				bestsArguments.add(randomTest.get(numberOfArguments.get(i)));
				for (E arguments : getInput().get(numberOfArguments.get(i))) {
					randomTest.set(numberOfArguments.get(i), arguments);
					if (checkConstraints(randomTest) == EvaluationResult.TRUE) {
						coverageCurrent = getCoverage(randomTest);
						if (coverageCurrent <= coverageWorst) {
							if (coverageCurrent < coverageWorst) {
								continueIteration = true;
								coverageWorst = coverageCurrent;
								bestsArguments.clear();
							}
							bestsArguments.add(arguments);
						}
					}
				}

				randomTest.set(numberOfArguments.get(i), bestsArguments.get((new Random().nextInt(bestsArguments.size()))));
			}
		} while (continueIteration);

		return randomTest;
	}
	
	private List<Integer> getListOfMissingArguments(List<DimensionedItem<E>> referenceTuple) {
		List<Integer> missingElements = new ArrayList<>();
		
		for (int i = 0 ; i < getInput().size() ; i++) {
			if (!isArgumentInTuple(i, referenceTuple)) {
				missingElements.add(i);
			}
		}
		
		return missingElements;
	}
	
	private boolean isArgumentInTuple(int argumentNumber, List<DimensionedItem<E>> referenceTuple) {
		
		for (DimensionedItem<E> tuple : referenceTuple) {
			if (tuple.getDimension() == argumentNumber) {
				return true;
			}
		}
		
		return false;
	}
	
	private List<E> generateTest(List<DimensionedItem<E>> referenceTuple) {

		List<E> bestTest = null;
		int worstCoverage = Integer.MAX_VALUE;

		for (int i = 0 ; i < GENERATE_TEST_TRIES ; i++) {
			List<E> currentTest = generateTestUsingTuple(referenceTuple);
			
			if (currentTest == null) {
				continue;
			}

			if (numberOfRemainingTuples <= numberOfIgnoredTuples) {
				return currentTest;
			}

			int currentCoverage = getCoverage(currentTest) + 1;

			if (currentCoverage <= worstCoverage) {
				bestTest = currentTest;
				worstCoverage = currentCoverage;
			}
		}

		return bestTest;
	}

	private List<E> generateTestUsingTuple(List<DimensionedItem<E>> referenceTuple) {
		List<E> testVector = null;

		for (int i = 0 ; i < GENERATE_TEST_USING_TUPLE_TRIES ; i++) {
			testVector = fillEmptyArgumentsWithRandomValue(referenceTuple);
			
			if (checkConstraints(testVector) == EvaluationResult.TRUE) {
				return testVector;
			}
		} 

		return null;
	}
	
	private List<E> fillEmptyArgumentsWithRandomValue(List<DimensionedItem<E>> referenceTuple) {
		List<E> generatedResults = new ArrayList<>();
		
		for (int i = 0 ; i < getInput().size() ; i++) {
			generatedResults.add(getInput().get(i).get((new Random()).nextInt(getInput().get(i).size())));
		}
		
		for (DimensionedItem<E> tuple : referenceTuple) {
			generatedResults.set(tuple.getDimension(), tuple.getItem());
		}
		
		return generatedResults;	
	}

	private int getCoverage(List<E> referenceVector) {
		int numberOfCoveredTuples = 0;
		
		TuplesIterator<E> localIteratorTuples = TuplesIterator.createFromList(referenceVector, getN());
		for (List<DimensionedItem<E>> element : localIteratorTuples) {
			if (isTupleInTestSet(element)) {
				numberOfCoveredTuples++;
			}
		}
		
		return numberOfCoveredTuples;
	}

//	protected EvaluationResult checkConstraintsOnExtendedTuple(List<E> vector) {
//
//		if (vector == null) {
//			return EvaluationResult.TRUE;
//		}
//
//		boolean insufficientData = false;
//
//		for (IConstraint<E> constraint : getConstraints()) {
//
//			EvaluationResult value = constraint.evaluate(vector);
//			if (value == EvaluationResult.FALSE) {
//				return EvaluationResult.FALSE;
//			}
//
//			if (value == EvaluationResult.INSUFFICIENT_DATA) {
//				insufficientData = true;
//			}
//		}
//
//		if (insufficientData)
//			return EvaluationResult.INSUFFICIENT_DATA;
//
//		return EvaluationResult.TRUE;
//	}
}
