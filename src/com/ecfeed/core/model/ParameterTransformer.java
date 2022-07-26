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
import java.util.Set;

import com.ecfeed.core.operations.MethodOperationSetConstraints;
import com.ecfeed.core.operations.OperationSimpleAddChoice;
import com.ecfeed.core.operations.OperationSimpleSetLink;
import com.ecfeed.core.operations.OperationSimpleSetTestCases;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.IParameterConversionItemPart;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.ParameterConversionItemPartForChoice;
import com.ecfeed.core.utils.ParameterConversionItemPartForRaw;

public class ParameterTransformer {

	public static MethodNode linkMethodParameteToGlobalParameter(
			MethodParameterNode srcMethodParameterNode,
			GlobalParameterNode dstGlobalParameterNode, 
			ParameterConversionDefinition parameterConversionDefinition,
			ListOfModelOperations outReverseOperations,
			IExtLanguageManager extLanguageManager) {

		checkParametersForNotNull(srcMethodParameterNode, dstGlobalParameterNode);

		MethodOperationSetConstraints reverseOperation = 
				createReverseOperationSetConstraints(srcMethodParameterNode, extLanguageManager);

		outReverseOperations.add(reverseOperation);

		if (parameterConversionDefinition != null) {
			convertByConversionList(
					parameterConversionDefinition, 
					srcMethodParameterNode, 
					dstGlobalParameterNode,
					outReverseOperations,
					extLanguageManager);
		}

		deleteRemainingChoices(srcMethodParameterNode, outReverseOperations, extLanguageManager);

		MethodNode methodNode = srcMethodParameterNode.getMethod();

		//		MethodNodeHelper.updateParameterReferencesInConstraints(
		//				srcMethodParameterNode, 
		//				dstGlobalParameterNode,
		//				methodNode.getConstraintNodes(),
		//				outReverseOperations,
		//				extLanguageManager);


		removeTestCases(methodNode, outReverseOperations, extLanguageManager);

		setLink(srcMethodParameterNode, dstGlobalParameterNode, outReverseOperations, extLanguageManager);

		return methodNode;
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

		ChoicesParentNode choicesParentNode = choiceNode.getParent();
		int indexOfTopChoice = choiceNode.getMyIndex();

		OperationSimpleAddChoice operationSimpleAddChoice = 
				new OperationSimpleAddChoice(choiceNode, indexOfTopChoice, choicesParentNode, extLanguageManager);

		outReverseOperations.add(operationSimpleAddChoice);

		choicesParentNode.removeChoice(choiceNode);
	}

	public static void unlinkMethodParameteFromGlobalParameter(
			MethodParameterNode methodParameterNode,
			GlobalParameterNode globalParameterNode, 
			ListOfModelOperations outReverseOperations,
			IExtLanguageManager extLanguageManager) {

		checkParametersForNotNull(methodParameterNode, globalParameterNode);

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
			MethodParameterNode methodParameterNode,
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
			MethodParameterNode srcMethodParameterNode,
			ListOfModelOperations inOutReverseOperations,
			IExtLanguageManager extLanguageManager) {

		GlobalParameterNode oldGlobalParameterNode = srcMethodParameterNode.getLink();

		srcMethodParameterNode.setLink(null);
		srcMethodParameterNode.setLinked(false);

		OperationSimpleSetLink reverseOperationSimpleSetLink = 
				new OperationSimpleSetLink(
						srcMethodParameterNode, oldGlobalParameterNode, extLanguageManager);

		inOutReverseOperations.add(reverseOperationSimpleSetLink);
	}

	private static void setLink(
			MethodParameterNode srcMethodParameterNode,
			GlobalParameterNode dstParameterForChoices,
			ListOfModelOperations inOutReverseOperations,
			IExtLanguageManager extLanguageManager) {


		srcMethodParameterNode.setLink(dstParameterForChoices);
		srcMethodParameterNode.setLinked(true);

		OperationSimpleSetLink reverseOperationSimpleSetLink = 
				new OperationSimpleSetLink(
						srcMethodParameterNode, null, extLanguageManager);

		inOutReverseOperations.add(reverseOperationSimpleSetLink);
	}

	public static void convertByConversionList(
			ParameterConversionDefinition parameterConversionItems,
			MethodParameterNode srcParameterNode, 
			GlobalParameterNode dstParameterNode,
			ListOfModelOperations inOutReverseOperations,
			IExtLanguageManager extLanguageManager) {

		List<ParameterConversionItem> sortedParameterConversionItems = 
				parameterConversionItems.createSortedCopyOfConversionItems();

		for (ParameterConversionItem parameterConversionItem : sortedParameterConversionItems) {

			convertByConversionItem(
					parameterConversionItem, 
					srcParameterNode, dstParameterNode,
					inOutReverseOperations, extLanguageManager); 
		}
	}

	private static MethodOperationSetConstraints createReverseOperationSetConstraints(
			MethodParameterNode srcParameterNode,
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

	private static void convertByConversionItem(
			ParameterConversionItem parameterConversionItem, 
			MethodParameterNode srcParameterNode, 
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

	public static void verifyConversionOfParameterToType(
			String newType, 
			MethodParameterNode methodParameterNode,
			ParameterConversionDefinition inOutParameterConversionDefinition) {
		
		verifyConversionOfChoices(methodParameterNode, newType, inOutParameterConversionDefinition);

		verifyConversionOfConstraints(methodParameterNode, newType, inOutParameterConversionDefinition);
	}

	private static void verifyConversionOfChoices(
			MethodParameterNode methodParameterNode, 
			String newType, 
			ParameterConversionDefinition inOutParameterConversionDefinition) {

		Set<ChoiceNode> choiceNodes = methodParameterNode.getAllChoices();

		for (ChoiceNode choiceNode : choiceNodes) {

			if (!canConvertChoiceValueFromToType(
					choiceNode.getValueString(), choiceNode.isRandomizedValue(), 
					methodParameterNode.getType(), newType)) {

				addConversionDefinitionItem(choiceNode, inOutParameterConversionDefinition); 
			}
		}
	}

	private static void addConversionDefinitionItem(
			ChoiceNode choiceNode,
			ParameterConversionDefinition inOutParameterConversionDefinition) {

		IParameterConversionItemPart srcPart = 
				new ParameterConversionItemPartForRaw(
						IParameterConversionItemPart.ItemPartType.VALUE.getCode(), 
						choiceNode.getValueString());

		String objectsContainingSrcItem = choiceNode.getName();

		ParameterConversionItem parameterConversionItem = 
				new ParameterConversionItem(srcPart, null, objectsContainingSrcItem);

		inOutParameterConversionDefinition.addItem(parameterConversionItem);
	}

	private static void verifyConversionOfConstraints(
			MethodParameterNode methodParameterNode, 
			String newType,
			ParameterConversionDefinition inOutParameterConversionDefinition) {

		MethodNode methodNode = methodParameterNode.getMethod();

		List<Constraint> constraints = methodNode.getConstraints();

		for (Constraint constraint : constraints) {

			constraint.verifyConversionToNewType(newType, inOutParameterConversionDefinition);
		}
	}


	private static boolean canConvertChoiceValueFromToType(
			String value, boolean isChoiceRandomized, 
			String oldType, String newType) {

		if (oldType.equals(JavaLanguageHelper.TYPE_NAME_BOOLEAN) 
				|| newType.equals(JavaLanguageHelper.TYPE_NAME_BOOLEAN)) {

			return checkConversionForBoolean(oldType, newType);
		}

		ITypeAdapterProvider typeAdapterProvider = new TypeAdapterProviderForJava();

		ITypeAdapter<?> typeAdapter = typeAdapterProvider.getAdapter(newType);

		boolean canConvert = typeAdapter.canCovertWithoutLossOfData(value, isChoiceRandomized);

		return canConvert;
	}

	private static boolean checkConversionForBoolean(String oldType, String newType) {

		if (oldType.equals(JavaLanguageHelper.TYPE_NAME_BOOLEAN)) {

			if (newType.equals(JavaLanguageHelper.TYPE_NAME_BOOLEAN)) {
				return true;
			}

			return false;
		}

		if (newType.equals(JavaLanguageHelper.TYPE_NAME_BOOLEAN)) {

			if (oldType.equals(JavaLanguageHelper.TYPE_NAME_BOOLEAN)) {
				return true;
			}

			return false;
		}

		return false;
	}

}