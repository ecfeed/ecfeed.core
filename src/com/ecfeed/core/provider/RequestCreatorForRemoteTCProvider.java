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
			AmbiguousConstraintAction ambiguousConstraintAction,  // TODO - EX-AM
			List<IGeneratorValue> generatorArguments) {

		TestCasesRequest testCasesRequest = new TestCasesRequest();
		TestCasesUserInput testCasesUserInput = new TestCasesUserInput();

		testCasesRequest.setSessionId(sessionId);

		testCasesRequest.setMethod(
				MethodNodeHelper.createLongSignature(methodNode, true, new ExtLanguageManagerForJava()));

		testCasesRequest.setModelIdentificationStr(modelUuidOrName); 
		
		testCasesUserInput.setDataSource(generatorType.toString());

		setChoices(methodNode, allChoicesSelected, algorithmInput, testCasesUserInput);
		setConstraints(allConstraintsSelected, iConstraints, testCasesUserInput);
		testCasesUserInput.setAmbiguousConstraintsFlag(ambiguousConstraintAction.getCode());
		setGeneratorArguments(generatorArguments, testCasesUserInput);

		String requestText = serializeRequest(testCasesRequest, testCasesUserInput);
		System.out.println("Request:" + requestText);
		return requestText;
	}

	private static void setChoices(
			MethodNode methodNode, boolean allChoicesSelected,
			List<List<ChoiceNode>> algorithmInput, 
			TestCasesUserInput testCasesUserInput) {

		if (allChoicesSelected) {
			return;
		}

		Map<String, List<String>> argsAndChoiceNames = 
				convertToParamAndChoiceNames(
						methodNode, algorithmInput, new ExtLanguageManagerForJava());

		testCasesUserInput.setChoices(argsAndChoiceNames);
	}

	private static Map<String, List<String>> convertToParamAndChoiceNames(
			MethodNode methodNode, 
			List<List<ChoiceNode>> algorithmInput,
			IExtLanguageManager extLanguageManager) {

		Map<String, List<String>> paramAndChoiceNames = new HashMap<String, List<String>>();

		int parametersCount = methodNode.getParametersCount();

		for (int parameterIndex = 0;  parameterIndex < parametersCount;  parameterIndex++) {

			String parameterName = methodNode.getParameter(parameterIndex).getName();
			List<ChoiceNode> choicesForParameter = algorithmInput.get(parameterIndex);

			paramAndChoiceNames.put(parameterName, ChoiceNodeHelper.getChoiceNames(choicesForParameter, extLanguageManager));
		}

		return paramAndChoiceNames;
	}

	private static void setConstraints(
			boolean allConstraintsSelected, 
			List<Constraint> iConstraints,
			TestCasesUserInput testCasesUserInput) {

		if (allConstraintsSelected) {
			return;
		}

		if (ConstraintHelper.containsConstraints(iConstraints)) {
			List<String> constraintNames = ConstraintHelper.createListOfConstraintNames(iConstraints);
			testCasesUserInput.setConstraints(constraintNames);
			return;

		} 

		testCasesUserInput.setNoConstraints();
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