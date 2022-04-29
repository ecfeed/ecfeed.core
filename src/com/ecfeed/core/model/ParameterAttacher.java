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

import java.util.List;

import com.ecfeed.core.utils.ChoiceConversionItem;
import com.ecfeed.core.utils.ExceptionHelper;

public class ParameterAttacher {

	public static void attach(MethodParameterNode methodParameterNode, GlobalParameterNode globalParameterNode,
			List<ChoiceConversionItem> choiceConversionItems) {
		// TODO Auto-generated method stub
		
//		if (methodParameterNode == null) {
//			ExceptionHelper.reportRuntimeException("Empty method parameter.");
//		}
//
//		if (methodParameterNode == null) {
//			ExceptionHelper.reportRuntimeException("Empty global parameter.");
//		}
//		
//		if (choiceConversionItems != null) {
//			moveChoicesByConversionList(
//					choiceConversionItems, 
//					detachedParameterNode, 
//					destinationParameterNode,
//					methodNode);
//		}
//
//		moveRemainingTopChoices(detachedParameterNode, destinationParameterNode);
//
//		methodNode.updateParameterReferencesInConstraints(
//				(MethodParameterNode)detachedParameterNode, 
//				(MethodParameterNode)destinationParameterNode);
//
//
//		fDetachedParameters.remove(detachedParameterNode);
//	}
//
//	private void moveChoicesByConversionList(
//			List<ChoiceConversionItem> choiceConversionItems,
//			MethodParameterNode srcParameterNode, 
//			MethodParameterNode dstParameterNode,
//			MethodNode methodNode) {
//		
//		List<ChoiceConversionItem> sortedChoiceConversionItems = 
//				createSortedCopyOfConversionItems(choiceConversionItems);
//		
//		for (ChoiceConversionItem choiceConversionItem : sortedChoiceConversionItems) {
//
//			ChoiceNode srcChoiceNode = srcParameterNode.getChoice(choiceConversionItem.getSrcName());
//
//			if (srcChoiceNode == null) {
//				ExceptionHelper.reportRuntimeException("Cannot find source choice.");
//			}
//
//			ChoiceNode dstChoiceNode = dstParameterNode.getChoice(choiceConversionItem.getDstName());
//
//			if (dstChoiceNode == null) {
//				ExceptionHelper.reportRuntimeException("Cannot find destination choice.");
//			}
//
//			moveChildChoices(srcChoiceNode, dstChoiceNode);
//
//			methodNode.updateChoiceReferencesInTestCases(srcChoiceNode, dstChoiceNode);
//			methodNode.updateChoiceReferencesInConstraints(srcChoiceNode, dstChoiceNode);
//
//			// remove source choice
//
//			ChoicesParentNode choicesParentNode = srcChoiceNode.getParent();
//			choicesParentNode.removeChoice(srcChoiceNode);
//		}
	}


}