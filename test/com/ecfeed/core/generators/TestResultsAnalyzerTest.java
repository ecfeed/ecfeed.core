/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestResultsAnalyzerTest {

	@Test
	public void shouldReturnCountZeroWhenEmpty() {

		TestResultsAnalyzer testResultsAnalyzer = new TestResultsAnalyzer();

		List<TestResultDescription> testResults = new ArrayList<TestResultDescription>(); 

		TestResultsAnalysis testResultsAnalysis = 
				testResultsAnalyzer.generateAnalysis(testResults, null, 0, 0);

		assertEquals(testResultsAnalysis.getCulpritCount(), 0);
	}

	@Test
	public void shouldcheckGenerationForOneResultWithTrueResult() {

		testForOneResult(true, 0);
	}

	@Test
	public void shouldcheckGenerationForOneResultWithTrueFalse() {

		testForOneResult(false, 1);
	}	

	private void testForOneResult(boolean testResult, int expectedFailureCount) {

		List<TestResultDescription> testResultDescrs = new ArrayList<TestResultDescription>();
		addTestResult(new String[]{ "1" }, testResult, testResultDescrs);

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, null, 1, 1);

		assertEquals(1, testResultsAnalysis.getCulpritCount());
		Culprit culprit = testResultsAnalysis.getCulprit(0);


		Culprit expectedCulprit = new Culprit(new DimensionedString[]{new DimensionedString(0, "1")}, 1, expectedFailureCount);

		assertTrue(expectedCulprit.isBasicMatch(culprit));		
	}

	@Test
	public void shouldGenerateAnalysisForOneResultWithThreeArgs() {

		List<TestResultDescription> testResultDescrs = new ArrayList<TestResultDescription>();

		addTestResult(new String[]{ "1", "2", "3" }, true, testResultDescrs);

		checkGenerationForN1(testResultDescrs);
		checkGenerationForN2(testResultDescrs);
		checkGenerationForN3(testResultDescrs);

		checkGenerationForN1to2(testResultDescrs);
		checkGenerationForN2to3(testResultDescrs);

		checkGenerationForN1to3(testResultDescrs);
	}	

	private void checkGenerationForN1(List<TestResultDescription> testResultDescrs) {

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, null, 1, 1);

		assertEquals(3, testResultsAnalysis.getCulpritCount());
		checkCulpritsForN1(testResultsAnalysis);
	}

	private void checkGenerationForN2(List<TestResultDescription> testResultDescrs) {

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, null, 2, 2);

		assertEquals(3, testResultsAnalysis.getCulpritCount());
		checkCulpritsForN2(testResultsAnalysis);
	}

	private void checkGenerationForN3(List<TestResultDescription> testResultDescrs) {

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, null, 3, 3);

		assertEquals(1, testResultsAnalysis.getCulpritCount());
		checkCulpritsForN3(testResultsAnalysis);
	}

	private void checkGenerationForN1to2(List<TestResultDescription> testResultDescrs) {

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, null, 1, 2);

		assertEquals(6, testResultsAnalysis.getCulpritCount());
		checkCulpritsForN1(testResultsAnalysis);
		checkCulpritsForN2(testResultsAnalysis);
	}

	private void checkGenerationForN2to3(List<TestResultDescription> testResultDescrs) {

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, null, 2, 3);

		assertEquals(4, testResultsAnalysis.getCulpritCount());
		checkCulpritsForN2(testResultsAnalysis);
		checkCulpritsForN3(testResultsAnalysis);
	}	

	private void checkGenerationForN1to3(List<TestResultDescription> testResultDescrs) {

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, null, 1, 3);

		assertEquals(7, testResultsAnalysis.getCulpritCount());
		checkCulpritsForN1(testResultsAnalysis);
		checkCulpritsForN2(testResultsAnalysis);
		checkCulpritsForN3(testResultsAnalysis);
	}	

	private void checkCulpritsForN1(TestResultsAnalysis testResultsAnalysis) {

		Culprit culprit = new Culprit(new DimensionedString[]{ new DimensionedString(0, "1") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		culprit = new Culprit(new DimensionedString[]{ new DimensionedString(1, "2") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		culprit = new Culprit(new DimensionedString[]{ new DimensionedString(2, "3") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));		
	}

	private void checkCulpritsForN2(TestResultsAnalysis testResultsAnalysis) {

		Culprit culprit = new Culprit(new DimensionedString[]{ new DimensionedString(0, "1"), new DimensionedString(1, "2") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		culprit = new Culprit(new DimensionedString[]{ new DimensionedString(1, "2"), new DimensionedString(2, "3") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));		

		culprit = new Culprit(new DimensionedString[]{ new DimensionedString(0, "1"), new DimensionedString(2, "3") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));		
	}

	private void checkCulpritsForN3(TestResultsAnalysis testResultsAnalysis) {

		Culprit culprit = new Culprit(new DimensionedString[]{ new DimensionedString(0, "1"), new DimensionedString(1, "2"), new DimensionedString(2, "3") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));
	}

	@Test
	public void shouldGenerateAnalysisForThreeResultsWithTwoArgs() {

		List<TestResultDescription> testResultDescrs = new ArrayList<TestResultDescription>();

		addTestResult(new String[]{ "2", "1" }, true, testResultDescrs);
		addTestResult(new String[]{ "1", "2" }, false, testResultDescrs);
		addTestResult(new String[]{ "1", "3" }, false, testResultDescrs);

		checkGeneration2by3forN1(testResultDescrs);
		checkGeneration2by3forN2(testResultDescrs);
	}

	private void checkGeneration2by3forN1(List<TestResultDescription> testResultDescrs) {
		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, null, 1, 1);

		assertEquals(5, testResultsAnalysis.getCulpritCount());


		Culprit culprit = new Culprit(new DimensionedString[]{ new DimensionedString(0, "1") }, 2, 2);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		Culprit culpritZero = testResultsAnalysis.getCulprit(0);
		assertTrue(culprit.isBasicMatch(culpritZero));

		culprit = new Culprit(new DimensionedString[]{ new DimensionedString(1, "2") }, 1, 1);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		culprit = new Culprit(new DimensionedString[]{ new DimensionedString(1, "3") }, 1, 1);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));		

		culprit = new Culprit(new DimensionedString[]{ new DimensionedString(0, "2") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		culprit = new Culprit(new DimensionedString[]{ new DimensionedString(1, "1") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));		
	}

	private void checkGeneration2by3forN2(List<TestResultDescription> testResultDescrs) {
		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, null, 2, 2);

		assertEquals(3, testResultsAnalysis.getCulpritCount());

		Culprit culprit = new Culprit(new DimensionedString[]{ new DimensionedString(0, "2"), new DimensionedString(1, "1") }, 1, 0);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		culprit = new Culprit(new DimensionedString[]{ new DimensionedString(0, "1"), new DimensionedString(1, "2") }, 1, 1);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));

		culprit = new Culprit(new DimensionedString[]{ new DimensionedString(0, "1"), new DimensionedString(1, "3") }, 1, 1);
		assertTrue(testResultsAnalysis.containsCulprit(culprit));		
	}

	@Test
	public void shouldGenerateAnalysisForFiveResultsWithFiveArgs() {

		List<TestResultDescription> testResultDescrs = new ArrayList<TestResultDescription>();

		addTestResult(new String[]{ "1", "2", "3", "4", "5" }, false, testResultDescrs);
		addTestResult(new String[]{ "0", "2", "3", "5", "4" }, false, testResultDescrs);
		addTestResult(new String[]{ "5", "2", "3", "7", "8" }, true, testResultDescrs);
		addTestResult(new String[]{ "7", "7", "3", "9", "8" }, false, testResultDescrs);
		addTestResult(new String[]{ "2", "4", "5", "3", "8" }, true, testResultDescrs);

		checkGeneration5by5forN1(testResultDescrs);
		checkGeneration5by5forN2(testResultDescrs);
	}

	private void checkGeneration5by5forN1(List<TestResultDescription> testResultDescrs) {

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, null, 1, 1);

				System.out.println(testResultsAnalysis.toString());

		Culprit culprit9 = testResultsAnalysis.getCulprit(9);
		assertTrue(culprit9.isBasicMatch(new Culprit(new DimensionedString[]{ new DimensionedString(2, "3") }, 4, 3)));

		Culprit culprit10 = testResultsAnalysis.getCulprit(10);
		assertTrue(culprit10.isBasicMatch(new Culprit(new DimensionedString[]{ new DimensionedString(1, "2") }, 3, 2)));		

		Culprit culprit11 = testResultsAnalysis.getCulprit(11);
		assertTrue(culprit11.isBasicMatch(new Culprit(new DimensionedString[]{ new DimensionedString(4, "8") }, 3, 1)));		
	}

	private void checkGeneration5by5forN2(List<TestResultDescription> testResultDescrs) {

		TestResultsAnalysis testResultsAnalysis = 
				new TestResultsAnalyzer().generateAnalysis(testResultDescrs, null, 2, 2);

		//		System.out.println(testResultsAnalysis.toString());

		Culprit culpritWithTupleToFind = 
				new Culprit( new DimensionedString[]{ 
						new DimensionedString(1, "2"), new DimensionedString(2, "3") }, 3, 2);
		Culprit foundCulprit = testResultsAnalysis.findCulpritByTuple(culpritWithTupleToFind);
		assertTrue(culpritWithTupleToFind.isBasicMatch(foundCulprit));
//		assertTrue(foundCulprit.getFailureIndex() > 6600);

		culpritWithTupleToFind = 
				new Culprit( new DimensionedString[]{ 
						new DimensionedString(2, "3"), new DimensionedString(4, "8") }, 2, 1);
		foundCulprit = testResultsAnalysis.findCulpritByTuple(culpritWithTupleToFind);
		assertTrue(culpritWithTupleToFind.isBasicMatch(foundCulprit));
//		assertTrue(foundCulprit.getFailureIndex() > 5000);		
	}	

	private void addTestResult(
			String[] testArguments, boolean result, List<TestResultDescription> testResultDescrs) {

		List<String> testArgList = new ArrayList<String>();

		for (String testArgument : testArguments) {
			testArgList.add(testArgument);
		}

		testResultDescrs.add(new TestResultDescription(testArgList, result));
	}

}
