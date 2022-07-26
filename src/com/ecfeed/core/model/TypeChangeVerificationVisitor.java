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
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.ParameterConversionItemPartForValue;

public class TypeChangeVerificationVisitor implements IStatementVisitor {

	private ParameterConversionDefinition fInOutParameterConversionDefinition;
	ITypeAdapter<?> fNewTypeAdapter;

	public TypeChangeVerificationVisitor(String newType,
			ParameterConversionDefinition inOutParameterConversionDefinition) {

		fInOutParameterConversionDefinition = inOutParameterConversionDefinition;

		ITypeAdapterProvider typeAdapterProvider = new TypeAdapterProviderForJava();
		fNewTypeAdapter = typeAdapterProvider.getAdapter(newType);
	}

	@Override
	public Object visit(ExpectedValueStatement statement) throws Exception {

		ChoiceNode choiceNode = statement.getCondition();
		String valueString = choiceNode.getValueString();

		verifyConversionOfValue(statement.toString(), valueString);

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

		String valueString = condition.getRightValue();
		verifyConversionOfValue(condition.toString(), valueString);

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

	private void verifyConversionOfValue(String objectsContainingItem, String valueString) {

		boolean canConvert = fNewTypeAdapter.canCovertWithoutLossOfData(valueString, false);

		if (!canConvert) {

			ParameterConversionItemPartForValue srcPart = 
					new ParameterConversionItemPartForValue(valueString);

			ParameterConversionItem parameterConversionItem = 
					new ParameterConversionItem(
							srcPart, null, objectsContainingItem);

			fInOutParameterConversionDefinition.addItem(parameterConversionItem);
		}
	}

}
