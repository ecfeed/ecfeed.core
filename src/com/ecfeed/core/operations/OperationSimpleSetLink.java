/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OperationSimpleSetLink extends AbstractModelOperation {

	MethodParameterNode fMethodParameterNode;
	GlobalParameterNode fNewlink;
	GlobalParameterNode fOldLink;

	public OperationSimpleSetLink(
			MethodParameterNode methodParameterNode,
			GlobalParameterNode link,
			IExtLanguageManager extLanguageManager){

		super("Set link", extLanguageManager);

		fMethodParameterNode = methodParameterNode;
		fNewlink = link;
		fOldLink = methodParameterNode.getLink();
	}

	@Override
	public void execute() {

		setLink(fNewlink);
		markModelUpdated();
	}

	private void setLink(GlobalParameterNode link) {

		if (link == null) {
			fMethodParameterNode.setLink(null);
			fMethodParameterNode.setLinked(false);
		} else {
			fMethodParameterNode.setLink(link);
			fMethodParameterNode.setLinked(true);
		}
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	private class ReverseOperation extends AbstractModelOperation {

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(OperationSimpleSetLink.this.getName() + " - reverse operation", extLanguageManager);
		}

		@Override
		public void execute() {

			setLink(fOldLink);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new OperationSimpleSetLink(fMethodParameterNode, fNewlink, getExtLanguageManager());
		}

	}

}
