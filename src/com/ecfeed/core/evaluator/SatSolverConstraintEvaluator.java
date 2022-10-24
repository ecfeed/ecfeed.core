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

	private ParamChoiceSets fParamChoiceSets;

	private ChoicesMappingsBucket fChoiceMappingsBucket = new ChoicesMappingsBucket();

	private ChoiceToSolverIdMappings fChoiceToSolverIdMappings = new ChoiceToSolverIdMappings();

	private List<RelationStatement> fAllRelationStatements = new ArrayList<>();
	private OldExpectedValueConstraintsData fOldExpectedValueConstraintsData = new OldExpectedValueConstraintsData();;
	private ExpectedValueAssignmentsData fExpectedValueAssignmentsData = new ExpectedValueAssignmentsData();;

	private List<MethodParameterNode> fMethodParameters;
	private Collection<Constraint> fInitConstraints;

	private EcSatSolver fSat4Solver = new EcSatSolver();

	private enum TypeOfEndpoint {
		LEFT_ENDPOINT,
		RIGHT_ENDPOINT
	}

	public SatSolverConstraintEvaluator(Collection<Constraint> constraints,	MethodNode method) {

		if (constraints == null || constraints.size() == 0) {
			return;
		}

		init(constraints, method);

		enabled = true;
	}

	private void init(Collection<Constraint> constraints, MethodNode methodX) {
		MethodNode method = null;

		if (constraints.size() > 0) {
			Set<AbstractParameterNode> tmpAbstractParameterNodes = constraints.iterator().next().getReferencedParameters();

			if (tmpAbstractParameterNodes.size() > 0) {
				List<MethodNode> tmpMethodNodes = tmpAbstractParameterNodes.iterator().next().getMethods();

				if (tmpMethodNodes.size() > 0) {
					method = tmpMethodNodes.get(0);
				}
			}
		}

		fMethodParameters = method == null ? new ArrayList<>() : method.getMethodParameters();
		fInitConstraints = constraints;

		fParamChoiceSets = new ParamChoiceSets(fMethodParameters);

		initSat4Solver();
	}

	private void initSat4Solver() {

		prepareSolversClauses(fSat4Solver);

		for (MethodParameterNode parameter : fMethodParameters) {
			if (!parameter.isExpected()) {
				EvaluatorHelper.prepareVariablesForParameter(parameter,
						fParamChoiceSets,
						fSat4Solver,
						fChoiceMappingsBucket,
						fChoiceToSolverIdMappings);
			}
		}

		fSat4Solver.packClauses();
	}

	private void prepareSolversClauses(EcSatSolver sat4Solver) {

		sat4Solver.setHasConstraints();

		collectSanitizedValues(fParamChoiceSets,fChoiceMappingsBucket);

		fAllRelationStatements = collectRelationStatements(fInitConstraints);
		LogHelperCore.log("fAllRelationStatements", fAllRelationStatements);

		sanitizeRelationStatementsWithRelation(
				fAllRelationStatements,
				fParamChoiceSets,
				fChoiceMappingsBucket);

		createInputToSanitizedMapping(fParamChoiceSets, fChoiceMappingsBucket);

		createSanitizedAndAtomicMappings(fParamChoiceSets, fChoiceMappingsBucket);

		parseConstraintsToSat(
				fInitConstraints,
				fOldExpectedValueConstraintsData,
				fExpectedValueAssignmentsData);

		LogHelperCore.log("fExpectedValConstraints", fOldExpectedValueConstraintsData);
		LogHelperCore.log("fExpectedValueAssignmentsData", fExpectedValueAssignmentsData);
	}

	@Override
	public void initialize(List<List<ChoiceNode>> input) {

		if (!fSat4Solver.hasConstraints())
			return;

		List<MethodParameterNode> methodParameters = fMethodParameters;
		assertEqualSizes(input, methodParameters);

		for (int parameterIndex = 0; parameterIndex < methodParameters.size(); parameterIndex++) {

			List<Integer> sat4Indexes = new ArrayList<>();
			MethodParameterNode methodParameterNode = methodParameters.get(parameterIndex);

			if (methodParameterNode.isExpected())
				continue;

			for (ChoiceNode choiceNode : input.get(parameterIndex)) {

				final Map<ChoiceNode, Integer> choiceNodeIntegerMap =
						fChoiceToSolverIdMappings.getEqMapping(methodParameterNode);

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

	private void assertEqualSizes(List<List<ChoiceNode>> input, List<MethodParameterNode> parameterNodes) {

		if (input.size() != parameterNodes.size()) {
			ExceptionHelper.reportRuntimeException("Input data and parameters should have the same length.");
		}
	}

	@Override
	public void excludeAssignment(List<ChoiceNode> choicesToExclude) {

		fSat4Solver.setHasConstraints();

		if (fSat4Solver.isContradicting())
			return;

		List<MethodParameterNode> methodParameterNodes = fMethodParameters;

		// TODO - what does it do ?
		for (MethodParameterNode methodParameterNode : methodParameterNodes)
			EvaluatorHelper.prepareVariablesForParameter(
					methodParameterNode,
					fParamChoiceSets,
					fSat4Solver,
					fChoiceMappingsBucket,
					fChoiceToSolverIdMappings
					);

		fSat4Solver.setNewVar();

		final int[] assumptions =
				createSolverAssumptions(
						choicesToExclude,
						fSat4Solver,
						fMethodParameters,
						fChoiceToSolverIdMappings)
				.stream()
				.map(x -> -x)
				.mapToInt(Integer::intValue)
				.toArray();

		fSat4Solver.addClause(assumptions);
	}

	@Override
	public EvaluationResult evaluate(List<ChoiceNode> valueAssignment) {

		if (!fSat4Solver.hasConstraints()) {
			return EvaluationResult.TRUE;
		}

		if (fSat4Solver.isContradicting())
			return EvaluationResult.FALSE;

		final List<Integer> assumptionsFromValues =
				createSolverAssumptions(
						valueAssignment,
						fSat4Solver,
						fMethodParameters,
						fChoiceToSolverIdMappings);

		if (fSat4Solver.isProblemSatisfiable(assumptionsFromValues)) {
			return EvaluationResult.TRUE;
		} else {
			return EvaluationResult.FALSE;
		}
	}

	@Override
	public List<ChoiceNode> setExpectedValues(List<ChoiceNode> testCaseChoices) {

		if (!fSat4Solver.hasConstraints())
			return testCaseChoices;

		final List<Integer> assumptionsFromValues =
				createSolverAssumptions(
						testCaseChoices,
						fSat4Solver,
						fMethodParameters,
						fChoiceToSolverIdMappings);

		boolean isSatisfiable = fSat4Solver.isProblemSatisfiable(assumptionsFromValues);

		if (!isSatisfiable) {
			ExceptionHelper.reportRuntimeException("Problem is unsatisfiable. Cannot adapt expected choice.");
			return null;
		}

		Set<Integer> model = new HashSet<>(Ints.asList(fSat4Solver.getModel()));

		for (Pair<Integer, ExpectedValueStatement> expectedValConstraint : fOldExpectedValueConstraintsData.getList()) {
			if (model.contains(expectedValConstraint.getFirst())) {
				expectedValConstraint.getSecond().setExpectedValues(testCaseChoices);
			}
		}

		for (Pair<Integer, AssignmentStatement> expectedValConstraint : fExpectedValueAssignmentsData.getList()) {
			if (model.contains(expectedValConstraint.getFirst())) {
				expectedValConstraint.getSecond().setExpectedValues(testCaseChoices);
			}
		}

		for (int i = 0; i < testCaseChoices.size(); i++) {
			ChoiceNode p = testCaseChoices.get(i);
			MethodParameterNode parameter = fMethodParameters.get(i);
			if (parameter.isExpected()) {
				testCaseChoices.set(i, p.makeClone());
			}
		}

		return testCaseChoices;
	}

	private void createInputToSanitizedMapping(
			ParamChoiceSets paramChoiceSets,
			ChoicesMappingsBucket choicesMappingsBucket) {

		for (MethodParameterNode method : paramChoiceSets.sanitizedGetKeySet()) {

			choicesMappingsBucket.inputToSanPut(method);

			for (ChoiceNode sanitizedChoice : paramChoiceSets.sanitizedGet(method)) {

				ChoiceNode inputChoice = choicesMappingsBucket.sanToInpGet(sanitizedChoice);

				choicesMappingsBucket.inputToSanPut(method, inputChoice, sanitizedChoice);
			}
		}
	}

	private void createSanitizedAndAtomicMappings(
			ParamChoiceSets fParamChoiceSets,
			ChoicesMappingsBucket choicesMappingsBucket) {

		for (MethodParameterNode methodParameterNode : fParamChoiceSets.sanitizedGetKeySet()) {

			// TODO - why do we need an empty set ?
			fParamChoiceSets.atomicPut(methodParameterNode, new HashSet<>());

			createSanitizedAndAtomicMappingsForParam(
					methodParameterNode,
					fParamChoiceSets,
					choicesMappingsBucket
					);
		}
	}

	private void createSanitizedAndAtomicMappingsForParam(
			MethodParameterNode methodParameterNode,
			ParamChoiceSets paramChoiceSets,
			ChoicesMappingsBucket choicesMappingsBucket) {

		//build AtomicVal <-> Sanitized Val mappings, build Param -> Atomic Val mapping
		for (ChoiceNode sanitizedChoiceNode : paramChoiceSets.sanitizedGet(methodParameterNode)) {

			if (isRandomizedExtIntOrFloat(methodParameterNode.getType(), sanitizedChoiceNode)) {

				List<ChoiceNode> interleavedChoices =
						ChoiceNodeHelper.getInterleavedValues(
								sanitizedChoiceNode, paramChoiceSets.sanitizedGetSize());

				paramChoiceSets.atomicGet(methodParameterNode).addAll(interleavedChoices);

				for (ChoiceNode interleavedChoiceNode : interleavedChoices) {
					choicesMappingsBucket.sanToAtmPut(sanitizedChoiceNode, interleavedChoiceNode);
				}

			} else {

				paramChoiceSets.atomicGet(methodParameterNode).add(sanitizedChoiceNode);
				choicesMappingsBucket.sanToAtmPut(sanitizedChoiceNode, sanitizedChoiceNode);
			}
		}
	}

	private boolean isRandomizedExtIntOrFloat(
			String methodParameterType,
			ChoiceNode choiceNode) {

		return choiceNode.isRandomizedValue() &&
				(JavaLanguageHelper.isExtendedIntTypeName(methodParameterType)
						|| JavaLanguageHelper.isFloatingPointTypeName(methodParameterType));
	}

	private void collectSanitizedValues(
			ParamChoiceSets paramChoiceSets,
			ChoicesMappingsBucket choicesMappingsBucket) {

		for (MethodParameterNode methodParameterNode : paramChoiceSets.inputGetKeySet()) {

			Set<ChoiceNode> copy = new HashSet<>(paramChoiceSets.inputGet(methodParameterNode));
			paramChoiceSets.sanitizedPut(methodParameterNode, copy);

			for (ChoiceNode choiceNode : copy) //maintaining the dependencies
				choicesMappingsBucket.sanToInpPut(choiceNode, choiceNode);
		}
	}

	private void sanitizeRelationStatementsWithRelation(
			List<RelationStatement> fAllRelationStatements,
			ParamChoiceSets paramChoiceSets,
			ChoicesMappingsBucket choicesMappingsBucket) {

		while (true) {
			Boolean anyChange = false;
			for (RelationStatement relationStatement : fAllRelationStatements) {
				if (sanitizeValsWithRelation(
						relationStatement,
						paramChoiceSets,
						choicesMappingsBucket)) {
					anyChange = true;
				}
			}
			if (!anyChange)
				break;
		}
	}

	private Boolean sanitizeValsWithRelation(
			RelationStatement relationStatement,
			ParamChoiceSets paramChoiceSets,
			ChoicesMappingsBucket choicesMappingsBucket) {

		IStatementCondition condition = relationStatement.getCondition();
		if (condition instanceof LabelCondition)
			return false;

		MethodParameterNode lParam = relationStatement.getLeftParameter();

		if (!JavaLanguageHelper.isExtendedIntTypeName(lParam.getType())
				&& !JavaLanguageHelper.isFloatingPointTypeName(lParam.getType()))
			return false;

		List<ChoiceNode> allLVals = new ArrayList<>(paramChoiceSets.sanitizedGet(lParam));

		if (condition instanceof ParameterCondition) {
			MethodParameterNode rParam = ((ParameterCondition) condition).getRightParameterNode();
			List<ChoiceNode> allRVals = new ArrayList<>(paramChoiceSets.sanitizedGet(rParam));

			boolean anyChange = false;
			List<ChoiceNode> allLValsCopy = new ArrayList<>(allLVals);
			for (ChoiceNode it : allRVals) {
				Pair<Boolean, List<ChoiceNode>> changeResult =
						splitListWithChoiceNode(allLValsCopy, it, choicesMappingsBucket);

				anyChange = anyChange || changeResult.getFirst();
				allLValsCopy = changeResult.getSecond();
			}

			List<ChoiceNode> allRValsCopy = new ArrayList<>(allRVals);
			for (ChoiceNode it : allLVals) {
				Pair<Boolean, List<ChoiceNode>> changeResult =
						splitListWithChoiceNode(allRValsCopy, it, choicesMappingsBucket);
				anyChange = anyChange || changeResult.getFirst();
				allRValsCopy = changeResult.getSecond();
			}

			paramChoiceSets.sanitizedPut(lParam, new HashSet<>(allLValsCopy));

			paramChoiceSets.sanitizedPut(rParam, new HashSet<>(allRValsCopy));

			return anyChange;
		}
		if ((condition instanceof ValueCondition) || (condition instanceof ChoiceCondition)) {
			ChoiceNode it;

			if (condition instanceof ValueCondition) {
				String val = ((ValueCondition) condition).getRightValue();

				it = allLVals.get(0).makeCloneUnlink();
				it.setRandomizedValue(false);
				it.setValueString(val);
			} else {
				it = ((ChoiceCondition) condition).getRightChoice();
			}

			Pair<Boolean, List<ChoiceNode>> changeResult =
					splitListWithChoiceNode(allLVals, it, choicesMappingsBucket);

			paramChoiceSets.sanitizedPut(lParam, new HashSet<>(changeResult.getSecond()));
			return changeResult.getFirst();
		}

		ExceptionHelper.reportRuntimeException("Invalid condition type.");
		return true;
	}

	private Pair<Boolean, List<ChoiceNode>> splitListWithChoiceNode(
			List<ChoiceNode> toSplit,
			ChoiceNode val,
			ChoicesMappingsBucket choicesMappingsBucket) {

		ChoiceNode start, end;
		if (val.isRandomizedValue()) {
			Pair<ChoiceNode, ChoiceNode> startEnd = ChoiceNodeHelper.rangeSplit(val);
			start = startEnd.getFirst();
			end = startEnd.getSecond();
		} else {
			start = val;
			end = val;
		}
		Pair<Boolean, List<ChoiceNode>> changeResultLeft =
				splitListByValue(
						toSplit,
						start,
						TypeOfEndpoint.LEFT_ENDPOINT,
						choicesMappingsBucket);

		Pair<Boolean, List<ChoiceNode>> changeResultRight =
				splitListByValue(
						changeResultLeft.getSecond(),
						end,
						TypeOfEndpoint.RIGHT_ENDPOINT,
						choicesMappingsBucket);

		return new Pair<>(changeResultLeft.getFirst() || changeResultRight.getFirst(), changeResultRight.getSecond());
	}

	private Pair<Boolean, List<ChoiceNode>> splitListByValue(
			List<ChoiceNode> toSplit,
			ChoiceNode val,
			TypeOfEndpoint type,
			ChoicesMappingsBucket choicesMappingsBucket) {

		Boolean anyChange = false;
		List<ChoiceNode> newList = new ArrayList<>();
		for (ChoiceNode it : toSplit)
			if (!it.isRandomizedValue())
				newList.add(it);
			else {
				Pair<ChoiceNode, ChoiceNode> startEnd = ChoiceNodeHelper.rangeSplit(it);
				ChoiceNode start = startEnd.getFirst();
				ChoiceNode end = startEnd.getSecond();

				ChoiceNode val1, val2;
				val1 = start.makeCloneUnlink();
				val2 = end.makeCloneUnlink();
				val1.setValueString(ChoiceNodeHelper.convertValueToNumeric(val).getValueString());
				val2.setValueString(ChoiceNodeHelper.convertValueToNumeric(val).getValueString());
				if (JavaLanguageHelper.isExtendedIntTypeName(start.getParameter().getType())
						&& JavaLanguageHelper.isFloatingPointTypeName(val.getParameter().getType())) {
					val1 = ChoiceNodeHelper.roundValueDown(val1);
					val2 = ChoiceNodeHelper.roundValueUp(val2);
				}
				if (new ChoiceNodeComparator().compare(val1, val2) == 0) {
					if (type == TypeOfEndpoint.LEFT_ENDPOINT)
						val1 = ChoiceNodeHelper.getPrecedingValue(val1);
					else //RIGHT_ENDPOINT
						val2 = ChoiceNodeHelper.followingVal(val2);
				}

				if (new ChoiceNodeComparator().compare(val1, val2) == 0) { //only happens if one was too extreme to be further moved, as in Long.MAX_VALUE or so
					newList.add(it);
					continue;
				}
				int cmp1 = new ChoiceNodeComparator().compare(start, val1);
				int cmp2 = new ChoiceNodeComparator().compare(val2, end);
				if (cmp1 > 0 || cmp2 > 0) {
					newList.add(it);
					continue;
				}

				ChoiceNode it1, it2;
				if (cmp1 < 0)
					it1 = ChoiceNodeHelper.toRangeFromFirst(start, val1);
				else
					it1 = start;
				if (cmp2 < 0)
					it2 = ChoiceNodeHelper.toRangeFromSecond(val2, end);
				else
					it2 = end;

				anyChange = true;

				// TODO - side effect - extract
				choicesMappingsBucket.sanToInpPut(it1, choicesMappingsBucket.sanToInpGet(it));
				choicesMappingsBucket.sanToInpPut(it2, choicesMappingsBucket.sanToInpGet(it));
				newList.add(it1);
				newList.add(it2);
			}

		return new Pair<>(anyChange, newList);
	}

	private List<RelationStatement> collectRelationStatements(
			Collection<Constraint> initConstraints) {

		List<RelationStatement> result = new ArrayList<>();
		;

		for (Constraint constraint : initConstraints) {

			if (constraint.getType() == ConstraintType.ASSIGNMENT)  {
				continue;
			}
			collectRelationStatements(constraint, result);
		}

		return result;
	}

	private void collectRelationStatements(
			Constraint constraint,
			List<RelationStatement> inOutRelationStatements) {

		if (constraint == null) {
			return;
		}

		AbstractStatement precondition = constraint.getPrecondition(), postcondition = constraint.getPostcondition();
		if (postcondition instanceof ExpectedValueStatement) {
			try {
				precondition.accept(new CollectingStatementVisitor(inOutRelationStatements));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				precondition.accept(new CollectingStatementVisitor(inOutRelationStatements));
				postcondition.accept(new CollectingStatementVisitor(inOutRelationStatements));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void parseConstraintsToSat(
			Collection<Constraint> initConstraints,
			OldExpectedValueConstraintsData outExpectedValueConstraintsData,
			ExpectedValueAssignmentsData outExpectedValueAssignmentsData) {

		for (Constraint constraint : initConstraints) {
			parseConstraintToSat(
					constraint,
					outExpectedValueConstraintsData,
					outExpectedValueAssignmentsData);
		}
	}

	private void parseConstraintToSat(
			Constraint constraint,
			OldExpectedValueConstraintsData inOutExpectedValConstraints,
			ExpectedValueAssignmentsData inOutExpectedValueAssignmentsData) {

		if (constraint == null) {
			return;
		}

		AbstractStatement precondition = constraint.getPrecondition();
		AbstractStatement postcondition = constraint.getPostcondition();

		if (constraint.getType() == ConstraintType.ASSIGNMENT) {

			addAssignmentStatementsToAssignmentsTable(
					precondition, postcondition, inOutExpectedValueAssignmentsData);

			return;
		}

		if (postcondition instanceof ExpectedValueStatement) {

			addExpectedValueStatementToAssignmentsTable(
					precondition, (ExpectedValueStatement)postcondition, inOutExpectedValConstraints);

			return;
		}

		addFilteringConstraintToSatSolver(precondition, postcondition);
	}

	private void addAssignmentStatementsToAssignmentsTable(
			AbstractStatement precondition,
			AbstractStatement postcondition,
			ExpectedValueAssignmentsData inOutExpectedValueAssignmentsData) {

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

			addOneStatementToAssignmetsTable(precondition, assignmentStatement, inOutExpectedValueAssignmentsData);
		}
	}

	private void addOneStatementToAssignmetsTable(
			AbstractStatement precondition,
			AssignmentStatement assignmentStatement,
			ExpectedValueAssignmentsData inOutExpectedValueAssignmentsData) {

		try {
			Integer preconditionId =
					(Integer) precondition.accept(
							new ParseConstraintToSATVisitor(
									fMethodParameters,
									fSat4Solver,
									fParamChoiceSets,
									fChoiceMappingsBucket,
									fChoiceToSolverIdMappings));

			inOutExpectedValueAssignmentsData.add(new Pair<>(preconditionId, assignmentStatement));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addExpectedValueStatementToAssignmentsTable(
			AbstractStatement precondition,
			ExpectedValueStatement expectedValueStatement,
			OldExpectedValueConstraintsData inOutExpectedValConstraints) {

		try {
			Integer preconditionId =
					(Integer) precondition.accept(
							new ParseConstraintToSATVisitor(
									fMethodParameters,
									fSat4Solver,
									fParamChoiceSets,
									fChoiceMappingsBucket,
									fChoiceToSolverIdMappings));

			inOutExpectedValConstraints.add(new Pair<>(preconditionId, expectedValueStatement));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addFilteringConstraintToSatSolver(
			AbstractStatement precondition,
			AbstractStatement postcondition) {

		Integer preconditionId = null;
		Integer postconditionId = null;

		try {
			preconditionId = (Integer) precondition.accept(
					new ParseConstraintToSATVisitor(
							fMethodParameters,
							fSat4Solver,
							fParamChoiceSets,
							fChoiceMappingsBucket,
							fChoiceToSolverIdMappings));

			postconditionId =
					(Integer) postcondition.accept(
							new ParseConstraintToSATVisitor(
									fMethodParameters,
									fSat4Solver,
									fParamChoiceSets,
									fChoiceMappingsBucket,
									fChoiceToSolverIdMappings));
		} catch (Exception e) {
			e.printStackTrace();
		}

		fSat4Solver.addSat4Clause(new int[]{-preconditionId, postconditionId});
	}

	private List<Integer> createSolverAssumptions(
			List<ChoiceNode> currentArgumentAssignments, // main input parameter
			EcSatSolver satSolver,
			List<MethodParameterNode> methodParameters,
			ChoiceToSolverIdMappings choiceToSolverIdMappings) {

		if (!satSolver.hasConstraints())
			return new ArrayList<>();

		List<MethodParameterNode> methodParameterNodes = methodParameters;

		List<Integer> assumptions = new ArrayList<>();

		if (currentArgumentAssignments.size() != methodParameterNodes.size()) {
			ExceptionHelper.reportRuntimeException("Value assignment list and parameters list should be of equal size.");
			return null;
		}

		for (int i = 0; i < methodParameterNodes.size(); i++) {

			MethodParameterNode methodParameterNode = methodParameterNodes.get(i);
			ChoiceNode choiceAssignedToParameter = currentArgumentAssignments.get(i);

			if (choiceAssignedToParameter != null) {

				final Map<ChoiceNode, Integer> choiceNodeIntegerMap =
						choiceToSolverIdMappings.eqGet(methodParameterNode);

				if (choiceNodeIntegerMap == null)
					continue; //no constraint on this method parameter

				Integer solverId =
						choiceNodeIntegerMap.get(
								choiceAssignedToParameter.getOrigChoiceNode());

				assumptions.add(solverId);
			}
		}

		return assumptions;
	}

}
