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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.evaluator.DummyEvaluator;
import com.ecfeed.core.evaluator.HomebrewConstraintEvaluator;
import com.ecfeed.core.generators.CartesianProductGenerator;
import com.ecfeed.core.generators.testutils.GeneratorTestUtils;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.SimpleProgressMonitor;

public class NWiseAlgorithmTest{

	@SuppressWarnings("rawtypes")
	protected void testCorrectness(Class<? extends IAlgorithm> algorithmUnderTestClass) {
		for(int numOfVariables : new int[]{1, 2, 3}){ // for(int numOfVariables : new int[]{1, 2, 5}){
			for(int choicesPerVariable : new int[]{1, 2, 3}){ //for(int choicesPerVariable : new int[]{1, 2, 5}){
				for(int n = 1; n <= numOfVariables; n++){
					List<List<String>> input = GeneratorTestUtils.prepareInput(numOfVariables, choicesPerVariable);
					try{
						IAlgorithm<String> algorithmUnderTest = getAlgorithm(algorithmUnderTestClass, n);
						algorithmUnderTest.initialize(input, new DummyEvaluator<>(), new SimpleProgressMonitor());
						Set<List<String>> algorithmResult = GeneratorTestUtils.algorithmResult(algorithmUnderTest);
						assertTrue(containsAllTuples(algorithmResult, input, n));
					} catch (Exception e) {
						fail("Unexpected algorithm exception: " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected void testConstraints(Class<? extends IAlgorithm> algorithmUnderTestClass){
		for(int numOfVariables : new int[]{1, 2, 3}){ // for(int numOfVariables : new int[]{1, 2, 5}){
			for(int choicesPerVariable : new int[]{1, 2, 3}){ // for(int choicesPerVariable : new int[]{1, 2, 5}){
				for(int n = 1; n <= numOfVariables; n++){
					List<List<String>> input = GeneratorTestUtils.prepareInput(numOfVariables, choicesPerVariable);
					Collection<IConstraint<String>> constraints = GeneratorTestUtils.generateRandomConstraints(input);
					try {
						IAlgorithm<String> algorithmUnderTest = getAlgorithm(algorithmUnderTestClass, n);
						algorithmUnderTest.initialize(input, new HomebrewConstraintEvaluator<>(constraints), new SimpleProgressMonitor());
						Set<List<String>> algorithmResult = GeneratorTestUtils.algorithmResult(algorithmUnderTest);
						for(List<String> vector : algorithmResult){
							for(IConstraint<String> constraint : constraints){
								assertTrue(constraint.evaluate(vector) == EvaluationResult.TRUE);
							}
						}
					} catch (Exception e) {
						fail("Unexpected algorithm exception: " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private IAlgorithm<String> getAlgorithm(Class<? extends IAlgorithm> algorithmUnderTestClass, int n) {
		Constructor<? extends IAlgorithm> algorithmUnderTestConstructor;
		try {
			algorithmUnderTestConstructor = algorithmUnderTestClass.getConstructor(int.class, int.class);
			IAlgorithm<String> algorithm = algorithmUnderTestConstructor.newInstance(n, 100);
			return algorithm;
		} catch (Exception e) {
			fail("Unexpected algorithm exception: " + e.getMessage());
			return null;
		}
	}

	protected boolean containsAllTuples(Set<List<String>> algorithmResult,
			List<List<String>> input, int n) throws Exception {
		Set<List<String>> notCoveredTuples = getAllTuples(input, n);
		for(List<String> vector : algorithmResult){
			notCoveredTuples.removeAll((new Tuples<String>(vector, n)).getAll());
		}
		return notCoveredTuples.isEmpty();
	}

	//	@Test
	//	public void nwisePercentageCovered() {
	//
	//		try {
	//			for (int n = 2; n < 4; n++) {
	//				List<List<String>> input = GeneratorTestUtils
	//						.prepareInput(5, 6);
	//				long totalTuples = calculateTotalTuples(input, n);
	//				for (int p = 0; p <= 100; p+=10) {
	//
	//					OptimalNWiseAlgorithm<String> nwise = new OptimalNWiseAlgorithm<String>(
	//							n, p);
	//
	//					nwise.initialize(input, new DummyEvaluator<>(), new SimpleProgressMonitor());
	//
	//					List<List<String>> nwiseSuite = new ArrayList<List<String>>();
	//
	//					List<String> next = null;
	//
	//					while ((next = nwise.getNext()) != null) {
	//						nwiseSuite.add(next);
	//					}
	//
	//					int nwiseTuplesCovered = calculateCoveredTuples(nwiseSuite,
	//							input, n);
	//					int leastTuplesExpected = (int)Math.ceil(((double) (p * totalTuples)) / 100);
	//
	//					assertTrue( nwiseTuplesCovered>= leastTuplesExpected);
	//				}
	//			}
	//		} catch (Exception e) {
	//			fail("Unexpected Exception: " + e.getMessage());
	//		}
	//	}


	protected Set<List<String>> getAllTuples(List<List<String>> input, int n) throws Exception{
		Set<List<String>> result  = new HashSet<List<String>>();
		Tuples<List<String>> parameterTuples = new Tuples<List<String>>(input, n);
		while(parameterTuples.hasNext()){
			List<List<String>> next = parameterTuples.next();
			CartesianProductGenerator<String> generator = new CartesianProductGenerator<String>();
			generator.initialize(next, new DummyEvaluator<>(), new ArrayList<>(), new SimpleProgressMonitor());
			List<String> tuple;
			while((tuple = generator.next()) != null){
				result.add(tuple);
			}
		}
		return result;
	}
}
