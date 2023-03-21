/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.utils.BooleanHelper;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.SignatureHelper;

public abstract class AbstractNode implements IAbstractNode {

	private String fName;
	private IAbstractNode fParent;
	private String fDescription;
	private final IModelChangeRegistrator fModelChangeRegistrator;
	private NodeProperties fProperties = new NodeProperties();
	protected final List<IAbstractNode> EMPTY_CHILDREN_ARRAY = new ArrayList<>();

	public AbstractNode(String name, IModelChangeRegistrator modelChangeRegistrator) {

		verifyName(name);

		fName = name;
		fModelChangeRegistrator = modelChangeRegistrator;
	}

	@Override
	public void verifyName(String nameInIntrLanguage) {

		if (AbstractNodeHelper.isTheSameExtAndIntrLanguage(this)) {
			return;
		}

		String errorMessage = JavaLanguageHelper.verifySeparatorsInName(nameInIntrLanguage);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}
	}

	@Override
	public String toString() {

		return getName();
	}

	@Override
	public int getMyIndex() {

		if(getParent() == null){
			return -1;
		}

		return getParent().getChildren().indexOf(this);
	}

	public static List<String> getFullNames(Collection<? extends IAbstractNode> nodes) {

		List<String> result = new ArrayList<String>();
		for(IAbstractNode node : nodes){
			result.add(node.getName());
		}

		return result;
	}

	@Override
	public String getName() {
		return fName;
	}

	@Override
	public void setName(String name) {

		setName(name, true);
	}

	@Override
	public void setCompositeName(String name) {

		setName(name, true);
	}

	@Override
	public void setName(String name, boolean checkName) {

		if (checkName ) {
			verifyName(name);
		}

		fName = name;
		registerChange();
	}

	@Override
	public IModelChangeRegistrator getModelChangeRegistrator() {

		return fModelChangeRegistrator;
	}

	@Override
	public void setParent(IAbstractNode newParent) {
		fParent = newParent;
		registerChange();
	}

	@Override
	public void setDescription(String desc) {

		fDescription = desc;
		registerChange();
	}

	@Override
	public String getDescription() {

		return fDescription;
	}

	@Override
	public List<IAbstractNode> getChildren() {

		return EMPTY_CHILDREN_ARRAY;
	}

	@Override
	public boolean hasChildren() {

		if(getChildren() != null) {
			return (getChildren().size() > 0);
		}

		return false;
	}

	@Override
	public List<IAbstractNode> getAncestors() {

		IAbstractNode parent = getParent();

		if (parent == null) {
			return new ArrayList<>();
		}

		List<IAbstractNode> result = new ArrayList<>();

		List<? extends IAbstractNode> ancestors = parent.getAncestors();

		for (IAbstractNode abstractNode : ancestors) {
			result.add(abstractNode);
		}

		result.add(parent);
		return result;
	}

	@Override
	public IAbstractNode getContainer() {
		IAbstractNode container;
		
		container = MethodNodeHelper.findMethodNode(this);
		
		if (container != null) {
			return container;
		}
		
		container = ClassNodeHelper.findClassNode(this);
		
		if (container != null) {
			return container;
		}
		
		return RootNodeHelper.findRootNode(this);
	}
	
	@Override
	public IAbstractNode getParent() {

		return fParent;
	}

	@Override
	public IAbstractNode getRoot() {

		return RootNodeHelper.findRootNode(this);
	}

	@Override
	public IAbstractNode getChild(String qualifiedName) {

		String regex = "\\" + SignatureHelper.SIGNATURE_NAME_SEPARATOR;
		String[] tokens = qualifiedName.split(regex);
		
		if(tokens.length == 0){
			return null;
		}

		if (tokens.length == 1) {

			List<IAbstractNode> children = getChildren();

			for (IAbstractNode child : children) {
				if (child.getName().equals(tokens[0])) {
					return child;
				}
			}
		} else {

			IAbstractNode nextChild = getChild(tokens[0]);

			if(nextChild == null) { 
				return null;
			}

			//tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
			String newName = qualifiedName.substring(qualifiedName.indexOf(SignatureHelper.SIGNATURE_NAME_SEPARATOR) + 1);
			return nextChild.getChild(newName);
		}

		return null;
	}

	@Override
	public IAbstractNode getSibling(String name) {

		final IAbstractNode parent = getParent();

		if (parent == null)
			return null;

		final List<? extends IAbstractNode> siblings = parent.getChildren();

		for (IAbstractNode sibling : siblings) {

			if (sibling.getName().equals(name) && sibling != this) {
				return sibling;
			}
		}

		return null;
	}

	@Override
	public boolean hasSibling(String name) {

		return getSibling(name) != null;
	}

	@Override
	public int getSubtreeSize() {

		int size = 1;
		for(IAbstractNode child : getChildren()){
			size += child.getSubtreeSize();
		}

		return size;
	}

	@Override
	public boolean isMyAncestor(IAbstractNode candidateForAncestor) {

		IAbstractNode currentNode = this;

		for (;;) {

			IAbstractNode parentNode = currentNode.getParent();

			if (parentNode == null) {
				return false;
			}

			if (parentNode.equals(candidateForAncestor)) {
				return true;
			}

			currentNode = parentNode;
		}
	}

	@Override
	public String getFullNamePath() {

		List<String> path = createNodePathList();

		return convertPathToString(path);
	}

	private List<String> createNodePathList() {

		List<String> path = new ArrayList<String>();

		IAbstractNode currentNode = this;

		for (;;) {

			path.add(currentNode.getName());

			IAbstractNode parentNode = currentNode.getParent();

			if (parentNode == null) {
				break;
			}

			currentNode = parentNode;
		}

		Collections.reverse(path);
		return path;
	}

	private String convertPathToString(List<String> path) {

		final String separator = " | ";

		StringBuilder stringBuilder = new StringBuilder();
		boolean firstTime = true;

		for (String pathPart : path) {

			if (!firstTime) {
				stringBuilder.append(separator);

			}

			stringBuilder.append(pathPart);
			firstTime = false;
		}

		String result = stringBuilder.toString();
		return result;
	}

	@Override
	public boolean isMatch(IAbstractNode nodeToCompare) {

		String name = getName();
		String nameToCompare = nodeToCompare.getName();

		if (!name.equals(nameToCompare)) {
			return false;
		}

		if (!fProperties.isMatch(nodeToCompare.getProperties())) {
			return false;
		}

		return true;
	}

	@Override
	public int getMaxIndex() {

		if (getParent() != null) {
			return getParent().getChildren().size();
		}

		return -1;
	}

	@Override
	public void setProperties(NodeProperties nodeProperties) {

		fProperties = nodeProperties.getCopy();
		registerChange();
	}

	@Override
	public NodeProperties getProperties() {

		return fProperties; 
	}

	@Override
	public int getMaxChildIndex(IAbstractNode potentialChild) {

		return getChildren().size();
	}

	@Override
	public void setPropertyValue(NodePropertyDefs.PropertyId propertyId, String value) {

		NodeProperty nodeProperty = new NodeProperty(NodePropertyDefs.getPropertyType(propertyId), value);
		fProperties.put(NodePropertyDefs.getPropertyName(propertyId), nodeProperty);

		registerChange();
	}

	@Override
	public void setPropertyDefaultValue(NodePropertyDefs.PropertyId propertyId) {

		NodeProperty nodeProperty = 
				new NodeProperty(
						NodePropertyDefs.getPropertyType(propertyId), 
						NodePropertyDefs.getPropertyDefaultValue(propertyId, null));

		fProperties.put(NodePropertyDefs.getPropertyName(propertyId), nodeProperty);

		registerChange();
	}	

	@Override
	public String getPropertyValue(NodePropertyDefs.PropertyId propertyId) {

		String propertyName = NodePropertyDefs.getPropertyName(propertyId);

		NodeProperty nodeProperty = fProperties.get(propertyName);
		if (nodeProperty == null) {
			return null;
		}

		return nodeProperty.getValue();
	}

	@Override
	public boolean getPropertyValueBoolean(NodePropertyDefs.PropertyId propertyId) {

		String str = getPropertyValue(propertyId);
		return BooleanHelper.parseBoolean(str);
	}	

	@Override
	public Set<String> getPropertyKeys() {

		return fProperties.getKeys();
	}

	@Override
	public int getPropertyCount() {

		return fProperties.size();
	}

	@Override
	public void removeProperty(NodePropertyDefs.PropertyId propertyId) {

		fProperties.remove(NodePropertyDefs.getPropertyName(propertyId));
		registerChange();
	}

	@Override
	public void registerChange() {

		if (fModelChangeRegistrator == null) {
			return;
		}

		fModelChangeRegistrator.registerChange();
	}
}
