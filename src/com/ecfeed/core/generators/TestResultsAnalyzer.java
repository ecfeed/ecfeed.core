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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.generators.algorithms.Tuples;
import com.ecfeed.core.model.MethodNode;

public class TestResultsAnalyzer {

	TestResultsAnalysis fTestResultAnalysis = new TestResultsAnalysis();
	MethodNode fMethodNode;

	public TestResultsAnalysis generateAnalysis(List<TestResultDescription> testResults, MethodNode methodNode, int n1, int n2) {
		fMethodNode = methodNode;
		
		for (TestResultDescription testResult : testResults) {
			processTestResult(testResult, n1, n2);
		}

		fTestResultAnalysis.calculateFailureRates();
		return fTestResultAnalysis;
	}

	private void processTestResult(TestResultDescription testResult, int n1, int n2) {

		for (int n = n1; n <= n2; n++) {
			processTestResultForN(testResult, n);
		}
	}

	private void processTestResultForN(TestResultDescription testResult, int n) {

		Tuples<DimensionedString> tuples = new Tuples<DimensionedString>(
				createListOfDimensionedStrings(testResult.getTestArguments()), n);

		Set<List<DimensionedString>> allTuples = tuples.getAll();

		for (List<DimensionedString> tuple: allTuples) {
			aggregateTuple(tuple, testResult.getResult());
		}
	}

	private void aggregateTuple(List<DimensionedString> tuple, boolean result) {

		int culpritFailureCount = 0;

		if (result == false) {
			culpritFailureCount = 1;
		}

		Culprit culprit = new Culprit(tuple, fMethodNode, 1, culpritFailureCount);

		fTestResultAnalysis.aggregateCulprit(culprit);
	}

	private List<DimensionedString> createListOfDimensionedStrings(List<String> items) {

		List<DimensionedString> DimensionedStrings = new ArrayList<DimensionedString>();
		int dim = 0;

		for (String item: items) {
			DimensionedString DimensionedString = new DimensionedString(dim, item);
			DimensionedStrings.add(DimensionedString);
			dim += 1;
		}

		return DimensionedStrings;
	}

}