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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.utils.ExtLanguageManagerForJava;

public class ChoicesParentNodeHelperTest {

	@Test
	public void traversingChoiceNodes(){

		// metod node 

		MethodNode methodNode = new MethodNode("M", null);

		// source parameter with two choices

		MethodParameterNode methodParameterNode1 = 
				MethodNodeHelper.addParameterToMethod(methodNode, "P1", "String");

		ChoiceNode choiceNode1 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(
						methodParameterNode1, "C1", "1");

		ChoiceNode choiceNode2 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(
						methodParameterNode1, "C2", "1");

		ChoiceNode choiceNode21 = 
				ChoiceNodeHelper.addChoiceToChoice(choiceNode2, "C21", "1");

		// traversing choices

		ChoiceNodeWorker choiceNodeWorker = new ChoiceNodeWorker();

		ChoicesParentNodeHelper.traverseSubTreesOfChoices(methodParameterNode1, choiceNodeWorker);

		// checks

		List<String> resultChoiceNames = choiceNodeWorker.getChoiceNames();
		assertEquals(choiceNode1.getName(), resultChoiceNames.get(0));
		assertEquals(choiceNode2.getName(), resultChoiceNames.get(1));
		assertEquals(choiceNode21.getName(), resultChoiceNames.get(2));
	}

	static class ChoiceNodeWorker implements IObjectWorker {

		List<String> fChoiceNames;

		public ChoiceNodeWorker() {

			fChoiceNames = new ArrayList<>();
		}

		@Override
		public void doWork(Object choiceNodeObject) {

			ChoiceNode choiceNode = (ChoiceNode)choiceNodeObject;
			fChoiceNames.add(choiceNode.getName());
		}

		public List<String> getChoiceNames() {
			return fChoiceNames;
		}
	}

	@Test
	public void copyChoicesOfOneLevel(){

		// metod node 

		MethodNode methodNode = new MethodNode("M", null);

		// source parameter with two choices

		MethodParameterNode methodParameterNode1 = 
				MethodNodeHelper.addParameterToMethod(methodNode, "P1", "String");

		ChoiceNode choiceNode1 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(
						methodParameterNode1, "C1", "1");

		ChoiceNode choiceNode2 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(
						methodParameterNode1, "C2", "1");

		ChoiceNode choiceNode3 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(
						methodParameterNode1, "C3", "1");


		// destination parameter 

		MethodParameterNode methodParameterNode2 = 
				MethodNodeHelper.addParameterToMethod(methodNode, "P2", "String");


		// creating copy

		ListOfModelOperations reverseOperations = new ListOfModelOperations();

		ChoicesParentNodeHelper.createCopyOfChoicesSubTrees(
				methodParameterNode1, methodParameterNode2, 
				reverseOperations, new ExtLanguageManagerForJava());

		// checks

		assertEquals(3, methodParameterNode1.getChoices().size());

		List<ChoiceNode> copiedChoices = methodParameterNode2.getChoices();
		assertEquals(3, copiedChoices.size());

		ChoiceNode resultChoiceNode1 = copiedChoices.get(0);
		assertEquals(choiceNode1.getName(), resultChoiceNode1.getName());

		ChoiceNode resultChoiceNode2 = copiedChoices.get(1);
		assertEquals(choiceNode2.getName(), resultChoiceNode2.getName());

		ChoiceNode resultChoiceNode3 = copiedChoices.get(2);
		assertEquals(choiceNode3.getName(), resultChoiceNode3.getName());
	}

	@Test
	public void copyChoicesOfManyLevels(){

		// metod node 

		MethodNode methodNode = new MethodNode("M", null);

		// source parameter with two choices

		MethodParameterNode methodParameterNode1 = 
				MethodNodeHelper.addParameterToMethod(methodNode, "P1", "String");

		ChoiceNode choiceNode1 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(
						methodParameterNode1, "C1", "1");

		ChoiceNode choiceNode2 = 
				ChoiceNodeHelper.addChoiceToChoice(choiceNode1, "C2", "1");

		ChoiceNode choiceNode3 = 
				ChoiceNodeHelper.addChoiceToChoice(choiceNode2, "C3", "1");

		// destination parameter 

		MethodParameterNode methodParameterNode2 = 
				MethodNodeHelper.addParameterToMethod(methodNode, "P2", "String");

		// creating copy

		ListOfModelOperations reverseOperations = new ListOfModelOperations();

		ChoicesParentNodeHelper.createCopyOfChoicesSubTrees(
				methodParameterNode1, methodParameterNode2, 
				reverseOperations, new ExtLanguageManagerForJava());

		// check choice 1

		assertEquals(1, methodParameterNode1.getChoices().size());

		List<ChoiceNode> copiedChoices1 = methodParameterNode2.getChoices();
		assertEquals(1, copiedChoices1.size());

		ChoiceNode resultChoiceNode1 = copiedChoices1.get(0);
		assertEquals(choiceNode1.getName(), resultChoiceNode1.getName());

		// check choice 2

		List<ChoiceNode> copiedChoices2 = resultChoiceNode1.getChoices();
		ChoiceNode resultChoiceNode2 = copiedChoices2.get(0);
		assertEquals(choiceNode2.getName(), resultChoiceNode2.getName());

		// check choice 3

		List<ChoiceNode> copiedChoices3 = resultChoiceNode2.getChoices();
		ChoiceNode resultChoiceNode3 = copiedChoices3.get(0);
		assertEquals(choiceNode3.getName(), resultChoiceNode3.getName());
	}

}
