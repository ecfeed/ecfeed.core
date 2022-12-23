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
import com.ecfeed.core.utils.SignatureHelper;
import com.ecfeed.core.utils.StringHelper;

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

		String qualifiedName = createQualifiedName(constraintNode);

		String signatureOfConditions = 
				ConstraintHelper.createSignatureOfConditions(constraintNode.getConstraint(), extLanguageManager);

		return qualifiedName + SignatureHelper.SIGNATURE_CONTENT_SEPARATOR + signatureOfConditions; 
	}

	private static String createQualifiedName(ConstraintNode constraintNode) {
		
		String prefix = getQualifiedPrefix(constraintNode);
		
		String name = constraintNode.getConstraint().getName();
		
		if (StringHelper.isNullOrEmpty(prefix)) {
			return name;
		}
		
		String qualifiedName = prefix + SignatureHelper.SIGNATURE_TYPE_SEPARATOR + name;
		
		return qualifiedName;
	}

	public static String getName(ConstraintNode ownNode, IExtLanguageManager extLanguageManager) {

		return AbstractNodeHelper.getName(ownNode, extLanguageManager);
	}

//	public static String getQualifiedName(ConstraintNode constraintNode) {
//
//		String prefix = getQualifiedPrefix(constraintNode);
//		
//		if (prefix == null) {
//			prefix = "";
//		}
//		
//		String name = constraintNode.getName();
//
//		return prefix + name; 
//	}

	private static String getQualifiedPrefix(ConstraintNode constraintNode) {

		String prefix = "";
		IAbstractNode currentNode = constraintNode;

		for (;;) {

			IAbstractNode parent = currentNode.getParent();

			if (parent == null) {
				return "";
			}

			if (parent instanceof MethodNode) {
				return prefix;
			}

			if (!(parent instanceof CompositeParameterNode)) {
				ExceptionHelper.reportRuntimeException("Composite parameter expected.");
			}

			prefix = parent.getName() + SignatureHelper.SIGNATURE_NAME_SEPARATOR + prefix;

			currentNode = parent;
		}

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
