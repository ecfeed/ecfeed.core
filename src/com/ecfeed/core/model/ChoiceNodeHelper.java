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

public class ChoiceNodeHelper {

	public static ChoiceNode createSubstitutePath(ChoiceNode choice, MethodParameterNode parameter) {
		List<ChoiceNode> copies = createListOfCopies(choice);
		setParentsOfChoices(copies, parameter);
		return copies.get(0);
	}

	private static List<ChoiceNode> createListOfCopies(ChoiceNode choice) {
		ChoiceNode orgChoice = choice;
		List<ChoiceNode> copies = new ArrayList<ChoiceNode>();

		for(;;) {
			ChoiceNode copy = 
					new ChoiceNode(
							orgChoice.getFullName(), orgChoice.getModelChangeRegistrator(), orgChoice.getValueString());
			
			copies.add(copy);

			AbstractNode orgParent = orgChoice.getParent();
			if (!(orgParent instanceof ChoiceNode)) {
				break;
			}

			orgChoice = (ChoiceNode)orgParent;
		}

		return copies;
	}

	private static void setParentsOfChoices(List<ChoiceNode> copies, MethodParameterNode parameter) {
		int copiesSize = copies.size();
		int lastIndex = copiesSize - 1;

		for (int index = 0; index < lastIndex; index++) {
			ChoiceNode current = copies.get(index);
			ChoiceNode next = copies.get(index + 1);

			current.setParent(next);
		}

		ChoiceNode last = copies.get(copiesSize - 1);
		last.setParent(parameter);
	}

}
