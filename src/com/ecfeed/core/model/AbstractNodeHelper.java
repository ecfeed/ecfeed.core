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

import java.util.Collection;
import java.util.List;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.StringHelper;

public abstract class AbstractNodeHelper  {

	private static final String PARENT_NODES_DO_NOT_MATCH = "Parent nodes do not match.";

	public static String convertTextFromIntrToExtLanguage(
			String textInIntrLanguage,
			IAbstractNode abstractNode, 
			IExtLanguageManager extLanguageManager) {

		String textInExtLanguage;

		if (isTheSameExtAndIntrLanguage(abstractNode)) {
			textInExtLanguage = textInIntrLanguage;
		} else {
			textInExtLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(textInIntrLanguage);
		}

		return textInExtLanguage;
	}

	public static String convertTextFromExtToIntrLanguage(
			IAbstractNode abstractNode, 
			String textInExtLanguage,
			IExtLanguageManager extLanguageManager) {

		String textInIntrLanguage;

		if (isTheSameExtAndIntrLanguage(abstractNode)) {
			textInIntrLanguage = textInExtLanguage;
		} else {
			textInIntrLanguage = extLanguageManager.convertTextFromExtToIntrLanguage(textInExtLanguage);
		}

		return textInIntrLanguage;
	}

	public static String getName(IAbstractNode abstractNode, IExtLanguageManager extLanguageManager) {

		String nameInIntrLanguage = abstractNode.getName();

		String nameInExtLanguage = convertTextFromIntrToExtLanguage(nameInIntrLanguage, abstractNode, extLanguageManager);

		return nameInExtLanguage;
	}


	public static void setName(IAbstractNode abstractNode, String nameInExtLanguage, IExtLanguageManager extLanguageManager) {

		String nameInIntrLanguage;

		nameInIntrLanguage = convertTextFromExtToIntrLanguage(abstractNode, nameInExtLanguage, extLanguageManager);

		abstractNode.setName(nameInIntrLanguage);
	}

	public static boolean isTheSameExtAndIntrLanguage(IAbstractNode abstractNode) {

		boolean isTheSameExtAndIntrLanguage;

		try {
			isTheSameExtAndIntrLanguage = (boolean) abstractNode.accept(new IsTheSameExtAndIntrLanguageProvider());
		} catch (Exception e) {
			return false;
		}

		return isTheSameExtAndIntrLanguage;
	}

	private static class IsTheSameExtAndIntrLanguageProvider  implements IModelVisitor {

		@Override
		public Object visit(RootNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(TestSuiteNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return true;
		}

		@Override
		public Object visit(BasicParameterNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(CompositeParameterNode node) throws Exception {
			return true;
		}

	}

	public static String getParentName(String parameterExtendedName) {

		String[] dstParamNameParts = StringHelper.splitIntoTokens(parameterExtendedName, ":");

		if (dstParamNameParts.length == 2) {
			return dstParamNameParts[0]; 
		}

		return null;
	}

	public static IAbstractNode findTopNode(IAbstractNode anyNode) {

		IAbstractNode currentNode = anyNode;

		for(;;) {

			IAbstractNode parent = currentNode.getParent();

			if (parent == null) {
				return currentNode;
			}

			currentNode = parent;
		}
	}

	public static boolean nodesAreOfTheSameType(List<IAbstractNode> nodes) {

		Class<?> type = nodes.get(0).getClass();

		for (IAbstractNode node : nodes) {

			if (!node.getClass().equals(type)) { 
				return false;
			}
		}

		return true;
	}

	public static String getShortClassName(IAbstractNode abstractNode) {

		String fullClassName = abstractNode.getClass().getName();
		String shortClassName = StringHelper.getLastToken(fullClassName, ".");
		return shortClassName;
	}

	public static boolean parentIsTheSame(IAbstractNode child, IAbstractNode expectedParent) {

		IAbstractNode parent = child.getParent();

		if (!parent.equals(expectedParent)) {
			return false;
		}

		return true;
	}

	public static String checkIfCanAddChildrenToParent(
			Collection<IAbstractNode> childrenToAdd, 
			IAbstractNode parentNode, 
			boolean displayErrorDialog) {

		for (IAbstractNode child : childrenToAdd) {

			if (!parentNode.canAddChild(child)) {

				String className = AbstractNodeHelper.getShortClassName(child);

				return ("Cannot add " + className + " to parent: " + parentNode.getName());
			}
		}

		return null;
	}

	public static void compareParents(
			AbstractNode node1, AbstractNode parent1, 
			AbstractNode node2, AbstractNode parent2) {

		if (node1.getParent() == null && node2.getParent() == null) {
			return;
		}

		if (node1.getParent() == null && node2.getParent() != null) {
			ExceptionHelper.reportRuntimeException(PARENT_NODES_DO_NOT_MATCH);
		}

		if (node1.getParent() != null && node2.getParent() == null) {
			ExceptionHelper.reportRuntimeException(PARENT_NODES_DO_NOT_MATCH);
		}

		if (node1.getParent() == parent1 && node2.getParent() != parent2) {
			ExceptionHelper.reportRuntimeException(PARENT_NODES_DO_NOT_MATCH);
		}

		if (node1.getParent() != parent1 && node2.getParent() == parent2) {
			ExceptionHelper.reportRuntimeException(PARENT_NODES_DO_NOT_MATCH);
		}
	}

	public static void compareSizes(
			Collection<? extends IAbstractNode> collection1, 
			Collection<? extends IAbstractNode> collection2, 
			String errorMessage) {

		int size1 = collection1.size();

		int size2 = collection2.size();

		if (size1 != size2) {
			ExceptionHelper.reportRuntimeException(errorMessage + " " + collection1.size() + " vs " + collection2.size() + ".");
		}
	}
	
}
