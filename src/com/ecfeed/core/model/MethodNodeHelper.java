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
import java.util.stream.Collectors;

import com.ecfeed.core.model.AbstractParameterSignatureHelper.Decorations;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.ExtendedName;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.TypeIncluded;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.model.utils.ParameterWithLinkingContextHelper;
import com.ecfeed.core.utils.CommonConstants;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.NameHelper;
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.SignatureHelper;
import com.ecfeed.core.utils.TypeHelper;

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

	public static String createSignatureOldStandard(MethodNode method, IExtLanguageManager manager) {

		List<String> parameterNames = method.getParameters().stream().map(e -> AbstractParameterSignatureHelper.createSignatureOldStandard(e, manager)).collect(Collectors.toList());
		String methodName = method.getName();

		return methodName + "(" + String.join(", ", parameterNames) + ")";
	}

	public static String createSignatureNewStandard(
			MethodNode methodNode,
			boolean paramNamesAdded,
			boolean expectedDecorationsAdded, 
			IExtLanguageManager extLanguageManager) {

		String nameInExtLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(methodNode.getName());

		String signaturesOfParameters = 
				createSignaturesOfParametersNewStandard(
						methodNode,
						paramNamesAdded,
						expectedDecorationsAdded, 
						extLanguageManager);

		String signature = nameInExtLanguage + "(" + signaturesOfParameters + ")";

		return signature;

	}

	private static String createSignaturesOfParametersNewStandard(
			MethodNode methodNode,
			boolean paramNamesAdded,
			boolean expectedDecorationsAdded, 
			IExtLanguageManager extLanguageManager) {

		ExtendedName extendedName = (paramNamesAdded == true ? ExtendedName.NAME_ONLY : ExtendedName.EMPTY);
		Decorations parameterDecorations = (expectedDecorationsAdded == true ? Decorations.YES : Decorations.NO);

		List<String> signaturesOfSingleParameters = new ArrayList<>();

		List<AbstractParameterNode> parameters = methodNode.getParameters();

		for (AbstractParameterNode parameter : parameters) {

			String signatureOfOneParameter = 
					AbstractParameterSignatureHelper.createSignatureNewStandard(
							parameter,
							extendedName,
							parameterDecorations,
							TypeIncluded.YES,
							extLanguageManager);

			signaturesOfSingleParameters.add(signatureOfOneParameter);
		}

		String result = String.join(", ", signaturesOfSingleParameters);

		return result;
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
			boolean paramNamesAdded,
			boolean expectedDecorationsAdded, 
			IExtLanguageManager extLanguageManager) {

		String nameInExtLanguage = extLanguageManager.convertTextFromIntrToExtLanguage(methodNode.getName());

		String signaturesOfParameters = 
				createSignaturesOfParametersNewStandard(
						methodNode, 
						paramNamesAdded,
						expectedDecorationsAdded, 
						extLanguageManager);


		String signature = nameInExtLanguage + "(" + signaturesOfParameters + ")";

		return signature;

		//		final List<Boolean> expectedParametersFlags =
		//				(expectedDecorationsAdded ? getExpectedParametersFlags(methodNode.getParameters()) : null);
		//
		//		List<String> parametersNames = new ArrayList<>();
		//
		//		if (paramNamesAdded == true) {
		//			parametersNames = methodNode.getParametersNames();
		//		} else {
		//			parametersNames = null;
		//		}
		//
		//		List<String> parameterTypes = methodNode.getParameterTypes();
		//
		//		String methodName = methodNode.getName();
		//
		//		String signature =
		//				createSignatureByIntrLanguage(
		//						methodName,
		//						parameterTypes,
		//						parametersNames,
		//						expectedParametersFlags,
		//						extLanguageOfTheResult);
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
						AbstractParameterSignatureHelper.createSignatureNewStandard(
								basicParameterNode, 
								ExtendedName.NAME_ONLY, 
								Decorations.YES, 
								TypeIncluded.YES, 
								extLanguageManager);
			} else {

				CompositeParameterNode compositeParameterNode = (CompositeParameterNode) methodParameterNode;

				signatureOfOneParameter = 
						AbstractParameterSignatureHelper.createSignatureNewStandard(
								compositeParameterNode, 
								ExtendedName.NAME_ONLY, Decorations.NO, TypeIncluded.YES, 
								extLanguageManager);
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

	public static CompositeParameterNode addNewCompositeParameterToMethod(
			MethodNode methodNode, String name, boolean setParent, IModelChangeRegistrator modelChangeRegistrator) {

		CompositeParameterNode compositeParameterNode = new CompositeParameterNode(name, modelChangeRegistrator);

		if (setParent) {
			compositeParameterNode.setParent(methodNode);
		}

		methodNode.addParameter(compositeParameterNode);

		return compositeParameterNode;
	}

	public static BasicParameterNode addNewBasicParameter(
			MethodNode methodNode, 
			String name, 
			String type,
			String defaultValue,
			boolean setParent,
			IModelChangeRegistrator modelChangeRegistrator) {

		BasicParameterNode basicParameterNode = 
				new BasicParameterNode(name, type, defaultValue, false, modelChangeRegistrator);

		if (setParent) {
			basicParameterNode.setParent(methodNode);
		}

		methodNode.addParameter(basicParameterNode);

		return basicParameterNode;
	}

	public static TestCaseNode addNewTestCase(MethodNode methodNode, String testCaseName, List<ChoiceNode> choicesOfTestCase, boolean setParent) {

		TestCaseNode testCaseNode = new TestCaseNode(testCaseName, null, choicesOfTestCase);

		if (setParent) {
			testCaseNode.setParent(methodNode);
		}

		methodNode.addTestCase(testCaseNode);

		return testCaseNode;
	}

	public static TestCaseNode addNewTestCase(MethodNode methodNode, List<ChoiceNode> choicesOfTestCase, boolean setParent) {

		TestCaseNode testCaseNode = addNewTestCase(methodNode, "Test case", choicesOfTestCase, setParent);
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

	public static AbstractParameterNode findParameterByPath(String path, MethodNode methodNode) {

		String[] pathElements = path.split(SignatureHelper.SIGNATURE_NAME_SEPARATOR);

		int pathSize = pathElements.length;

		IParametersParentNode currentParametersParent = methodNode;

		for (int pathIndex = 0; pathIndex < pathSize; pathIndex++) {

			String pathElement = pathElements[pathIndex];

			AbstractParameterNode foundParameter = currentParametersParent.findParameter(pathElement);

			if (foundParameter == null) {
				return null;
			}

			if (pathIndex == pathSize - 1) {
				return foundParameter;
			}

			if (!(foundParameter instanceof CompositeParameterNode)) {
				ExceptionHelper.reportRuntimeException("Current parameter is not a composite.");
			}

			currentParametersParent = (IParametersParentNode) foundParameter;
		}

		ExceptionHelper.reportRuntimeException("Parameter not found");
		return null;
	}

	public static boolean containsStructures(MethodNode methodNode) {

		return methodNode.getNestedCompositeParameters(true).size() > 0;
	}

	public static void compareDeployedParameters(MethodNode method1, MethodNode method2) {

		List<ParameterWithLinkingContext> deployedParametersWithContexts1 = method1.getDeployedParametersWithLinkingContexts();
		List<ParameterWithLinkingContext> deployedParametersWithContexts2 = method2.getDeployedParametersWithLinkingContexts();

		if (deployedParametersWithContexts1.size() != deployedParametersWithContexts2.size()) {
			ExceptionHelper.reportRuntimeException("Length of deployed parameters in two method differs.");
		}

		int size = deployedParametersWithContexts1.size();

		for (int i = 0; i < size; ++i) {

			ParameterWithLinkingContext parameterWithContext1 = deployedParametersWithContexts1.get(i);
			ParameterWithLinkingContext parameterWithContext2 = deployedParametersWithContexts2.get(i);

			ParameterWithLinkingContextHelper.compareParametersWithLinkingContexts(parameterWithContext1,parameterWithContext2);
		}
	}

	public static void compareMethodParameters(
			BasicParameterNode methodParameterNode1, 
			BasicParameterNode methodParameterNode2) {

		NameHelper.compareNames(methodParameterNode1.getName(), methodParameterNode2.getName());

		TypeHelper.compareIntegers(
				methodParameterNode1.getChoices().size(), methodParameterNode2.getChoices().size(), "Length of choices list differs.");

		for(int i = 0; i < methodParameterNode1.getChoices().size(); i++){
			ChoiceNodeHelper.compareChoices(methodParameterNode1.getChoices().get(i), methodParameterNode2.getChoices().get(i));
		}
	}

	public static void compareTestCases(MethodNode methodNode1, MethodNode methodNode2) {

		AbstractNodeHelper.compareSizes(
				methodNode1.getTestCases(), methodNode2.getTestCases(), "Number of test cases differs.");

		for (int i =0; i < methodNode1.getTestCases().size(); ++i) {

			TestCaseNode testCaseNode1 = methodNode1.getTestCases().get(i);
			TestCaseNode testCaseNode2 = methodNode2.getTestCases().get(i);

			AbstractNodeHelper.compareParents(testCaseNode1, methodNode1, testCaseNode2, methodNode2);
			TestCaseNodeHelper.compareTestCases(testCaseNode1, testCaseNode2);
		}
	}

	public static void compareMethodParameters(MethodNode methodNode1, MethodNode methodNode2) {

		List<AbstractParameterNode> parameters1 = methodNode1.getParameters();
		List<AbstractParameterNode> parameters2 = methodNode2.getParameters();

		AbstractNodeHelper.compareSizes(parameters1, parameters2, "Number of parameters differs.");

		for (int i =0; i < parameters1.size(); ++i) {

			AbstractParameterNode abstractParameterNode1 = parameters1.get(i);
			AbstractParameterNode abstractParameterNode2 = parameters2.get(i);

			AbstractNodeHelper.compareParents(abstractParameterNode1, methodNode1, abstractParameterNode2, methodNode2);
			AbstractParameterNodeHelper.compareParameters(abstractParameterNode1, abstractParameterNode2);
		}
	}

	public static void compareMethodConstraints(MethodNode methodNode1, MethodNode methodNode2) {

		List<ConstraintNode> constraintNodes1 = methodNode1.getConstraintNodes();
		List<ConstraintNode> constraintNodes2 = methodNode2.getConstraintNodes();

		AbstractNodeHelper.compareSizes(constraintNodes1, constraintNodes2, "Number of constraints differs.");

		for (int i =0; i < constraintNodes1.size(); ++i) {

			ConstraintNode constraintNode1 = constraintNodes1.get(i);

			ConstraintNode constraintNode2 = constraintNodes2.get(i);

			AbstractNodeHelper.compareParents(constraintNode1, methodNode1, constraintNode2, methodNode2);
			ConstraintNodeHelper.compareConstraintNodes(constraintNode1, constraintNode2);
		}
	}

	public static void compareMethods(MethodNode method1, MethodNode method2) {

		if (method1 == null) {
			ExceptionHelper.reportRuntimeException("Empty method 1.");
		}

		if (method2 == null) {
			ExceptionHelper.reportRuntimeException("Empty method 2.");
		}

		NameHelper.compareNames(method1.getName(), method2.getName());

		MethodNodeHelper.compareMethodParameters(method1, method2);
		MethodNodeHelper.compareDeployedParameters(method1, method2);
		MethodNodeHelper.compareMethodConstraints(method1, method2);
		MethodNodeHelper.compareTestCases(method1, method2);
	}
}
