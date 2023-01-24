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
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.IChoicesParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.ParameterConversionDefinition;

public class OnAbstractParameterOperationSetType extends AbstractModelOperation {

	private BasicParameterNode fAbstractParameterNode;
	private ParameterConversionDefinition fParameterConversionDefinition;
	private String fNewTypeInIntrLanguage;
	private String fCurrentType;
	private ITypeAdapterProvider fAdapterProvider;
	private Map<IChoicesParentNode, List<ChoiceNode>> fOriginalChoices;
	private Map<ChoiceNode, String> fOriginalValues;

	public OnAbstractParameterOperationSetType(
			BasicParameterNode abstractParameterNode, 
			String newTypeInIntrLanguage, 
			ParameterConversionDefinition parameterConversionDefinition,
			ITypeAdapterProvider adapterProvider, 
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.SET_TYPE, extLanguageManager);

		if (adapterProvider == null) {
			ExceptionHelper.reportRuntimeException("Type adapter is empty.");
		}
		
		if (newTypeInIntrLanguage == null) {
			ExceptionHelper.reportRuntimeException("New type is empty.");
		}

		fAbstractParameterNode = abstractParameterNode;
		fNewTypeInIntrLanguage = newTypeInIntrLanguage;
		fParameterConversionDefinition = parameterConversionDefinition;
		fAdapterProvider = adapterProvider;
		fOriginalChoices = new HashMap<>();
		fOriginalValues = new HashMap<>();
	}

	public static final String METHOD_GLOBAL_PARAMETER_SIGNATURE_DUPLICATE_PROBLEM(String className, String methodName, String types1, String types2){
		return "This action would result in duplicate methods in class:\n" + className + "\nmethods:\n" + methodName + types1 + "\n" + methodName + types2; 
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fAbstractParameterNode);

		fCurrentType = fAbstractParameterNode.getType();
		getOriginalChoices().clear();
		getOriginalValues().clear();

		saveChoices(fAbstractParameterNode);
		saveValues(fAbstractParameterNode);

		// Check for duplicate signatures possibly caused by global parameter type change
		if (fAbstractParameterNode instanceof BasicParameterNode && fAbstractParameterNode.isGlobalParameter()) {
			checkForSignatureDuplicates();
		}

		fAbstractParameterNode.setType(fNewTypeInIntrLanguage);
	}

	private void checkForSignatureDuplicates() {

		BasicParameterNode target = (BasicParameterNode)fAbstractParameterNode;
		List<MethodNode> linkingMethods = new ArrayList<MethodNode>(target.getMethods());
		MethodNode testedMethod;

		// Iterate through methods. Methods of same class and name are matched just once and then removed from iteration.
		for(int i = 0; i < linkingMethods.size();){
			testedMethod = linkingMethods.get(i);
			checkOneMethodForSignatureDuplicates(target, testedMethod, linkingMethods);
		}
	}

	private void checkOneMethodForSignatureDuplicates(
			BasicParameterNode target, 
			MethodNode testedMethod,
			List<MethodNode> inOutLinkingMethods) {

		ClassNode classNode = testedMethod.getClassNode();
		// Map of methods and their parameter lists
		HashMap<MethodNode, List<String>> methods = new HashMap<>();

		//searching for methods with same name as currently investigated
		for(MethodNode methodNode: classNode.getMethods()){
			prepareCollectionsOfMethods(target, methodNode, testedMethod, inOutLinkingMethods, methods);
		}

		if(methods.size() < 2) {
			return;
		}

		checkMethodSignaturesForDuplicates(methods);
	}

	private void checkMethodSignaturesForDuplicates(HashMap<MethodNode, List<String>> methods) {

		ArrayList<MethodNode> remainingMethods = new ArrayList<MethodNode>(methods.keySet());
		MethodNode method;

		// match with not yet iterated through methods till duplicate found or there is just 1 method left
		for(int n = 0; n < remainingMethods.size() -1; n++){
			method = remainingMethods.get(n);
			for(int k = n+1; k < remainingMethods.size(); k++){
				if(methods.get(method).equals(methods.get(remainingMethods.get(k)))){
					ExceptionHelper.reportRuntimeException(METHOD_GLOBAL_PARAMETER_SIGNATURE_DUPLICATE_PROBLEM(
							method.getClassNode().getName(), method.getName(), method.getParameters().toString(),
							remainingMethods.get(k).getParameters().toString()));
				}
			}
		}
	}

	private void prepareCollectionsOfMethods(
			BasicParameterNode target, 
			MethodNode methodNode, 
			MethodNode testedMethod,
			List<MethodNode> inOutLinkingMethods, 
			HashMap<MethodNode,	List<String>> inOutMethods) {

		if(methodNode.getName().equals(testedMethod.getName())
				&& methodNode.getParameters().size() == testedMethod.getParameters().size()){
			// if method links edited global parameter - replace types before matching
			
			if(target.getMethods().contains(methodNode)){
				
				List<String> types = methodNode.getParameterTypes();
				
				for(AbstractParameterNode parameter: methodNode.getParameters()){
					BasicParameterNode param = (BasicParameterNode)parameter;
					if(param.isLinked() && param.getLinkToGlobalParameter().equals(target)){
						types.set(parameter.getMyIndex(), fNewTypeInIntrLanguage);
					}			
				}
				inOutMethods.put(methodNode, types);
			}
			// else add parameter list without alterations
			else {
				inOutMethods.put(methodNode, methodNode.getParameterTypes());
			}		
			// remove from linking parameter list, so no methods are matched twice
			inOutLinkingMethods.remove(methodNode);	
		}
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	protected void saveChoices(IChoicesParentNode parent){
		getOriginalChoices().put(parent, new ArrayList<ChoiceNode>(getChoices(parent)));
		for(ChoiceNode child : getChoices(parent)){
			saveChoices(child);
		}
	}

	protected void saveValues(IChoicesParentNode parent) {
		for(ChoiceNode choice : getChoices(parent)){
			getOriginalValues().put(choice, choice.getValueString());
			saveValues(choice);
		}
	}

	private void adaptChoices(IChoicesParentNode parent) {
		Iterator<ChoiceNode> it = getChoices(parent).iterator();
		ITypeAdapter<?> adapter = fAdapterProvider.getAdapter(fNewTypeInIntrLanguage);
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
				adapter.adapt(
						choice.getValueString(), choice.isRandomizedValue(), ERunMode.QUIET, getExtLanguageManager());

		if (newValue == null) {
			newValue = adapter.getDefaultValue();
		}

		choice.setValueString(newValue);
	}

	private void adaptValueChoice(ChoiceNode choice, ITypeAdapter<?> adapter, Iterator<ChoiceNode> it) {

		String newValue = 
				adapter.adapt(
						choice.getValueString(), 
						choice.isRandomizedValue(), 
						ERunMode.QUIET,
						getExtLanguageManager());

		if (newValue == null) {
			it.remove();
			return;
		}

		choice.setValueString(newValue);

		if (!adapter.isRandomizable()) {
			choice.setRandomizedValue(false);
		}
	}

	protected Map<IChoicesParentNode, List<ChoiceNode>> getOriginalChoices(){
		return fOriginalChoices;
	}

	protected Map<ChoiceNode, String> getOriginalValues(){
		return fOriginalValues;
	}

	protected List<ChoiceNode> getChoices(IChoicesParentNode parent){
		return parent.getChoices();
	}

	protected ITypeAdapterProvider getTypeAdapterProvider(){
		return fAdapterProvider;
	}

	protected String getNewType(){
		return fNewTypeInIntrLanguage;
	}

	protected class ReverseOperation extends AbstractReverseOperation {

		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(OnAbstractParameterOperationSetType.this, extLanguageManager);
		}

		@Override
		public void execute() {

			setOneNodeToSelect(fAbstractParameterNode);

			restoreOriginalChoices(fAbstractParameterNode);
			restoreOriginalValues(fAbstractParameterNode);

			fAbstractParameterNode.setType(fCurrentType);
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new OnAbstractParameterOperationSetType(
					fAbstractParameterNode, 
					fNewTypeInIntrLanguage,
					fParameterConversionDefinition,
					fAdapterProvider, 
					getExtLanguageManager());
		}

		protected void restoreOriginalChoices(IChoicesParentNode parent) {
			parent.replaceChoices(getOriginalChoices().get(parent));
			for(ChoiceNode child : getChoices(parent)){
				restoreOriginalChoices(child);
			}
		}

		protected void restoreOriginalValues(IChoicesParentNode parent) {
			for(ChoiceNode choice : getChoices(parent)){
				if(getOriginalValues().containsKey(choice)){
					choice.setValueString(getOriginalValues().get(choice));
				}
				restoreOriginalValues(choice);
			}
		}

	}

}
