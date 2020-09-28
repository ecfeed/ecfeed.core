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

import com.ecfeed.core.utils.ExtLanguage;

public class ConstraintHelper {
	
	
	public static String getSignature(Constraint constraint, ExtLanguage extLanguage) { // TODO SIMPLE-VIEW move to helper and test

		return constraint.getName() + ": " + constraint.getSignature();
	}
	
	public static List<String> createListOfConstraintNames(List<Constraint> constraints) {
		
		List<String> constraintNames = new ArrayList<>();
		
		for (IConstraint<ChoiceNode> iConstraint : constraints) {
			
			if (iConstraint instanceof Constraint) {
				
				Constraint constraint = (Constraint)iConstraint;
				constraintNames.add(constraint.getName());
			}
		}
		return constraintNames;
	}
	
	public static boolean containsConstraints(List<Constraint> iConstraints) {

		for (IConstraint<ChoiceNode> iConstraint : iConstraints) {
			
			if (iConstraint instanceof Constraint) {
				return true;
			}
		}
		
		return false;
	}

}