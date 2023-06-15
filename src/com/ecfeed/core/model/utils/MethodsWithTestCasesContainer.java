package com.ecfeed.core.model.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;

public class MethodsWithTestCasesContainer {

	MethodNode x;
	TestCaseNode y;

	Map<MethodNode, List<TestCaseNode>> fMethodsToTestCases;

	public MethodsWithTestCasesContainer() {

		fMethodsToTestCases = new HashMap<>();
	}

	public void putMethodWithTestCases(MethodNode methodNode, List<TestCaseNode> testCases) {

		fMethodsToTestCases.put(methodNode, testCases);
	}

	public List<MethodNode> getMethods() {

		List<MethodNode> methodNodes = new ArrayList<>();

		for (Map.Entry<MethodNode, List<TestCaseNode>> entry : fMethodsToTestCases.entrySet()) {
			methodNodes.add(entry.getKey());
		}

		return methodNodes;
	}

	public List<TestCaseNode> getTestCaseNodes(MethodNode methodNode) {
		return fMethodsToTestCases.get(methodNode);
	}

}
