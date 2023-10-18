/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.model.AbstractNodeHelper;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.ConstraintHelper;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IChoicesParentNode;
import com.ecfeed.core.model.IChoicesParentVisitor;
import com.ecfeed.core.model.IConstraintsParentNode;
import com.ecfeed.core.model.IParametersAndConstraintsParentNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.ITestCasesParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ParametersParentNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.IParameterConversionItemPart;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.LogHelperCore;
import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.SimpleLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class OnBasicParameterOperationSetType extends OnAbstractParameterOperationSetType {

	private String fOriginalDefaultValue;
	private Map<Integer, String> fOriginalConstraintValues;
	private ArrayList<TestCaseNode> fOriginalTestCases;
	private ArrayList<ConstraintNode> fOriginalConstraints;
	private ParameterConversionDefinition fParameterConversionDefinition;

	private BasicParameterNode fMethodParameterNode;
	private String fNewTypeInIntrLanguage;
	private IExtLanguageManager fExtLanguageManager;

	public OnBasicParameterOperationSetType(
			BasicParameterNode target, 
			String newTypeInIntrLanguage, 
			ParameterConversionDefinition parameterConversionDefinition,
			IExtLanguageManager extLanguageManager) {

		super(target, newTypeInIntrLanguage, parameterConversionDefinition, extLanguageManager);

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
		IConstraintsParentNode parent = (IConstraintsParentNode) fMethodParameterNode.getParent();
		fOriginalConstraintValues = ConstraintHelper.getOriginalConstraintValues(parent);
	}

	@Override
	public void execute() {

		IAbstractNode parent = fMethodParameterNode.getParent();

		IParametersAndConstraintsParentNode parametersAndConstraintsParentNode = 
				(IParametersAndConstraintsParentNode) parent;

		checkForDuplicateSignatureInExtLanguage(parametersAndConstraintsParentNode);

		super.execute();

		fOriginalTestCases = getTestCases(parent);
		List<ConstraintNode> constraintNodes = parametersAndConstraintsParentNode.getConstraintNodes();
		fOriginalConstraints = new ArrayList<>(constraintNodes);

		convertDefaultValue(
				fMethodParameterNode, 
				fNewTypeInIntrLanguage, 
				fParameterConversionDefinition, 
				getExtLanguageManager());

		BasicParameterNodeHelper.convertChoicesAndConstraintsToType(
				fMethodParameterNode, fParameterConversionDefinition);		

		markModelUpdated();
	}

	private ArrayList<TestCaseNode> getTestCases(IAbstractNode parent) {

		if (parent instanceof ITestCasesParentNode) {

			ITestCasesParentNode testCasesParentNode = (ITestCasesParentNode) parent;

			return new ArrayList<>(testCasesParentNode.getTestCases());
		}

		return new ArrayList<>();

	}

	private void checkForDuplicateSignatureInExtLanguage(IParametersParentNode parametersParentNode) {

		IExtLanguageManager extLanguageManager = getExtLanguageManager();

		List<String> parameterTypesInExtLanguage = 
				ParametersParentNodeHelper.getParameterTypes(parametersParentNode, extLanguageManager);

		String newParameterTypeInIntrLanguage = getNewType();
		String newParameterTypeInExtLanguage = extLanguageManager.convertTypeFromIntrToExtLanguage(newParameterTypeInIntrLanguage);

		parameterTypesInExtLanguage.set(fMethodParameterNode.getMyIndex(), newParameterTypeInExtLanguage);

		if (parametersParentNode instanceof MethodNode) {

			MethodNode methodNode = (MethodNode) parametersParentNode;

			ClassNode classNode = methodNode.getClassNode();

			String methodNameInExtLanguage = AbstractNodeHelper.getName(methodNode, fExtLanguageManager);

			MethodNode foundMethodNode = 
					ClassNodeHelper.findMethodByName(
							classNode, methodNameInExtLanguage, fExtLanguageManager);

			if (foundMethodNode == null) {
				return;
			}

			if (foundMethodNode == parametersParentNode) {
				return;
			}

			String message = 
					ClassNodeHelper.createMethodNameDuplicateMessage(
							classNode, foundMethodNode, false, extLanguageManager);

			ExceptionHelper.reportRuntimeException(message);
		}
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseSetTypeOperation(getExtLanguageManager());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<ChoiceNode> getChoices(IChoicesParentNode parent) {
		try {
			return (List<ChoiceNode>)parent.accept(new RealChoicesProvider());
		} catch(Exception e) {
			LogHelperCore.logCatch(e);}
		return null;
	}

	private void convertDefaultValue(
			BasicParameterNode methodParameterNode,
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
			BasicParameterNode methodParameterNode,
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

	private void setAdaptedValueAsDefault(
			BasicParameterNode methodParameterNode, 
			String newType,
			IExtLanguageManager extLanguageManager, 
			String currentDefaultValue) {

		ITypeAdapter<?> adapter = JavaLanguageHelper.getTypeAdapter(newType);

		String newDefaultValue = 
				adapter.adapt(currentDefaultValue, false, ERunMode.QUIET, extLanguageManager);

		methodParameterNode.setDefaultValueString(newDefaultValue);
	}

	private class RealChoicesProvider implements IChoicesParentVisitor{

		@Override
		public Object visit(BasicParameterNode node) throws Exception {
			return node.getRealChoices();
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return node.getChoices();
		}

	}

	private class ReverseSetTypeOperation extends OnAbstractParameterOperationSetType.ReverseOperation{

		public ReverseSetTypeOperation(IExtLanguageManager extLanguageManager) {

			super(extLanguageManager);
		}

		@Override
		public void execute() {

			super.execute();

			IAbstractNode parent = fMethodParameterNode.getParent();

			IParametersAndConstraintsParentNode parametersAndConstraintsParentNode 
			= (IParametersAndConstraintsParentNode) parent;

			if (parent instanceof ITestCasesParentNode) {

				ITestCasesParentNode testCasesParentNode = (ITestCasesParentNode) parent;
				testCasesParentNode.setTestCases(fOriginalTestCases);
			}
			parametersAndConstraintsParentNode.replaceConstraints(fOriginalConstraints);

			fMethodParameterNode.setDefaultValueString(fOriginalDefaultValue);

			ConstraintHelper.restoreOriginalConstraintValues(
					parametersAndConstraintsParentNode, fOriginalConstraintValues);

			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {

			return new OnBasicParameterOperationSetType(
					fMethodParameterNode, 
					getNewType(), 
					fParameterConversionDefinition, 
					getExtLanguageManager());
		}

	}

}
