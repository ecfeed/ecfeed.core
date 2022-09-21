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
import java.util.List;
import java.util.Map;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.ConstraintHelper;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IChoicesParentVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ParameterTransformer;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.IParameterConversionItemPart;
import com.ecfeed.core.utils.LogHelperCore;
import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.SimpleLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class MethodParameterOperationSetType extends AbstractParameterOperationSetType {

	private String fOriginalDefaultValue;
	private Map<Integer, String> fOriginalConstraintValues;
	private ArrayList<TestCaseNode> fOriginalTestCases;
	private ArrayList<ConstraintNode> fOriginalConstraints;
	private ParameterConversionDefinition fParameterConversionDefinition;

	private MethodParameterNode fMethodParameterNode;
	private String fNewTypeInIntrLanguage;
	private IExtLanguageManager fExtLanguageManager;

	public MethodParameterOperationSetType(
			MethodParameterNode target, 
			String newTypeInIntrLanguage, 
			ParameterConversionDefinition parameterConversionDefinition,
			ITypeAdapterProvider adapterProvider, 
			IExtLanguageManager extLanguageManager) {

		super(target, newTypeInIntrLanguage, parameterConversionDefinition, adapterProvider, extLanguageManager);

		if (newTypeInIntrLanguage == null) {
			ExceptionHelper.reportRuntimeException("Cannot set new type to null.");
		}

		if (SimpleLanguageHelper.isSimpleType(newTypeInIntrLanguage)) {
			ExceptionHelper.reportRuntimeException("Invalid name.");
		}

		fMethodParameterNode = target;
		fNewTypeInIntrLanguage = newTypeInIntrLanguage;
		fParameterConversionDefinition = parameterConversionDefinition;
		fExtLanguageManager = extLanguageManager;

		fOriginalDefaultValue = fMethodParameterNode.getDefaultValue();
		fOriginalConstraintValues = ConstraintHelper.getOriginalConstraintValues(fMethodParameterNode.getMethod());
	}

	@Override
	public void execute() {

		MethodNode methodNode = fMethodParameterNode.getMethod();

		checkForDuplicateSignatureInExtLanguage(methodNode);

		super.execute();

		fOriginalTestCases = new ArrayList<>(methodNode.getTestCases());
		fOriginalConstraints = new ArrayList<>(methodNode.getConstraintNodes());

		convertDefaultValue(
				fMethodParameterNode, 
				fNewTypeInIntrLanguage, 
				fParameterConversionDefinition, 
				getExtLanguageManager());

		ParameterTransformer.convertChoicesAndConstraintsToType(
				fMethodParameterNode, fParameterConversionDefinition);		

		markModelUpdated();
	}

	private void checkForDuplicateSignatureInExtLanguage(MethodNode oldMethodNode) {

		IExtLanguageManager extLanguageManager = getExtLanguageManager();

		List<String> parameterTypesInExtLanguage = 
				MethodNodeHelper.getParameterTypes(oldMethodNode, extLanguageManager);

		String newParameterTypeInIntrLanguage = getNewType();
		String newParameterTypeInExtLanguage = extLanguageManager.convertTypeFromIntrToExtLanguage(newParameterTypeInIntrLanguage);

		parameterTypesInExtLanguage.set(fMethodParameterNode.getMyIndex(), newParameterTypeInExtLanguage);

		ClassNode classNode = oldMethodNode.getClassNode();

		String methodNameInExtLanguage = MethodNodeHelper.getName(oldMethodNode, fExtLanguageManager);

		MethodNode foundMethodNode = 
				ClassNodeHelper.findMethodByExtLanguage(
						classNode, methodNameInExtLanguage, parameterTypesInExtLanguage, fExtLanguageManager);

		if (foundMethodNode == null) {
			return;
		}

		if (foundMethodNode == oldMethodNode) {
			return;
		}

		String message = 
				ClassNodeHelper.createMethodSignatureDuplicateMessage(
						classNode, foundMethodNode, false, extLanguageManager);

		ExceptionHelper.reportRuntimeException(message);
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseSetTypeOperation(getExtLanguageManager());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<ChoiceNode> getChoices(ChoicesParentNode parent) {
		try {
			return (List<ChoiceNode>)parent.accept(new RealChoicesProvider());
		} catch(Exception e) {
			LogHelperCore.logCatch(e);}
		return null;
	}

	private void convertDefaultValue(
			MethodParameterNode methodParameterNode,
			String newTypeInIntrLanguage,
			ParameterConversionDefinition parameterConversionDefinition,
			IExtLanguageManager extLanguageManager) {

		String currentDefaultValue = methodParameterNode.getDefaultValue();

		if (parameterConversionDefinition == null) {
			setAdaptedValueAsDefault(
					methodParameterNode, 
					newTypeInIntrLanguage, 
					extLanguageManager, 
					currentDefaultValue);
			return;
		}

		convertDefaultValueUsingConversionDefinition(
				methodParameterNode, parameterConversionDefinition,	currentDefaultValue);
	}

	private void convertDefaultValueUsingConversionDefinition(
			MethodParameterNode methodParameterNode,
			ParameterConversionDefinition parameterConversionDefinition, 
			String currentDefaultValue) {

		int itemCount = parameterConversionDefinition.getItemCount();

		for (int index = 0; index < itemCount; index++) {
			ParameterConversionItem parameterConversionItem = parameterConversionDefinition.getCopyOfItem(index);

			IParameterConversionItemPart parameterConversionItemPart = parameterConversionItem.getSrcPart();

			String srcValue = parameterConversionItemPart.getStr();

			if (StringHelper.isEqual(srcValue, currentDefaultValue)) {

				String dstValue = parameterConversionItem.getDstPart().getStr();

				methodParameterNode.setDefaultValueString(dstValue);
				return;
			}

		}
	}

	private void setAdaptedValueAsDefault(MethodParameterNode methodParameterNode, String newType,
			IExtLanguageManager extLanguageManager, String currentDefaultValue) {
		ITypeAdapter<?> adapter = getTypeAdapterProvider().getAdapter(newType);

		String newDefaultValue = 
				adapter.adapt(currentDefaultValue, false, ERunMode.QUIET, extLanguageManager);

		methodParameterNode.setDefaultValueString(newDefaultValue);
	}

	private class RealChoicesProvider implements IChoicesParentVisitor{

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return node.getRealChoices();
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return node.getChoices();
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return node.getChoices();
		}

	}

	private class ReverseSetTypeOperation extends AbstractParameterOperationSetType.ReverseOperation{

		public ReverseSetTypeOperation(IExtLanguageManager extLanguageManager) {

			super(extLanguageManager);
		}

		@Override
		public void execute() {

			super.execute();
			fMethodParameterNode.getMethod().replaceTestCases(fOriginalTestCases);
			fMethodParameterNode.getMethod().replaceConstraints(fOriginalConstraints);
			fMethodParameterNode.setDefaultValueString(fOriginalDefaultValue);

			ConstraintHelper.restoreOriginalConstraintValues(
					fMethodParameterNode.getMethod(), fOriginalConstraintValues);

			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {

			return new MethodParameterOperationSetType(
					fMethodParameterNode, 
					getNewType(), 
					fParameterConversionDefinition, 
					getTypeAdapterProvider(), 
					getExtLanguageManager());
		}

	}

}
