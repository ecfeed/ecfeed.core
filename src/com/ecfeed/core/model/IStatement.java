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
import java.util.Optional;

import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;

public interface IStatement{
	String createSignature(IExtLanguageManager extLanguageManager);
	EvaluationResult evaluate(List<ChoiceNode> values);
	boolean setExpectedValues(List<ChoiceNode> testCaseChoices);
	boolean isEqualTo(IStatement statement);
	Object accept(IStatementVisitor visitor) throws Exception;
	boolean isAmbiguous(List<List<ChoiceNode>> values, MessageStack outWhyAmbiguous, IExtLanguageManager extLanguageManager);
	boolean isAmbiguous(List<List<ChoiceNode>> values);
	List<ChoiceNode> getChoices();
	List<ChoiceNode> getChoices(BasicParameterNode methodParameterNode);
	List<String> getLabels(BasicParameterNode methodParameterNode);
	void derandomize();
	AbstractStatement makeClone(); // TODO MO-RE obsolete ?
	AbstractStatement createCopy(NodeMapper mapper); // TODO MO-RE obsolete ?
	AbstractStatement makeClone(Optional<NodeMapper> mapper);
	String getLeftOperandName();
	BasicParameterNode getLeftParameter();
	CompositeParameterNode getLeftParameterLinkingContext();
	boolean mentions(int methodParameterIndex);
	boolean isConsistent(IParametersAndConstraintsParentNode topParentNode);
}
