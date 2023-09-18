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

import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.ParameterConversionItemPartForValue;

public class TypeChangeVerificationStatementVisitor implements IStatementVisitor {

	private BasicParameterNode fMethodParameterNode;
	private String fOldType;
	private String fConstraintName;
	private ParameterConversionDefinition fInOutParameterConversionDefinition;
	private ITypeAdapter<?> fNewTypeAdapter;

	public TypeChangeVerificationStatementVisitor(
			BasicParameterNode methodParameterNode,
			String oldType,
			String newType,
			String constraintName,
			ParameterConversionDefinition inOutParameterConversionDefinition) {

		fMethodParameterNode = methodParameterNode;
		fOldType = oldType;
		fConstraintName = constraintName;
		fInOutParameterConversionDefinition = inOutParameterConversionDefinition;

		fNewTypeAdapter = JavaLanguageHelper.getTypeAdapter(newType);
	}

	@Override
	public Object visit(ExpectedValueStatement statement) throws Exception {
		
		BasicParameterNode methodParameterNodeFromConstraint = statement.getLeftMethodParameterNode(); 

		if (methodParameterNodeFromConstraint != fMethodParameterNode) {
			return null;
		}
		
		ChoiceNode choiceNode = statement.getChoice();
		String valueString = choiceNode.getValueString();

		verifyConversionOfValue(
				fMethodParameterNode, fOldType, valueString, choiceNode.isRandomizedValue(), statement.toString());

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

		RelationStatement parentRelationStatement = condition.getParentRelationStatement();
		
		BasicParameterNode methodParameterNodeFromConstraint = parentRelationStatement.getLeftParameter();
		
		if (methodParameterNodeFromConstraint != fMethodParameterNode) {
			return null;
		}
		
		String valueString = condition.getRightValue();
		verifyConversionOfValue(fMethodParameterNode, fOldType, valueString, false, fConstraintName + "(constraint)");

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

	private void verifyConversionOfValue(
			AbstractParameterNode abstractParameterNode,
			String oldType, 
			String valueString, 
			boolean isRandomized, 
			String objectsContainingItem) {

		boolean canConvert = fNewTypeAdapter.canCovertWithoutLossOfData(oldType, valueString, false);

		if (!canConvert) {

			ParameterConversionItemPartForValue srcPart = 
					new ParameterConversionItemPartForValue(abstractParameterNode, valueString);
			
			ParameterConversionItem parameterConversionItem = 
					new ParameterConversionItem(
							srcPart, null, isRandomized, objectsContainingItem);

			fInOutParameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItem);
		}
	}

}
