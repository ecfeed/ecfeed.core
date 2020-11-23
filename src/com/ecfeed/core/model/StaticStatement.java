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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;

public class StaticStatement extends AbstractStatement {

	public static final String STATIC_STATEMENT_TRUE_VALUE = "true";
	public static final String STATIC_STATEMENT_FALSE_VALUE = "false";
	public static final String STATIC_STATEMENT_NULL_VALUE = "null";

	private EvaluationResult fValue;

	public StaticStatement(EvaluationResult value, IModelChangeRegistrator modelChangeRegistrator) {

		super(modelChangeRegistrator);

		fValue = value;
	}

	public StaticStatement(boolean value, IModelChangeRegistrator modelChangeRegistrator) {

		super(modelChangeRegistrator);

		if (value) {
			fValue = EvaluationResult.TRUE;
		} else {
			fValue = EvaluationResult.FALSE;
		}
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> values) {
		return fValue;
	}

	@Override
	public String toString() {

		return convertToString(fValue);
	}

	@Override
	public String createSignature(IExtLanguageManager extLanguageManager) {

		return convertToString(fValue);
	}

	public static String convertToString(EvaluationResult result) {

		switch(result) {

		case TRUE:
			return STATIC_STATEMENT_TRUE_VALUE;
		case FALSE:
			return STATIC_STATEMENT_FALSE_VALUE;
		case INSUFFICIENT_DATA:
			return STATIC_STATEMENT_NULL_VALUE;
		}

		return STATIC_STATEMENT_NULL_VALUE;
	}

	@Override
	public StaticStatement getCopy(){
		return new StaticStatement(fValue, getModelChangeRegistrator());
	}

	@Override
	public boolean updateReferences(MethodNode method){
		return true;
	}

	@Override
	public boolean isEqualTo(IStatement statement){
		if(statement instanceof StaticStatement == false){
			return false;
		}
		StaticStatement compared = (StaticStatement)statement;
		return getValue() == compared.getValue();
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public boolean mentions(int methodParameterIndex) {
		return false;
	}

	public String getLeftOperandName(){
		return toString();
	}

	public EvaluationResult getValue(){
		return fValue;
	}

	public void setValue(boolean value) {
		fValue = EvaluationResult.convertFromBoolean(value);
	}

	@Override
	public boolean isAmbiguous(List<List<ChoiceNode>> values, MessageStack messageStack, IExtLanguageManager extLanguageManager) {
		return false;
	}

	@Override
	public List<ChoiceNode> getListOfChoices() {
		return new ArrayList<ChoiceNode>();
	}

}
