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

public abstract class AbstractParameterNode extends AbstractNode {

	private AbstractParameterNode fLinkToGlobalParameter;

	public enum ParameterType {
		BASIC,
		COMPOSITE
	}

	public AbstractParameterNode(String name, IModelChangeRegistrator modelChangeRegistrator) {
		super(name, modelChangeRegistrator);
	}

	public void setLinkToGlobalParameter(AbstractParameterNode node) {

		this.fLinkToGlobalParameter = node;
	}

	public AbstractParameterNode getLinkToGlobalParameter() {

		return fLinkToGlobalParameter;
	}

	public boolean isLinked() {

		AbstractParameterNode linkToGlobalParameter = getLinkToGlobalParameter();
		
		if (linkToGlobalParameter == null) {
			return false;
		}
		
		return true;
	}

	@Override
	public IParametersParentNode getParent() {

		return (IParametersParentNode)(super.getParent());
	}

	@Override
	public int getMyIndex() {

		IParametersParentNode parametersParent = getParametersParent();

		if (parametersParent == null) {
			return -1;
		}

		List<AbstractParameterNode> parameters = parametersParent.getParameters();

		return parameters.indexOf(this);
	}

	@Override
	public int getMaxIndex() {

		IParametersParentNode parametersParent = getParametersParent();

		if (parametersParent == null) {
			return -1;
		}

		List<AbstractParameterNode> parameters = parametersParent.getParameters();

		return parameters.size();
	}

	public boolean isRootParameter() {
		IAbstractNode parent = this;

		while (parent != null) {
			parent = parent.getParent();

			if (parent instanceof RootNode) {
				return true;
			}
		}

		return false;
	}

	public boolean isClassParameter() {
		IAbstractNode parent = this;

		while (parent != null) {
			parent = parent.getParent();

			if (parent instanceof MethodNode) {
				return false;
			}

			if (parent instanceof ClassNode) {
				return true;
			}
		}

		return false;
	}

	public boolean isMethodParameter() {
		IAbstractNode parent = this;

		while (parent != null) {
			parent = parent.getParent();

			if (parent instanceof MethodNode) {
				return true;
			}
		}

		return false;
	}

	public boolean isGlobalParameter() {

		RootNode rootNode = RootNodeHelper.findRootNode(this);

		if (rootNode == null) {
			return false;
		}

		MethodNode methodNode = MethodNodeHelper.findMethodNode(this);

		if (methodNode != null) {
			return false;
		}

		return true;
	}

	public IParametersParentNode getParametersParent() {

		IAbstractNode parent = getParent();

		if (parent instanceof IParametersParentNode) {
			return (IParametersParentNode) parent;
		}

		return null;
	}

	public AbstractParameterNode getLinkDestination() {

		if (isLinked() && (getLinkToGlobalParameter() != null)) {
			return getLinkToGlobalParameter().getLinkDestination();
		}

		return this;
	}

}
