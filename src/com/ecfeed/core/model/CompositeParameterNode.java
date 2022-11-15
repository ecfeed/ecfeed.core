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

import com.ecfeed.core.utils.JavaLanguageHelper;

public class CompositeParameterNode extends AbstractParameterNode {

	public CompositeParameterNode(
			String name,
			IModelChangeRegistrator modelChangeRegistrator) {
		
		super(name, modelChangeRegistrator);

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		createDefaultProperties();
	}

	public boolean isGlobalParameter() {
		
		IAbstractNode parent = getParent();
		
		if (parent instanceof MethodNode) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public void setName(String name) {

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		super.setName(name);
	}

	@Override
	public String getNonQualifiedName() {
		return getName();
	}

	@Override
	public String toString() {
		
		return new String(getName());
	}

	@Override
	public CompositeParameterNode makeClone() {
		CompositeParameterNode copy = 
				new CompositeParameterNode(getName(), getModelChangeRegistrator());

		copy.setProperties(getProperties());
		copy.setParent(this.getParent());

		copy.setParent(getParent());
		return copy;
	}

	@Override
	public List<IAbstractNode> getChildren() {
		
		return null;
	}

	@Override
	public boolean isMatch(IAbstractNode other) {
		
		if (other instanceof CompositeParameterNode == false) {
			return false;
		}
		
		return super.isMatch(other);
	}

	@Override
	public int getChildrenCount() {

		return 0;
	}

	private void createDefaultProperties() {
		// TODO
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		// TODO
		return null;
		//return visitor.visit(this);
	}
	
}
