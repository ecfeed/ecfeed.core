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

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceCondition;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IChoicesParentVisitor;
import com.ecfeed.core.model.IStatementVisitor;
import com.ecfeed.core.model.LabelCondition;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.ParameterCondition;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.ValueCondition;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.SimpleTypeHelper;
import com.ecfeed.core.utils.SystemLogger;
import com.ecfeed.core.utils.ExtLanguage;

public class MethodParameterOperationSetType extends BulkOperation {
	
	private ExtLanguage fExtLanguage;

	private class SetTypeOperation extends AbstractParameterOperationSetType{

		private String fOriginalDefaultValue;
		private Map<AbstractStatement, String> fOriginalStatementValues;
		private ArrayList<TestCaseNode> fOriginalTestCases;
		private ArrayList<ConstraintNode> fOriginalConstraints;

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

		private class StatementAdapter implements IStatementVisitor{

			@Override
			public Object visit(StaticStatement statement) throws Exception {
				return true;
			}

			@Override
			public Object visit(StatementArray statement) throws Exception {
				boolean success = true;
				for(AbstractStatement child : statement.getChildren()) {
					try {
						success &= (boolean)child.accept(this);
					} catch(Exception e) {
						success = false;
					}
				}
				return success;
			}

			@Override
			public Object visit(ExpectedValueStatement statement) throws Exception {

				boolean success = true;

				ITypeAdapter<?> adapter = getTypeAdapterProvider().getAdapter(getNewType());
				String newValue = 
						adapter.convert(
								statement.getCondition().getValueString(), false, ERunMode.QUIET);

				fOriginalStatementValues.put(statement, statement.getCondition().getValueString());
				statement.getCondition().setValueString(newValue);
				if (JavaTypeHelper.isUserType(getNewType())) {
					success = newValue != null && fMethodParameterNode.getLeafChoiceValues().contains(newValue);
				}
				else{
					success = newValue != null;
				}
				return success;
			}

			@Override
			public Object visit(RelationStatement statement)
					throws Exception {
				return true;
			}

			@Override
			public Object visit(LabelCondition condition) throws Exception {
				return true;
			}

			@Override
			public Object visit(ChoiceCondition condition) throws Exception {
				return true;
			}

			@Override
			public Object visit(ParameterCondition condition) throws Exception {
				return true;
			}

			@Override
			public Object visit(ValueCondition condition) throws Exception {
				return null;
			}
		}

		private class ReverseSetTypeOperation extends AbstractParameterOperationSetType.ReverseOperation{
			
			public ReverseSetTypeOperation(ExtLanguage extLanguage) {
				
				super(extLanguage);
			}

			private class StatementValueRestorer implements IStatementVisitor{

				@Override
				public Object visit(StaticStatement statement) throws Exception {
					return null;
				}

				@Override
				public Object visit(StatementArray statement) throws Exception {

					for(AbstractStatement child : statement.getChildren()) {
						try {
							child.accept(this);
						} catch(Exception e) {SystemLogger.logCatch(e);}
					}
					return null;
				}

				@Override
				public Object visit(ExpectedValueStatement statement)
						throws Exception {
					if (fOriginalStatementValues.containsKey(statement)) {
						statement.getCondition().setValueString(fOriginalStatementValues.get(statement));
					}
					return null;
				}

				@Override
				public Object visit(RelationStatement statement)
						throws Exception {
					return null;
				}

				@Override
				public Object visit(LabelCondition condition) throws Exception {
					return null;
				}

				@Override
				public Object visit(ChoiceCondition condition)
						throws Exception {
					return null;
				}

				@Override
				public Object visit(ParameterCondition condition)
						throws Exception {
					return null;
				}

				@Override
				public Object visit(ValueCondition condition) throws Exception {
					return null;
				}
			}

			@Override
			public void execute() throws ModelOperationException{

				super.execute();
				fMethodParameterNode.getMethod().replaceTestCases(fOriginalTestCases);
				fMethodParameterNode.getMethod().replaceConstraints(fOriginalConstraints);
				fMethodParameterNode.setDefaultValueString(fOriginalDefaultValue);
				restoreStatementValues();
				markModelUpdated();
			}

			@Override
			public IModelOperation getReverseOperation() {

				return new SetTypeOperation(
						fMethodParameterNode, getNewType(), getTypeAdapterProvider(), getExtLanguage());
			}

			private void restoreStatementValues() {

				IStatementVisitor valueRestorer = new StatementValueRestorer();
				for(ConstraintNode constraint : fMethodParameterNode.getMethod().getConstraintNodes()) {
					try {
						constraint.getConstraint().getPremise().accept(valueRestorer);
						constraint.getConstraint().getConsequence().accept(valueRestorer);
					} catch(Exception e) {SystemLogger.logCatch(e);}
				}
			}

		}

		private MethodParameterNode fMethodParameterNode;

		public SetTypeOperation(
				MethodParameterNode target, 
				String newType, 
				ITypeAdapterProvider adapterProvider, 
				ExtLanguage extLanguage) {
			
			super(target, newType, adapterProvider, extLanguage);

			fMethodParameterNode = target;
			fOriginalStatementValues = new HashMap<>();
		}

		@Override
		public void execute() throws ModelOperationException {

			MethodNode methodNode = fMethodParameterNode.getMethod();

			checkForDuplicateSignature(methodNode);

			super.execute();

			fOriginalTestCases = new ArrayList<>(methodNode.getTestCases());
			fOriginalConstraints = new ArrayList<>(methodNode.getConstraintNodes());

			adaptDefaultValue();

			if (fMethodParameterNode.isExpected()) {
				adaptTestCases();
				adaptConstraints();
			}

			markModelUpdated();
		}

		private void checkForDuplicateSignature(MethodNode oldMethodNode) throws ModelOperationException {

			List<String> parameterTypes = MethodNodeHelper.getMethodParameterTypes(oldMethodNode, fExtLanguage);
			
			String newParameterType = SimpleTypeHelper.convertConditionallyJavaTypeToSimpleType(getNewType(), fExtLanguage);
			
			parameterTypes.set(fMethodParameterNode.getMyIndex(), newParameterType);

			ClassNode classNode = oldMethodNode.getClassNode();
			
			MethodNode foundMethodNode = 
					ClassNodeHelper.findMethod(
							classNode, oldMethodNode.getName(), parameterTypes, fExtLanguage);

			if (foundMethodNode == null) {
				return;
			}

			if (foundMethodNode == oldMethodNode) {
				return;
			}
			
			String message = 
					ClassNodeHelper.generateMethodSignatureDuplicateMessage(
							classNode, foundMethodNode, getExtLanguage());
			
			ModelOperationException.report(message);
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ReverseSetTypeOperation(getExtLanguage());
		}

		@SuppressWarnings("unchecked")
		@Override
		protected List<ChoiceNode> getChoices(ChoicesParentNode parent) {
			try {
				return (List<ChoiceNode>)parent.accept(new RealChoicesProvider());
			} catch(Exception e) {SystemLogger.logCatch(e);}
			return null;
		}

		private void adaptDefaultValue() {

			String newType = getNewType();

			fOriginalDefaultValue = fMethodParameterNode.getDefaultValue();

			ITypeAdapter<?> adapter = getTypeAdapterProvider().getAdapter(getNewType());
			String newDefaultValue = 
					adapter.convert(fMethodParameterNode.getDefaultValue(), false, ERunMode.QUIET);

			if (newDefaultValue == null) {
				if (fMethodParameterNode.getLeafChoices().size() > 0) {
					newDefaultValue = fMethodParameterNode.getLeafChoices().toArray(new ChoiceNode[]{})[0].getValueString();
				}
				else{
					newDefaultValue = adapter.getDefaultValue();
				}
			}

			if (JavaTypeHelper.isUserType(newType)) {
				if (fMethodParameterNode.getLeafChoices().size() > 0) {
					if (fMethodParameterNode.getLeafChoiceValues().contains(newDefaultValue) == false) {
						newDefaultValue = fMethodParameterNode.getLeafChoiceValues().toArray(new String[]{})[0];
					}
				}
				else{
					fMethodParameterNode.addChoice(
							new ChoiceNode(
									newDefaultValue.toLowerCase(), 
									fMethodParameterNode.getModelChangeRegistrator(), 
									newDefaultValue));
				}
			}

			fMethodParameterNode.setDefaultValueString(newDefaultValue);
		}

		private void adaptTestCases() {

			MethodNode method = fMethodParameterNode.getMethod();
			if (method != null) {
				Iterator<TestCaseNode> tcIt = method.getTestCases().iterator();

				ITypeAdapter<?> adapter = getTypeAdapterProvider().getAdapter(getNewType());

				while (tcIt.hasNext()) {
					ChoiceNode expectedValue = tcIt.next().getTestData().get(fMethodParameterNode.getMyIndex());
					String newValue = 
							adapter.convert(
									expectedValue.getValueString(), false, ERunMode.QUIET);

					if (JavaTypeHelper.isUserType(getNewType())) {
						if (fMethodParameterNode.getLeafChoiceValues().contains(newValue) == false) {
							tcIt.remove();
							continue;
						}
					}
					if (newValue == null && adapter.isNullAllowed() == false) {
						tcIt.remove();
						continue;
					}
					else{
						if (expectedValue.getValueString().equals(newValue) == false) {
							expectedValue.setValueString(newValue);
						}
					}
				}
			}
		}

		private void adaptConstraints() {
			MethodNode methodNode = fMethodParameterNode.getMethod();
			MethodNode.ConstraintsItr constraintItr = methodNode.getIterator();

			while (methodNode.hasNextConstraint(constraintItr)) {

				ConstraintNode constraintNode = methodNode.getNextConstraint(constraintItr);
				Constraint constraint = constraintNode.getConstraint();

				if (isRelevantConstraint(constraint)) {

					IStatementVisitor statementAdapter = new StatementAdapter();
					try {
						if (!(boolean) constraint.getPremise().accept(statementAdapter)
								|| !(boolean) constraint.getConsequence().accept(statementAdapter)) {
							methodNode.removeConstraint(constraintItr);
						}
					} catch (Exception e) {
						methodNode.removeConstraint(constraintItr);
					}
				}
			}
		}

		private boolean isRelevantConstraint(Constraint constraint) {
			if (constraint.getConsequence() instanceof ExpectedValueStatement) {
				ExpectedValueStatement expectedValueStatement = (ExpectedValueStatement)constraint.getConsequence();
				MethodParameterNode methodParameterNode = expectedValueStatement.getParameter();
				if(fMethodParameterNode.equals(methodParameterNode)) {
					return true;
				}
			}
			return false;
		}
	}

	public MethodParameterOperationSetType(
			MethodParameterNode targetMethodParameterNode, 
			String newType, 
			ExtLanguage extLanguage,
			ITypeAdapterProvider adapterProvider) {

		super(OperationNames.SET_TYPE, true, targetMethodParameterNode, targetMethodParameterNode, extLanguage);

		fExtLanguage = extLanguage;
		
		if (newType == null) {
			ExceptionHelper.reportRuntimeException("Cannot set new type to null.");
		}

		if (!JavaTypeHelper.isJavaType(newType)) {
			ExceptionHelper.reportRuntimeException("Cannot set new type to non-Java type.");
		}

		addOperation(new SetTypeOperation(targetMethodParameterNode, newType, adapterProvider, getExtLanguage()));

		if (targetMethodParameterNode.getMethod() != null) {
			addOperation(new MethodOperationMakeConsistent(targetMethodParameterNode.getMethod(), getExtLanguage()));
		}
	}
}
