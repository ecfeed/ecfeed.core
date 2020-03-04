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

import java.util.Set;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.JustifyType;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.JavaTypeHelper;

public class GherkinExportTemplate extends AbstractExportTemplate {

	public GherkinExportTemplate(MethodNode methodNode) {
		super(methodNode, createDefaultTemplateText(methodNode));
	}

	private static String createDefaultTemplateText(MethodNode methodNode) {

		String defaultTemplateText =
				TemplateText.createTemplateText(
						createDefaultHeaderTemplate(methodNode),
						createDefaultTestCaseTemplate(methodNode),
						"");

		return defaultTemplateText;
	}

	@Override
	public String getFileExtension() {
		return "feature";
	}

	@Override 
	public String getTemplateFormat() {
		return getTemplateFormatSt();
	}

	public static String getTemplateFormatSt() {
		final String FORMAT_GHERKIN = "Gherkin";
		return FORMAT_GHERKIN;
	}

	private static String createDefaultHeaderTemplate(MethodNode methodNode) {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("Scenario: executing " + methodNode.getFullName() + "\n");
		stringBuilder.append(createInputParametersSection(methodNode));
		stringBuilder.append(createExecuteSection(methodNode));
		stringBuilder.append(createExpectedParametersSection(methodNode));
		stringBuilder.append("\n");
		stringBuilder.append(createExamplesSection(methodNode));

		return StringHelper.removeNewlineAtEnd(stringBuilder.toString());
	}

	private static String createInputParametersSection(MethodNode methodNode) {

		StringBuilder stringBuilder = new StringBuilder();

		int methodParametersCount = methodNode.getParametersCount();

		int counter = 0;
		for (int parameterIndex = 0; 
				parameterIndex < methodParametersCount; 
				++parameterIndex) {

			MethodParameterNode methodParameterNode = methodNode.getMethodParameter(parameterIndex);

			if (methodParameterNode.isExpected()) {
				continue;
			}

			String parameterName = methodNode.getParameter(parameterIndex).getFullName();
			String line = getInputParameterPrefix(counter) + parameterName + " is <" + parameterName + ">" + "\n";
			stringBuilder.append(line);

			counter++;
		}

		return stringBuilder.toString();
	}

	private static String getInputParameterPrefix(int counter) {
		if (counter == 0) {
			return "\tGiven the value of ";
		}
		return "\tAnd the value of ";
	}

	private static String createExpectedParametersSection(MethodNode methodNode) {

		StringBuilder stringBuilder = new StringBuilder();

		int methodParametersCount = methodNode.getParametersCount();

		int counter = 0;

		for (int parameterIndex = 0; parameterIndex < methodParametersCount; ++parameterIndex) {

			MethodParameterNode methodParameterNode = 
					(MethodParameterNode) methodNode.getParameter(parameterIndex);

			if (!methodParameterNode.isExpected()) {
				continue;
			}

			String parameterName = methodNode.getParameter(parameterIndex).getFullName();
			String line = getExpectedParameterPrefix(counter) + parameterName + " is <" + parameterName + ">" + "\n";
			stringBuilder.append(line);
			counter++;
		}

		return stringBuilder.toString();
	}

	private static int getChoiceLength(ChoiceNode choiceNode) {
		return choiceNode.getValueString().length();
	}

	private static String getExpectedParameterPrefix(int counter) {
		if (counter == 0) {
			return "\tThen the value of ";
		}
		return "\tAnd the value of ";
	}

	private static String createExamplesSection(MethodNode methodNode) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Examples:\n");
		stringBuilder.append("| ");

		int methodParametersCount = methodNode.getParametersCount();
		
		for (int parameterIndex = 0; parameterIndex < methodParametersCount; ++parameterIndex) {

			MethodParameterNode methodParameterNode = (MethodParameterNode)methodNode.getParameter(parameterIndex);

			String parameterDescription = createParameterDescription(methodParameterNode); 
			stringBuilder.append(parameterDescription + " | ");
		}

		stringBuilder.append("\n");

		return stringBuilder.toString();
	}

	private static String createParameterDescription(MethodParameterNode methodParameterNode) {

		String parameterName = methodParameterNode.getFullName();
		int maxParamValueLength = getMaxParamValueLength(methodParameterNode, parameterName);

		return embedInMinWidthOperator("<" + parameterName + ">", maxParamValueLength, JustifyType.CENTER); 
	}

	private static String embedInMinWidthOperator(String string, int minWidth, JustifyType justifyType) {
		return "(" + string + ").min_width(" + minWidth + ", " + JustifyType.convertToString(justifyType) +  ")";	
	}

	private static int getMaxParamValueLength(MethodParameterNode methodParameterNode, String parameterName) {

		Set<ChoiceNode> choices = methodParameterNode.getAllChoices();

		int maxLength = 0;
		for (ChoiceNode choiceNode : choices) {
			maxLength = Math.max(maxLength, getChoiceLength(choiceNode));
		}

		maxLength = Math.max(maxLength, parameterName.length() + 2);
		
		return maxLength;
	}

	private static String createExecuteSection(MethodNode methodNode) {
		return "\tWhen " + methodNode.getFullName() + " is executed\n";
	}

	private static String createDefaultTestCaseTemplate(MethodNode methodNode) {

		int methodParametersCount = methodNode.getParametersCount();
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("| ");

		for (int index = 0; index < methodParametersCount; ++index) {

			MethodParameterNode methodParameterNode = methodNode.getMethodParameter(index);  
			String parameterName = methodParameterNode.getFullName(); 

			int maxParamValueLength = getMaxParamValueLength(methodParameterNode, parameterName);

			JustifyType justifyType = JavaTypeHelper.getJustifyType(methodParameterNode.getType());

			String paramDescription = embedInMinWidthOperator("$" + parameterName + "." + "value", maxParamValueLength, justifyType);
			stringBuilder.append(paramDescription);
			stringBuilder.append(" | ");
		}

		return stringBuilder.toString();
	}

}
