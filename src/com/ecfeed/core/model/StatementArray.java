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

public class StatementArray extends AbstractStatement {

	private StatementArrayOperator fOperator;
	private List<AbstractStatement> fStatements;

	public StatementArray(StatementArrayOperator operator, IModelChangeRegistrator modelChangeRegistrator) {

		super(modelChangeRegistrator);

		fStatements = new ArrayList<AbstractStatement>();
		fOperator = operator;
	}

	@Override
	public List<AbstractStatement> getChildren() {
		return fStatements;
	}

	public boolean removeChild(AbstractStatement child) {
		return getChildren().remove(child);
	}

	@Override
	public boolean mentions(ChoiceNode choice) {

		for (AbstractStatement child : fStatements) {
			if (child.mentions(choice)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mentions(MethodParameterNode parameter) {

		for (AbstractStatement child : fStatements) {
			if (child.mentions(parameter)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mentionsParameterAndOrderRelation(MethodParameterNode parameter) {

		for (AbstractStatement child : fStatements) {
			if (child.mentionsParameterAndOrderRelation(parameter)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> values) {

		if (fStatements.size() == 0) {
			return EvaluationResult.FALSE;
		}

		switch (fOperator) {

		case AND:
			return evaluateForAndOperator(values);
		case OR:
			return evaluateForOrOperator(values);
		}
		return EvaluationResult.FALSE;
	}

	private EvaluationResult evaluateForOrOperator(List<ChoiceNode> values) {

		boolean insufficient_data = false;

		for (IStatement statement : fStatements) {

			EvaluationResult evaluationResult = statement.evaluate(values);

			if (evaluationResult == EvaluationResult.TRUE) {
				return EvaluationResult.TRUE;
			}

			if (evaluationResult == EvaluationResult.INSUFFICIENT_DATA) {
				insufficient_data = true;
			}			
		}

		if (insufficient_data) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		return EvaluationResult.FALSE;
	}

	private EvaluationResult evaluateForAndOperator(List<ChoiceNode> values) {

		boolean insufficient_data = false;

		for (IStatement statement : fStatements) {

			EvaluationResult evaluationResult = statement.evaluate(values);

			if (evaluationResult == EvaluationResult.FALSE) {
				return EvaluationResult.FALSE;
			}

			if (evaluationResult == EvaluationResult.INSUFFICIENT_DATA) {
				insufficient_data = true;
			}
		}

		if (insufficient_data) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		return EvaluationResult.TRUE;
	}

	@Override
	public String toString() {

		String result = new String("(");
		for (int i = 0; i < fStatements.size(); i++) {
			result += fStatements.get(i).toString();
			if (i < fStatements.size() - 1) {
				switch(fOperator) {
				case AND:
					result += " \u2227 ";
					break;
				case OR:
					result += " \u2228 ";
					break;
				}
			}
		}
		return result + ")";
	}

	@Override
	public String createSignature(IExtLanguageManager extLanguageManager) {

		String result = new String("(");

		for (int i = 0; i < fStatements.size(); i++) {

			final AbstractStatement abstractStatement = fStatements.get(i);

			result += abstractStatement.createSignature(extLanguageManager);

			if (i < fStatements.size() - 1) {
				switch(fOperator) {
					case AND:
						result += " \u2227 ";
						break;
					case OR:
						result += " \u2228 ";
						break;
				}
			}
		}

		return result + ")";
	}

	@Override
	public StatementArray getCopy() {

		StatementArray copy = new StatementArray(fOperator, getModelChangeRegistrator());

		for (AbstractStatement statement: fStatements) {
			copy.addStatement(statement.getCopy());
		}

		return copy;
	}

	@Override
	public boolean updateReferences(MethodNode method) {

		for (AbstractStatement statement: fStatements) {
			if (!statement.updateReferences(method)) return false;
		}
		return true;
	}

	List<AbstractStatement> getStatements() {
		return fStatements;
	}

	@Override 
	public boolean compare(IStatement statement) {

		if (statement instanceof StatementArray == false) {
			return false;
		}
		StatementArray compared = (StatementArray)statement;

		if (getOperator() != compared.getOperator()) {
			return false;
		}

		if (getStatements().size() != compared.getStatements().size()) {
			return false;
		}

		for (int i = 0; i < getStatements().size(); i++) {
			if (getStatements().get(i).compare(compared.getStatements().get(i)) == false) {
				return false;
			}
		}

		return true;
	}

	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public boolean mentions(int methodParameterIndex) {

		for ( AbstractStatement abstractStatement : fStatements) {
			if (abstractStatement.mentions(methodParameterIndex)) {
				return true;
			}
		}

		return false;
	}

	public String getLeftOperandName() {
		return fOperator.toString();
	}

	public StatementArrayOperator getOperator() {
		return fOperator;
	}

	public void setOperator(StatementArrayOperator operator) {
		fOperator = operator;
	}

	public void addStatement(AbstractStatement statement, int index) {

		fStatements.add(index, statement);
		statement.setParent(this);
	}

	public void addStatement(AbstractStatement statement) {
		addStatement(statement, fStatements.size());
	}

	@Override
	public boolean isAmbiguous(
			List<List<ChoiceNode>> values, MessageStack messageStack, IExtLanguageManager extLanguageManager) {

		for (AbstractStatement statement : fStatements) {
			if (statement.isAmbiguous(values, messageStack, extLanguageManager)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<ChoiceNode> getListOfChoices() {

		List<ChoiceNode> result = new ArrayList<ChoiceNode>();

		for (AbstractStatement abstractStatement : fStatements) {

			List<ChoiceNode> listOfChoices = abstractStatement.getListOfChoices();

			if (listOfChoices != null) {
				result.addAll(listOfChoices);
			}
		}

		return null;
	}
}
