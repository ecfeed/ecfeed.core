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

		createSanitizedAndAtomicMappings();

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

	private void sanitizeRelationStatementsWithRelation() {
		boolean change = true;

		while (change) {
			change = false;

			for (RelationStatement relationStatement : fRelationStatements) {
				if (sanitizeValuesWithRelation(relationStatement)) {
					change = true;
				}
			}
		}
	}

	private boolean sanitizeValuesWithRelation(RelationStatement relation) {
		IStatementCondition condition = relation.getCondition();

		if (condition instanceof LabelCondition) {
			return sanitizeValueWithRelationLabel();
		}

		MethodParameterNode leftParameter = relation.getLeftParameter();

		if (!isNumericalType(leftParameter)) {
			return false;
		}

		List<ChoiceNode> leftParameterChoices = new ArrayList<>(fParameterChoices.getSanitized(leftParameter));

		if (condition instanceof ParameterCondition) {
			return sanitizeValueWithRelationParameter(condition, leftParameter, leftParameterChoices);
		}

		if (condition instanceof ValueCondition) {
			return sanitizeValueWithRelationValueChoice(condition, leftParameter, leftParameterChoices);
		}

		if (condition instanceof ChoiceCondition) {
			return sanitizeValueWithRelationValueChoice(condition, leftParameter, leftParameterChoices);
		}

		ExceptionHelper.reportRuntimeException("Invalid condition type.");

		return true;
	}

	private boolean sanitizeValueWithRelationLabel() {

		return false;
	}

	private boolean sanitizeValueWithRelationParameter(IStatementCondition condition, MethodParameterNode leftParameter, List<ChoiceNode> leftParameterChoices) {
		MethodParameterNode rightParameter = ((ParameterCondition) condition).getRightParameterNode();
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

	private boolean sanitizeValueWithRelationValueChoice(IStatementCondition condition, MethodParameterNode leftParameter, List<ChoiceNode> leftParameterChoices) {
		ChoiceNode choice;

		if (condition instanceof ValueCondition) {
			String value = ((ValueCondition) condition).getRightValue();

			choice = leftParameterChoices.get(0).makeCloneUnlink();
			choice.setRandomizedValue(false);
			choice.setValueString(value);
		} else {
			choice = ((ChoiceCondition) condition).getRightChoice();
		}

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

		Pair<Boolean, List<ChoiceNode>> changeResultLeft = splitListByValue(parameterChoices, choiceStart, TypeOfEndpoint.LEFT_ENDPOINT);
		Pair<Boolean, List<ChoiceNode>> changeResultRight =	splitListByValue(changeResultLeft.getSecond(), choiceEnd, TypeOfEndpoint.RIGHT_ENDPOINT);

		return new Pair<>(changeResultLeft.getFirst() || changeResultRight.getFirst(), changeResultRight.getSecond());
	}

	private Pair<Boolean, List<ChoiceNode>> splitListByValue(List<ChoiceNode> parameterChoices, ChoiceNode choiceRef, TypeOfEndpoint type) {
		List<ChoiceNode> updatedChoices = new ArrayList<>();
		boolean change = false;

		for (ChoiceNode choice : parameterChoices) {

			if (!choice.isRandomizedValue()) {
				updatedChoices.add(choice);
			} else {
				Pair<ChoiceNode, ChoiceNode> choicePair = ChoiceNodeHelper.rangeSplit(choice);
				ChoiceNode choiceStart = choicePair.getFirst();
				ChoiceNode choiceEnd = choicePair.getSecond();

				ChoiceNode val1 = choiceStart.makeCloneUnlink();
				ChoiceNode val2 = choiceEnd.makeCloneUnlink();

				choiceUpdateValueString(choiceRef, val1, val2);

				choiceRound(choiceStart, choiceRef, val1, val2);
				choiceShiftEpsilon(val1, val2, type);

				if (fChoiceComparator.compare(val1, val2) == 0) {
					updatedChoices.add(choice);
					continue;
				}

				int cmp1 = fChoiceComparator.compare(choiceStart, val1);
				int cmp2 = fChoiceComparator.compare(val2, choiceEnd);

				if (cmp1 > 0 || cmp2 > 0) {
					updatedChoices.add(choice);
					continue;
				}

				ChoiceNode it1 = cmp1 < 0 ? ChoiceNodeHelper.toRangeFromFirst(choiceStart, val1) : choiceStart;
				ChoiceNode it2 = cmp2 < 0 ? ChoiceNodeHelper.toRangeFromSecond(val2, choiceEnd) : choiceEnd;

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

	private void choiceRound(ChoiceNode choiceStart, ChoiceNode choiceRef, ChoiceNode choice1, ChoiceNode choice2) {

		if (isIntegerType(choiceStart.getParameter()) && isFloatType(choiceRef.getParameter())) {
			ChoiceNodeHelper.roundValueDown(choice1);
			ChoiceNodeHelper.roundValueUp(choice2);
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

			for (ChoiceNode sanitizedChoice : fParameterChoices.getSanitized(method)) {

				ChoiceNode inputChoice = fChoiceMappings.getSanitizedToInput(sanitizedChoice);

				fChoiceMappings.putInputToSanitized(method, inputChoice, sanitizedChoice);
			}
		}
	}

// ------------------------------------------------------------------------------------

	private void createSanitizedAndAtomicMappings() {

		for (MethodParameterNode methodParameterNode : fParameterChoices.getKeySetSanitized()) {

			// TODO - why do we need an empty set ?
			fParameterChoices.putAtomic(methodParameterNode, new HashSet<>());

			createSanitizedAndAtomicMappingsForParam(methodParameterNode);
		}
	}

	private void createSanitizedAndAtomicMappingsForParam(MethodParameterNode methodParameterNode) {

		//build AtomicVal <-> Sanitized Val mappings, build Param -> Atomic Val mapping
		for (ChoiceNode sanitizedChoiceNode : fParameterChoices.getSanitized(methodParameterNode)) {

			if (isRandomizedExtIntOrFloat(methodParameterNode.getType(), sanitizedChoiceNode)) {

				List<ChoiceNode> interleavedChoices =
						ChoiceNodeHelper.getInterleavedValues(
								sanitizedChoiceNode, fParameterChoices.getSizeSanitized());

				fParameterChoices.getAtomic(methodParameterNode).addAll(interleavedChoices);

				for (ChoiceNode interleavedChoiceNode : interleavedChoices) {
					fChoiceMappings.putSanitizedToAtomic(sanitizedChoiceNode, interleavedChoiceNode);
				}

			} else {

				fParameterChoices.getAtomic(methodParameterNode).add(sanitizedChoiceNode);
				fChoiceMappings.putSanitizedToAtomic(sanitizedChoiceNode, sanitizedChoiceNode);
			}
		}
	}



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


	private boolean isRandomizedExtIntOrFloat(String methodParameterType, ChoiceNode choiceNode) {

		return choiceNode.isRandomizedValue() &&
				(JavaLanguageHelper.isExtendedIntTypeName(methodParameterType)
						|| JavaLanguageHelper.isFloatingPointTypeName(methodParameterType));
	}

	private void parseConstraintsToSat() {

		for (Constraint constraint : fConstraints) {
			parseConstraintToSat(constraint);
		}
	}

	private void parseConstraintToSat(Constraint constraint) {

		if (constraint == null) {
			return;
		}

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

			AssignmentStatement assignmentStatement = (AssignmentStatement)abstractStatement;

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
