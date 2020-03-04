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

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.StringHelper;

public class CsvExportTemplate extends AbstractExportTemplate {

	public CsvExportTemplate(MethodNode methodNode) {

		super(methodNode, createDefaultTemplateText(methodNode.getParametersCount()));
	}

	private static String createDefaultTemplateText(int methodParametersCount) {

		String defaultTemplateText =
				TemplateText.createTemplateText(
						createDefaultHeaderTemplate(methodParametersCount),
						createDefaultTestCaseTemplate(methodParametersCount),
						"");

		return defaultTemplateText;
	}

	@Override
	public String getFileExtension() {
		return "csv";
	}

	@Override 
	public String getTemplateFormat() {
		return getTemplateFormatSt();
	}

	public static String getTemplateFormatSt() {
		final String FORMAT_CSV = "CSV";
		return FORMAT_CSV;
	}

	private static String createDefaultHeaderTemplate(int methodParametersCount) {

		final String NAME_TAG = "name";
		return createParameterTemplate(NAME_TAG, methodParametersCount);
	}

	private static String createDefaultTestCaseTemplate(int methodParametersCount) {

		final String VALUE_TAG = "value";
		return createParameterTemplate(VALUE_TAG, methodParametersCount);
	}

	private static String createParameterTemplate( String parameterTag, int methodParametersCount) {

		String template = new String();

		for (int cnt = 1; cnt <= methodParametersCount; ++cnt) {
			if (cnt > 1) {
				template = template + ",";
			}
			String paramDescription = "$" + cnt + "." + parameterTag;
			template = template + paramDescription;
		}

		return template;
	}

}
