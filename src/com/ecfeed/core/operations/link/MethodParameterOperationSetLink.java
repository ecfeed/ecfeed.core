/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.link;

import java.util.List;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.AbstractReverseOperation;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.operations.nodes.OnMethodOperationRemoveInconsistentChildren;
import com.ecfeed.core.utils.IExtLanguageManager;

public class MethodParameterOperationSetLink extends CompositeOperation {

	private class SetLinkOperation extends AbstractModelOperation{
		private BasicParameterNode fTarget;
		private BasicParameterNode fNewLink;
		private BasicParameterNode fCurrentLink;

		private class ReverseOperation extends AbstractReverseOperation{

			public ReverseOperation(IExtLanguageManager extLanguageManager) {
				super(MethodParameterOperationSetLink.this, extLanguageManager);
			}

			@Override
			public void execute() {
				setOneNodeToSelect(fTarget);
				fTarget.setLinkToGlobalParameter(fCurrentLink);
			}

			@Override
			public IModelOperation getReverseOperation() {
				return new SetLinkOperation(fTarget, fNewLink, getExtLanguageManager());
			}

		}

		public SetLinkOperation(BasicParameterNode target, BasicParameterNode link, IExtLanguageManager extLanguageManager) {
			super(OperationNames.SET_LINK, extLanguageManager);
			fTarget = target;
			fNewLink = link;
		}

		@Override
		public void execute() {

			setOneNodeToSelect(fTarget);

			MethodNode method = (MethodNode) fTarget.getParent();
			List<String> types = method.getParameterTypes();
			types.set(fTarget.getMyIndex(), fNewLink.getType());

			fCurrentLink = (BasicParameterNode) fTarget.getLinkToGlobalParameter();
			fTarget.setLinkToGlobalParameter(fNewLink);
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ReverseOperation(getExtLanguageManager());
		}

	}

	public MethodParameterOperationSetLink(BasicParameterNode target, BasicParameterNode link, IExtLanguageManager extLanguageManager) {
		super(OperationNames.SET_LINK, true, target, target, extLanguageManager);
		addOperation(new SetLinkOperation(target, link, extLanguageManager));
		MethodNode methodNode = (MethodNode) target.getParent();
		addOperation(new OnMethodOperationRemoveInconsistentChildren(methodNode, extLanguageManager));
	}
}
