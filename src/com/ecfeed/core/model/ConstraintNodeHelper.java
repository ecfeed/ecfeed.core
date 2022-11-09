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

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.ParameterConversionItem;

public class ConstraintNodeHelper {

	public static void convertConstraint(
			ConstraintNode constraintNode,
			ParameterConversionItem parameterConversionItem) {

		Constraint constraint = constraintNode.getConstraint();

		if (constraint == null) {
			ExceptionHelper.reportRuntimeException("Cannot update choice references. Constraint is empty.");
		}

		constraint.convert(parameterConversionItem);
	}

	//	public static void updateParameterReferences(
	//			ConstraintNode constraintNode,
	//			MethodParameterNode oldMethodParameterNode,
	//			ChoicesParentNode dstParameterForChoices) {
	//
	//		Constraint constraint = constraintNode.getConstraint();
	//
	//		if (constraint == null) {
	//			ExceptionHelper.reportRuntimeException("Cannot update choice references. Constraint is empty.");
	//		}
	//
	//		//		constraint.updateParameterReferences(oldMethodParameterNode, dstParameterForChoices);
	//	}

	public static List<ChoiceNode> getChoicesUsedInConstraint(
			ConstraintNode constraintNode,
			BasicParameterNode methodParameterNode) {

		List<ChoiceNode> result = 
				ConstraintHelper.getChoicesUsedInConstraints(
						constraintNode.getConstraint(),
						methodParameterNode);

		return result;
	}

	public static List<String> getLabelsUsedInConstraint(
			ConstraintNode constraintNode,
			BasicParameterNode methodParameterNode) {

		List<String> result = 
				ConstraintHelper.getLabelsUsedInConstraints(
						constraintNode.getConstraint(),
						methodParameterNode);

		return result;
	}

	public static String createSignature(ConstraintNode constraintNode, IExtLanguageManager extLanguageManager) {

		return ConstraintHelper.createSignature(constraintNode.getConstraint(), extLanguageManager);
	}

	public static String getName(ConstraintNode ownNode, IExtLanguageManager extLanguageManager) {

		return AbstractNodeHelper.getName(ownNode, extLanguageManager);
	}

	public static List<ConstraintNode> makeDerandomizedCopyOfConstraintNodes(List<ConstraintNode> constraints) {

		List<ConstraintNode> clonedConstraintNodes = new ArrayList<ConstraintNode>();

		for (ConstraintNode constraint : constraints) {

			ConstraintNode clonedConstraint = constraint.makeClone();

			clonedConstraint.derandomize();
			clonedConstraintNodes.add(clonedConstraint);
		}

		return clonedConstraintNodes;
	}

	public static List<ConstraintNode> createListOfConstraintNodes(
			List<Constraint> constraints, 
			MethodNode methodNode) {

		List<ConstraintNode> constraintNodes = new ArrayList<>();

		for (Constraint constraint: constraints) {

			ConstraintNode constraintNode = new ConstraintNode(constraint.getName(), constraint, null);

			constraintNodes.add(constraintNode);
		}

		return constraintNodes;
	}

	public static List<Constraint> createListOfConstraints(List<ConstraintNode> constraintNodes) {

		List<Constraint> constraints = new ArrayList<>();

		for (ConstraintNode constraintNode : constraintNodes) {
			constraints.add(constraintNode.getConstraint());
		}

		return constraints;
	}

}
