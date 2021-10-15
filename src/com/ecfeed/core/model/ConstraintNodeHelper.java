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

import com.ecfeed.core.utils.IExtLanguageManager;

public class ConstraintNodeHelper {

	public static String createSignature(ConstraintNode constraintNode, IExtLanguageManager extLanguageManager) {

		return ConstraintHelper.createSignature(constraintNode.getConstraint(), extLanguageManager);
	}

	public static String getName(ConstraintNode ownNode, IExtLanguageManager extLanguageManager) {

		return AbstractNodeHelper.getName(ownNode, extLanguageManager);
	}

	// TODO EX-AM - add method to convert constraint to non-randomized constraint
	
	public static ConstraintNode makeCloneWithoutRandomization(ConstraintNode constraintNode) {
		
	}
}
