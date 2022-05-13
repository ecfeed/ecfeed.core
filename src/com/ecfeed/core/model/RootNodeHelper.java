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

import java.util.List;
import java.util.Optional;

import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.QualifiedNameHelper;
import com.ecfeed.core.utils.StringHelper;

public class RootNodeHelper {

	public static final String CLASS_WITH_NAME = "Class with name";
	public static final String ALREADY_EXISTS = "already exists";

	public static GlobalParameterNode addGlobalParameterToRoot(RootNode rootNode, String name, String type) {

		GlobalParameterNode globalParameterNode = new GlobalParameterNode (name, type, null);
		rootNode.addParameter(globalParameterNode);

		return globalParameterNode;
	}

	public static ClassNode addClassNodeToRoot(
			RootNode rootNode, String className, IModelChangeRegistrator modelChangeRegistrator) {

		ClassNode classNode = new ClassNode("Class1", null);
		rootNode.addClass(classNode);

		return classNode;
	}

	public static String classWithNameExists(
			String classNameInExtLanguage,
			RootNode rootNode,
			IExtLanguageManager extLanguageManager) {

		List<ClassNode> classes = rootNode.getClasses();

		for (ClassNode node : classes) {

			String existingClassNameInExtLanguage = ClassNodeHelper.getQualifiedName(node, extLanguageManager);

			if (StringHelper.isEqual(classNameInExtLanguage, existingClassNameInExtLanguage)) {
				return CLASS_WITH_NAME + ": " + classNameInExtLanguage + " " +	ALREADY_EXISTS +
						".";
			}
		}

		return null;
	}

	public static String generateNewClassName(RootNode rootNode, String startClassNameCore) {
		boolean defaultPackage = !QualifiedNameHelper.hasPackageName(startClassNameCore);

		for (int i = 1;   ; i++) {
			String newClassName = startClassNameCore + String.valueOf(i);
			Optional<String> validatedNewClassName = validateClassName(rootNode, newClassName, defaultPackage);

			if (validatedNewClassName.isPresent()) {
				return validatedNewClassName.get();
			}
		}
	}

	private static Optional<String> validateClassName(RootNode rootNode, String newClassName, boolean defaultPackage) {

		if (rootNode.getClass(newClassName) == null) {
			return validateClassPackage(rootNode, newClassName, defaultPackage);
		}

		return Optional.empty();
	}

	private static Optional<String> validateClassPackage(RootNode rootNode, String newClassName, boolean defaultPackage) {

		if (defaultPackage) {
			if (isUniqueAcrossPackages(rootNode, newClassName)) {
				return Optional.of(newClassName);
			}
		} else {
			return Optional.of(newClassName);
		}

		return Optional.empty();
	}

	private static boolean isUniqueAcrossPackages(RootNode rootNode, String newClassName) {

		for (ClassNode node : rootNode.getClasses()) {
			if (node.getName().endsWith(newClassName)) {
				return false;
			}
		}

		return true;
	}

}
