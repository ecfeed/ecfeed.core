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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractParameterNodeHelper;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.AssignmentStatement;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.CompositeParameterNodeHelper;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.IParametersAndConstraintsParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StatementArrayOperator;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public class ModelParserForConstraint {

	public static ConstraintNode parseConstraint(
			Element element, 
			IParametersAndConstraintsParentNode parent, 
			ListOfStrings errorList) {

		String name;

		try {
			ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), CONSTRAINT_NODE_NAME, errorList);
			name = ModelParserHelper.getElementName(element, errorList);
		} catch (Exception e) {
			errorList.add(e.getMessage());
			return null;
		}

		ConstraintType constraintType = getConstraintType(element, errorList);

		Optional<AbstractStatement> precondition = Optional.empty();
		Optional<AbstractStatement> postcondition = Optional.empty();

		if ((ModelParserHelper.getIterableChildren(element, SerializationConstants.CONSTRAINT_PRECONDITION_NODE_NAME).size() != 1) ||
				(ModelParserHelper.getIterableChildren(element, SerializationConstants.CONSTRAINT_POSTCONDITION_NODE_NAME).size() != 1)) {

			errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(parent.getName(), name));
			return null;
		}

		for (Element child : ModelParserHelper.getIterableChildren(element, SerializationConstants.CONSTRAINT_PRECONDITION_NODE_NAME)) {
			if (child.getLocalName().equals(SerializationConstants.CONSTRAINT_PRECONDITION_NODE_NAME)) {
				if (ModelParserHelper.getIterableChildren(child).size() == 1) {
					//there is only one statement per precondition or postcondition that is either
					//a single statement or statement array
					precondition = parseStatement(child.getChildElements().get(0), parent, errorList);
				} else {
					errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(parent.getName(), name));
					return null;
				}
			}
		}

		for (Element child : ModelParserHelper.getIterableChildren(element, SerializationConstants.CONSTRAINT_POSTCONDITION_NODE_NAME)) {
			if (child.getLocalName().equals(SerializationConstants.CONSTRAINT_POSTCONDITION_NODE_NAME)) {
				if (ModelParserHelper.getIterableChildren(child).size() == 1) {
					postcondition = parseStatement(child.getChildElements().get(0), parent, errorList);
				} else {
					errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(parent.getName(), name));
					return null;
				}
			} else {
				errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(parent.getName(), name));
				return null;
			}
		}

		if (!precondition.isPresent() || !postcondition.isPresent()) {
			errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(parent.getName(), name));
			return null;
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

		return targetConstraint;
	}

	private static ConstraintType getConstraintType(Element element, ListOfStrings errorList) {

		String type = element.getAttributeValue(SerializationConstants.PROPERTY_ATTRIBUTE_TYPE);

		if (type == null) {
			return ConstraintType.EXTENDED_FILTER;
		}

		ConstraintType constraintType = null;

		try {
			constraintType = ConstraintType.parseCode(type);
		} catch (Exception e) {
			errorList.add(e.getMessage());
		}

		return constraintType;
	}

	public static Optional<AbstractStatement> parseStatement(
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

	public static StatementArray parseStatementArray(
			Element element,
			IParametersAndConstraintsParentNode parent,
			ListOfStrings errorList) {

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

	public static StaticStatement parseStaticStatement(
			Element element,
			IModelChangeRegistrator modelChangeRegistrator,
			ListOfStrings errorList) {

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

	public static AbstractStatement parseChoiceStatement(
			Element element, 
			IParametersAndConstraintsParentNode parent, 
			ListOfStrings errorList) {

		ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), CONSTRAINT_CHOICE_STATEMENT_NODE_NAME, errorList);

		CompositeParameterNode parameterContext = 
				getParameterContext(element, parent, SerializationConstants.STATEMENT_LINKING_PARAMETER_CONTEXT);

		BasicParameterNode parameterNode = 
				getParameter(
						element, 
						SerializationHelperVersion1.getStatementParameterAttributeName(), 
						parent, 
						parameterContext, 
						errorList);

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

	public static AbstractStatement parseParameterStatement(
			Element element, IParametersAndConstraintsParentNode parent, ListOfStrings errorList) {

		ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), SerializationConstants.CONSTRAINT_PARAMETER_STATEMENT_NODE_NAME, errorList);

		CompositeParameterNode parameterContext = 
				getParameterContext(element, parent, SerializationConstants.STATEMENT_LINKING_PARAMETER_CONTEXT);

		BasicParameterNode parameterNode = 
				getParameter(
						element, 
						SerializationHelperVersion1.getStatementParameterAttributeName(), 
						parent, 
						parameterContext, 
						errorList);

		if (parameterNode == null) {
			return null;
		}

		String relationName = ModelParserHelper.getAttributeValue(
				element, SerializationConstants.STATEMENT_RELATION_ATTRIBUTE_NAME, errorList);

		EMathRelation relation = ModelParserHelper.parseRelationName(relationName, errorList);

		if (!isOkExpectedPropertyOfParameter(parameterNode, relation, errorList)) {
			return null;
		}

		CompositeParameterNode rightParameterContext = 
				getParameterContext(element, parent, SerializationConstants.STATEMENT_LINKING_RIGHT_PARAMETER_CONTEXT);

		BasicParameterNode rightParameterNode = 
				getParameter(
						element, 
						SerializationConstants.STATEMENT_RIGHT_PARAMETER_ATTRIBUTE_NAME, 
						parent, 
						rightParameterContext, 
						errorList);

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

	public static AbstractStatement parseValueStatement(
			Element element, IParametersAndConstraintsParentNode parent, ListOfStrings errorList) {

		ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), SerializationConstants.CONSTRAINT_VALUE_STATEMENT_NODE_NAME, errorList);

		CompositeParameterNode parameterContext = 
				getParameterContext(element, parent, SerializationConstants.STATEMENT_LINKING_PARAMETER_CONTEXT);

		BasicParameterNode parameterNode = 
				getParameter(
						element, 
						SerializationHelperVersion1.getStatementParameterAttributeName(), 
						parent, 
						parameterContext, 
						errorList);

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

	private static boolean isOkExpectedPropertyOfParameter(
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

	public static RelationStatement parseLabelStatement(
			Element element, IParametersAndConstraintsParentNode parent, ListOfStrings errorList) {

		ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), CONSTRAINT_LABEL_STATEMENT_NODE_NAME, errorList);

		CompositeParameterNode parameterContext = 
				getParameterContext(element, parent, SerializationConstants.STATEMENT_LINKING_PARAMETER_CONTEXT);

		BasicParameterNode parameterNode = 
				getParameter(
						element, 
						SerializationHelperVersion1.getStatementParameterAttributeName(), 
						parent, 
						parameterContext, 
						errorList);

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

	public static AssignmentStatement parseExpectedValueStatement(
			Element element, IParametersAndConstraintsParentNode parent, ListOfStrings errorList) {

		ModelParserHelper.assertNameEqualsExpectedName(element.getQualifiedName(), CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME, errorList);

		CompositeParameterNode parameterContext = 
				getParameterContext(element, parent, SerializationConstants.STATEMENT_LINKING_PARAMETER_CONTEXT);

		BasicParameterNode parameterNode = 
				getParameter(
						element, 
						SerializationHelperVersion1.getStatementParameterAttributeName(), 
						parent, 
						parameterContext, 
						errorList);

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

		ChoiceNode choiceForCondition = new ChoiceNode("expected", valueString, parameterNode.getModelChangeRegistrator());
		choiceForCondition.setParent(parameterNode);

//		return new ExpectedValueStatement(
//				parameterNode, parameterContext, condition, new JavaPrimitiveTypePredicate());
		
		return AssignmentStatement.createAssignmentWithChoiceCondition(parameterNode, choiceForCondition);
	}

	private static CompositeParameterNode getParameterContext(
			Element element,
			IParametersAndConstraintsParentNode parent,
			String elementName) {

		String pathToParameter = ModelParserHelper.getAttributeValue(element, elementName);

		if (pathToParameter == null) {
			return null;
		}

		AbstractParameterNode context = findParameterForPathWhichStartsFromTopAllowedNode(pathToParameter, parent);

		return (CompositeParameterNode) context;
	}

	//	private IParametersParentNode calculateParentOfParameter(String path, IParametersParentNode initialParent) {
	//
	//		IParametersParentNode calculatedParentNode = initialParent;
	//
	//		if (path.startsWith(SignatureHelper.SIGNATURE_ROOT_MARKER)) {
	//
	//			IAbstractNode topNode = AbstractNodeHelper.findTopNode(initialParent);
	//
	//			if (!(topNode instanceof RootNode)) {
	//				ExceptionHelper.reportRuntimeException("Cannot find root node.");
	//			}
	//
	//			calculatedParentNode = (IParametersParentNode) topNode;
	//		}
	//		return calculatedParentNode;
	//	}

	private static BasicParameterNode getParameter(
			Element element,
			String attributeName,
			IParametersAndConstraintsParentNode parent,
			CompositeParameterNode parameterContext,
			ListOfStrings errorList) {

		String pathToParameter = ModelParserHelper.getAttributeValue(element, attributeName, errorList);

		AbstractParameterNode parameter = findParameterForPathWhichStartsFromTopAllowedNode(pathToParameter, parent);

		if (parameter == null) {
			errorList.add("Cannot find parameter: " + pathToParameter + " for parsed attribute: " + attributeName + ".");
		}

		if (!(parameter instanceof BasicParameterNode)) {
			errorList.add("Parameter type is invalid. Expected basic parameter.");
		}

		return (BasicParameterNode) parameter;
	}

	private static AbstractParameterNode findParameterForPathWhichStartsFromTopAllowedNode(
			String pathToParameterRelativeToTopParametersParent,
			IParametersAndConstraintsParentNode parent) {

		MethodNode methodNode = MethodNodeHelper.findMethodNode(parent);

		if (methodNode != null) {
			return AbstractParameterNodeHelper.findParameter(pathToParameterRelativeToTopParametersParent, methodNode);
		}

		CompositeParameterNode topComposite = CompositeParameterNodeHelper.findTopComposite(parent);

		if (topComposite != null) {
			return AbstractParameterNodeHelper.findParameter(pathToParameterRelativeToTopParametersParent, topComposite);
		}

		return AbstractParameterNodeHelper.findParameter(pathToParameterRelativeToTopParametersParent, parent);
	}
}
