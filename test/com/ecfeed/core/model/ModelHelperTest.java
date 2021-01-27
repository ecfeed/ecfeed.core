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

import com.ecfeed.core.testutils.RandomModelGenerator;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ExtLanguage;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ModelHelperTest {

	@Test
	public void getFullPathTest() {

		RootNode rootNode = new RootNode("root_1", null);
		String path = ModelHelper.getFullPath(rootNode, new ExtLanguageManagerForJava());
		assertEquals("root_1", path);
		path = ModelHelper.getFullPath(rootNode, new ExtLanguageManagerForSimple());
		assertEquals("root_1", path);

		ClassNode classNode = new ClassNode("class_1", null);
		classNode.setParent(rootNode);

		path = ModelHelper.getFullPath(classNode, new ExtLanguageManagerForJava());
		assertEquals("root_1.class_1", path);
		path = ModelHelper.getFullPath(classNode, new ExtLanguageManagerForSimple());
		assertEquals("root_1.class 1", path);
	}

}
