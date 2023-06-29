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

import com.ecfeed.core.model.NodeMapper.MappingDirection;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.ParameterConversionItem;

public interface IStatementCondition {
	public String createSignature(IExtLanguageManager extLanguageManager);
	public Object getCondition();
	public RelationStatement getParentRelationStatement();
	public void setParentRelationStatement(RelationStatement relationStatement);
	public EvaluationResult evaluate(List<ChoiceNode> values);
	public boolean adapt(List<ChoiceNode> values);
	public boolean compare(IStatementCondition condition);
	public Object accept(IStatementVisitor visitor) throws Exception;
	public boolean mentions(AbstractParameterNode abstractParameterNode);
	public boolean mentionsChoiceOfParameter(BasicParameterNode abstractParameterNode);
	public boolean isAmbiguous(List<List<ChoiceNode>> domain, MessageStack messageStack, IExtLanguageManager extLanguageManager);
	public List<ChoiceNode> getChoices();
	public List<ChoiceNode> getChoices(BasicParameterNode methodParameterNode);
	public void derandomize();
	public void convert(ParameterConversionItem parameterConversionItem);
	public String getLabel(BasicParameterNode methodParameterNode);
	public IStatementCondition makeClone();
	IStatementCondition createCopy(RelationStatement statement, NodeMapper mapper); // TODO MO-RE obsolete
	IStatementCondition makeClone(RelationStatement statement, Optional<NodeMapper> mapper);
	public boolean isConsistent(IParametersAndConstraintsParentNode topParentNode);
	public void replaceReferences(NodeMapper nodeMapper, MappingDirection mappingDirection);
}

