/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.SignatureHelper;
import com.ecfeed.core.utils.StringHelper;

public class BasicParameterNodeHelper {
	
	public static void compareParameters(
			BasicParameterNode basicParameterNode1, 
			BasicParameterNode basicParameterNode2) {
		
		if (basicParameterNode1.isExpected() != basicParameterNode2.isExpected()) {
			ExceptionHelper.reportRuntimeException("Expected property does not match.");
		}
		
		ModelCompareHelper.compareTypes(basicParameterNode1.getType(), basicParameterNode1.getType());
		ModelCompareHelper.compareSizes(basicParameterNode1.getChoices(), basicParameterNode2.getChoices());
		for(int i = 0; i < basicParameterNode1.getChoices().size(); ++i){
			ModelCompareHelper.compareChoices(basicParameterNode1.getChoices().get(i), basicParameterNode2.getChoices().get(i));
		}
		
	}
	
	public static boolean propertiesOfBasicParametrsMatch(
			List<BasicParameterNode> parameters1, List<BasicParameterNode> parameters2) {
		
		if (parameters1.size() != parameters2.size()) {
			return false;
		}
		
		ListIterator<BasicParameterNode> iterator1 = parameters1.listIterator();
		ListIterator<BasicParameterNode> iterator2 = parameters2.listIterator();

		for(;;) {
			
			boolean hasNext1 = iterator1.hasNext();
			boolean hasNext2 = iterator2.hasNext();

			if (!hasNext1) {
				return true;
			}
			
			if (hasNext1 != hasNext2) {
				return false;
			}
				
			BasicParameterNode basicParameterNode1 = iterator1.next();
			BasicParameterNode basicParameterNode2 = iterator2.next();
			
			if (!basicParameterNode1.propertiesMatch(basicParameterNode2)) {
				return false;
			}
		}
	}
	
	public static List<BasicParameterNode> convertAbstractListToBasicList(List<AbstractParameterNode> abstractParameterNodes) {
		
		List<BasicParameterNode> result = new ArrayList<>(); 
		
		for(AbstractParameterNode abstractParameterNode : abstractParameterNodes) {
			
			if (!(abstractParameterNode instanceof BasicParameterNode)) {
				ExceptionHelper.reportRuntimeException("Cannot convert abstract parameters to basic parameters.");
			}
			
			result.add((BasicParameterNode) abstractParameterNode);
		}
		
		return result;
	}
	
	public static List<BasicParameterNode> getBasicParametersFromTheWholeMethodTree(
			IAbstractNode anyNodeFromMethodTree) {
		
		MethodNode methodNode = MethodNodeHelper.findMethodNode(anyNodeFromMethodTree);
		
		if (methodNode == null) {
			ExceptionHelper.reportRuntimeException("Cannot find method node.");
		}
		
		List<BasicParameterNode> result = new ArrayList<BasicParameterNode>();
		
		accumulateBasicParametersRecursively(methodNode, result);
		
		return result;
	}
	
	private static void accumulateBasicParametersRecursively(
			IParametersParentNode parametersParentNode,
			List<BasicParameterNode> inOutResult) {
		
		List<AbstractParameterNode> abstractParameters = parametersParentNode.getParameters();
		
		for (AbstractParameterNode abstractParameterNode : abstractParameters) {
			
			if (abstractParameterNode instanceof CompositeParameterNode) {
				accumulateBasicParametersRecursively((IParametersParentNode)abstractParameterNode, inOutResult);
			} else {
				inOutResult.add((BasicParameterNode) abstractParameterNode);
			}
		}
	}
	
	public static BasicParameterNode findBasicParameterByQualifiedName(
			String parameterNameToFindInExtLanguage, 
			IParametersParentNode parametersParentNode,
			IExtLanguageManager extLanguageManager) {
		
		String parameterNameToFindInIntrLanguage = 
				extLanguageManager.convertTextFromExtToIntrLanguage(parameterNameToFindInExtLanguage);
		
		return findParameterByQualifiedNameRecursive(
				parameterNameToFindInIntrLanguage, parametersParentNode,	extLanguageManager);
	}

	private static BasicParameterNode findParameterByQualifiedNameRecursive(
			String parameterNameToFindInIntrLanguage,
			IParametersParentNode parametersParentNode, 
			IExtLanguageManager extLanguageManager) {
		
		String firstToken = 
				StringHelper.getFirstToken(parameterNameToFindInIntrLanguage, SignatureHelper.SIGNATURE_NAME_SEPARATOR);
		
		if (firstToken == null) {
			AbstractParameterNode abstractParameterNode = 
					parametersParentNode.findParameter(parameterNameToFindInIntrLanguage);
			
			return (BasicParameterNode) abstractParameterNode;
		}
		
		String remainingPart = 
				StringHelper.removeToPrefix(SignatureHelper.SIGNATURE_NAME_SEPARATOR, parameterNameToFindInIntrLanguage);
		
		CompositeParameterNode compositeParameterNode = 
				(CompositeParameterNode) parametersParentNode.findParameter(firstToken);
		
		return findParameterByQualifiedNameRecursive(remainingPart, compositeParameterNode, extLanguageManager);
	}
	
}
