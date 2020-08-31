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
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExtLanguage;

public class GenericOperationAddChoice extends BulkOperation {
	
	public GenericOperationAddChoice(
			ChoicesParentNode target, 
			ChoiceNode choice, 
			ITypeAdapterProvider adapterProvider, 
			int index, 
			boolean validate, 
			ExtLanguage extLanguage) {

		super(OperationNames.ADD_PARTITION, true, target, target, extLanguage);
		addOperation(new AddChoiceOperation(target, choice, adapterProvider, index, extLanguage));

		for (MethodNode method : target.getParameter().getMethods()) {
			if((method != null) && validate){
				addOperation(new MethodOperationMakeConsistent(method, getViewMode()));
			}
		}
	}

	public GenericOperationAddChoice(
			ChoicesParentNode target, 
			ChoiceNode choice, 
			ITypeAdapterProvider adapterProvider, 
			boolean validate,
			ExtLanguage extLanguage) {

		this(target, choice, adapterProvider, -1, validate, extLanguage);
	}

	private class AddChoiceOperation extends AbstractModelOperation {
		private ChoicesParentNode fChoicesParentNode;
		private ChoiceNode fChoice;
		private int fIndex;
		private ITypeAdapterProvider fAdapterProvider;

		public AddChoiceOperation(
				ChoicesParentNode target, ChoiceNode choice, ITypeAdapterProvider adapterProvider, int index, ExtLanguage extLanguage) {

			super(OperationNames.ADD_PARTITION, extLanguage);
			fChoicesParentNode = target;
			fChoice = choice;
			fIndex = index;
			fAdapterProvider = adapterProvider;
		}

		public final String CHOICE_NAME_DUPLICATE_PROBLEM(String parentName, String choiceName) {
			return "The choice " + choiceName + " already exists in parent " + parentName;
		}

		@Override
		public void execute() throws ModelOperationException {

			setOneNodeToSelect(fChoicesParentNode);
			generateUniqueChoiceName(fChoice);

			if(fIndex == -1) {
				fIndex = fChoicesParentNode.getChoices().size();
			}
			if(fChoicesParentNode.getChoiceNames().contains(fChoice.getName())){
				ModelOperationException.report(CHOICE_NAME_DUPLICATE_PROBLEM(fChoicesParentNode.getName(), fChoice.getName()));
			}
			if(fIndex < 0){
				ModelOperationException.report(OperationMessages.NEGATIVE_INDEX_PROBLEM);
			}
			if(fIndex > fChoicesParentNode.getChoices().size()){
				ModelOperationException.report(OperationMessages.TOO_HIGH_INDEX_PROBLEM);
			}

			validateChoiceValue(fChoice);
			fChoicesParentNode.addChoice(fChoice, fIndex);

			markModelUpdated();
		}

		private void generateUniqueChoiceName(ChoiceNode choiceNode) {

			String newName = ChoicesParentNode.generateNewChoiceName(fChoicesParentNode, choiceNode.getName());
			choiceNode.setName(newName);
		}

		@Override
		public IModelOperation getReverseOperation() {

			return 
					new GenericOperationRemoveChoice(
							fChoicesParentNode, fChoice, fAdapterProvider, false, getViewMode());
		}

		public final String PARTITION_VALUE_PROBLEM(String value){
			return "Value " + value + " is not valid for given parameter.\n\n" +
					"Choice value must fit to type and range of the represented parameter.\n" +
					"Choices of user defined type must follow Java enum defining rules.";
		}

		private void validateChoiceValue(ChoiceNode choice) throws ModelOperationException {

			if (choice.isAbstract() == false) {

				String type = fChoicesParentNode.getParameter().getType();
				ITypeAdapter<?> adapter = fAdapterProvider.getAdapter(type);
				String newValue = 
						adapter.convert(
								choice.getValueString(), choice.isRandomizedValue(), ERunMode.QUIET);

				if(newValue == null){
					ModelOperationException.report(PARTITION_VALUE_PROBLEM(choice.getValueString()));
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
