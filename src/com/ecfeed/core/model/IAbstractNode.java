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

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IAbstractNode {

	void verifyName(String nameInIntrLanguage);
	public int getMyIndex();
	public String getName();
	public void setName(String name);
	public void setCompositeName(String name);
	public void setName(String name, boolean checkName);
	public IModelChangeRegistrator getModelChangeRegistrator();
	public void setParent(IAbstractNode newParent);
	public void setDescription(String desc);
	public String getDescription();
	public List<IAbstractNode> getDirectChildren();
	public List<IAbstractNode> getChildren();
	public abstract int getChildrenCount();
	public boolean hasChildren();
	public List<IAbstractNode> getAncestors();
	public IAbstractNode getContainer();
	public IAbstractNode getParent();
	public IAbstractNode getRoot();
	public IAbstractNode getChild(String qualifiedName);
	public IAbstractNode getSibling(String name);
	public boolean hasSibling(String name);
	public int getSubtreeSize();
	public boolean isMyAncestor(IAbstractNode candidateForAncestor);
	public String getFullNamePath();
	public boolean isMatch(IAbstractNode nodeToCompare);
	public int getMaxIndex();
	public abstract IAbstractNode makeClone(Optional<NodeMapper> nodeMapper);
	public abstract Object accept(IModelVisitor visitor) throws Exception;
	public void setProperties(NodeProperties nodeProperties);
	public NodeProperties getProperties();
	public int getMaxChildIndexAfterAddingNewChildNode(IAbstractNode potentialChild);
	public void setPropertyValue(NodePropertyDefs.PropertyId propertyId, String value);
	public void setPropertyDefaultValue(NodePropertyDefs.PropertyId propertyId);
	public String getPropertyValue(NodePropertyDefs.PropertyId propertyId);
	public boolean getPropertyValueBoolean(NodePropertyDefs.PropertyId propertyId);
	public Set<String> getPropertyKeys();
	public int getPropertyCount();
	public void removeProperty(NodePropertyDefs.PropertyId propertyId);
	void registerChange();
	public abstract String getNonQualifiedName();
	public abstract boolean canAddChild(IAbstractNode child);

}
