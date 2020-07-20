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
import com.ecfeed.core.utils.SimpleTypeHelper;


public class ViewModeModelVerifier {

	private static final String TITLE_NON_UNIQUE_CLASS_NAMES = "Non-unique class names";
	private static final String TITLE_AMBIGUOUS_GLOBAL_PARAMETER_NAMES = "Ambiguous global parameter names";	

	private static final String GLOBAL_PARAMETERS_WITH_THE_SAME_NAME = "There are some global parameters in the model that would have the same name in the simple view mode. Please edit the model before proceeding:";
	private static final String THE_SAME_CLASSES = "There are some classes in the model that have the same name. Please edit the model before proceeding:";

	public static String isNewClassNameValid(ClassNode classNode, String className) {

		String simpleValueName = SimpleTypeHelper.parseToSimpleView(className);

		for (ClassNode node : classNode.getRoot().getClasses()) {
			if (node != classNode) {
				if (SimpleTypeHelper.parseToSimpleView(node.getSimpleName()).equals(simpleValueName)) {
					String errorMessage = THE_SAME_CLASSES 
							+ System.lineSeparator() 
							+ System.lineSeparator() 
							+ SimpleTypeHelper.parseToSimpleView(node.getSimpleName());
					
					return TITLE_NON_UNIQUE_CLASS_NAMES + " " + errorMessage;
				}
			}
		}

		return null;
	}

	public static String checkIsModelCompatibleWithJavaMode(RootNode rootNode) {
		
		for (ClassNode element : rootNode.getClasses()) {
			
			if (!ClassNodeHelper.classNameCompliesWithJavaNamingRules(element.getFullName())) {
				return "Class name cannot be converted to java identifier. Class: " + element.getFullNamePath() + ".";
			}
		}
		
		return null; // TODO SIMPLE-VIEW
	}
	
	public static String checkIsModelCompatibleWithSimpleMode(RootNode rootNode) {

		String message = checkIsGlobalParameterOfRootDuplicated(rootNode);  // TODO SIMPLE-VIEW when global parameter is duplicated ? names are unique already in model (editor checks this)
		
		if (message != null) {
			return message;
		}
		
		message = checkClassDuplicated(rootNode);
		
		if (message != null) {
			return message;
		}

		message = checkIsGlobalParameterOfClassDuplicated(rootNode); // TODO SIMPLE-VIEW when global parameter is duplicated ? names are unique already in model (editor checks this)
		
		if (message != null) {
			return message;
		}

		message = checkIsMethodDuplicated(rootNode);
		
		return message;
	}

	private static String checkIsGlobalParameterOfRootDuplicated(RootNode rootNode) {

		if (rootNode.getParameters().size() <= 1) {
			return null;
		}

		List<String> parametersForSimpleView = createParametersForSimpleView(rootNode);

		for (int i = 0 ; i < parametersForSimpleView.size() - 1 ; i++) {

			if (parametersForSimpleView.get(i).equals(parametersForSimpleView.get(i + 1))) {

				String errorMessage = GLOBAL_PARAMETERS_WITH_THE_SAME_NAME 
						+ System.lineSeparator() 
						+ System.lineSeparator() 
						+ parametersForSimpleView.get(i);

				return TITLE_AMBIGUOUS_GLOBAL_PARAMETER_NAMES + " " + errorMessage;
			}

		}

		return null;
	}

	private static List<String> createParametersForSimpleView(RootNode rootNode) {

		List<String> parameters = new ArrayList<>();

		for (AbstractParameterNode element : rootNode.getParameters()) {
			parameters.add(SimpleTypeHelper.parseToSimpleView(element.getFullName())); // TODO SIMPLE-VIEW what parseToSimpleView does ?
		}

		Collections.sort(parameters);
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
						"Cannot switch to simple mode because classes: "
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

				return "Duplicate class signatures. " + errorMessage;
			}		
		}

		return null;
	}

	private static List<Pair<String, String>> createClassesForSimpleView(RootNode rootNode) {

		List<Pair<String, String>> signaturePairs = new ArrayList<>();

		for (ClassNode element : rootNode.getClasses()) {


			String simpleSignature = element.getSimpleName();
			String fullSignature = element.getFullName();

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

		List<String> parametersForSimpleView = createListOfSimpleViewParameters(classNode);

		for (int i = 0 ; i < parametersForSimpleView.size() - 1 ; i++) {

			if (parametersForSimpleView.get(i).equals(parametersForSimpleView.get(i + 1))) {

				String errorMessage = GLOBAL_PARAMETERS_WITH_THE_SAME_NAME 
						+ System.lineSeparator() 
						+ System.lineSeparator() 
						+ parametersForSimpleView.get(i);

				return TITLE_AMBIGUOUS_GLOBAL_PARAMETER_NAMES + " " + errorMessage;
			}
		}

		return null;
	}

	private static List<String> createListOfSimpleViewParameters(ClassNode classNode) {

		List<String> parameters = new ArrayList<>();

		for (AbstractParameterNode element : classNode.getParameters()) {
			parameters.add(SimpleTypeHelper.parseToSimpleView(element.getFullName()));
		}

		Collections.sort(parameters);
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

		// TODO - SIMPLE-VIEW what if there are methods method1(int arg1) and method1(byte arg2) ?

		List<Pair<String, String>> signaturePairs = createSignaturePairs(methods, methodsSize);

		for (int index = 0; index < methodsSize - 1; index++) {

			Pair<String, String> signaturesPair1 = signaturePairs.get(index);
			Pair<String, String> signaturesPair2 = signaturePairs.get(index + 1);

			String simpleSignature1 = signaturesPair1.getFirst();
			String simpleSignature2 = signaturesPair2.getFirst();

			if (simpleSignature1.equals(simpleSignature2)) {

				String errorMessage =
						"Cannot switch to simple mode because methods: "
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

			Pair<String,String> pairOfSignatures = 
					new Pair<String, String>(
							SimpleTypeHelper.createMethodSimpleSignature(methodNode), 
							methodNode.getShortSignature());

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

	//	private static boolean isMethodDuplicatedForClass(ClassNode classNode) {
	//		
	//		if (classNode.getMethods().size() <= 1) {
	//			return false;
	//		}
	//		
	//		List<MethodNode> methods = classNode.getMethods();
	//		List<String> methodsForSimpleView = createMethodsForSimpleView(methods);
	//		
	//		for (int i = 0 ; i < methodsForSimpleView.size() - 1 ; i++) {
	//				
	//			String currentMethodSV = methodsForSimpleView.get(i);
	//			String nextMethodSV = methodsForSimpleView.get(i + 1);
	//			
	//			if (!currentMethodSV.equals(nextMethodSV)) {
	//				continue;
	//			}
	//			
	//			MethodNode currentMethod = methods.get(i);
	//			MethodNode nextMethod = methods.get(i + 1);
	//			
	//			if (isMethodParameterDuplicated2(currentMethod, nextMethod)) {
	//				String errorMessage = Messages.DIALOG_DUPLICATE_METHOD_NAME_SWITCH_MESSAGE 
	//						+ System.lineSeparator() 
	//						+ System.lineSeparator() 
	//						+ MethodNodeHelper.getSimpleName(currentMethod)
	//						+ System.lineSeparator() 
	//						+ MethodNodeHelper.getSimpleName(nextMethod);
	//				
	//				ErrorDialog.open(Messages.DIALOG_DUPLICATE_METHOD_NAME_TITLE, errorMessage);
	//				return true;
	//			}
	//		}
	//		
	//		return false;
	//	}

	//	private static List<String> createMethodsForSimpleView(List<MethodNode> methods) {
	//		
	//		List<String> methodsForSimpleView = new ArrayList<>();
	//		
	//		for (MethodNode element : methods) {
	//			methodsForSimpleView.add(SimpleTypeHelper.parseToSimpleView(element.getFullName()));
	//		}
	//		
	//		// Collections.sort(methodsForSimpleView);
	//		return methodsForSimpleView;
	//	}

	//	private static boolean isMethodParameterDuplicated2(MethodNode method1, MethodNode method2) {
	//		
	//		if (method1.getParameters().size() != method2.getParameters().size()) {
	//			return false;
	//		}
	//		
	//		List<String> serializedParams1 = createSerializedParameters(method1.getParameters());
	//		List<String> serializedParams2 = createSerializedParameters(method2.getParameters());
	//		
	//		for (int index = 0 ; index < serializedParams1.size() ; index++) {
	//			
	//			
	//			
	//		}


	//		Collections.sort(parametersMethod1, new CompareMethodParameter());
	//		Collections.sort(parametersMethod2, new CompareMethodParameter());
	//		
	//		for (int i = 0 ; i < parametersMethod1.size() ; i++) {
	//			AbstractParameterNode parameterNode1 = (AbstractParameterNode) parametersMethod1.get(i).makeClone();
	//			AbstractParameterNode parameterNode2 = (AbstractParameterNode) parametersMethod2.get(i).makeClone();
	//			SimpleTypeHelper.convertTypeJavaToSimple(parameterNode1);
	//			SimpleTypeHelper.convertTypeJavaToSimple(parameterNode2);
	//			
	//			if (parameterNode1.getType().equals(parameterNode2.getType())) {
	//				return true;
	//			}
	//		}
	//		
	//		return false;
	//	}

	//	private static List<String> createSerializedParameters(List<AbstractParameterNode> parameters) {
	//		
	//		List<String> serializedParameters = new ArrayList<String>();
	//		
	//		String serializedParameter;
	//		
	//		for (int index = 0; index < parameters.size(); index++) {
	//		
	//			AbstractParameterNode abstractParameterNode = parameters.get(index);
	//			
	//			serializedParameter = 
	//					abstractParameterNode.getFullName() + 
	//					SimpleTypeHelper.getSimpleType(abstractParameterNode.getType());
	//			
	//			serializedParameters.add(serializedParameter);
	//		}
	//		
	//		Collections.sort(serializedParameters);
	//		
	//		return serializedParameters;
	//	}

	//	private static class CompareMethodParameter implements Comparator<AbstractParameterNode> {
	//
	//		@Override
	//		public int compare(AbstractParameterNode parameter1, AbstractParameterNode parameter2) {
	//			return parameter1.getMyIndex() - parameter2.getMyIndex();
	//		}
	//		
	//	}

}
