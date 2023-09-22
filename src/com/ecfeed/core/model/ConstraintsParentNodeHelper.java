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

import com.ecfeed.core.utils.BooleanHolder;

public abstract class ConstraintsParentNodeHelper {

	public static ConstraintNode addNewConstraintNode(
			IConstraintsParentNode parameterParentNode,
			Constraint constraint, 
			boolean setParent, 
			IModelChangeRegistrator modelChangeRegistrator) {

		ConstraintNode constraintNode = new ConstraintNode(constraint.getName(), constraint, null);

		if (setParent) {
			constraintNode.setParent(parameterParentNode);
		}

		parameterParentNode.addConstraint(constraintNode);

		return constraintNode;
	}

	public static ConstraintNode addNewConstraintNode(
			IConstraintsParentNode parameterParentNode,
			String constraintName,
			ConstraintType constraintType,
			AbstractStatement precondition, 
			AbstractStatement postcondition,
			boolean setParent, 
			IModelChangeRegistrator modelChangeRegistrator) {

		Constraint constraint = 
				new Constraint(
						constraintName, 
						constraintType, 
						precondition, 
						postcondition, 
						null);

		ConstraintNode constraintNode = new ConstraintNode(constraintName, constraint, null);

		if (setParent) {
			constraintNode.setParent(parameterParentNode);
		}

		parameterParentNode.addConstraint(constraintNode);

		return constraintNode;
	}


	public static void removeInconsistentConstraints(IConstraintsParentNode constraintsParentNode, BooleanHolder modelUpdated) {

		ConstraintNodeListHolder.ConstraintsItr constraintItr = constraintsParentNode.getIterator();

		while (constraintsParentNode.hasNextConstraint(constraintItr)) {

			ConstraintNode constraintNode = constraintsParentNode.getNextConstraint(constraintItr);

			if (!constraintNode.isConsistent()) {

				constraintsParentNode.removeConstraint(constraintItr);
				modelUpdated.set(true);
			}
		}
	}

	public static List<ConstraintNode> findChildConstraints(IConstraintsParentNode constraintsParentNode) { // XYX move to ConstraintNodeHelper

		return findChildConstraintsRecursive(constraintsParentNode);
	}

	private static List<ConstraintNode> findChildConstraintsRecursive(IConstraintsParentNode constraintsParentNode) {

		List<ConstraintNode> resultConstraintNodes = new ArrayList<>();

		List<IAbstractNode> children = constraintsParentNode.getChildren();

		for (IAbstractNode child : children) {

			if (child instanceof ConstraintNode) {
				resultConstraintNodes.add((ConstraintNode) child);
			}
		}

		for (IAbstractNode child : children) {

			if (child instanceof CompositeParameterNode) {
				List<ConstraintNode> constraintNodesOfChild = 
						findChildConstraintsRecursive((CompositeParameterNode) child);

				resultConstraintNodes.addAll(constraintNodesOfChild);
			}
		}


		return resultConstraintNodes;
	}


}
