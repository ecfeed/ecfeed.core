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

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.RegexHelper;

public class ModelHelper {

	public static String getFullPath(IAbstractNode abstractNode, IExtLanguageManager extLanguageManager) {

		List<String> nodeNames = new ArrayList<String>();

		IAbstractNode currentNode = abstractNode;

		for(;;) {

			if (currentNode == null) {
				return createPath(nodeNames);
			}

			if (currentNode instanceof RootNode) {
				nodeNames.add(currentNode.getName());
			} else {
				nodeNames.add(AbstractNodeHelper.getName(currentNode, extLanguageManager));
			}

			currentNode = currentNode.getParent();
		}
	}

	private static String createPath(List<String> nodeNames) {

		String path = "";

		int nodeNamesLength = nodeNames.size();

		for (int index = nodeNamesLength - 1; index >= 0; index--) {

			String nodeName = nodeNames.get(index);
			path += nodeName;

			if (index > 0) {
				path += ".";
			}
		}

		return path;
	}

	public static String getNonQualifiedName(String qualifiedName) {

		int lastDotIndex = qualifiedName.lastIndexOf('.');

		if (lastDotIndex == -1) {
			return qualifiedName;
		}

		return qualifiedName.substring(lastDotIndex + 1);
	}

	public static String getQualifiedName(String packageName, String localName) {

		return packageName + "." + localName;
	}

	public static String getPackageName(String qualifiedName) {

		int lastDotIndex = qualifiedName.lastIndexOf('.');

		return (lastDotIndex == -1)? "" : qualifiedName.substring(0, lastDotIndex);
	}

	public static boolean isValidTestCaseName(String name) {

		return name.matches(RegexHelper.REGEX_TEST_CASE_NODE_NAME);
	}

	public static boolean isValidConstraintName(String name) {

		return name.matches(RegexHelper.REGEX_CONSTRAINT_NODE_NAME);
	}

	public static boolean validateTestCaseName(String name){

		return name.matches(RegexHelper.REGEX_TEST_CASE_NODE_NAME);
	}

	public static RootNode findRoot(AbstractNode startNode) { 

		IAbstractNode node = startNode;

		for (int cnt = 0;  ; cnt++) {

			if (cnt >= 1000) {
				ExceptionHelper.reportRuntimeException("Model too deep or recursive. Cannot find root.");
			}

			IAbstractNode parent = node.getParent();

			if (parent == null) {

				if (node instanceof RootNode) {
					return (RootNode)node;
				}

				return null;
			}

			node = parent;
		}

	}

	public static String getNodeTypeName(IAbstractNode abstractNode) { // TODO SIMPLE-VIEW visitor, test

		if (abstractNode instanceof GlobalParameterNode) {
			return "GlobalParameter";
		}

		if (abstractNode instanceof ClassNode) {
			return "Class";
		}

		if (abstractNode instanceof MethodNode) {
			return "Method";
		}

		if (abstractNode instanceof MethodParameterNode) {
			return "MethodParameter";
		}

		if (abstractNode instanceof ChoiceNode) {
			return "Choice";
		}

		if (abstractNode instanceof ConstraintNode) {
			return "Constraint";
		}

		if (abstractNode instanceof TestSuiteNode) {
			return "TestSuite";
		}

		if (abstractNode instanceof TestCaseNode) {
			return "TestCase";
		}

		return "Node";
	}

}
