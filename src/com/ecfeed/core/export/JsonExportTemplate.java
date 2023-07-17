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
import java.util.List;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class JsonExportTemplate extends AbstractExportTemplate {

	public JsonExportTemplate(MethodNode methodNode, IExtLanguageManager extLanguageManager) {
		super(methodNode, createDefaultTemplateText(methodNode), extLanguageManager);
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
		return "json";
	}

	@Override 
	public String getTemplateFormat() {
		return getTemplateFormatSt();
	}

	public static String getTemplateFormatSt() {

		return "JSON";
	}	

	private static String createDefaultHeaderTemplate() {
		return "{ \n\t\"testCases\" : [";
	}

	private static String createDefaultFooterTemplate() {
		return "\t]\n} ";
	}

	private static String createDefaultTestCaseTemplate(List<AbstractParameterNode> parameters) {
		StringBuilder template = new StringBuilder();

		template.append("\t\t{\n\t\t\t\"index\": %index, \n");
		template.append(createParametersTemplate(parameters));
		template.append( "\t\t}");

		return template.toString();
	}

	private static String createParametersTemplate(List<AbstractParameterNode> parameters) {
		List<String> elements = new ArrayList<>();

		for (int index = 0; index < parameters.size(); index++) {
			elements.add(createParameterString(index+1));
		}

		StringBuilder template = new StringBuilder();

		template.append(String.join(", \n", elements));
		template.append("\n");

		return template.toString();
	}

	private static String createParameterString(int counter) {

		return "\t\t\t\"$" + counter + ".name\":\"$" + counter + ".value\"";
	}
}
