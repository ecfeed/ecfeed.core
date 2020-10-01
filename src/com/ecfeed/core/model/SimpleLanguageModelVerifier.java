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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ecfeed.core.utils.Pair;
import com.ecfeed.core.utils.SimpleLanguageHelper;
import com.ecfeed.core.utils.ExtLanguage;
import com.ecfeed.core.utils.ExtLanguageHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;


public class SimpleLanguageModelVerifier { // TODO - SIMPLE-VIEW - unit tests

	private static final String TITLE_NON_UNIQUE_CLASS_NAMES = "Non-unique class names";
	private static final String THE_SAME_CLASSES = "There are some classes in the model that have the same name. Please edit the model before proceeding:";

	public static String checkIsNewClassNameValid(ClassNode classNode, String className) {

		String simpleValueName = SimpleLanguageHelper.convertTextFromJavaToSimpleLanguage(className);

		for (ClassNode node : classNode.getRoot().getClasses()) {
			if (node != classNode) {
				if (SimpleLanguageHelper.convertTextFromJavaToSimpleLanguage(node.getSimpleName()).equals(simpleValueName)) {
					String errorMessage = THE_SAME_CLASSES 
							+ System.lineSeparator() 
							+ System.lineSeparator() 
							+ SimpleLanguageHelper.convertTextFromJavaToSimpleLanguage(node.getSimpleName());

					return TITLE_NON_UNIQUE_CLASS_NAMES + " " + errorMessage;
				}
			}
		}

		return null;
	}

	public static String checkIsModelCompatibleWithSimpleLanguage(RootNode rootNode) {

		String message = checkParameterTypesForSimpleView(rootNode);

		if (message != null) {
			return message;
		}

		message = checkIsGlobalParameterOfRootDuplicated(rootNode);

		if (message != null) {
			return message;
		}

		message = checkClassDuplicated(rootNode);

		if (message != null) {
			return message;
		}

		message = checkIsGlobalParameterOfClassDuplicated(rootNode);

		if (message != null) {
			return message;
		}

		message = checkIsMethodDuplicated(rootNode);

		return message;
	}

	private static String checkParameterTypesForSimpleView(AbstractNode abstractNode) {

		String message = checkIfIsAllowedParameterType(abstractNode);

		if (message != null) {
			return message;
		}

		List<? extends AbstractNode> childNodes = abstractNode.getChildren();

		if (childNodes == null) {
			return null;
		}

		if (childNodes.size() == 0) {
			return null;

		}

		for (AbstractNode childNode : childNodes) {

			message = checkParameterTypesForSimpleView(childNode);

			if (message != null) {
				return message;
			}
		}

		return null;
	}

	private static String checkIfIsAllowedParameterType(AbstractNode abstractNode) {

		if (!(abstractNode instanceof AbstractParameterNode)) {
			return null;
		}

		AbstractParameterNode abstractParameterNode = (AbstractParameterNode) abstractNode;

		String type = abstractParameterNode.getType();

		if (!JavaLanguageHelper.isJavaType(type)) {
			return 
					"Non java types are not allowed in simple view. \nNode: " + 
					ModelHelper.getFullPath(abstractParameterNode, ExtLanguage.JAVA) + ".\n" +
					" Type: " + type + ".";
		}

		return null;
	}

	private static String checkIsGlobalParameterOfRootDuplicated(RootNode rootNode) {

		if (rootNode.getParameters().size() <= 1) {
			return null;
		}

		List<Pair<String, String>> parameterPairs = createParametersForSimpleView(rootNode);

		return checkIsParamNameDuplicated(parameterPairs);
	}

	private static String checkIsParamNameDuplicated(List<Pair<String, String>> parametersForSimpleView) {
		for (int i = 0 ; i < parametersForSimpleView.size() - 1 ; i++) {

			Pair<String, String> currentPair = parametersForSimpleView.get(i);
			Pair<String, String> nextPair = parametersForSimpleView.get(i + 1);

			String currentSimpleName = currentPair.getFirst();
			String nextSimpleName = nextPair.getFirst();

			if (currentSimpleName.equals(nextSimpleName)) {

				String errorMessage = "Model is not compatible with simple view mode because global parameters: " + currentPair.getSecond() + " and " + nextPair.getSecond()
				+ " would have the same name in the simple view mode.";

				return errorMessage;
			}

		}

		return null;
	}

	private static List<Pair<String, String>> createParametersForSimpleView(RootNode rootNode) { 

		List<Pair<String, String>> parameters = new ArrayList<>();

		for (AbstractParameterNode element : rootNode.getParameters()) {

			String simpleName = element.getName();
			String javaName = element.getType() + " " + element.getName();

			Pair<String, String> pair = new Pair<String, String>(simpleName, javaName);
			parameters.add(pair);
		}

		Collections.sort(parameters, new CompareBySimpleSignature());

		return parameters;
	}

	private static String checkClassDuplicated(RootNode rootNode) {

		if (rootNode.getClasses().size() <= 1) {
			return null;
		}

		List<Pair<String, String>> classesForSimpleView = createClassesForSimpleView(rootNode);

		for (int i = 0 ; i < classesForSimpleView.size() - 1 ; i++) {

			Pair<String, String> currentClass = classesForSimpleView.get(i);  
			Pair<String, String> nextClass = classesForSimpleView.get(i + 1);

			String currentSimpleClass = currentClass.getFirst();
			String nextSimpleClass = nextClass.getFirst();

			if (currentSimpleClass.equals(nextSimpleClass)) {

				String errorMessage =
						"Model is not compatible with simple view mode because classes: " 
								+ System.lineSeparator()
								+ System.lineSeparator()
								+ currentClass.getSecond()
								+ System.lineSeparator()
								+ nextClass.getSecond()
								+ System.lineSeparator()
								+ System.lineSeparator()
								+ " would have the same signature in simple mode: "
								+ currentSimpleClass
								+ " .";						

				return errorMessage;
			}		
		}

		return null;
	}

	private static List<Pair<String, String>> createClassesForSimpleView(RootNode rootNode) {

		List<Pair<String, String>> signaturePairs = new ArrayList<>();

		for (ClassNode element : rootNode.getClasses()) {


			String simpleSignature = element.getSimpleName();
			String fullSignature = element.getName();

			Pair<String, String> classSignaturesPair = new Pair<String, String>(simpleSignature, fullSignature);

			signaturePairs.add(classSignaturesPair);
		}

		Collections.sort(signaturePairs, new CompareBySimpleSignature());

		return signaturePairs;
	}  

	private static String checkIsGlobalParameterOfClassDuplicated(RootNode rootNode) {

		String message = "";

		for (ClassNode classNode : rootNode.getClasses()) {

			message = checkIsClassParameterDuplicatedInOneClass(classNode);

			if (message != null) {
				return message;
			}
		}

		return null;
	}

	private static String checkIsClassParameterDuplicatedInOneClass(ClassNode classNode) {

		if (classNode.getParameters().size() <= 1) {
			return null;
		}

		List<Pair<String, String>> parametersForSimpleView = createListOfSimpleViewParameters(classNode);

		return checkIsParamNameDuplicated(parametersForSimpleView);
	}

	private static List<Pair<String, String>> createListOfSimpleViewParameters(ClassNode classNode) {

		List<Pair<String, String>> parameters = new ArrayList<>();

		for (AbstractParameterNode element : classNode.getParameters()) {


			Pair<String, String> pair = new Pair<String, String>(element.getName(), element.getType() + " " + element.getName());

			parameters.add(pair);
		}

		Collections.sort(parameters, new CompareBySimpleSignature());
		return parameters;
	}  

	private static String checkIsMethodDuplicated(RootNode rootNode) {

		String message = "";

		for (ClassNode classNode : rootNode.getClasses()) {

			message = isMethodDuplicatedForClass(classNode);
			if (message != null) {
				return message;
			}
		}

		return null;
	}

	private static String isMethodDuplicatedForClass(ClassNode classNode) {

		List<MethodNode> methods = classNode.getMethods();

		int methodsSize = methods.size();

		if (methodsSize <= 1) {
			return null;
		}

		List<Pair<String, String>> signaturePairs = createSignaturePairs(methods, methodsSize);

		for (int index = 0; index < methodsSize - 1; index++) {

			Pair<String, String> signaturesPair1 = signaturePairs.get(index);
			Pair<String, String> signaturesPair2 = signaturePairs.get(index + 1);

			String simpleSignature1 = signaturesPair1.getFirst();
			String simpleSignature2 = signaturesPair2.getFirst();

			if (simpleSignature1.equals(simpleSignature2)) {

				String errorMessage =
						"Model is not compatible with simple mode because methods: "
								+ System.lineSeparator()
								+ System.lineSeparator()
								+ signaturesPair1.getSecond()
								+ System.lineSeparator()
								+ signaturesPair2.getSecond()
								+ System.lineSeparator()
								+ System.lineSeparator()
								+ " would have the same signature in simple mode: "
								+ simpleSignature1
								+ " .";

				return "Duplicate method signatures. " + errorMessage;
			}
		}

		return null;
	}

	private static List<Pair<String, String>> createSignaturePairs(List<MethodNode> methods, int methodsSize) {

		List<Pair<String, String>> signaturePairs = new ArrayList<Pair<String,String>>();

		for (int index = 0; index < methodsSize; index++) {

			MethodNode methodNode = methods.get(index);

			Pair<String, String> pairOfSignatures = ExtLanguageHelper.createPairOfMethodSignatures(methodNode);

			signaturePairs.add(pairOfSignatures);
		}

		Collections.sort(signaturePairs, new CompareBySimpleSignature());
		return signaturePairs;
	}

	private static class CompareBySimpleSignature implements Comparator<Pair<String,String>> {

		@Override
		public int compare(Pair<String,String> signatures1, Pair<String,String> signatures2) {

			String signature1 = signatures1.getFirst();
			String signature2 = signatures2.getFirst();

			return signature1.compareTo(signature2);
		}
	}

}
