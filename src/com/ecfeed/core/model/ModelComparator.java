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


import java.io.ByteArrayOutputStream;

import com.ecfeed.core.model.serialization.ModelSerializer;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.NameHelper;
import com.ecfeed.core.utils.StringHelper;

public class ModelComparator { // XYX improve method comparator to check parents (if parent matches in model1 then it should match also in model2) 

	// XYX compare constraints of composite parameter

	public static void compareRootNodes(RootNode rootNode1, RootNode rootNode2) {

		NameHelper.compareNames(rootNode1.getName(), rootNode2.getName());
		
		AbstractNodeHelper.compareSizes(rootNode1.getClasses(), rootNode2.getClasses(), "Number of classes differs.");

		for (int i = 0; i < rootNode1.getClasses().size(); ++i) {

			ClassNode classNode1 = rootNode1.getClasses().get(i);
			ClassNode classNode2 = rootNode2.getClasses().get(i);

			AbstractNodeHelper.compareParents(classNode1, rootNode1, classNode2, rootNode2);
			compareClasses(classNode1, classNode2);
		}

		compareSerializedModelsAsLastResort(rootNode1, rootNode2);
	}

	public static void compareClasses(ClassNode classNode1, ClassNode classNode2) {

		NameHelper.compareNames(classNode1.getName(), classNode2.getName());
		AbstractNodeHelper.compareSizes(classNode1.getMethods(), classNode2.getMethods(), "Number of methods differs.");

		for (int i = 0; i < classNode1.getMethods().size(); ++i) {

			MethodNode methodNode1 = classNode1.getMethods().get(i);
			MethodNode methodNode2 = classNode2.getMethods().get(i);

			AbstractNodeHelper.compareParents(methodNode1, classNode1, methodNode2, classNode2);
			compareMethods(methodNode1, methodNode2);
		}
	}

	public static void compareMethods(MethodNode method1, MethodNode method2) {

		if (method1 == null) {
			ExceptionHelper.reportRuntimeException("Empty method 1.");
		}

		if (method2 == null) {
			ExceptionHelper.reportRuntimeException("Empty method 2.");
		}

		// XYX
		NameHelper.compareNames(method1.getName(), method2.getName());

		MethodNodeHelper.compareMethodParameters(method1, method2);
		MethodNodeHelper.compareDeployedParameters(method1, method2);
		MethodNodeHelper.compareMethodConstraints(method1, method2);
		MethodNodeHelper.compareTestCases(method1, method2);
	}

	public static void compareParametersWithLinkingContexts(
			ParameterWithLinkingContext parameterWithContext1, 
			ParameterWithLinkingContext parameterWithContext2) {

		AbstractParameterNodeHelper.compareParameters(
				parameterWithContext1.getParameter(), parameterWithContext2.getParameter());
		
		AbstractParameterNodeHelper.compareParameters(
				parameterWithContext1.getLinkingContext(), parameterWithContext2.getLinkingContext());
	}

	private static void compareSerializedModelsAsLastResort(RootNode model1, RootNode model2) {

		String xml1 = serializeModel(model1);
		String xml2 = serializeModel(model2);

		String[] lines1 = xml1.split("\n");
		String[] lines2 = xml2.split("\n");

		if (xml1.equals(xml2)) {
			return;
		}

		String errorMessage = StringHelper.isEqualByLines(lines1, lines2);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException("Model comparison failed with message: " + errorMessage);
		}

		ExceptionHelper.reportRuntimeException("Comparison of serialized models failed.");
	}

	private static String serializeModel(RootNode model1) {

		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		ModelSerializer serializer = new ModelSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());

		try {
			serializer.serialize(model1);
		} catch (Exception e) {
			ExceptionHelper.reportRuntimeException("Failed to serialize model.", e);
		}

		return ostream.toString();
	}

}
