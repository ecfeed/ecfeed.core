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

import com.ecfeed.core.model.ChoiceCondition;
import com.ecfeed.core.model.LabelCondition;
import com.ecfeed.core.model.ParameterCondition;

public interface IStatementVisitor {

	public Object visit(StaticStatement statement) throws Exception;
	public Object visit(StatementArray statement) throws Exception;
	public Object visit(ExpectedValueStatement statement) throws Exception;
	public Object visit(RelationStatement statement) throws Exception;
	public Object visit(LabelCondition condition) throws Exception;
	public Object visit(ChoiceCondition condition) throws Exception;
	public Object visit(ParameterCondition condition) throws Exception;
	public Object visit(ValueCondition condition) throws Exception;
}
