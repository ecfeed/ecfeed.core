package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.*;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.utils.*;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import com.ecfeed.core.model.MethodParameterNode;


import java.util.*;
import java.util.List;

public class SatSolverConstraintEvaluator implements IConstraintEvaluator<ChoiceNode> {

    ParamChoiceSets fParamChoiceSets;

//    private SimpleChoiceMapping fAtomicToSanitizedMappings;
    private ChoicesMappingsBucket fChoiceMappingsBucket;

    ChoiceToSolverIdMappings fChoiceToSolverIdMappings;

    private List<RelationStatement> fAllRelationStatements;
    private ExpectedConstraintsData fExpectedValConstraints;

    private MethodNode fMethodNode;
    private EcSatSolver fSat4Solver;

    static final int fLogLevel = 0;

    private enum TypeOfEndpoint {
        LEFT_ENDPOINT,
        RIGHT_ENDPOINT
    }

    public SatSolverConstraintEvaluator(Collection<Constraint> initConstraints, MethodNode method) {

        fMethodNode = method;

        fChoiceToSolverIdMappings = new ChoiceToSolverIdMappings();
        fParamChoiceSets = new ParamChoiceSets(method);

        fExpectedValConstraints = new ExpectedConstraintsData();

        fAllRelationStatements = new ArrayList<>();
        fChoiceMappingsBucket = new ChoicesMappingsBucket();


        prepareSat4Solver(initConstraints, method);
    }

    private void prepareSat4Solver(Collection<Constraint> initConstraints, MethodNode method) {

        if (fMethodNode == null && !initConstraints.isEmpty()) {
            ExceptionHelper.reportRuntimeException("Constraints without method.");
        }

        fSat4Solver = new EcSatSolver();

        if (initConstraints != null && !initConstraints.isEmpty()) {
            prepareSolversClauses(initConstraints, fSat4Solver, method);
        }

        fSat4Solver.packClauses();
    }

    private void prepareSolversClauses(Collection<Constraint> initConstraints, EcSatSolver sat4Solver, MethodNode method) {

        sat4Solver.setHasConstraints();


        collectSanitizedValues(fParamChoiceSets,fChoiceMappingsBucket);

//        Sat4Logger.log("fSanitizedToInputMappings", fSanitizedToInputMappings, 1, fLogLevel);


        fAllRelationStatements = collectRelationStatements(initConstraints);
        Sat4Logger.log("fAllRelationStatements", fAllRelationStatements, 1, fLogLevel);

        sanitizeRelationStatementsWithRelation(
                fAllRelationStatements,
                fParamChoiceSets,
                fChoiceMappingsBucket);
//        Sat4Logger.log("fSanitizedToInputMappings after sanitize", fSanitizedToInputMappings, 1, fLogLevel);


        createInputToSanitizedMapping(fParamChoiceSets, fChoiceMappingsBucket);
//        Sat4Logger.log("fArgInputValToSanitizedVal", fArgInputValToSanitizedVal, 1, fLogLevel);


        createSanitizedAndAtomicMappings(fParamChoiceSets, fChoiceMappingsBucket);
//        Sat4Logger.log("fAtomicToSanitizedMappings", fAtomicToSanitizedMappings, 1, fLogLevel);
//        Sat4Logger.log("fSanitizedValToAtomicVal", fSanitizedValToAtomicVal, 1, fLogLevel);

        parseConstraintsToSat(
                initConstraints,
                fExpectedValConstraints,
                sat4Solver);



        for(MethodParameterNode parameterNode : method.getMethodParameters())
            if(! parameterNode.isExpected())
                EvaluatorHelper.prepareVariablesForParameter(parameterNode,
                        fParamChoiceSets,
                        sat4Solver,
                        fChoiceMappingsBucket,
                        fChoiceToSolverIdMappings);

        Sat4Logger.log("fExpectedValConstraints", fExpectedValConstraints, 1, fLogLevel);
    }

    @Override
    public void initialize(List<List<ChoiceNode>> input) {

        if (!fSat4Solver.hasConstraints())
            return;

        List<MethodParameterNode> methodParameters = fMethodNode.getMethodParameters();
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

    private void assertEqualSizes(
            List<List<ChoiceNode>> input, List<MethodParameterNode> parameterNodes) {

        if (input.size() != parameterNodes.size()) {
            ExceptionHelper.reportRuntimeException("Input data and parameters should have the same length.");
        }
    }

    @Override
    public void excludeAssignment(List<ChoiceNode> choicesToExclude) {

        if (!fSat4Solver.hasConstraints())
            return;

        if (fSat4Solver.isContradicting())
            return;

        List<MethodParameterNode> methodParameterNodes = fMethodNode.getMethodParameters();

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
                        fMethodNode,
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
                        fMethodNode,
                        fChoiceToSolverIdMappings);

        if (fSat4Solver.isProblemSatisfiable(assumptionsFromValues)) {
            return EvaluationResult.TRUE;
        } else {
            return EvaluationResult.FALSE;
        }
    }

    @Override
    public List<ChoiceNode> adapt(List<ChoiceNode> valueAssignment) { // TODO - rename adapt to adaptExpectedChoices

        if (!fSat4Solver.hasConstraints())
            return valueAssignment;

        final List<Integer> assumptionsFromValues =
                createSolverAssumptions(
                        valueAssignment,
                        fSat4Solver,
                        fMethodNode,
                        fChoiceToSolverIdMappings);

        boolean isSatisfiable = fSat4Solver.isProblemSatisfiable(assumptionsFromValues);

        if (!isSatisfiable) {
            ExceptionHelper.reportRuntimeException("Problem is unsatisfiable. Cannot adapt expected choice.");
            return null;
        }

        Set<Integer> model = new HashSet<>(Ints.asList(fSat4Solver.getModel()));

        for (Pair<Integer, ExpectedValueStatement> expectedValConstraint : fExpectedValConstraints.getList()) {
            if (model.contains(expectedValConstraint.getFirst())) {
                expectedValConstraint.getSecond().adapt(valueAssignment);
            }
        }

        for (int i = 0; i < valueAssignment.size(); i++) {
            ChoiceNode p = valueAssignment.get(i);
            MethodParameterNode parameter = fMethodNode.getMethodParameters().get(i);
            if (parameter.isExpected()) {
                valueAssignment.set(i, p.makeClone());
            }
        }

        return valueAssignment;
    }

    private static void createInputToSanitizedMapping(
            ParamChoiceSets paramChoiceSets,
            ChoicesMappingsBucket choicesMappingsBucket) {

        for (MethodParameterNode methodParameterNode : paramChoiceSets.sanitizedGetKeySet()) {

            choicesMappingsBucket.inputToSanPut(methodParameterNode, HashMultimap.create());

            for (ChoiceNode sanitizedChoice : paramChoiceSets.sainitizedGet(methodParameterNode)) {

                ChoiceNode inputChoice = choicesMappingsBucket.sanToInpGet(sanitizedChoice);

                choicesMappingsBucket.inputToSanGet(methodParameterNode).
                        put(inputChoice, sanitizedChoice);
            }
        }
    }

    private static void createSanitizedAndAtomicMappings(
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

    private static void createSanitizedAndAtomicMappingsForParam(
            MethodParameterNode methodParameterNode,
            ParamChoiceSets paramChoiceSets,
            ChoicesMappingsBucket choicesMappingsBucket) {

        //build AtomicVal <-> Sanitized Val mappings, build Param -> Atomic Val mapping
        for (ChoiceNode sanitizedChoiceNode : paramChoiceSets.sainitizedGet(methodParameterNode)) {

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

    private static boolean isRandomizedExtIntOrFloat(
            String methodParameterType,
            ChoiceNode choiceNode) {

        return choiceNode.isRandomizedValue() &&
                (JavaTypeHelper.isExtendedIntTypeName(methodParameterType)
                        || JavaTypeHelper.isFloatingPointTypeName(methodParameterType));
    }

    private static void collectSanitizedValues(
            ParamChoiceSets paramChoiceSets,
            ChoicesMappingsBucket choicesMappingsBucket) {

        for (MethodParameterNode methodParameterNode : paramChoiceSets.inputGetKeySet()) {

            Set<ChoiceNode> copy = new HashSet<>(paramChoiceSets.inputGet(methodParameterNode));
            paramChoiceSets.sainitizedPut(methodParameterNode, copy);

            for (ChoiceNode choiceNode : copy) //maintaining the dependencies
                choicesMappingsBucket.sanToInpPut(choiceNode, choiceNode);
        }
    }

    private static void sanitizeRelationStatementsWithRelation(
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

    private static Boolean sanitizeValsWithRelation(
            RelationStatement relationStatement,
            ParamChoiceSets paramChoiceSets,
            ChoicesMappingsBucket choicesMappingsBucket) {

        IStatementCondition condition = relationStatement.getCondition();
        if (condition instanceof LabelCondition)
            return false;

        MethodParameterNode lParam = relationStatement.getLeftParameter();

        if (!JavaTypeHelper.isExtendedIntTypeName(lParam.getType())
                && !JavaTypeHelper.isFloatingPointTypeName(lParam.getType()))
            return false;

        List<ChoiceNode> allLVals = new ArrayList<>(paramChoiceSets.sainitizedGet(lParam));


        if (condition instanceof ParameterCondition) {
            MethodParameterNode rParam = ((ParameterCondition) condition).getRightParameterNode();
            List<ChoiceNode> allRVals = new ArrayList<>(paramChoiceSets.sainitizedGet(rParam));

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

            paramChoiceSets.sainitizedPut(lParam, new HashSet<>(allLValsCopy));

            paramChoiceSets.sainitizedPut(rParam, new HashSet<>(allRValsCopy));

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

            paramChoiceSets.sainitizedPut(lParam, new HashSet<>(changeResult.getSecond()));
            return changeResult.getFirst();
        }

        ExceptionHelper.reportRuntimeException("Invalid condition type.");
        return true;
    }

    private static Pair<Boolean, List<ChoiceNode>> splitListWithChoiceNode(
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

    private static Pair<Boolean, List<ChoiceNode>> splitListByValue(
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
                if (JavaTypeHelper.isExtendedIntTypeName(start.getParameter().getType())
                        && JavaTypeHelper.isFloatingPointTypeName(val.getParameter().getType())) {
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

    private static List<RelationStatement> collectRelationStatements(
            Collection<Constraint> initConstraints) {

        List<RelationStatement> result = new ArrayList<>();
        ;

        for (Constraint constraint : initConstraints) {
            collectRelationStatements(constraint, result);
        }

        return result;
    }

    private static void collectRelationStatements(
            Constraint constraint,
            List<RelationStatement> inOutRelationStatements) {

        if (constraint == null) {
            return;
        }

        AbstractStatement premise = constraint.getPremise(), consequence = constraint.getConsequence();
        if (consequence instanceof ExpectedValueStatement) {
            try {
                premise.accept(new CollectingStatementVisitor(inOutRelationStatements));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                premise.accept(new CollectingStatementVisitor(inOutRelationStatements));
                consequence.accept(new CollectingStatementVisitor(inOutRelationStatements));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parseConstraintsToSat(
            Collection<Constraint> initConstraints,
            ExpectedConstraintsData outExpectedValConstraints,
            EcSatSolver sat4Solver) { // TODO - input / output

        for (Constraint constraint : initConstraints) {
            parseConstraintToSat(constraint, outExpectedValConstraints, sat4Solver);
        }
    }

    private void parseConstraintToSat(
            Constraint constraint,
            ExpectedConstraintsData outExpectedValConstraints,
            EcSatSolver sat4Solver) {

        if (constraint == null) {
            return;
        }

        AbstractStatement premise = constraint.getPremise(), consequence = constraint.getConsequence();
        if (consequence instanceof ExpectedValueStatement) {
            Integer premiseID = null;
            try {
                premiseID =
                        (Integer) premise.accept(
                                new ParseConstraintToSATVisitor(
                                        fMethodNode,
                                        fSat4Solver,
                                        fParamChoiceSets,
                                        fChoiceMappingsBucket,
                                        fChoiceToSolverIdMappings));

                outExpectedValConstraints.add(new Pair<>(premiseID, (ExpectedValueStatement) consequence));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Integer premiseID = null, consequenceID = null;
            try {
                premiseID = (Integer) premise.accept(
                        new ParseConstraintToSATVisitor(
                                fMethodNode,
                                fSat4Solver,
                                fParamChoiceSets,
                                fChoiceMappingsBucket,
                                fChoiceToSolverIdMappings));

                consequenceID =
                        (Integer) consequence.accept(
                                new ParseConstraintToSATVisitor(
                                        fMethodNode,
                                        fSat4Solver,
                                        fParamChoiceSets,
                                        fChoiceMappingsBucket,
                                        fChoiceToSolverIdMappings));
            } catch (Exception e) {
                e.printStackTrace();
            }

            fSat4Solver.addSat4Clause(new int[]{-premiseID, consequenceID});
        }
    }

    private static List<Integer> createSolverAssumptions(
            List<ChoiceNode> currentArgumentAssignments, // main input parameter
            EcSatSolver satSolver,
            MethodNode methodNode,
            ChoiceToSolverIdMappings choiceToSolverIdMappings) {

        if (!satSolver.hasConstraints())
            return new ArrayList<>();

        List<MethodParameterNode> methodParameterNodes = methodNode.getMethodParameters();

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