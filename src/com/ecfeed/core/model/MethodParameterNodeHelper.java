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

	public static ChoiceNode addNewChoice(
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

	//	// OBSOLETE 
	//	public static String createSignature(
	//			BasicParameterNode methodParameterNode,
	//			IExtLanguageManager extLanguageManager) {
	//
	//		IAbstractNode parent = methodParameterNode.getParent();
	//
	//		String parentCompositeParameterSignature = "";
	//
	//		if (parent instanceof CompositeParameterNode) {
	//
	//			CompositeParameterNode compositeParameterNode = (CompositeParameterNode) parent;
	//
	//			parentCompositeParameterSignature = compositeParameterNode.getName() + SignatureHelper.SIGNATURE_NAME_SEPARATOR;
	//		}
	//
	//		String type = AbstractParameterSignatureHelper.createSignatureOfType(methodParameterNode, extLanguageManager);
	//
	//		//		String name = AbstractParameterSignatureHelper.createSignatureOfParameterName(methodParameterNode, extLanguageManager);
	//		String name = 
	//				AbstractParameterSignatureHelper.createSignatureOfParameterNewStandard(
	//						methodParameterNode,
	//						ExtendedName.NAME_ONLY,	Decorations.NO, TypeIncluded.NO,
	//						extLanguageManager);
	//
	//		String signature = 
	//				AbstractParameterSignatureHelper.createSignature(
	//						type,
	//						parentCompositeParameterSignature + name,
	//						methodParameterNode.isExpected(),
	//						extLanguageManager);
	//
	//		final AbstractParameterNode link = methodParameterNode.getLinkToGlobalParameter();
	//
	//		if (methodParameterNode.isLinked() && link != null) {
	//			signature += "[LINKED]->" + AbstractParameterSignatureHelper.createPathToTopContainerNewStandard(
	//					link, extLanguageManager);
	//		}
	//
	//		return signature;
	//	}



	public static String getType(BasicParameterNode methodParameterNode, IExtLanguageManager extLanguageManager) {

		String type = methodParameterNode.getType();
		type =  extLanguageManager.convertTypeFromIntrToExtLanguage(type);

		return type;
	}

	public static ChoiceNode findChoice(BasicParameterNode methodParameterNode, String choiceQualifiedName) {

		if (!methodParameterNode.isLinked()) {
			return findChoiceIntr(methodParameterNode, choiceQualifiedName);
		}

		AbstractParameterNode abstractLink = methodParameterNode.getLinkToGlobalParameter();

		if (abstractLink == null)  {
			ExceptionHelper.reportRuntimeException("Missing link for linked parameter.");
		}

		if (abstractLink instanceof BasicParameterNode) {

			BasicParameterNode basicParameterLink = (BasicParameterNode) abstractLink;
			return findChoiceIntr(basicParameterLink, choiceQualifiedName);
		} else {

			return null;
		}
	}

	private static ChoiceNode findChoiceIntr(BasicParameterNode link, String choiceQualifiedName) {

		Set<ChoiceNode> choiceNodes = link.getAllChoices();

		for(ChoiceNode choiceNode : choiceNodes) {
			String choiceName = choiceNode.getName();

			if (choiceName.equals(choiceQualifiedName))
				return choiceNode;
		}

		return null;
	}

}
