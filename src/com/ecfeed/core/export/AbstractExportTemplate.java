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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.utils.CommonConstants;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;


public abstract class AbstractExportTemplate implements IExportTemplate {

	private MethodNode fMethodNode;
	private TemplateText fTemplateText;
	private IExtLanguageManager fExtLanguageManager;

	public AbstractExportTemplate(MethodNode methodNode, String defaultlTemplateText, IExtLanguageManager extLanguageManager) {

		fMethodNode = methodNode;
		fTemplateText = new TemplateText(defaultlTemplateText);
		fExtLanguageManager = extLanguageManager;
	}

	@Override
	public String getDefaultTemplateText() {
		
		return fTemplateText.getInitialTemplateText();
	}
	@Override
	public void setTemplateText(String templateText) {

		if (templateText == null) {
			ExceptionHelper.reportRuntimeException("Template text must not be empty.");
		}

		fTemplateText.setTemplateText(templateText);
	}

	@Override
	public String getTemplateText() {

		return fTemplateText.getCompleteTemplateText();
	}

	@Override
	public String getHeaderTemplate() {

		return fTemplateText.getHeaderTemplateText();
	}

	@Override
	public String getTestCaseTemplate() {

		return fTemplateText.getTestCaseTemplateText();
	}

	@Override
	public String getFooterTemplate() {

		return fTemplateText.getFooterTemplateText();
	}

	@Override
	public boolean isTemplateTextModified() {

		return fTemplateText.isTemplateTextModified();
	}

	@Override
	public String createPreview(
			Collection<TestCaseNode> selectedTestCases, 
			MethodNode methodNode,
			List<ParameterWithLinkingContext> deployedParameters) {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(
				TestCasesExportHelper.generateSection(
					fMethodNode, deployedParameters, fTemplateText.getHeaderTemplateText(), fExtLanguageManager));

		stringBuilder.append("\n");

		appendPreviewOfTestCases(selectedTestCases, methodNode, deployedParameters, stringBuilder);

		stringBuilder.append(
				TestCasesExportHelper.generateSection(
						fMethodNode, deployedParameters, fTemplateText.getFooterTemplateText(), fExtLanguageManager));

		stringBuilder.append("\n");

		String result = stringBuilder.toString();
		result = TestCasesExportHelper.evaluateMinWidthOperators(result);

		return result;
	}

	private void appendPreviewOfTestCases(
			Collection<TestCaseNode> selectedTestCases,
			MethodNode methodNode,
			List<ParameterWithLinkingContext> deployedParameters,
			StringBuilder inOutStringBuilder) {

		List<TestCaseNode> testCases = createPreviewTestCasesSample(selectedTestCases);
		int sequenceIndex = 0;

		for (TestCaseNode testCase : testCases) {

			inOutStringBuilder.append(
					TestCasesExportHelper.generateTestCaseString(
							sequenceIndex++,
							testCase,
							methodNode,
							deployedParameters,
							fTemplateText.getTestCaseTemplateText(), 
							fExtLanguageManager));

			inOutStringBuilder.append("\n");
		}

	}

	private List<TestCaseNode> createPreviewTestCasesSample(Collection<TestCaseNode> selectedTestCases) {

		final int MAX_PREVIEW_TEST_CASES = 5;

		if (selectedTestCases == null) {
			return createRandomTestCasesSample(MAX_PREVIEW_TEST_CASES);
		}

		return createSampleFromSelectedTestCases(selectedTestCases, MAX_PREVIEW_TEST_CASES);
	}

	private List<TestCaseNode> createSampleFromSelectedTestCases(
			Collection<TestCaseNode> selectedTestCases,
			final int maxPreviewTestCases) {

		List<TestCaseNode> testCases = new ArrayList<TestCaseNode>();

		int cnt = Math.min(maxPreviewTestCases, selectedTestCases.size());
		List<TestCaseNode> selectedTestCasesList = new ArrayList<TestCaseNode>(selectedTestCases);

		for (int index = 0; index < cnt; index++) {
			testCases.add(selectedTestCasesList.get(index));
		}

		return testCases;
	}

	private List<TestCaseNode> createRandomTestCasesSample(
			final int maxPreviewTestCases) {

		List<TestCaseNode> testCases = new ArrayList<TestCaseNode>();

		for (int index = 0; index < maxPreviewTestCases; index++) {
			testCases.add(createTestCase(fMethodNode, index));
		}

		return testCases;
	}

	private TestCaseNode createTestCase(MethodNode methodNode, int testCaseNumber) {

		Random randomGenerator = new Random();
		List<ChoiceNode> choiceNodes = new ArrayList<ChoiceNode>();
		List<AbstractParameterNode> parameters = methodNode.getParameters();

		for (AbstractParameterNode abstractParameterNode : parameters) {
			
			BasicParameterNode methodParameterNode = (BasicParameterNode) abstractParameterNode;
			
			ChoiceNode choiceNode;
			
			if (methodParameterNode.isExpected()) {
				choiceNode = new ChoiceNode(ChoiceNode.ASSIGNMENT_NAME, methodParameterNode.getDefaultValue(), null);
				choiceNode.setParent(methodParameterNode);
			} else {
				String parameterName = methodParameterNode.getName();
				choiceNode = getRandomChoiceNode(methodNode, parameterName, randomGenerator);
			}
			
			choiceNodes.add(choiceNode);
		}

		TestCaseNode testCaseNode = 
				new TestCaseNode(CommonConstants.DEFAULT_NEW_TEST_SUITE_NAME, null, choiceNodes);

		testCaseNode.setParent(methodNode);

		return testCaseNode;
	}

	ChoiceNode getRandomChoiceNode(MethodNode methodNode, String parameterName, Random randomGenerator) {

		BasicParameterNode methodParameterNode = (BasicParameterNode)methodNode.findParameter(parameterName);
		List<ChoiceNode> choices = methodParameterNode.getLeafChoicesWithCopies();

		ChoiceNode choiceNode = choices.get(randomGenerator.nextInt(choices.size()));
		return choiceNode;
	}

	protected MethodNode getMethodNode() {
		return fMethodNode;
	}

}
