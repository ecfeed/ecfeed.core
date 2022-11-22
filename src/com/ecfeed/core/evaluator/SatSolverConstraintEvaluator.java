package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.*;
import com.google.common.primitives.Ints;

import java.util.*;

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

	private List<BasicParameterNode> fParameters = new ArrayList<>();
	private Collection<Constraint> fConstraints;

	private ChoiceNodeComparator fChoiceComparator = new ChoiceNodeComparator();

	private enum TypeOfEndpoint {
		LEFT_ENDPOINT,
		RIGHT_ENDPOINT
	}

	public SatSolverConstraintEvaluator(Collection<Constraint> constraints,	MethodNode method) {

		if (constraints == null || constraints.size() == 0) {
			return;
		}

		initMethod(constraints);
	}

	private void initMethod(Collection<Constraint> constraints) {
		Set<MethodNode> methods = initGetMethodNode(constraints);

		if (methods.size() == 0) {
			return;
		}

		fConstraints = constraints;

		for (MethodNode method : methods) {
			fParameters.addAll(convertToBasicParameters(method));
		}

		fParameterChoices.update(fParameters);
		fChoiceMappings.updateSanitizedToInput(fParameterChoices.getInputChoices());

		initSat4Solver();

		enabled = true;
	}

	private List<BasicParameterNode> convertToBasicParameters(MethodNode method) {
		
		List<BasicParameterNode> result = new ArrayList<>();
		
		List<AbstractParameterNode> methodParameters = method.getMethodParameters();
		
		for (AbstractParameterNode abstractParameterNode : methodParameters) {
			
			BasicParameterNode basicParameterNode = (BasicParameterNode) abstractParameterNode;
			result.add(basicParameterNode);
		}
		
		return result;
	}

	private Set<MethodNode> initGetMethodNode(Collection<Constraint> constraints) {
		Set<MethodNode> methods = new HashSet<>();

		for (Constraint constraint : constraints) {
			methods.addAll(ConstraintHelper.getMethods(constraint));
		}

		return methods;
	}

	private void initSat4Solver() {

		prepareSolversClauses();

		for (BasicParameterNode parameter : fParameters) {
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

		sanitizeRelationStatements();

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

//	Adjust relation choices. it is only needed for numerical comparison.
	private void sanitizeRelationStatements() {
		boolean change = true;

		while (change) {
			change = false;

			for (RelationStatement relationStatement : fRelationStatements) {
				if (sanitizeRelationStatement(relationStatement)) {
					change = true;
				}
			}
		}
	}

	private boolean sanitizeRelationStatement(RelationStatement relation) {
		IStatementCondition condition = relation.getCondition();

// Labels are not numerical. This is the simplest case, we can return from the function.
		if (condition instanceof LabelCondition) {
			return false;
		}

		BasicParameterNode leftParameter = relation.getLeftParameter();

// The parameter is not numerical, no adjustment is needed. We can return from the function.
		if (!isNumericalType(leftParameter)) {
			return false;
		}

		if (condition instanceof ParameterCondition) {
			return sanitizeRelationStatementConditionParameter(condition, leftParameter);
		}

		if (condition instanceof ValueCondition) {
			return sanitizeRelationStatementConditionValue(condition, leftParameter);
		}

		if (condition instanceof ChoiceCondition) {
			return sanitizeRelationStatementConditionChoice(condition, leftParameter);
		}

		ExceptionHelper.reportRuntimeException("Invalid condition type.");

		return true;
	}

	private boolean sanitizeRelationStatementConditionParameter(IStatementCondition condition, BasicParameterNode leftParameter) {
		BasicParameterNode rightParameter = ((ParameterCondition) condition).getRightParameterNode();

		List<ChoiceNode> leftParameterChoices = new ArrayList<>(fParameterChoices.getSanitized(leftParameter));
		List<ChoiceNode> rightParameterChoices = new ArrayList<>(fParameterChoices.getSanitized(rightParameter));

		boolean change = false;

		List<ChoiceNode> leftParameterChoicesCopy = new ArrayList<>(leftParameterChoices);

		for (ChoiceNode choice : rightParameterChoices) {
			Pair<Boolean, List<ChoiceNode>> changeResult = splitChoiceRangeByChoiceRef(leftParameterChoicesCopy, choice);

			change = change || changeResult.getFirst();
			leftParameterChoicesCopy = changeResult.getSecond();
		}

		List<ChoiceNode> rightParameterChoicesCopy = new ArrayList<>(rightParameterChoices);

		for (ChoiceNode choice : leftParameterChoices) {
			Pair<Boolean, List<ChoiceNode>> changeResult = splitChoiceRangeByChoiceRef(rightParameterChoicesCopy, choice);

			change = change || changeResult.getFirst();
			rightParameterChoicesCopy = changeResult.getSecond();
		}

		fParameterChoices.putSanitized(leftParameter, new HashSet<>(leftParameterChoicesCopy));
		fParameterChoices.putSanitized(rightParameter, new HashSet<>(rightParameterChoicesCopy));

		return change;
	}

	private boolean sanitizeRelationStatementConditionValue(IStatementCondition condition, BasicParameterNode leftParameter) {
		List<ChoiceNode> leftParameterChoices = new ArrayList<>(fParameterChoices.getSanitized(leftParameter));

		String value = ((ValueCondition) condition).getRightValue();

		ChoiceNode choice = leftParameterChoices.get(0).makeCloneUnlink();
		choice.setRandomizedValue(false);
		choice.setValueString(value);

		Pair<Boolean, List<ChoiceNode>> changeResult = splitChoiceRangeByChoiceRef(leftParameterChoices, choice);

		fParameterChoices.putSanitized(leftParameter, new HashSet<>(changeResult.getSecond()));

		return changeResult.getFirst();
	}

	private boolean sanitizeRelationStatementConditionChoice(IStatementCondition condition, BasicParameterNode leftParameter) {
		List<ChoiceNode> leftParameterChoices = new ArrayList<>(fParameterChoices.getSanitized(leftParameter));

		ChoiceNode choice = ((ChoiceCondition) condition).getRightChoice();

		Pair<Boolean, List<ChoiceNode>> changeResult = splitChoiceRangeByChoiceRef(leftParameterChoices, choice);

		fParameterChoices.putSanitized(leftParameter, new HashSet<>(changeResult.getSecond()));

		return changeResult.getFirst();
	}

// Split single range choices into two range choices using the value of the reference choice (in case of an intersection).
// If the reference choice is also a range choice, then the two boundary values are used separately.
	private Pair<Boolean, List<ChoiceNode>> splitChoiceRangeByChoiceRef(List<ChoiceNode> parameterChoices, ChoiceNode choiceRef) {
		ChoiceNode choiceStart;
		ChoiceNode choiceEnd;

		if (choiceRef.isRandomizedValue()) {
			Pair<ChoiceNode, ChoiceNode> choicePair = ChoiceNodeHelper.rangeSplit(choiceRef);
			choiceStart = choicePair.getFirst();
			choiceEnd = choicePair.getSecond();
		} else {
			choiceStart = choiceRef;
			choiceEnd = choiceRef;
		}

// Validate choices against the lower boundary of the referenced choice.
		Pair<Boolean, List<ChoiceNode>> changeResultLeft = splitChoiceRangeByChoiceRefBorderType(parameterChoices, choiceStart, TypeOfEndpoint.LEFT_ENDPOINT);
// Validate choices against the upper boundary of the referenced choice.
		Pair<Boolean, List<ChoiceNode>> changeResultRight =	splitChoiceRangeByChoiceRefBorderType(changeResultLeft.getSecond(), choiceEnd, TypeOfEndpoint.RIGHT_ENDPOINT);

		return new Pair<>(changeResultLeft.getFirst() || changeResultRight.getFirst(), changeResultRight.getSecond());
	}

	private Pair<Boolean, List<ChoiceNode>> splitChoiceRangeByChoiceRefBorderType(List<ChoiceNode> parameterChoices, ChoiceNode choiceRef, TypeOfEndpoint type) {
// A list of corrected choices.
		List<ChoiceNode> updatedChoices = new ArrayList<>();
		boolean change = false;

		for (ChoiceNode choice : parameterChoices) {
// The choice is not randomized, no adjustment is necessary.
			if (!choice.isRandomizedValue()) {
				updatedChoices.add(choice);
			} else {
// Divide the provided choice into two boundary choices (lower, upper).
				Pair<ChoiceNode, ChoiceNode> choicePair = ChoiceNodeHelper.rangeSplit(choice);
				ChoiceNode choiceBorderLeft = choicePair.getFirst();
				ChoiceNode choiceBorderRight = choicePair.getSecond();

				ChoiceNode choiceRefBorderLeft = choiceBorderLeft.makeCloneUnlink();
				ChoiceNode choiceRefBorderRight = choiceBorderRight.makeCloneUnlink();
// Change value of the two choices to the value of the reference choice.
				choiceUpdateValueString(choiceRef, choiceRefBorderLeft, choiceRefBorderRight);
// Alter values of the two choices (the reference value should be between them).
				choiceRound(choiceBorderLeft, choiceRef, choiceRefBorderLeft, choiceRefBorderRight);
				choiceShiftEpsilon(choiceRefBorderLeft, choiceRefBorderRight, type);
// This should not happen (there should be always a small gap between values). However, this can happen if values are extreme.
// In this cese, the error is definitely lower than epsilon.
				if (fChoiceComparator.compare(choiceRefBorderLeft, choiceRefBorderRight) == 0) {
					updatedChoices.add(choice);
					continue;
				}
// Comparison of the reference choice (denoted by mutable choices) with the provided range.
// The left value (range) compared to the reference choice (left border).
				int cmpBorderLeft = fChoiceComparator.compare(choiceBorderLeft, choiceRefBorderLeft);
// The right value (range) compared to the reference choice (right border).
				int cmpBorderRight = fChoiceComparator.compare(choiceBorderRight, choiceRefBorderRight);
// The reference choice might exist withing the epsilon range. However, it is very unlikely (we shouldn't care about that).
				if (cmpBorderRight < 0 || cmpBorderLeft > 0) {
					updatedChoices.add(choice);
					continue;
				}
// Condition: left value (range) is lower than the reference choice (left border). If true - make range from the left value (range) to the reference (left border).
// Otherwise, the reference is exactly on the border or outside the range.
				ChoiceNode sanitizedBorderLeft = cmpBorderLeft < 0 ? ChoiceNodeHelper.toRangeFromFirst(choiceBorderLeft, choiceRefBorderLeft) : choiceBorderLeft;
// Condition: right value (range) is greater than the reference choice (right border). If true - make range from the reference (right border) to the right value.
// Otherwise, the reference is exactly on the border or outside the range.
				ChoiceNode sanitizedBorderRight = cmpBorderRight > 0 ? ChoiceNodeHelper.toRangeFromSecond(choiceRefBorderRight, choiceBorderRight) : choiceBorderRight;
// Map sanitized values.
// For example: ref = 5, choice = 1:9. The choice is split into two values, e.g. 1:5 and 6:9.
// For example: ref = 9.0, choice = 9.0:10.0. The choice is split into two values, i.e. 9.0 and 9.00001:10.0.
// If the type is 'LEFT_ENDPOINT', then the reference value is included in the right range.
// If the type is 'RIGHT_VALUE', then the reference value is included in the left range.
				fChoiceMappings.putSanitizedToInput(sanitizedBorderLeft, fChoiceMappings.getSanitizedToInput(choice));
				fChoiceMappings.putSanitizedToInput(sanitizedBorderRight, fChoiceMappings.getSanitizedToInput(choice));

				updatedChoices.add(sanitizedBorderLeft);
				updatedChoices.add(sanitizedBorderRight);

				change = true;
			}
		}

		return new Pair<>(change, updatedChoices);
	}

	private boolean isFloatType(BasicParameterNode parameter) {

		return JavaLanguageHelper.isFloatingPointTypeName(parameter.getType());
	}

	private boolean isIntegerType(BasicParameterNode parameter) {

		return JavaLanguageHelper.isExtendedIntTypeName(parameter.getType());
	}

	private boolean isNumericalType(BasicParameterNode parameter) {

		if (isIntegerType(parameter)) {
			return true;
		}

		if (isFloatType(parameter)) {
			return true;
		}

		return false;
	}

	private void choiceRound(ChoiceNode choiceBorderLeft, ChoiceNode choiceRef, ChoiceNode choiceRefBorderLeft, ChoiceNode choiceRefBorderRight) {
// If the reference choice type is float and provided choice type is integer, the float value will be positioned between two integer values.
// If the reference choice type is integer, no adjustment is needed (all values are the same and provided choices do not have to be converted from float to integer).
		if (isIntegerType(choiceBorderLeft.getParameter()) && isFloatType(choiceRef.getParameter())) {
// Modify the lower boundary.
			ChoiceNodeHelper.roundValueDown(choiceRefBorderLeft);
// Modify the upper boundary.
			ChoiceNodeHelper.roundValueUp(choiceRefBorderRight);
		}
	}

	private void choiceShiftEpsilon(ChoiceNode choiceRefBorderLeft, ChoiceNode choiceRefBorderRight, TypeOfEndpoint type) {
// If the values are identical, make a small gap between them.
// The reference choice is always in the (closed) range defined by these two values.
// This procedure is mostly invoked when all choices are of integer type (rounding was not needed, are values are equal).
		if (fChoiceComparator.compare(choiceRefBorderLeft, choiceRefBorderRight) == 0) {
			if (type == TypeOfEndpoint.LEFT_ENDPOINT) {
// Shift left the left choice. The reference choice is at the end of the range.
				ChoiceNodeHelper.getPrecedingValue(choiceRefBorderLeft);
			} else {
// Shift right the right choice. The reference choice is at the beginning of the range.
				ChoiceNodeHelper.getFollowingVal(choiceRefBorderRight);
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

		for (BasicParameterNode parameter : fParameterChoices.getKeySetSanitized()) {

			fChoiceMappings.putInputToSanitized(parameter);

			for (ChoiceNode choiceSanitized : fParameterChoices.getSanitized(parameter)) {

				ChoiceNode choiceInput = fChoiceMappings.getSanitizedToInput(choiceSanitized);

				fChoiceMappings.putInputToSanitized(parameter, choiceInput, choiceSanitized);
			}
		}
	}

	private void createSanitizedToAtomicMapping() {

		for (BasicParameterNode parameter : fParameterChoices.getKeySetSanitized()) {

			fParameterChoices.putAtomic(parameter);

			createSanitizedToAtomicMappings(parameter);
		}
	}

	private void createSanitizedToAtomicMappings(BasicParameterNode parameter) {

// Build AtomicVal <-> SanitizedVal mappings ;  Build Param -> AtomicVal mappings.
		for (ChoiceNode choiceSanitized : fParameterChoices.getSanitized(parameter)) {
			if (isRandomizedNumericalType(parameter, choiceSanitized)) {
				List<ChoiceNode> choicesInterleaved = ChoiceNodeHelper.getInterleavedValues(choiceSanitized, fParameterChoices.getSizeSanitized());

				fParameterChoices.putAtomic(parameter, choicesInterleaved);

				for (ChoiceNode interleavedChoice : choicesInterleaved) {
					fChoiceMappings.putSanitizedToAtomic(choiceSanitized, interleavedChoice);
				}
			} else {
				fParameterChoices.putAtomic(parameter, choiceSanitized);
				fChoiceMappings.putSanitizedToAtomic(choiceSanitized, choiceSanitized);
			}
		}
	}

	private boolean isRandomizedNumericalType(BasicParameterNode parameter, ChoiceNode choice) {

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
			BasicParameterNode basicParameterNode = fParameters.get(parameterIndex);

			if (basicParameterNode.isExpected()) {
				continue;
			}

			for (ChoiceNode choiceNode : input.get(parameterIndex)) {

				final Map<ChoiceNode, Integer> choiceNodeIntegerMap = fChoiceToSolverIdMappings.getEqMapping(basicParameterNode);

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
		for (BasicParameterNode basicParameterNode : fParameters)
			EvaluatorHelper.prepareVariablesForParameter(
					basicParameterNode,
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
			BasicParameterNode parameter = fParameters.get(i);
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

			BasicParameterNode basicParameterNode = fParameters.get(i);
			ChoiceNode choiceAssignedToParameter = currentArgumentAssignments.get(i);

			if (choiceAssignedToParameter != null) {

				final Map<ChoiceNode, Integer> choiceNodeIntegerMap = fChoiceToSolverIdMappings.eqGet(basicParameterNode);

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
