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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractParameterNodeHelper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.IParametersParentNode;

public class ParametersContainer {

	public enum ParameterType {

		STANDARD,
		EXPECTED
	};

	Map<String, ParametersData> fParametersDescriptions;

	public void calculateParametersData(
			IParametersParentNode parametersParentNode,
			ParameterType parameterType) {

		fParametersDescriptions = new HashMap<>();
		CompositeParameterNode linkingParameterNode = null;

		calculateParametersDataRecursive(
				parametersParentNode, parameterType, linkingParameterNode, fParametersDescriptions);
	}

	private void calculateParametersDataRecursive(
			IParametersParentNode parametersParentNode,
			ParameterType parameterType, 
			CompositeParameterNode linkingParameterNode,
			Map<String, ParametersData> inOutParametersDescriptions) {

		List<AbstractParameterNode> childParameters = parametersParentNode.getParameters();

		for (AbstractParameterNode childParameterNode : childParameters) {

			if (childParameterNode instanceof BasicParameterNode) {

				BasicParameterNode basicParameterNode = (BasicParameterNode) childParameterNode;

				addBasicParameter(
						basicParameterNode, 
						parameterType, 
						linkingParameterNode, 
						inOutParametersDescriptions);

				continue;
			}

			if (childParameterNode instanceof CompositeParameterNode) {

				CompositeParameterNode currentCompositeParameterNode = (CompositeParameterNode) childParameterNode; 

				calculateParametersForComposite(
						currentCompositeParameterNode, 
						parameterType, 
						linkingParameterNode,
						inOutParametersDescriptions);

				continue;
			}
		}
	}

	private void calculateParametersForComposite(
			CompositeParameterNode currentCompositeParameterNode,
			ParameterType parameterType, 
			CompositeParameterNode linkingParameterNode,
			Map<String, ParametersData> inOutParametersDescriptions) {

		AbstractParameterNode newlinkingParameterNode = 
				currentCompositeParameterNode.getLinkToGlobalParameter();

		if (newlinkingParameterNode == null) {

			calculateParametersDataRecursive(
					currentCompositeParameterNode,
					parameterType, 
					linkingParameterNode,
					inOutParametersDescriptions);

			return;
		}

		calculateParametersDataRecursive(
				(IParametersParentNode)newlinkingParameterNode,
				parameterType, 
				currentCompositeParameterNode,
				inOutParametersDescriptions);
	}

	private void addBasicParameter(
			BasicParameterNode basicParameterNode,
			ParameterType parameterType,
			CompositeParameterNode linkingParameterNode, 
			Map<String, ParametersData> inOutParametersDescriptions) {

		String qualifiedName = 
				AbstractParameterNodeHelper.getQualifiedName(basicParameterNode, linkingParameterNode);

		BasicParameterNode link = (BasicParameterNode) basicParameterNode.getLinkToGlobalParameter();

		if (link == null) {

			if (shouldAddParameter(basicParameterNode, parameterType)) {
				ParametersData parametersData = new ParametersData(basicParameterNode, linkingParameterNode);
				inOutParametersDescriptions.put(qualifiedName, parametersData);
			}

			return;
		}

		if (shouldAddParameter(link, parameterType)) {
			ParametersData parametersData = new ParametersData(basicParameterNode, linkingParameterNode);
			inOutParametersDescriptions.put(qualifiedName, parametersData);
		}

		return;
	}

	//	public void calculateParametersData(
	//			IParametersParentNode parametersParentNode,
	//			ParameterType parameterType) {
	//
	//		fParametersDescriptions = new HashMap<>();
	//
	////		List<BasicParameterNode> parameters = parametersParentNode.getNestedBasicParameters(false);
	////		
	////		for (BasicParameterNode basicParameterNode : parameters) {
	////		
	////			if (shouldAddParameter(basicParameterNode, parameterType)) {
	////		
	////				String qualifiedName = AbstractParameterNodeHelper.getQualifiedName(basicParameterNode);
	////		
	////				fParametersDescriptions.put(qualifiedName, basicParameterNode);
	////			}
	////		}
	//		
	//		List<CompositeParameterNode> parameters = parametersParentNode.getNestedCompositeParameters(false);
	//		
	//		for (CompositeParameterNode compositeParameterNode : parameters) {
	//			
	//			calculateParametersDataForComposite(compositeParameterNode); 
	//		}
	//		
	//	}

	//	private void calculateParametersDataForComposite(CompositeParameterNode compositeParameterNode) {
	//
	//		// TODO Auto-generated method stub
	//
	//		CompositeParameterNode link = (CompositeParameterNode) compositeParameterNode.getLinkToGlobalParameter();
	//
	//		if (link == null) {
	//			calculateDataForLocalParameters(compositeParameterNode);
	//		}
	//
	//		TUTAJ calculate data for linked composite
	//
	//	}

	public List<String> getParameterNames() {

		List<String> resultNames = new ArrayList<>();

		for (String key : fParametersDescriptions.keySet()) {
			resultNames.add(key);
		}

		Collections.sort(resultNames);

		return resultNames;
	}

	public BasicParameterNode findBasicParameter(String qualifiedNameOfParameter) {

		ParametersData parametersData = fParametersDescriptions.get(qualifiedNameOfParameter);

		if (parametersData == null) {
			return null;
		}

		return parametersData.getBasicParameterNode();
	}

	public String findName(BasicParameterNode basicParameterNode) {

		for (String key : fParametersDescriptions.keySet()) {

			ParametersData parametersData = fParametersDescriptions.get(key);

			BasicParameterNode currentBasicParameterNode = parametersData.getBasicParameterNode();

			if (basicParameterNode == currentBasicParameterNode) {
				return key;
			}
		}

		return null;
	}

	public CompositeParameterNode findLinkingParameter(String qualifiedNameOfParameter) {

		ParametersData parametersData = fParametersDescriptions.get(qualifiedNameOfParameter);

		if (parametersData == null) {
			return null;
		}

		return parametersData.getLinkingParameterNode();

	}

	private static boolean shouldAddParameter(
			BasicParameterNode basicParameterNode,
			ParameterType parameterType) {

		if (parameterType == ParameterType.EXPECTED) {

			if (basicParameterNode.isExpected()) {
				return true;
			}

			return false;

		} else {

			if (basicParameterNode.isExpected()) {
				return false;
			}

			if (basicParameterNode.getChoices().isEmpty()) {
				return false;
			}

			return true;
		}
	}

	private static class ParametersData {

		private BasicParameterNode fBasicParameterNode;
		private CompositeParameterNode fLinkingCompositeParameterNode;

		ParametersData(BasicParameterNode basicParameterNode, CompositeParameterNode linkingCompositeParameterNode) {

			fBasicParameterNode = basicParameterNode;
			fLinkingCompositeParameterNode = linkingCompositeParameterNode;
		}

		BasicParameterNode getBasicParameterNode() {
			return fBasicParameterNode;
		}

		CompositeParameterNode getLinkingParameterNode() {
			return fLinkingCompositeParameterNode;
		}
	}

}
