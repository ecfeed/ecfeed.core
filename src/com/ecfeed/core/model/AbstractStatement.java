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
import com.ecfeed.core.utils.ParameterConversionItem;

public abstract class AbstractStatement implements IStatement {

	AbstractStatement fParent = null;
	private final IModelChangeRegistrator fModelChangeRegistrator;
	public abstract boolean mentionsChoiceOfParameter(BasicParameterNode parameter);
	public abstract boolean updateReferences(IParametersAndConstraintsParentNode parent);

	protected abstract void convert(ParameterConversionItem parameterConversionItem);

	public AbstractStatement(IModelChangeRegistrator modelChangeRegistrator) {

		fModelChangeRegistrator = modelChangeRegistrator;
	}

	public abstract String getLeftParameterCompositeName();
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

	public boolean mentions(AbstractParameterNode parameter) {
		return false;
	}

	public boolean mentions(AbstractParameterNode parameter, String label) {
		return false;
	}

	public boolean mentionsParameterAndOrderRelation(AbstractParameterNode parameter) {
		return false;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> values) {
		return EvaluationResult.FALSE;
	}

	@Override
	public boolean setExpectedValues(List<ChoiceNode> values) {
		return false;
	}

}