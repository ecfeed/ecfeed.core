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
import java.util.Optional;

import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.NameHelper;
import com.ecfeed.core.utils.QualifiedNameHelper;
import com.ecfeed.core.utils.StringHelper;

public class RootNodeHelper {

	public static final String CLASS_NEW_NAME = "TestClass";
	public static final String CLASS_WITH_NAME = "Class with name";
	public static final String ALREADY_EXISTS = "already exists";

	public static BasicParameterNode addNewBasicParameter(
			RootNode rootNode, 
			String name, 
			String type,
			String defaultValue,
			boolean setParent,
			IModelChangeRegistrator modelChangeRegistrator) {

		BasicParameterNode globalParameterNode = 
				new BasicParameterNode (name, type, defaultValue, false, modelChangeRegistrator);

		if (setParent) {
			globalParameterNode.setParent(rootNode);
		}

		rootNode.addParameter(globalParameterNode);

		return globalParameterNode;
	}

	public static CompositeParameterNode addNewCompositeParameter(
			RootNode rootNode, String name, boolean setParent, IModelChangeRegistrator modelChangeRegistrator) {

		CompositeParameterNode globalParameterNode = new CompositeParameterNode(name, modelChangeRegistrator);

		if (setParent) {
			globalParameterNode.setParent(rootNode);
		}

		rootNode.addParameter(globalParameterNode);

		return globalParameterNode;
	}

	public static ClassNode addNewClassNode(
			RootNode rootNode,
			String className, 
			boolean setParent,
			IModelChangeRegistrator modelChangeRegistrator) {

		ClassNode classNode = new ClassNode(className, modelChangeRegistrator);

		if (setParent) {
			classNode.setParent(classNode);
		}

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

	public static ClassNode findClassByName( // XYX class node helper
			String classNameInIntrLanguage,
			RootNode rootNode) {

		List<ClassNode> classes = rootNode.getClasses();

		for (ClassNode node : classes) {

			String currentName = node.getName();

			if (StringHelper.isEqual(currentName, classNameInIntrLanguage)) {
				return node;
			}
		}

		return null;
	}

	public static String generateUniqueClassName(
			RootNode rootNode, 
			String oldName, 
			String availableClassName,
			IExtLanguageManager extLanguageManager) {

		String oldNameInIntrLanguage = extLanguageManager.convertTextFromExtToIntrLanguage(oldName);

		String newNameInIntrLanguage = generateUniqueClassName(rootNode, oldNameInIntrLanguage, availableClassName);

		String newNameInExtLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(newNameInIntrLanguage);

		return newNameInExtLanguage;
	}

	public static String generateNewClassName(RootNode rootNode) {

		String fullClassName = CLASS_NEW_NAME;
		return generateUniqueClassNameFromClassNameCore(rootNode, fullClassName, null);
	}

	public static String generateUniqueClassName(
			RootNode rootNode, 
			String oldNameInIntrLanguage) {

		String oldNameCore = StringHelper.removeFromNumericPostfix(oldNameInIntrLanguage);

		String newName = generateUniqueClassNameFromClassNameCore(rootNode, oldNameCore, null);

		return newName;
	}

	public static String generateUniqueClassName(
			RootNode rootNode, 
			String oldNameInIntrLanguage,
			String availableClassName) {

		String oldNameCore = StringHelper.removeFromNumericPostfix(oldNameInIntrLanguage);

		String newName = generateUniqueClassNameFromClassNameCore(rootNode, oldNameCore, availableClassName);

		return newName;
	}

	public static String generateUniqueClassNameFromClassNameCore(
			RootNode rootNode, 
			String startClassNameCore,
			String availableClassName) {

		boolean defaultPackage = !QualifiedNameHelper.hasPackageName(startClassNameCore);

		for (int i = 1;   ; i++) {

			String newClassName = startClassNameCore + String.valueOf(i);

			if (availableClassName != null && StringHelper.isEqual(newClassName, availableClassName)) {
				return availableClassName;
			}

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

	public static RootNode findRootNode(IAbstractNode anyNode) {

		IAbstractNode parent = anyNode;

		while (parent != null) {

			if (parent instanceof RootNode) {
				return (RootNode) parent;
			}

			parent = parent.getParent();
		}

		return null;
	}

	public static void compareRootNodes(RootNode rootNode1, RootNode rootNode2) {

		NameHelper.compareNames(rootNode1.getName(), rootNode2.getName());

		AbstractNodeHelper.compareSizes(rootNode1.getClasses(), rootNode2.getClasses(), "Number of classes differs.");

		for (int i = 0; i < rootNode1.getClasses().size(); ++i) {

			ClassNode classNode1 = rootNode1.getClasses().get(i);
			ClassNode classNode2 = rootNode2.getClasses().get(i);

			AbstractNodeHelper.compareParents(classNode1, rootNode1, classNode2, rootNode2);
			ClassNodeHelper.compareClasses(classNode1, classNode2);
		}
	}

	public static List<MethodNode> getAllMethodNodes(RootNode rootNode) {

		List<MethodNode> result = new ArrayList<>();

		List<ClassNode> classNodes = rootNode.getClasses();

		for (ClassNode classNode : classNodes) {

			result.addAll(classNode.getMethods());
		}

		return result;
	}

}
