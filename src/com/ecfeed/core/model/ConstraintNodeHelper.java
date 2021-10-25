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

import com.ecfeed.core.utils.IExtLanguageManager;

public class ConstraintNodeHelper {

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

}
