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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.SimpleLanguageHelper;
import com.ecfeed.core.utils.ExtLanguage;

public class AbstractParameterOperationSetType extends AbstractModelOperation {

	private AbstractParameterNode fTarget;
	private String fNewType;
	private String fCurrentType;
	private ITypeAdapterProvider fAdapterProvider;
	private Map<ChoicesParentNode, List<ChoiceNode>> fOriginalChoices;
	private Map<ChoiceNode, String> fOriginalValues;

	protected class ReverseOperation extends AbstractReverseOperation {

		public ReverseOperation(ExtLanguage extLanguage) {
			super(AbstractParameterOperationSetType.this, extLanguage);
		}

		@Override
		public void execute() throws ModelOperationException {

			setOneNodeToSelect(fTarget);

			restoreOriginalChoices(fTarget);
			restoreOriginalValues(fTarget);

			fTarget.setType(fCurrentType);
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new AbstractParameterOperationSetType(fTarget, fNewType, fAdapterProvider, getExtLanguage());
		}

		protected void restoreOriginalChoices(ChoicesParentNode parent) {
			parent.replaceChoices(getOriginalChoices().get(parent));
			for(ChoiceNode child : getChoices(parent)){
				restoreOriginalChoices(child);
			}
		}

		protected void restoreOriginalValues(ChoicesParentNode parent) {
			for(ChoiceNode choice : getChoices(parent)){
				if(getOriginalValues().containsKey(choice)){
					choice.setValueString(getOriginalValues().get(choice));
				}
				restoreOriginalValues(choice);
			}
		}

	}

	public AbstractParameterOperationSetType(
			AbstractParameterNode target, 
			String newType, 
			ITypeAdapterProvider adapterProvider, 
			ExtLanguage extLanguage) {

		super(OperationNames.SET_TYPE, extLanguage);

		fTarget = target;
		fNewType = newType;
		fAdapterProvider = adapterProvider;
		fOriginalChoices = new HashMap<>();
		fOriginalValues = new HashMap<>();
	}

	public static final String METHOD_GLOBAL_PARAMETER_SIGNATURE_DUPLICATE_PROBLEM(String className, String methodName, String types1, String types2){
		return "This action would result in duplicate methods in class:\n" + className + "\nmethods:\n" + methodName + types1 + "\n" + methodName + types2; 
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fTarget);

		fCurrentType = fTarget.getType();
		getOriginalChoices().clear();
		getOriginalValues().clear();

		saveChoices(fTarget);
		saveValues(fTarget);

		if (!JavaLanguageHelper.isJavaType(fNewType) 
				&& !SimpleLanguageHelper.isSimpleType(fNewType)
				&& !JavaLanguageHelper.isValidComplexTypeIdentifier(fNewType)) {

			ModelOperationException.report(OperationMessages.CATEGORY_TYPE_REGEX_PROBLEM);
		}
		// Check for duplicate signatures possibly caused by global parameter type change
		if(fTarget instanceof GlobalParameterNode){
			GlobalParameterNode target = (GlobalParameterNode)fTarget;
			List<MethodNode> linkingMethods = new ArrayList<MethodNode>(target.getMethods());
			MethodNode testedMethod;
			// Iterate through methods. Methods of same class and name are matched just once and then removed from iteration.
			for(int i = 0; i < linkingMethods.size();){
				testedMethod = linkingMethods.get(i);
				ClassNode classNode = testedMethod.getClassNode();
				// Map of methods and their parameter lists
				HashMap<MethodNode, List<String>> methods = new HashMap<>();
				//searching for methods with same name as currently investigated
				for(MethodNode methodNode: classNode.getMethods()){
					if(methodNode.getName().equals(testedMethod.getName())
							&& methodNode.getParameters().size() == testedMethod.getParameters().size()){
						// if method links edited global parameter - replace types before matching
						if(target.getMethods().contains(methodNode)){
							List<String> types = methodNode.getParameterTypes();
							for(AbstractParameterNode parameter: methodNode.getParameters()){
								MethodParameterNode param = (MethodParameterNode)parameter;
								if(param.isLinked() && param.getLink().equals(target)){
									types.set(parameter.getMyIndex(), fNewType);
								}			
							}
							methods.put(methodNode, types);
						}
						// else add parameter list without alterations
						else {
							methods.put(methodNode, methodNode.getParameterTypes());
						}		
						// remove from linking parameter list, so no methods are matched twice
						linkingMethods.remove(methodNode);	
					}
				}
				// if less than 2 methods of same name found - skip matching
				if(methods.size() < 2) continue;
				// else match all found methods with each other
				else{
					ArrayList<MethodNode> remainingMethods = new ArrayList<MethodNode>(methods.keySet());
					MethodNode method;
					// match with not yet iterated through methods till duplicate found or there is just 1 method left
					for(int n = 0; n < remainingMethods.size() -1; n++){
						method = remainingMethods.get(n);
						for(int k = n+1; k < remainingMethods.size(); k++){
							if(methods.get(method).equals(methods.get(remainingMethods.get(k)))){
								ModelOperationException.report(METHOD_GLOBAL_PARAMETER_SIGNATURE_DUPLICATE_PROBLEM(
										method.getClassNode().getName(), method.getName(), method.getParameters().toString(),
										remainingMethods.get(k).getParameters().toString()));
							}
						}
					}
				}
			}
		}

		fTarget.setType(fNewType);
		adaptChoices(fTarget);
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguage());
	}

	protected void saveChoices(ChoicesParentNode parent){
		getOriginalChoices().put(parent, new ArrayList<ChoiceNode>(getChoices(parent)));
		for(ChoiceNode child : getChoices(parent)){
			saveChoices(child);
		}
	}

	protected void saveValues(ChoicesParentNode parent) {
		for(ChoiceNode choice : getChoices(parent)){
			getOriginalValues().put(choice, choice.getValueString());
			saveValues(choice);
		}
	}

	private void adaptChoices(ChoicesParentNode parent) {
		Iterator<ChoiceNode> it = getChoices(parent).iterator();
		ITypeAdapter<?> adapter = fAdapterProvider.getAdapter(fNewType);
		while(it.hasNext()){
			adaptOneChoice(it, adapter);
		}
	}

	private void adaptOneChoice(Iterator<ChoiceNode> it, ITypeAdapter<?> adapter) {

		ChoiceNode choice = it.next();

		if (choice.isAbstract()) {
			adaptAbstractChoice(choice, adapter, it);
		} else{
			adaptValueChoice(choice, adapter, it);
		}
	}

	private void adaptAbstractChoice(ChoiceNode choice, ITypeAdapter<?> adapter, Iterator<ChoiceNode> it) {

		adaptChoices(choice);

		if (getChoices(choice).isEmpty()) {
			it.remove();
			return;
		}

		String newValue = 
				adapter.convert(
						choice.getValueString(), choice.isRandomizedValue(), ERunMode.QUIET);

		if (newValue == null) {
			newValue = adapter.getDefaultValue();
		}

		choice.setValueString(newValue);
	}

	private void adaptValueChoice(ChoiceNode choice, ITypeAdapter<?> adapter, Iterator<ChoiceNode> it) {

		String newValue = 
				adapter.convert(
						choice.getValueString(), choice.isRandomizedValue(), ERunMode.QUIET);

		if (newValue == null) {
			it.remove();
			return;
		}

		choice.setValueString(newValue);

		if (!adapter.isRandomizable()) {
			choice.setRandomizedValue(false);
		}
	}

	protected Map<ChoicesParentNode, List<ChoiceNode>> getOriginalChoices(){
		return fOriginalChoices;
	}

	protected Map<ChoiceNode, String> getOriginalValues(){
		return fOriginalValues;
	}

	protected List<ChoiceNode> getChoices(ChoicesParentNode parent){
		return parent.getChoices();
	}

	protected ITypeAdapterProvider getTypeAdapterProvider(){
		return fAdapterProvider;
	}

	protected String getNewType(){
		return fNewType;
	}

}
