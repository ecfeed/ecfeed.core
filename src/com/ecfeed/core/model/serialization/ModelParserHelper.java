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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.NodePropertyDefs;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.StringHelper;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

public class ModelParserHelper  {

	public static ModelParserForClass createStandardModelParserForClass() {

		ModelParserForClass modelParserForClass = new ModelParserForClass();
		return modelParserForClass;
	}

	public static boolean verifyElementName(Element element, String expectedName) {

		return element.getQualifiedName().equals(expectedName);
	}

	public static void assertNameEqualsExpectedName(
			String qualifiedName, String expectedName, ListOfStrings errorList) {

		if (qualifiedName.equals(expectedName) == false) {
			errorList.add("Unexpected node name: " + qualifiedName + " instead of " + expectedName);
		}
	}

	static List<Element> getIterableChildren(Element element) {

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

	static List<Element> getIterableChildren(Element element, String name) {

		List<Element> elements = getIterableChildren(element);
		Iterator<Element> it = elements.iterator();

		while (it.hasNext()) {
			if (it.next().getLocalName().equals(name) == false) {
				it.remove();
			}
		}

		return elements;
	}

	static List<Element> getIterableChildren(Element element, String[] names) {

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

	static String getElementName(
			Element element, ListOfStrings errorList) {

		String name = element.getAttributeValue(SerializationConstants.NODE_NAME_ATTRIBUTE);

		if (name == null) {
			errorList.add(Messages.MISSING_ATTRIBUTE(element, SerializationConstants.NODE_NAME_ATTRIBUTE));
			return null;
		}

		return WhiteCharConverter.getInstance().decode(name);
	}

	static String getAttributeValue(
			Element element, String attributeName, ListOfStrings errorList) {

		String value = element.getAttributeValue(attributeName);

		if (value == null) {
			errorList.add(Messages.MISSING_ATTRIBUTE(element, attributeName));
		}

		return WhiteCharConverter.getInstance().decode(value);
	}

	static String getAttributeValue(Element element, String attributeName) {
		String value = element.getAttributeValue(attributeName);

		if (value == null) {
			return null;
		}

		return WhiteCharConverter.getInstance().decode(value);
	}

	static boolean getIsRandomizedValue(Element element, String attributeName) {
		String isRandomizedValue = element.getAttributeValue(attributeName);

		if (isRandomizedValue == null) {
			return false;
		}

		return Boolean.parseBoolean(WhiteCharConverter.getInstance().decode(isRandomizedValue));
	}

	static EMathRelation parseRelationName(
			String relationName, ListOfStrings errorList) {

		EMathRelation relation = EMathRelation.parse(relationName);

		if (relation == null) {
			errorList.add(Messages.WRONG_OR_MISSING_RELATION_FORMAT(relationName));
		}

		return relation;
	}

	static String parseComments(Element element) {

		if (element.getChildElements(SerializationConstants.COMMENTS_BLOCK_TAG_NAME).size() > 0) {
			Element comments = element.getChildElements(SerializationConstants.COMMENTS_BLOCK_TAG_NAME).get(0);
			if (comments.getChildElements(SerializationConstants.BASIC_COMMENTS_BLOCK_TAG_NAME).size() > 0) {
				Element basicComments = comments.getChildElements(SerializationConstants.BASIC_COMMENTS_BLOCK_TAG_NAME).get(0);
				return WhiteCharConverter.getInstance().decode(basicComments.getValue());
			}
		}

		return null;
	}

	static String parseTypeComments(Element element) {

		if (element.getChildElements(SerializationConstants.COMMENTS_BLOCK_TAG_NAME).size() > 0) {
			
			Element comments = element.getChildElements(SerializationConstants.COMMENTS_BLOCK_TAG_NAME).get(0);
			if (comments.getChildElements(SerializationConstants.TYPE_COMMENTS_BLOCK_TAG_NAME).size() > 0) {
				Element typeComments = comments.getChildElements(SerializationConstants.TYPE_COMMENTS_BLOCK_TAG_NAME).get(0);
				return WhiteCharConverter.getInstance().decode(typeComments.getValue());
			}
		}

		return null;
	}

	static String getPropertyValue(NodePropertyDefs.PropertyId propertyId, Element classElement) {

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

	static void parseParameterProperties(Element parameterElement, BasicParameterNode targetAbstractParameterNode) {

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

	static void parseParameterProperty(
			NodePropertyDefs.PropertyId propertyId, 
			Element methodElement, 
			BasicParameterNode targetAbstractParameterNode) {

		String value = ModelParserHelper.getPropertyValue(propertyId, methodElement);
		if (StringHelper.isNullOrEmpty(value)) {
			return;
		}
		targetAbstractParameterNode.setPropertyValue(propertyId, value);		
	}	

}
