/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;

public class Constraint implements IConstraint<ChoiceNode> {

	private String fName;
	private ConstraintType fConstraintType;
	private final IModelChangeRegistrator fModelChangeRegistrator;
	private AbstractStatement fPrecondition;
	private AbstractStatement fPostcondition;


	public Constraint(String name,
					  AbstractStatement precondition,
					  AbstractStatement postcondition,
					  IModelChangeRegistrator modelChangeRegistrator) {

		if (name == null) {
			fName = "constraint";
		}

		fName = name;
		fModelChangeRegistrator = modelChangeRegistrator;
		fPrecondition = precondition;
		fPostcondition = postcondition;
	}

	public String getName() {
		return fName;
	}

	public void setName(String name) {
		fName = name;
	}	

	public ConstraintType getConstraintType() { // TODO CONSTRAINTS-NEW rename to getType
		return fConstraintType;
	}

	public void setConstratintType(ConstraintType constraintType) { // TODO CONSTRAINTS-NEW rename to setType
		fConstraintType = constraintType;
	}

	public boolean isAmbiguous(
			List<List<ChoiceNode>> testDomain, 
			MessageStack outWhyAmbiguous, 
			IExtLanguageManager extLanguageManager) {

		if (isAmbiguousForPreconditionOrPostcondition(testDomain, outWhyAmbiguous, extLanguageManager)) {
			ConditionHelper.addConstraintNameToMesageStack(getName(), outWhyAmbiguous);
			return true;
		}

		return false;

	}

	private boolean isAmbiguousForPreconditionOrPostcondition(
			List<List<ChoiceNode>> testDomain, MessageStack outWhyAmbiguous, IExtLanguageManager extLanguageManager) {

		if (fPrecondition.isAmbiguous(testDomain, outWhyAmbiguous, extLanguageManager)) {
			return true;
		}

		if (fPostcondition.isAmbiguous(testDomain, outWhyAmbiguous, extLanguageManager)) {
			return true;
		}

		return false;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> values) {

		if (fPrecondition == null) { 
			return EvaluationResult.TRUE;
		}

		EvaluationResult preconditionEvaluationResult = fPrecondition.evaluate(values);

		if (preconditionEvaluationResult == EvaluationResult.FALSE) {
			return EvaluationResult.TRUE;
		}

		if (preconditionEvaluationResult == EvaluationResult.INSUFFICIENT_DATA) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		if (fPostcondition == null) {
			return EvaluationResult.FALSE;
		}

		EvaluationResult postconditionEvaluationResult = fPostcondition.evaluate(values);

		if (postconditionEvaluationResult == EvaluationResult.TRUE) {
			return EvaluationResult.TRUE;
		}

		if (postconditionEvaluationResult == EvaluationResult.INSUFFICIENT_DATA) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		return EvaluationResult.FALSE;
	}

	@Override
	public boolean adapt(List<ChoiceNode> values) {

		if (fPrecondition == null) {
			return true;
		}

		if (fPrecondition.evaluate(values) == EvaluationResult.TRUE) {
			return fPostcondition.adapt(values);
		}

		return true;
	}

	@Override
	public String toString() {

		return createSignature(new ExtLanguageManagerForJava());
	}

	public String createSignature(IExtLanguageManager extLanguageManager) {

		String preconditionSignature = AbstractStatementHelper.createSignature(fPrecondition, extLanguageManager);
		String postconditionSignature = AbstractStatementHelper.createSignature(fPostcondition, extLanguageManager);

		return preconditionSignature + " => " + postconditionSignature;
	}

	@Override
	public boolean mentions(int dimension) {

		if (fPrecondition.mentions(dimension)) {
			return true;
		}

		if (fPostcondition.mentions(dimension)) {
			return true;
		}

		return false;
	}

	public AbstractStatement getPrecondition() {

		return fPrecondition;
	}

	public AbstractStatement getPostcondition() {

		return fPostcondition;
	}

	public void setPrecondition(AbstractStatement statement) {

		fPrecondition = statement;

		if (fModelChangeRegistrator != null) {
			fModelChangeRegistrator.registerChange();
		}
	}

	public void setPostcondition(AbstractStatement postcondition) {

		fPostcondition = postcondition;

		if (fModelChangeRegistrator != null) {
			fModelChangeRegistrator.registerChange();
		}
	}

	public boolean mentions(MethodParameterNode parameter) {

		return fPrecondition.mentions(parameter) || fPostcondition.mentions(parameter);
	}

	public boolean mentions(MethodParameterNode parameter, String label) {

		return fPrecondition.mentions(parameter, label) || fPostcondition.mentions(parameter, label);
	}

	public boolean mentions(ChoiceNode choice) {

		return fPrecondition.mentions(choice) || fPostcondition.mentions(choice);
	}

	public List<ChoiceNode> getListOfChoices() {

		List<ChoiceNode> result = new ArrayList<ChoiceNode>();

		result.addAll(fPrecondition.getListOfChoices());
		result.addAll(fPostcondition.getListOfChoices());

		return result;
	}

	public boolean mentionsParameterAndOrderRelation(MethodParameterNode parameter) {

		if (fPrecondition.mentionsParameterAndOrderRelation(parameter)) {
			return true;
		}

		if (fPostcondition.mentionsParameterAndOrderRelation(parameter)) {
			return true;
		}

		return false;
	}

	public boolean updateReferences(MethodNode method) {

		if (fPrecondition.updateReferences(method) && fPostcondition.updateReferences(method)) {
			return true;
		}

		return false;
	}

	public Constraint getCopy(){ // TOOD CONSTRAINT-NEW rename to makeClone

		AbstractStatement precondition = fPrecondition.getCopy();
		AbstractStatement postcondition = fPostcondition.getCopy();

		return new Constraint(new String(fName), precondition, postcondition, fModelChangeRegistrator);
	}

	@SuppressWarnings("unchecked")
	public Set<ChoiceNode> getReferencedChoices() {

		try {
			Set<ChoiceNode> referenced = (Set<ChoiceNode>)fPrecondition.accept(new ReferencedChoicesProvider());
			referenced.addAll((Set<ChoiceNode>)fPostcondition.accept(new ReferencedChoicesProvider()));

			return referenced;
		} catch(Exception e) {
			return new HashSet<ChoiceNode>();
		}
	}

	@SuppressWarnings("unchecked")
	public Set<AbstractParameterNode> getReferencedParameters() {

		try{
			Set<AbstractParameterNode> referenced = 
					(Set<AbstractParameterNode>)fPrecondition.accept(new ReferencedParametersProvider());

			referenced.addAll(
					(Set<AbstractParameterNode>)fPostcondition.accept(new ReferencedParametersProvider()));

			return referenced;
		} catch(Exception e) {
			return new HashSet<AbstractParameterNode>();
		}
	}

	@SuppressWarnings("unchecked")
	public Set<String> getReferencedLabels(MethodParameterNode parameter) {

		try {
			Set<String> referenced = (Set<String>)fPrecondition.accept(new ReferencedLabelsProvider(parameter));
			referenced.addAll((Set<String>)fPostcondition.accept(new ReferencedLabelsProvider(parameter)));

			return referenced;
		} catch(Exception e) {
			return new HashSet<>();
		}
	}

	boolean mentionsParameter(MethodParameterNode methodParameter) {

		if (fPrecondition.mentions(methodParameter)) {
			return true;
		}

		if (fPostcondition.mentions(methodParameter)) {
			return true;
		}

		return false;
	}

	private class ReferencedChoicesProvider implements IStatementVisitor {

		@Override
		public Object visit(StaticStatement statement) throws Exception {
			return new HashSet<ChoiceNode>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object visit(StatementArray statement) throws Exception {

			Set<ChoiceNode> set = new HashSet<ChoiceNode>();

			for (AbstractStatement s : statement.getStatements()) {
				set.addAll((Set<ChoiceNode>)s.accept(this));
			}

			return set;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {

			Set<ChoiceNode> result = new HashSet<>();

			if (statement.isParameterPrimitive()) {
				result.add(statement.getCondition());
			}

			return result;
		}

		@Override
		public Object visit(RelationStatement statement) throws Exception {

			return statement.getCondition().accept(this);
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {

			return new HashSet<ChoiceNode>();
		}

		@Override
		public Object visit(ChoiceCondition condition) throws Exception {

			Set<ChoiceNode> set = new HashSet<ChoiceNode>();
			set.add(condition.getRightChoice());

			return set;
		}

		@Override
		public Object visit(ParameterCondition condition) throws Exception {

			return new HashSet<ChoiceNode>();
		}

		@Override
		public Object visit(ValueCondition condition) throws Exception {

			return new HashSet<ChoiceNode>();
		}		

	}

	private class ReferencedParametersProvider implements IStatementVisitor {

		@Override
		public Object visit(StaticStatement statement) throws Exception {
			return new HashSet<MethodParameterNode>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object visit(StatementArray statement) throws Exception {

			Set<MethodParameterNode> set = new HashSet<MethodParameterNode>();

			for (AbstractStatement s : statement.getStatements()) {
				set.addAll((Set<MethodParameterNode>)s.accept(this));
			}

			return set;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {

			Set<AbstractParameterNode> set = new HashSet<AbstractParameterNode>();
			set.add(statement.getParameter());

			return set;
		}

		@Override
		public Object visit(RelationStatement statement) throws Exception {

			return statement.getCondition().accept(this);
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {

			return new HashSet<MethodParameterNode>();
		}

		@Override
		public Object visit(ChoiceCondition condition) throws Exception {

			Set<AbstractParameterNode> set = new HashSet<AbstractParameterNode>();
			AbstractParameterNode parameter = condition.getRightChoice().getParameter();

			if (parameter != null) {
				set.add(parameter);
			}

			return set;
		}

		@Override
		public Object visit(ParameterCondition condition) throws Exception {

			Set<AbstractParameterNode> set = new HashSet<AbstractParameterNode>();

			set.add(condition.getRightParameterNode());

			return set;
		}

		@Override
		public Object visit(ValueCondition condition) throws Exception {

			return new HashSet<MethodParameterNode>();
		}
	}


	private class ReferencedLabelsProvider implements IStatementVisitor {

		private MethodParameterNode fParameter;
		private Set<String> EMPTY_SET = new HashSet<String>();

		public ReferencedLabelsProvider(MethodParameterNode parameter) {

			fParameter = parameter;
		}

		@Override
		public Object visit(StaticStatement statement) throws Exception {

			return EMPTY_SET;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object visit(StatementArray statement) throws Exception {

			Set<String> set = new HashSet<String>();

			for (AbstractStatement s : statement.getStatements()) {
				set.addAll((Set<String>)s.accept(this));
			}

			return set;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {

			return EMPTY_SET;
		}

		@Override
		public Object visit(RelationStatement statement) throws Exception {

			if (fParameter == statement.getLeftParameter()) {
				return statement.getCondition().accept(this);
			}

			return EMPTY_SET;
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {

			Set<String> result = new HashSet<String>();
			result.add(condition.getRightLabel());

			return result;
		}

		@Override
		public Object visit(ChoiceCondition condition) throws Exception {

			return EMPTY_SET;
		}

		@Override
		public Object visit(ParameterCondition condition) throws Exception {
			return EMPTY_SET;
		}

		@Override
		public Object visit(ValueCondition condition) throws Exception {
			return EMPTY_SET;
		}

	}
}