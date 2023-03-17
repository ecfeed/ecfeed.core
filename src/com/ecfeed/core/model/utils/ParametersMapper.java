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
import com.ecfeed.core.model.AbstractParameterSignatureHelper;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.Decorations;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.ExtendedName;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.TypeIncluded;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.TypeOfLink;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class ParametersMapper {

	public enum ParameterType {

		STANDARD,
		EXPECTED
	};

	Map<String, BasicParameterWithLinkingContext> fParametersDescriptions;

	public void calculateParametersData(
			IParametersParentNode parametersParentNode,
			ParameterType parameterType,
			IExtLanguageManager extLanguageManager) {

		fParametersDescriptions = new HashMap<>();
		CompositeParameterNode linkingParameterNode = null;

		calculateParametersDataRecursive(
				parametersParentNode, 
				parameterType, 
				linkingParameterNode, 
				fParametersDescriptions, 
				extLanguageManager);
	}

	public List<String> getParameterNames() {

		List<String> resultNames = new ArrayList<>();

		for (String key : fParametersDescriptions.keySet()) {
			resultNames.add(key);
		}

		Collections.sort(resultNames);

		return resultNames;
	}

	public BasicParameterNode findBasicParameter(String qualifiedNameOfParameter) {

		BasicParameterWithLinkingContext parametersData = fParametersDescriptions.get(qualifiedNameOfParameter);

		if (parametersData == null) {
			return null;
		}

		return parametersData.getBasicParameterNode();
	}

	public String findName(BasicParameterNode basicParameterNode) {

		for (String key : fParametersDescriptions.keySet()) {

			BasicParameterWithLinkingContext parametersData = fParametersDescriptions.get(key);

			BasicParameterNode currentBasicParameterNode = parametersData.getBasicParameterNode();

			if (basicParameterNode == currentBasicParameterNode) {
				return key;
			}
		}

		return null;
	}

	public CompositeParameterNode findLinkingParameter(String qualifiedNameOfParameter) {

		BasicParameterWithLinkingContext parametersData = fParametersDescriptions.get(qualifiedNameOfParameter);

		if (parametersData == null) {
			return null;
		}

		return parametersData.getLinkingParameterNode();

	}

	public List<BasicParameterDescription> getListOfParameterDescriptions() {

		List<BasicParameterDescription> result = new ArrayList<>();

		for (Map.Entry<String, BasicParameterWithLinkingContext> entry : fParametersDescriptions.entrySet()) {

			String qualifiedName = entry.getKey();
			BasicParameterWithLinkingContext basicParameterWithLinkingContext = entry.getValue();

			BasicParameterDescription basicParameterDescription = 
					new BasicParameterDescription(
							qualifiedName, 
							basicParameterWithLinkingContext.getBasicParameterNode(), 
							basicParameterWithLinkingContext.getLinkingParameterNode());

			result.add(basicParameterDescription);
		}		

		return result;
	}

	private void calculateParametersDataRecursive(
			IParametersParentNode parametersParentNode,
			ParameterType parameterType, 
			CompositeParameterNode linkingParameterNode,
			Map<String, BasicParameterWithLinkingContext> inOutParametersDescriptions,
			IExtLanguageManager extLanguageManager) {

		List<AbstractParameterNode> childParameters = parametersParentNode.getParameters();

		for (AbstractParameterNode childParameterNode : childParameters) {

			if (childParameterNode instanceof BasicParameterNode) {

				BasicParameterNode basicParameterNode = (BasicParameterNode) childParameterNode;

				addBasicParameter(
						basicParameterNode, 
						parameterType, 
						linkingParameterNode, 
						inOutParametersDescriptions,
						extLanguageManager);

				continue;
			}

			if (childParameterNode instanceof CompositeParameterNode) {

				CompositeParameterNode currentCompositeParameterNode = (CompositeParameterNode) childParameterNode; 

				calculateParametersForComposite(
						currentCompositeParameterNode, 
						parameterType, 
						linkingParameterNode,
						inOutParametersDescriptions, 
						extLanguageManager);

				continue;
			}
		}
	}

	private void calculateParametersForComposite(
			CompositeParameterNode currentCompositeParameterNode,
			ParameterType parameterType, 
			CompositeParameterNode linkingParameterNode,
			Map<String, BasicParameterWithLinkingContext> inOutParametersDescriptions,
			IExtLanguageManager extLanguageManager) {

		AbstractParameterNode newlinkingParameterNode = 
				currentCompositeParameterNode.getLinkToGlobalParameter();

		if (newlinkingParameterNode == null) {

			calculateParametersDataRecursive(
					currentCompositeParameterNode,
					parameterType, 
					linkingParameterNode,
					inOutParametersDescriptions, 
					extLanguageManager);

			return;
		}

		calculateParametersDataRecursive(
				(IParametersParentNode)newlinkingParameterNode,
				parameterType, 
				currentCompositeParameterNode,
				inOutParametersDescriptions, 
				extLanguageManager);
	}

	private void addBasicParameter(
			BasicParameterNode basicParameterNode,
			ParameterType parameterType,
			CompositeParameterNode parameterNodeWhichHasLink, 
			Map<String, BasicParameterWithLinkingContext> inOutParametersDescriptions,
			IExtLanguageManager extLanguageManager) {

		String signatureNew = 
				AbstractParameterSignatureHelper.createSignatureWithLinkNewStandard(
						parameterNodeWhichHasLink,
						ExtendedName.PATH_TO_TOP_CONTAINTER,
						TypeOfLink.SHORTENED,
						basicParameterNode,
						ExtendedName.PATH_TO_TOP_CONTAINTER_WITHOUT_LINKED_ITEM,
						Decorations.NO,
						TypeIncluded.NO,
						extLanguageManager);
		
		String qualifiedName = signatureNew;

		BasicParameterNode link = (BasicParameterNode) basicParameterNode.getLinkToGlobalParameter();

		if (link == null) {

			if (shouldAddParameter(basicParameterNode, parameterType)) {
				BasicParameterWithLinkingContext parametersData = 
						new BasicParameterWithLinkingContext(basicParameterNode, parameterNodeWhichHasLink);
				inOutParametersDescriptions.put(qualifiedName, parametersData);
			}

			return;
		}

		if (shouldAddParameter(link, parameterType)) {
			BasicParameterWithLinkingContext parametersData = 
					new BasicParameterWithLinkingContext(basicParameterNode, parameterNodeWhichHasLink);
			inOutParametersDescriptions.put(qualifiedName, parametersData);
		}

		return;
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

}
