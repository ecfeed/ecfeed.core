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
import com.ecfeed.core.utils.PackageClassHelper;
import com.ecfeed.core.utils.StringHelper;

public class RootNodeHelper {

	public static String generateNewClassName(RootNode rootNode, String startClassName) {
		boolean defaultPackage = !PackageClassHelper.hasPackageName(startClassName);

		for (int i = 1;   ; i++) {
			String newClassName = startClassName + String.valueOf(i);
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

	// TODO SIMPLE-VIEW test
	public static String classWithNameExists(
			String classNameInExtLanguage, 
			RootNode rootNode,
			IExtLanguageManager extLanguageManager) {

		List<ClassNode> classes = rootNode.getClasses();

		for (ClassNode node : classes) {

			String existingClassNameInExtLanguage = ClassNodeHelper.getQualifiedName(node, extLanguageManager);

			if (StringHelper.isEqual(classNameInExtLanguage, existingClassNameInExtLanguage)) {
				return "Class with name: " + classNameInExtLanguage + " already exists."; 
			}
		}

		return null;
	}

}
