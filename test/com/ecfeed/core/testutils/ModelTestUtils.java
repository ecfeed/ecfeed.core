/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.testutils;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ecfeed.core.model.*;

public class ModelTestUtils {

	public static void assertElementsEqual(AbstractNode n, AbstractNode n1) {
		ModelStringifier stringifier = new ModelStringifier();
		if(n.isMatch(n1) == false){
			fail("Parsed element differs from original\n" + stringifier.stringify(n, 0) + "\n" + stringifier.stringify(n1, 0));
		}
	}

	public static void assertCollectionsEqual(Collection<? extends AbstractNode> col1, Collection<? extends AbstractNode> col2){
		if(col1.size() != col2.size()){
			fail("Parsed collection differs from original\n" + col1 + "\n" + col2);
		}
		List<AbstractNode> l1 = new ArrayList<>(col1);
		List<AbstractNode> l2 = new ArrayList<>(col2);
		for(int i = 0; i < col1.size(); ++i){
			if(l1.get(i).isMatch(l2.get(i)) == false){
				fail("Parsed collection differs from original at element " + i +"\n" + l1.get(i) + "\n" + l2.get(i));
			}
		}
	}

	public static AbstractNode getNode(ENodeType type, String name){
		switch(type){
		case CHOICE: return new ChoiceNode(name, "value", null);
		case CLASS: return new ClassNode(name, null);
		case CONSTRAINT: return new ConstraintNode(
				name,
				new Constraint(
						name,
						ConstraintType.EXTENDED_FILTER,
						new StaticStatement(true, null),
						new StaticStatement(true, null),
						null),
				null);
		case METHOD: return new MethodNode(name, null);
		case PARAMETER: return new MethodParameterNode(name, "int", "0", false, null);
		case METHOD_PARAMETER: return new MethodParameterNode(name, "int", "0", false, null);
		case GLOBAL_PARAMETER: return new GlobalParameterNode(name, "int", null);
		case PROJECT: return new RootNode(name, null);
		case TEST_CASE: return new TestCaseNode(name, null, new ArrayList<ChoiceNode>());
		}
		return null;
	}

}
