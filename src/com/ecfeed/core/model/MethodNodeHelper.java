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

import com.ecfeed.core.utils.*;

public class MethodNodeHelper {

	public static BasicParameterNode findGlobalParameter(MethodNode fMethodNode, String globalParameterExtendedName) {

		if (StringHelper.isNullOrEmpty(globalParameterExtendedName)) {
			return null;
		}

		String parentName = getParentName(globalParameterExtendedName);
		String parameterName = getParameterName(globalParameterExtendedName);

		ClassNode classNode = fMethodNode.getClassNode();
		String className = classNode.getName();

		if (StringHelper.isEqual(className, parentName)) {
			AbstractParameterNode abstractParameterNode = classNode.findParameter(parameterName);
			return (BasicParameterNode)abstractParameterNode;
		}

		RootNode rootNode = classNode.getRoot();
		String rootName = rootNode.getName();

		if (parentName == null || rootName.equals(parentName)) {
			AbstractParameterNode abstractParameterNode = rootNode.findParameter(parameterName);
			return (BasicParameterNode)abstractParameterNode;
		}			

		ExceptionHelper.reportRuntimeException("Invalid dst parameter extended name.");
		return null;
	}

	private static String getParentName(String parameterExtendedName) {

		String[] dstParamNameParts = StringHelper.splitIntoTokens(parameterExtendedName, ":");

		if (dstParamNameParts.length == 2) {
			return dstParamNameParts[0]; 
		}

		return null;
	}

	private static String getParameterName(String parameterExtendedName) {

		String[] dstParamNameParts = StringHelper.splitIntoTokens(parameterExtendedName, ":");

		if (dstParamNameParts.length == 2) {
			return dstParamNameParts[1]; 
		}

		return dstParamNameParts[0];
	}

	public static List<ChoiceNode> getChoicesUsedInConstraints(BasicParameterNode methodParameterNode) {

		List<ChoiceNode> resultChoiceNodes = new ArrayList<ChoiceNode>();

		MethodNode methodNode = (MethodNode) methodParameterNode.getParent();

		List<ConstraintNode> constraintNodes = methodNode.getConstraintNodes();

		for (ConstraintNode constraintNode : constraintNodes) {

			List<ChoiceNode> choiceNodesForConstraint = 
					ConstraintNodeHelper.getChoicesUsedInConstraint(
							constraintNode, methodParameterNode);

			resultChoiceNodes.addAll(choiceNodesForConstraint);
		}

		resultChoiceNodes = ChoiceNodeHelper.removeDuplicates(resultChoiceNodes);

		return resultChoiceNodes;
	}

	public static List<String> getLabelsUsedInConstraints(BasicParameterNode methodParameterNode) {

		List<String> resultLabels = new ArrayList<>();

		MethodNode methodNode = (MethodNode) methodParameterNode.getParent();

		List<ConstraintNode> constraintNodes = methodNode.getConstraintNodes();

		for (ConstraintNode constraintNode : constraintNodes) {

			List<String> labelsOfConstraint = 
					ConstraintNodeHelper.getLabelsUsedInConstraint(
							constraintNode, methodParameterNode);

			resultLabels.addAll(labelsOfConstraint);

			resultLabels = StringHelper.removeDuplicates(resultLabels);
		}

		return resultLabels;
	}


	//	public static void updateParameterReferencesInConstraints(
	//			MethodParameterNode oldMethodParameterNode,
	//			ChoicesParentNode dstParameterForChoices,
	//			List<ConstraintNode> constraintNodes,
	//			ListOfModelOperations reverseOperations,
	//			IExtLanguageManager extLanguageManager) {
	//
	//		if (oldMethodParameterNode == null) {
	//			ExceptionHelper.reportRuntimeException("Invalid old parameter node.");
	//		}
	//
	//		if (dstParameterForChoices == null) {
	//			ExceptionHelper.reportRuntimeException("Invalid new parameter node.");
	//		}
	//
	//		for (ConstraintNode constraintNode : constraintNodes) {
	//			ConstraintNodeHelper.updateParameterReferences(
	//					constraintNode,
	//					oldMethodParameterNode, dstParameterForChoices);
	//		}
	//	}

	//	public static void updateChoiceReferencesInTestCases(
	//			ParameterConversionItem parameterConversionItem,
	//			List<TestCaseNode> testCaseNodes,
	//			ListOfModelOperations inOutReverseOperations,
	//			IExtLanguageManager extLanguageManager) {
	//
	//		IParameterConversionItemPart srcPart = parameterConversionItem.getDstPart();
	//
	//		if (!(srcPart instanceof ParameterConversionItemPartForChoice)) {
	//			return;
	//		}
	//
	//		IParameterConversionItemPart dstPart = parameterConversionItem.getDstPart();
	//
	//		if (!(dstPart instanceof ParameterConversionItemPartForChoice)) {
	//			return;
	//		}
	//
	//		ParameterConversionItemPartForChoice srcPartForChoice = (ParameterConversionItemPartForChoice) srcPart;
	//		ParameterConversionItemPartForChoice dstPartForChoice = (ParameterConversionItemPartForChoice) dstPart;
	//
	//		ChoiceNode srcChoice = srcPartForChoice.getChoiceNode();
	//		ChoiceNode dstChoice = dstPartForChoice.getChoiceNode();
	//
	//		for (TestCaseNode testCaseNode : testCaseNodes)  {
	//
	//			testCaseNode.updateChoiceReferences(srcChoice, dstChoice);
	//		}
	//
	//		if (inOutReverseOperations != null) {
	//			MethodOperationUpdateChoiceReferencesInTestCases reverseOperation = 
	//					new MethodOperationUpdateChoiceReferencesInTestCases(
	//							parameterConversionItem, 
	//							testCaseNodes, extLanguageManager);
	//
	//			inOutReverseOperations.add(reverseOperation);
	//		}
	//	}

	public static void convertConstraints(
			List<ConstraintNode> constraintNodes,
			ParameterConversionItem parameterConversionItem) {

		for (ConstraintNode constraintNode : constraintNodes) {
			ConstraintNodeHelper.convertConstraint(
					constraintNode, 
					parameterConversionItem);
		}
	}

	public static void addTestCaseToMethod(MethodNode methodNode, ChoiceNode choiceNode) {

		List<ChoiceNode> listOfChoicesForTestCase = new ArrayList<ChoiceNode>();
		listOfChoicesForTestCase.add(choiceNode);

		TestCaseNode testCaseNode = new TestCaseNode("name", null, listOfChoicesForTestCase);
		methodNode.addTestCase(testCaseNode);
	}

	public static BasicParameterNode findMethodParameterByName(
			String parameterNameToFindInExtLanguage, 
			MethodNode methodNode,
			IExtLanguageManager extLanguageManager) {

		List<AbstractParameterNode> methodParameters = methodNode.getParameters();

		for (AbstractParameterNode parameter : methodParameters) {

			BasicParameterNode methodParameterNode = (BasicParameterNode)parameter;

			String parameterNameInExtLanguage = MethodParameterNodeHelper.getName(methodParameterNode, extLanguageManager);

			if (StringHelper.isEqual(parameterNameToFindInExtLanguage, parameterNameInExtLanguage)) {
				return methodParameterNode;
			}
		}
		
		return null;

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

	public static BasicParameterNode addParameterToMethod(
			IParametersAndConstraintsParentNode methodNode, String name, String type) {

		BasicParameterNode methodParameterNode = new BasicParameterNode(name, type, "0", false, null);
		methodNode.addParameter(methodParameterNode);

		return methodParameterNode;
	}

	public static BasicParameterNode addLinkedParameterToMethod(
			MethodNode methodNode, String name, String type, BasicParameterNode linkToGlobalParameter) {

		BasicParameterNode methodParameterNode = new BasicParameterNode(name, type, "0", false, null);
		methodParameterNode.setLinkToGlobalParameter(linkToGlobalParameter);
		methodNode.addParameter(methodParameterNode);

		return methodParameterNode;
	}
	
	public static BasicParameterNode addExpectedParameterToMethod(
			MethodNode methodNode, String name, String type, String defaultValue) {

		BasicParameterNode methodParameterNode = new BasicParameterNode(name, type, defaultValue, true, null);
		methodNode.addParameter(methodParameterNode);

		return methodParameterNode;
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

	public static String createLongSignature(MethodNode methodNode, boolean isParamNameAdded, IExtLanguageManager extLanguageManager) {

		String shortSignature = createSignature(methodNode, isParamNameAdded, extLanguageManager);

		IAbstractNode parent = methodNode.getParent();
		
		if (parent == null) {
			
			return shortSignature;
		}
		
		return parent.getName() + "." + shortSignature;
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
					AbstractParameterNodeHelper.createSignature(
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
						AbstractParameterNodeHelper.createSignatureOfOneParameterByIntrLanguage(
								basicParameterNode.getType(),
								basicParameterNode.getName(),
								basicParameterNode.isExpected(), 
								extLanguageManager);
			} else {
			
				CompositeParameterNode compositeParameterNode = (CompositeParameterNode) methodParameterNode;
				
				signatureOfOneParameter = 
						AbstractParameterNodeHelper.createSignature(compositeParameterNode);
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


	public static AbstractParameterNode createNewParameter( // TODO MO-RE rename
			IParametersParentNode parametersParentNode,
			AbstractParameterNode.ParameterType parameterType,
			IExtLanguageManager extLanguageManager) {

		// TODO MO-RE divide into composite parameter helper and method node helper ? or rename method node helper
		String name = MethodNodeHelper.generateNewParameterName(parametersParentNode);

		IModelChangeRegistrator modelChangeRegistrator = parametersParentNode.getModelChangeRegistrator();

		if (parameterType == AbstractParameterNode.ParameterType.COMPOSITE) {

			CompositeParameterNode compositeParameterNode =
					new CompositeParameterNode(name, modelChangeRegistrator);

			return compositeParameterNode;
		}

		if (parametersParentNode instanceof MethodNode) {

			MethodNode methodNode = (MethodNode) parametersParentNode;

			String type = MethodNodeHelper.findNotUsedJavaTypeForParameter(
					methodNode, extLanguageManager);

			String defaultValue = JavaLanguageHelper.getDefaultValue(type);

			BasicParameterNode parameter =
					new BasicParameterNode(name, type, defaultValue, false, modelChangeRegistrator);

			return parameter;
		}

		if (parametersParentNode instanceof CompositeParameterNode) {

			//			MethodNode methodNode = (MethodNode) parametersParentNode;

			//	String type = MethodNodeHelper.findNotUsedJavaTypeForParameter(methodNode, extLanguageManager);

			String type = null;

			if (extLanguageManager instanceof ExtLanguageManagerForJava) {  // TODO MO-RE move to extManagers
				type = "int";
			} else {
				type = "Number";
			}

			String defaultValue = JavaLanguageHelper.getDefaultValue(type);

			BasicParameterNode parameter =
					new BasicParameterNode(name, type, defaultValue, false, modelChangeRegistrator);

			return parameter;
		}


		ExceptionHelper.reportRuntimeException("Not supported parameter type.");
		return null;
	}

	public static BasicParameterNode createBasicParameter(MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		BasicParameterNode basicParameterNode =
				(BasicParameterNode) createNewParameter(
				methodNode,
				AbstractParameterNode.ParameterType.BASIC,
				extLanguageManager);

		return basicParameterNode;
	}

	public static CompositeParameterNode createNewCompositeParameter(
			MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		String name = MethodNodeHelper.generateNewParameterName(methodNode);

		CompositeParameterNode parameter =
				new CompositeParameterNode(name, methodNode.getModelChangeRegistrator());

		return parameter;
	}

	public static String generateNewParameterName(IParametersParentNode parametersParentNode) {

		int i = 0;

		String name = CommonConstants.DEFAULT_NEW_PARAMETER_NAME + i++;

		while(parametersParentNode.findParameter(name) != null) {
			name = CommonConstants.DEFAULT_NEW_PARAMETER_NAME + i++;
		}

		return name;
	}

	public static String findNotUsedJavaTypeForParameter(
			MethodNode methodNode, IExtLanguageManager extLanguageManager) {

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

		List<String> parameterTypesInExternalLanguage = ParametersParentNodeHelper.getParameterTypes(methodNode, extLanguageManager);
		parameterTypesInExternalLanguage.add(typeForLastParameter);

		String methodNameInExternalLanguage = AbstractNodeHelper.getName(methodNode, extLanguageManager);

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
				testSuiteNode.setName(testSuiteName);
				testSuiteNode.setDisplayLimitExceededFlag(true);
			} else {
				testSuiteNode.getTestCaseNodes().addAll(testCasesSuite);
				testSuiteNode.setName(testSuiteName);
				testSuiteNode.setDisplayLimitExceededFlag(false);
			}
		}

		testSuites.sort((a, b) -> a.getSuiteName().compareTo(b.getSuiteName()));

		return testSuites;
	}

	public static BasicParameterNode findExpectedParameterNotUsedInAssignment(MethodNode methodNode, Constraint constraint) {

		if (constraint.getType() != ConstraintType.ASSIGNMENT) {
			return null;
		}

		AbstractStatement postcondition = constraint.getPostcondition();

		if (!(postcondition instanceof StatementArray)) {
			return null;
		}

		StatementArray statementArray = (StatementArray)postcondition;

		BasicParameterNode parameterNode = findNotUsedExpectedParameter(methodNode, statementArray);
		return parameterNode;
	}

	public static BasicParameterNode findNotUsedExpectedParameter(MethodNode methodNode, StatementArray statementArray) {

		List<AbstractParameterNode> parameters = methodNode.getMethodParameters();

		for (AbstractParameterNode abstractParameterNode : parameters) {
			
			if (!(abstractParameterNode instanceof BasicParameterNode)) {
				continue;
			}

			BasicParameterNode basicParameterNode = (BasicParameterNode)abstractParameterNode;
			
			if (!basicParameterNode.isExpected()) {
				continue;
			}

			if (!isParameterUsedInAssignment(basicParameterNode, statementArray)) {
				return basicParameterNode;
			}
		}
		return null;
	}

	public static boolean isParameterUsedInAssignment(BasicParameterNode parameterNode, StatementArray statementArray) {

		List<AbstractStatement> statements = statementArray.getStatements();

		for (AbstractStatement abstractStatement : statements) {

			if (!(abstractStatement instanceof AssignmentStatement)) {
				continue;
			}

			AssignmentStatement assignmentStatement = (AssignmentStatement)abstractStatement;

			BasicParameterNode leftParameter = assignmentStatement.getLeftParameter();

			if (leftParameter == parameterNode) {
				return true;
			}

		}

		return false;
	}

	public static List<String> getStatementValuesForParameter(
			MethodNode methodNode,
			BasicParameterNode methodParameterNode) {

		List<Constraint> constraints = methodNode.getConstraints();

		List<String> values = new ArrayList<>();

		for (Constraint constraint : constraints) {

			List<String> valuesOfConstraint = constraint.getStatementValuesForParameter(); 

			if (valuesOfConstraint != null && !valuesOfConstraint.isEmpty()) {
				values.addAll(valuesOfConstraint);
			}
		}

		values = StringHelper.removeDuplicates(values);

		return values;
	}

}
