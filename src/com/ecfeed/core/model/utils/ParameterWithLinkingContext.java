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
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.utils.ObjectHelper;

public class ParameterWithLinkingContext {

	private AbstractParameterNode fAbstractParameterNode;
	private CompositeParameterNode fLinkingCompositeParameterNode;

	ParameterWithLinkingContext(
			AbstractParameterNode abstractParameterNode, 
			CompositeParameterNode linkingCompositeParameterNode) {

		fAbstractParameterNode = abstractParameterNode;
		fLinkingCompositeParameterNode = linkingCompositeParameterNode;
	}

	AbstractParameterNode getParameter() {
		return fAbstractParameterNode;
	}

	CompositeParameterNode getLinkingParameter() {
		return fLinkingCompositeParameterNode;
	}

	@Override
	public String toString() {

		return 
				"Par:" + fAbstractParameterNode.getName() 
				+ " LinkingComposite: " + getLinkingCompositeName(fLinkingCompositeParameterNode); 
	}

	public boolean isMatch(ParameterWithLinkingContext other) {

		AbstractParameterNode thisParameter = this.getParameter();
		AbstractParameterNode otherParameter = other.getParameter();

		//		int thisHash = thisParameter.hashCode();
		//		int otherHash = otherParameter.hashCode();

		if (!ObjectHelper.isEqual(thisParameter, otherParameter)) {
			return false;
		}

		if (!ObjectHelper.isEqual(this.getLinkingParameter(), other.getLinkingParameter())) {
			return false;
		}

		return true;
	}

	private String getLinkingCompositeName(CompositeParameterNode linkingCompositeParameterNode) {

		if (linkingCompositeParameterNode == null) {
			return "Null";
		}

		return linkingCompositeParameterNode.getName();
	}

}
