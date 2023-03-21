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

import com.ecfeed.core.model.*;
import com.ecfeed.core.type.adapter.JavaPrimitiveTypePredicate;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForConstraint implements IModelParserForConstraint {

	private static final String EMPTY_PARAMETER_WHILE_PARSING_VALUE_STATEMENT = "Empty parameter while parsing value statement.";

	public ModelParserForConstraint() {
	}

	public Optional<ConstraintNode> parseConstraint(Element element, IParametersAndConstraintsParentNode parent, ListOfStrings errorList) throws ParserException {

		String name;

		try {
			ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), CONSTRAINT_NODE_NAME, errorList);
			name = ModelParserHelper.getElementName(element, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}

		ConstraintType constraintType = getConstraintType(element, errorList);

		Optional<AbstractStatement> precondition = Optional.empty();
		Optional<AbstractStatement> postcondition = Optional.empty();

		if ((ModelParserHelper.getIterableChildren(element, SerializationConstants.CONSTRAINT_PRECONDITION_NODE_NAME).size() != 1) ||
				(ModelParserHelper.getIterableChildren(element, SerializationConstants.CONSTRAINT_POSTCONDITION_NODE_NAME).size() != 1)) {

			errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(parent.getName(), name));
			return Optional.empty();
		}

		for (Element child : ModelParserHelper.getIterableChildren(element, SerializationConstants.CONSTRAINT_PRECONDITION_NODE_NAME)) {
			if (child.getLocalName().equals(SerializationConstants.CONSTRAINT_PRECONDITION_NODE_NAME)) {
				if (ModelParserHelper.getIterableChildren(child).size() == 1) {
					//there is only one statement per precondition or postcondition that is either
					//a single statement or statement array
					precondition = parseStatement(child.getChildElements().get(0), parent, errorList);
				} else {
					errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(parent.getName(), name));
					return Optional.empty();
				}
			}
		}

		for (Element child : ModelParserHelper.getIterableChildren(element, SerializationConstants.CONSTRAINT_POSTCONDITION_NODE_NAME)) {
			if (child.getLocalName().equals(SerializationConstants.CONSTRAINT_POSTCONDITION_NODE_NAME)) {
				if (ModelParserHelper.getIterableChildren(child).size() == 1) {
					postcondition = parseStatement(child.getChildElements().get(0), parent, errorList);
				} else {
					errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(parent.getName(), name));
					return Optional.empty();
				}
			} else {
				errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(parent.getName(), name));
				return Optional.empty();
			}
		}

		if (!precondition.isPresent() || !postcondition.isPresent()) {
			errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(parent.getName(), name));
			return Optional.empty();
		}

		Constraint constraint =
				new Constraint(
						name,
						constraintType,
						precondition.get(),
						postcondition.get(),
						parent.getModelChangeRegistrator());

		ConstraintNode targetConstraint = new ConstraintNode(name, constraint, parent.getModelChangeRegistrator());

		targetConstraint.setDescription(ModelParserHelper.parseComments(element));

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
			IParametersAndConstraintsParentNode parent,
			ListOfStrings errorList) {

		try {
			String localName = element.getLocalName();

			switch(localName) {

			case SerializationConstants.CONSTRAINT_CHOICE_STATEMENT_NODE_NAME:
				return Optional.ofNullable(parseChoiceStatement(element, parent, errorList));

			case SerializationConstants.CONSTRAINT_PARAMETER_STATEMENT_NODE_NAME:
				return Optional.ofNullable(parseParameterStatement(element, parent, errorList));

			case SerializationConstants.CONSTRAINT_VALUE_STATEMENT_NODE_NAME:
				return Optional.ofNullable(parseValueStatement(element, parent, errorList));

			case SerializationConstants.CONSTRAINT_LABEL_STATEMENT_NODE_NAME:
				return Optional.ofNullable(parseLabelStatement(element, parent, errorList));

			case SerializationConstants.CONSTRAINT_STATEMENT_ARRAY_NODE_NAME:
				return Optional.ofNullable(parseStatementArray(element, parent, errorList));

			case SerializationConstants.CONSTRAINT_STATIC_STATEMENT_NODE_NAME:
				return Optional.ofNullable(parseStaticStatement(element, parent.getModelChangeRegistrator(), errorList));

			case SerializationConstants.CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME:
				return Optional.ofNullable(parseExpectedValueStatement(element, parent, errorList));

			default: return null;
			}
		} catch (Exception e) {
			return Optional.empty();
		}

	}

	public StatementArray parseStatementArray(
			Element element,
			IParametersAndConstraintsParentNode parent,
			ListOfStrings errorList) throws ParserException {

		ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), CONSTRAINT_STATEMENT_ARRAY_NODE_NAME, errorList);

		StatementArray statementArray = null;
		String operatorValue = ModelParserHelper.getAttributeValue(
				element, SerializationConstants.STATEMENT_OPERATOR_ATTRIBUTE_NAME, errorList);

		switch(operatorValue) {

		case SerializationConstants.STATEMENT_OPERATOR_OR_ATTRIBUTE_VALUE:
			statementArray = new StatementArray(StatementArrayOperator.OR, parent.getModelChangeRegistrator());
			break;

		case SerializationConstants.STATEMENT_OPERATOR_AND_ATTRIBUTE_VALUE:
			statementArray = new StatementArray(StatementArrayOperator.AND, parent.getModelChangeRegistrator());
			break;

		case SerializationConstants.STATEMENT_OPERATOR_ASSIGN_ATTRIBUTE_VALUE:
			statementArray = new StatementArray(StatementArrayOperator.ASSIGN, parent.getModelChangeRegistrator());
			break;

		default:
			errorList.add(Messages.WRONG_STATEMENT_ARRAY_OPERATOR(parent.getName(), operatorValue));
			return null;
		}

		for (Element child : ModelParserHelper.getIterableChildren(element)) {
			Optional<AbstractStatement> childStatement = parseStatement(child, parent, errorList);
			if (childStatement.isPresent()) {
				statementArray.addStatement(childStatement.get());
			}
		}

		return statementArray;
	}

	public StaticStatement parseStaticStatement(
			Element element,
			IModelChangeRegistrator modelChangeRegistrator,
			ListOfStrings errorList) throws ParserException {

		ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), CONSTRAINT_STATIC_STATEMENT_NODE_NAME, errorList);

		String valueString = ModelParserHelper.getAttributeValue(
				element, SerializationConstants.STATIC_VALUE_ATTRIBUTE_NAME, errorList);

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
			Element element, IParametersAndConstraintsParentNode parent, ListOfStrings errorList) throws ParserException {

		ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), CONSTRAINT_CHOICE_STATEMENT_NODE_NAME, errorList);

		CompositeParameterNode parameterContext = getParameterContext(element, parent, true);
		BasicParameterNode parameterNode = getParameter(element, parent, parameterContext, true, errorList);

		if (parameterNode == null) {
			return null;
		}

		String relationName = ModelParserHelper.getAttributeValue(
				element, SerializationConstants.STATEMENT_RELATION_ATTRIBUTE_NAME, errorList);

		EMathRelation relation = ModelParserHelper.parseRelationName(relationName, errorList);

		if (!isOkExpectedPropertyOfParameter(parameterNode, relation, errorList)) {
			return null;
		}

		String choiceName = ModelParserHelper.getAttributeValue(
				element, SerializationHelperVersion1.getStatementChoiceAttributeName(), errorList);

		ChoiceNode choice = parameterNode.getChoice(choiceName);
		if (choice == null) {
			errorList.add(Messages.WRONG_PARTITION_NAME(choiceName, parameterNode.getName(), parent.getName()));
			return null;
		}

		if (relation == EMathRelation.ASSIGN) {
			return AssignmentStatement.createAssignmentWithChoiceCondition(parameterNode, choice);
		}

		return RelationStatement.createRelationStatementWithChoiceCondition
				(parameterNode, parameterContext, relation, choice);
	}

	public AbstractStatement parseParameterStatement(
			Element element, IParametersAndConstraintsParentNode parent, ListOfStrings errorList) throws ParserException {

		ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), SerializationConstants.CONSTRAINT_PARAMETER_STATEMENT_NODE_NAME, errorList);

		CompositeParameterNode parameterContext = getParameterContext(element, parent, true);
		BasicParameterNode parameterNode = getParameter(element, parent, parameterContext, true, errorList);

		if (parameterNode == null) {
			return null;
		}
		
		String relationName = ModelParserHelper.getAttributeValue(
				element, SerializationConstants.STATEMENT_RELATION_ATTRIBUTE_NAME, errorList);

		EMathRelation relation = ModelParserHelper.parseRelationName(relationName, errorList);

		if (!isOkExpectedPropertyOfParameter(parameterNode, relation, errorList)) {
			return null;
		}

		CompositeParameterNode rightParameterContext = getParameterContext(element, parent, false);
		BasicParameterNode rightParameterNode = getParameter(element, parent, rightParameterContext, false, errorList);

		if (rightParameterNode == null) {
			return null;
		}

		if (relation == EMathRelation.ASSIGN) {
			return AssignmentStatement.createAssignmentWithParameterCondition(
					parameterNode, rightParameterNode, rightParameterContext);
		}

		return RelationStatement.createRelationStatementWithParameterCondition(
				parameterNode, parameterContext, relation, rightParameterNode, rightParameterContext);
	}

	public AbstractStatement parseValueStatement(
			Element element, IParametersAndConstraintsParentNode parent, ListOfStrings errorList) throws ParserException {

		ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), SerializationConstants.CONSTRAINT_VALUE_STATEMENT_NODE_NAME, errorList);

		CompositeParameterNode parameterContext = getParameterContext(element, parent, true);
		BasicParameterNode parameterNode = getParameter(element, parent, parameterContext, true, errorList);

		if (parameterNode == null) {
			return null;
		}

		String relationName = ModelParserHelper.getAttributeValue(
				element, SerializationConstants.STATEMENT_RELATION_ATTRIBUTE_NAME, errorList);

		EMathRelation relation = ModelParserHelper.parseRelationName(relationName, errorList);

		if (!isOkExpectedPropertyOfParameter(parameterNode, relation, errorList)) {
			return null;
		}

		String value = ModelParserHelper.getAttributeValue(
				element, SerializationConstants.STATEMENT_RIGHT_VALUE_ATTRIBUTE_NAME, errorList);

		if (relation == EMathRelation.ASSIGN) {
			return AssignmentStatement.createAssignmentWithValueCondition(parameterNode, value);
		}

		return RelationStatement.createRelationStatementWithValueCondition(
				parameterNode, parameterContext, relation, value);
	}

	private boolean isOkExpectedPropertyOfParameter(
			BasicParameterNode leftParameterNode,
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
			Element element, IParametersAndConstraintsParentNode parent, ListOfStrings errorList) throws ParserException {

		ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), CONSTRAINT_LABEL_STATEMENT_NODE_NAME, errorList);

		CompositeParameterNode parameterContext = getParameterContext(element, parent, true);
		BasicParameterNode parameterNode = getParameter(element, parent, parameterContext, true, errorList);

		if (parameterNode == null) {
			return null;
		}

		String parameterName = ModelParserHelper.getAttributeValue(
				element, SerializationHelperVersion1.getStatementParameterAttributeName(), errorList);

		String label = ModelParserHelper.getAttributeValue(
				element, SerializationConstants.STATEMENT_LABEL_ATTRIBUTE_NAME, errorList);

		String relationName = ModelParserHelper.getAttributeValue(
				element, SerializationConstants.STATEMENT_RELATION_ATTRIBUTE_NAME, errorList);

		if (parameterNode.isExpected()) {
			errorList.add(Messages.WRONG_PARAMETER_NAME(parameterName, parent.getName()));
			return null;
		}
		EMathRelation relation = ModelParserHelper.parseRelationName(relationName, errorList);

		return RelationStatement.createRelationStatementWithLabelCondition(
				parameterNode, parameterContext, relation, label);
	}

	public ExpectedValueStatement parseExpectedValueStatement(
			Element element, IParametersAndConstraintsParentNode parent, ListOfStrings errorList) throws ParserException {

		ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME, errorList);

		CompositeParameterNode parameterContext = getParameterContext(element, parent, true);
		BasicParameterNode parameterNode = getParameter(element, parent, parameterContext, true, errorList);

		if (parameterNode == null) {
			return null;
		}

		String parameterName = ModelParserHelper.getAttributeValue(
				element, SerializationHelperVersion1.getStatementParameterAttributeName(), errorList);

		String valueString = ModelParserHelper.getAttributeValue(
				element, SerializationConstants.STATEMENT_EXPECTED_VALUE_ATTRIBUTE_NAME, errorList);

		if (!parameterNode.isExpected()) {
			errorList.add(Messages.WRONG_PARAMETER_NAME(parameterName, parent.getName()));
			return null;
		}

		ChoiceNode condition = new ChoiceNode("expected", valueString, parameterNode.getModelChangeRegistrator());
		condition.setParent(parameterNode);

		return new ExpectedValueStatement(
				parameterNode, parameterContext, condition, new JavaPrimitiveTypePredicate());
	}

//-----------------------------------------------------------------------------------------------

	private CompositeParameterNode getParameterContext(
			Element element,
			IParametersAndConstraintsParentNode parent,
			boolean primary) throws ParserException {

		String serialization;
		if (primary) {
			serialization = SerializationConstants.STATEMENT_LINKING_PARAMETER_CONTEXT;
		} else {
			serialization = SerializationConstants.STATEMENT_LINKING_RIGHT_PARAMETER_CONTEXT;
		}

		String parameterContextName = ModelParserHelper.getAttributeValue(element, serialization);

		return CompositeParameterNodeHelper.getParameterFromPath(parent, parameterContextName);
	}

	private BasicParameterNode getParameter(
			Element element,
			IParametersAndConstraintsParentNode parent,
			CompositeParameterNode parameterContext,
			boolean primary,
			ListOfStrings errorList) throws ParserException {

		String serialization;
		if (primary) {
			serialization = SerializationHelperVersion1.getStatementParameterAttributeName();
		} else {
			serialization = SerializationConstants.STATEMENT_RIGHT_PARAMETER_ATTRIBUTE_NAME;
		}

		String parameterName =	ModelParserHelper.getAttributeValue(element, serialization, errorList);

		BasicParameterNode parameter;
		if (parameterContext != null) {
			parameter = BasicParameterNodeHelper.getParameterFromPath(parameterContext, parameterName);
		} else {
			parameter = BasicParameterNodeHelper.getParameterFromPath(parent, parameterName);
		}

		if (parameter == null) {
			if (primary) {
				errorList.add(EMPTY_PARAMETER_WHILE_PARSING_VALUE_STATEMENT);
			} else {
				errorList.add(Messages.WRONG_PARAMETER_NAME(parameterName, parent.getName()));
			}
		}

		return parameter;
	}
}
