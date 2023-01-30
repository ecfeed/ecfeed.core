/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.nodes;

import java.util.List;

import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IConstraintsParentNode;
import com.ecfeed.core.operations.AbstractOneWayModelOperation;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnConstraintsOperationSetOnMethod extends AbstractOneWayModelOperation {

	private IConstraintsParentNode fMethodNode;
	private List<ConstraintNode> fConstraintNodes;

	public OnConstraintsOperationSetOnMethod(
			IConstraintsParentNode methodNode, List<ConstraintNode> constraintNodes, IExtLanguageManager extLanguageManager){

		super("Set constraints", extLanguageManager);

		fMethodNode = methodNode;
		fConstraintNodes = constraintNodes;
	}

	@Override
	public void execute() {

		fMethodNode.removeAllConstraints();

		for (ConstraintNode constraintNode : fConstraintNodes) {
			fMethodNode.addConstraint(constraintNode);
		}
	}

}