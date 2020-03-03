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

import java.io.IOException;
import java.io.OutputStream;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.StringHelper;


public class BaseTestCasesExporter {

	private IExportTemplate fExportTemplate;
	private int fExportedTestCases;

	public BaseTestCasesExporter(IExportTemplate exportTemplate) {

		fExportTemplate = exportTemplate;
	}
	
	protected IExportTemplate getExportTemplate() {
		
		return fExportTemplate;
	}

	protected void exportHeader(
			MethodNode method, 
			OutputStream outputStream) throws IOException {

		fExportedTestCases = 0;

		if (fExportTemplate.getHeaderTemplate() == null) {
			return;
		}

		String section = 
				TestCasesExportHelper.generateSection(
						method, fExportTemplate.getHeaderTemplate()) + StringHelper.newLine();

		outputStream.write(section.getBytes());
	}

	protected void exportTestCase(TestCaseNode testCase, OutputStream outputStream) throws IOException {

		String testCaseText = 
				TestCasesExportHelper.generateTestCaseString(
						fExportedTestCases, testCase, fExportTemplate.getTestCaseTemplate())
				+ StringHelper.newLine();

		outputStream.write(testCaseText.getBytes());
		++fExportedTestCases;
	}

	protected void exportFooter(MethodNode method, OutputStream outputStream) throws IOException {

		if (fExportTemplate.getFooterTemplate() == null) {
			return;
		}

		String section = 
				TestCasesExportHelper.generateSection(
						method, fExportTemplate.getFooterTemplate()) + StringHelper.newLine();

		outputStream.write(section.getBytes());
	}

}