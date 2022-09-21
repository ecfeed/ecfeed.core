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

import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.StringHelper;

public class TypeChangeStatementVisitor implements IStatementVisitor {

	private MethodParameterNode fMethodParameterNode;
	private ParameterConversionDefinition fParameterConversionDefinition;

	public TypeChangeStatementVisitor(
			MethodParameterNode methodParameterNode,
			ParameterConversionDefinition inOutParameterConversionDefinition) {

		fMethodParameterNode = methodParameterNode;
		fParameterConversionDefinition = inOutParameterConversionDefinition;
	}

	@Override
	public Object visit(ExpectedValueStatement statement) throws Exception {

		// TODO DE-NO move code to ExpectedValueStatement.convert(parameterConversionItem);

		MethodParameterNode methodParameterNodeFromConstraint = statement.getLeftMethodParameterNode(); 

		if (methodParameterNodeFromConstraint != fMethodParameterNode) {
			return null;
		}

		ChoiceNode choiceNode = statement.getChoice();
		String valueString = choiceNode.getValueString();

		int itemCount = fParameterConversionDefinition.getItemCount();

		for (int index = 0; index < itemCount; index++) {
			ParameterConversionItem parameterConversionItem = fParameterConversionDefinition.getCopyOfItem(index);

			String srcString = parameterConversionItem.getSrcPart().getStr();

			if (StringHelper.isEqual(srcString, valueString)) {
				String dstString = parameterConversionItem.getDstPart().getStr();
				choiceNode.setValueString(dstString);
			}
		}

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

		// TODO DE-NO move code to ValueCondtion.convert(parameterConversionItem);

		RelationStatement parentRelationStatement = condition.getParentRelationStatement();

		MethodParameterNode methodParameterNodeFromConstraint = parentRelationStatement.getLeftParameter();

		if (methodParameterNodeFromConstraint != fMethodParameterNode) {
			return null;
		}

		String valueString = condition.getRightValue();

		int itemCount = fParameterConversionDefinition.getItemCount();

		for (int index = 0; index < itemCount; index++) {
			ParameterConversionItem parameterConversionItem = fParameterConversionDefinition.getCopyOfItem(index);

			String srcString = parameterConversionItem.getSrcPart().getStr();

			if (StringHelper.isEqual(srcString, valueString)) {
				String dstString = parameterConversionItem.getDstPart().getStr();
				condition.setRightValue(dstString);
			}
		}

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


		int itemCount = fParameterConversionDefinition.getItemCount();

		for (int index = 0; index < itemCount; index++) {
			ParameterConversionItem parameterConversionItem = fParameterConversionDefinition.getCopyOfItem(index);
			condition.convert(parameterConversionItem);
		}

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
