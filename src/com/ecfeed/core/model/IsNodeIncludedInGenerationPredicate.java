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


public class IsNodeIncludedInGenerationPredicate {

	private final MethodNode fGeneratorMethodNode;
	private final List<List<ChoiceNode>> fAllowedChoiceInput;
	private final List<IConstraint<ChoiceNode>> fAllowedConstraints;

	public IsNodeIncludedInGenerationPredicate(
			MethodNode generatorMethodNode,
			List<List<ChoiceNode>> allowedChoiceInput,
			List<IConstraint<ChoiceNode>> allowedConstraints) {

		fGeneratorMethodNode = generatorMethodNode;

		if (allowedChoiceInput == null) {
			fAllowedChoiceInput = new ArrayList<List<ChoiceNode>>();
		} else {
			fAllowedChoiceInput = allowedChoiceInput;

		}

		if (allowedConstraints != null) {
			fAllowedChoiceInput.add(getListOfChoices(allowedConstraints));
		}

		if (allowedConstraints == null) {
			fAllowedConstraints = new ArrayList<IConstraint<ChoiceNode>>();
		} else {
			fAllowedConstraints = allowedConstraints;
		}
	}

	private static List<ChoiceNode> getListOfChoices(List<IConstraint<ChoiceNode>> allowedConstraints) {

		List<ChoiceNode> listOfChoices = new ArrayList<ChoiceNode>();

		for (IConstraint<ChoiceNode> iconstraint : allowedConstraints) {
			Constraint constraint = (Constraint)iconstraint;
			List<ChoiceNode> referencedConstraints = constraint.getListOfChoices();
			listOfChoices.addAll(referencedConstraints);
		}

		return listOfChoices;
	}

	public boolean test(AbstractNode abstractNode) {

		if (abstractNode instanceof RootNode) {

			return true;
		}

		if (abstractNode instanceof ClassNode) {

			return shouldSerializeClassNode(abstractNode, fGeneratorMethodNode);
		}

		if (abstractNode instanceof MethodNode) {

			return shouldSerializeMethodNode(abstractNode, fGeneratorMethodNode);
		}

		if (abstractNode instanceof MethodParameterNode) {

			return shouldSerializeMethodParameterNode(abstractNode, fGeneratorMethodNode);
		}

		if (abstractNode instanceof GlobalParameterNode) {

			return shouldSerializeGlobalParameterNode(abstractNode, fAllowedChoiceInput); 
		}

		if (abstractNode instanceof ChoiceNode) {

			return shouldSerializeChoiceNode(abstractNode, fAllowedChoiceInput, fAllowedConstraints); 
		}

		if (abstractNode instanceof ConstraintNode) {

			return shouldSerializeConstraintNode(abstractNode, fAllowedConstraints); 
		}

		return false;
	}

	private static boolean shouldSerializeClassNode(AbstractNode abstractNode, MethodNode generatorMethodNode) {

		ClassNode classNode = (ClassNode)abstractNode; 

		ClassNode parentOfMethod = (ClassNode)generatorMethodNode.getParent();

		if (classNode.equals(parentOfMethod)) {
			return true;
		}

		return false;
	}

	private static boolean shouldSerializeMethodNode(AbstractNode abstractNode, MethodNode generatorMethodNode) {

		MethodNode methodNode = (MethodNode)abstractNode;

		if (methodNode.equals(generatorMethodNode)) {
			return true;
		}

		return false;
	}

	private static boolean shouldSerializeMethodParameterNode(
			AbstractNode abstractNode, MethodNode generatorMethodNode) {

		if (abstractNode.getParent().equals(generatorMethodNode)) {
			return true;
		}

		return false;
	}

	private static boolean shouldSerializeGlobalParameterNode(
			AbstractNode abstractNode, 
			List<List<ChoiceNode>> allowedChoiceInput) {

		return isAncestorOfAllowedChoices(abstractNode, allowedChoiceInput);
	}

	private static boolean shouldSerializeChoiceNode(
			AbstractNode abstractNode, 
			List<List<ChoiceNode>> allowedChoiceInput,
			List<IConstraint<ChoiceNode>> allowedConstraints) {

		ChoiceNode choiceNode = (ChoiceNode)abstractNode;

		if (isChoiceAllowed(choiceNode, allowedChoiceInput)) {
			return true;
		}

		return isAncestorOfAllowedChoices(abstractNode, allowedChoiceInput);
	}

	private static boolean shouldSerializeConstraintNode(
			AbstractNode abstractNode,
			List<IConstraint<ChoiceNode>> allowedConstraints) {

		ConstraintNode constraintNode = (ConstraintNode)abstractNode;
		Constraint constraintFromNode = constraintNode.getConstraint();

		for (IConstraint<ChoiceNode> constraint : allowedConstraints) {
			if (constraint.equals(constraintFromNode)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isChoiceAllowed(ChoiceNode testedChoiceNode, List<List<ChoiceNode>> allowedChoiceInput) {

		for (List<ChoiceNode> choiceList : allowedChoiceInput) {
			for (ChoiceNode allowedChoiceNode : choiceList) {
				if (allowedChoiceNode.equals(testedChoiceNode)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean isAncestorOfAllowedChoices(
			AbstractNode abstractNode,
			List<List<ChoiceNode>> allowedChoiceInput) {

		for (List<ChoiceNode> choiceList : allowedChoiceInput) {
			for (ChoiceNode choiceNode : choiceList) {

				if (choiceNode.isMyAncestor(abstractNode)) {
					return true;
				}
			}
		}

		return false;
	}

}

