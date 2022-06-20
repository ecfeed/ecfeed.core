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
import com.ecfeed.core.utils.ChoiceConversionItem;
import com.ecfeed.core.utils.ChoiceConversionList;
import com.ecfeed.core.utils.ChoiceConversionOperation;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class ParameterTransformer {

	public static MethodNode linkMethodParameteToGlobalParameter(
			MethodParameterNode srcMethodParameterNode,
			GlobalParameterNode dstGlobalParameterNode, 
			ChoiceConversionList choiceConversionList,
			ListOfModelOperations outReverseOperations,
			IExtLanguageManager extLanguageManager) {

		checkParametersForNotNull(srcMethodParameterNode, dstGlobalParameterNode);

		MethodOperationSetConstraints reverseOperation = 
				createReverseOperationSetConstraints(srcMethodParameterNode, extLanguageManager);

		outReverseOperations.add(reverseOperation);


		if (choiceConversionList != null) {
			moveChoicesByConversionList(
					choiceConversionList, 
					srcMethodParameterNode, 
					dstGlobalParameterNode,
					outReverseOperations,
					extLanguageManager);
		}

		deleteRemainingChoices(srcMethodParameterNode, outReverseOperations, extLanguageManager);

		MethodNode methodNode = srcMethodParameterNode.getMethod();

		MethodNodeHelper.updateParameterReferencesInConstraints(
				srcMethodParameterNode, 
				dstGlobalParameterNode,
				methodNode.getConstraintNodes(),
				outReverseOperations,
				extLanguageManager);


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
			deleteRemainingChoicesRecursive(choiceNode, outReverseOperations, extLanguageManager);
		}
	}

	private static void deleteRemainingChoicesRecursive(
			ChoiceNode topChoiceNode,
			ListOfModelOperations outReverseOperations, 
			IExtLanguageManager extLanguageManager) {

		List<ChoiceNode> choices = new ArrayList<>(topChoiceNode.getChoices());

		if (choices.size() == 0) {

			ChoicesParentNode choicesParentNode = topChoiceNode.getParent();

			OperationSimpleAddChoice operationSimpleAddChoice = 
					new OperationSimpleAddChoice(topChoiceNode, choicesParentNode, extLanguageManager);

			outReverseOperations.add(operationSimpleAddChoice);

			choicesParentNode.removeChoice(topChoiceNode);
			return;
		}

		for (ChoiceNode choiceNode : choices) {
			deleteRemainingChoicesRecursive(choiceNode, outReverseOperations, extLanguageManager);
		}
	}

	public static void unlinkMethodParameteFromGlobalParameter(
			MethodParameterNode methodParameterNode,
			GlobalParameterNode globalParameterNode, 
			ListOfModelOperations outReverseOperations,
			IExtLanguageManager extLanguageManager) {

		checkParametersForNotNull(methodParameterNode, globalParameterNode);

		MethodNode methodNode = methodParameterNode.getMethod();

		List<ChoiceConversionItem> choiceConversionList = createChoiceConversionList(globalParameterNode);

		removeLinkOnMethodParameter(methodParameterNode, outReverseOperations, extLanguageManager);

		ListOfModelOperations reverseOperationsForChoicesCopy = new ListOfModelOperations();

		ChoicesParentNodeHelper.createCopyOfChoicesSubTrees(
				globalParameterNode, methodParameterNode, reverseOperationsForChoicesCopy, extLanguageManager);

		convertChoicesInConstraints(
				methodNode, 
				globalParameterNode, methodParameterNode, 
				choiceConversionList, outReverseOperations, 
				extLanguageManager);

		outReverseOperations.addAll(reverseOperationsForChoicesCopy);

		removeTestCases(methodNode, outReverseOperations, extLanguageManager);
	}

	private static void convertChoicesInConstraints(
			MethodNode methodNode, 
			AbstractParameterNode srcParameterNode,
			AbstractParameterNode dstParameterNode, 
			List<ChoiceConversionItem> choiceConversionList,
			ListOfModelOperations outReverseOperations, 
			IExtLanguageManager extLanguageManager) {

		for (ChoiceConversionItem choiceConversionItem : choiceConversionList) {

			String srcName = choiceConversionItem.getSrcName();
			ChoiceNode srcChoiceNode = srcParameterNode.getChoice(srcName);

			if (srcChoiceNode == null) {
				ExceptionHelper.reportRuntimeException("Cannot find source choice.");
			}

			String dstName = choiceConversionItem.getDstName();
			ChoiceNode dstChoiceNode = dstParameterNode.getChoice(dstName);

			if (dstChoiceNode == null) {
				ExceptionHelper.reportRuntimeException("Cannot find destination choice.");
			}

			MethodNodeHelper.updateChoiceReferencesInConstraints(
					srcChoiceNode, dstChoiceNode,
					methodNode.getConstraintNodes(),
					extLanguageManager);
		}
	}

	private static List<ChoiceConversionItem> createChoiceConversionList(GlobalParameterNode globalParameterNode) {

		ChoiceConversionListCreator choiceConversionListCreator = new ChoiceConversionListCreator();

		ChoicesParentNodeHelper.traverseSubTreesOfChoices(globalParameterNode, choiceConversionListCreator);

		List<ChoiceConversionItem> choiceConversionList = choiceConversionListCreator.getChoiceConversionList();
		return choiceConversionList;
	}

	private static class ChoiceConversionListCreator implements IObjectWorker {

		ChoiceConversionList fChoiceConversionList;

		public ChoiceConversionListCreator() {

			fChoiceConversionList = new ChoiceConversionList();
		}

		@Override
		public void doWork(Object choiceNodeObj) {

			ChoiceNode choiceNode = (ChoiceNode)choiceNodeObj;
			String choiceName = choiceNode.getQualifiedName();
			fChoiceConversionList.addItem(choiceName, ChoiceConversionOperation.MERGE, choiceName, null);
		}

		public List<ChoiceConversionItem> getChoiceConversionList() {

			List<ChoiceConversionItem> createSortedCopyOfConversionItems = 
					fChoiceConversionList.createSortedCopyOfConversionItems();

			return createSortedCopyOfConversionItems;
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

	public static void moveChoicesByConversionList(
			ChoiceConversionList choiceConversionItems,
			MethodParameterNode srcParameterNode, 
			GlobalParameterNode dstParameterNode,
			ListOfModelOperations inOutReverseOperations,
			IExtLanguageManager extLanguageManager) {

		List<ChoiceConversionItem> sortedChoiceConversionItems = 
				choiceConversionItems.createSortedCopyOfConversionItems();

		for (ChoiceConversionItem choiceConversionItem : sortedChoiceConversionItems) {

			moveChoicesByConversionItem(
					choiceConversionItem, 
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

	private static void moveChoicesByConversionItem(
			ChoiceConversionItem choiceConversionItem, 
			MethodParameterNode srcParameterNode, 
			GlobalParameterNode dstParameterNode,
			ListOfModelOperations inOutReverseOperations, 
			IExtLanguageManager extLanguageManager) {

		ChoiceNode srcChoiceNode = srcParameterNode.getChoice(choiceConversionItem.getSrcName());

		if (srcChoiceNode == null) {
			ExceptionHelper.reportRuntimeException("Cannot find source choice.");
		}

		ChoiceNode dstChoiceNode = dstParameterNode.getChoice(choiceConversionItem.getDstName());

		if (dstChoiceNode == null) {
			ExceptionHelper.reportRuntimeException("Cannot find destination choice.");
		}

		//		moveChildChoices(srcChoiceNode, dstChoiceNode, inOutReverseOperations, extLanguageManager);

		MethodNode methodNode = srcParameterNode.getMethod();

		updateChoiceReferencesInConstraints(
				srcChoiceNode, 
				dstChoiceNode, 
				methodNode, 
				inOutReverseOperations, // TODO DE-NO remove - not used
				extLanguageManager); 

		removeSourceChoice(srcChoiceNode, inOutReverseOperations, extLanguageManager);
	}

	private static void removeSourceChoice(
			ChoiceNode srcChoiceNode, 
			ListOfModelOperations inOutReverseOperations,
			IExtLanguageManager extLanguageManager) {

		ChoicesParentNode choicesParentNode = srcChoiceNode.getParent();

		choicesParentNode.removeChoice(srcChoiceNode);

		OperationSimpleAddChoice reverseOperation = 
				new OperationSimpleAddChoice(srcChoiceNode,choicesParentNode, extLanguageManager);

		inOutReverseOperations.add(reverseOperation);
	}

	private static void updateChoiceReferencesInConstraints(
			ChoiceNode srcChoiceNode,
			ChoiceNode dstChoiceNode, 
			MethodNode methodNode,
			ListOfModelOperations inOutReverseOperations, 
			IExtLanguageManager extLanguageManager) {

		MethodNodeHelper.updateChoiceReferencesInConstraints(
				srcChoiceNode, dstChoiceNode,
				methodNode.getConstraintNodes(),
				extLanguageManager);
	}

	//	private static void moveChildChoices(
	//			ChoiceNode srcChoiceNode, ChoiceNode dstChoiceNode,
	//			ListOfModelOperations inOutReverseOperations, 
	//			IExtLanguageManager extLanguageManager) {
	//
	//		ChoiceNodeHelper.moveChildChoices(srcChoiceNode, dstChoiceNode);
	//
	//		ChoiceOperationMoveChildren choiceOperationMoveChildren = 
	//				new ChoiceOperationMoveChildren(dstChoiceNode, srcChoiceNode, extLanguageManager);
	//
	//		inOutReverseOperations.add(choiceOperationMoveChildren);
	//	}

	//	private static void moveRemainingTopChoices(
	//			MethodParameterNode srcMethodParameterNode,
	//			ChoicesParentNode dstParameterNode,
	//			ListOfModelOperations reverseOperations, 
	//			IExtLanguageManager extLanguageManager) {
	//
	//		List<ChoiceNode> choiceNodes = srcMethodParameterNode.getChoices();
	//
	//		for (ChoiceNode choiceNode : choiceNodes) {
	//			addChoiceWithUniqueName(choiceNode, dstParameterNode, reverseOperations, extLanguageManager);
	//		}
	//	}

	//	private static void addChoiceWithUniqueName(
	//			ChoiceNode choiceNode, 
	//			ChoicesParentNode methodParameterNode,
	//			ListOfModelOperations reverseOperations,
	//			IExtLanguageManager extLanguageManager) {
	//
	//		String orginalChoiceName = choiceNode.getName();
	//
	//		if (!choiceNameExistsAmongChildren(orginalChoiceName, methodParameterNode)) {
	//
	//			methodParameterNode.addChoice(choiceNode);
	//			return;
	//		}
	//
	//		for (int postfixCounter = 1; postfixCounter < 999; postfixCounter++) {
	//
	//			String tmpName = orginalChoiceName + "-" + postfixCounter;
	//
	//			if (!choiceNameExistsAmongChildren(tmpName, methodParameterNode)) {
	//
	//				choiceNode.setName(tmpName);
	//				methodParameterNode.addChoice(choiceNode);
	//				return;
	//			}
	//		}
	//
	//		ExceptionHelper.reportRuntimeException("Cannot add choice to method parameter.");
	//	}

	//	private static boolean choiceNameExistsAmongChildren(String choiceName, ChoicesParentNode methodParameterNode) {
	//
	//		List<ChoiceNode> choiceNodes = methodParameterNode.getChoices();
	//
	//		for (ChoiceNode choiceNode : choiceNodes) {
	//
	//			String currentChoiceName = choiceNode.getName();
	//
	//			if (currentChoiceName.equals(choiceName)) {
	//				return true;
	//			}
	//		}
	//
	//		return false;
	//	}

}