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

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.RegexHelper;

public class ModelHelper {

	public static String getSimpleName(String qualifiedName) {
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

		AbstractNode node = startNode;

		for (int cnt = 0;  ; cnt++) {
			
			if (cnt >= 1000) {
				ExceptionHelper.reportRuntimeException("Model too deep or recursive. Cannot find root.");
			}

			AbstractNode parent = node.getParent();

			if (parent == null) {

				if (node instanceof RootNode) {
					return (RootNode)node;
				}

				return null;
			}

			node = parent;
		}

	}

}
