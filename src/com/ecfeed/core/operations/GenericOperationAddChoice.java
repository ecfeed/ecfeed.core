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

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoiceNodeHelper;
import com.ecfeed.core.model.IChoicesParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.operations.nodes.OnMethodOperationRemoveInconsistentChildren;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class GenericOperationAddChoice extends CompositeOperation {
	
	public GenericOperationAddChoice(
			IChoicesParentNode target, 
			ChoiceNode choice, 
			int index, 
			boolean validate, 
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.ADD_PARTITION, true, target, target, extLanguageManager);
		addOperation(new AddChoiceOperation(target, choice, index, extLanguageManager));

		for (MethodNode method : target.getParameter().getMethods()) {
			if((method != null) && validate){
				addOperation(new OnMethodOperationRemoveInconsistentChildren(method, getExtLanguageManager()));
			}
		}
	}

	public GenericOperationAddChoice(
			IChoicesParentNode target, 
			ChoiceNode choice, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		this(target, choice, -1, validate, extLanguageManager);
	}

	private class AddChoiceOperation extends AbstractModelOperation {
		private IChoicesParentNode fChoicesParentNode;
		private ChoiceNode fChoice;
		private int fIndex;

		public AddChoiceOperation(
				IChoicesParentNode target, ChoiceNode choice, int index, IExtLanguageManager extLanguageManager) {

			super(OperationNames.ADD_PARTITION, extLanguageManager);
			fChoicesParentNode = target;
			fChoice = choice;
			fIndex = index;
		}

		public final String CHOICE_NAME_DUPLICATE_PROBLEM(String parentName, String choiceName) {
			return "The choice " + choiceName + " already exists in parent " + parentName;
		}

		@Override
		public void execute() {

			setOneNodeToSelect(fChoicesParentNode);
			generateUniqueChoiceName(fChoice);

			if(fIndex == -1) {
				fIndex = fChoicesParentNode.getChoices().size();
			}
			if(fChoicesParentNode.getChoiceNames().contains(fChoice.getName())){
				ExceptionHelper.reportRuntimeException(CHOICE_NAME_DUPLICATE_PROBLEM(fChoicesParentNode.getName(), fChoice.getName()));
			}
			if(fIndex < 0){
				ExceptionHelper.reportRuntimeException(OperationMessages.NEGATIVE_INDEX_PROBLEM);
			}
			if(fIndex > fChoicesParentNode.getChoices().size()){
				ExceptionHelper.reportRuntimeException(OperationMessages.TOO_HIGH_INDEX_PROBLEM);
			}

			validateChoiceValue(fChoice);
			fChoicesParentNode.addChoice(fChoice, fIndex);

			markModelUpdated();
		}

		private void generateUniqueChoiceName(ChoiceNode choiceNode) {

			String newName = ChoiceNodeHelper.generateNewChoiceName(fChoicesParentNode, choiceNode.getName());
			choiceNode.setName(newName);
		}

		@Override
		public IModelOperation getReverseOperation() {

			return 
					new GenericOperationRemoveChoice(
							fChoicesParentNode, fChoice, false, getExtLanguageManager());
		}

		public final String PARTITION_VALUE_PROBLEM(String value){
			return "Value " + value + " is not valid for given parameter.\n\n" +
					"Choice value must fit to type and range of the represented parameter.\n" +
					"Choices of user defined type must follow Java enum defining rules.";
		}

		private void validateChoiceValue(ChoiceNode choice) {

			if (choice.isAbstract() == false) {

				String type = fChoicesParentNode.getParameter().getType();
				ITypeAdapter<?> adapter = JavaLanguageHelper.getAdapter(type);
				String newValue = 
						adapter.adapt(
								choice.getValueString(), 
								choice.isRandomizedValue(), 
								ERunMode.QUIET,
								getExtLanguageManager());

				if(newValue == null){
					ExceptionHelper.reportRuntimeException(PARTITION_VALUE_PROBLEM(choice.getValueString()));
				}
			}
			else {
				for(ChoiceNode child : choice.getChoices()) {
					validateChoiceValue(child);
				}
			}
		}

	}

}
