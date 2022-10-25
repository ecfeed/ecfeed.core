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

import static com.ecfeed.core.model.serialization.SerializationConstants.BASIC_COMMENTS_BLOCK_TAG_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CLASS_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.COMMENTS_BLOCK_TAG_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_POSTCONDITION_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CONSTRAINT_PRECONDITION_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.EXPECTED_PARAMETER_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.LABEL_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.LABEL_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.METHOD_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.NODE_IS_RADOMIZED_ATTRIBUTE;
import static com.ecfeed.core.model.serialization.SerializationConstants.NODE_NAME_ATTRIBUTE;
import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_IS_LINKED_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.PARAMETER_LINK_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.PROPERTIES_BLOCK_TAG_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.PROPERTY_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.PROPERTY_ATTRIBUTE_TYPE;
import static com.ecfeed.core.model.serialization.SerializationConstants.PROPERTY_ATTRIBUTE_VALUE;
import static com.ecfeed.core.model.serialization.SerializationConstants.PROPERTY_TAG_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.ROOT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.TEST_CASE_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.TEST_PARAMETER_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.TEST_SUITE_NAME_ATTRIBUTE;
import static com.ecfeed.core.model.serialization.SerializationConstants.TYPE_COMMENTS_BLOCK_TAG_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.TYPE_NAME_ATTRIBUTE;
import static com.ecfeed.core.model.serialization.SerializationConstants.VALUE_ATTRIBUTE;
import static com.ecfeed.core.model.serialization.SerializationConstants.VALUE_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.VERSION_ATTRIBUTE;

import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

public abstract class XomBuilder implements IModelVisitor {

	private final SerializatorParams fSerializatorParams;
	private WhiteCharConverter fWhiteCharConverter = new WhiteCharConverter();

	protected abstract String getParameterNodeName();
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
				targetRootElement.appendChild((Element)visit(classNode));
			}
		}

		for (AbstractParameterNode parameterNode : rootNode.getParameters()) {

			GlobalParameterNode globalParameterNode = (GlobalParameterNode)parameterNode;
			
			if (shouldSerializeNode(globalParameterNode)) {
				targetRootElement.appendChild((Element)visit(globalParameterNode));
			}
		}

		return targetRootElement;
	}

	@Override
	public Object visit(ClassNode classNode) throws Exception {

		Element targetClassElement = createTargetClassElement(classNode);

		for (MethodNode methodNode : classNode.getMethods()) {

			if (shouldSerializeNode(methodNode)) {
				targetClassElement.appendChild((Element)visit(methodNode));
			}
		}

		for (GlobalParameterNode parameterNode : classNode.getGlobalParameters()) {

			if (shouldSerializeNode(parameterNode)) {
				targetClassElement.appendChild((Element)visit(parameterNode));
			}
		}

		return targetClassElement;
	}

	@Override
	public Object visit(MethodNode methodNode) throws Exception {

		Element targetMethodElement = createTargetMethodElement(methodNode);

		for (MethodParameterNode parameter : methodNode.getMethodParameters()) {

			if (shouldSerializeNode(parameter)) {
				targetMethodElement.appendChild((Element)parameter.accept(this));
			}
		}

		for (ConstraintNode constraint : methodNode.getConstraintNodes()) {

			if (shouldSerializeNode(constraint)) {
				targetMethodElement.appendChild((Element)constraint.accept(this));
			}
		}

		for (TestCaseNode testCase : methodNode.getTestCases()) {

			if (shouldSerializeNode(testCase)) {
				targetMethodElement.appendChild((Element)testCase.accept(this));
			}
		}

		return targetMethodElement;
	}

	@Override
	public Object visit(MethodParameterNode node)  throws Exception {

		Element targetParameterElement = createTargetMethodParameterElement(node); 

		for (ChoiceNode choiceNode : node.getRealChoices()) {

			if (shouldSerializeNode(choiceNode)) {
				targetParameterElement.appendChild((Element)choiceNode.accept(this));
			}
		}

		return targetParameterElement;
	}

	@Override
	public Object visit(GlobalParameterNode node) throws Exception {

		Element targetGlobalParamElement = createTargetGlobalParameterElement(node);

		for (ChoiceNode choiceNode : node.getChoices()) {

			if (shouldSerializeNode(choiceNode)) {
				targetGlobalParamElement.appendChild((Element)choiceNode.accept(this));
			}
		}

		return targetGlobalParamElement;
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

		boolean isRandomizedValue = ((ChoiceNode)node).isRandomizedValue();
		targetChoiceElement.addAttribute(new Attribute(NODE_IS_RADOMIZED_ATTRIBUTE, String.valueOf(isRandomizedValue)));

		return targetChoiceElement;
	}

	private Element createTargetClassElement(ClassNode classNode) {
		Element targetClassElement = createAbstractElement(CLASS_NODE_NAME, classNode);

		return targetClassElement;
	}

	private Element createTargetMethodParameterElement(MethodParameterNode node) {

		Element targetParameterElement = createAbstractElement(getParameterNodeName(), node);

		if (fSerializatorParams.getSerializeProperties()) {
			addParameterProperties(node, targetParameterElement);
		}

		if (fSerializatorParams.getSerializeComments()) {
			appendTypeComments(targetParameterElement, node);
		}

		encodeAndAddAttribute(
				targetParameterElement, new Attribute(TYPE_NAME_ATTRIBUTE, node.getRealType()), 
				fWhiteCharConverter);

		encodeAndAddAttribute(
				targetParameterElement, 
				new Attribute(PARAMETER_IS_EXPECTED_ATTRIBUTE_NAME, Boolean.toString(node.isExpected())),
				fWhiteCharConverter);

		encodeAndAddAttribute(
				targetParameterElement, 
				new Attribute(DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, node.getDefaultValueForSerialization()),
				fWhiteCharConverter);

		encodeAndAddAttribute(
				targetParameterElement, 
				new Attribute(PARAMETER_IS_LINKED_ATTRIBUTE_NAME, Boolean.toString(node.isLinked())),
				fWhiteCharConverter);

		if (node.getLinkToGlobalParameter() != null) {
			encodeAndAddAttribute(
					targetParameterElement, 
					new Attribute(PARAMETER_LINK_ATTRIBUTE_NAME, node.getLinkToGlobalParameter().getQualifiedName()), 
					fWhiteCharConverter);
		}

		return targetParameterElement;
	}

	private Element createTargetGlobalParameterElement(GlobalParameterNode node) {
		Element targetGlobalParamElement = createAbstractElement(getParameterNodeName(), node);

		if (fSerializatorParams.getSerializeProperties()) {
			addParameterProperties(node, targetGlobalParamElement);
		}

		if (fSerializatorParams.getSerializeComments()) {
			appendTypeComments(targetGlobalParamElement, node);
		}

		encodeAndAddAttribute(
				targetGlobalParamElement, 
				new Attribute(TYPE_NAME_ATTRIBUTE, node.getType()), 
				fWhiteCharConverter);
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

	private void appendChoiceOfTestCase(Element targetTestCaseElement,
			TestCaseNode node, ChoiceNode choiceNode) {

		if (choiceNode.getParameter() != null && node.getMethodParameter(choiceNode).isExpected()) {

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

	private void appendTypeComments(Element element, MethodParameterNode node) {

		if (node.isLinked() == false) {
			appendTypeComments(element, (AbstractParameterNode)node);
		}
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

		typeComments.appendChild(fWhiteCharConverter.encode(node.getTypeComments()));
		commentElement.appendChild(typeComments);
	}

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
