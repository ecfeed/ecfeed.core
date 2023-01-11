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

import com.ecfeed.core.model.*;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.AbstractReverseOperation;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.MethodOperationMakeConsistent;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class MethodParameterOperationSetLink extends CompositeOperation {

	private class SetLinkOperation extends AbstractModelOperation{
		private AbstractParameterNode fTarget;
		private AbstractParameterNode fNewLink;
		private AbstractParameterNode fCurrentLink;

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

		public SetLinkOperation(AbstractParameterNode target, AbstractParameterNode link, IExtLanguageManager extLanguageManager) {
			super(OperationNames.SET_LINK, extLanguageManager);
			fTarget = target;
			fNewLink = link;
		}

		@Override
		public void execute() {

			setOneNodeToSelect(fTarget);

			if (fTarget instanceof BasicParameterNode) {
				BasicParameterNode newLinkParsed = (BasicParameterNode) fTarget;

				MethodNode method = (MethodNode) fTarget.getParent();
				List<String> types = method.getParameterTypes();
				types.set(fTarget.getMyIndex(), newLinkParsed.getType());

				if(method.checkDuplicate(fTarget.getMyIndex(), newLinkParsed.getType())){

					ExceptionHelper.reportRuntimeException(
							ClassNodeHelper.createMethodSignatureDuplicateMessage(
									method.getClassNode(), method, false, getExtLanguageManager()));
				}
			}

			fCurrentLink = fTarget.getLinkToGlobalParameter();
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
		addOperation(new MethodOperationMakeConsistent(methodNode, extLanguageManager));
	}
}
