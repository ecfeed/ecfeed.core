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
import com.ecfeed.core.utils.ExtLanguageManager;


public class ExportTemplateFactory {

	private MethodNode fMethodNode;
	private ExtLanguageManager fExtLanguage;

	public ExportTemplateFactory(MethodNode methodNode, ExtLanguageManager extLanguage) {
		fMethodNode = methodNode;
		fExtLanguage = extLanguage;
	}

	public IExportTemplate createDefaultTemplate() {
		return createTemplate(getDefaultFormat());
	}

	public IExportTemplate createDefaultTemplate(String format) {

		if (format == null) {
			return null;
		}

		return createTemplate(format);
	}

	public IExportTemplate createTemplate(String formatName) {

		IExportTemplate exportTemplate = createTemplateIntr(formatName);

		return exportTemplate;
	}

	private IExportTemplate createTemplateIntr(String formatName) {

		if (formatName.equals(CsvExportTemplate.getTemplateFormatSt())) {
			return new CsvExportTemplate(fMethodNode, fExtLanguage);
		}
		if (formatName.equals(XmlExportTemplate.getTemplateFormatSt())) {
			return new XmlExportTemplate(fMethodNode, fExtLanguage);
		}
		if (formatName.equals(GherkinExportTemplate.getTemplateFormatSt())) {
			return new GherkinExportTemplate(fMethodNode, fExtLanguage);
		}
		if (formatName.equals(JsonExportTemplate.getTemplateFormatSt())) {
			return new JsonExportTemplate(fMethodNode, fExtLanguage);
		}		
		
		return null;
	}

	public static String[] getAvailableExportFormats() {

		String[] formats = { 
				CsvExportTemplate.getTemplateFormatSt(), 
				XmlExportTemplate.getTemplateFormatSt(), 
				GherkinExportTemplate.getTemplateFormatSt(),
				JsonExportTemplate.getTemplateFormatSt()
		};

		return formats;
	}

	private static String getDefaultFormat() {
		return CsvExportTemplate.getTemplateFormatSt();
	}

}
