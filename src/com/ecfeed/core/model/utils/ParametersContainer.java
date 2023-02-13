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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.IParametersParentNode;

public class ParametersContainer {

	public enum ParameterType { // TODO MO-RE use instead of boolean addExpectedParameterNodes

		STANDARD,
		EXPECTED
	};

	Map<String, BasicParameterNode> fParametersDescriptions;

	public void calculateParametersData(
			IParametersParentNode parametersParentNode,
			boolean addExpectedParameterNodes) {

		fParametersDescriptions = new HashMap<>();

		addParameterNamesRecursively(parametersParentNode, addExpectedParameterNodes, fParametersDescriptions);
	}

	public List<String> getParameterNames() {

		List<String> resultNames = new ArrayList<>();

		for (String key : fParametersDescriptions.keySet()) {
			resultNames.add(key);
		}

		Collections.sort(resultNames);

		return resultNames;
	}

	public BasicParameterNode findBasicParameter(
			String qualifiedNameOfParameter, 
			IParametersParentNode parametersParentNode) {

		for (String key : fParametersDescriptions.keySet()) {

			if (qualifiedNameOfParameter.equals(key)) {
				return fParametersDescriptions.get(key);
			}
		}

		return null;
	}

	private static void addParameterNamesRecursively(
			IParametersParentNode parametersParentNode, 
			boolean addExpectedParameterNodes,
			Map<String, BasicParameterNode> inOutParameterCompositeNames) {

		List<BasicParameterNode> parameters = parametersParentNode.getNestedBasicParameters(true);

		for (BasicParameterNode basicParameterNode : parameters) {

			if (shouldAddParameter(basicParameterNode, addExpectedParameterNodes)) {

				String qualifiedName = basicParameterNode.getQualifiedName();

				inOutParameterCompositeNames.put(qualifiedName, basicParameterNode);
			}
		}
	}

	private static boolean shouldAddParameter(
			BasicParameterNode methodParameterNode,
			boolean addExpectedParameterNodes) {

		if (addExpectedParameterNodes) {

			if (methodParameterNode.isExpected()) {
				return true;
			}

			return false;

		} else {

			if (methodParameterNode.isExpected()) {
				return false;
			}

			if (methodParameterNode.getChoices().size() == 0) {
				return false;
			}

			return true;
		}
	}

}
