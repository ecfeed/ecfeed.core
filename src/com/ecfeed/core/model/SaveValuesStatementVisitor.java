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

import java.util.Map;

public class SaveValuesStatementVisitor implements IStatementVisitor {

	private Map<Integer,String> fInOutHashesAndValues;

	public SaveValuesStatementVisitor(Map<Integer, String> inOutHashesAndValues) {

		fInOutHashesAndValues = inOutHashesAndValues;
	}

	public Map<Integer,String> getValues() {
		return fInOutHashesAndValues;
	}

	@Override
	public Object visit(ExpectedValueStatement statement) throws Exception {

		// TODO DE-NO
		return null;
	}

	@Override
	public Object visit(RelationStatement statement) throws Exception {

		IStatementCondition statementCondition = statement.getCondition();
		statementCondition.accept(this);
		return null;
	}

	@Override
	public Object visit(ValueCondition condition) throws Exception {

		int hashCode = condition.hashCode();

		String value = condition.getRightValue();

		fInOutHashesAndValues.put(hashCode, value);
		return null;
	}

	@Override
	public Object visit(StatementArray statement) throws Exception {

		for (AbstractStatement abstractStatement : statement.getStatements()) {
			abstractStatement.accept(this);
		}

		return null;
	}

	@Override
	public Object visit(StaticStatement statement) throws Exception {
		return null;
	}

	@Override
	public Object visit(LabelCondition condition) throws Exception {

		return null;
	}

	@Override
	public Object visit(ChoiceCondition condition) throws Exception {
		return null;
	}

	@Override
	public Object visit(ParameterCondition condition) throws Exception {
		return null;
	}

}
