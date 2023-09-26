///*******************************************************************************
// *
// * Copyright (c) 2016 ecFeed AS.                                                
// * All rights reserved. This program and the accompanying materials              
// * are made available under the terms of the Eclipse Public License v1.0         
// * which accompanies this distribution, and is available at                      
// * http://www.eclipse.org/legal/epl-v10.html 
// *  
// *******************************************************************************/
//
//package com.ecfeed.core.model;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//import com.ecfeed.core.utils.*;
//
//
//public class SimpleLanguageModelVerifier {
//
//	public static String checkIsModelCompatibleWithSimpleLanguage(RootNode rootNode) {
//
//		String message = checkNodeNames(rootNode);
//
//		if (message != null) {
//			return message;
//		}
//
//		message = checkParameterTypesForSimpleView(rootNode);
//
//		if (message != null) {
//			return message;
//		}
//
//		//		message = checkIsGlobalParameterOfRootDuplicated(rootNode);
//		//
//		//		if (message != null) {
//		//			return message;
//		//		}
//
//		message = checkClassDuplicated(rootNode);
//
//		if (message != null) {
//			return message;
//		}
//
//		//		message = checkIsGlobalParameterOfClassDuplicated(rootNode);
//		//
//		//		if (message != null) {
//		//			return message;
//		//		}
//
//		message = checkIsMethodDuplicated(rootNode);
//
//		return message;
//	}
//
//	private static String checkParameterTypesForSimpleView(IAbstractNode abstractNode) {
//
//		List<IAbstractNode> childNodes = abstractNode.getChildren();
//
//		if (childNodes == null) {
//			return null;
//		}
//
//		if (childNodes.size() == 0) {
//			return null;
//
//		}
//
//		String message = "";
//
//		for (IAbstractNode childNode : childNodes) {
//
//			message = checkParameterTypesForSimpleView(childNode);
//
//			if (message != null) {
//				return message;
//			}
//		}
//
//		return null;
//	}
//
//	//	private static String checkIsGlobalParameterOfRootDuplicated(RootNode rootNode) {
//	//
//	//		if (rootNode.getParameters().size() <= 1) {
//	//			return null;
//	//		}
//	//
//	//		List<Pair<String, String>> parameterPairs = createParametersForSimpleView(rootNode);
//	//
//	//		return checkIsParamNameDuplicated(parameterPairs);
//	//	}
//
//	//	private static String checkIsParamNameDuplicated(List<Pair<String, String>> parametersForSimpleView) {
//	//		
//	//		for (int i = 0 ; i < parametersForSimpleView.size() - 1 ; i++) {
//	//
//	//			Pair<String, String> currentPair = parametersForSimpleView.get(i);
//	//			Pair<String, String> nextPair = parametersForSimpleView.get(i + 1);
//	//
//	//			String currentSimpleName = currentPair.getFirst();
//	//			String nextSimpleName = nextPair.getFirst();
//	//
//	//			
//	//			
//	//			if (currentSimpleName.equals(nextSimpleName)) {
//	//
//	//				// this function can be  called from simple (rename) and java view - adjust message acccordingly - node names are always in java convention
//	//				String errorMessage = "Model is not compatible with simple view mode because global parameters: " + 
//	//				currentPair.getSecond() + " and " + nextPair.getSecond()
//	//				+ " would have the same name in the simple view mode.";
//	//
//	//				return errorMessage;
//	//			}
//	//
//	//		}
//	//
//	//		return null;
//	//	}
//
//	//	private static List<Pair<String, String>> createParametersForSimpleView(RootNode rootNode) { 
//	//
//	//		List<Pair<String, String>> parameters = new ArrayList<>();
//	//
//	//		for (BasicParameterNode element : rootNode.getParameters()) {
//	//
//	//			String simpleName = AbstractNodeHelper.getName(element, new ExtLanguageManagerForSimple());
//	//			String javaName = element.getType() + " " + element.getName();
//	//
//	//			Pair<String, String> pair = new Pair<String, String>(simpleName, javaName);
//	//			parameters.add(pair);
//	//		}
//	//
//	//		Collections.sort(parameters, new CompareBySimpleSignature());
//	//
//	//		return parameters;
//	//	}
//
//	private static String checkClassDuplicated(RootNode rootNode) {
//
//		if (rootNode.getClasses().size() <= 1) {
//			return null;
//		}
//
//		List<Pair<String, String>> classesForSimpleView = createClassesForSimpleView(rootNode);
//
//		for (int i = 0 ; i < classesForSimpleView.size() - 1 ; i++) {
//
//			Pair<String, String> currentClass = classesForSimpleView.get(i);  
//			Pair<String, String> nextClass = classesForSimpleView.get(i + 1);
//
//			String currentSimpleClass = currentClass.getFirst();
//			String nextSimpleClass = nextClass.getFirst();
//
//			if (currentSimpleClass.equals(nextSimpleClass)) {
//
//				String errorMessage =
//						"Model is not compatible with simple view mode because classes: " 
//								+ System.lineSeparator()
//								+ System.lineSeparator()
//								+ currentClass.getSecond()
//								+ System.lineSeparator()
//								+ nextClass.getSecond()
//								+ System.lineSeparator()
//								+ System.lineSeparator()
//								+ " would have the same signature in simple mode: "
//								+ currentSimpleClass
//								+ " .";						
//
//				return errorMessage;
//			}		
//		}
//
//		return null;
//	}
//
//	private static List<Pair<String, String>> createClassesForSimpleView(RootNode rootNode) {
//
//		List<Pair<String, String>> signaturePairs = new ArrayList<>();
//
//		for (ClassNode element : rootNode.getClasses()) {
//
//
//			String simpleSignature = element.getNonQualifiedName();
//			String fullSignature = element.getName();
//
//			Pair<String, String> classSignaturesPair = new Pair<String, String>(simpleSignature, fullSignature);
//
//			signaturePairs.add(classSignaturesPair);
//		}
//
//		Collections.sort(signaturePairs, new CompareBySimpleSignature());
//
//		return signaturePairs;
//	}  
//
//	//	private static String checkIsGlobalParameterOfClassDuplicated(RootNode rootNode) {
//	//
//	//		String message = "";
//	//
//	//		for (ClassNode classNode : rootNode.getClasses()) {
//	//
//	//			message = checkIsClassParameterDuplicatedInOneClass(classNode);
//	//
//	//			if (message != null) {
//	//				return message;
//	//			}
//	//		}
//	//
//	//		return null;
//	//	}
//
//	//	private static String checkIsClassParameterDuplicatedInOneClass(ClassNode classNode) {
//	//
//	//		if (classNode.getParameters().size() <= 1) {
//	//			return null;
//	//		}
//	//
//	//		List<Pair<String, String>> parametersForSimpleView = createListOfSimpleViewParameters(classNode);
//	//
//	//		return checkIsParamNameDuplicated(parametersForSimpleView);
//	//	}
//
//	//	private static List<Pair<String, String>> createListOfSimpleViewParameters(ClassNode classNode) {
//	//
//	//		List<Pair<String, String>> parameters = new ArrayList<>();
//	//
//	//		for (BasicParameterNode element : classNode.getParameters()) {
//	//
//	//
//	//			Pair<String, String> pair = new Pair<String, String>(element.getName(), element.getType() + " " + element.getName());
//	//
//	//			parameters.add(pair);
//	//		}
//	//
//	//		Collections.sort(parameters, new CompareBySimpleSignature());
//	//		return parameters;
//	//	}  
//
//	private static String checkIsMethodDuplicated(RootNode rootNode) {
//
//		String message = "";
//
//		for (ClassNode classNode : rootNode.getClasses()) {
//
//			message = isMethodDuplicatedForClass(classNode);
//			if (message != null) {
//				return message;
//			}
//		}
//
//		return null;
//	}
//
//	private static String isMethodDuplicatedForClass(ClassNode classNode) {
//
//		List<MethodNode> methods = classNode.getMethods();
//
//		int methodsSize = methods.size();
//
//		if (methodsSize <= 1) {
//			return null;
//		}
//
//		List<Pair<String, String>> signaturePairs = createSignaturePairs(methods, methodsSize);
//
//		for (int index = 0; index < methodsSize - 1; index++) {
//
//			Pair<String, String> signaturesPair1 = signaturePairs.get(index);
//			Pair<String, String> signaturesPair2 = signaturePairs.get(index + 1);
//
//			String simpleSignature1 = signaturesPair1.getFirst();
//			String simpleSignature2 = signaturesPair2.getFirst();
//
//			if (simpleSignature1.equals(simpleSignature2)) {
//
//				String errorMessage =
//						"Model is not compatible with simple mode because methods: "
//								+ System.lineSeparator()
//								+ System.lineSeparator()
//								+ signaturesPair1.getSecond()
//								+ System.lineSeparator()
//								+ signaturesPair2.getSecond()
//								+ System.lineSeparator()
//								+ System.lineSeparator()
//								+ " would have the same signature in simple mode: "
//								+ simpleSignature1
//								+ " .";
//
//				return "Duplicate method signatures. " + errorMessage;
//			}
//		}
//
//		return null;
//	}
//
//	private static List<Pair<String, String>> createSignaturePairs(List<MethodNode> methods, int methodsSize) {
//
//		List<Pair<String, String>> signaturePairs = new ArrayList<Pair<String,String>>();
//
//		for (int index = 0; index < methodsSize; index++) {
//
//			MethodNode methodNode = methods.get(index);
//
//			Pair<String, String> pairOfSignatures = createPairOfMethodSignatures(methodNode);
//
//			signaturePairs.add(pairOfSignatures);
//		}
//
//		Collections.sort(signaturePairs, new CompareBySimpleSignature());
//		return signaturePairs;
//	}
//
//	private static Pair<String, String> createPairOfMethodSignatures(MethodNode methodNode) {  
//
//		Pair<String,String> pairOfSignatures = 
//				new Pair<String, String>(
//						MethodNodeHelper.createSignature(methodNode, false, true, new ExtLanguageManagerForSimple()),
//						MethodNodeHelper.createSignature(methodNode, false, true, new ExtLanguageManagerForJava()));
//
//
//		return pairOfSignatures;
//	}
//
//	private static class CompareBySimpleSignature implements Comparator<Pair<String,String>> {
//
//		@Override
//		public int compare(Pair<String,String> signatures1, Pair<String,String> signatures2) {
//
//			String signature1 = signatures1.getFirst();
//			String signature2 = signatures2.getFirst();
//
//			return signature1.compareTo(signature2);
//		}
//	}
//
//	private static String checkNodeNames(IAbstractNode abstractNode) {
//
//		String name = abstractNode.getNonQualifiedName();
//		String errorMessage = JavaLanguageHelper.checkCompatibilityWithSimpleMode(name);
//
//		if (errorMessage != null) {
//
//			String decoratedMessage = createMessageWithNodeName(abstractNode, errorMessage);
//
//			return decoratedMessage;
//		}
//
//		List<IAbstractNode> children = abstractNode.getChildren();
//
//		for (IAbstractNode child : children) {
//
//			errorMessage = checkNodeNames(child);
//
//			if (errorMessage != null) {
//				return errorMessage;
//			}
//		}
//
//		return null;
//	}
//
//	public static String createMessageWithNodeName(IAbstractNode abstractNode, String errorMessage) {
//
//		String fullPath = AbstractNodeHelper.getFullPath(abstractNode, new ExtLanguageManagerForJava());
//
//		String nodeTypeName = AbstractNodeHelper.getNodeTypeName(abstractNode);
//
//		String message = errorMessage + "\n" + nodeTypeName	+ ": " + fullPath + ".";
//
//		return message;
//	}
//
//}
