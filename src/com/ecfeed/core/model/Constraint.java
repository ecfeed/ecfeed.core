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

import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;

public class Constraint implements IConstraint<ChoiceNode> {

	private String fName;
	private ConstraintType fConstraintType;
	private final IModelChangeRegistrator fModelChangeRegistrator;
	private AbstractStatement fPrecondition;
	private AbstractStatement fPostcondition;


	public Constraint(
			String name,
			ConstraintType constraintType,
			AbstractStatement precondition,
			AbstractStatement postcondition,
			IModelChangeRegistrator modelChangeRegistrator) {

		if (name == null) {
			fName = "constraint";
		}

		fName = name;
		fConstraintType = constraintType;
		fPrecondition = precondition;
		fPostcondition = postcondition;

		fModelChangeRegistrator = modelChangeRegistrator;
	}

	public String getName() {
		return fName;
	}

	public void setName(String name) {
		fName = name;
	}	

	public ConstraintType getType() {
		return fConstraintType;
	}

	public void setType(ConstraintType constraintType) {
		fConstraintType = constraintType;
	}

	public IModelChangeRegistrator getModelChangeRegistrator() {

		return fModelChangeRegistrator;
	}

	public String checkIntegrity(IExtLanguageManager extLanguageManager) {

		if (fConstraintType == ConstraintType.EXTENDED_FILTER) {
			return checkExtendedFilterConstraint(extLanguageManager);
		}

		if (fConstraintType == ConstraintType.BASIC_FILTER) {
			return checkBasicFilterConstraint(extLanguageManager);
		}

		if (fConstraintType == ConstraintType.ASSIGNMENT) {
			return checkAssignmentConstraint(extLanguageManager);
		}

		ExceptionHelper.reportRuntimeException("Invalid constraint type.");
		return null;
	}

	private String checkAssignmentConstraint(IExtLanguageManager extLanguageManager) {

		AbstractStatement precondition = getPrecondition();
		String errorMessage = checkFilteringStatement(precondition, extLanguageManager);

		if (errorMessage != null) {
			return errorMessage;
		}

		return checkPostconditionOfAssignmentConstraint(extLanguageManager);
	}

	private String checkFilteringStatement(AbstractStatement abstractStatement, IExtLanguageManager extLanguageManager) {

		if (abstractStatement instanceof StaticStatement) {
			return null;
		}

		if (abstractStatement instanceof ExpectedValueStatement) {
			return "Expected value statement is not allowed in this version of software.";
		}

		String errorMessage = checkIfStatementValueIsAdaptable(abstractStatement, extLanguageManager);

		if (errorMessage != null) {
			return errorMessage;
		}

		if (abstractStatement instanceof RelationStatement) {
			return checkFilteringRelationStatement(abstractStatement);
		}

		if (abstractStatement instanceof StatementArray) {

			StatementArray statementArray = (StatementArray)abstractStatement;

			List<AbstractStatement> statements = statementArray.getStatements();

			for (AbstractStatement childAbstractStatement : statements) {

				errorMessage = 
						checkFilteringStatement(
								childAbstractStatement, extLanguageManager);

				if (errorMessage != null) {
					return errorMessage;
				}
			}
		}

		return null;
	}

	public String checkFilteringRelationStatement(AbstractStatement abstractStatement) {

		RelationStatement relationStatement = (RelationStatement)abstractStatement;

		EMathRelation mathRelation = relationStatement.getRelation();

		if (mathRelation == EMathRelation.ASSIGN) {
			return "Assignment is not allowed in filtering statement.";
		}

		String errorMessage = checkParameterTypes(relationStatement);

		if (errorMessage != null) {
			return errorMessage;
		}

		return null;
	}

	private String checkParameterTypes(RelationStatement relationStatement) {

		IStatementCondition statementCondition = relationStatement.getCondition();

		if (!(statementCondition instanceof ParameterCondition)) {
			return null;
		}

		ParameterCondition parameterCondition = (ParameterCondition)statementCondition;

		String rightParameterType = parameterCondition.getRightParameterNode().getType();

		if (!relationStatement.isRightParameterTypeAllowed(rightParameterType)) {
			return "Parameter type mismatch.";
		}

		return null;
	}

	private String checkPostconditionOfAssignmentConstraint(IExtLanguageManager extLanguageManager) {

		AbstractStatement postcondition = getPostcondition();

		if (postcondition instanceof StaticStatement) {
			return null;
		}

		if (postcondition instanceof StatementArray) {
			return checkAssignmentStatementArray(postcondition, extLanguageManager);
		}

		return "Expected output constraint has postcondition of invalid type.";
	}

	private String checkAssignmentStatementArray(AbstractStatement postcondition, IExtLanguageManager extLanguageManager) {

		StatementArray statementArray = (StatementArray)postcondition;

		StatementArrayOperator statementArrayOperator = statementArray.getOperator();

		if (statementArrayOperator != StatementArrayOperator.ASSIGN) {
			return "Expected output statement has operator of invalid type.";
		}

		List<AbstractStatement> abstractStatements = statementArray.getStatements();

		for (AbstractStatement abstractStatement : abstractStatements )  {

			if (!(abstractStatement instanceof AssignmentStatement)) {
				return "Expected output constraint has postcondition with statement of invalid type.";
			}

			AssignmentStatement assignmentStatement = (AssignmentStatement)abstractStatement;

			MethodParameterNode leftParameterNode = assignmentStatement.getLeftParameter();

			if (!leftParameterNode.isExpected()) {
				return ("Left parameter should of assignment: " + assignmentStatement.createSignature(extLanguageManager) + " should be expected.");
			}

		}

		return null;
	}

	public String checkExtendedFilterConstraint(IExtLanguageManager extLanguageManager) {

		AbstractStatement precondition = getPrecondition();

		String errorMessage = checkFilteringStatement(precondition, extLanguageManager);

		if (errorMessage != null) {
			return errorMessage;
		}

		AbstractStatement postcondition = getPostcondition();

		errorMessage = checkFilteringStatement(postcondition, extLanguageManager);

		if (errorMessage != null) {
			return errorMessage;
		}

		return null;
	}

	public String checkBasicFilterConstraint(IExtLanguageManager extLanguageManager) {

		AbstractStatement precondition = getPrecondition();

		if (!(precondition instanceof StaticStatement))  {
			return "Invariant constraint has precondition of a wrong type.";
		}

		StaticStatement staticStatement = (StaticStatement)precondition;

		if (staticStatement.getValue() != EvaluationResult.TRUE) {
			return "Precondition has an invalid value.";
		}

		AbstractStatement postcondition = getPostcondition();

		String errorMessage = checkFilteringStatement(postcondition, extLanguageManager);

		if (errorMessage != null) {
			return errorMessage;
		}

		return null;
	}

	private String checkIfStatementValueIsAdaptable(
			AbstractStatement abstractStatement,
			IExtLanguageManager extLanguageManager) {

		if (!(abstractStatement instanceof RelationStatement)) {
			return null;
		}

		RelationStatement relationStatement = (RelationStatement)abstractStatement;

		String leftParameterType = relationStatement.getLeftParameter().getType();

		if (leftParameterType == null) {
			return null;
		}

		ITypeAdapterProvider typeAdapterProvider = new TypeAdapterProviderForJava();
		ITypeAdapter<?> typeAdapter = typeAdapterProvider.getAdapter(leftParameterType);

		IStatementCondition statementCondition = relationStatement.getCondition();

		if (!(statementCondition instanceof ValueCondition)) {
			return null;
		}

		ValueCondition valueCondition = (ValueCondition)statementCondition;

		String value = valueCondition.getRightValue();

		try {
			typeAdapter.adapt(
					value, 
					false, 
					ERunMode.WITH_EXCEPTION,
					extLanguageManager);

		} catch (RuntimeException ex) {
			return "Incompatible types. " + ex.getMessage();
		}

		return null;
	}

	public void assertIsCorrect(IExtLanguageManager extLanguageManager) {

		String errorMessage = checkIntegrity(extLanguageManager);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}
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

	public boolean isAmbiguous(List<List<ChoiceNode>> testDomain) {

		MessageStack outWhyAmbiguous = new MessageStack();
		ExtLanguageManagerForJava extLanguageManager = new ExtLanguageManagerForJava();

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
	public EvaluationResult evaluate(List<ChoiceNode> choiceNodesFromTestCase) {

		if (fConstraintType == ConstraintType.ASSIGNMENT) {
			return EvaluationResult.TRUE;
		}

		if (fPrecondition == null) { 
			return EvaluationResult.TRUE;
		}

		EvaluationResult preconditionEvaluationResult = fPrecondition.evaluate(choiceNodesFromTestCase);

		if (preconditionEvaluationResult == EvaluationResult.FALSE) {
			return EvaluationResult.TRUE;
		}

		if (preconditionEvaluationResult == EvaluationResult.INSUFFICIENT_DATA) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		if (fPostcondition == null) {
			return EvaluationResult.FALSE;
		}

		EvaluationResult postconditionEvaluationResult = fPostcondition.evaluate(choiceNodesFromTestCase);

		if (postconditionEvaluationResult == EvaluationResult.TRUE) {
			return EvaluationResult.TRUE;
		}

		if (postconditionEvaluationResult == EvaluationResult.INSUFFICIENT_DATA) {
			return EvaluationResult.INSUFFICIENT_DATA;
		}

		return EvaluationResult.FALSE;
	}

	@Override
	public boolean setExpectedValues(List<ChoiceNode> choiceNodesFromTestCase) {

		if (fPrecondition == null) {
			return true;
		}

		if (fPrecondition.evaluate(choiceNodesFromTestCase) == EvaluationResult.TRUE) {
			return fPostcondition.setExpectedValues(choiceNodesFromTestCase);
		}

		return true;
	}

	@Override
	public String toString() {

		return createSignature(new ExtLanguageManagerForJava());
	}


	// TODO DE-NO Move to helper
	public void updateChoiceReferences(
			ChoiceNode oldChoiceNode, 
			ChoiceNode newChoiceNode,
			ListOfModelOperations reverseOperations,
			IExtLanguageManager extLanguageManager) {

		fPrecondition.updateChoiceReferences(oldChoiceNode, newChoiceNode, reverseOperations, extLanguageManager);
		fPostcondition.updateChoiceReferences(oldChoiceNode, newChoiceNode, reverseOperations, extLanguageManager);
	}

	public void updateParameterReferences(
			MethodParameterNode oldMethodParameterNode,
			ChoicesParentNode dstParameterForChoices,
			ListOfModelOperations reverseOperations,
			IExtLanguageManager extLanguageManager) {

		fPrecondition.updateParameterReferences(oldMethodParameterNode, dstParameterForChoices, reverseOperations, extLanguageManager);
		fPostcondition.updateParameterReferences(oldMethodParameterNode, dstParameterForChoices, reverseOperations, extLanguageManager);
	}

	public String createSignature(IExtLanguageManager extLanguageManager) {

		String postconditionSignature = AbstractStatementHelper.createSignature(fPostcondition, extLanguageManager);

		if (fConstraintType == ConstraintType.BASIC_FILTER) {
			return postconditionSignature;
		}

		String preconditionSignature = AbstractStatementHelper.createSignature(fPrecondition, extLanguageManager);

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

	@Override
	public void derandomize() {

		AbstractStatement precondition = getPrecondition();
		precondition.derandomize();

		AbstractStatement postcondition = getPostcondition();
		postcondition.derandomize();

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

	public Constraint makeClone() {

		AbstractStatement precondition = fPrecondition.makeClone();
		AbstractStatement postcondition = fPostcondition.makeClone();

		return new Constraint(new String(fName), fConstraintType, precondition, postcondition, fModelChangeRegistrator);
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
