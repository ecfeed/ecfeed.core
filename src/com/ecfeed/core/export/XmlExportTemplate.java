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

import java.util.List;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.ExtLanguageManager;

public class XmlExportTemplate extends AbstractExportTemplate {

	public XmlExportTemplate(MethodNode methodNode, ExtLanguageManager extLanguage) {
		super(methodNode, createDefaultTemplateText(methodNode), extLanguage);
	}

	private static String createDefaultTemplateText(MethodNode methodNode) {

		String defaultTemplateText =
				TemplateText.createTemplateText(
						createDefaultHeaderTemplate(),
						createDefaultTestCaseTemplate(methodNode.getParameters()),
						createDefaultFooterTemplate());

		return defaultTemplateText;
	}

	@Override
	public String getFileExtension() {
		return "xml";
	}

	@Override 
	public String getTemplateFormat() {
		return getTemplateFormatSt();
	}

	public static String getTemplateFormatSt() {
		final String FORMAT_XML = "XML";
		return FORMAT_XML;
	}	
	private static String createDefaultHeaderTemplate() {
		return "<TestCases>";
	}

	private static String createDefaultFooterTemplate() {
		return "</TestCases>";
	}

	private static String createDefaultTestCaseTemplate(List<AbstractParameterNode> parameters) {

		StringBuilder template = new StringBuilder();
		template.append("\t<TestCase ");
		template.append("testSuite=\"%suite\" ");
		template.append(createParametersTemplate(parameters));
		template.append("/>");

		return template.toString();
	}

	private static String createParametersTemplate(List<AbstractParameterNode> parameters) {

		StringBuilder template = new StringBuilder();
		int counter = 0;

		for (AbstractParameterNode node : parameters) {
			counter++;
			template.append(createParameterString(node.getName(), counter));
		}

		return template.toString();
	}

	private static String createParameterString(String name, int counter) {
		return name + "=" + "\"$" + counter + "." + "value" + "\" ";
	}

}
