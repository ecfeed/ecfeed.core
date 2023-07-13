package com.ecfeed.core.model.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;

public class MethodsWithResultsOfGenerations {

	Map<MethodNode, List<ParameterWithLinkingContext>> fMethodsToDeployedParameters;
	Map<MethodNode, List<TestCaseNode>> fMethodsToTestCases;

	public MethodsWithResultsOfGenerations() {

		fMethodsToDeployedParameters = new HashMap<>();
		fMethodsToTestCases = new HashMap<>();
	}

	public void saveResultsForMethods(List<MethodNode> methodNodes) {

		for (MethodNode methodNode : methodNodes) {
			putMethodWithResultsOfGenerations(methodNode);
		}
	}

	public void clearResultsForAllMethods() {

		List<MethodNode> methodNodes = getMethods();

		for (MethodNode methodNode : methodNodes) {

			methodNode.removeAllTestCases();
			methodNode.removeAllDeployedParameters();
		}
	}

	public void restoreResultsForAllMethods() {

		List<MethodNode> methodNodes = getMethods();

		for (MethodNode methodNode : methodNodes) {

			restoreResultsForOneMethod(methodNode);
		}
	}

	private void putMethodWithResultsOfGenerations(MethodNode methodNode) {

		putDeployedParamaters(methodNode);
		putTestCases(methodNode);
	}

	private void putDeployedParamaters(MethodNode methodNode) {

		List<ParameterWithLinkingContext> deployedParametersWithLinkingContexts = 
				methodNode.getDeployedParametersWithLinkingContexts();

		fMethodsToDeployedParameters.put(methodNode, new ArrayList<>(deployedParametersWithLinkingContexts));
	}

	private void putTestCases(MethodNode methodNode) {

		List<TestCaseNode> testCases = methodNode.getTestCases();
		fMethodsToTestCases.put(methodNode, new ArrayList<>(testCases));
	}

	private void restoreResultsForOneMethod(MethodNode methodNode) {

		methodNode.removeAllTestCases();
		methodNode.addTestCases(fMethodsToTestCases.get(methodNode));

		methodNode.removeAllDeployedParameters();
		methodNode.setDeployedParametersWithContexts(fMethodsToDeployedParameters.get(methodNode));
	}

	private List<MethodNode> getMethods() {

		List<MethodNode> methodNodes = new ArrayList<>();

		for (Map.Entry<MethodNode, List<TestCaseNode>> entry : fMethodsToTestCases.entrySet()) {
			methodNodes.add(entry.getKey());
		}

		return methodNodes;
	}

}
