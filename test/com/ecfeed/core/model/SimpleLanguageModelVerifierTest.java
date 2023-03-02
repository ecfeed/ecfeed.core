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

import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.TestHelper;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleLanguageModelVerifierTest {

	@Test
	public void classDuplicatedTest() {

		RootNode rootNode = new RootNode("rootNode", null);
		assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		ClassNode classNode1 = new ClassNode("com.Class_1", null);
		rootNode.addClass(classNode1);
		assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		ClassNode classNode2 = new ClassNode("com.xx.Class_1", null);
		rootNode.addClass(classNode2);
		assertNotNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		classNode2.setName("Class_1");
		assertNotNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));

		classNode2.setName("com.xx.Class_2");
		assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));
	}

	@Test
	public void notAllowedNames() {

		RootNode rootNode = new RootNode("rootNode", null);

		// invalid global parameter name

		BasicParameterNode globalParameterNodeErr = new BasicParameterNode("__", "int", "0", false, null);
		rootNode.addParameter(globalParameterNodeErr);

		String errorMessage = SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode);
		assertNotNull(errorMessage);
		checkInvalidNameMessage(errorMessage, globalParameterNodeErr);

		rootNode.removeParameter(globalParameterNodeErr);


		// valid global parameter name

		new BasicParameterNode("par1", "int", "0", false, null);
		errorMessage = SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode);
		assertNull(errorMessage);


		// invalid class name

		ClassNode classNodeErr = new ClassNode("com.__", null);
		rootNode.addClass(classNodeErr);

		errorMessage = SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode);
		assertNotNull(errorMessage);
		checkInvalidNameMessage(errorMessage, classNodeErr);

		rootNode.removeClass(classNodeErr);

		// valid class name

        ClassNode classNodeOk = new ClassNode("com.fun", null);
        rootNode.addClass(classNodeOk);

        assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));


        // invalid method name

        MethodNode methodNodeErr = new MethodNode("___", null);
        classNodeOk.addMethod(methodNodeErr);

        errorMessage = SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode);
        assertNotNull(errorMessage);
		checkInvalidNameMessage(errorMessage, methodNodeErr);


        // valid method name

        classNodeOk.removeMethod(methodNodeErr);

        MethodNode methodNodeOk = new MethodNode("fun", null);
        classNodeOk.addMethod(methodNodeOk);

        assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));


        // invalid method parameter name

        BasicParameterNode methodParameterNodeErr =
                new BasicParameterNode("___","int", "0", false, null);
        methodNodeOk.addParameter(methodParameterNodeErr);

		errorMessage = SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode);
        assertNotNull(errorMessage);
		checkInvalidNameMessage(errorMessage, methodParameterNodeErr);


        // valid method parameter name

        methodNodeOk.removeParameter(methodParameterNodeErr);

        BasicParameterNode methodParameterNodeOk =
                new BasicParameterNode("par","int", "0", false, null);
        methodNodeOk.addParameter(methodParameterNodeOk);

        assertNull(SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode));


        // invalid choice name

		ChoiceNode choiceNodeErr = new ChoiceNode("__", "1", null);
		methodParameterNodeOk.addChoice(choiceNodeErr);

		errorMessage = SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode);
		assertNotNull(errorMessage);
		checkInvalidNameMessage(errorMessage, choiceNodeErr);

		methodParameterNodeOk.removeChoice(choiceNodeErr);


		// valid choice name

		ChoiceNode choiceNodeoK = new ChoiceNode("c1", "1", null);
		methodParameterNodeOk.addChoice(choiceNodeoK);
		errorMessage = SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode);
		assertNull(errorMessage);


		// invalid constraint name

		ConstraintNode constraintNodeErr = new ConstraintNode("__", null, null);
		methodNodeOk.addConstraint(constraintNodeErr);

		errorMessage = SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode);
		assertNotNull(errorMessage);
		checkInvalidNameMessage(errorMessage, constraintNodeErr);

		methodNodeOk.removeConstraint(constraintNodeErr);


		// valid constraint

		ConstraintNode constraintNodeOk = new ConstraintNode("constr", null, null);
		methodNodeOk.addConstraint(constraintNodeOk);

		errorMessage = SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode);
		assertNull(errorMessage);
	}

	public void checkInvalidNameMessage(String errorMessage, IAbstractNode nodeErr) {

		TestHelper.checkMessage(
				errorMessage,
				JavaLanguageHelper.NAME_MUST_NOT_CONTAIN_ONLY_UNDERLINE_CHARACTERS,
				ModelHelper.getFullPath(nodeErr, new ExtLanguageManagerForJava()));
	}

}
