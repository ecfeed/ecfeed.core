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


public class ExportTemplateFactory {

	MethodNode fMethodNode;

	public ExportTemplateFactory(MethodNode methodNode) {
		fMethodNode = methodNode;
	}

	public IExportTemplate createDefaultTemplate() {
		return createTemplate(getDefaultFormat());
	}

	public IExportTemplate createTemplate(String formatName) {

		IExportTemplate exportTemplate = createTemplateIntr(formatName);
		exportTemplate.initialize();

		return exportTemplate;
	}

	private IExportTemplate createTemplateIntr(String formatName) {

		if (formatName.equals(CsvExportTemplate.getTemplateFormatSt())) {
			return new CsvExportTemplate(fMethodNode);
		}
		if (formatName.equals(XmlExportTemplate.getTemplateFormatSt())) {
			return new XmlExportTemplate(fMethodNode);
		}
		if (formatName.equals(GherkinExportTemplate.getTemplateFormatSt())) {
			return new GherkinExportTemplate(fMethodNode);
		}		
		return null;
	}

	public static String[] getAvailableExportFormats() {
		String[] formats = 
			{ 
				CsvExportTemplate.getTemplateFormatSt(), 
				XmlExportTemplate.getTemplateFormatSt(), 
				GherkinExportTemplate.getTemplateFormatSt() };
		return formats;
	}

	private static String getDefaultFormat() {
		return CsvExportTemplate.getTemplateFormatSt();
	}

}
