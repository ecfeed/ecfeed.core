/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.export;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ecfeed.core.model.AbstractNodeHelper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoiceNodeHelper;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.FixedChoiceValueFactory;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.MethodParameterNodeHelper;
import com.ecfeed.core.model.ModelHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.JustifyType;
import com.ecfeed.core.utils.StringHelper;

public class TestCasesExportHelper {

	private static final String CLASS_NAME_SEQUENCE = "%class";
	private static final String PACKAGE_NAME_SEQUENCE = "%package";
	private static final String METHOD_NAME_SEQUENCE = "%method";
	private static final String TEST_SUITE_NAME_SEQUENCE = "%suite";
	private static final String TEST_CASE_INDEX_NAME_SEQUENCE = "%index";
	private static final String PARAMETER_COMMAND_NAME = "name";
	private static final String PARAMETER_COMMAND_TYPE = "type";
	private static final String CHOICE_COMMAND_SHORT_NAME = "choice";
	private static final String CHOICE_COMMAND_FULL_NAME = "full_choice";
	private static final String CHOICE_COMMAND_VALUE = "value";


	public static final String METHOD_PARAMETER_SEQUENCE_GENERIC_PATTERN_FOR_JAVA_LANGUAGE = "\\$[\\w|_]+\\.(" + PARAMETER_COMMAND_NAME + "|" + PARAMETER_COMMAND_TYPE + ")";
	public static final String METHOD_PARAMETER_SEQUENCE_GENERIC_PATTERN_FOR_SIMPLE_LANGUAGE = "\\$[\\w|\\s]+\\.(" + PARAMETER_COMMAND_NAME + "|" + PARAMETER_COMMAND_TYPE + ")";

	public static final String TEST_PARAMETER_SEQUENCE_GENERIC_PATTERN_FOR_JAVA_LANGUAGE = "\\$[\\w|_]+\\.(" + CHOICE_COMMAND_SHORT_NAME + "|" + CHOICE_COMMAND_FULL_NAME + "|" + CHOICE_COMMAND_VALUE + ")";
	public static final String TEST_PARAMETER_SEQUENCE_GENERIC_PATTERN_FOR_SIMPLE_LANGUAGE = "\\$[\\w|\\s]+\\.(" + CHOICE_COMMAND_SHORT_NAME + "|" + CHOICE_COMMAND_FULL_NAME + "|" + CHOICE_COMMAND_VALUE + ")";	

	private static final String ARITHMETIC_EXPRESSION_SEQUENCE_GENERIC_PATTERN = "\\$\\(.*\\)";
	private static final String PARAMETER_SEPARATOR = ",";

	public static String generateSection(MethodNode method, String template, IExtLanguageManager extLanguageManager) {

		if (template == null) {
			return new String();
		}

		String result = template.replace(CLASS_NAME_SEQUENCE, ClassNodeHelper.getNonQualifiedName(method.getClassNode(), extLanguageManager));
		result = result.replace(PACKAGE_NAME_SEQUENCE, ClassNodeHelper.getPackageName(method.getClassNode(), extLanguageManager));
		result = result.replace(METHOD_NAME_SEQUENCE, method.getName());

		result = replaceParameterNameSequences(method, result, extLanguageManager);
		result = evaluateExpressions(result);
		result = evaluateMinWidthOperators(result);

		return result;
	}

	public static String generateTestCaseString(int sequenceIndex, TestCaseNode testCaseNode, String template, IExtLanguageManager extLanguageManager) {

		MethodNode method = testCaseNode.getMethod();

		if (template == null) {
			return new String();
		}

		String result = template.replace(CLASS_NAME_SEQUENCE, ClassNodeHelper.getNonQualifiedName(method.getClassNode(), extLanguageManager));
		result = result.replace(PACKAGE_NAME_SEQUENCE, ClassNodeHelper.getPackageName(method.getClassNode(), extLanguageManager));
		result = result.replace(METHOD_NAME_SEQUENCE, MethodNodeHelper.getName(method, extLanguageManager));
		result = result.replace(TEST_CASE_INDEX_NAME_SEQUENCE, String.valueOf(sequenceIndex));
		result = result.replace(TEST_SUITE_NAME_SEQUENCE, AbstractNodeHelper.getName(testCaseNode, extLanguageManager));

		result = replaceParameterSequences(testCaseNode, result, extLanguageManager);
		result = evaluateExpressions(result);
		result = evaluateMinWidthOperators(result);

		return result;
	}	

	private static String replaceParameterNameSequences(MethodNode methodNode, String template, IExtLanguageManager extLanguageManager) {

		String result = template;

		String regexPattern = getRegexPatternForMethodParameter(extLanguageManager);

		Matcher matcher = Pattern.compile(regexPattern).matcher(template);

		while(matcher.find()){

			String parameterCommandSequence = matcher.group();
			String parameterSubstitute = getParameterSubstitute(parameterCommandSequence, methodNode, extLanguageManager);

			result = result.replace(parameterCommandSequence, parameterSubstitute);
		}		

		return result;
	}

	public static String getRegexPatternForMethodParameter(IExtLanguageManager extLanguageManager) {

		return extLanguageManager.chooseString(
				METHOD_PARAMETER_SEQUENCE_GENERIC_PATTERN_FOR_JAVA_LANGUAGE, 
				METHOD_PARAMETER_SEQUENCE_GENERIC_PATTERN_FOR_SIMPLE_LANGUAGE);
	}

	private static String getParameterSubstitute(String parameterCommandSequence, MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		String command = getParameterCommand(parameterCommandSequence);
		int parameterNumber = getParameterNumber(parameterCommandSequence, methodNode, extLanguageManager) - 1;

		if (parameterNumber == -1) {
			return null;
		}

		BasicParameterNode parameter = methodNode.getMethodParameters().get(parameterNumber);
		String substitute = resolveParameterCommand(command, parameter, extLanguageManager);

		return substitute;
	}

	private static String resolveParameterCommand(String command, BasicParameterNode parameter, IExtLanguageManager extLanguageManager) {
		String result = command;
		switch(command){
		case PARAMETER_COMMAND_NAME:
			result = MethodParameterNodeHelper.getName(parameter, extLanguageManager);
			break;
		case PARAMETER_COMMAND_TYPE:
			result = MethodParameterNodeHelper.getType(parameter, extLanguageManager);
		default:
			break;
		}
		return result;
	}

	private static String getParameterCommand(String parameterCommandSequence) {
		return parameterCommandSequence.substring(parameterCommandSequence.indexOf(".") + 1, parameterCommandSequence.length());
	}

	private static int getParameterNumber(String parameterSequence, MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		String parameterDescriptionString = parameterSequence.substring(1, parameterSequence.indexOf("."));

		try {
			return Integer.parseInt(parameterDescriptionString);
		} catch(NumberFormatException e) {

			parameterDescriptionString = extLanguageManager.convertTextFromExtToIntrLanguage(parameterDescriptionString);
			final int parameterNumber = methodNode.getParameterIndex(parameterDescriptionString) + 1;
			return parameterNumber;
		}
	}

	private static String evaluateExpressions(String template) {
		String result = template;
		Matcher m = Pattern.compile(ARITHMETIC_EXPRESSION_SEQUENCE_GENERIC_PATTERN).matcher(template);
		while(m.find()){
			String expressionSequence = m.group();
			String expressionString = expressionSequence.substring(2, expressionSequence.length() - 1); //remove initial "$(" and ending ")"
			try{
				Expression expression = new Expression(expressionString);
				String substitute = expression.eval().toPlainString();
				result = result.replace(expressionSequence, substitute);
			}catch(RuntimeException e){} //if evaluation failed, do not stop, keep the result as it is
		}		
		return result;
	}

	public static String evaluateMinWidthOperators(String template) {

		final String MIN_WIDTH_OPERATOR_PATTERN = "\\((.*?)\\)\\.min_width((\\(\\s*\\-?\\d*\\s*\\))|(\\(\\s*\\d+\\s*\\,\\s*\\w*\\s*\\)))";

		String result = template;
		Matcher matcher = Pattern.compile(MIN_WIDTH_OPERATOR_PATTERN).matcher(template);

		while(matcher.find()) {

			String expressionSequence = matcher.group();
			String expandedValue = getExpandedValue(expressionSequence);
			result = result.replace(expressionSequence, expandedValue);
		}	

		return result;
	}	

	private static String getExpandedValue(String minWidthSequence) {

		String valueStr = getValueString(minWidthSequence);
		String minWidthParameters = getMinWidthParameters(minWidthSequence);

		String expandedValue = expandValue(valueStr, minWidthParameters);

		if (expandedValue == null) {
			return minWidthSequence;
		} else {
			return expandedValue;
		}
	}

	private static String getValueString(String string) {

		String tag = getArgWithBrackets(string, 0);
		if (tag == null) {
			return null;
		}

		return removeBrackets(tag);
	}

	private static String getMinWidthParameters(String minWidthSequence) {

		String tag = getArgWithBrackets(minWidthSequence, 1);
		if (tag == null) {
			return null;
		}

		return removeBrackets(tag);
	}

	private static String removeBrackets(String string) {
		return StringHelper.removeToPrefixAndFromPostfix("(", ")", string);
	}

	private static String getArgWithBrackets(String minWidthSequence, int index) {

		final String ARG_WITH_BRACKETS_PATTERN = "\\(\\s*[^\\)]*\\s*\\)";

		return StringHelper.getMatch(minWidthSequence, ARG_WITH_BRACKETS_PATTERN, index);
	}

	private static String expandValue(String valueStr, String parameters) {

		Integer repetitions = getRepetitions(parameters);
		if (repetitions == null) {
			return null;
		}

		JustifyType justifyType = getJustifyType(parameters);

		if (justifyType == JustifyType.ERROR) {
			return null;
		}

		return expandValue(valueStr, repetitions, justifyType);
	}

	private static String expandValue(String valueStr, int repetitions, JustifyType justifyType) {

		switch(justifyType) {
		case LEFT:
			return StringHelper.appendSpacesToLength(valueStr, repetitions);
		case RIGHT:
			return StringHelper.insertSpacesToLength(valueStr, repetitions);
		case CENTER:
			return StringHelper.centerStringToLength(valueStr, repetitions);
		default:
			return null;
		}
	}

	private static Integer getRepetitions(String parameters) {

		String repetitionsStr = getRepetitionsStr(parameters);

		try {
			return JavaLanguageHelper.convertToInteger(repetitionsStr);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static String getRepetitionsStr(String parameters) {

		if (parameters.contains(PARAMETER_SEPARATOR)) {
			return StringHelper.getFirstToken(parameters, PARAMETER_SEPARATOR);
		}

		return parameters;
	}

	private static JustifyType getJustifyType(String parameters) {

		String typeString = getJustifyTypeString(parameters);
		if (typeString == null) {
			return JustifyType.LEFT; 
		}

		return JustifyType.convertFromString(typeString);
	}

	private static String getJustifyTypeString(String parameters) {

		if (parameters.contains(PARAMETER_SEPARATOR)) {
			return StringHelper.getLastToken(parameters, PARAMETER_SEPARATOR).trim();
		}
		return null;
	}

	private static String replaceParameterSequences(TestCaseNode testCase, String template, IExtLanguageManager extLanguageManager) {

		String result = replaceParameterNameSequences(testCase.getMethod(), template, extLanguageManager);

		String pattern = getParameterSequencePattern(extLanguageManager);

		Matcher matcher = Pattern.compile(pattern).matcher(template);

		while(matcher.find()){

			String parameterCommandSequence = matcher.group();

			String valueSubstitute = createValueSubstitute(parameterCommandSequence, testCase, extLanguageManager);
			if (valueSubstitute != null) {
				result = result.replace(parameterCommandSequence, valueSubstitute);
			}
		}

		return result;
	}

	public static String getParameterSequencePattern(IExtLanguageManager extLanguageManager) {

		return extLanguageManager.chooseString(
				TEST_PARAMETER_SEQUENCE_GENERIC_PATTERN_FOR_JAVA_LANGUAGE, 
				TEST_PARAMETER_SEQUENCE_GENERIC_PATTERN_FOR_SIMPLE_LANGUAGE);

	}

	private static String createValueSubstitute(String parameterCommandSequence, TestCaseNode testCase, IExtLanguageManager extLanguageManager) {

		int parameterNumber = getParameterNumber(parameterCommandSequence, testCase.getMethod(), extLanguageManager) - 1;

		if (parameterNumber == -1) {
			return null;
		}
		if (parameterNumber >= testCase.getTestData().size()) {
			return null;
		}

		ChoiceNode choice = testCase.getTestData().get(parameterNumber);

		String command = getParameterCommand(parameterCommandSequence);
		String substitute = resolveChoiceCommand(command, choice, extLanguageManager);

		return substitute;
	}

	private static String resolveChoiceCommand(String command, ChoiceNode choice, IExtLanguageManager extLanguageManager) {
		String result = command;
		switch(command){
		case CHOICE_COMMAND_SHORT_NAME:
			result = ChoiceNodeHelper.getName(choice, extLanguageManager);
			break;
		case CHOICE_COMMAND_FULL_NAME:
			result = ChoiceNodeHelper.getQualifiedName(choice, extLanguageManager);
			break;
		case CHOICE_COMMAND_VALUE:
			result = getValue(choice, extLanguageManager);
			break;
		default:
			break;
		}
		return result;
	}

	private static String getValue(ChoiceNode choice, IExtLanguageManager extLanguageManager) {

		String convertedValue = convertValue(choice, extLanguageManager);

		if (convertedValue != null) {
			return convertedValue;
		}

		return ChoiceNodeHelper.getValueString(choice, extLanguageManager);
	}

	private static String convertValue(ChoiceNode choice, IExtLanguageManager extLanguageManager) {

		BasicParameterNode parameter = choice.getParameter();
		if (parameter == null) {
			return null;
		}

		String argType = choice.getParameter().getType();
		if (argType == null) {
			return null;
		}

		FixedChoiceValueFactory fixedValueFactory = new FixedChoiceValueFactory(null, true);
		
		String context = "Model path: " + ModelHelper.getFullPath(choice, extLanguageManager);
		
		Object parsedObject = 
				fixedValueFactory.createValue(
						choice.getValueString(), choice.isRandomizedValue(), argType, context);
		
		if (parsedObject == null) {
			return null;
		}

		return parsedObject.toString();
	}

}