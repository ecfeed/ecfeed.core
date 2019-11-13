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

public class ConstraintHelper {
	
	public static List<String> createListOfConstraintNames(List<AbstractConstraint> constraints) {
		
		List<String> constraintNames = new ArrayList<>();
		
		for (AbstractConstraint iConstraint : constraints) {
			
			if (iConstraint instanceof ImplicationConstraint) {

				ImplicationConstraint constraint = (ImplicationConstraint)iConstraint;
				constraintNames.add(constraint.getName());
			}
		}
		return constraintNames;
	}
	
	public static boolean containsConstraints(List<AbstractConstraint> iConstraints) {

		for (IConstraint<ChoiceNode> iConstraint : iConstraints) {
			
			if (iConstraint instanceof ImplicationConstraint) {
				return true;
			}
		}
		
		return false;
	}

}