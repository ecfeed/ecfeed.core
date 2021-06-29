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

import static com.ecfeed.core.model.serialization.SerializationConstants.NODE_IS_RADOMIZED_ATTRIBUTE;
import static com.ecfeed.core.model.serialization.SerializationConstants.VALUE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

public abstract class ModelParserForChoice {

	private WhiteCharConverter fWhiteCharConverter = new WhiteCharConverter();

	protected abstract String getChoiceNodeName();

	public ModelParserForChoice() {
	}

	public Optional<ChoiceNode> parseChoice(
			Element element, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings errorList) {

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

}
