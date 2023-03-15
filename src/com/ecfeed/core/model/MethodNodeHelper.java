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

import com.ecfeed.core.model.AbstractParameterSignatureHelper.Decorations;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.ExtendedName;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.TypeIncluded;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.utils.CommonConstants;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.RegexHelper;

public class MethodNodeHelper {

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

	public static String verifyMethodNameIsValid(
			String methodNameInExtLanguage,
			IExtLanguageManager extLanguageManager) {

		String errorMessage = validateMethodName(methodNameInExtLanguage, extLanguageManager);

		if (errorMessage != null) {
			return errorMessage;
		}

		return null;
	}

	private static boolean isValid(String name) {

		if (!JavaLanguageHelper.isValidJavaIdentifier(name)) {
			return false;
		}

		return true;
	}

	public static String createSignatureNewStandard(MethodNode methodNode, IExtLanguageManager extLanguageManager) {
		
		String nameInExtLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(methodNode.getName());

		String signature = new String(nameInExtLanguage) + "(";

		String signaturesOfParameters = 
				createSignaturesOfParametersNewStandard(
						methodNode,
						extLanguageManager);

		signature += signaturesOfParameters;

		signature += ")";

		return signature;
		
	}
	
	private static String createSignaturesOfParametersNewStandard(
			MethodNode methodNode,
			IExtLanguageManager extLanguageManager) {

		String signature = "";
		
		List<AbstractParameterNode> parameters = methodNode.getParameters();

		int parametersSize = parameters.size();
		for (int paramIndex = 0; paramIndex < parametersSize; paramIndex++) {

			AbstractParameterNode parameter = parameters.get(paramIndex);

			String signatureOfOneParameter = 
					AbstractParameterSignatureHelper.createSignatureNewStandard(
							parameter, 
							ExtendedName.NAME_ONLY, Decorations.YES, TypeIncluded.YES, 
							extLanguageManager);
							

			signature += signatureOfOneParameter;

			if (paramIndex < parametersSize - 1) {
				signature += ", ";
			}
		}

		return signature;
	}
	
	public static String createSignature(MethodNode methodNode, boolean isParamNameAdded, IExtLanguageManager extLanguageManager) {

		return createSignature( 
				methodNode,
				isParamNameAdded,
				false, extLanguageManager);
	}

	public static String createLongSignature(MethodNode methodNode, boolean isParamNameAdded, IExtLanguageManager extLanguageManager) {

		String shortSignature = createSignature(methodNode, isParamNameAdded, extLanguageManager);

		IAbstractNode parent = methodNode.getParent();

		if (parent == null) {

			return shortSignature;
		}

		return parent.getName() + "." + shortSignature;
	}

	public static String createSignature(
			MethodNode methodNode,
			boolean isParamNameAdded,
			boolean isExpectedDecorationAdded, 
			IExtLanguageManager extLanguageOfTheResult) {


		final List<Boolean> expectedParametersFlags =
				(isExpectedDecorationAdded ? getExpectedParametersFlags(methodNode.getParameters()) : null);

		List<String> parametersNames = new ArrayList<>();

		if (isParamNameAdded == true) {
			parametersNames = methodNode.getParametersNames();
		} else {
			parametersNames = null;
		}

		List<String> parameterTypes = methodNode.getParameterTypes();

		String methodName = methodNode.getName();

		String signature =
				createSignatureByIntrLanguage(
						methodName,
						parameterTypes,
						parametersNames,
						expectedParametersFlags,
						extLanguageOfTheResult);

		return signature;
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
			List<Boolean> expectedFlags,
			IExtLanguageManager extLanguageManager) {

		String signature = new String(methodName) + "(";

		String signaturesOfParameters =
				createSignaturesOfParameters(
						parameterTypes, parameterNames, expectedFlags, extLanguageManager);

		signature += signaturesOfParameters;

		signature += ")";

		return signature;
	}

	private static String createSignaturesOfParameters(
			List<String> parameterTypes,
			List<String> parameterNames,
			List<Boolean> expectedFlags,
			IExtLanguageManager extLanguageManager) {

		String signature = "";

		for (int paramIndex = 0; paramIndex < parameterTypes.size(); paramIndex++) {

			String parameterType = parameterTypes.get(paramIndex);
			String parameterName = (parameterNames != null ? parameterNames.get(paramIndex) : null);
			Boolean expectedFlag = (expectedFlags != null ? expectedFlags.get(paramIndex) : null);

			String signatureOfOneParameter =
					AbstractParameterSignatureHelper.createSignature(
							parameterType, parameterName, expectedFlag, extLanguageManager);

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

			AbstractParameterNode methodParameterNode = methodNode.getMethodParameter(paramIndex);

			String signatureOfOneParameter = "";

			if (methodParameterNode instanceof BasicParameterNode) {

				BasicParameterNode basicParameterNode = (BasicParameterNode) methodParameterNode;

				signatureOfOneParameter = 
						AbstractParameterSignatureHelper.createSignatureOfOneParameterByIntrLanguage(
								basicParameterNode.getType(),
								basicParameterNode.getName(),
								basicParameterNode.isExpected(), 
								extLanguageManager);
			} else {

				CompositeParameterNode compositeParameterNode = (CompositeParameterNode) methodParameterNode;

				signatureOfOneParameter = 
						AbstractParameterSignatureHelper.createSignature(compositeParameterNode, extLanguageManager);
			}

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
					AbstractParameterSignatureHelper.createSignatureOfOneParameterByIntrLanguage(
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

	private static List<Boolean> getExpectedParametersFlags(List<AbstractParameterNode> methodParameters) {

		List<Boolean> expectedFlags = new ArrayList<Boolean>();

		for (AbstractParameterNode abstractParameterNode : methodParameters) {

			if (!(abstractParameterNode instanceof BasicParameterNode)) {
				continue;
			}

			BasicParameterNode basicParameterNode = (BasicParameterNode) abstractParameterNode;

			if (basicParameterNode.isExpected()) {
				expectedFlags.add(true);
			} else {
				expectedFlags.add(false);
			}
		}

		return expectedFlags;
	}

	//	public static List<TestSuiteNode> createGroupingTestSuites(MethodNode method) {
	//
	//		List<TestSuiteNode> testSuites = method.getTestSuites();
	//
	//		List<String> testSuiteNames = new ArrayList<>();
	//		testSuiteNames.addAll(method.getTestCaseNames());
	//
	//		testSuites.removeIf(e -> !testSuiteNames.contains(e.getSuiteName()));
	//
	//		TestSuiteNode testSuiteNode;
	//		for (String testSuiteName : testSuiteNames) {
	//
	//			Optional<TestSuiteNode> existingNode = method.getTestSuite(testSuiteName);
	//
	//			if (existingNode.isPresent()) {
	//				testSuiteNode = existingNode.get();
	//				testSuiteNode.getTestCaseNodes().clear();
	//			} else {
	//				testSuiteNode = new TestSuiteNode();
	//				testSuiteNode.setSuiteName(testSuiteName);
	//				testSuiteNode.setParent(method);
	//				testSuites.add(testSuiteNode);
	//			}
	//
	//			Collection<TestCaseNode> testCasesSuite = method.getTestCases(testSuiteName);
	//			if(testCasesSuite.size() > CommonConstants.MAX_DISPLAYED_TEST_CASES_PER_SUITE) {
	//				testSuiteNode.setName(testSuiteName);
	//				testSuiteNode.setDisplayLimitExceededFlag(true);
	//			} else {
	//				testSuiteNode.getTestCaseNodes().addAll(testCasesSuite);
	//				testSuiteNode.setName(testSuiteName);
	//				testSuiteNode.setDisplayLimitExceededFlag(false);
	//			}
	//		}
	//
	//		testSuites.sort((a, b) -> a.getSuiteName().compareTo(b.getSuiteName()));
	//
	//		return testSuites;
	//	}

	//	public static String findNotUsedJavaTypeForParameter(
	//			MethodNode methodNode, IExtLanguageManager extLanguageManager) {
	//
	//		ClassNode classNode = methodNode.getClassNode();
	//
	//		String[] typeListInExtLanguage = extLanguageManager.createListListOfSupportedTypes();
	//
	//		for (String type : typeListInExtLanguage) {
	//			if (!isNewTypeUsed(type, classNode, methodNode, extLanguageManager)) {
	//				type = extLanguageManager.convertToMinimalTypeFromExtToIntrLanguage(type);
	//				return type;
	//			}
	//		}
	//
	//		String userType = findNewUserTypeForJavaLanguage(methodNode, extLanguageManager);
	//
	//		return userType;
	//	}

	//	private static boolean isNewTypeUsed(
	//			String typeForLastParameter,
	//			ClassNode classNode,
	//			MethodNode methodNode,
	//			IExtLanguageManager extLanguageManager) {
	//
	////		List<String> parameterTypesInExternalLanguage = ParametersParentNodeHelper.getParameterTypes(methodNode, extLanguageManager);
	////		parameterTypesInExternalLanguage.add(typeForLastParameter);
	//
	//		String methodNameInExternalLanguage = AbstractNodeHelper.getName(methodNode, extLanguageManager);
	//
	//		MethodNode foundMethodNode =
	//				ClassNodeHelper.findMethodByExtLanguage(
	//						classNode,
	//						methodNameInExternalLanguage,
	//						extLanguageManager);
	//
	//		if (foundMethodNode != null) {
	//			return true;
	//		}
	//
	//		return false;
	//	}

	public static String findNewUserTypeForJavaLanguage(
			MethodNode methodNode, 
			IExtLanguageManager extLanguageManager) {

		ClassNode classNode = methodNode.getClassNode();

		String startUserType = extLanguageManager.chooseString(
				CommonConstants.DEFAULT_USER_TYPE_FOR_JAVA, CommonConstants.DEFAULT_USER_TYPE_FOR_SIMPLE);

		String type = startUserType; 
		int i = 0;

		while (true) {

			List<String> newTypes = methodNode.getParameterTypes();
			newTypes.add(type);

			if (classNode.findMethodWithTheSameName(methodNode.getName()) == null) {
				break;

			} else {
				type = startUserType + i++;
			}
		}

		return type;
	}

	public static MethodNode findMethodNode(IAbstractNode anyNode) {
		IAbstractNode parent = anyNode;

		while (parent != null) {

			if (parent instanceof MethodNode) {
				return (MethodNode) parent;
			}

			parent = parent.getParent();
		}

		return null;
	}

	public static boolean methodNodeMentionsBasicParameter(
			MethodNode methodNode,
			BasicParameterNode basicParameterNode) {

		List<AbstractParameterNode> abstractParameterNodes = methodNode.getParameters();

		for (AbstractParameterNode abstractParameterNode : abstractParameterNodes) {

			if (AbstractParameterNodeHelper.parameterMentionsBasicParameter(abstractParameterNode, basicParameterNode)) {
				return true;
			}
		}

		return false;
	}

	public static CompositeParameterNode addCompositeParameter( // TODO MO-RE rename addNew ... because creating
			MethodNode methodNode, String name, IModelChangeRegistrator modelChangeRegistrator) {

		CompositeParameterNode compositeParameterNode = new CompositeParameterNode(name, modelChangeRegistrator);

		methodNode.addParameter(compositeParameterNode);

		return compositeParameterNode;
	}

	public static BasicParameterNode addNewBasicParameter(
			MethodNode methodNode, 
			String name, 
			String type,
			String defaultValue,
			IModelChangeRegistrator modelChangeRegistrator) {

		BasicParameterNode basicParameterNode = 
				new BasicParameterNode(name, type, defaultValue, false, modelChangeRegistrator);

		methodNode.addParameter(basicParameterNode);

		return basicParameterNode;
	}

	public static TestCaseNode addNewTestCase(MethodNode methodNode, List<ChoiceNode> choicesOfTestCase) {

		TestCaseNode testCaseNode = new TestCaseNode("TestSuite", null, choicesOfTestCase);
		methodNode.addTestCase(testCaseNode);

		return testCaseNode;
	}

	public static List<ParameterWithLinkingContext> getNestedBasicParametersWithLinkingContexts( // TODO MO-RE unit tests
			MethodNode methodSource) {

		List<ParameterWithLinkingContext> result = new ArrayList<>();

		List<AbstractParameterNode> parameters = methodSource.getParameters();

		for (AbstractParameterNode currentParameterNode : parameters) {

			AbstractParameterNode currentLinkingContext = currentParameterNode;

			accumulateBasicParametersAndContextsRecursive(
					currentParameterNode, 
					currentLinkingContext, 
					result);
		}

		return result;
	}

	private static void accumulateBasicParametersAndContextsRecursive(
			AbstractParameterNode currentAbstractParameterNode, 
			AbstractParameterNode currentLinkingContext,
			List<ParameterWithLinkingContext> inOutResult) {

		if (currentAbstractParameterNode instanceof BasicParameterNode) {

			if (currentAbstractParameterNode.isLinked()) {
				accumulateBasicLinkedParameter(currentAbstractParameterNode, inOutResult);
				return;
			}

			accumulateBasicParameter(currentAbstractParameterNode, null, inOutResult);
			return;
		}

		CompositeParameterNode currentCompositeParameterNode = 
				(CompositeParameterNode) currentAbstractParameterNode;

		if (currentCompositeParameterNode.isGlobalParameter()) {
			accumulateBasicParametersInGlobalCompositesRecursive(
					currentCompositeParameterNode, currentLinkingContext, inOutResult);
			return;
		} 

		// accumulating parameters in local composites

		AbstractParameterNode linkToGlobalParameter = currentCompositeParameterNode.getLinkToGlobalParameter();

		if (linkToGlobalParameter != null) {

			// jump to global composites 

			accumulateBasicParametersAndContextsRecursive(
					linkToGlobalParameter, currentLinkingContext, inOutResult);

			return;
		}

		List<AbstractParameterNode> parameters = currentCompositeParameterNode.getParameters();

		for (AbstractParameterNode childAbstractParameterNode : parameters) {

			AbstractParameterNode newLinkingContext = childAbstractParameterNode;

			accumulateBasicParametersAndContextsRecursive(
					childAbstractParameterNode, newLinkingContext, inOutResult);
		}
	}

	private static void accumulateBasicParametersInGlobalCompositesRecursive(
			CompositeParameterNode currentCompositeParameterNode, 
			AbstractParameterNode currentLinkingContext,
			List<ParameterWithLinkingContext> inOutResult) {

		List<AbstractParameterNode> childParameters = currentCompositeParameterNode.getParameters();

		for (AbstractParameterNode childParameter : childParameters) {

			if (childParameter instanceof BasicParameterNode) {
				accumulateBasicParameter(childParameter, currentLinkingContext, inOutResult);
				continue;
			}

			CompositeParameterNode childCompositeParameterNode = (CompositeParameterNode) childParameter;

			accumulateBasicParametersInGlobalCompositesRecursive(
					childCompositeParameterNode, 
					currentLinkingContext, 
					inOutResult);
		}
	}

	private static void accumulateBasicLinkedParameter(
			AbstractParameterNode currentAbstractParameterNode,
			List<ParameterWithLinkingContext> inOutResult) {

		AbstractParameterNode newContext = currentAbstractParameterNode;
		AbstractParameterNode newParameter = newContext.getLinkToGlobalParameter();

		accumulateBasicParameter(newParameter, newContext, inOutResult);

	}

	private static void accumulateBasicParameter(
			AbstractParameterNode currentAbstractParameterNode,
			AbstractParameterNode linkingContext, 
			List<ParameterWithLinkingContext> inOutResult) {

		ParameterWithLinkingContext parameterWithLinkingContext = 
				new ParameterWithLinkingContext(currentAbstractParameterNode, linkingContext);

		inOutResult.add(parameterWithLinkingContext);
	}

}
