/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.utils.ExceptionHelper;

public class TestCasesHolder {

	private List<TestCaseNode> fTestCaseNodes;
	private List<TestSuiteNode> fTestSuiteNodes;
	private IModelChangeRegistrator fModelChangeRegistrator;

	public TestCasesHolder(IModelChangeRegistrator modelChangeRegistrator) {

		fTestCaseNodes = new ArrayList<>();
		fTestSuiteNodes = new ArrayList<>();
		fModelChangeRegistrator = modelChangeRegistrator;
	}

	public List<TestCaseNode> getTestCaseNodes() { // XYX convert to getting copies

		return fTestCaseNodes;
	}

	public List<TestSuiteNode> getTestSuiteNodes() { // XYX remove

		return fTestSuiteNodes;
	}

	public boolean hasTestSuites() {

		if (fTestSuiteNodes.isEmpty()) {
			return false;
		}
		return true;
	}

	public void addTestCase(TestCaseNode testCaseNode, int index) { // XYX REMOVE ??

		fTestCaseNodes.add(index, testCaseNode);
		registerChange();
	}

	public void addTestCase(
			TestCaseNode testCaseNode, int index, Optional<Integer> indexOfNewTestSuite, IAbstractNode parent) {

		String testSuiteName = testCaseNode.getName();

		TestSuiteNode testSuiteNode = findTestSuite(testSuiteName);

		if (testSuiteNode == null) {

			testSuiteNode = new TestSuiteNode(testSuiteName, fModelChangeRegistrator);

			if (indexOfNewTestSuite.isPresent()) {
				addTestSuite(testSuiteNode, indexOfNewTestSuite.get(), parent);
			} else {
				addTestSuite(testSuiteNode, parent);
			}
		}

		testSuiteNode.addTestCase(testCaseNode);

		fTestCaseNodes.add(index, testCaseNode);
		testCaseNode.setParent(parent);

		registerChange();
	}

	//	public void removeTestCase(TestCaseNode testCaseNode) {
	//		
	//		fTestCaseNodes.remove(testCaseNode);
	//		registerChange();
	//	}

	public void removeTestCase(TestCaseNode testCaseNode) {

		String testSuiteName = testCaseNode.getName();

		TestSuiteNode testSuiteNode = findTestSuite(testSuiteName);

		if (testSuiteNode == null) {
			ExceptionHelper.reportRuntimeException("Non existing test suite.");
		}

		testSuiteNode.removeTestCase(testCaseNode);

		if (testSuiteNode.getTestCaseNodes().size() == 0) {
			removeTestSuite(testSuiteNode);
		}

		testCaseNode.setParent(null);

		//		fTestCasesHolder.removeTestCase(testCaseNode);
		fTestCaseNodes.remove(testCaseNode);

		registerChange();
	}


	public void addTestSuite(TestSuiteNode testSuite, IAbstractNode parent) {

		addTestSuite(testSuite, fTestSuiteNodes.size(), parent);
	}

	public void addTestSuite(TestSuiteNode testSuiteNode, int index, IAbstractNode parent) {

		testSuiteNode.setParent(parent);
		fTestSuiteNodes.add(index, testSuiteNode);
		registerChange();
	}

	public void removeTestSuite(TestSuiteNode testSuite) {

		fTestSuiteNodes.remove(testSuite);
		registerChange();
	}

	public boolean isEmpty() {

		return fTestCaseNodes.isEmpty();
	}

	public void removeAllTestCases() {

		fTestCaseNodes.clear();
		fTestSuiteNodes.clear();
		registerChange();
	}

	private void registerChange() {

		if (fModelChangeRegistrator == null) {
			return;
		}

		fModelChangeRegistrator.registerChange();
	}

	public void replaceTestCases(List<TestCaseNode> testCases){ // TODO MO-RE fix test suites after changing test cases

		fTestCaseNodes.clear();
		fTestCaseNodes.addAll(testCases);
		registerChange();
	}

	public Optional<TestSuiteNode> getTestSuite(String testSuiteName) {

		for (TestSuiteNode testSuite : fTestSuiteNodes) {
			if (testSuite.getSuiteName().equalsIgnoreCase(testSuiteName)) {
				return Optional.of(testSuite);
			}
		}

		return Optional.empty();
	}

	public TestSuiteNode findTestSuite(String testSuiteName) {

		if (testSuiteName == null) {
			ExceptionHelper.reportRuntimeException("Empty test suite name.");
		}

		for (TestSuiteNode testSuiteNode : fTestSuiteNodes) {
			if (testSuiteName.equals(testSuiteNode.getName())) {
				return testSuiteNode; 
			}
		}

		return null;
	}

	public TestSuiteNode provideValidTestSuiteNode(String newName, IAbstractNode parent) { // XYX convert parent type do method node - also in other methods

		TestSuiteNode newTestSuiteNode = findTestSuite(newName);

		if (newTestSuiteNode == null) {
			newTestSuiteNode = new TestSuiteNode(newName, fModelChangeRegistrator);
			addTestSuite(newTestSuiteNode, parent);
		}

		return newTestSuiteNode;
	}

}
