package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.*;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.utils.*;
import com.google.common.primitives.Ints;
import com.ecfeed.core.model.MethodParameterNode;


import java.util.*;
import java.util.List;

public class SatSolverConstraintEvaluator implements IConstraintEvaluator<ChoiceNode> {
	private boolean enabled = false;

	private EcSatSolver fSat4Solver = new EcSatSolver();

	private ParameterChoices fParameterChoices = new ParameterChoices();
	private ChoiceMappings fChoiceMappings = new ChoiceMappings();

	private ChoiceToSolverIdMappings fChoiceToSolverIdMappings = new ChoiceToSolverIdMappings();

	// All relation statements extracted from all provided constraints.
	private List<RelationStatement> fRelationStatements = new ArrayList<>();
	private List<Pair<Integer, ExpectedValueStatement>> fOldExpectedValueConstraintsData = new ArrayList<>();
	private List<Pair<Integer, AssignmentStatement>> fExpectedValueAssignmentsData = new ArrayList<>();

	private List<MethodParameterNode> fParameters;
	private Collection<Constraint> fConstraints;

	private ChoiceNodeComparator fChoiceComparator = new ChoiceNodeComparator();

	private enum TypeOfEndpoint {
		LEFT_ENDPOINT,
		RIGHT_ENDPOINT
	}

	public SatSolverConstraintEvaluator(Collection<Constraint> constraints,	MethodNode methodX) {

		if (constraints == null || constraints.size() == 0) {
			return;
		}

		init(constraints);
	}

	private void init(Collection<Constraint> constraints) {

		Optional<MethodNode> method = initGetMethodNode(constraints);

		if (!method.isPresent()) {
			return;
		}

		fConstraints = constraints;
		fParameters = method.get().getMethodParameters();

		fParameterChoices.update(fParameters);
		fChoiceMappings.updateSanitizedToInput(fParameterChoices.getInputChoices());

		initSat4Solver();

		enabled = true;
	}

	private Optional<MethodNode> initGetMethodNode(Collection<Constraint> constraints) {

		for (Constraint constraint : constraints) {
			Optional<MethodNode> method = ConstraintHelper.getMethodNode(constraint);

			if (method.isPresent()) {
				return method;
			}
		}

		return Optional.empty();
	}

	private void initSat4Solver() {

		prepareSolversClauses();

		for (MethodParameterNode parameter : fParameters) {
			if (!parameter.isExpected()) {
				EvaluatorHelper.prepareVariablesForParameter(parameter,
						fParameterChoices,
						fSat4Solver,
						fChoiceMappings,
						fChoiceToSolverIdMappings);
			}
		}

		fSat4Solver.packClauses();
	}

	private void prepareSolversClauses() {
// This is always executed (providing that there are constraints).
		fSat4Solver.setHasConstraints();

		collectAllRelationStatements();

		LogHelperCore.log("fAllRelationStatements", fRelationStatements);

		sanitizeRelationStatementsWithRelation();

		createInputToSanitizedMapping();
		createSanitizedToAtomicMapping();

		parseConstraintsToSat();

		LogHelperCore.log("fExpectedValConstraints", fOldExpectedValueConstraintsData);
		LogHelperCore.log("fExpectedValueAssignmentsData", fExpectedValueAssignmentsData);
	}

// ------------------------------------------------------------------------------------

	private void collectAllRelationStatements() {

		for (Constraint constraint : fConstraints) {

			if (constraint.getType() == ConstraintType.ASSIGNMENT)  {
				continue;
			}

			collectRelationStatements(constraint);
		}
	}

	private void collectRelationStatements(Constraint constraint) {

		if (constraint.getPostcondition() instanceof ExpectedValueStatement) {
			collectRelationStatementsExpectedValue(constraint);
		} else {
			collectRelationStatementsOther(constraint);
		}
	}

	private void collectRelationStatementsExpectedValue(Constraint constraint) {

		try {
			constraint.getPrecondition().accept(new CollectingStatementVisitor(fRelationStatements));
		} catch (Exception e) {
			ExceptionHelper.reportRuntimeException("Relation statements could not be collected: " + constraint, e);
		}
	}

	private void collectRelationStatementsOther(Constraint constraint) {

		try {
			constraint.getPrecondition().accept(new CollectingStatementVisitor(fRelationStatements));
			constraint.getPostcondition().accept(new CollectingStatementVisitor(fRelationStatements));
		} catch (Exception e) {
			ExceptionHelper.reportRuntimeException("Relation statements could not be collected: " + constraint, e);
		}
	}

// ------------------------------------------------------------------------------------

	/**
	 * Adjust relation choices. it is only needed for numerical comparison.
	 */
	private void sanitizeRelationStatementsWithRelation() {
		boolean change = true;		// A choice value has been adjusted.

		while (change) {
			change = false;

			for (RelationStatement relationStatement : fRelationStatements) {
				if (sanitizeValues(relationStatement)) {
					change = true;
				}
			}
		}
	}

	private boolean sanitizeValues(RelationStatement relation) {
		IStatementCondition condition = relation.getCondition();

// Labels are not numerical. This is the simplest case, we can return from the function.
		if (condition instanceof LabelCondition) {
			return false;
		}

		MethodParameterNode leftParameter = relation.getLeftParameter();

// The parameter is not numerical, no adjustment is needed. We can return from the function.
		if (!isNumericalType(leftParameter)) {
			return false;
		}

		if (condition instanceof ParameterCondition) {
			return sanitizeWithParameterCondition(condition, leftParameter);
		}

		if (condition instanceof ValueCondition) {
			return sanitizeWithValueCondition(condition, leftParameter);
		}

		if (condition instanceof ChoiceCondition) {
			return sanitizeWithChoiceCondition(condition, leftParameter);
		}

		ExceptionHelper.reportRuntimeException("Invalid condition type.");

		return true;
	}

	private boolean sanitizeWithParameterCondition(IStatementCondition condition, MethodParameterNode leftParameter) {
		MethodParameterNode rightParameter = ((ParameterCondition) condition).getRightParameterNode();

		List<ChoiceNode> leftParameterChoices = new ArrayList<>(fParameterChoices.getSanitized(leftParameter));
		List<ChoiceNode> rightParameterChoices = new ArrayList<>(fParameterChoices.getSanitized(rightParameter));

		boolean change = false;

		List<ChoiceNode> leftParameterChoicesCopy = new ArrayList<>(leftParameterChoices);

		for (ChoiceNode choice : rightParameterChoices) {
			Pair<Boolean, List<ChoiceNode>> changeResult = splitListWithChoiceNode(leftParameterChoicesCopy, choice);

			change = change || changeResult.getFirst();
			leftParameterChoicesCopy = changeResult.getSecond();
		}

		List<ChoiceNode> rightParameterChoicesCopy = new ArrayList<>(rightParameterChoices);

		for (ChoiceNode choice : leftParameterChoices) {
			Pair<Boolean, List<ChoiceNode>> changeResult = splitListWithChoiceNode(rightParameterChoicesCopy, choice);

			change = change || changeResult.getFirst();
			rightParameterChoicesCopy = changeResult.getSecond();
		}

		fParameterChoices.putSanitized(leftParameter, new HashSet<>(leftParameterChoicesCopy));
		fParameterChoices.putSanitized(rightParameter, new HashSet<>(rightParameterChoicesCopy));

		return change;
	}

	private boolean sanitizeWithValueCondition(IStatementCondition condition, MethodParameterNode leftParameter) {
		List<ChoiceNode> leftParameterChoices = new ArrayList<>(fParameterChoices.getSanitized(leftParameter));

		String value = ((ValueCondition) condition).getRightValue();

		ChoiceNode choice = leftParameterChoices.get(0).makeCloneUnlink();
		choice.setRandomizedValue(false);
		choice.setValueString(value);

		Pair<Boolean, List<ChoiceNode>> changeResult = splitListWithChoiceNode(leftParameterChoices, choice);

		fParameterChoices.putSanitized(leftParameter, new HashSet<>(changeResult.getSecond()));

		return changeResult.getFirst();
	}

	private boolean sanitizeWithChoiceCondition(IStatementCondition condition, MethodParameterNode leftParameter) {
		List<ChoiceNode> leftParameterChoices = new ArrayList<>(fParameterChoices.getSanitized(leftParameter));

		ChoiceNode choice = ((ChoiceCondition) condition).getRightChoice();

		Pair<Boolean, List<ChoiceNode>> changeResult = splitListWithChoiceNode(leftParameterChoices, choice);

		fParameterChoices.putSanitized(leftParameter, new HashSet<>(changeResult.getSecond()));

		return changeResult.getFirst();
	}

	private Pair<Boolean, List<ChoiceNode>> splitListWithChoiceNode(List<ChoiceNode> parameterChoices, ChoiceNode choice) {
		ChoiceNode choiceStart;
		ChoiceNode choiceEnd;

		if (choice.isRandomizedValue()) {
			Pair<ChoiceNode, ChoiceNode> choicePair = ChoiceNodeHelper.rangeSplit(choice);
			choiceStart = choicePair.getFirst();
			choiceEnd = choicePair.getSecond();
		} else {
			choiceStart = choice;
			choiceEnd = choice;
		}

// Validate choices against the lower boundary of the referenced choice.
		Pair<Boolean, List<ChoiceNode>> changeResultLeft = splitListByValue(parameterChoices, choiceStart, TypeOfEndpoint.LEFT_ENDPOINT);
// Validate choices against the upper boundary of the referenced choice.
		Pair<Boolean, List<ChoiceNode>> changeResultRight =	splitListByValue(changeResultLeft.getSecond(), choiceEnd, TypeOfEndpoint.RIGHT_ENDPOINT);

		return new Pair<>(changeResultLeft.getFirst() || changeResultRight.getFirst(), changeResultRight.getSecond());
	}

	private Pair<Boolean, List<ChoiceNode>> splitListByValue(List<ChoiceNode> parameterChoices, ChoiceNode choiceRef, TypeOfEndpoint type) {
// A list of corrected choices.
		List<ChoiceNode> updatedChoices = new ArrayList<>();
		boolean change = false;

		for (ChoiceNode choice : parameterChoices) {
// The choice is not randomized, no adjustment is necessary.
			if (!choice.isRandomizedValue()) {
				updatedChoices.add(choice);
			} else {
// Divide the choice into two boundaries (lower, upper).
				Pair<ChoiceNode, ChoiceNode> choicePair = ChoiceNodeHelper.rangeSplit(choice);
				ChoiceNode choiceStart = choicePair.getFirst();
				ChoiceNode choiceEnd = choicePair.getSecond();

				ChoiceNode choiceStartMutable = choiceStart.makeCloneUnlink();
				ChoiceNode choiceEndMutable = choiceEnd.makeCloneUnlink();

				choiceUpdateValueString(choiceRef, choiceStartMutable, choiceEndMutable);

				choiceRound(choiceStart, choiceRef, choiceStartMutable, choiceEndMutable);
				choiceShiftEpsilon(choiceStartMutable, choiceEndMutable, type);

				if (fChoiceComparator.compare(choiceStartMutable, choiceEndMutable) == 0) {
					updatedChoices.add(choice);
					continue;
				}

				int cmp1 = fChoiceComparator.compare(choiceStart, choiceStartMutable);
				int cmp2 = fChoiceComparator.compare(choiceEndMutable, choiceEnd);

				if (cmp1 > 0 || cmp2 > 0) {
					updatedChoices.add(choice);
					continue;
				}

				ChoiceNode it1 = cmp1 < 0 ? ChoiceNodeHelper.toRangeFromFirst(choiceStart, choiceStartMutable) : choiceStart;
				ChoiceNode it2 = cmp2 < 0 ? ChoiceNodeHelper.toRangeFromSecond(choiceEndMutable, choiceEnd) : choiceEnd;

				change = true;

				fChoiceMappings.putSanitizedToInput(it1, fChoiceMappings.getSanitizedToInput(choice));
				fChoiceMappings.putSanitizedToInput(it2, fChoiceMappings.getSanitizedToInput(choice));

				updatedChoices.add(it1);
				updatedChoices.add(it2);
			}
		}

		return new Pair<>(change, updatedChoices);
	}

	private boolean isFloatType(AbstractParameterNode parameter) {

		return JavaLanguageHelper.isFloatingPointTypeName(parameter.getType());
	}

	private boolean isIntegerType(AbstractParameterNode parameter) {

		return JavaLanguageHelper.isExtendedIntTypeName(parameter.getType());
	}

	private boolean isNumericalType(AbstractParameterNode parameter) {

		if (isIntegerType(parameter)) {
			return true;
		}

		if (isFloatType(parameter)) {
			return true;
		}

		return false;
	}

	private void choiceRound(ChoiceNode choiceStart, ChoiceNode choiceRef, ChoiceNode choiceStartMutable, ChoiceNode choiceEndMutable) {

// We cannot compare integer to float directly. Here, we change float to integer and diminish the gap between both values.
		if (isIntegerType(choiceStart.getParameter()) && isFloatType(choiceRef.getParameter())) {
			ChoiceNodeHelper.roundValueDown(choiceStartMutable);
			ChoiceNodeHelper.roundValueUp(choiceEndMutable);
		}
	}

	private void choiceShiftEpsilon(ChoiceNode choice1, ChoiceNode choice2, TypeOfEndpoint type) {

		if (fChoiceComparator.compare(choice1, choice2) == 0) {
			if (type == TypeOfEndpoint.LEFT_ENDPOINT) {
				ChoiceNodeHelper.getPrecedingValue(choice1);
			} else {
				ChoiceNodeHelper.getFollowingVal(choice2);
			}
		}
	}

	private void choiceUpdateValueString(ChoiceNode choiceRef, ChoiceNode choice1, ChoiceNode choice2) {
		String valueString = ChoiceNodeHelper.convertValueToNumeric(choiceRef).getValueString();

		choice1.setValueString(valueString);
		choice2.setValueString(valueString);
	}

// ------------------------------------------------------------------------------------

	private void createInputToSanitizedMapping() {

		for (MethodParameterNode method : fParameterChoices.getKeySetSanitized()) {

			fChoiceMappings.putInputToSanitized(method);

			for (ChoiceNode choiceSanitized : fParameterChoices.getSanitized(method)) {

				ChoiceNode choiceInput = fChoiceMappings.getSanitizedToInput(choiceSanitized);

				fChoiceMappings.putInputToSanitized(method, choiceInput, choiceSanitized);
			}
		}
	}

	private void createSanitizedToAtomicMapping() {

		for (MethodParameterNode parameter : fParameterChoices.getKeySetSanitized()) {

			fParameterChoices.putAtomic(parameter);

			createSanitizedToAtomicMappings(parameter);
		}
	}

	private void createSanitizedToAtomicMappings(MethodParameterNode parameter) {

		//build AtomicVal <-> Sanitized Val mappings, build Param -> Atomic Val mapping
		for (ChoiceNode choiceSanitized : fParameterChoices.getSanitized(parameter)) {

			if (isRandomizedNumericalType(parameter, choiceSanitized)) {

				List<ChoiceNode> interleavedChoices = ChoiceNodeHelper.getInterleavedValues(choiceSanitized, fParameterChoices.getSizeSanitized());

				fParameterChoices.putAtomic(parameter, interleavedChoices);

				for (ChoiceNode interleavedChoice : interleavedChoices) {
					fChoiceMappings.putSanitizedToAtomic(choiceSanitized, interleavedChoice);
				}

			} else {

				fParameterChoices.putAtomic(parameter, choiceSanitized);
				fChoiceMappings.putSanitizedToAtomic(choiceSanitized, choiceSanitized);
			}
		}
	}

	private boolean isRandomizedNumericalType(AbstractParameterNode parameter, ChoiceNode choice) {

		return choice.isRandomizedValue() && isNumericalType(parameter);
	}

// ------------------------------------------------------------------------------------

	private void parseConstraintsToSat() {

		for (Constraint constraint : fConstraints) {
			parseConstraintToSat(constraint);
		}
	}

	private void parseConstraintToSat(Constraint constraint) {
		AbstractStatement precondition = constraint.getPrecondition();
		AbstractStatement postcondition = constraint.getPostcondition();

		if (constraint.getType() == ConstraintType.ASSIGNMENT) {

			addAssignmentStatementsToAssignmentsTable(precondition, postcondition);

			return;
		}

		if (postcondition instanceof ExpectedValueStatement) {

			addExpectedValueStatementToAssignmentsTable(precondition, (ExpectedValueStatement) postcondition);

			return;
		}

		addFilteringConstraintToSatSolver(precondition, postcondition);
	}

	private void addAssignmentStatementsToAssignmentsTable(AbstractStatement precondition, AbstractStatement postcondition) {

		if (!(postcondition instanceof StatementArray)) {
			return;
		}

		StatementArray statementArray = (StatementArray)postcondition;
		List<AbstractStatement> abstractStatements = statementArray.getChildren();

		for (AbstractStatement abstractStatement : abstractStatements) {

			if (!(abstractStatement instanceof AssignmentStatement)) {
				continue;
			}

			AssignmentStatement assignmentStatement = (AssignmentStatement) abstractStatement;

			addOneStatementToAssignmentsTable(precondition, assignmentStatement);
		}
	}

	private void addOneStatementToAssignmentsTable(AbstractStatement precondition, AssignmentStatement assignmentStatement) {

		try {
			Integer preconditionId =
					(Integer) precondition.accept(
							new ParseConstraintToSATVisitor(
									fParameters,
									fSat4Solver,
									fParameterChoices,
									fChoiceMappings,
									fChoiceToSolverIdMappings));

			fExpectedValueAssignmentsData.add(new Pair<>(preconditionId, assignmentStatement));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addExpectedValueStatementToAssignmentsTable(AbstractStatement precondition, ExpectedValueStatement expectedValueStatement) {

		try {
			Integer preconditionId =
					(Integer) precondition.accept(
							new ParseConstraintToSATVisitor(
									fParameters,
									fSat4Solver,
									fParameterChoices,
									fChoiceMappings,
									fChoiceToSolverIdMappings));

			fOldExpectedValueConstraintsData.add(new Pair<>(preconditionId, expectedValueStatement));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addFilteringConstraintToSatSolver(AbstractStatement precondition, AbstractStatement postcondition) {

		Integer preconditionId = null;
		Integer postconditionId = null;

		try {
			preconditionId = (Integer) precondition.accept(
					new ParseConstraintToSATVisitor(
							fParameters,
							fSat4Solver,
							fParameterChoices,
							fChoiceMappings,
							fChoiceToSolverIdMappings));

			postconditionId =
					(Integer) postcondition.accept(
							new ParseConstraintToSATVisitor(
									fParameters,
									fSat4Solver,
									fParameterChoices,
									fChoiceMappings,
									fChoiceToSolverIdMappings));
		} catch (Exception e) {
			e.printStackTrace();
		}

		fSat4Solver.addSat4Clause(new int[]{-preconditionId, postconditionId});
	}

// ------------------------------------------------------------------------------------

	@Override
	public void initialize(List<List<ChoiceNode>> input) {

		if (!enabled || !fSat4Solver.hasConstraints()) {
			return;
		}

		assertEqualSizes(input);

		for (int parameterIndex = 0; parameterIndex < fParameters.size(); parameterIndex++) {

			List<Integer> sat4Indexes = new ArrayList<>();
			MethodParameterNode methodParameterNode = fParameters.get(parameterIndex);

			if (methodParameterNode.isExpected()) {
				continue;
			}

			for (ChoiceNode choiceNode : input.get(parameterIndex)) {

				final Map<ChoiceNode, Integer> choiceNodeIntegerMap = fChoiceToSolverIdMappings.getEqMapping(methodParameterNode);

				Integer idOfParamChoiceVar = choiceNodeIntegerMap.get(choiceNode.getOrigChoiceNode());

				sat4Indexes.add(idOfParamChoiceVar);
			}

			//one of the input values has to be taken, for each variable
			final int[] clauseValues = sat4Indexes
					.stream()
					.mapToInt(Integer::intValue)
					.toArray();

			fSat4Solver.addSat4Clause(clauseValues);
			fSat4Solver.addClause(clauseValues);
		}
	}

	private void assertEqualSizes(List<List<ChoiceNode>> input) {

		if (input.size() != fParameters.size()) {
			ExceptionHelper.reportRuntimeException("Input data and parameters should have the same length.");
		}
	}

	@Override
	public void excludeAssignment(List<ChoiceNode> choicesToExclude) {

		fSat4Solver.setHasConstraints();

		if (fSat4Solver.isContradicting()) {
			return;
		}

		// TODO - what does it do ?
		for (MethodParameterNode methodParameterNode : fParameters)
			EvaluatorHelper.prepareVariablesForParameter(
					methodParameterNode,
					fParameterChoices,
					fSat4Solver,
					fChoiceMappings,
					fChoiceToSolverIdMappings
					);

		fSat4Solver.setNewVar();

		final int[] assumptions = createSolverAssumptions(choicesToExclude)
				.stream()
				.map(x -> -x)
				.mapToInt(Integer::intValue)
				.toArray();

		fSat4Solver.addClause(assumptions);
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> valueAssignment) {

		if (!enabled || !fSat4Solver.hasConstraints()) {
			return EvaluationResult.TRUE;
		}

		if (fSat4Solver.isContradicting()) {
			return EvaluationResult.FALSE;
		}

		final List<Integer> assumptionsFromValues =	createSolverAssumptions(valueAssignment);

		if (fSat4Solver.isProblemSatisfiable(assumptionsFromValues)) {
			return EvaluationResult.TRUE;
		} else {
			return EvaluationResult.FALSE;
		}
	}

	@Override
	public List<ChoiceNode> setExpectedValues(List<ChoiceNode> testCaseChoices) {

		if (!enabled || !fSat4Solver.hasConstraints()) {
			return testCaseChoices;
		}

		final List<Integer> assumptionsFromValues =	createSolverAssumptions(testCaseChoices);

		boolean isSatisfiable = fSat4Solver.isProblemSatisfiable(assumptionsFromValues);

		if (!isSatisfiable) {
			ExceptionHelper.reportRuntimeException("Problem is unsatisfiable. Cannot adapt expected choice.");
			return null;
		}

		Set<Integer> model = new HashSet<>(Ints.asList(fSat4Solver.getModel()));

		for (Pair<Integer, ExpectedValueStatement> expectedValConstraint : fOldExpectedValueConstraintsData) {
			if (model.contains(expectedValConstraint.getFirst())) {
				expectedValConstraint.getSecond().setExpectedValues(testCaseChoices);
			}
		}

		for (Pair<Integer, AssignmentStatement> expectedValConstraint : fExpectedValueAssignmentsData) {
			if (model.contains(expectedValConstraint.getFirst())) {
				expectedValConstraint.getSecond().setExpectedValues(testCaseChoices);
			}
		}

		for (int i = 0; i < testCaseChoices.size(); i++) {
			ChoiceNode p = testCaseChoices.get(i);
			MethodParameterNode parameter = fParameters.get(i);
			if (parameter.isExpected()) {
				testCaseChoices.set(i, p.makeClone());
			}
		}

		return testCaseChoices;
	}
	
	private List<Integer> createSolverAssumptions(List<ChoiceNode> currentArgumentAssignments) {

		if (!fSat4Solver.hasConstraints()) {
			return new ArrayList<>();
		}

		List<Integer> assumptions = new ArrayList<>();

		if (currentArgumentAssignments.size() != fParameters.size()) {
			ExceptionHelper.reportRuntimeException("Value assignment list and parameters list should be of equal size.");
			return null;
		}

		for (int i = 0; i < fParameters.size(); i++) {

			MethodParameterNode methodParameterNode = fParameters.get(i);
			ChoiceNode choiceAssignedToParameter = currentArgumentAssignments.get(i);

			if (choiceAssignedToParameter != null) {

				final Map<ChoiceNode, Integer> choiceNodeIntegerMap = fChoiceToSolverIdMappings.eqGet(methodParameterNode);

				if (choiceNodeIntegerMap == null) {
					continue; //no constraint on this method parameter
				}

				Integer solverId = choiceNodeIntegerMap.get(choiceAssignedToParameter.getOrigChoiceNode());

				assumptions.add(solverId);
			}
		}

		return assumptions;
	}
}
