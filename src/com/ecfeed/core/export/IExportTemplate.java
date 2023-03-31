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

import java.util.Collection;
import java.util.List;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;


public interface IExportTemplate {

	String getDefaultTemplateText();
	void setTemplateText(String summaryTemplate);
	String getTemplateText();
	boolean isTemplateTextModified();
	String getFileExtension();
	String getTemplateFormat();

	String getFooterTemplate();
	String getHeaderTemplate();
	String getTestCaseTemplate();

	String createPreview(Collection<TestCaseNode> testCases, MethodNode methodNode, List<ParameterWithLinkingContext> deployedParameters);
}
