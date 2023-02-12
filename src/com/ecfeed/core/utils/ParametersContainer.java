/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.IParametersParentNode;

public class ParametersContainer {

	public List<String> initialize(
			IParametersParentNode parametersParentNode,
			boolean addExpectedParameterNodes) {

		List<String> parametersCompositeNames = new ArrayList<String>();

		addParameterNamesRecursively(parametersParentNode, addExpectedParameterNodes, parametersCompositeNames);

		return parametersCompositeNames;
	}

	public BasicParameterNode findBasicParameter(
			String qualifiedNameOfParameter, 
			IParametersParentNode parametersParentNode) {

		Optional<BasicParameterNode> candidate = parametersParentNode.getNestedBasicParameters(true).stream()
				.filter(e -> e.getQualifiedName().equals(qualifiedNameOfParameter))
				.findAny();

		if (candidate.isPresent()) {
			return candidate.get();
		}

		return null;
	}

	private static void addParameterNamesRecursively(
			IParametersParentNode parametersParentNode, 
			boolean addExpectedParameterNodes,
			List<String> inOutParameterCompositeNames) {

		List<BasicParameterNode> parameters = parametersParentNode.getNestedBasicParameters(true);

		parameters.stream()
		.filter(e -> shouldAddItem(e, addExpectedParameterNodes))
		.forEach(e -> inOutParameterCompositeNames.add(e.getQualifiedName()));
	}

	private static boolean shouldAddItem(
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
