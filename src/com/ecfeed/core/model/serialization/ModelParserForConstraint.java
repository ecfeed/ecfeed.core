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
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_STATEMENT_ARRAY_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_STATIC_STATEMENT_NODE_NAME;

import java.util.Optional;

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.AssignmentStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StatementArrayOperator;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.type.adapter.JavaPrimitiveTypePredicate;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForConstraint implements IModelParserForConstraint {

	private static final String EMPTY_PARAMETER_WHILE_PARSING_VALUE_STATEMENT = "Empty parameter while parsing value statement.";
	private WhiteCharConverter fWhiteCharConverter = new WhiteCharConverter();

	public ModelParserForConstraint() {
	}

	public Optional<ConstraintNode> parseConstraint(Element element, MethodNode method, ListOfStrings errorList) throws ParserException {

		String name;

		try {
			ModelParserHelper.assertNodeTag(element.getQualifiedName(), CONSTRAINT_NODE_NAME, errorList);
			name = ModelParserHelper.getElementName(element, fWhiteCharConverter, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}

		ConstraintType constraintType = getConstraintType(element, errorList);

		Optional<AbstractStatement> precondition = null;
		Optional<AbstractStatement> postcondition = null;

		if ((ModelParserHelper.getIterableChildren(element, SerializationConstants.CONSTRAINT_PRECONDITION_NODE_NAME).size() != 1) ||
				(ModelParserHelper.getIterableChildren(element, SerializationConstants.CONSTRAINT_POSTCONDITION_NODE_NAME).size() != 1)) {

			errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
			return Optional.empty();
		}

		for (Element child : ModelParserHelper.getIterableChildren(element, SerializationConstants.CONSTRAINT_PRECONDITION_NODE_NAME)) {
			if (child.getLocalName().equals(SerializationConstants.CONSTRAINT_PRECONDITION_NODE_NAME)) {
				if (ModelParserHelper.getIterableChildren(child).size() == 1) {
					//there is only one statement per precondition or postcondition that is either
					//a single statement or statement array
					precondition = parseStatement(child.getChildElements().get(0), method, errorList);
				} else {
					errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
					return Optional.empty();
				}
			}
		}

		for (Element child : ModelParserHelper.getIterableChildren(element, SerializationConstants.CONSTRAINT_POSTCONDITION_NODE_NAME)) {
			if (child.getLocalName().equals(SerializationConstants.CONSTRAINT_POSTCONDITION_NODE_NAME)) {
				if (ModelParserHelper.getIterableChildren(child).size() == 1) {
					postcondition = parseStatement(child.getChildElements().get(0), method, errorList);
				} else {
					errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
					return Optional.empty();
				}
			} else {
				errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
				return Optional.empty();
			}
		}

		if (!precondition.isPresent() || !postcondition.isPresent()) {
			errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
			return Optional.empty();
		}

		Constraint constraint =
				new Constraint(
						name,
						constraintType,
						precondition.get(),
						postcondition.get(),
						method.getModelChangeRegistrator());

		ConstraintNode targetConstraint = new ConstraintNode(name, constraint, method.getModelChangeRegistrator());

		targetConstraint.setDescription(ModelParserHelper.parseComments(element, fWhiteCharConverter));

		return Optional.ofNullable(targetConstraint);
	}

	protected ConstraintType getConstraintType(Element element, ListOfStrings errorList) throws ParserException {

		String type = element.getAttributeValue(SerializationConstants.PROPERTY_ATTRIBUTE_TYPE);

		if (type == null) {
			return ConstraintType.EXTENDED_FILTER;
		}

		ConstraintType constraintType = null;

		try {
			constraintType = ConstraintType.parseCode(type);
		} catch (Exception e) {
			errorList.add("Cannot parse constraint type.");
			ParserException.create();
		}

		return constraintType;
	}

	public Optional<AbstractStatement> parseStatement(
			Element element,
			MethodNode method,
			ListOfStrings errorList) {

		try {
			String localName = element.getLocalName();

			switch(localName) {

			case SerializationConstants.CONSTRAINT_CHOICE_STATEMENT_NODE_NAME:
				return Optional.ofNullable(parseChoiceStatement(element, method, errorList));

			case SerializationConstants.CONSTRAINT_PARAMETER_STATEMENT_NODE_NAME:
				return Optional.ofNullable(parseParameterStatement(element, method, errorList));

			case SerializationConstants.CONSTRAINT_VALUE_STATEMENT_NODE_NAME:
				return Optional.ofNullable(parseValueStatement(element, method, errorList));

			case SerializationConstants.CONSTRAINT_LABEL_STATEMENT_NODE_NAME:
				return Optional.ofNullable(parseLabelStatement(element, method, errorList));

			case SerializationConstants.CONSTRAINT_STATEMENT_ARRAY_NODE_NAME:
				return Optional.ofNullable(parseStatementArray(element, method, errorList));

			case SerializationConstants.CONSTRAINT_STATIC_STATEMENT_NODE_NAME:
				return Optional.ofNullable(parseStaticStatement(element, method.getModelChangeRegistrator(), errorList));

			case SerializationConstants.CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME:
				return Optional.ofNullable(parseExpectedValueStatement(element, method, errorList));

			default: return null;
			}
		} catch (Exception e) {
			return Optional.empty();
		}

	}

	public StatementArray parseStatementArray(
			Element element, MethodNode method, ListOfStrings errorList) throws ParserException {

		ModelParserHelper.assertNodeTag(element.getQualifiedName(), CONSTRAINT_STATEMENT_ARRAY_NODE_NAME, errorList);

		StatementArray statementArray = null;
		String operatorValue = 
				ModelParserHelper.getAttributeValue(
						element, SerializationConstants.STATEMENT_OPERATOR_ATTRIBUTE_NAME, 
						fWhiteCharConverter, errorList);

		switch(operatorValue) {

		case SerializationConstants.STATEMENT_OPERATOR_OR_ATTRIBUTE_VALUE:
			statementArray = new StatementArray(StatementArrayOperator.OR, method.getModelChangeRegistrator());
			break;

		case SerializationConstants.STATEMENT_OPERATOR_AND_ATTRIBUTE_VALUE:
			statementArray = new StatementArray(StatementArrayOperator.AND, method.getModelChangeRegistrator());
			break;

		case SerializationConstants.STATEMENT_OPERATOR_ASSIGN_ATTRIBUTE_VALUE:
			statementArray = new StatementArray(StatementArrayOperator.ASSIGN, method.getModelChangeRegistrator());
			break;

		default:
			errorList.add(Messages.WRONG_STATEMENT_ARRAY_OPERATOR(method.getName(), operatorValue));
			return null;
		}

		for (Element child : ModelParserHelper.getIterableChildren(element)) {
			Optional<AbstractStatement> childStatement = parseStatement(child, method, errorList);
			if (childStatement.isPresent()) {
				statementArray.addStatement(childStatement.get());
			}
		}

		return statementArray;
	}

	public StaticStatement parseStaticStatement(
			Element element, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings errorList) throws ParserException {

		ModelParserHelper.assertNodeTag(element.getQualifiedName(), CONSTRAINT_STATIC_STATEMENT_NODE_NAME, errorList);

		String valueString = 
				ModelParserHelper.getAttributeValue(
						element, SerializationConstants.STATIC_VALUE_ATTRIBUTE_NAME, fWhiteCharConverter, errorList);

		switch(valueString) {
		case SerializationConstants.STATIC_STATEMENT_TRUE_VALUE:
			return new StaticStatement(true, modelChangeRegistrator);
		case SerializationConstants.STATIC_STATEMENT_FALSE_VALUE:
			return new StaticStatement(false, modelChangeRegistrator);
		default:
			errorList.add(Messages.WRONG_STATIC_STATEMENT_VALUE(valueString));
			return null;
		}

	}

	public AbstractStatement parseChoiceStatement(
			Element element, MethodNode method, ListOfStrings errorList) throws ParserException {

		ModelParserHelper.assertNodeTag(element.getQualifiedName(), CONSTRAINT_CHOICE_STATEMENT_NODE_NAME, errorList);

		String parameterName = 
				ModelParserHelper.getAttributeValue(
						element, SerializationHelperVersion1.getStatementParameterAttributeName(), 
						fWhiteCharConverter, errorList);

		MethodParameterNode methodParameterNode = (MethodParameterNode)method.findParameter(parameterName);

		if (methodParameterNode == null) {
			errorList.add(EMPTY_PARAMETER_WHILE_PARSING_VALUE_STATEMENT);
			return null;
		}

		String relationName = 
				ModelParserHelper.getAttributeValue(
						element, SerializationConstants.STATEMENT_RELATION_ATTRIBUTE_NAME, 
						fWhiteCharConverter, errorList);

		EMathRelation relation = ModelParserHelper.parseRelationName(relationName, errorList);

		if (!isOkExpectedPropertyOfParameter(methodParameterNode, relation, errorList)) {
			return null;
		}

		String choiceName = 
				ModelParserHelper.getAttributeValue(
						element, SerializationHelperVersion1.getStatementChoiceAttributeName(), 
						fWhiteCharConverter, errorList);

		ChoiceNode choice = methodParameterNode.getChoice(choiceName);
		if (choice == null) {
			errorList.add(Messages.WRONG_PARTITION_NAME(choiceName, parameterName, method.getName()));
			return null;
		}

		if (relation == EMathRelation.ASSIGN) {
			return AssignmentStatement.createAssignmentWithChoiceCondition(methodParameterNode, choice);
		}

		return RelationStatement.createRelationStatementWithChoiceCondition(methodParameterNode, relation, choice);
	}

	public AbstractStatement parseParameterStatement(
			Element element, MethodNode method, ListOfStrings errorList) throws ParserException {

		ModelParserHelper.assertNodeTag(element.getQualifiedName(), SerializationConstants.CONSTRAINT_PARAMETER_STATEMENT_NODE_NAME, errorList);

		String parameterName = 
				ModelParserHelper.getAttributeValue(
						element, SerializationHelperVersion1.getStatementParameterAttributeName(), 
						fWhiteCharConverter, errorList);

		MethodParameterNode leftParameterNode = (MethodParameterNode)method.findParameter(parameterName);

		if (leftParameterNode == null) {
			errorList.add(EMPTY_PARAMETER_WHILE_PARSING_VALUE_STATEMENT);
			return null;
		}

		String relationName = 
				ModelParserHelper.getAttributeValue(
						element, SerializationConstants.STATEMENT_RELATION_ATTRIBUTE_NAME, 
						fWhiteCharConverter, errorList);

		EMathRelation relation = ModelParserHelper.parseRelationName(relationName, errorList);

		if (!isOkExpectedPropertyOfParameter(leftParameterNode, relation, errorList)) {
			return null;
		}

		String rightParameterName = 
				ModelParserHelper.getAttributeValue(
						element, SerializationConstants.STATEMENT_RIGHT_PARAMETER_ATTRIBUTE_NAME, 
						fWhiteCharConverter, errorList);

		MethodParameterNode rightParameterNode = (MethodParameterNode)method.findParameter(rightParameterName);
		if (rightParameterNode == null) {
			errorList.add(Messages.WRONG_PARAMETER_NAME(rightParameterName, method.getName()));
			return null;
		}

		if (relation == EMathRelation.ASSIGN) {
			return AssignmentStatement.createAssignmentWithParameterCondition(leftParameterNode, rightParameterNode);
		}

		return RelationStatement.createRelationStatementWithParameterCondition(leftParameterNode, relation, rightParameterNode);
	}
	//
	public AbstractStatement parseValueStatement(
			Element element, MethodNode method, ListOfStrings errorList) throws ParserException {

		ModelParserHelper.assertNodeTag(element.getQualifiedName(), SerializationConstants.CONSTRAINT_VALUE_STATEMENT_NODE_NAME, errorList);

		String parameterName = 
				ModelParserHelper.getAttributeValue(
						element, SerializationHelperVersion1.getStatementParameterAttributeName(), 
						fWhiteCharConverter, errorList);

		MethodParameterNode leftParameterNode = (MethodParameterNode)method.findParameter(parameterName);

		if (leftParameterNode == null) {
			errorList.add(EMPTY_PARAMETER_WHILE_PARSING_VALUE_STATEMENT);
			return null;
		}

		String relationName = 
				ModelParserHelper.getAttributeValue(
						element, SerializationConstants.STATEMENT_RELATION_ATTRIBUTE_NAME, 
						fWhiteCharConverter, errorList);

		EMathRelation relation = ModelParserHelper.parseRelationName(relationName, errorList);

		if (!isOkExpectedPropertyOfParameter(leftParameterNode, relation, errorList)) {
			return null;
		}

		String value = 
				ModelParserHelper.getAttributeValue(
						element, SerializationConstants.STATEMENT_RIGHT_VALUE_ATTRIBUTE_NAME, 
						fWhiteCharConverter, errorList);

		if (relation == EMathRelation.ASSIGN) {
			return AssignmentStatement.createAssignmentWithValueCondition(leftParameterNode, value);
		}

		return RelationStatement.createRelationStatementWithValueCondition(leftParameterNode, relation, value);
	}

	private boolean isOkExpectedPropertyOfParameter(
			MethodParameterNode leftParameterNode,
			EMathRelation relation,
			ListOfStrings errorList) {

		if (relation == EMathRelation.ASSIGN) {

			if (!leftParameterNode.isExpected()) {
				errorList.add("Left parameter of value statement in assignment should be expected.");
				return false;
			}

			return true;
		} 

		if (leftParameterNode.isExpected()) {
			errorList.add("Left parameter of value statement should not be expected.");
			return false;
		}

		return true;
	}

	public RelationStatement parseLabelStatement(
			Element element, MethodNode method, ListOfStrings errorList) throws ParserException {

		ModelParserHelper.assertNodeTag(element.getQualifiedName(), CONSTRAINT_LABEL_STATEMENT_NODE_NAME, errorList);

		String parameterName = 
				ModelParserHelper.getAttributeValue(
						element, SerializationHelperVersion1.getStatementParameterAttributeName(), 
						fWhiteCharConverter, errorList);

		String label = 
				ModelParserHelper.getAttributeValue(
						element, SerializationConstants.STATEMENT_LABEL_ATTRIBUTE_NAME, 
						fWhiteCharConverter, errorList);

		String relationName = 
				ModelParserHelper.getAttributeValue(
						element, SerializationConstants.STATEMENT_RELATION_ATTRIBUTE_NAME, 
						fWhiteCharConverter, errorList);

		MethodParameterNode parameter = method.findMethodParameter(parameterName);

		if (parameter == null || parameter.isExpected()) {
			errorList.add(Messages.WRONG_PARAMETER_NAME(parameterName, method.getName()));
			return null;
		}
		EMathRelation relation = ModelParserHelper.parseRelationName(relationName, errorList);

		return RelationStatement.createRelationStatementWithLabelCondition(parameter, relation, label);
	}

	public ExpectedValueStatement parseExpectedValueStatement(
			Element element, MethodNode method, ListOfStrings errorList) throws ParserException {

		ModelParserHelper.assertNodeTag(element.getQualifiedName(), CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME, errorList);

		String parameterName = 
				ModelParserHelper.getAttributeValue(
						element, SerializationHelperVersion1.getStatementParameterAttributeName(), fWhiteCharConverter, errorList);

		String valueString = 
				ModelParserHelper.getAttributeValue(
						element, SerializationConstants.STATEMENT_EXPECTED_VALUE_ATTRIBUTE_NAME, 
						fWhiteCharConverter, errorList);

		MethodParameterNode parameter = method.findMethodParameter(parameterName);

		if (parameter == null || !parameter.isExpected()) {
			errorList.add(Messages.WRONG_PARAMETER_NAME(parameterName, method.getName()));
			return null;
		}

		ChoiceNode condition = new ChoiceNode("expected", valueString, parameter.getModelChangeRegistrator());
		condition.setParent(parameter);

		return new ExpectedValueStatement(parameter, condition, new JavaPrimitiveTypePredicate());
	}

}
