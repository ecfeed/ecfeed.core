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
import java.util.Optional;

import com.ecfeed.core.operations.link.OperationSimpleSetLink;
import com.ecfeed.core.operations.nodes.OnChoiceOperationAddSimple;
import com.ecfeed.core.operations.nodes.OnConstraintsOperationSetOnMethod;
import com.ecfeed.core.operations.nodes.OnMethodParameterOperationSimpleSetType;
import com.ecfeed.core.operations.nodes.OnTestCasesOperationSimpleSet;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.IParameterConversionItemPart;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.ParameterConversionItemPart;
import com.ecfeed.core.utils.ParameterConversionItemPartForChoice;
import com.ecfeed.core.utils.ParameterConversionItemPartForValue;

public class ParameterTransformer {

	public static MethodNode linkLocalParameteToGlobalParameter(
			AbstractParameterNode localParameterNode,
			AbstractParameterNode globalParameterNode, 
			ParameterConversionDefinition parameterConversionDefinition,
			ListOfModelOperations outReverseOperations,
			Optional<NodeMapper> nodeMapper,
			IExtLanguageManager extLanguageManager) {

		checkParameters(localParameterNode, globalParameterNode, parameterConversionDefinition);

		createReverseOperationsForConstraints(
				parameterConversionDefinition, 
				nodeMapper, 
				extLanguageManager,
				outReverseOperations);

		convertByConversionListForLinking(
				parameterConversionDefinition, 
				outReverseOperations,
				extLanguageManager);

		deleteRemainingChoices(parameterConversionDefinition, extLanguageManager, outReverseOperations);

		IAbstractNode parent = localParameterNode.getParent();
		IParametersParentNode methodNode = (IParametersParentNode) parent;

		if (parent instanceof ITestCasesParentNode) {

			removeTestCases((ITestCasesParentNode)methodNode, outReverseOperations, extLanguageManager);
		}

		setLink(localParameterNode, globalParameterNode, outReverseOperations, extLanguageManager);

		if (localParameterNode instanceof BasicParameterNode 
				&& globalParameterNode instanceof BasicParameterNode) {

			setTypeOfLocalParameter(localParameterNode, globalParameterNode, outReverseOperations, extLanguageManager);
		}

		return (MethodNode) parent;
	}

	private static void checkParameters(
			AbstractParameterNode localParameterNode,
			AbstractParameterNode globalParameterNode, 
			ParameterConversionDefinition parameterConversionDefinition) {

		if (parameterConversionDefinition == null) {
			ExceptionHelper.reportRuntimeException("Missing parameter conversion definition");
		}

		checkParametersForNotNull(localParameterNode, globalParameterNode);

		if (localParameterNode instanceof BasicParameterNode 
				&& !(globalParameterNode instanceof BasicParameterNode)) {
			ExceptionHelper.reportRuntimeException("Type mismatch for local and global parameter");
		}

		if (localParameterNode instanceof CompositeParameterNode 
				&& !(globalParameterNode instanceof CompositeParameterNode)) {
			ExceptionHelper.reportRuntimeException("Type mismatch for local and global parameter");
		}
	}

	private static void setTypeOfLocalParameter(
			AbstractParameterNode localParameterNode, 
			AbstractParameterNode globalParameterNode,
			ListOfModelOperations outReverseOperations, 
			IExtLanguageManager extLanguageManager) {

		BasicParameterNode localBasicParameterNode = (BasicParameterNode) localParameterNode;
		BasicParameterNode globalBasicParameterNode = (BasicParameterNode) globalParameterNode;

		String oldMethodParameterType = localBasicParameterNode.getType();
		String globalParameterType = globalBasicParameterNode.getType();

		OnMethodParameterOperationSimpleSetType reverseSetTypeOperation = 
				new OnMethodParameterOperationSimpleSetType(
						localBasicParameterNode, 
						oldMethodParameterType, 
						extLanguageManager);

		outReverseOperations.add(reverseSetTypeOperation);

		localBasicParameterNode.setType(globalParameterType);
	}

	private static void deleteRemainingChoices(
			ParameterConversionDefinition parameterConversionDefinition,
			IExtLanguageManager extLanguageManager, 
			ListOfModelOperations outReverseOperations) {

		List<BasicParameterNode> localBasicParameterNodes = 
				parameterConversionDefinition.createListOfUniqueSourceLocalParameters();

		for (BasicParameterNode localBasicParameterNode : localBasicParameterNodes) {
			deleteRemainingChoicesForBasicParameter(localBasicParameterNode, outReverseOperations, extLanguageManager);
		}
	}

	private static void createReverseOperationsForConstraints(
			ParameterConversionDefinition parameterConversionDefinition,
			Optional<NodeMapper> nodeMapper,
			IExtLanguageManager extLanguageManager,
			ListOfModelOperations outReverseOperations) {

		List<BasicParameterNode> localBasicParameterNodes = 
				parameterConversionDefinition.createListOfUniqueSourceLocalParameters();

		for (BasicParameterNode localBasicParameterNode : localBasicParameterNodes) {
			OnConstraintsOperationSetOnMethod reverseOperation = 
					createReverseOperationSetConstraints(localBasicParameterNode, nodeMapper, extLanguageManager);

			outReverseOperations.add(reverseOperation);
		}
	}


	public static void unlinkMethodParameteFromGlobalParameter(
			AbstractParameterNode methodParameterNode,
			AbstractParameterNode globalParameterNode, 
			ListOfModelOperations outReverseOperations,
			IExtLanguageManager extLanguageManager) {

		if (methodParameterNode instanceof BasicParameterNode) {
			unlinkMethodParameteFromGlobalParameter(
					(BasicParameterNode) methodParameterNode, globalParameterNode,
					outReverseOperations, extLanguageManager);
		} else if (methodParameterNode instanceof CompositeParameterNode) {
			unlinkMethodParameteFromGlobalParameter(
					(CompositeParameterNode) methodParameterNode, globalParameterNode,
					outReverseOperations, extLanguageManager);
		}
	}

	public static void unlinkMethodParameteFromGlobalParameter(
			BasicParameterNode methodParameterNode,
			AbstractParameterNode globalParameterNode, 
			ListOfModelOperations outReverseOperations,
			IExtLanguageManager extLanguageManager) {

		checkParametersForNotNull(methodParameterNode, globalParameterNode);

		BasicParameterNode link = (BasicParameterNode) methodParameterNode.getLinkToGlobalParameter();
		String linkedParameterType = link.getType();

		String oldMethodParameterType = methodParameterNode.getType();

		IAbstractNode parent = methodParameterNode.getParent();

		IParametersAndConstraintsParentNode methodNode = 
				(IParametersAndConstraintsParentNode) parent;

		removeLinkOnMethodParameter(methodParameterNode, outReverseOperations, extLanguageManager);

		ListOfModelOperations reverseOperations = new ListOfModelOperations();

		BasicParameterNode global2 = (BasicParameterNode) globalParameterNode; 

		List<ParameterConversionItem> parameterConversionItems = new ArrayList<>();
		
		ChoicesParentNodeHelper.createCopyOfChoicesAndConversionList(
				methodParameterNode,
				global2, methodParameterNode, 
				reverseOperations,
				parameterConversionItems,
				extLanguageManager);
		
		convertConstraints(
				methodNode, 
				globalParameterNode, methodParameterNode, 
				parameterConversionItems, outReverseOperations, 
				extLanguageManager);

		outReverseOperations.addAll(reverseOperations);

		if (parent instanceof ITestCasesParentNode) {
			removeTestCases((ITestCasesParentNode)parent, outReverseOperations, extLanguageManager);
		}

		OnMethodParameterOperationSimpleSetType reverseSetTypeOperation = 
				new OnMethodParameterOperationSimpleSetType(
						methodParameterNode, 
						oldMethodParameterType, 
						extLanguageManager);

		outReverseOperations.add(reverseSetTypeOperation);

		methodParameterNode.setType(linkedParameterType);
	}

	public static void unlinkMethodParameteFromGlobalParameter(
			CompositeParameterNode methodParameterNode,
			AbstractParameterNode globalParameterNode, 
			ListOfModelOperations outReverseOperations,
			IExtLanguageManager extLanguageManager) {

		checkParametersForNotNull(methodParameterNode, globalParameterNode);

		removeLinkOnMethodParameter(methodParameterNode, outReverseOperations, extLanguageManager);
	}

	private static void setLink(
			AbstractParameterNode srcMethodParameterNode,
			AbstractParameterNode dstParameterForChoices,
			ListOfModelOperations inOutReverseOperations,
			IExtLanguageManager extLanguageManager) {


		srcMethodParameterNode.setLinkToGlobalParameter(dstParameterForChoices);

		OperationSimpleSetLink reverseOperationSimpleSetLink = 
				new OperationSimpleSetLink(
						srcMethodParameterNode, null, extLanguageManager);

		inOutReverseOperations.add(reverseOperationSimpleSetLink);
	}

	public static void convertByConversionListForLinking(
			ParameterConversionDefinition parameterConversionItems,
			//BasicParameterNode srcParameterNode, 
			//BasicParameterNode dstParameterNode,
			ListOfModelOperations inOutReverseOperations,
			IExtLanguageManager extLanguageManager) {

		List<ParameterConversionItem> sortedParameterConversionItems = 
				parameterConversionItems.createSortedCopyOfConversionItems();

		for (ParameterConversionItem parameterConversionItem : sortedParameterConversionItems) {

			convertByConversionItemForLinking(
					parameterConversionItem, 
					inOutReverseOperations, 
					extLanguageManager); 
		}
	}

	// XYX not part of parameter transformer ??
	public static void verifyConversionOfParameterToType(
			String newType, 
			BasicParameterNode abstractParameterNode,
			ParameterConversionDefinition inOutParameterConversionDefinition) {

		if (abstractParameterNode instanceof BasicParameterNode && abstractParameterNode.isGlobalParameter()) {

			BasicParameterNode globalParameterNode = (BasicParameterNode)abstractParameterNode;

			ChoiceNodeHelper.verifyConversionOfChoices(globalParameterNode, newType, inOutParameterConversionDefinition);
			return;
		}

		BasicParameterNode basicParameterNode = (BasicParameterNode)abstractParameterNode;

		if (basicParameterNode.isExpected()) {
			addDefaultValueToConversionDefinition(
					basicParameterNode, basicParameterNode.getDefaultValue(), inOutParameterConversionDefinition);
		}

		ChoiceNodeHelper.verifyConversionOfChoices(
				basicParameterNode, newType, inOutParameterConversionDefinition);

		ConstraintHelper.verifyConversionOfConstraints(
				basicParameterNode, newType, inOutParameterConversionDefinition);
	}

	public static void convertChoicesAndConstraintsToType(
			BasicParameterNode methodParameterNode,
			ParameterConversionDefinition parameterConversionDefinition) {

		convertChoicesToType(methodParameterNode, parameterConversionDefinition);

		ConstraintHelper.convertValuesOfConstraintsToType(methodParameterNode, parameterConversionDefinition);
	}

	public static void convertChoicesToType(
			BasicParameterNode abstractParameterNode,
			ParameterConversionDefinition parameterConversionDefinition) {

		ChoiceNodeHelper.convertValuesOfChoicesToType(abstractParameterNode, parameterConversionDefinition);
	}

	public static boolean isValueCompatibleWithType(
			String value, 
			String newType, 
			boolean isChoiceRandomized) {

		ITypeAdapter<?> typeAdapter = JavaLanguageHelper.getTypeAdapter(newType);

		boolean isCompatible = typeAdapter.isValueCompatibleWithType(value, isChoiceRandomized);

		return isCompatible;
	}

	private static void deleteRemainingChoicesForBasicParameter(
			IChoicesParentNode srcMethodParameterNode,
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

		OnChoiceOperationAddSimple operationSimpleAddChoice = 
				new OnChoiceOperationAddSimple(choiceNode, indexOfTopChoice, choicesParentNode, extLanguageManager);

		outReverseOperations.add(operationSimpleAddChoice);

		choicesParentNode.removeChoice(choiceNode);
	}

	private static void convertConstraints(
			IConstraintsParentNode methodNode, 
			AbstractParameterNode srcParameterNode,
			BasicParameterNode dstParameterNode, 
			List<ParameterConversionItem> parameterConversionItems,
			ListOfModelOperations outReverseOperations, 
			IExtLanguageManager extLanguageManager) {

		for (ParameterConversionItem parameterConversionItem : parameterConversionItems) {

			convertOneItemForOneConstraintsParent(parameterConversionItem, methodNode);
		}
	}

	private static void checkParametersForNotNull(
			AbstractParameterNode methodParameterNode,
			AbstractParameterNode dstGlobalParameterNode) {

		if (methodParameterNode == null) {
			ExceptionHelper.reportRuntimeException("Empty method parameter.");
		}

		if (dstGlobalParameterNode == null) {
			ExceptionHelper.reportRuntimeException("Empty global parameter.");
		}
	}

	private static void removeTestCases(
			ITestCasesParentNode methodNode, 
			ListOfModelOperations reverseOperations,
			IExtLanguageManager extLanguageManager) {

		OnTestCasesOperationSimpleSet inOutReverseOperation = 
				new OnTestCasesOperationSimpleSet(methodNode, methodNode.getTestCases(), extLanguageManager);

		reverseOperations.add(inOutReverseOperation);

		methodNode.removeAllTestCases();
	}

	private static void removeLinkOnMethodParameter(
			AbstractParameterNode srcMethodParameterNode,
			ListOfModelOperations inOutReverseOperations,
			IExtLanguageManager extLanguageManager) {

		AbstractParameterNode oldGlobalParameterNode = srcMethodParameterNode.getLinkToGlobalParameter();

		srcMethodParameterNode.setLinkToGlobalParameter(null);

		OperationSimpleSetLink reverseOperationSimpleSetLink = 
				new OperationSimpleSetLink(
						srcMethodParameterNode, oldGlobalParameterNode, extLanguageManager);

		inOutReverseOperations.add(reverseOperationSimpleSetLink);
	}


	private static OnConstraintsOperationSetOnMethod createReverseOperationSetConstraints(
			BasicParameterNode srcParameterNode,
			Optional<NodeMapper> nodeMapper,
			IExtLanguageManager extLanguageManager) {

		IConstraintsParentNode methodNode = (IConstraintsParentNode) srcParameterNode.getParent();

		List<ConstraintNode> constraintNodes = methodNode.getConstraintNodes();

		List<ConstraintNode> listOfClonedConstraintNodes = new ArrayList<>();

		for (ConstraintNode constraintNode : constraintNodes) {

			ConstraintNode clone = constraintNode.makeClone(nodeMapper);
			listOfClonedConstraintNodes.add(clone);
		}

		OnConstraintsOperationSetOnMethod reverseOperation = 
				new OnConstraintsOperationSetOnMethod(methodNode, listOfClonedConstraintNodes, extLanguageManager);

		return reverseOperation;
	}

	private static void convertByConversionItemForLinking(
			ParameterConversionItem parameterConversionItem, 
			ListOfModelOperations inOutReverseOperations, 
			IExtLanguageManager extLanguageManager) {

		AbstractParameterNode srcAbstractParameterNode = 
				parameterConversionItem.getSrcPart().getParameter();

		BasicParameterNode srcParameterNode = (BasicParameterNode) srcAbstractParameterNode; 

		IConstraintsParentNode constraintsParentNode = (IConstraintsParentNode) srcParameterNode.getParent();

		convertConstraintsAtAllLevelsForOneItem(parameterConversionItem, constraintsParentNode);

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

	private static void convertConstraintsAtAllLevelsForOneItem(
			ParameterConversionItem parameterConversionItem,
			IConstraintsParentNode constraintsParentNode) {

		for (;;) {

			if (constraintsParentNode == null) {
				break;
			}

			convertOneItemForOneConstraintsParent(parameterConversionItem, constraintsParentNode);

			if (constraintsParentNode instanceof MethodNode) {
				break;
			}

			constraintsParentNode = (IConstraintsParentNode) constraintsParentNode.getParent();
		}
	}

	private static void convertOneItemForOneConstraintsParent(
			ParameterConversionItem parameterConversionItem,
			IConstraintsParentNode constraintsParentNode) {

		ParametersAndConstraintsParentNodeHelper.convertConstraints(
				constraintsParentNode.getConstraintNodes(),
				parameterConversionItem);
	}

	private static void removeSourceChoice(
			ChoiceNode srcChoiceNode, 
			ListOfModelOperations inOutReverseOperations,
			IExtLanguageManager extLanguageManager) {

		deleteChoice(srcChoiceNode, inOutReverseOperations, extLanguageManager);
	}

	private static void addDefaultValueToConversionDefinition(
			BasicParameterNode basicParameterNode,
			String defaultValue,
			ParameterConversionDefinition inOutParameterConversionDefinition) {

		ParameterConversionItemPart srcPart = 
				new ParameterConversionItemPartForValue(basicParameterNode, null, defaultValue);

		boolean isRandomized = false;

		ParameterConversionItem parameterConversionItem = 
				new ParameterConversionItem(srcPart, null, isRandomized, "default value");

		inOutParameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItem);
	}

}