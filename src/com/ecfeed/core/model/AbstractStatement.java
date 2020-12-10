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

public abstract class AbstractStatement implements IStatement {

	AbstractStatement fParent = null;
	private final IModelChangeRegistrator fModelChangeRegistrator;

	public AbstractStatement(IModelChangeRegistrator modelChangeRegistrator) {

		fModelChangeRegistrator = modelChangeRegistrator;
	}

	public abstract String getLeftOperandName();
	public abstract boolean mentions(int methodParameterIndex);

	public IModelChangeRegistrator getModelChangeRegistrator() {

		return fModelChangeRegistrator;
	}

	public AbstractStatement getParent() {
		return fParent;
	}

	public void setParent(AbstractStatement parent) {
		fParent = parent;
	}

	public List<AbstractStatement> getChildren() {
		return null;
	}

	public void replaceChild(AbstractStatement oldStatement, AbstractStatement newStatement) {

		List<AbstractStatement> children = getChildren();

		if(children == null) {
			return;
		}

		int index = children.indexOf(oldStatement);

		if(index == -1) {
			return;
		}

		newStatement.setParent(this);
		children.set(index, newStatement);
	}



	public boolean mentions(ChoiceNode choice) {
		return false;
	}

	public boolean mentions(MethodParameterNode parameter) {
		return false;
	}

	public boolean mentions(MethodParameterNode parameter, String label) {
		return false;
	}

	public boolean mentionsParameterAndOrderRelation(MethodParameterNode parameter) {
		return false;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> values) {
		return EvaluationResult.FALSE;
	}

	@Override
	public boolean setExpectedValue(List<ChoiceNode> values){
		return false;
	}

	@Override
	public boolean setExpectedValues(List<ChoiceNode> values) {
		return false;
	}

	public abstract AbstractStatement makeClone();

	public abstract boolean updateReferences(MethodNode method);
}