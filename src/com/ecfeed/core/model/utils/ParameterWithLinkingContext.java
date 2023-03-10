/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.utils;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ObjectHelper;

public class ParameterWithLinkingContext {

	private AbstractParameterNode fAbstractParameterNode;
	private AbstractParameterNode fLinkingContext;

	public ParameterWithLinkingContext(
			AbstractParameterNode abstractParameterNode, 
			AbstractParameterNode linkingContext) {

		fAbstractParameterNode = abstractParameterNode;
		fLinkingContext = linkingContext;
	}

	public ParameterWithLinkingContext(ParameterWithLinkingContext otherParameterWithLinkingContext) {

		fAbstractParameterNode = otherParameterWithLinkingContext.getParameter();
		fLinkingContext = otherParameterWithLinkingContext.getLinkingContext();
	}

	@Override
	public String toString() {

		return 
				"Pararam:" + fAbstractParameterNode.getName() 
				+ " LinkContext: " + getParameterName(fLinkingContext); 
	}

	public AbstractParameterNode getParameter() {
		return fAbstractParameterNode;
	}

	public BasicParameterNode getParameterAsBasic() {

		if (!(fAbstractParameterNode instanceof BasicParameterNode)) {
			ExceptionHelper.reportRuntimeException("Cannot get basic parameter. Invalid parameter type.");

		}

		return (BasicParameterNode)fAbstractParameterNode;
	}

	public AbstractParameterNode getLinkingContext() {
		return fLinkingContext;
	}

	public CompositeParameterNode getLinkingContextAsCompositeParameter() {
		return (CompositeParameterNode) fLinkingContext;
	}

	public boolean isMatch(ParameterWithLinkingContext other) {

		AbstractParameterNode thisParameter = this.getParameter();
		AbstractParameterNode otherParameter = other.getParameter();

		if (!ObjectHelper.isEqual(thisParameter, otherParameter)) {
			return false;
		}

		if (!ObjectHelper.isEqual(this.getLinkingContext(), other.getLinkingContext())) {
			return false;
		}

		return true;
	}

	private String getParameterName(AbstractParameterNode linkingContext) {

		if (linkingContext == null) {
			return "Null";
		}

		return linkingContext.getName();
	}

}
