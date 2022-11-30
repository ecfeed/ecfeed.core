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

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.LogHelperCore;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import java.util.Objects;

import static com.ecfeed.core.model.serialization.SerializationConstants.*;

public abstract class XomBuilder implements IModelVisitor {

	private final SerializatorParams fSerializatorParams;
	private WhiteCharConverter fWhiteCharConverter = new WhiteCharConverter();

	protected abstract String getBasicParameterNodeName();
	protected abstract String getCompositeParameterNodeName();
	protected abstract String getStatementParameterAttributeName();
	protected abstract String getChoiceNodeName();
	protected abstract String getChoiceAttributeName();
	protected abstract String getStatementChoiceAttributeName();
	protected abstract int getModelVersion();
	protected abstract void addConstraintTypeAttribute(ConstraintType constraintType, Element targetConstraintElement);

	XomBuilder(SerializatorParams serializatorParams) {

		if (serializatorParams == null) {
			fSerializatorParams = new SerializatorParams(null, true, true);
		} else {
			fSerializatorParams = serializatorParams;
		}
	}

	@Override
	public Object visit(RootNode rootNode) throws Exception {
		Element targetRootElement = createTargetRootElement(rootNode);

		for (ClassNode classNode : rootNode.getClasses()) {

			if (shouldSerializeNode(classNode)) {
				targetRootElement.appendChild((Element) visit(classNode));
			}
		}

		for (AbstractParameterNode parameterNode : rootNode.getParameters()) {

			if (shouldSerializeNode(parameterNode)) {
				targetRootElement.appendChild(createTargetParameterElement(parameterNode));
			}
		}

		return targetRootElement;
	}

	@Override
	public Object visit(ClassNode classNode) throws Exception {
		Element targetClassElement = createTargetClassElement(classNode);

		for (MethodNode methodNode : classNode.getMethods()) {

			if (shouldSerializeNode(methodNode)) {
				targetClassElement.appendChild((Element) visit(methodNode));
			}
		}

		for (AbstractParameterNode parameterNode : classNode.getParameters()) {

			if (shouldSerializeNode(parameterNode)) {
				targetClassElement.appendChild(createTargetParameterElement(parameterNode));
			}
		}

		return targetClassElement;
	}

	@Override
	public Object visit(MethodNode methodNode) throws Exception {
		Element targetMethodElement = createTargetMethodElement(methodNode);

		for (AbstractParameterNode parameterNode : methodNode.getParameters()) {

			if (shouldSerializeNode(parameterNode)) {
				targetMethodElement.appendChild((Element) parameterNode.accept(this));
			}
		}

		for (ConstraintNode constraintNode : methodNode.getConstraintNodes()) {

			if (shouldSerializeNode(constraintNode)) {
				targetMethodElement.appendChild((Element) constraintNode.accept(this));
			}
		}

		for (TestCaseNode testCaseNode : methodNode.getTestCases()) {

			if (shouldSerializeNode(testCaseNode)) {
				targetMethodElement.appendChild((Element)testCaseNode.accept(this));
			}
		}

		if (methodNode.isDeployed()) {
			if (MethodDeployer.validateDeploymentSizeConsistency(methodNode)) {
				MethodDeployer.updateDeploymentNameConsistency(methodNode);

				targetMethodElement.appendChild(createTargetMethodDeployedParametersElement(methodNode));
			} else {
				LogHelperCore.logError("The number of deployed parameters is inconsistent.");
			}
		}

		return targetMethodElement;
	}

	@Override
	public Object visit(BasicParameterNode parameterNode)  throws Exception {
		Element targetParamElement;

		if (parameterNode.isGlobalParameter()) {
			targetParamElement = createTargetGlobalBasicParameterElement(parameterNode);

			for (ChoiceNode choiceNode : parameterNode.getChoices()) {

				if (shouldSerializeNode(choiceNode)) {
					targetParamElement.appendChild((Element) choiceNode.accept(this));
				}
			}
		} else {
			targetParamElement = createTargetBasicMethodParameterElement(parameterNode);

			for (ChoiceNode choiceNode : parameterNode.getRealChoices()) {

				if (shouldSerializeNode(choiceNode)) {
					targetParamElement.appendChild((Element)choiceNode.accept(this));
				}
			}
		}

		return targetParamElement;
	}
	
	@Override
	public Object visit(CompositeParameterNode parameterNode)  throws Exception {
		Element targetParamElement;

		if (parameterNode.isGlobalParameter()) {
			targetParamElement = createTargetGlobalCompositeParameterElement(parameterNode);

			for (AbstractParameterNode parameterParsed : parameterNode.getParameters()) {
				targetParamElement.appendChild(createTargetParameterElement(parameterParsed));
			}

		} else {
			targetParamElement = createTargetCompositeMethodParameterElement(parameterNode);

			for (AbstractParameterNode parameterParsed : parameterNode.getParameters()) {
				targetParamElement.appendChild(createTargetParameterElement(parameterParsed));
			}
		}

		return targetParamElement;
	}
	
	@Override
	public Object visit(TestSuiteNode node) throws Exception {

		return null;
	}

	@Override
	public Object visit(TestCaseNode node) throws Exception {
		Element targetTestCaseElement = createTargetTestCaseElement(node);

		for (ChoiceNode choiceNode : node.getTestData()) {

			if (shouldSerializeNode(choiceNode)) {
				appendChoiceOfTestCase(targetTestCaseElement, node, choiceNode);
			}
		}

		return targetTestCaseElement;
	}

	@Override
	public Object visit(ConstraintNode node) throws Exception{
		Element targetConstraintElement = createAbstractElement(CONSTRAINT_NODE_NAME, node);

		ConstraintType constraintType = node.getConstraint().getType();
		addConstraintTypeAttribute(constraintType, targetConstraintElement);

		AbstractStatement precondition = node.getConstraint().getPrecondition();
		AbstractStatement postcondition = node.getConstraint().getPostcondition();

		Element preconditionElement = new Element(CONSTRAINT_PRECONDITION_NODE_NAME);
		preconditionElement.appendChild((Element)precondition.accept(
				new XomStatementBuilder(
						getStatementParameterAttributeName(),
						getStatementChoiceAttributeName())));

		Element postconditionElement = new Element(CONSTRAINT_POSTCONDITION_NODE_NAME);
		postconditionElement.appendChild((Element)postcondition.accept(
				new XomStatementBuilder(
						getStatementParameterAttributeName(),
						getStatementChoiceAttributeName())));

		targetConstraintElement.appendChild(preconditionElement);
		targetConstraintElement.appendChild(postconditionElement);

		return targetConstraintElement;
	}

	@Override
	public Object visit(ChoiceNode node) throws Exception {
		Element targetChoiceElement = createTargetChoiceElement(node);

		for (String label : node.getLabels()) {

			Element labelElement = new Element(LABEL_NODE_NAME);
			encodeAndAddAttribute(labelElement, new Attribute(LABEL_ATTRIBUTE_NAME, label), fWhiteCharConverter);
			targetChoiceElement.appendChild(labelElement);
		}

		for (ChoiceNode choiceNode : node.getChoices()) {

			if (shouldSerializeNode(choiceNode)) {
				targetChoiceElement.appendChild((Element)choiceNode.accept(this));
			}
		}

		return targetChoiceElement;
	}

	protected WhiteCharConverter getWhiteCharConverter() {
		return fWhiteCharConverter;
	}

	private Element createTargetChoiceElement(ChoiceNode node) {
		Element targetChoiceElement = createAbstractElement(getChoiceNodeName(), node);

		String legalValue = removeDisallowedXmlCharacters(node);

		encodeAndAddAttribute(targetChoiceElement, new Attribute(VALUE_ATTRIBUTE, legalValue), fWhiteCharConverter);

		boolean isRandomizedValue = node.isRandomizedValue();
		targetChoiceElement.addAttribute(new Attribute(NODE_IS_RADOMIZED_ATTRIBUTE, String.valueOf(isRandomizedValue)));

		return targetChoiceElement;
	}

	private Element createTargetClassElement(ClassNode classNode) {
		Element targetClassElement = createAbstractElement(CLASS_NODE_NAME, classNode);

		return targetClassElement;
	}

	private Element createTargetParameterElement(AbstractParameterNode parameter) throws Exception {

		if (parameter instanceof BasicParameterNode) {
			return (Element) visit((BasicParameterNode) parameter);
		} else if (parameter instanceof CompositeParameterNode) {
			return (Element) visit((CompositeParameterNode) parameter);
		}

		ExceptionHelper.reportRuntimeException("The parameter type is not compatible.");

		return null;
	}

	public Element createDeployedParameter(BasicParameterNode parameterNode) {
		Element targetParamElement;

		if (parameterNode.isGlobalParameter()) {
			targetParamElement = createTargetGlobalDeployedParameterElement(parameterNode);
		} else {
			targetParamElement = createTargetDeployedParameterElement(parameterNode);
		}

		return targetParamElement;
	}

	private Element createTargetDeployedParameterElement(BasicParameterNode node) {
		Element targetBasicParameterElement = createAbstractElement(getBasicParameterNodeName(), node);

		encodeAndAddAttribute(
				targetBasicParameterElement, new Attribute(TYPE_NAME_ATTRIBUTE, node.getRealType()),
				fWhiteCharConverter);

		encodeAndAddAttribute(
				targetBasicParameterElement,
				new Attribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME, Boolean.toString(node.isExpected())),
				fWhiteCharConverter);

		encodeAndAddAttribute(
				targetBasicParameterElement,
				new Attribute(DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, node.getDefaultValueForSerialization()),
				fWhiteCharConverter);

		encodeAndAddAttribute(
				targetBasicParameterElement,
				new Attribute(PARAMETER_IS_LINKED_ATTRIBUTE_NAME, Boolean.toString(node.isLinked())),
				fWhiteCharConverter);

		if (node.getLinkToGlobalParameter() != null) {
			encodeAndAddAttribute(
					targetBasicParameterElement,
					new Attribute(PARAMETER_LINK_ATTRIBUTE_NAME, node.getLinkToGlobalParameter().getQualifiedName()),
					fWhiteCharConverter);
		}

		return targetBasicParameterElement;
	}

	private Element createTargetBasicMethodParameterElement(BasicParameterNode node) {
		Element targetBasicParameterElement = createAbstractElement(getBasicParameterNodeName(), node);

		if (fSerializatorParams.getSerializeProperties()) {
			addParameterProperties(node, targetBasicParameterElement);
		}

		if (fSerializatorParams.getSerializeComments()) {
			appendTypeCommentsForMethodParameterNode(targetBasicParameterElement, node);
		}

		encodeAndAddAttribute(
				targetBasicParameterElement, new Attribute(TYPE_NAME_ATTRIBUTE, node.getRealType()),
				fWhiteCharConverter);

		encodeAndAddAttribute(
				targetBasicParameterElement,
				new Attribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME, Boolean.toString(node.isExpected())),
				fWhiteCharConverter);

		encodeAndAddAttribute(
				targetBasicParameterElement,
				new Attribute(DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, node.getDefaultValueForSerialization()),
				fWhiteCharConverter);

		encodeAndAddAttribute(
				targetBasicParameterElement,
				new Attribute(PARAMETER_IS_LINKED_ATTRIBUTE_NAME, Boolean.toString(node.isLinked())),
				fWhiteCharConverter);

		if (node.getLinkToGlobalParameter() != null) {
			encodeAndAddAttribute(
					targetBasicParameterElement,
					new Attribute(PARAMETER_LINK_ATTRIBUTE_NAME, node.getLinkToGlobalParameter().getQualifiedName()), 
					fWhiteCharConverter);
		}

		return targetBasicParameterElement;
	}

	private Element createTargetCompositeMethodParameterElement(CompositeParameterNode node) {
		Element targetCompositeParameterElement = createAbstractElement(getCompositeParameterNodeName(), node);

		if (fSerializatorParams.getSerializeProperties()) {
			addParameterProperties(node, targetCompositeParameterElement);
		}

		if (fSerializatorParams.getSerializeComments()) {
			appendTypeComments(targetCompositeParameterElement, node);
		}

		return targetCompositeParameterElement;
	}

	private Element createTargetGlobalBasicParameterElement(BasicParameterNode node) {
		Element targetGlobalBasicParamElement = createAbstractElement(getBasicParameterNodeName(), node);

		if (fSerializatorParams.getSerializeProperties()) {
			addParameterProperties(node, targetGlobalBasicParamElement);
		}

		if (fSerializatorParams.getSerializeComments()) {
			appendTypeComments(targetGlobalBasicParamElement, node);
		}

		encodeAndAddAttribute(
				targetGlobalBasicParamElement,
				new Attribute(TYPE_NAME_ATTRIBUTE, node.getType()), 
				fWhiteCharConverter);

		return targetGlobalBasicParamElement;
	}

	private Element createTargetGlobalDeployedParameterElement(BasicParameterNode node) {
		Element targetGlobalBasicParamElement = createAbstractElement(getBasicParameterNodeName(), node);

		encodeAndAddAttribute(
				targetGlobalBasicParamElement,
				new Attribute(TYPE_NAME_ATTRIBUTE, node.getType()),
				fWhiteCharConverter);

		return targetGlobalBasicParamElement;
	}

	private Element createTargetGlobalCompositeParameterElement(CompositeParameterNode node) {
		Element targetGlobalParamElement = createAbstractElement(getCompositeParameterNodeName(), node);

		if (fSerializatorParams.getSerializeProperties()) {
			addParameterProperties(node, targetGlobalParamElement);
		}

		if (fSerializatorParams.getSerializeComments()) {
			appendTypeComments(targetGlobalParamElement, node);
		}

		return targetGlobalParamElement;
	}

	private Element createTargetTestCaseElement(TestCaseNode node) {

		Element targetTestCaseElement = new Element(TEST_CASE_NODE_NAME);

		encodeAndAddAttribute(
				targetTestCaseElement, 
				new Attribute(TEST_SUITE_NAME_ATTRIBUTE, node.getName()), 
				fWhiteCharConverter);

		if (fSerializatorParams.getSerializeComments()) {
			appendComments(targetTestCaseElement, node);
		}

		return targetTestCaseElement;
	}

	private Element createTargetMethodElement(MethodNode methodNode) {
		Element targetMethodElement = createAbstractElement(METHOD_NODE_NAME, methodNode);

		if (fSerializatorParams.getSerializeProperties()) {
			addMethodProperties(methodNode, targetMethodElement);
		}

		return targetMethodElement;
	}

	private Element createTargetMethodDeployedParametersElement(MethodNode methodNode) {
		Element targetMethodDeployedParameters = new Element(METHOD_DEPLOYED_PARAMETERS_NAME);

		for (BasicParameterNode parameter : Objects.requireNonNull(methodNode.getDeployedMethodParameters())) {
			targetMethodDeployedParameters.appendChild(createDeployedParameter(parameter));
		}

		return targetMethodDeployedParameters;
	}

	private boolean shouldSerializeNode(IAbstractNode abstractNode) {

		ISerializerPredicate serializerPredicate = fSerializatorParams.getSerializerPredicate();

		if (serializerPredicate == null) {
			return true;
		}

		return serializerPredicate.shouldSerializeNode(abstractNode);
	}

	private String removeDisallowedXmlCharacters(ChoiceNode node) {

		String value = node.getValueString();

		String xml10pattern = "[^"
				+ "\u0009\r\n"
				+ "\u0020-\uD7FF"
				+ "\uE000-\uFFFD"
				+ "\ud800\udc00-\udbff\udfff"
				+ "]";

		return value.replaceAll(xml10pattern, "");
	}

	private void appendChoiceOfTestCase(Element targetTestCaseElement, TestCaseNode node, ChoiceNode choiceNode) {

		if (choiceNode.getParameter() != null && node.getBasicMethodParameter(choiceNode).isExpected()) {

			Element expectedParameterElement = new Element(EXPECTED_PARAMETER_NODE_NAME);
			Attribute expectedValueAttribute = new Attribute(VALUE_ATTRIBUTE_NAME, choiceNode.getValueString());
			encodeAndAddAttribute(expectedParameterElement, expectedValueAttribute, fWhiteCharConverter);

			targetTestCaseElement.appendChild(expectedParameterElement);

		} else {

			Element testParameterElement = new Element(TEST_PARAMETER_NODE_NAME);
			Attribute choiceNameAttribute = new Attribute(getChoiceAttributeName(), choiceNode.getQualifiedName());

			encodeAndAddAttribute(testParameterElement, choiceNameAttribute, fWhiteCharConverter);
			targetTestCaseElement.appendChild(testParameterElement);
		}
	}

	private void addMethodProperties(MethodNode methodNode, Element targetElement) {

		addMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_METHOD_RUNNER, methodNode, targetElement);
		addMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_MAP_BROWSER_TO_PARAM,  methodNode, targetElement);
		addMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_WEB_BROWSER, methodNode, targetElement);
		addMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_BROWSER_DRIVER_PATH, methodNode, targetElement);
		addMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_MAP_START_URL_TO_PARAM, methodNode, targetElement);
		addMethodProperty(NodePropertyDefs.PropertyId.PROPERTY_START_URL, methodNode, targetElement);
	}

	private void addMethodProperty(NodePropertyDefs.PropertyId propertyId,  MethodNode methodNode, Element targetElement) {

		String value = methodNode.getPropertyValue(propertyId);

		if (value == null) {
			return;
		}

		appendProperty(getPropertyName(propertyId), getPropertyType(propertyId), value, targetElement);
	}

	private void addParameterProperties(AbstractParameterNode abstractParameterNode, Element targetElement) {

		addParameterProperty(NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE, abstractParameterNode, targetElement);
		addParameterProperty(NodePropertyDefs.PropertyId.PROPERTY_OPTIONAL, abstractParameterNode, targetElement);
		addParameterProperty(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_TYPE_OF_ELEMENT, abstractParameterNode, targetElement);
		addParameterProperty(NodePropertyDefs.PropertyId.PROPERTY_FIND_BY_VALUE_OF_ELEMENT, abstractParameterNode, targetElement);
		addParameterProperty(NodePropertyDefs.PropertyId.PROPERTY_ACTION, abstractParameterNode, targetElement);
	}

	private void addParameterProperty(
			NodePropertyDefs.PropertyId propertyId,
			AbstractParameterNode abstractParameterNode,
			Element targetElement) {

		String value = abstractParameterNode.getPropertyValue(propertyId);
		if (value == null) {
			return;
		}
		appendProperty(getPropertyName(propertyId), getPropertyType(propertyId), value, targetElement);
	}

	private Element createAbstractElement(String nodeTag, IAbstractNode node) {

		Element targetAbstractElement = new Element(nodeTag);
		Attribute nameAttr = new Attribute(NODE_NAME_ATTRIBUTE, node.getName());
		encodeAndAddAttribute(targetAbstractElement, nameAttr, fWhiteCharConverter);
		appendComments(targetAbstractElement, node);

		return targetAbstractElement;
	}

	private Element appendComments(Element element, IAbstractNode node) {

		if (node.getDescription() != null) {
			Element commentsBlock = new Element(COMMENTS_BLOCK_TAG_NAME);
			Element basicComments = new Element(BASIC_COMMENTS_BLOCK_TAG_NAME);

			basicComments.appendChild(fWhiteCharConverter.encode(node.getDescription()));
			commentsBlock.appendChild(basicComments);
			element.appendChild(commentsBlock);
			return commentsBlock;
		}

		return null;
	}

	private void appendTypeCommentsForMethodParameterNode(Element element, BasicParameterNode node) {

		if (node.isLinked() == false) {
			appendTypeComments(element, node);
		}
	}

	private void appendTypeComments(Element element, BasicParameterNode node) {

		Elements commentElements = element.getChildElements(COMMENTS_BLOCK_TAG_NAME);
		Element commentElement;

		if (commentElements.size() > 0) {
			commentElement = commentElements.get(0);
		} else {
			commentElement = new Element(COMMENTS_BLOCK_TAG_NAME);
			element.appendChild(commentElement);
		}

		Element typeComments = new Element(TYPE_COMMENTS_BLOCK_TAG_NAME);

		typeComments.appendChild(fWhiteCharConverter.encode(node.getTypeComments()));
		commentElement.appendChild(typeComments);
	}

	private void appendTypeComments(Element element, AbstractParameterNode node) {

		Elements commentElements = element.getChildElements(COMMENTS_BLOCK_TAG_NAME);
		Element commentElement;

		if (commentElements.size() > 0) {
			commentElement = commentElements.get(0);
		} else {
			commentElement = new Element(COMMENTS_BLOCK_TAG_NAME);
			element.appendChild(commentElement);
		}

		Element typeComments = new Element(TYPE_COMMENTS_BLOCK_TAG_NAME);

		commentElement.appendChild(typeComments);
	}
	
//	private void appendTypeComments(Element element, BasicParameterNode node) {
//
//		if (node.isLinked() == false) {
//			appendComments(element, node);
//			return;
//		}
//		
//		Elements commentElements = element.getChildElements(COMMENTS_BLOCK_TAG_NAME);
//		Element commentElement;
//
//		if (commentElements.size() > 0) {
//			commentElement = commentElements.get(0);
//		} else {
//			commentElement = new Element(COMMENTS_BLOCK_TAG_NAME);
//			element.appendChild(commentElement);
//		}
//
//		Element typeComments = new Element(TYPE_COMMENTS_BLOCK_TAG_NAME);
//
//		typeComments.appendChild(fWhiteCharConverter.encode(node.getTypeComments()));
//		commentElement.appendChild(typeComments);
//	}

	public static void encodeAndAddAttribute(
			Element element, Attribute attribute, WhiteCharConverter whiteCharConverter) {

		attribute.setValue(whiteCharConverter.encode(attribute.getValue()));
		element.addAttribute(attribute);
	}

	public static void addIsRandomizedValue(Element element, Attribute attribute) {
		attribute.setValue(Boolean.FALSE.toString());
		element.addAttribute(attribute);
	}

	private Element createTargetRootElement(RootNode rootNode) {
		Element targetRootElement = createAbstractElement(ROOT_NODE_NAME, rootNode);

		String versionStr = Integer.toString(rootNode.getModelVersion());
		Attribute versionAttr = new Attribute(VERSION_ATTRIBUTE, versionStr);
		targetRootElement.addAttribute(versionAttr);
		return targetRootElement;
	}

	private String getPropertyName(NodePropertyDefs.PropertyId propertyId) {

		return NodePropertyDefs.getPropertyName(propertyId);
	}

	private String getPropertyType(NodePropertyDefs.PropertyId propertyId) {

		return NodePropertyDefs.getPropertyType(propertyId);
	}	

	private void appendProperty(String key, String type, String value, Element targetElement) {

		Element propertiesBlock = getPropertiesBlock(targetElement);
		Element propertyElement = createCommonPropertyElement(key, type, value);

		propertiesBlock.appendChild(propertyElement);
	}	

	private Element getPropertiesBlock(Element parentElement) {

		Elements propiertiesBlocks = parentElement.getChildElements(PROPERTIES_BLOCK_TAG_NAME);

		if (propiertiesBlocks.size() == 0) {
			Element propertiesBlock = new Element(PROPERTIES_BLOCK_TAG_NAME);
			parentElement.appendChild(propertiesBlock);
			return propertiesBlock;
		}

		return propiertiesBlocks.get(0);
	}

	private Element createCommonPropertyElement(String name, String type, String value) {

		Element targetPropertyElement = new Element(PROPERTY_TAG_NAME);

		Attribute attributeName = new Attribute(PROPERTY_ATTRIBUTE_NAME, name);
		targetPropertyElement.addAttribute(attributeName);

		Attribute attributeType = new Attribute(PROPERTY_ATTRIBUTE_TYPE, type);
		targetPropertyElement.addAttribute(attributeType);

		Attribute attributeValue = new Attribute(PROPERTY_ATTRIBUTE_VALUE, value);
		targetPropertyElement.addAttribute(attributeValue);

		return targetPropertyElement;
	}
}
