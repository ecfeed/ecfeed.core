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

import java.util.Set;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class MethodParameterNodeHelper {

	public static ChoiceNode addChoiceToMethodParameter(
			BasicParameterNode methodParameterNode, 
			String choiceNodeName, 
			String valueString) {

		ChoiceNode choiceNode = new ChoiceNode(choiceNodeName, valueString, null);
		methodParameterNode.addChoice(choiceNode);

		return choiceNode;
	}

	public static String getName(BasicParameterNode methodParameterNode, IExtLanguageManager extLanguageManager) {

		return AbstractNodeHelper.getName(methodParameterNode, extLanguageManager);
	}

	public static String createSignature(
			BasicParameterNode methodParameterNode,
			IExtLanguageManager extLanguageManager) {

		String type = AbstractParameterNodeHelper.getType(methodParameterNode, extLanguageManager);
		String name = AbstractParameterNodeHelper.createNameSignature(methodParameterNode, extLanguageManager);

		String signature = 
				AbstractParameterNodeHelper.createSignature(
						type,
						name,
						methodParameterNode.isExpected(),
						extLanguageManager);

		final BasicParameterNode link = methodParameterNode.getLinkToGlobalParameter();

		if (methodParameterNode.isLinked() && link != null) {
			signature += "[LINKED]->" + GlobalParameterNodeHelper.getQualifiedName(link, extLanguageManager);
		}

		return signature;
	}

	public static String createReverseSignature(
			BasicParameterNode methodParameterNode,
			IExtLanguageManager extLanguageManager) {

		String type = AbstractParameterNodeHelper.getType(methodParameterNode, extLanguageManager);
		String name = AbstractParameterNodeHelper.createNameSignature(methodParameterNode, extLanguageManager);

		String signature = 
				AbstractParameterNodeHelper.createReverseSignature(
						type,
						name,
						methodParameterNode.isExpected());

		if (methodParameterNode.isLinked()) {
		
			BasicParameterNode globalParameterNode = methodParameterNode.getLinkToGlobalParameter();
	
			if (globalParameterNode != null) {
				signature += " [LINKED]->" + GlobalParameterNodeHelper.getQualifiedName(globalParameterNode, extLanguageManager);
			}
			
			MethodNode methodNode = methodParameterNode.getLinkToMethod();
			
			if (methodNode != null) {
				signature += "[LINKED]->" + methodNode.getName();
			}
		}

		return signature;
	}

	public static String getType(BasicParameterNode methodParameterNode, IExtLanguageManager extLanguageManager) {

		String type = methodParameterNode.getType();
		type =  extLanguageManager.convertTypeFromIntrToExtLanguage(type);

		return type;
	}

	public static ChoiceNode findChoice(BasicParameterNode methodParameterNode, String choiceQualifiedName) {

		if (!methodParameterNode.isLinked()) {
			return findChoiceIntr(methodParameterNode, choiceQualifiedName);
		}

		BasicParameterNode link = methodParameterNode.getLinkToGlobalParameter();

		if (link == null)  {
			ExceptionHelper.reportRuntimeException("Missing link for linked parameter.");
		}

		return findChoiceIntr(link, choiceQualifiedName);
	}

	private static ChoiceNode findChoiceIntr(AbstractParameterNode link, String choiceQualifiedName) {

		Set<ChoiceNode> choiceNodes = link.getAllChoices();

		for(ChoiceNode choiceNode : choiceNodes) {
			String choiceName = choiceNode.getName();

			if (choiceName.equals(choiceQualifiedName))
				return choiceNode;
		}

		return null;
	}

}
