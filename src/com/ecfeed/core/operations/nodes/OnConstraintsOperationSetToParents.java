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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IConstraintsParentNode;
import com.ecfeed.core.operations.AbstractOneWayModelOperation;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnConstraintsOperationSetToParents extends AbstractOneWayModelOperation {

	private List<ConstraintNode> fConstraintNodes;

	public OnConstraintsOperationSetToParents(
			List<ConstraintNode> constraintNodes, 
			IExtLanguageManager extLanguageManager){

		super("Set constraints to parent nodes", extLanguageManager);

		fConstraintNodes = constraintNodes;
	}

	@Override
	public void execute() {

		List<IConstraintsParentNode> parentsWithRemovedConstraints = new ArrayList<>();

		for (ConstraintNode constraintNode : fConstraintNodes) {

			IConstraintsParentNode constraintsParentNode = 
					(IConstraintsParentNode) constraintNode.getParent();

			if (!parentsWithRemovedConstraints.contains(constraintsParentNode)) {

				constraintsParentNode.removeAllConstraints();
				parentsWithRemovedConstraints.add(constraintsParentNode);
			}

			constraintsParentNode.addConstraint(constraintNode);
		}
	}

}
