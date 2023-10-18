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
import com.ecfeed.core.utils.IExtLanguageManager;

public class CustomExportTemplate extends AbstractExportTemplate {

	public CustomExportTemplate(MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		super(methodNode, createDefaultTemplateText(methodNode.getParametersCount()), extLanguageManager);
	}

	private static String createDefaultTemplateText(int methodParametersCount) {

		String defaultTemplateText =
				TemplateText.createTemplateText(
						createDefaultHeaderTemplate(methodParametersCount),
						createDefaultTestCaseTemplate(methodParametersCount),
						null);

		return defaultTemplateText;
	}

	@Override
	public String getFileExtension() {
		return "txt";
	}

	@Override 
	public String getTemplateFormat() {
		return getTemplateFormatSt();
	}

	public static String getTemplateFormatSt() {
		return "Template - Custom";
	}

	private static String createDefaultHeaderTemplate(int methodParametersCount) {
		String template = new String();

		for (int cnt = 1; cnt <= methodParametersCount; ++cnt) {
			if (cnt > 1) {
				template = template + "|";
			}
			String paramDescription = "($" + cnt + ".name).min_width(25, CENTER)";
			template = template + paramDescription;
		}

		return "Test cases:\n\nPlease adapt the template to your needs...\n\n" + template;
	}

	private static String createDefaultTestCaseTemplate(int methodParametersCount) {
		String template = new String();

		for (int cnt = 1; cnt <= methodParametersCount; ++cnt) {
			if (cnt > 1) {
				template = template + ",";
			}
			String paramDescription = "($" + cnt + ".value).min_width(25, LEFT)";
			template = template + paramDescription;
		}
		
		return template;
	}

	private static String createParameterTemplate(String parameterTag, int methodParametersCount) {

		String template = new String();

		for (int cnt = 1; cnt <= methodParametersCount; ++cnt) {
			if (cnt > 1) {
				template = template + ",";
			}
			String paramDescription = "($" + cnt + "." + parameterTag + ").min_width(25, LEFT)";
			template = template + paramDescription;
		}

		return template;
	}

}
