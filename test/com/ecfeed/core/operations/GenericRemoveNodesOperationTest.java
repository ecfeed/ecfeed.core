/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;

public class GenericRemoveNodesOperationTest {

	@Test
	public void removeClass() {

		RootNode rootNode = new RootNode("Root", null);
		ClassNode classNode = new ClassNode("Class1", null);
		rootNode.addClass(classNode);
		
		List<IAbstractNode> classes = new ArrayList<>();
		classes.add(classNode);
		
		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				new GenericRemoveNodesOperation(
						classes, 
						new TypeAdapterProviderForJava(), 
						true, 
						rootNode, 
						rootNode, 
						new ExtLanguageManagerForJava());
		
		genericRemoveNodesOperation.execute();
		
		List<ClassNode> classNodes = rootNode.getClasses();
		
		assertTrue(classNodes.isEmpty());
	}

}
