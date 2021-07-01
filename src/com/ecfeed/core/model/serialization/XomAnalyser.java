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

import static com.ecfeed.core.model.serialization.SerializationConstants.ANDROID_RUNNER_ATTRIBUTE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.CLASS_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.ROOT_NODE_NAME;
import static com.ecfeed.core.model.serialization.SerializationConstants.RUN_ON_ANDROID_ATTRIBUTE_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.core.utils.BooleanHolder;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ListOfStrings;
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
	protected abstract ConstraintType getConstraintType(Element element, ListOfStrings errorList) throws ParserException;

	public XomAnalyser() {
	}
	
	public RootNode parseRoot(
			Element element, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings outErrorList) throws ParserException {
		
		assertNodeTag(element.getQualifiedName(), ROOT_NODE_NAME, outErrorList);
		String name = getElementName(element, outErrorList);

		RootNode targetRootNode = new RootNode(name, modelChangeRegistrator, getModelVersion());

		targetRootNode.setDescription(parseComments(element));

		//parameters must be parsed before classes
		
		IModelParserForGlobalParameter modelParserForGlobalParameter = 
				new ModelParserForGlobalParameter();
		
		for (Element child : getIterableChildren(element, getParameterNodeName())) {
			Optional<GlobalParameterNode> node = modelParserForGlobalParameter.parseGlobalParameter(child, targetRootNode.getModelChangeRegistrator(), outErrorList);
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
			Element classElement, RootNode parent, ListOfStrings errorList) throws ParserException {

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

		
		ModelParserForGlobalParameter modelParserForGlobalParameter = new ModelParserForGlobalParameter();
		//parameters must be parsed before classes
		for (Element child : getIterableChildren(classElement, getParameterNodeName())) {
			Optional<GlobalParameterNode> node = modelParserForGlobalParameter.parseGlobalParameter(child, targetClassNode.getModelChangeRegistrator(), errorList);
			if (node.isPresent()) {
				targetClassNode.addParameter(node.get());
			}
		}

		ModelParserForMethod modelParserForMethod = new ModelParserForMethod();
		
		for (Element child : getIterableChildren(classElement, SerializationConstants.METHOD_NODE_NAME)) {
			Optional<MethodNode> node = modelParserForMethod.parseMethod(child, targetClassNode, errorList);
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

	private static void assertNodeTag(
			String qualifiedName, String expectedName, ListOfStrings errorList) throws ParserException {
		
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
			Element element, ListOfStrings errorList) throws ParserException {
		
		String name = element.getAttributeValue(SerializationConstants.NODE_NAME_ATTRIBUTE);
		
		if (name == null) {
			errorList.add(Messages.MISSING_ATTRIBUTE(element, SerializationConstants.NODE_NAME_ATTRIBUTE));
			ParserException.create();
		}
		
		return fWhiteCharConverter.decode(name);
	}

	protected String getAttributeValue(
			Element element, String attributeName, ListOfStrings errorList) throws ParserException {
		
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

	protected EMathRelation parseRelationName(
			String relationName, ListOfStrings errorList) throws ParserException {

		EMathRelation relation = EMathRelation.parse(relationName);

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
