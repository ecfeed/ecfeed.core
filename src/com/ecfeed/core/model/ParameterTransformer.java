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

import com.ecfeed.core.operations.MethodOperationSetConstraints;
import com.ecfeed.core.operations.OperationSimpleAddChoice;
import com.ecfeed.core.operations.OperationSimpleSetLink;
import com.ecfeed.core.operations.OperationSimpleSetTestCases;
import com.ecfeed.core.operations.SimpleOperationSetMethodParameterType;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.IParameterConversionItemPart;
import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.ParameterConversionItemPart;
import com.ecfeed.core.utils.ParameterConversionItemPartForChoice;
import com.ecfeed.core.utils.ParameterConversionItemPartForValue;

public class ParameterTransformer {

	public static MethodNode linkMethodParameteToGlobalParameter(
			BasicParameterNode srcMethodParameterNode,
			GlobalParameterNode dstGlobalParameterNode, 
			ParameterConversionDefinition parameterConversionDefinition,
			ListOfModelOperations outReverseOperations,
			IExtLanguageManager extLanguageManager) {

		checkParametersForNotNull(srcMethodParameterNode, dstGlobalParameterNode);

		String oldMethodParameterType = srcMethodParameterNode.getType();
		String globalParameterType = dstGlobalParameterNode.getType();
		
		MethodOperationSetConstraints reverseOperation = 
				createReverseOperationSetConstraints(srcMethodParameterNode, extLanguageManager);

		outReverseOperations.add(reverseOperation);

		if (parameterConversionDefinition != null) {
			convertByConversionListForLinking(
					parameterConversionDefinition, 
					srcMethodParameterNode, 
					dstGlobalParameterNode,
					outReverseOperations,
					extLanguageManager);
		}

		deleteRemainingChoices(srcMethodParameterNode, outReverseOperations, extLanguageManager);

		MethodNode methodNode = srcMethodParameterNode.getMethod();

		removeTestCases(methodNode, outReverseOperations, extLanguageManager);

		setLink(srcMethodParameterNode, dstGlobalParameterNode, outReverseOperations, extLanguageManager);

		SimpleOperationSetMethodParameterType reverseSetTypeOperation = 
			new SimpleOperationSetMethodParameterType(
					srcMethodParameterNode, 
					oldMethodParameterType, 
					extLanguageManager);

		outReverseOperations.add(reverseSetTypeOperation);

		
		srcMethodParameterNode.setType(globalParameterType);
		
		return methodNode;
	}

	public static void unlinkMethodParameteFromGlobalParameter(
			BasicParameterNode methodParameterNode,
			GlobalParameterNode globalParameterNode, 
			ListOfModelOperations outReverseOperations,
			IExtLanguageManager extLanguageManager) {

		checkParametersForNotNull(methodParameterNode, globalParameterNode);

		String linkedParameterType = methodParameterNode.getLinkToGlobalParameter().getType();
		String oldMethodParameterType = methodParameterNode.getType();

		MethodNode methodNode = methodParameterNode.getMethod();

		removeLinkOnMethodParameter(methodParameterNode, outReverseOperations, extLanguageManager);

		ListOfModelOperations reverseOperationsForChoicesCopy = new ListOfModelOperations();

		List<ParameterConversionItem> parameterConversionItems = new ArrayList<>();

		ChoicesParentNodeHelper.createCopyOfChoicesSubTreesBetweenParameters(
				globalParameterNode, methodParameterNode, 
				reverseOperationsForChoicesCopy,
				parameterConversionItems,
				extLanguageManager);

		convertConstraints(
				methodNode, 
				globalParameterNode, methodParameterNode, 
				parameterConversionItems, outReverseOperations, 
				extLanguageManager);

		outReverseOperations.addAll(reverseOperationsForChoicesCopy);

		removeTestCases(methodNode, outReverseOperations, extLanguageManager);

		SimpleOperationSetMethodParameterType reverseSetTypeOperation = 
				new SimpleOperationSetMethodParameterType(
						methodParameterNode, 
						oldMethodParameterType, 
						extLanguageManager);
		
		outReverseOperations.add(reverseSetTypeOperation);
		
		methodParameterNode.setType(linkedParameterType);
	}

	private static void setLink(
			BasicParameterNode srcMethodParameterNode,
			GlobalParameterNode dstParameterForChoices,
			ListOfModelOperations inOutReverseOperations,
			IExtLanguageManager extLanguageManager) {


		srcMethodParameterNode.setLinkToGlobalParameter(dstParameterForChoices);
		srcMethodParameterNode.setLinked(true);

		OperationSimpleSetLink reverseOperationSimpleSetLink = 
				new OperationSimpleSetLink(
						srcMethodParameterNode, null, extLanguageManager);

		inOutReverseOperations.add(reverseOperationSimpleSetLink);
	}

	public static void convertByConversionListForLinking(
			ParameterConversionDefinition parameterConversionItems,
			BasicParameterNode srcParameterNode, 
			GlobalParameterNode dstParameterNode,
			ListOfModelOperations inOutReverseOperations,
			IExtLanguageManager extLanguageManager) {

		List<ParameterConversionItem> sortedParameterConversionItems = 
				parameterConversionItems.createSortedCopyOfConversionItems();

		for (ParameterConversionItem parameterConversionItem : sortedParameterConversionItems) {

			convertByConversionItemForLinking(
					parameterConversionItem, 
					srcParameterNode, dstParameterNode,
					inOutReverseOperations, extLanguageManager); 
		}
	}

	public static void verifyConversionOfParameterToType(
			String newType, 
			AbstractParameterNode abstractParameterNode,
			ParameterConversionDefinition inOutParameterConversionDefinition) {

		if (abstractParameterNode instanceof GlobalParameterNode) {

			GlobalParameterNode globalParameterNode = (GlobalParameterNode)abstractParameterNode;

			ChoiceNodeHelper.verifyConversionOfChoices(globalParameterNode, newType, inOutParameterConversionDefinition);
			return;
		}

		BasicParameterNode methodParameterNode = (BasicParameterNode)abstractParameterNode;

		if (methodParameterNode.isExpected()) {
			addDefaultValueToConversionDefinition(
					methodParameterNode.getDefaultValue(), inOutParameterConversionDefinition);
		}

		ChoiceNodeHelper.verifyConversionOfChoices(
				methodParameterNode, newType, inOutParameterConversionDefinition);

		ConstraintHelper.verifyConversionOfConstraints(
				methodParameterNode, newType, inOutParameterConversionDefinition);
	}

	public static void convertChoicesAndConstraintsToType(
			BasicParameterNode methodParameterNode,
			ParameterConversionDefinition parameterConversionDefinition) {

		convertChoicesToType(methodParameterNode, parameterConversionDefinition);

		ConstraintHelper.convertValuesOfConstraintsToType(methodParameterNode, parameterConversionDefinition);
	}

	public static void convertChoicesToType(
			AbstractParameterNode abstractParameterNode,
			ParameterConversionDefinition parameterConversionDefinition) {

		ChoiceNodeHelper.convertValuesOfChoicesToType(abstractParameterNode, parameterConversionDefinition);
	}

	public static boolean isValueCompatibleWithType(
			String value, 
			String newType, 
			boolean isChoiceRandomized) {

		ITypeAdapterProvider typeAdapterProvider = new TypeAdapterProviderForJava();

		ITypeAdapter<?> typeAdapter = typeAdapterProvider.getAdapter(newType);

		boolean isCompatible = typeAdapter.isValueCompatibleWithType(value, isChoiceRandomized);

		return isCompatible;
	}

	private static void deleteRemainingChoices(
			ChoicesParentNode srcMethodParameterNode,
			ListOfModelOperations outReverseOperations, 
			IExtLanguageManager extLanguageManager) {

		List<ChoiceNode> choices = new ArrayList<>(srcMethodParameterNode.getChoices());

		for (ChoiceNode choiceNode : choices) {
			deleteChoiceWithChildrenRecursive(choiceNode, outReverseOperations, extLanguageManager);
		}
	}

	private static void deleteChoiceWithChildrenRecursive(
			ChoiceNode mainChoiceNode,
			ListOfModelOperations outReverseOperations, 
			IExtLanguageManager extLanguageManager) {

		List<ChoiceNode> choices = new ArrayList<>(mainChoiceNode.getChoices());

		for (ChoiceNode choiceNode : choices) {
			deleteChoiceWithChildrenRecursive(choiceNode, outReverseOperations, extLanguageManager);
		}

		deleteChoice(mainChoiceNode, outReverseOperations, extLanguageManager);
		return;

	}

	private static void deleteChoice(
			ChoiceNode choiceNode, 
			ListOfModelOperations outReverseOperations,
			IExtLanguageManager extLanguageManager) {

		IChoicesParentNode choicesParentNode = (IChoicesParentNode)choiceNode.getParent();
		int indexOfTopChoice = choiceNode.getMyIndex();

		OperationSimpleAddChoice operationSimpleAddChoice = 
				new OperationSimpleAddChoice(choiceNode, indexOfTopChoice, choicesParentNode, extLanguageManager);

		outReverseOperations.add(operationSimpleAddChoice);

		choicesParentNode.removeChoice(choiceNode);
	}

	private static void convertConstraints(
			MethodNode methodNode, 
			AbstractParameterNode srcParameterNode,
			AbstractParameterNode dstParameterNode, 
			List<ParameterConversionItem> parameterConversionItems,
			ListOfModelOperations outReverseOperations, 
			IExtLanguageManager extLanguageManager) {

		for (ParameterConversionItem parameterConversionItem : parameterConversionItems) {

			MethodNodeHelper.convertConstraints(
					methodNode.getConstraintNodes(),
					parameterConversionItem);
		}
	}

	private static void checkParametersForNotNull(
			BasicParameterNode methodParameterNode,
			GlobalParameterNode dstGlobalParameterNode) {

		if (methodParameterNode == null) {
			ExceptionHelper.reportRuntimeException("Empty method parameter.");
		}

		if (dstGlobalParameterNode == null) {
			ExceptionHelper.reportRuntimeException("Empty global parameter.");
		}
	}

	private static void removeTestCases(MethodNode methodNode, ListOfModelOperations reverseOperations,
			IExtLanguageManager extLanguageManager) {

		OperationSimpleSetTestCases inOutReverseOperation = 
				new OperationSimpleSetTestCases(methodNode, methodNode.getTestCases(), extLanguageManager);

		reverseOperations.add(inOutReverseOperation);

		methodNode.removeAllTestCases();
	}

	private static void removeLinkOnMethodParameter(
			BasicParameterNode srcMethodParameterNode,
			ListOfModelOperations inOutReverseOperations,
			IExtLanguageManager extLanguageManager) {

		GlobalParameterNode oldGlobalParameterNode = srcMethodParameterNode.getLinkToGlobalParameter();

		srcMethodParameterNode.setLinkToGlobalParameter(null);
		srcMethodParameterNode.setLinked(false);

		OperationSimpleSetLink reverseOperationSimpleSetLink = 
				new OperationSimpleSetLink(
						srcMethodParameterNode, oldGlobalParameterNode, extLanguageManager);

		inOutReverseOperations.add(reverseOperationSimpleSetLink);
	}


	private static MethodOperationSetConstraints createReverseOperationSetConstraints(
			BasicParameterNode srcParameterNode,
			IExtLanguageManager extLanguageManager) {

		MethodNode methodNode = srcParameterNode.getMethod();

		List<ConstraintNode> constraintNodes = methodNode.getConstraintNodes();

		List<ConstraintNode> listOfClonedConstraintNodes = new ArrayList<>();

		for (ConstraintNode constraintNode : constraintNodes) {

			ConstraintNode clone = constraintNode.makeClone();
			listOfClonedConstraintNodes.add(clone);
		}

		MethodOperationSetConstraints reverseOperation = 
				new MethodOperationSetConstraints(methodNode, listOfClonedConstraintNodes, extLanguageManager);

		return reverseOperation;
	}

	private static void convertByConversionItemForLinking(
			ParameterConversionItem parameterConversionItem, 
			BasicParameterNode srcParameterNode, 
			GlobalParameterNode dstParameterNode,
			ListOfModelOperations inOutReverseOperations, 
			IExtLanguageManager extLanguageManager) {

		MethodNode methodNode = srcParameterNode.getMethod();

		MethodNodeHelper.convertConstraints(
				methodNode.getConstraintNodes(),
				parameterConversionItem); 

		IParameterConversionItemPart srcPart = parameterConversionItem.getSrcPart();

		if (srcPart instanceof ParameterConversionItemPartForChoice) {

			ParameterConversionItemPartForChoice parameterConversionItemPartForChoice = 
					(ParameterConversionItemPartForChoice) srcPart;

			removeSourceChoice(
					parameterConversionItemPartForChoice.getChoiceNode(), 
					inOutReverseOperations, 
					extLanguageManager);
		}
	}

	private static void removeSourceChoice(
			ChoiceNode srcChoiceNode, 
			ListOfModelOperations inOutReverseOperations,
			IExtLanguageManager extLanguageManager) {

		deleteChoice(srcChoiceNode, inOutReverseOperations, extLanguageManager);
	}

	private static void addDefaultValueToConversionDefinition(
			String defaultValue,
			ParameterConversionDefinition inOutParameterConversionDefinition) {

		ParameterConversionItemPart srcPart = new ParameterConversionItemPartForValue(defaultValue);

		boolean isRandomized = false;

		ParameterConversionItem parameterConversionItem = 
				new ParameterConversionItem(srcPart, null, isRandomized, "default value");

		inOutParameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItem);
	}

}