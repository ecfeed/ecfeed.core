/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package operations;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.operations.FactoryRenameOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.RootOperationAddClass;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import com.ecfeed.core.utils.TestHelper;
import org.junit.Test;

import static org.junit.Assert.fail;

public class FactoryRenameOperationTest {

	@Test
	public void renameClassTest() {

		RootNode rootNode = new RootNode("Root", null);

		// add class1

		ClassNode classNode1 = new ClassNode("com.Class1", null);

		rootNode.addClass(classNode1);

		// add class2

		ClassNode classNode2 = new ClassNode("com.xx.Class2", null);

		rootNode.addClass(classNode2);

		// rename in simple mode - the same classNames

		final ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		IModelOperation modelOperation1 =
				FactoryRenameOperation.getRenameOperation(
						classNode2,
						"com.xx",
						"Class1",
						extLanguageManagerForSimple);

		try {
			modelOperation1.execute();
			fail();
		} catch (Exception e) {
		}

		// rename in java mode

		final ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();

		IModelOperation modelOperation2 =
				FactoryRenameOperation.getRenameOperation(
						classNode2,
						"com.xx",
						"Class1",
						extLanguageManagerForJava);

		try {
			modelOperation2.execute();
		} catch (Exception e) {
			fail();
		}
	}

	// TODO SIMPLE-VIEW rename method
	// TODO SIMPLE-VIEW rename parameter
}
