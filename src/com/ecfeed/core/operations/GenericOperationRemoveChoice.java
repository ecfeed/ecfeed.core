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

import java.util.Set;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.IBasicParameterVisitor;
import com.ecfeed.core.model.IChoicesParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.operations.nodes.OnMethodOperationRemoveInconsistentChildren;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.LogHelperCore;

public class GenericOperationRemoveChoice extends CompositeOperation {

	private class RemoveChoiceOperation extends AbstractModelOperation {

		private IChoicesParentNode fTarget;
		private ChoiceNode fChoice;
		private String fOriginalDefaultValue;
		private int fOriginalIndex;

		private class ReverseOperation extends AbstractModelOperation{
			
			@Override
			public String toString() {
				return "reverse Remove choice " + fChoice.getName();
			}

			private class ReverseParameterAdapter implements IBasicParameterVisitor{

				@Override
				public Object visit(BasicParameterNode node) throws Exception {

					if (node.isGlobalParameter()) {
						return null;
					} else {
						node.setDefaultValueString(fOriginalDefaultValue);
						return null;
					}
				}

			}

			public ReverseOperation(IExtLanguageManager extLanguageManager) {
				super(RemoveChoiceOperation.this.getName(), extLanguageManager);
			}

			@Override
			public void execute() {

				setOneNodeToSelect(fTarget);
				fTarget.addChoice(fChoice, fOriginalIndex);
				reverseAdaptParameter();
				markModelUpdated();
			}

			@Override
			public IModelOperation getReverseOperation() {
				return new RemoveChoiceOperation(fTarget, fChoice, getExtLanguageManager());
			}

			private void reverseAdaptParameter() {
				try{
					fTarget.getParameter().accept(new ReverseParameterAdapter());
				}catch(Exception e){
					LogHelperCore.logCatch(e);}
			}

		}

		private class OperationValidator implements IBasicParameterVisitor{

			@Override
			public Object visit(BasicParameterNode parameter) throws Exception {

				if (parameter.isGlobalParameter()) {
					return null;
				} else {

					if(parameter.isExpected() && JavaLanguageHelper.isJavaType(parameter.getType()) == false && parameter.getChoices().size() == 1 && parameter.getChoices().get(0) == fChoice){
						// We are removing the only choice of expected parameter.
						// The last parameter must represent the default expected value
						ExceptionHelper.reportRuntimeException(OperationMessages.EXPECTED_USER_TYPE_CATEGORY_LAST_PARTITION_PROBLEM);
					}
					return null;
				}
			}

		}

		private class ParameterAdapter implements IBasicParameterVisitor{

			@Override
			public Object visit(BasicParameterNode parameter) throws Exception {

				if (parameter.isGlobalParameter()) {
					return null;
				} else {

					fOriginalDefaultValue = parameter.getDefaultValue();
					if(parameter.isExpected() && fChoice.getValueString().equals(parameter.getDefaultValue())){
						// the value of removed choice is the same as default expected value
						// Check if there are leaf choices with the same value. If not, update the default value
						Set<String> leafValues = parameter.getLeafChoiceValues();
						if(leafValues.contains(parameter.getDefaultValue()) == false){
							if(leafValues.size() > 0){
								parameter.setDefaultValueString(leafValues.toArray(new String[]{})[0]);
							}
							else{
								ExceptionHelper.reportRuntimeException(OperationMessages.UNEXPECTED_PROBLEM_WHILE_REMOVING_ELEMENT);
							}
						}
					}
					return null;
				}
			}

		}

		public RemoveChoiceOperation(
				IChoicesParentNode target, 
				ChoiceNode choice, 
				IExtLanguageManager extLanguageManager){
			
			super(OperationNames.REMOVE_PARTITION, extLanguageManager);
			fTarget = target;
			fChoice = choice;
			fOriginalIndex = fChoice.getMyIndex();
		}

		@Override
		public String toString() {
			return "Remove choice " + fChoice.getName();
		}
		
		@Override
		public void execute() {

			setOneNodeToSelect(fTarget);
			fOriginalIndex = fChoice.getMyIndex();
			validateOperation();
			fTarget.removeChoice(fChoice);
			adaptParameter();
			if(fChoice.getParent() instanceof ChoiceNode){
				adaptParentChoice((ChoiceNode)fChoice.getParent());
			}
		}

		private void adaptParentChoice(ChoiceNode parentChoiceNode) {
			if(parentChoiceNode.isAbstract() == false){
				ITypeAdapter<?> adapter = JavaLanguageHelper.getAdapter(parentChoiceNode.getParameter().getType());
				String newValue = 
						adapter.adapt(
								parentChoiceNode.getValueString(), 
								parentChoiceNode.isRandomizedValue(),
								ERunMode.QUIET,
								getExtLanguageManager());

				if(newValue == null){
					newValue = adapter.getDefaultValue();
				}
				parentChoiceNode.setValueString(newValue);
			}
		}

		private void adaptParameter() {
			try{
				fTarget.getParameter().accept(new ParameterAdapter());
			}catch(Exception e){LogHelperCore.logCatch(e);}
		}

		private void validateOperation() {
			try{
				if(fTarget.getParameter() != null){
					fTarget.getParameter().accept(new OperationValidator());
				}
			}catch(Exception e){
				ExceptionHelper.reportRuntimeException(e);
			}
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ReverseOperation(getExtLanguageManager());
		}

	}

	public GenericOperationRemoveChoice(
			IChoicesParentNode target, 
			ChoiceNode choice, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.REMOVE_PARTITION, true, target, target, extLanguageManager);

		addOperation(new RemoveChoiceOperation(target, choice, extLanguageManager));

		if (validate) {
			
			for (MethodNode method : target.getParameter().getMethods()) {
				addOperation(new OnMethodOperationRemoveInconsistentChildren(method, extLanguageManager));
			}
		}
	}
}
