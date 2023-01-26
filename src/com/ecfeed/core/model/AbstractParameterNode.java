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

import java.util.LinkedList;
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

		return getLinkToGlobalParameter() != null;
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

	public boolean isGlobalParameter() {

		IAbstractNode parent = getParent();

		if (parent instanceof MethodNode) {
			return false;
		}

		if (parent instanceof CompositeParameterNode) {
			return false;
		}

		return true;
	}

	public String getQualifiedName() { // TODO MO-RE remove
		LinkedList<String> segments = new LinkedList<>();
		IAbstractNode parent = this;
			
		do {
			segments.addFirst(parent.getName());
			parent = parent.getParent();
		} while (parent != null && !(parent instanceof RootNode));
			
		return String.join(":", segments);
	}

	public IParametersParentNode getParametersParent() {

		IAbstractNode parent = getParent();

		if (parent instanceof IParametersParentNode) {
			return (IParametersParentNode) parent;
		}

		return null;
	}	

}
