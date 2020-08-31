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

import java.util.List;

import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.ExtLanguage;

public class MethodParameterOperationSetLink extends BulkOperation {

	private class SetLinkOperation extends AbstractModelOperation{
		private MethodParameterNode fTarget;
		private GlobalParameterNode fNewLink;
		private GlobalParameterNode fCurrentLink;

		private class ReverseOperation extends AbstractReverseOperation{

			public ReverseOperation(ExtLanguage viewMode) {
				super(MethodParameterOperationSetLink.this, viewMode);
			}

			@Override
			public void execute() throws ModelOperationException {
				setOneNodeToSelect(fTarget);
				fTarget.setLink(fCurrentLink);
			}

			@Override
			public IModelOperation getReverseOperation() {
				return new SetLinkOperation(fTarget, fNewLink, getViewMode());
			}

		}

		public SetLinkOperation(MethodParameterNode target, GlobalParameterNode link, ExtLanguage viewMode) {
			super(OperationNames.SET_LINK, viewMode);
			fTarget = target;
			fNewLink = link;
		}

		@Override
		public void execute() throws ModelOperationException {

			setOneNodeToSelect(fTarget);
			
			// TODO SIMPLE-VIEW  check - use method from helper instead of getMethod
			MethodNode method = fTarget.getMethod();
			List<String> types = method.getParameterTypes();
			types.set(fTarget.getMyIndex(), fNewLink.getType());

			if(method.checkDuplicate(fTarget.getMyIndex(), fNewLink.getType())){
				
				ModelOperationException.report(
						ClassNodeHelper.generateMethodSignatureDuplicateMessage(
								method.getClassNode(), method, getViewMode()));
			}

			fCurrentLink = fTarget.getLink();
			fTarget.setLink(fNewLink);
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ReverseOperation(getViewMode());
		}

	}

	public MethodParameterOperationSetLink(MethodParameterNode target, GlobalParameterNode link, ExtLanguage viewMode) {
		super(OperationNames.SET_LINK, true, target, target, viewMode);
		addOperation(new SetLinkOperation(target, link, viewMode));
		addOperation(new MethodOperationMakeConsistent(target.getMethod(), viewMode));
	}
}
