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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ecfeed.core.utils.CommonConstants;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.StringHelper;

public class MethodNodeHelper {

	public static String getName(MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		return AbstractNodeHelper.getName(methodNode, extLanguageManager);
	}

	public static void setName(MethodNode methodNode, String name, IExtLanguageManager extLanguageManager) {

		AbstractNodeHelper.setName(methodNode, name, extLanguageManager);
	}

	public static MethodParameterNode findMethodParameterByName(
			String parameterNameToFindInExtLanguage, 
			MethodNode methodNode, 
			IExtLanguageManager extLanguageManager) {

		List<AbstractParameterNode> methodParameters = methodNode.getParameters();

		for (AbstractParameterNode parameter : methodParameters) {

			MethodParameterNode methodParameterNode = (MethodParameterNode)parameter;

			String parameterNameInExtLanguage = MethodParameterNodeHelper.getName(methodParameterNode, extLanguageManager);

			if (StringHelper.isEqual(parameterNameToFindInExtLanguage, parameterNameInExtLanguage)) {
				return methodParameterNode;
			}
		}
		return null;

	}

	public static List<String> getParameterNames(MethodNode method, IExtLanguageManager extLanguageManager) {

		List<String> result = new ArrayList<String>();

		for(AbstractParameterNode parameter : method.getParameters()){

			MethodParameterNode methodParameterNode = (MethodParameterNode)parameter;

			String name = MethodParameterNodeHelper.getName(methodParameterNode, extLanguageManager);

			result.add(name);
		}

		return result;
	}

	public static List<String> getParameterTypes(MethodNode method, IExtLanguageManager extLanguageManager) {

		List<String> result = new ArrayList<String>();

		for (AbstractParameterNode parameter : method.getParameters()) {

			String type = parameter.getType();

			type = extLanguageManager.convertTypeFromIntrToExtLanguage(type);

			result.add(type);
		}

		return result;
	}

	public static String validateMethodName(String nameInExternalLanguage, IExtLanguageManager extLanguageManager) {

		String errorMessage = extLanguageManager.verifySeparatorsInName(nameInExternalLanguage);

		if (errorMessage != null) {
			return errorMessage;
		}

		String nameInInternalLanguage = extLanguageManager.convertTextFromExtToIntrLanguage(nameInExternalLanguage);

		if (isValid(nameInInternalLanguage)) {
			return null;
		}

		return RegexHelper.createMessageAllowedCharsForMethod(extLanguageManager);
	}

	public static String verifyMethodSignatureIsValid(
			String methodNameInExtLanguage,
			List<String> parameterTypesInExtLanguage,
			IExtLanguageManager extLanguageManager) {

		String errorMessage = MethodNodeHelper.validateMethodName(methodNameInExtLanguage, extLanguageManager);

		if (errorMessage != null) {
			return errorMessage;
		}

		return null;
	}

	public static String createSignature(MethodNode methodNode, boolean isParamNameAdded, IExtLanguageManager extLanguageManager) {

		return MethodNodeHelper.createSignature(
				methodNode,
				isParamNameAdded,
				false, extLanguageManager);
	}

	public static String createSignature(
			MethodNode methodNode,
			boolean isParamNameAdded,
			boolean isExpectedDecorationAdded, 
			IExtLanguageManager extLanguageOfTheResult) {


		final List<Boolean> expectedParametersFlags =
				(isExpectedDecorationAdded ? getExpectedParametersFlags(methodNode.getMethodParameters()) : null);

		List<String> parametersNames = new ArrayList<>();

		if (isParamNameAdded == true) {
			parametersNames = methodNode.getParametersNames();
		} else {
			parametersNames = null;
		}

		String signature =
				createSignatureByIntrLanguage(
						methodNode.getName(),
						methodNode.getParameterTypes(),
						parametersNames,
						expectedParametersFlags,
						extLanguageOfTheResult);

		return signature;
	}

	public static String createLongSignature(MethodNode methodNode, boolean isParamNameAdded, IExtLanguageManager extLanguageManager) {

		String shortSignature = createSignature(methodNode, isParamNameAdded, extLanguageManager);

		return methodNode.getParent().getName() + "." + shortSignature;
	}

	public static String createSignatureWithExpectedDecorations(MethodNode methodNode, boolean isParamNameAdded, IExtLanguageManager extLanguageManager) {

		String signature = createSignature(methodNode, isParamNameAdded, true,  extLanguageManager);

		return signature;
	}

	public static String createSignatureByIntrLanguage(
			String nameInIntrLanguage,
			List<String> parameterTypesInIntrLanguage,
			List<String> parameterNames, 
			List<Boolean> expectedFlags, 
			IExtLanguageManager extLanguageManager) {

		String nameInExtLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(nameInIntrLanguage);

		String signature = new String(nameInExtLanguage) + "(";

		String signaturesOfParameters = 
				createSignaturesOfParametersByIntrLanguage(
						parameterTypesInIntrLanguage, parameterNames, expectedFlags,
						extLanguageManager);

		signature += signaturesOfParameters;

		signature += ")";

		return signature;
	}

	public static String createSignature(
			String methodName,
			List<String> parameterTypes,
			List<String> parameterNames,
			List<Boolean> expectedFlags) {

		String signature = new String(methodName) + "(";

		String signaturesOfParameters =
				createSignaturesOfParameters(
						parameterTypes, parameterNames, expectedFlags);

		signature += signaturesOfParameters;

		signature += ")";

		return signature;
	}

	private static String createSignaturesOfParameters(
			List<String> parameterTypes,
			List<String> parameterNames,
			List<Boolean> expectedFlags) {

		String signature = "";

		for (int paramIndex = 0; paramIndex < parameterTypes.size(); paramIndex++) {

			String parameterType = parameterTypes.get(paramIndex);
			String parameterName = (parameterNames != null ? parameterNames.get(paramIndex) : null);
			Boolean expectedFlag = (expectedFlags != null ? expectedFlags.get(paramIndex) : null);

			String signatureOfOneParameter =
					AbstractParameterNodeHelper.createSignature(
							parameterType, parameterName, expectedFlag);

			signature += signatureOfOneParameter;

			if (paramIndex < parameterTypes.size() - 1) {
				signature += ", ";
			}
		}

		return signature;
	}

	public static String createSignaturesOfParameters(
			MethodNode methodNode,
			IExtLanguageManager extLanguageManager) {

		String signature = "";
		int paramCount = methodNode.getParametersCount();


		for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {

			MethodParameterNode methodParameterNode = methodNode.getMethodParameter(paramIndex);



			String signatureOfOneParameter = 
					AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
							methodParameterNode.getType(),
							methodParameterNode.getName(),
							methodParameterNode.isExpected(), 
							extLanguageManager);

			signature += signatureOfOneParameter;

			if (paramIndex < paramCount - 1) {
				signature += ", ";
			}
		}

		return signature;

	}	

	private static String createSignaturesOfParametersByIntrLanguage(
			List<String> parameterTypesInIntrLanguage,
			List<String> parameterNames,
			List<Boolean> expectedFlags, 
			IExtLanguageManager extLanguageManager) {

		String signature = "";

		for (int paramIndex = 0; paramIndex < parameterTypesInIntrLanguage.size(); paramIndex++) {

			String parameterType = parameterTypesInIntrLanguage.get(paramIndex);
			String parameterName = (parameterNames != null ? parameterNames.get(paramIndex) : null);
			Boolean expectedFlag = (expectedFlags != null ? expectedFlags.get(paramIndex) : null);

			String signatureOfOneParameter = 
					AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
							parameterType,
							parameterName,
							expectedFlag, 
							extLanguageManager);

			signature += signatureOfOneParameter;

			if (paramIndex < parameterTypesInIntrLanguage.size() - 1) {
				signature += ", ";
			}
		}

		return signature;
	}

	private static List<Boolean> getExpectedParametersFlags(List<MethodParameterNode> methodParameters) {

		List<Boolean> expectedFlags = new ArrayList<Boolean>();

		for(MethodParameterNode methodParameter : methodParameters) {

			if (methodParameter.isExpected()) {
				expectedFlags.add(true);
			} else {
				expectedFlags.add(false);
			}

		}

		return expectedFlags;
	}

	private static boolean isValid(String name) {

		if (!JavaLanguageHelper.isValidJavaIdentifier(name)) {
			return false;
		}

		return true;
	}

	public static Set<String> getConstraintNames(MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		Set<String> constraintNames = methodNode.getConstraintsNames();

		//		constraintNames = convertConstraintNamesToExtLanguage(constraintNames, extLanguageManager);

		return constraintNames;
	}


	//	private static Set<String> convertConstraintNamesToExtLanguage(Set<String> constraintNames, IExtLanguageManager extLanguageManager) {
	//
	//		Set<String> result = new HashSet<String>();
	//
	//		for(String constraintName : constraintNames) {
	//
	//			String nameInExtLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(constraintName);
	//			result.add(nameInExtLanguage);
	//		}
	//
	//		return result;
	//	}


	public static MethodParameterNode createNewParameter(
			MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		String name = MethodNodeHelper.generateNewParameterName(methodNode);

		String type = MethodNodeHelper.findNotUsedJavaTypeForParameter(methodNode, extLanguageManager);

		String defaultValue = JavaLanguageHelper.getDefaultValue(type);

		MethodParameterNode parameter = 
				new MethodParameterNode(name, type, defaultValue, false, methodNode.getModelChangeRegistrator());

		return parameter;
	}

	public static String generateNewParameterName(ParametersParentNode parametersParentNode) {

		int i = 0;

		String name = CommonConstants.DEFAULT_NEW_PARAMETER_NAME + i++;

		while(parametersParentNode.findParameter(name) != null) {
			name = CommonConstants.DEFAULT_NEW_PARAMETER_NAME + i++;
		}

		return name;
	}

	public static String findNotUsedJavaTypeForParameter(MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		ClassNode classNode = methodNode.getClassNode();

		String[] typeListInExtLanguage = extLanguageManager.createListListOfSupportedTypes();

		for (String type : typeListInExtLanguage) {
			if (!isNewTypeUsed(type, classNode, methodNode, extLanguageManager)) {
				type = extLanguageManager.convertToMinimalTypeFromExtToIntrLanguage(type);
				return type;
			}
		}

		String userType = findNewUserTypeForJavaLanguage(methodNode, extLanguageManager);

		return userType;
	}

	private static boolean isNewTypeUsed(
			String typeForLastParameter, ClassNode classNode, MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		List<String> parameterTypesInExternalLanguage = MethodNodeHelper.getParameterTypes(methodNode, extLanguageManager);
		parameterTypesInExternalLanguage.add(typeForLastParameter);

		String methodNameInExternalLanguage = MethodNodeHelper.getName(methodNode, extLanguageManager);

		MethodNode foundMethodNode = 
				ClassNodeHelper.findMethodByExtLanguage(
						classNode,
						methodNameInExternalLanguage,
						parameterTypesInExternalLanguage,
						extLanguageManager);

		if (foundMethodNode != null) {
			return true;
		}

		return false;
	}

	public static String findNewUserTypeForJavaLanguage(MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		ClassNode classNode = methodNode.getClassNode();

		String startUserType = extLanguageManager.chooseString(
				CommonConstants.DEFAULT_USER_TYPE_FOR_JAVA, CommonConstants.DEFAULT_USER_TYPE_FOR_SIMPLE);

		String type = startUserType; 
		int i = 0;

		while (true) {

			List<String> newTypes = methodNode.getParameterTypes();
			newTypes.add(type);

			if (classNode.findMethodWithTheSameSignature(methodNode.getName(), newTypes) == null) {
				break;

			} else {
				type = startUserType + i++;
			}
		}

		return type;
	}

	public static List<TestSuiteNode> createGroupingTestSuites(MethodNode method) {

		List<TestSuiteNode> testSuites = method.getTestSuites();

		List<String> testSuiteNames = new ArrayList<>();
		testSuiteNames.addAll(method.getTestCaseNames());

		testSuites.removeIf(e -> !testSuiteNames.contains(e.getSuiteName()));

		TestSuiteNode testSuiteNode;
		for (String testSuiteName : testSuiteNames) {

			Optional<TestSuiteNode> existingNode = method.getTestSuite(testSuiteName);

			if (existingNode.isPresent()) {
				testSuiteNode = existingNode.get();
				testSuiteNode.getTestCaseNodes().clear();
			} else {
				testSuiteNode = new TestSuiteNode();
				testSuiteNode.setSuiteName(testSuiteName);
				testSuiteNode.setParent(method);
				testSuites.add(testSuiteNode);
			}

			Collection<TestCaseNode> testCasesSuite = method.getTestCases(testSuiteName);
			if(testCasesSuite.size() > CommonConstants.MAX_DISPLAYED_TEST_CASES_PER_SUITE) {
				testSuiteNode.setName(testSuiteName + " - Display limit exceeded" );
			} else {
				testSuiteNode.getTestCaseNodes().addAll(testCasesSuite);
				testSuiteNode.setName(testSuiteName);
			}
		}

		testSuites.sort((a, b) -> a.getSuiteName().compareTo(b.getSuiteName()));

		return testSuites;
	}
	
	public static MethodParameterNode findNotUsedExpectedParameter(MethodNode fCurrentMethodNode) {

		List<MethodParameterNode> parameters = fCurrentMethodNode.getMethodParameters();

		for (MethodParameterNode parameterNode : parameters) {

			if (parameterNode.isExpected()) {
				return parameterNode;
			}
		}

		return null;
	}

}
