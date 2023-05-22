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

}
