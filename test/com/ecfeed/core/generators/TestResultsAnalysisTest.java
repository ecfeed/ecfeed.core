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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestResultsAnalysisTest {

	@Test
	public void shouldReturnCountZeroWhenEmpty() {
		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalysis();
		assertEquals(testResultsAnalysis.getCulpritCount(), 0);
	}

	@Test
	public void shouldReturnAggregateOneCulprit() {

		Culprit culprit = createCulpritWith1Dimension("1", 1, 0);

		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalysis();
		testResultsAnalysis.aggregateCulprit(culprit);

		assertEquals(testResultsAnalysis.getCulpritCount(), 1);

		Culprit aggregatedCulprit = testResultsAnalysis.getCulprit(0);

		assertTrue(culprit.isBasicMatch(aggregatedCulprit));
	}	

	@Test
	public void shouldReturnAggregateCulpritTwiceVerA() {

		Culprit culprit = createCulpritWith1Dimension("1", 1, 0);

		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalysis();
		testResultsAnalysis.aggregateCulprit(culprit);
		testResultsAnalysis.aggregateCulprit(culprit);

		assertEquals(testResultsAnalysis.getCulpritCount(), 1);

		Culprit aggregatedCulprit = testResultsAnalysis.getCulprit(0);

		assertTrue(culprit.isTupleMatch(aggregatedCulprit));
		assertEquals(2, aggregatedCulprit.getOccurenceCount());
		assertEquals(0, aggregatedCulprit.getFailureCount());
	}	

	@Test
	public void shouldReturnAggregateCulpritTwiceVerB() {

		Culprit culprit = createCulpritWith1Dimension("1", 1, 1);

		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalysis();
		testResultsAnalysis.aggregateCulprit(culprit);
		testResultsAnalysis.aggregateCulprit(culprit);

		assertEquals(testResultsAnalysis.getCulpritCount(), 1);
		Culprit aggregatedCulprit = testResultsAnalysis.getCulprit(0);

		assertTrue(culprit.isTupleMatch(aggregatedCulprit));
		assertEquals(2, aggregatedCulprit.getOccurenceCount());
		assertEquals(2, aggregatedCulprit.getFailureCount());
	}	

	@Test
	public void shouldReturnAggregateTwoDifferentCulpritsOfSameSize() {

		Culprit culprit1 = createCulpritWith1Dimension("1", 1, 1);
		Culprit culprit2 = createCulpritWith1Dimension("2", 1, 1);

		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalysis();
		testResultsAnalysis.aggregateCulprit(culprit1);
		testResultsAnalysis.aggregateCulprit(culprit2);

		assertEquals(testResultsAnalysis.getCulpritCount(), 2);
		Culprit aggregatedCulprit1 = testResultsAnalysis.getCulprit(0);
		Culprit aggregatedCulprit2 = testResultsAnalysis.getCulprit(1);

		assertTrue(culprit1.isTupleMatch(aggregatedCulprit1));
		assertTrue(culprit2.isTupleMatch(aggregatedCulprit2));
	}	

	@Test
	public void shouldReturnAggregateTwoCulpritsOfDifferentSizes() {

		Culprit culprit1 = createCulpritWith1Dimension("1", 3, 1);
		Culprit culprit2 = createCulpritWith2Dimensions("1", "1", 5, 2);

		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalysis();
		testResultsAnalysis.aggregateCulprit(culprit1);
		testResultsAnalysis.aggregateCulprit(culprit2);

		assertEquals(testResultsAnalysis.getCulpritCount(), 2);

		Culprit[] arr = {culprit1, culprit2};
		assertTrue(analysisContainsCulprits(testResultsAnalysis, arr));
	}

	@Test
	public void shouldReturnAggregateMultipleCulprits() {

		TestResultsAnalysis testResultsAnalysis = new TestResultsAnalysis();

		testResultsAnalysis.aggregateCulprit(createCulpritWith2Dimensions("1", "1", 1, 1));
		testResultsAnalysis.aggregateCulprit(createCulpritWith2Dimensions("5", "2", 1, 0));
		testResultsAnalysis.aggregateCulprit(createCulpritWith1Dimension("5", 1, 1));
		testResultsAnalysis.aggregateCulprit(createCulpritWith1Dimension("1", 1, 0));
		testResultsAnalysis.aggregateCulprit(createCulpritWith2Dimensions("1", "1", 1, 0));
		testResultsAnalysis.aggregateCulprit(createCulpritWith2Dimensions("5", "2", 1, 1));
		testResultsAnalysis.aggregateCulprit(createCulpritWith2Dimensions("1", "1", 1, 1));
		testResultsAnalysis.aggregateCulprit(createCulpritWith1Dimension("5", 1, 1));
		testResultsAnalysis.aggregateCulprit(createCulpritWith2Dimensions("5", "2", 1, 0));
		testResultsAnalysis.aggregateCulprit(createCulpritWith1Dimension("5", 1, 1));
		testResultsAnalysis.aggregateCulprit(createCulpritWith1Dimension("1", 1, 1));

		assertEquals(4, testResultsAnalysis.getCulpritCount());

		Culprit[] aggregatedCulprits = {
				createCulpritWith1Dimension("1", 2, 1),
				createCulpritWith2Dimensions("1", "1", 3, 2),
				createCulpritWith2Dimensions("5", "2", 3, 1),
				createCulpritWith1Dimension("5", 3, 3) };

		assertTrue(analysisContainsCulprits(testResultsAnalysis, aggregatedCulprits));
	}	
	
	@Test 
	public void ShouldCalculateFailureIndex()
	{
		TestResultsAnalysis testresultanalysis = new TestResultsAnalysis();
		Culprit culprit1 = createCulpritWith1Dimension("1", 3, 1);
		Culprit culprit2 = createCulpritWith2Dimensions("1", "1", 5, 2);
		Culprit culprit3 = createCulpritWith2Dimensions("1", "1", 7, 3);
		Culprit culprit4 = createCulpritWith2Dimensions("1", "1", 9, 2);
		testresultanalysis.aggregateCulprit(culprit1);
		testresultanalysis.aggregateCulprit(culprit2);
		testresultanalysis.aggregateCulprit(culprit3);
		testresultanalysis.aggregateCulprit(culprit4);
		testresultanalysis.calculateFailureRates();
		int expectedIndex2 = (7/21);
		int expectedIndex1 = (1/3) ;
		assertEquals(expectedIndex2, culprit2.getFailureIndex(), 100*7/21);
		assertEquals(expectedIndex1, (double) culprit1.getFailureIndex(), 100/3);
	}
	
	@Test
	public void ShouldCalculateFailureIndexWith5Dim()
	{
		List<TestResultDescription> fTestResultDescrs = createtestResultDescrs();
		TestResultsAnalysis fTestResultsAnalysis 
		= new TestResultsAnalyzer().generateAnalysis(fTestResultDescrs, null, 1, 1);
			
		int failureCount0 = fTestResultsAnalysis.getCulprit(0).getFailureCount();
		int occurenceCount0 = fTestResultsAnalysis.getCulprit(0).getOccurenceCount();
		int failsByOccurs = failureCount0 / occurenceCount0;
		double expectedIndex = (failsByOccurs);
		int actualIndex = fTestResultsAnalysis.getCulprit(0).getFailureIndex();
		assertEquals(expectedIndex, actualIndex, 100);
		
		TestResultsAnalysis fTestResultsAnalysis2 
		= new TestResultsAnalyzer().generateAnalysis(fTestResultDescrs, null, 2, 5);
		int failureCount5 = fTestResultsAnalysis2.getCulprit(4).getFailureCount();
		int occurenceCount5 = fTestResultsAnalysis2.getCulprit(4).getOccurenceCount();
		double expectedIndex5 = (failureCount5/occurenceCount5);
		int actualIndex5 = fTestResultsAnalysis2.getCulprit(4).getFailureIndex();
		assertEquals(expectedIndex5, actualIndex5, 100);	
	}
	
	@Test
	public void ShouldFindCulprit()
	{
		TestResultsAnalysis testresultanalysis = new TestResultsAnalysis();
		Culprit culprit2 = createCulpritWith2Dimensions("1", "1", 5, 2);
		Culprit culprit3 = createCulpritWith2Dimensions("1", "1", 7, 3);
		Culprit culprit4 = createCulpritWith2Dimensions("1", "1", 9, 2);
		testresultanalysis.aggregateCulprit(culprit2);
		testresultanalysis.aggregateCulprit(culprit3);
		Culprit FoundCulprit = testresultanalysis.findCulpritByTuple(culprit4);
		Culprit expectedCulprit = createCulpritWith2Dimensions("1", "1", 12, 5);
		assertTrue(expectedCulprit.isTupleMatch(FoundCulprit));
		assertTrue(testresultanalysis.containsCulprit(culprit2));
		assertFalse(testresultanalysis.containsCulprit(culprit4));
	}

	private static Culprit createCulpritWith1Dimension(String testInputValue1, int occurences, int failures) {

		List<DimensionedString> testInput = new ArrayList<DimensionedString>();
		DimensionedString dimension1 = new DimensionedString(0, testInputValue1);
		testInput.add(dimension1);

		Culprit culprit = new Culprit(testInput, occurences, failures);
		return culprit;
	}

	private static Culprit createCulpritWith2Dimensions(String testInputValue1, String testInputValue2, int occurences, int failures) {

		List<DimensionedString> testInput = new ArrayList<DimensionedString>();

		DimensionedString dimension1 = new DimensionedString(0, testInputValue1);
		testInput.add(dimension1);

		DimensionedString dimension2 = new DimensionedString(0, testInputValue2);
		testInput.add(dimension2);

		Culprit culprit = new Culprit(testInput, occurences, failures);
		return culprit;
	}	

	private boolean analysisContainsCulprits(TestResultsAnalysis testResultsAnalysis, Culprit[] culprits) {

		for (Culprit culprit : culprits) {
			if (!testResultsAnalysis.containsCulprit(culprit)) {
				return false;
			}
		}

		return true;
	}
	
	private void addTestResult(
			String[] testArguments, boolean result, List<TestResultDescription> testResultDescrs) {

		List<String> testArgList = new ArrayList<String>();

		for (String testArgument : testArguments) {
			testArgList.add(testArgument);
		}
		System.out.println(testArgList);

		testResultDescrs.add(new TestResultDescription(testArgList, result));
	}

	public List<TestResultDescription> createtestResultDescrs()	{ 

		List<TestResultDescription> testResultDescrs = new ArrayList<TestResultDescription>();

		addTestResult(new String[]{ "1", "2", "3", "4", "5" }, false, testResultDescrs);
		addTestResult(new String[]{ "0", "2", "3", "5", "4" }, false, testResultDescrs);
		addTestResult(new String[]{ "5", "2", "3", "7", "8" }, true, testResultDescrs);
		addTestResult(new String[]{ "7", "7", "3", "9", "8" }, false, testResultDescrs);
		addTestResult(new String[]{ "2", "4", "5", "3", "8" }, true, testResultDescrs);

		return testResultDescrs;
	}

}
