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
import com.ecfeed.core.utils.StringHelper;

public class MethodParameterOperationSetType extends BulkOperation { // TODO DE-NO remove bulk operation

	private IExtLanguageManager fExtLanguageManager;

	public MethodParameterOperationSetType(
			MethodParameterNode targetMethodParameterNode, 
			String newType, 
			ParameterConversionDefinition parameterConversionDefinition,
			IExtLanguageManager extLanguageManager,
			ITypeAdapterProvider adapterProvider) {

		super(OperationNames.SET_TYPE, true, targetMethodParameterNode, targetMethodParameterNode, extLanguageManager);

		fExtLanguageManager = extLanguageManager;

		if (newType == null) {
			ExceptionHelper.reportRuntimeException("Cannot set new type to null.");
		}

		SetTypeOperation setTypeOperation = 
				new SetTypeOperation(
						targetMethodParameterNode, 
						newType, 
						parameterConversionDefinition, 
						adapterProvider, 
						getExtLanguageManager());

		addOperation(setTypeOperation);
	}

	private class SetTypeOperation extends AbstractParameterOperationSetType{

		private String fOriginalDefaultValue;
		//		private Map<AbstractStatement, String> fOriginalStatementValues;
		private Map<Integer, String> fOriginalConstraintValues;
		private ArrayList<TestCaseNode> fOriginalTestCases;
		private ArrayList<ConstraintNode> fOriginalConstraints;
		private ParameterConversionDefinition fParameterConversionDefinition;

		private MethodParameterNode fMethodParameterNode;
		private String fNewType;

		public SetTypeOperation(
				MethodParameterNode target, 
				String newType, 
				ParameterConversionDefinition parameterConversionDefinition,
				ITypeAdapterProvider adapterProvider, 
				IExtLanguageManager extLanguageManager) {

			super(target, newType, parameterConversionDefinition, adapterProvider, extLanguageManager);

			fMethodParameterNode = target;
			fNewType = newType;
			fParameterConversionDefinition = parameterConversionDefinition;

			fOriginalDefaultValue = fMethodParameterNode.getDefaultValue();
			fOriginalConstraintValues = ConstraintHelper.getOriginalConstraintValues(fMethodParameterNode.getMethod());
		}

		@Override
		public void execute() {

			MethodNode methodNode = fMethodParameterNode.getMethod();

			checkForDuplicateSignature(methodNode);

			super.execute();

			fOriginalTestCases = new ArrayList<>(methodNode.getTestCases());
			fOriginalConstraints = new ArrayList<>(methodNode.getConstraintNodes());

			convertDefaultValue(fMethodParameterNode, fNewType, fParameterConversionDefinition, getExtLanguageManager());

			//			if (fMethodParameterNode.isExpected()) {
			//
			//				// TODO DE-NO - convert instead of adapting
			//				adaptTestCases();
			//				adaptConstraints();
			//			}

			ParameterTransformer.convertChoicesAndConstraintsToType(
					fMethodParameterNode, getNewType(), fParameterConversionDefinition);		

			markModelUpdated();
		}

		private void checkForDuplicateSignature(MethodNode oldMethodNode) {

			List<String> parameterTypesInExtLanguage = 
					MethodNodeHelper.getParameterTypes(oldMethodNode, getExtLanguageManager());

			String newParameterType = getNewType();

			parameterTypesInExtLanguage.set(fMethodParameterNode.getMyIndex(), newParameterType);

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
							classNode, foundMethodNode, false, getExtLanguageManager());

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
				String newType,
				ParameterConversionDefinition parameterConversionDefinition,
				IExtLanguageManager extLanguageManager) {

			String currentDefaultValue = methodParameterNode.getDefaultValue();

			if (parameterConversionDefinition == null) {
				setAdaptedValueAsDefault(methodParameterNode, newType, extLanguageManager, currentDefaultValue);
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

		//		private void adaptDefaultValue() { 
		//
		//			String newType = getNewType();
		//
		//			fOriginalDefaultValue = fMethodParameterNode.getDefaultValue();
		//
		//			ITypeAdapter<?> adapter = getTypeAdapterProvider().getAdapter(getNewType());
		//			String newDefaultValue = 
		//					adapter.adapt(fMethodParameterNode.getDefaultValue(), false, ERunMode.QUIET, getExtLanguageManager());
		//
		//			if (newDefaultValue == null) {
		//				if (fMethodParameterNode.getLeafChoices().size() > 0) {
		//					newDefaultValue = fMethodParameterNode.getLeafChoices().toArray(new ChoiceNode[]{})[0].getValueString();
		//				}
		//				else{
		//					newDefaultValue = adapter.getDefaultValue();
		//				}
		//			}
		//
		//			if (JavaLanguageHelper.isUserType(newType)) {
		//				if (fMethodParameterNode.getLeafChoices().size() > 0) {
		//					if (fMethodParameterNode.getLeafChoiceValues().contains(newDefaultValue) == false) {
		//						newDefaultValue = fMethodParameterNode.getLeafChoiceValues().toArray(new String[]{})[0];
		//					}
		//				}
		//				//				else{
		//				//					fMethodParameterNode.addChoice(
		//				//							new ChoiceNode(
		//				//									"choice1", 
		//				//									newDefaultValue, 
		//				//									fMethodParameterNode.getModelChangeRegistrator()));
		//				//				}
		//			}
		//
		//			fMethodParameterNode.setDefaultValueString(newDefaultValue);
		//		}

		//		private void adaptTestCases() {
		//
		//			MethodNode method = fMethodParameterNode.getMethod();
		//			if (method != null) {
		//				Iterator<TestCaseNode> tcIt = method.getTestCases().iterator();
		//
		//				ITypeAdapter<?> adapter = getTypeAdapterProvider().getAdapter(getNewType());
		//
		//				while (tcIt.hasNext()) {
		//					ChoiceNode expectedValue = tcIt.next().getTestData().get(fMethodParameterNode.getMyIndex());
		//					String newValue = 
		//							adapter.adapt(
		//									expectedValue.getValueString(), false, ERunMode.QUIET, getExtLanguageManager());
		//
		//					if (JavaLanguageHelper.isUserType(getNewType())) {
		//						if (fMethodParameterNode.getLeafChoiceValues().contains(newValue) == false) {
		//							tcIt.remove();
		//							continue;
		//						}
		//					}
		//					if (newValue == null && adapter.isNullAllowed() == false) {
		//						tcIt.remove();
		//						continue;
		//					}
		//					else{
		//						if (expectedValue.getValueString().equals(newValue) == false) {
		//							expectedValue.setValueString(newValue);
		//						}
		//					}
		//				}
		//			}
		//		}

		//		private void adaptConstraints() {
		//			MethodNode methodNode = fMethodParameterNode.getMethod();
		//			MethodNode.ConstraintsItr constraintItr = methodNode.getIterator();
		//
		//			while (methodNode.hasNextConstraint(constraintItr)) {
		//
		//				ConstraintNode constraintNode = methodNode.getNextConstraint(constraintItr);
		//				Constraint constraint = constraintNode.getConstraint();
		//
		//				if (isRelevantConstraint(constraint)) {
		//
		//					IStatementVisitor statementAdapter = new StatementAdapter();
		//					try {
		//						if (!(boolean) constraint.getPrecondition().accept(statementAdapter)
		//								|| !(boolean) constraint.getPostcondition().accept(statementAdapter)) {
		//							methodNode.removeConstraint(constraintItr);
		//						}
		//					} catch (Exception e) {
		//						methodNode.removeConstraint(constraintItr);
		//					}
		//				}
		//			}
		//		}

		//		private boolean isRelevantConstraint(Constraint constraint) {
		//			if (constraint.getPostcondition() instanceof ExpectedValueStatement) {
		//				ExpectedValueStatement expectedValueStatement = (ExpectedValueStatement)constraint.getPostcondition();
		//				MethodParameterNode methodParameterNode = expectedValueStatement.getLeftParameter();
		//				if(fMethodParameterNode.equals(methodParameterNode)) {
		//					return true;
		//				}
		//			}
		//			return false;
		//		}

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

		//		private class StatementAdapter implements IStatementVisitor{
		//
		//			@Override
		//			public Object visit(StaticStatement statement) throws Exception {
		//				return true;
		//			}
		//
		//			@Override
		//			public Object visit(StatementArray statement) throws Exception {
		//				boolean success = true;
		//				for(AbstractStatement child : statement.getChildren()) {
		//					try {
		//						success &= (boolean)child.accept(this);
		//					} catch(Exception e) {
		//						success = false;
		//					}
		//				}
		//				return success;
		//			}
		//
		//			@Override
		//			public Object visit(ExpectedValueStatement statement) throws Exception {
		//
		//				boolean success = true;
		//
		//				ITypeAdapter<?> adapter = getTypeAdapterProvider().getAdapter(getNewType());
		//				String newValue = 
		//						adapter.adapt(
		//								statement.getChoice().getValueString(), 
		//								false, 
		//								ERunMode.QUIET,
		//								getExtLanguageManager());
		//
		//				// TODO DE-NO
		//				//				fOriginalStatementValues.put(statement, statement.getChoice().getValueString());
		//				statement.getChoice().setValueString(newValue);
		//				if (JavaLanguageHelper.isUserType(getNewType())) {
		//					success = newValue != null && fMethodParameterNode.getLeafChoiceValues().contains(newValue);
		//				}
		//				else{
		//					success = newValue != null;
		//				}
		//				return success;
		//			}
		//
		//			@Override
		//			public Object visit(RelationStatement statement)
		//					throws Exception {
		//				return true;
		//			}
		//
		//			@Override
		//			public Object visit(LabelCondition condition) throws Exception {
		//				return true;
		//			}
		//
		//			@Override
		//			public Object visit(ChoiceCondition condition) throws Exception {
		//				return true;
		//			}
		//
		//			@Override
		//			public Object visit(ParameterCondition condition) throws Exception {
		//				return true;
		//			}
		//
		//			@Override
		//			public Object visit(ValueCondition condition) throws Exception {
		//				return null;
		//			}
		//		}

		private class ReverseSetTypeOperation extends AbstractParameterOperationSetType.ReverseOperation{

			public ReverseSetTypeOperation(IExtLanguageManager extLanguageManager) {

				super(extLanguageManager);
			}

			//			private class StatementValueRestorer implements IStatementVisitor{
			//
			//				@Override
			//				public Object visit(StaticStatement statement) throws Exception {
			//					return null;
			//				}
			//
			//				@Override
			//				public Object visit(StatementArray statement) throws Exception {
			//
			//					for(AbstractStatement child : statement.getChildren()) {
			//						try {
			//							child.accept(this);
			//						} catch(Exception e) {LogHelperCore.logCatch(e);}
			//					}
			//					return null;
			//				}
			//
			//				@Override
			//				public Object visit(ExpectedValueStatement statement)
			//						throws Exception {
			//					// TODO DE-NO
			//					//					if (fOriginalStatementValues.containsKey(statement)) {
			//					//						statement.getChoice().setValueString(fOriginalStatementValues.get(statement));
			//					//					}
			//					return null;
			//				}
			//
			//				@Override
			//				public Object visit(RelationStatement statement)
			//						throws Exception {
			//					return null;
			//				}
			//
			//				@Override
			//				public Object visit(LabelCondition condition) throws Exception {
			//					return null;
			//				}
			//
			//				@Override
			//				public Object visit(ChoiceCondition condition)
			//						throws Exception {
			//					return null;
			//				}
			//
			//				@Override
			//				public Object visit(ParameterCondition condition)
			//						throws Exception {
			//					return null;
			//				}
			//
			//				@Override
			//				public Object visit(ValueCondition condition) throws Exception {
			//					return null;
			//				}
			//			}

			@Override
			public void execute() {

				super.execute();
				fMethodParameterNode.getMethod().replaceTestCases(fOriginalTestCases);
				fMethodParameterNode.getMethod().replaceConstraints(fOriginalConstraints);
				fMethodParameterNode.setDefaultValueString(fOriginalDefaultValue);
				//restoreStatementValues();

				ConstraintHelper.restoreOriginalConstraintValues(
						fMethodParameterNode.getMethod(), fOriginalConstraintValues);

				markModelUpdated();
			}

			@Override
			public IModelOperation getReverseOperation() {

				return new SetTypeOperation(
						fMethodParameterNode, 
						getNewType(), 
						fParameterConversionDefinition, 
						getTypeAdapterProvider(), 
						getExtLanguageManager());
			}

			//			private void restoreStatementValues() {
			//
			//				IStatementVisitor valueRestorer = new StatementValueRestorer();
			//				for(ConstraintNode constraint : fMethodParameterNode.getMethod().getConstraintNodes()) {
			//					try {
			//						constraint.getConstraint().getPrecondition().accept(valueRestorer);
			//						constraint.getConstraint().getPostcondition().accept(valueRestorer);
			//					} catch(Exception e) {LogHelperCore.logCatch(e);}
			//				}
			//			}

		}

	}

}
