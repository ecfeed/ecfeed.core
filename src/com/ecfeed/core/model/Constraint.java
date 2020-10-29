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
	private final IModelChangeRegistrator fModelChangeRegistrator;
	private AbstractStatement fPremise;
	private AbstractStatement fConsequence;


	public Constraint(String name,
			IModelChangeRegistrator modelChangeRegistrator,
			AbstractStatement premise, 
			AbstractStatement consequence) {

		if (name == null) {
			fName = "constraint";
		}

		fName = name;
		fModelChangeRegistrator = modelChangeRegistrator;
		fPremise = premise;
		fConsequence = consequence;
	}

	public String getName() {
		return fName;
	}

	public void setName(String name) {
		fName = name;
	}	

	public boolean isAmbiguous(
			List<List<ChoiceNode>> testDomain, 
			MessageStack outWhyAmbiguous) {
		
		if (isAmbiguousForPremiseOrConsequence(testDomain, outWhyAmbiguous)) {
			ConditionHelper.addConstraintNameToMesageStack(getName(), outWhyAmbiguous);
			return true;
		}

		return false;

	}

	private boolean isAmbiguousForPremiseOrConsequence(
			List<List<ChoiceNode>> testDomain, MessageStack outWhyAmbiguous) {

		if (fPremise.isAmbiguous(testDomain, outWhyAmbiguous)) {
			return true;
		}

		if (fConsequence.isAmbiguous(testDomain, outWhyAmbiguous)) {
			return true;
		}

		return false;
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> values) {

		if (fPremise == null) { 
			return EvaluationResult.TRUE;
		}

		EvaluationResult premiseEvaluationResult = fPremise.evaluate(values); 

		if (premiseEvaluationResult == EvaluationResult.FALSE) {
			return EvaluationResult.TRUE;
		}

		if (premiseEvaluationResult == EvaluationResult.INSUFFICIENT_DATA) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		if (fConsequence == null) {
			return EvaluationResult.FALSE;
		}

		EvaluationResult consequenceEvaluationResult = fConsequence.evaluate(values);

		if (consequenceEvaluationResult == EvaluationResult.TRUE) {
			return EvaluationResult.TRUE;
		}

		if (consequenceEvaluationResult == EvaluationResult.INSUFFICIENT_DATA) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		return EvaluationResult.FALSE;
	}

	@Override
	public boolean adapt(List<ChoiceNode> values) {

		if (fPremise == null) {
			return true;
		}

		if (fPremise.evaluate(values) == EvaluationResult.TRUE) {
			return fConsequence.adapt(values);
		}

		return true;
	}

	@Override
	public String toString() {

		return createSignature(new ExtLanguageManagerForJava());
	}

	public String createSignature(IExtLanguageManager extLanguageManager) {
		
		String premiseString = AbstractStatementHelper.createSignature(fPremise, extLanguageManager);
		String consequenceString = AbstractStatementHelper.createSignature(fConsequence, extLanguageManager);

		return premiseString + " => " + consequenceString;
	}

	@Override
	public boolean mentions(int dimension) {

		if (fPremise.mentions(dimension)) {
			return true;
		}

		if (fConsequence.mentions(dimension)) {
			return true;
		}

		return false;
	}

	public AbstractStatement getPremise() {

		return fPremise;
	}

	public AbstractStatement getConsequence() {

		return fConsequence;
	}

	public void setPremise(AbstractStatement statement) {

		fPremise = statement;

		if (fModelChangeRegistrator != null) {
			fModelChangeRegistrator.registerChange();
		}
	}

	public void setConsequence(AbstractStatement consequence) {

		fConsequence = consequence;

		if (fModelChangeRegistrator != null) {
			fModelChangeRegistrator.registerChange();
		}
	}

	public boolean mentions(MethodParameterNode parameter) {

		return fPremise.mentions(parameter) || fConsequence.mentions(parameter);
	}

	public boolean mentions(MethodParameterNode parameter, String label) {

		return fPremise.mentions(parameter, label) || fConsequence.mentions(parameter, label);
	}

	public boolean mentions(ChoiceNode choice) {

		return fPremise.mentions(choice) || fConsequence.mentions(choice);
	}

	public List<ChoiceNode> getListOfChoices() { // TODO - remove ? exists: getReferencedChoices
		
		List<ChoiceNode> result = new ArrayList<ChoiceNode>();
		result.addAll(fPremise.getListOfChoices());
		result.addAll(fConsequence.getListOfChoices());
		
		return result;
	}

	public boolean mentionsParameterAndOrderRelation(MethodParameterNode parameter) {

		if (fPremise.mentionsParameterAndOrderRelation(parameter)) {
			return true;
		}

		if (fConsequence.mentionsParameterAndOrderRelation(parameter)) {
			return true;
		}

		return false;
	}

	public Constraint getCopy(){

		AbstractStatement premise = fPremise.getCopy();
		AbstractStatement consequence = fConsequence.getCopy();

		return new Constraint(new String(fName), fModelChangeRegistrator, premise, consequence);
	}

	public boolean updateReferences(MethodNode method) {

		if (fPremise.updateReferences(method) && fConsequence.updateReferences(method)) {
			return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public Set<ChoiceNode> getReferencedChoices() {

		try {
			Set<ChoiceNode> referenced = (Set<ChoiceNode>)fPremise.accept(new ReferencedChoicesProvider());
			referenced.addAll((Set<ChoiceNode>)fConsequence.accept(new ReferencedChoicesProvider()));

			return referenced;
		} catch(Exception e) {
			return new HashSet<ChoiceNode>();
		}
	}

	@SuppressWarnings("unchecked")
	public Set<AbstractParameterNode> getReferencedParameters() {

		try{
			Set<AbstractParameterNode> referenced = 
					(Set<AbstractParameterNode>)fPremise.accept(new ReferencedParametersProvider());

			referenced.addAll(
					(Set<AbstractParameterNode>)fConsequence.accept(new ReferencedParametersProvider()));

			return referenced;
		} catch(Exception e) {
			return new HashSet<AbstractParameterNode>();
		}
	}

	@SuppressWarnings("unchecked")
	public Set<String> getReferencedLabels(MethodParameterNode parameter) {

		try {
			Set<String> referenced = (Set<String>)fPremise.accept(new ReferencedLabelsProvider(parameter));
			referenced.addAll((Set<String>)fConsequence.accept(new ReferencedLabelsProvider(parameter)));

			return referenced;
		} catch(Exception e) {
			return new HashSet<>();
		}
	}

	boolean mentionsParameter(MethodParameterNode methodParameter) {

		if (fPremise.mentions(methodParameter)) {
			return true;
		}

		if (fConsequence.mentions(methodParameter)) {
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