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

	public static void removeInconsistentConstraints(IConstraintsParentNode fMethodNode, BooleanHolder modelUpdated) {
		
		ConstraintNodeListHolder.ConstraintsItr constraintItr = fMethodNode.getIterator();
		
		while (fMethodNode.hasNextConstraint(constraintItr)) {
			
			if (!fMethodNode.getNextConstraint(constraintItr).isConsistent()) {
				
				fMethodNode.removeConstraint(constraintItr);
				modelUpdated.set(true);
			}
		}
	}

}
