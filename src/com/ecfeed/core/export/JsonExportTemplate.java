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
import com.ecfeed.core.utils.StringHelper;

public class JsonExportTemplate extends AbstractExportTemplate {

	public static final String HEADER_MARKER = "[Header]";
	public static final String TEST_CASE_MARKER = "[TestCase]";
	public static final String FOOTER_MARKER = "[Footer]";

	public JsonExportTemplate(MethodNode methodNode) {
		super(methodNode);
	}

	@Override
	public String createDefaultTemplateText() {

		MethodNode methodNode = getMethodNode();

		String defaultTemplateText =
				StringHelper.appendNewline(HEADER_MARKER)
				+ StringHelper.appendNewline(createDefaultHeaderTemplate())
				+ StringHelper.appendNewline(TEST_CASE_MARKER)
				+ StringHelper.appendNewline(createDefaultTestCaseTemplate(methodNode.getParameters()))
				+ StringHelper.appendNewline(FOOTER_MARKER)
				+ StringHelper.appendNewline(createDefaultFooterTemplate());

		setDefaultTemplateText(defaultTemplateText);

		return defaultTemplateText;		
	}

	@Override
	public String getFileExtension() {
		return "json";
	}

	@Override 
	public String getTemplateFormat() {
		return getTemplateFormatSt();
	}

	public static String getTemplateFormatSt() {

		final String FORMAT_JSON = "JSON";
		return FORMAT_JSON;
	}	

	private static String createDefaultHeaderTemplate() {
		return "{ \n\t\"testCases\" : [";
	}

	private static String createDefaultFooterTemplate() {
		return "\t]\n} ";
	}

	private static String createDefaultTestCaseTemplate(List<AbstractParameterNode> parameters) {

		StringBuilder template = new StringBuilder();

		template.append("\t\t{\n\t\t\t\"index\":\"%index\", \n");

		template.append(createParametersTemplate(parameters));

		template.append( "\t\t},");

		return template.toString();
	}

	private static String createParametersTemplate(List<AbstractParameterNode> parameters) {

		StringBuilder template = new StringBuilder();

		int parametersSize = parameters.size();

		for (int index = 0; index < parametersSize; index++) {
			template.append(createParameterString(index+1));			
		}

		return template.toString();
	}

	private static String createParameterString(int counter) {

		return "\t\t\t\"$" + counter + ".name\":\"$" + counter + ".value\", \n";
	}

}
