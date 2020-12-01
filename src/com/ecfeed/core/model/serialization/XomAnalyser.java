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

import static com.ecfeed.core.model.ModelConstants.EXPECTED_VALUE_CHOICE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.ANDROID_RUNNER_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CLASS_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_CHOICE_STATEMENT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_LABEL_STATEMENT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_STATEMENT_ARRAY_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_STATIC_STATEMENT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.EXPECTED_PARAMETER_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.METHOD_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.NODE_IS_RADOMIZED_ATTRIBUTE;
import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_IS_LINKED_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_LINK_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.ROOT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.RUN_ON_ANDROID_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.TEST_CASE_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.TEST_PARAMETER_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.TEST_SUITE_NAME_ATTRIBUTE;
import static com.ecfeed.core.model.serialization.SerializationConstants.TYPE_NAME_ATTRIBUTE;
import static com.ecfeed.core.model.serialization.SerializationConstants.VALUE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.*;
import com.ecfeed.core.type.adapter.JavaPrimitiveTypePredicate;
import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.core.utils.BooleanHolder;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.StringHolder;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

public abstract class XomAnalyser {

	private WhiteCharConverter fWhiteCharConverter = new WhiteCharConverter();

	protected abstract int getModelVersion();
	protected abstract String getChoiceNodeName();
	protected abstract String getChoiceAttributeName();
	protected abstract String getStatementChoiceAttributeName();
	protected abstract String getParameterNodeName();
	protected abstract String getStatementParameterAttributeName();
	protected abstract ConstraintType getConstraintType(Element element, List<String> errorList) throws ParserException;

	public XomAnalyser() {
	}
	
	public RootNode parseRoot(
			Element element, IModelChangeRegistrator modelChangeRegistrator, List<String> outErrorList) throws ParserException {
		
		assertNodeTag(element.getQualifiedName(), ROOT_NODE_NAME, outErrorList);
		String name = getElementName(element, outErrorList);

		RootNode targetRootNode = new RootNode(name, modelChangeRegistrator, getModelVersion());

		targetRootNode.setDescription(parseComments(element));

		//parameters must be parsed before classes
		for (Element child : getIterableChildren(element, getParameterNodeName())) {
			Optional<GlobalParameterNode> node = parseGlobalParameter(child, targetRootNode.getModelChangeRegistrator(), outErrorList);
			if (node.isPresent()) {
				targetRootNode.addParameter(node.get());
			}
		}
		
		for (Element child : getIterableChildren(element, SerializationConstants.CLASS_NODE_NAME)) {
			Optional<ClassNode> node = parseClass(child, targetRootNode, outErrorList);
			if (node.isPresent()) {
				targetRootNode.addClass(node.get());
			}
		}

		return targetRootNode;
	}

	public Optional<ClassNode> parseClass(
			Element classElement, RootNode parent, List<String> errorList) {

		String name;
		
		try {
			assertNodeTag(classElement.getQualifiedName(), CLASS_NODE_NAME, errorList);
			name = getElementName(classElement, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}

		BooleanHolder runOnAndroidHolder = new BooleanHolder(false);
		StringHolder androidBaseRunnerHolder = new StringHolder(); 
		parseAndroidValues(classElement, runOnAndroidHolder, androidBaseRunnerHolder);

		ClassNode targetClassNode = 
				new ClassNode(
						name, 
						parent.getModelChangeRegistrator(), 
						runOnAndroidHolder.get(), 
						androidBaseRunnerHolder.get());

		targetClassNode.setDescription(parseComments(classElement));
		//we need to do it here, so the backward search for global parameters will work
		targetClassNode.setParent(parent);

		//parameters must be parsed before classes
		for (Element child : getIterableChildren(classElement, getParameterNodeName())) {
			Optional<GlobalParameterNode> node = parseGlobalParameter(child, targetClassNode.getModelChangeRegistrator(), errorList);
			if (node.isPresent()) {
				targetClassNode.addParameter(node.get());
			}
		}

		for (Element child : getIterableChildren(classElement, SerializationConstants.METHOD_NODE_NAME)) {
			Optional<MethodNode> node = parseMethod(child, targetClassNode, errorList);
			if (node.isPresent()) {
				targetClassNode.addMethod(node.get());
			}
		}

		return Optional.ofNullable(targetClassNode);
	}

	private void parseAndroidValues(
			Element classElement, 
			BooleanHolder runOnAndroidHolder, 
			StringHolder androidBaseRunnerHolder) {

		if (ModelVersionDistributor.isAndroidAttributeInTheClass(getModelVersion())) {
			parseAndroidAttributes(classElement, runOnAndroidHolder, androidBaseRunnerHolder);
		} else {
			parseAndroidProperties(classElement, runOnAndroidHolder, androidBaseRunnerHolder);
		}
	}

	private static void parseAndroidAttributes(
			Element classElement, BooleanHolder runOnAndroidHolder, StringHolder androidBaseRunnerHolder) {

		String runOnAndroidStr = classElement.getAttributeValue(RUN_ON_ANDROID_ATTRIBUTE_NAME);
		runOnAndroidHolder.set(BooleanHelper.parseBoolean(runOnAndroidStr));

		String androidBaseRunnerStr = classElement.getAttributeValue(ANDROID_RUNNER_ATTRIBUTE_NAME);

		if (StringHelper.isNullOrEmpty(androidBaseRunnerStr)) {
			return;
		}

		androidBaseRunnerHolder.set(androidBaseRunnerStr);
	}

	private static void parseAndroidProperties(
			Element classElement, 
			BooleanHolder runOnAndroidHolder, 
			StringHolder androidBaseRunnerHolder) {

		String runOnAndroidStr = getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_RUN_ON_ANDROID, classElement);
		runOnAndroidHolder.set(BooleanHelper.parseBoolean(runOnAndroidStr));

		String androidBaseRunnerStr = getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_ANDROID_RUNNER, classElement);
		androidBaseRunnerHolder.set(androidBaseRunnerStr);		
	}

	public Optional<MethodNode> parseMethod(
			Element methodElement, ClassNode classNode, List<String> errorList) {
		
		String name;
		
		try {
			assertNodeTag(methodElement.getQualifiedName(), METHOD_NODE_NAME, errorList);
			name = getElementName(methodElement, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}
		
		MethodNode targetMethodNode = new MethodNode(name, classNode.getModelChangeRegistrator());
		targetMethodNode.setParent(classNode);

		parseMethodProperties(methodElement, targetMethodNode);

		for (Element child : getIterableChildren(methodElement, getParameterNodeName())) {
			Optional<MethodParameterNode> node = parseMethodParameter(child, targetMethodNode, errorList);
			if (node.isPresent()) {
				targetMethodNode.addParameter(node.get());
			}
		}

		for (Element child : getIterableChildren(methodElement, SerializationConstants.TEST_CASE_NODE_NAME)) {
			Optional<TestCaseNode> node = parseTestCase(child, targetMethodNode, errorList);
			if (node.isPresent()) {
				targetMethodNode.addTestCase(node.get());
			}
		}

		for (Element child : getIterableChildren(methodElement, SerializationConstants.CONSTRAINT_NODE_NAME)) {
			Optional<ConstraintNode> node = parseConstraint(child, targetMethodNode, errorList);
			if (node.isPresent()) {
				targetMethodNode.addConstraint(node.get());
			}
		}

		targetMethodNode.setDescription(parseComments(methodElement));

		return Optional.ofNullable(targetMethodNode);
	}

	private void parseMethodProperties(Element methodElement, MethodNode targetMethodNode) {
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_METHOD_RUNNER, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_MAP_BROWSER_TO_PARAM, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_WEB_BROWSER, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_BROWSER_DRIVER_PATH, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_MAP_START_URL_TO_PARAM, methodElement, targetMethodNode);
		parseMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_START_URL, methodElement, targetMethodNode);
	}

	private void parseMethodProperty(
			NodePropertyDefs.PropertyId propertyId, 
			Element methodElement, 
			MethodNode targetMethodNode) {
		
		String value = getPropertyValue(propertyId, methodElement);
		if (StringHelper.isNullOrEmpty(value)) {
			return;
		}
		targetMethodNode.setPropertyValue(propertyId, value);		
	}

	public Optional<MethodParameterNode> parseMethodParameter(
			Element parameterElement, MethodNode method, List<String> errorList) {

		String name, type;
		String defaultValue = null;
		String expected = String.valueOf(false);
		
		try {
			assertNodeTag(parameterElement.getQualifiedName(), getParameterNodeName(), errorList);
			name = getElementName(parameterElement, errorList);
			type = getAttributeValue(parameterElement, TYPE_NAME_ATTRIBUTE, errorList);
			
			if (parameterElement.getAttribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME) != null) {
				expected = getAttributeValue(parameterElement, PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME, errorList);
				defaultValue = getAttributeValue(parameterElement, DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, errorList);
			}
			
		} catch (ParserException e) {
			return Optional.empty();
		}
		
		MethodParameterNode targetMethodParameterNode = 
				new MethodParameterNode(
						name, type, defaultValue, Boolean.parseBoolean(expected), method.getModelChangeRegistrator()
                );

		parseParameterProperties(parameterElement, targetMethodParameterNode);

		if (parameterElement.getAttribute(PARAMETER_IS_LINKED_ATTRIBUTE_NAME) != null) {
			boolean linked ;
			
			try {
				linked = Boolean.parseBoolean(getAttributeValue(parameterElement, PARAMETER_IS_LINKED_ATTRIBUTE_NAME, errorList));
			} catch (ParserException e) {
				return Optional.empty();
			}
			
			targetMethodParameterNode.setLinked(linked);
		}

		if (parameterElement.getAttribute(PARAMETER_LINK_ATTRIBUTE_NAME) != null && method != null && method.getClassNode() != null) {
			String linkPath;
			
			try {
				linkPath = getAttributeValue(parameterElement, PARAMETER_LINK_ATTRIBUTE_NAME, errorList);
			} catch (ParserException e) {
				return Optional.empty();
			}
			
			GlobalParameterNode link = method.getClassNode().findGlobalParameter(linkPath);
			
			if (link != null) {
				targetMethodParameterNode.setLink(link);
			} else {
				targetMethodParameterNode.setLinked(false);
			}
		} else {
			targetMethodParameterNode.setLinked(false);
		}

		for (Element child : getIterableChildren(parameterElement, getChoiceNodeName())) {
			Optional<ChoiceNode> node = parseChoice(child, targetMethodParameterNode.getModelChangeRegistrator(), errorList);
			if (node.isPresent()) {
				targetMethodParameterNode.addChoice(node.get());
			}
		}

		targetMethodParameterNode.setDescription(parseComments(parameterElement));
		if (targetMethodParameterNode.isLinked() == false) {
			targetMethodParameterNode.setTypeComments(parseTypeComments(parameterElement));
		}

		return Optional.ofNullable(targetMethodParameterNode);
	}

	private void parseParameterProperties(Element parameterElement, AbstractParameterNode targetAbstractParameterNode) {
		
		parseParameterProperty(
				NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE, 
				parameterElement, 
				targetAbstractParameterNode);

		parseParameterProperty(
				NodePropertyDefs.PropertyId.PROPERTY_OPTIONAL, 
				parameterElement, 
				targetAbstractParameterNode);		

		parseParameterProperty(
				NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_TYPE_OF_ELEMENT, 
				parameterElement, 
				targetAbstractParameterNode);

		parseParameterProperty(
				NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_VALUE_OF_ELEMENT, 
				parameterElement, 
				targetAbstractParameterNode);

		parseParameterProperty(
				NodePropertyDefs.PropertyId.PROPERTY_ACTION, 
				parameterElement, 
				targetAbstractParameterNode);
	}

	private void parseParameterProperty(
			NodePropertyDefs.PropertyId propertyId, 
			Element methodElement, 
			AbstractParameterNode targetAbstractParameterNode) {
		
		String value = getPropertyValue(propertyId, methodElement);
		if (StringHelper.isNullOrEmpty(value)) {
			return;
		}
		targetAbstractParameterNode.setPropertyValue(propertyId, value);		
	}

	public Optional<GlobalParameterNode> parseGlobalParameter(
			Element element, IModelChangeRegistrator modelChangeRegistrator, List<String> errorList) {

		String name, type;
		
		try {
			assertNodeTag(element.getQualifiedName(), getParameterNodeName(), errorList);
			name = getElementName(element, errorList);
			type = getAttributeValue(element, TYPE_NAME_ATTRIBUTE, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}
		
		GlobalParameterNode targetGlobalParameterNode = new GlobalParameterNode(name, type, modelChangeRegistrator);

		parseParameterProperties(element, targetGlobalParameterNode);

		for (Element child : getIterableChildren(element, getChoiceNodeName())) {
			Optional<ChoiceNode> node = parseChoice(child, modelChangeRegistrator, errorList);
			if (node.isPresent()) {
				targetGlobalParameterNode.addChoice(node.get());
			}
		}

		targetGlobalParameterNode.setDescription(parseComments(element));
		targetGlobalParameterNode.setTypeComments(parseTypeComments(element));

		return Optional.ofNullable(targetGlobalParameterNode);
	}

	public Optional<TestCaseNode> parseTestCase(
			Element element, MethodNode method, List<String> errorList) {

		String name;
		
		try {
			assertNodeTag(element.getQualifiedName(), TEST_CASE_NODE_NAME, errorList);
			name = getAttributeValue(element, TEST_SUITE_NAME_ATTRIBUTE, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}

		String[] elementTypes = new String[] { TEST_PARAMETER_NODE_NAME, EXPECTED_PARAMETER_NODE_NAME };
		List<Element> parameterElements = getIterableChildren(element, elementTypes);
		List<MethodParameterNode> parameters = method.getMethodParameters();

		List<ChoiceNode> testData = new ArrayList<ChoiceNode>();

		if (parameters.size() != parameterElements.size()) {
			errorList.add(Messages.WRONG_NUMBER_OF_TEST_PAREMETERS(name));
			return Optional.empty();
		}

		for (int i = 0; i < parameterElements.size(); i++) {
			Element testParameterElement = parameterElements.get(i);
			MethodParameterNode parameter = parameters.get(i);
			ChoiceNode testValue = null;

			if (testParameterElement.getLocalName().equals(SerializationConstants.TEST_PARAMETER_NODE_NAME)) {
				String choiceName;
				
				try {
					choiceName = getAttributeValue(testParameterElement, getChoiceAttributeName(), errorList);
				} catch (ParserException e) {
					return Optional.empty();
				}
				
				testValue = parameter.getChoice(choiceName);
				if (testValue == null) {
					errorList.add(Messages.PARTITION_DOES_NOT_EXIST(parameter.getName(), choiceName));
					return Optional.empty();
				}
				
			} else if (testParameterElement.getLocalName().equals(SerializationConstants.EXPECTED_PARAMETER_NODE_NAME)) {
				String valueString;
				
				try {
					valueString = getAttributeValue(testParameterElement, SerializationConstants.VALUE_ATTRIBUTE_NAME, errorList);
				} catch (ParserException e) {
					return Optional.empty();
				}
				
				if (valueString == null) {
					errorList.add(Messages.MISSING_VALUE_ATTRIBUTE_IN_TEST_CASE_ELEMENT);
					return Optional.empty();
				}
				
				testValue = new ChoiceNode(EXPECTED_VALUE_CHOICE_NAME, valueString, parameter.getModelChangeRegistrator());
				testValue.setParent(parameter);
			}
			
			testData.add(testValue);
		}

		TestCaseNode targetTestCaseNode = new TestCaseNode(name, method.getModelChangeRegistrator(), testData);
		targetTestCaseNode.setDescription(parseComments(element));
		
		return Optional.ofNullable(targetTestCaseNode);
	}

	public Optional<ConstraintNode> parseConstraint(Element element, MethodNode method, List<String> errorList) {
		
		String name;
		
		try {
			assertNodeTag(element.getQualifiedName(), CONSTRAINT_NODE_NAME, errorList);
			name = getElementName(element, errorList);
		} catch (ParserException e) {
			return Optional.empty();
		}

		Optional<AbstractStatement> precondition = null;
		Optional<AbstractStatement> postcondition = null;

		if ((getIterableChildren(element, SerializationConstants.CONSTRAINT_PRECONDITION_NODE_NAME).size() != 1) ||
				(getIterableChildren(element, SerializationConstants.CONSTRAINT_POSTCONDITION_NODE_NAME).size() != 1)) {
			
			errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
			return Optional.empty();
		}
		
		for (Element child : getIterableChildren(element, SerializationConstants.CONSTRAINT_PRECONDITION_NODE_NAME)) {
			if (child.getLocalName().equals(SerializationConstants.CONSTRAINT_PRECONDITION_NODE_NAME)) {
				if (getIterableChildren(child).size() == 1) {
					//there is only one statement per precondition or postcondition that is either
					//a single statement or statement array
					precondition = parseStatement(child.getChildElements().get(0), method, errorList);
				} else {
					errorList.add(Messages.MALFORMED_CONSTRAINT_NODE_DEFINITION(method.getName(), name));
					return Optional.empty();
				}
			}
		}
		
		for (Element child : getIterableChildren(element, SerializationConstants.CONSTRAINT_POSTCONDITION_NODE_NAME)) {
			if (child.getLocalName().equals(SerializationConstants.CONSTRAINT_POSTCONDITION_NODE_NAME)) {
				if (getIterableChildren(child).size() == 1) {
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
						ConstraintType.EXTENDED_FILTER,  // TODO CONSTRAINTS-NEW
						precondition.get(),
						postcondition.get(),
						method.getModelChangeRegistrator());

		ConstraintNode targetConstraint = new ConstraintNode(name, constraint, method.getModelChangeRegistrator());

		targetConstraint.setDescription(parseComments(element));

		return Optional.ofNullable(targetConstraint);
	}

	public Optional<AbstractStatement> parseStatement(Element element, MethodNode method, List<String> errorList) {

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
			Element element, MethodNode method, List<String> errorList) throws ParserException {

		assertNodeTag(element.getQualifiedName(), CONSTRAINT_STATEMENT_ARRAY_NODE_NAME, errorList);

		StatementArray statementArray = null;
		String operatorValue = getAttributeValue(element, SerializationConstants.STATEMENT_OPERATOR_ATTRIBUTE_NAME, errorList);
		
		switch(operatorValue) {
			case SerializationConstants.STATEMENT_OPERATOR_OR_ATTRIBUTE_VALUE:
				statementArray = new StatementArray(StatementArrayOperator.OR, method.getModelChangeRegistrator());
				break;
			case SerializationConstants.STATEMENT_OPERATOR_AND_ATTRIBUTE_VALUE:
				statementArray = new StatementArray(StatementArrayOperator.AND, method.getModelChangeRegistrator());
				break;
			default:
				errorList.add(Messages.WRONG_STATEMENT_ARRAY_OPERATOR(method.getName(), operatorValue));
				return null;
		}
		
		for (Element child : getIterableChildren(element)) {
			Optional<AbstractStatement> childStatement = parseStatement(child, method, errorList);
			if (childStatement.isPresent()) {
				statementArray.addStatement(childStatement.get());
			}
		}
		
		return statementArray;
	}

	public StaticStatement parseStaticStatement(
			Element element, IModelChangeRegistrator modelChangeRegistrator, List<String> errorList) throws ParserException {

		assertNodeTag(element.getQualifiedName(), CONSTRAINT_STATIC_STATEMENT_NODE_NAME, errorList);

		String valueString = getAttributeValue(element, SerializationConstants.STATIC_VALUE_ATTRIBUTE_NAME, errorList);
		
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

	public RelationStatement parseChoiceStatement(
			Element element, MethodNode method, List<String> errorList) throws ParserException {
		
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_CHOICE_STATEMENT_NODE_NAME, errorList);

		String parameterName = getAttributeValue(element, getStatementParameterAttributeName(), errorList);

		MethodParameterNode parameter = (MethodParameterNode)method.findParameter(parameterName);
		if (parameter == null || parameter.isExpected()) {
			errorList.add(Messages.WRONG_PARAMETER_NAME(parameterName, method.getName()));
			return null;
		}

		String choiceName = getAttributeValue(element, getStatementChoiceAttributeName(), errorList);
		ChoiceNode choice = parameter.getChoice(choiceName);
		if (choice == null) {
			errorList.add(Messages.WRONG_PARTITION_NAME(choiceName, parameterName, method.getName()));
			return null;
		}

		String relationName = getAttributeValue(element, SerializationConstants.STATEMENT_RELATION_ATTRIBUTE_NAME, errorList);
		EMathRelation relation = getRelation(relationName, errorList);

		return RelationStatement.createStatementWithChoiceCondition(parameter, relation, choice);
	}

	public RelationStatement parseParameterStatement(
			Element element, MethodNode method, List<String> errorList) throws ParserException { 
		
		assertNodeTag(element.getQualifiedName(), SerializationConstants.CONSTRAINT_PARAMETER_STATEMENT_NODE_NAME, errorList);

		String parameterName = getAttributeValue(element, getStatementParameterAttributeName(), errorList);

		MethodParameterNode leftParameterNode = (MethodParameterNode)method.findParameter(parameterName);
		if (leftParameterNode == null || leftParameterNode.isExpected()) {
			errorList.add(Messages.WRONG_PARAMETER_NAME(parameterName, method.getName()));
			return null;
		}

		String rightParameterName = getAttributeValue(element, SerializationConstants.STATEMENT_RIGHT_PARAMETER_ATTRIBUTE_NAME, errorList);

		MethodParameterNode rightParameterNode = (MethodParameterNode)method.findParameter(rightParameterName);
		if (rightParameterNode == null) {
			errorList.add(Messages.WRONG_PARAMETER_NAME(rightParameterName, method.getName()));
			return null;
		}

		String relationName = getAttributeValue(element, SerializationConstants.STATEMENT_RELATION_ATTRIBUTE_NAME, errorList);
		EMathRelation relation = getRelation(relationName, errorList);

		return RelationStatement.createStatementWithParameterCondition(leftParameterNode, relation, rightParameterNode);
	}

	public RelationStatement parseValueStatement(
			Element element, MethodNode method, List<String> errorList) throws ParserException {

		assertNodeTag(element.getQualifiedName(), SerializationConstants.CONSTRAINT_VALUE_STATEMENT_NODE_NAME, errorList);

		String parameterName = getAttributeValue(element, getStatementParameterAttributeName(), errorList);

		MethodParameterNode leftParameterNode = (MethodParameterNode)method.findParameter(parameterName);
		if (leftParameterNode == null || leftParameterNode.isExpected()) {
			errorList.add(Messages.WRONG_PARAMETER_NAME(parameterName, method.getName()));
			return null;
		}

		String text = getAttributeValue(element, SerializationConstants.STATEMENT_RIGHT_VALUE_ATTRIBUTE_NAME, errorList);

		String relationName = getAttributeValue(element, SerializationConstants.STATEMENT_RELATION_ATTRIBUTE_NAME, errorList);
		EMathRelation relation = getRelation(relationName, errorList);

		return RelationStatement.createStatementWithValueCondition(leftParameterNode, relation, text);
	}

	public RelationStatement parseLabelStatement(
			Element element, MethodNode method, List<String> errorList) throws ParserException {
		
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_LABEL_STATEMENT_NODE_NAME, errorList);

		String parameterName = getAttributeValue(element, getStatementParameterAttributeName(), errorList);
		String label = getAttributeValue(element, SerializationConstants.STATEMENT_LABEL_ATTRIBUTE_NAME, errorList);
		String relationName = getAttributeValue(element, SerializationConstants.STATEMENT_RELATION_ATTRIBUTE_NAME, errorList);

		MethodParameterNode parameter = method.findMethodParameter(parameterName);
		if (parameter == null || parameter.isExpected()) {
			errorList.add(Messages.WRONG_PARAMETER_NAME(parameterName, method.getName()));
			return null;
		}
		EMathRelation relation = getRelation(relationName, errorList);

		return RelationStatement.createStatementWithLabelCondition(parameter, relation, label);
	}

	public ExpectedValueStatement parseExpectedValueStatement(
			Element element, MethodNode method, List<String> errorList) throws ParserException {
		
		assertNodeTag(element.getQualifiedName(), CONSTRAINT_EXPECTED_STATEMENT_NODE_NAME, errorList);

		String parameterName = getAttributeValue(element, getStatementParameterAttributeName(), errorList);
		String valueString = getAttributeValue(element, SerializationConstants.STATEMENT_EXPECTED_VALUE_ATTRIBUTE_NAME, errorList);
		MethodParameterNode parameter = method.findMethodParameter(parameterName);
		
		if (parameter == null || !parameter.isExpected()) {
			errorList.add(Messages.WRONG_PARAMETER_NAME(parameterName, method.getName()));
			return null;
		}
		
		ChoiceNode condition = new ChoiceNode("expected", valueString, parameter.getModelChangeRegistrator());
		condition.setParent(parameter);

		return new ExpectedValueStatement(parameter, condition, new JavaPrimitiveTypePredicate());
	}

	public Optional<ChoiceNode> parseChoice(
			Element element, IModelChangeRegistrator modelChangeRegistrator, List<String> errorList) {

		String name, value;
		boolean isRandomized;
		
		try {
			assertNodeTag(element.getQualifiedName(), getChoiceNodeName(), errorList);
			name = getElementName(element, errorList);
			value = getAttributeValue(element, VALUE_ATTRIBUTE, errorList);
			isRandomized = getIsRandomizedValue(element, NODE_IS_RADOMIZED_ATTRIBUTE);
		} catch (ParserException e) {
			return Optional.empty();
		}

		ChoiceNode choice = new ChoiceNode(name, value, modelChangeRegistrator);
		choice.setRandomizedValue(isRandomized);
		choice.setDescription(parseComments(element));

		for (Element child : getIterableChildren(element)) {
			
			if (child.getLocalName() == getChoiceNodeName()) {
				Optional<ChoiceNode> node = parseChoice(child, modelChangeRegistrator, errorList);
				if (node.isPresent()) {
					choice.addChoice(node.get());
				} else {
					return Optional.empty();
				}

			}
			
			if (child.getLocalName() == SerializationConstants.LABEL_NODE_NAME) {
				choice.addLabel(fWhiteCharConverter.decode(child.getAttributeValue(SerializationConstants.LABEL_ATTRIBUTE_NAME)));
			}
		}

		return Optional.ofNullable(choice);
	}

	private static void assertNodeTag(
			String qualifiedName, String expectedName, List<String> errorList) throws ParserException {
		
		if (qualifiedName.equals(expectedName) == false) {
			errorList.add("Unexpected node name: " + qualifiedName + " instead of " + expectedName);
			ParserException.create();
		}
		
	}
	
	protected static List<Element> getIterableChildren(Element element) {
		
		ArrayList<Element> list = new ArrayList<Element>();
		Elements children = element.getChildElements();
		
		for (int i = 0; i < children.size(); i++) {
			Node node = children.get(i);
			if (node instanceof Element) {
				list.add((Element)node);
			}
		}
		
		return list;
	}

	protected static List<Element> getIterableChildren(Element element, String name) {

		List<Element> elements = getIterableChildren(element);
		Iterator<Element> it = elements.iterator();

		while (it.hasNext()) {
			if (it.next().getLocalName().equals(name) == false) {
				it.remove();
			}
		}
		
		return elements;
	}

	protected static List<Element> getIterableChildren(Element element, String[] names) {

		List<String> listOfNames = Arrays.asList(names);

		List<Element> elements = getIterableChildren(element);
		Iterator<Element> it = elements.iterator();

		while (it.hasNext()) {
			if (!listOfNames.contains(it.next().getLocalName())) {
				it.remove();
			}
		}

		return elements;
	}

	protected String getElementName(
			Element element, List<String> errorList) throws ParserException {
		
		String name = element.getAttributeValue(SerializationConstants.NODE_NAME_ATTRIBUTE);
		
		if (name == null) {
			errorList.add(Messages.MISSING_ATTRIBUTE(element, SerializationConstants.NODE_NAME_ATTRIBUTE));
			ParserException.create();
		}
		
		return fWhiteCharConverter.decode(name);
	}

	protected String getAttributeValue(
			Element element, String attributeName, List<String> errorList) throws ParserException {
		
		String value = element.getAttributeValue(attributeName);
		
		if (value == null) {
			errorList.add(Messages.MISSING_ATTRIBUTE(element, attributeName));
			ParserException.create();
		}
		
		return fWhiteCharConverter.decode(value);
	}

	protected boolean getIsRandomizedValue(Element element, String attributeName) throws ParserException {
		String isRandomizedValue = element.getAttributeValue(attributeName);
		
		if (isRandomizedValue == null) {
			return false;
		}
		
		return Boolean.parseBoolean(fWhiteCharConverter.decode(isRandomizedValue));
	}

	protected EMathRelation getRelation(
			String relationName, List<String> errorList) throws ParserException {

		EMathRelation relation = EMathRelation.getRelation(relationName);

		if (relation == null) {
			errorList.add(Messages.WRONG_OR_MISSING_RELATION_FORMAT(relationName));
			ParserException.report(Messages.WRONG_OR_MISSING_RELATION_FORMAT(relationName));
		}

		return relation;
	}

	protected String parseComments(Element element) {
		
		if (element.getChildElements(SerializationConstants.COMMENTS_BLOCK_TAG_NAME).size() > 0) {
			Element comments = element.getChildElements(SerializationConstants.COMMENTS_BLOCK_TAG_NAME).get(0);
			if (comments.getChildElements(SerializationConstants.BASIC_COMMENTS_BLOCK_TAG_NAME).size() > 0) {
				Element basicComments = comments.getChildElements(SerializationConstants.BASIC_COMMENTS_BLOCK_TAG_NAME).get(0);
				return fWhiteCharConverter.decode(basicComments.getValue());
			}
		}
		
		return null;
	}

	protected String parseTypeComments(Element element) {
		
		if (element.getChildElements(SerializationConstants.COMMENTS_BLOCK_TAG_NAME).size() > 0) {
			Element comments = element.getChildElements(SerializationConstants.COMMENTS_BLOCK_TAG_NAME).get(0);
			if (comments.getChildElements(SerializationConstants.TYPE_COMMENTS_BLOCK_TAG_NAME).size() > 0) {
				Element typeComments = comments.getChildElements(SerializationConstants.TYPE_COMMENTS_BLOCK_TAG_NAME).get(0);
				return fWhiteCharConverter.decode(typeComments.getValue());
			}
		}
		
		return null;
	}

	private static String getPropertyValue(NodePropertyDefs.PropertyId propertyId, Element classElement) {
		
		String propertyName = NodePropertyDefs.getPropertyName(propertyId);

		Elements propertyElements = getPropertyElements(classElement);
		if (propertyElements == null) {
			return null;
		}

		int propertiesSize = propertyElements.size();

		for (int cnt = 0; cnt < propertiesSize; cnt++) {
			Element propertyElement = propertyElements.get(cnt);

			String name = getNameFromPropertyElem(propertyElement);

			if (name.equals(propertyName)) {
				return getValueFromPropertyElem(propertyElement);
			}
		}

		return null;		
	}	

	private static Elements getPropertyElements(Element parentElement) {
		
		Elements propertyBlockElements = parentElement.getChildElements(SerializationConstants.PROPERTIES_BLOCK_TAG_NAME);
		if (propertyBlockElements.size() == 0) {
			return null;
		}

		Element firstBlockElement = propertyBlockElements.get(0);
		return firstBlockElement.getChildElements(SerializationConstants.PROPERTY_TAG_NAME);
	}

	private static String getNameFromPropertyElem(Element property) {
		return property.getAttributeValue(SerializationConstants.PROPERTY_ATTRIBUTE_NAME);
	}

	private static String getValueFromPropertyElem(Element property) {
		return property.getAttributeValue(SerializationConstants.PROPERTY_ATTRIBUTE_VALUE);
	}	
}
