/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *
 *******************************************************************************/

package com.ecfeed.core.provider;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoiceNodeHelper;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.utils.AmbiguousConstraintAction;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.GeneratorType;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.TestCasesRequest;
import com.ecfeed.core.utils.TestCasesUserInput;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestCreatorForRemoteTCProvider { // TODO - unit tests

	public static String createRequestText(
			String sessionId,
			String modelUuidOrName,
			MethodNode methodNode, 
			GeneratorType generatorType,
			boolean allChoicesSelected,
			List<List<ChoiceNode>> algorithmInput,
			boolean allConstraintsSelected,
			List<Constraint> iConstraints,	
			AmbiguousConstraintAction ambiguousConstraintAction,
			List<IGeneratorValue> generatorArguments) {

		Map<String, List<String>> argsAndChoiceNames = 
				convertToParamAndChoiceNames(
						methodNode, algorithmInput, new ExtLanguageManagerForJava());

		List<String> constraintNames = ConstraintHelper.createListOfConstraintNames(iConstraints);

		String methodSignature = MethodNodeHelper.createLongSignature(methodNode, true, new ExtLanguageManagerForJava());

		String requestText = createRequestText(
				sessionId, 
				modelUuidOrName, 
				methodSignature,
				generatorType, 
				allChoicesSelected,
				argsAndChoiceNames,
				allConstraintsSelected, 
				constraintNames,
				ambiguousConstraintAction, 
				generatorArguments);

		System.out.println("Request:" + requestText);
		return requestText;
	}

	public static String createRequestText(
			String sessionId, 
			String modelUuidOrName, 
			String methodSignature,
			GeneratorType generatorType, 
			boolean allChoicesSelected, 
			Map<String, List<String>> argsAndChoiceNames,
			boolean allConstraintsSelected, 
			List<String> constraintNames,
			AmbiguousConstraintAction ambiguousConstraintAction, 
			List<IGeneratorValue> generatorArguments) {

		TestCasesRequest testCasesRequest = new TestCasesRequest();
		TestCasesUserInput testCasesUserInput = new TestCasesUserInput();

		testCasesRequest.setSessionId(sessionId);

		testCasesRequest.setMethod(methodSignature);

		testCasesRequest.setModelIdentificationStr(modelUuidOrName); 

		testCasesUserInput.setDataSource(generatorType.toString());

		if  (!allChoicesSelected) {
			testCasesUserInput.setChoices(argsAndChoiceNames);
		}

		setConstraints(allConstraintsSelected, constraintNames, testCasesUserInput);

		testCasesUserInput.setAmbiguousTestCasesFlag(ambiguousConstraintAction.getCode());
		setGeneratorArguments(generatorArguments, testCasesUserInput);

		String requestText = serializeRequest(testCasesRequest, testCasesUserInput);
		return requestText;
	}

	private static Map<String, List<String>> convertToParamAndChoiceNames(
			MethodNode methodNode, 
			List<List<ChoiceNode>> algorithmInput,
			IExtLanguageManager extLanguageManager) {

		Map<String, List<String>> paramAndChoiceNames = new HashMap<String, List<String>>();
		
		List<BasicParameterNode> parameters;
		
		if (methodNode.isDeployed()) {
			parameters = methodNode.getDeployedParameters();
		} else {
			parameters = methodNode.getParametersAsBasic();
		}
		

		int parametersCount = parameters.size();

		for (int parameterIndex = 0;  parameterIndex < parametersCount;  parameterIndex++) {

			String parameterName = parameters.get(parameterIndex).getName();
			List<ChoiceNode> choicesForParameter = algorithmInput.get(parameterIndex);

			paramAndChoiceNames.put(parameterName, ChoiceNodeHelper.getChoiceNames(choicesForParameter, extLanguageManager));
		}

		return paramAndChoiceNames;
	}

	private static void setConstraints(
			boolean allConstraintsSelected,
			List<String> constraintNames,
			TestCasesUserInput testCasesUserInput) {

		if (allConstraintsSelected) {
			testCasesUserInput.setAllConstraints();
		}

		if (constraintNames.isEmpty()) {
			testCasesUserInput.setNoConstraints();
			return;
		} 

		testCasesUserInput.setConstraints(constraintNames);

	}

	private static String serializeRequest(
			TestCasesRequest testCasesRequest, 
			TestCasesUserInput testCasesUserInput) {

		String userInput = serializeUserInput(testCasesUserInput);
		String correctedUserInput = userInput.replaceAll("\"", "'");

		testCasesRequest.setUserData(correctedUserInput);

		return serializeTestCasesRequest(testCasesRequest);
	}

	private static String serializeUserInput(TestCasesUserInput userInput) {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);

		StringWriter stringWriter = new StringWriter();

		try {
			objectMapper.writeValue(stringWriter, userInput);
		} catch (Exception e) {
			ExceptionHelper.reportRuntimeException("Failed to serialize user input.", e);
		}

		String result = stringWriter.toString();
		return result;
	}

	private static String serializeTestCasesRequest(TestCasesRequest testCasesRequest) {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);

		StringWriter stringWriter = new StringWriter();

		try {
			objectMapper.writeValue(stringWriter, testCasesRequest);
		} catch (Exception e) {
			ExceptionHelper.reportRuntimeException("Failed to serialize user input.", e);
		}

		String result = stringWriter.toString();
		return result;
	}

	private static void setGeneratorArguments(
			List<IGeneratorValue> generatorArguments, 
			TestCasesUserInput testCasesUserInput) {

		for(IGeneratorValue entry : generatorArguments) {
			testCasesUserInput.getProperties().put(entry.getDefinition().getName(), entry.getValue().toString());
		}
	}
}