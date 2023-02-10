/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.serialization;

import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_CHOICE_STATEMENT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_LABEL_STATEMENT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_PARAMETER_STATEMENT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_STATEMENT_ARRAY_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_STATIC_STATEMENT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_VALUE_STATEMENT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.STATEMENT_EXPECTED_VALUE_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.STATEMENT_LABEL_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.STATEMENT_OPERATOR_AND_ATTRIBUTE_VALUE;
import static com.ecfeed.core.model.serialization.SerializationConstants.STATEMENT_OPERATOR_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.STATEMENT_OPERATOR_OR_ATTRIBUTE_VALUE;
import static com.ecfeed.core.model.serialization.SerializationConstants.STATEMENT_OPERATOR_ASSIGN_ATTRIBUTE_VALUE;
import static com.ecfeed.core.model.serialization.SerializationConstants.STATEMENT_RELATION_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.STATEMENT_RIGHT_PARAMETER_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.STATEMENT_RIGHT_VALUE_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.STATEMENT_STATIC_VALUE_ATTRIBUTE_NAME;

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.SignatureHelper;
import nu.xom.Attribute;
import nu.xom.Element;

import java.util.*;

public class XomStatementBuilder implements IStatementVisitor {

	private WhiteCharConverter fWhiteCharConverter = new WhiteCharConverter();
	private String fStatementParameterAttributeName;
	private String fStatementChoiceAttributeName;
	private IAbstractNode fConstraintParent;

	public XomStatementBuilder(String statementParameterAttributeName, String statementChoiceAttributeName) {

		this(null, statementParameterAttributeName, statementChoiceAttributeName);
	}

	public XomStatementBuilder(IAbstractNode constraintParent, String statementParameterAttributeName, String statementChoiceAttributeName) {

		fStatementParameterAttributeName = statementParameterAttributeName; 
		fStatementChoiceAttributeName = statementChoiceAttributeName;
		fConstraintParent = constraintParent;
	}

	@Override
	public Object visit(StaticStatement statement) throws Exception {

		Element targetStatementElement = new Element(CONSTRAINT_STATIC_STATEMENT_NODE_NAME);
		String attrName = STATEMENT_STATIC_VALUE_ATTRIBUTE_NAME;

		String attrValue = StaticStatement.convertToString(statement.getValue());

		XomBuilder.encodeAndAddAttribute(
				targetStatementElement, new Attribute(attrName, attrValue), fWhiteCharConverter);

		return targetStatementElement;
	}

	@Override
	public Object visit(StatementArray statement) throws Exception {

		Element targetStatementElement = new Element(CONSTRAINT_STATEMENT_ARRAY_NODE_NAME);
		Attribute operatorAttribute = null;

		switch(statement.getOperator()) {
		
		case AND:
			operatorAttribute = 
				new Attribute(STATEMENT_OPERATOR_ATTRIBUTE_NAME, STATEMENT_OPERATOR_AND_ATTRIBUTE_VALUE);
			break;
			
		case OR:
			operatorAttribute = 
				new Attribute(STATEMENT_OPERATOR_ATTRIBUTE_NAME, STATEMENT_OPERATOR_OR_ATTRIBUTE_VALUE);
			break;
			
		case ASSIGN:
			operatorAttribute = 
			new Attribute(STATEMENT_OPERATOR_ATTRIBUTE_NAME, STATEMENT_OPERATOR_ASSIGN_ATTRIBUTE_VALUE);
		break;
			
		}

		XomBuilder.encodeAndAddAttribute(targetStatementElement, operatorAttribute, fWhiteCharConverter);

		for (AbstractStatement child : statement.getChildren()) {
			targetStatementElement.appendChild((Element)child.accept(this));
		}
		return targetStatementElement;
	}

	@Override
	public Object visit(ExpectedValueStatement statement) throws Exception {

		String parameterName = statement.getLeftParameterCompositeName();
		ChoiceNode condition = statement.getChoice();
		Attribute parameterAttribute =
				new Attribute(fStatementParameterAttributeName, parameterName);

		Attribute valueAttribute =
				new Attribute(STATEMENT_EXPECTED_VALUE_ATTRIBUTE_NAME, condition.getValueString());

		Element targetStatementElement = new Element(CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME);
		XomBuilder.encodeAndAddAttribute(targetStatementElement, parameterAttribute, fWhiteCharConverter);
		XomBuilder.encodeAndAddAttribute(targetStatementElement, valueAttribute, fWhiteCharConverter);

		return targetStatementElement;
	}

	@Override
	public Object visit(RelationStatement statement) throws Exception {

		IAbstractNode parameter = statement.getLeftParameter();

		String prefix = getCommonParent(parameter);

		String parameterName = prefix + parameter.getName();

		Attribute parameterAttribute =
				new Attribute(fStatementParameterAttributeName, parameterName);

		Attribute relationAttribute =
				new Attribute(STATEMENT_RELATION_ATTRIBUTE_NAME, statement.getRelation().getName());

		IStatementCondition condition = statement.getCondition();
		Element targetStatementElement = (Element)condition.accept(this);

		XomBuilder.encodeAndAddAttribute(targetStatementElement, parameterAttribute, fWhiteCharConverter);
		XomBuilder.encodeAndAddAttribute(targetStatementElement, relationAttribute, fWhiteCharConverter);

		return targetStatementElement;
	}

	@Override
	public Object visit(LabelCondition condition) throws Exception {

		Element targetLabelElement = new Element(CONSTRAINT_LABEL_STATEMENT_NODE_NAME);

		XomBuilder.encodeAndAddAttribute(
				targetLabelElement, 
				new Attribute(STATEMENT_LABEL_ATTRIBUTE_NAME, condition.getRightLabel()), 
				fWhiteCharConverter);

		return targetLabelElement;
	}

	@Override
	public Object visit(ChoiceCondition condition) throws Exception {

		ChoiceNode choice = condition.getRightChoice();
		Element targetChoiceElement = new Element(CONSTRAINT_CHOICE_STATEMENT_NODE_NAME);

		XomBuilder.encodeAndAddAttribute(
				targetChoiceElement, 
				new Attribute(fStatementChoiceAttributeName, choice.getQualifiedName()),
				fWhiteCharConverter);

		return targetChoiceElement;
	}

	@Override
	public Object visit(ParameterCondition condition) throws Exception {

		BasicParameterNode rightMethodParameterNode = condition.getRightParameterNode();

		String prefix = getCommonParent(rightMethodParameterNode);

		Element targetParameterElement = new Element(CONSTRAINT_PARAMETER_STATEMENT_NODE_NAME);

		XomBuilder.encodeAndAddAttribute(
				targetParameterElement, 
				new Attribute(STATEMENT_RIGHT_PARAMETER_ATTRIBUTE_NAME, prefix + rightMethodParameterNode.getName()),
				fWhiteCharConverter);

		return targetParameterElement;
	}

	@Override
	public Object visit(ValueCondition condition) throws Exception {

		Element targetParameterElement = new Element(CONSTRAINT_VALUE_STATEMENT_NODE_NAME);

		XomBuilder.encodeAndAddAttribute(
				targetParameterElement, 
				new Attribute(STATEMENT_RIGHT_VALUE_ATTRIBUTE_NAME, condition.getRightValue()), 
				fWhiteCharConverter);

		return targetParameterElement;
	}	

	private String getCommonParent(IAbstractNode parameter) {

		if (fConstraintParent == null || parameter == null) {
			return "";
		}

		LinkedList<String> prefixes = new LinkedList<>();

		while ((parameter.getParent() != fConstraintParent) && (parameter.getParent() != parameter.getRoot())) {
			parameter = parameter.getParent();
			prefixes.addFirst(parameter.getName());
		}

		return prefixes.size() > 0 ? String.join(SignatureHelper.SIGNATURE_NAME_SEPARATOR, prefixes) + SignatureHelper.SIGNATURE_NAME_SEPARATOR : "";
	}
}

