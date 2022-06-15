/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.List;

import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;

public interface IStatement{
	public String createSignature(IExtLanguageManager extLanguageManager);
	public EvaluationResult evaluate(List<ChoiceNode> values);
	public boolean setExpectedValues(List<ChoiceNode> testCaseChoices);
	public boolean isEqualTo(IStatement statement);
	public Object accept(IStatementVisitor visitor) throws Exception;
	public boolean isAmbiguous(List<List<ChoiceNode>> values, MessageStack outWhyAmbiguous, IExtLanguageManager extLanguageManager);
	public boolean isAmbiguous(List<List<ChoiceNode>> values);
	public List<ChoiceNode> getChoices();
	public List<String> getLabels(MethodParameterNode methodParameterNode);
//	public List<String> getValues(MethodParameterNode methodParameterNode);
	public void derandomize();
}
