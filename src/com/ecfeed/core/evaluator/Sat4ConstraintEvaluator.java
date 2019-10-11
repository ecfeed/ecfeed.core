package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.*;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.utils.*;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import org.sat4j.core.VecInt;

import java.util.*;
import java.util.List;

public class Sat4ConstraintEvaluator implements IConstraintEvaluator<ChoiceNode> {

    private Sat4Clauses fSat4Clauses;
    private IntegerHolder fFirstFreeIDHolder = new IntegerHolder(1);

    private ParamsWithChoices fInputChoices;
    private ParamsWithChoices fSanitizedChoices;
    private ParamsWithChoices fAtomicChoices;

    private ChoiceMappings fSanitizedToInputMappings;
    private ChoiceMappings fAtomicToSanitizedMappings;
    private ChoiceMultiMappings fSanitizedValToAtomicVal;

    private Map<MethodParameterNode, Multimap<ChoiceNode, ChoiceNode>> fArgInputValToSanitizedVal;

    private ParamsWithChInts fChoiceToSolverIdLessEqMappings;
    private ParamsWithChInts fChoiceToSolverIdLessThMappings;
    private ParamsWithChInts fChoiceToSolverIdEqualMappings;

    private List<RelationStatement> fAllRelationStatements;
    private ExpectedConstraintsData fExpectedValConstraints;

    private MethodNode fMethodNode;
    private Sat4Solver fSat4Solver;

    static final int fLogLevel = 0;

    private enum TypeOfEndpoint {
        LEFT_ENDPOINT,
        RIGHT_ENDPOINT
    }

    public Sat4ConstraintEvaluator(Collection<Constraint> initConstraints, MethodNode method) {

        fChoiceToSolverIdLessEqMappings = new ParamsWithChInts("LEQ");
        Sat4Logger.log("fChoiceToSolverIdLessEqMappings", fChoiceToSolverIdLessEqMappings, 1, fLogLevel);

        fChoiceToSolverIdLessThMappings = new ParamsWithChInts("LES");
        Sat4Logger.log("fChoiceToSolverIdLessThMappings", fChoiceToSolverIdLessThMappings, 1, fLogLevel);

        fChoiceToSolverIdEqualMappings = new ParamsWithChInts("EQ");
        Sat4Logger.log("fChoiceToSolverIdEqualMappings", fChoiceToSolverIdEqualMappings, 1, fLogLevel);

        fSat4Clauses = new Sat4Clauses();
        Sat4Logger.log("fSat4Clauses", fSat4Clauses, 1, fLogLevel);

        fInputChoices = new ParamsWithChoices("ALL");
        fSanitizedChoices = new ParamsWithChoices("SAN");
        fAtomicChoices = new ParamsWithChoices("ATM");

        fSanitizedToInputMappings = new ChoiceMappings("STI");
        fAtomicToSanitizedMappings = new ChoiceMappings("ATS");
        fExpectedValConstraints = new ExpectedConstraintsData();
        fAllRelationStatements = new ArrayList<>();
        fArgInputValToSanitizedVal = new HashMap<>();
        fSanitizedValToAtomicVal = new ChoiceMultiMappings("STA");

        fMethodNode = method;
        fSat4Solver = new Sat4Solver();

        if (fMethodNode == null && !initConstraints.isEmpty()) {
            ExceptionHelper.reportRuntimeException("Constraints without method.");
        }

        if (initConstraints != null && !initConstraints.isEmpty()) {
            initializeForConstraints(initConstraints);
        }

        fSat4Solver.initialize(
                fFirstFreeIDHolder.get(),
                fSat4Clauses);
    }

    private void initializeForConstraints(Collection<Constraint> initConstraints) {

        fSat4Solver.setHasConstraints();

        fInputChoices = createInputChoices(fMethodNode);

        collectSanitizedValues(
                fInputChoices,
                fSanitizedChoices,
                fSanitizedToInputMappings);
        Sat4Logger.log("fSanitizedChoices", fSanitizedChoices, 1, fLogLevel);
        Sat4Logger.log("fSanitizedToInputMappings", fSanitizedToInputMappings, 1, fLogLevel);


        fAllRelationStatements = collectRelationStatements(initConstraints);
        Sat4Logger.log("fAllRelationStatements", fAllRelationStatements, 1, fLogLevel);

        sanitizeRelationStatementsWithRelation(
                fAllRelationStatements,
                fSanitizedChoices,
                fSanitizedToInputMappings);
        Sat4Logger.log("fSanitizedChoices after sanitize", fSanitizedChoices, 1, fLogLevel);
        Sat4Logger.log("fSanitizedToInputMappings after sanitize", fSanitizedToInputMappings, 1, fLogLevel);


        createInputToSanitizedMapping(
                fSanitizedChoices,
                fSanitizedToInputMappings,
                fArgInputValToSanitizedVal);
        Sat4Logger.log("fArgInputValToSanitizedVal", fArgInputValToSanitizedVal, 1, fLogLevel);


        createSanitizedAndAtomicMappings(fSanitizedChoices,
                fAtomicChoices,
                fAtomicToSanitizedMappings,
                fSanitizedValToAtomicVal);
        Sat4Logger.log("fAtomicToSanitizedMappings", fAtomicToSanitizedMappings, 1, fLogLevel);
        Sat4Logger.log("fSanitizedValToAtomicVal", fSanitizedValToAtomicVal, 1, fLogLevel);

        parseConstraintsToSat(
                initConstraints,
                fExpectedValConstraints,
                fSat4Clauses);
        Sat4Logger.log("fExpectedValConstraints", fExpectedValConstraints, 1, fLogLevel);
        Sat4Logger.log("fSat4Clauses", fSat4Clauses, 1, fLogLevel);
    }

    @Override
    public void initialize(List<List<ChoiceNode>> input) {

        if (!fSat4Solver.hasConstraints())
            return;

        List<MethodParameterNode> methodParameters = fMethodNode.getMethodParameters();
        assertEqualSizes(input, methodParameters);

        for (int parameterIndex = 0; parameterIndex < methodParameters.size(); parameterIndex++) {

            List<Integer> sat4Indexes = new ArrayList<>();
            MethodParameterNode p = methodParameters.get(parameterIndex);

            if (p.isExpected())
                continue;

            for (ChoiceNode c : input.get(parameterIndex)) {
                Integer idOfParamChoiceVar = fChoiceToSolverIdEqualMappings.get(p).get(c.getOrigChoiceNode());
                sat4Indexes.add(idOfParamChoiceVar);
            }

            //one of the input values has to be taken, for each variable
            final int[] clauseValues = sat4Indexes
                    .stream()
                    .mapToInt(Integer::intValue)
                    .toArray();

            VecInt clause = new VecInt(clauseValues);
            fSat4Clauses.add(clause);
            fSat4Solver.addClause(clause);
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
                    fAtomicChoices,
                    fFirstFreeIDHolder,
                    fSanitizedChoices,
                    fSanitizedValToAtomicVal,
                    fSat4Clauses,
                    fInputChoices,
                    fArgInputValToSanitizedVal,
                    fChoiceToSolverIdLessEqMappings,
                    fChoiceToSolverIdLessThMappings,
                    fChoiceToSolverIdEqualMappings
            );

        final int maxVar = fFirstFreeIDHolder.get();
        fSat4Solver.newVar(maxVar);

        final int[] assumptions =
                createSolverAssumptions(choicesToExclude, fSat4Solver, fMethodNode, fChoiceToSolverIdEqualMappings)
                        .stream()
                        .map(x -> -x)
                        .mapToInt(Integer::intValue)
                        .toArray();

        VecInt excludeClause = new VecInt(assumptions);
        fSat4Solver.addClause(excludeClause);
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
                        valueAssignment, fSat4Solver, fMethodNode, fChoiceToSolverIdEqualMappings);

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
                        valueAssignment, fSat4Solver, fMethodNode, fChoiceToSolverIdEqualMappings);

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
            ParamsWithChoices sanitizedChoices,
            ChoiceMappings sanitizedToInputMappings, Map<MethodParameterNode,
            Multimap<ChoiceNode, ChoiceNode>> inOutInputValToSanitizedVal) {

        for (MethodParameterNode methodParameterNode : sanitizedChoices.getKeySet()) {

            inOutInputValToSanitizedVal.put(methodParameterNode, HashMultimap.create());

            for (ChoiceNode sanitizedChoice : sanitizedChoices.get(methodParameterNode)) {

                ChoiceNode inputChoice = sanitizedToInputMappings.get(sanitizedChoice);

                inOutInputValToSanitizedVal.
                        get(methodParameterNode).
                        put(inputChoice, sanitizedChoice);
            }
        }
    }

    private static void createSanitizedAndAtomicMappings(
        ParamsWithChoices sanitizedChoices,
        ParamsWithChoices atomicChoices,
        ChoiceMappings outAtomicToSanitizedMappings,
        ChoiceMultiMappings outSanitizedValToAtomicVal ) {

        for (MethodParameterNode methodParameterNode : sanitizedChoices.getKeySet()) {

            // TODO - why do we need an empty set ?
            atomicChoices.put(methodParameterNode, new HashSet<>());

            createSanitizedAndAtomicMappingsForParam(
                    methodParameterNode,
                    sanitizedChoices,
                    atomicChoices,
                    outAtomicToSanitizedMappings,
                    outSanitizedValToAtomicVal
            );
        }
    }

    private static void createSanitizedAndAtomicMappingsForParam(
            MethodParameterNode methodParameterNode,
            ParamsWithChoices sanitizedChoices,
            ParamsWithChoices atomicChoices,
            ChoiceMappings outAtomicToSanitizedMappings,
            ChoiceMultiMappings outSanitizedValToAtomicVal) {

        //build AtomicVal <-> Sanitized Val mappings, build Param -> Atomic Val mapping
        for (ChoiceNode sanitizedChoiceNode : sanitizedChoices.get(methodParameterNode)) {

            if (isRandomizedExtIntOrFloat(methodParameterNode.getType(), sanitizedChoiceNode)) {

                List<ChoiceNode> interleavedChoices =
                        ChoiceNodeHelper.getInterleavedValues(
                                sanitizedChoiceNode, sanitizedChoices.getSize());

                atomicChoices.get(methodParameterNode).addAll(interleavedChoices);

                for (ChoiceNode interleavedChoiceNode : interleavedChoices) {
                    outAtomicToSanitizedMappings.put(interleavedChoiceNode, sanitizedChoiceNode);
                    outSanitizedValToAtomicVal.put(sanitizedChoiceNode, interleavedChoiceNode);
                }

            } else {

                atomicChoices.get(methodParameterNode).add(sanitizedChoiceNode);
                outAtomicToSanitizedMappings.put(sanitizedChoiceNode, sanitizedChoiceNode);
                outSanitizedValToAtomicVal.put(sanitizedChoiceNode, sanitizedChoiceNode);
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
            ParamsWithChoices inputValues,
            ParamsWithChoices outAllSanitizedValues,
            ChoiceMappings outSanitizedToValMappings) {

        for (MethodParameterNode methodParameterNode : inputValues.getKeySet()) {

            Set<ChoiceNode> copy = new HashSet<>(inputValues.get(methodParameterNode));
            outAllSanitizedValues.put(methodParameterNode, copy);

            for (ChoiceNode choiceNode : copy) //maintaining the dependencies
                outSanitizedToValMappings.put(choiceNode, choiceNode);
        }
    }

    private static ParamsWithChoices createInputChoices(
            MethodNode methodNode) {

        ParamsWithChoices inputValues = new ParamsWithChoices("TMP");

        List<MethodParameterNode> methodParameterNodes = methodNode.getMethodParameters();

        for (MethodParameterNode methodParameterNode : methodParameterNodes) {

            Set<ChoiceNode> choiceNodeSet = new HashSet<>();
            for (ChoiceNode choiceNode : methodParameterNode.getLeafChoicesWithCopies())
                choiceNodeSet.add(choiceNode.getOrigChoiceNode());

            inputValues.put(methodParameterNode, choiceNodeSet);
        }

        return inputValues;
    }

    private static void sanitizeRelationStatementsWithRelation(
            List<RelationStatement> fAllRelationStatements,
            ParamsWithChoices inOutSanitizedValues,
            ChoiceMappings inOutSanitizedToInputMappings) {

        while (true) {
            Boolean anyChange = false;
            for (RelationStatement relationStatement : fAllRelationStatements) {
                if (sanitizeValsWithRelation(
                        relationStatement,
                        inOutSanitizedValues,
                        inOutSanitizedToInputMappings)) {
                    anyChange = true;
                }
            }
            if (!anyChange)
                break;
        }
    }

    private static Boolean sanitizeValsWithRelation(
            RelationStatement relationStatement,
            ParamsWithChoices inOutSanitizedValues,
            ChoiceMappings inOutSanitizedToInputMappings) {

        IStatementCondition condition = relationStatement.getCondition();
        if (condition instanceof LabelCondition)
            return false;

        MethodParameterNode lParam = relationStatement.getLeftParameter();

        if (!JavaTypeHelper.isExtendedIntTypeName(lParam.getType())
                && !JavaTypeHelper.isFloatingPointTypeName(lParam.getType()))
            return false;

        List<ChoiceNode> allLVals = new ArrayList<>(inOutSanitizedValues.get(lParam));


        if (condition instanceof ParameterCondition) {
            MethodParameterNode rParam = ((ParameterCondition) condition).getRightParameterNode();
            List<ChoiceNode> allRVals = new ArrayList<>(inOutSanitizedValues.get(rParam));

            boolean anyChange = false;
            List<ChoiceNode> allLValsCopy = new ArrayList<>(allLVals);
            for (ChoiceNode it : allRVals) {
                Pair<Boolean, List<ChoiceNode>> changeResult =
                        splitListWithChoiceNode(allLValsCopy, it, inOutSanitizedToInputMappings);

                anyChange = anyChange || changeResult.getFirst();
                allLValsCopy = changeResult.getSecond();
            }

            List<ChoiceNode> allRValsCopy = new ArrayList<>(allRVals);
            for (ChoiceNode it : allLVals) {
                Pair<Boolean, List<ChoiceNode>> changeResult =
                        splitListWithChoiceNode(allRValsCopy, it, inOutSanitizedToInputMappings);
                anyChange = anyChange || changeResult.getFirst();
                allRValsCopy = changeResult.getSecond();
            }

            inOutSanitizedValues.put(lParam, new HashSet<>(allLValsCopy));

            inOutSanitizedValues.put(rParam, new HashSet<>(allRValsCopy));

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
                    splitListWithChoiceNode(allLVals, it, inOutSanitizedToInputMappings);

            inOutSanitizedValues.put(lParam, new HashSet<>(changeResult.getSecond()));
            return changeResult.getFirst();
        }

        ExceptionHelper.reportRuntimeException("Invalid condition type.");
        return true;
    }

    private static Pair<Boolean, List<ChoiceNode>> splitListWithChoiceNode(
            List<ChoiceNode> toSplit,
            ChoiceNode val,
            ChoiceMappings inOutSanitizedToInputMappings) {

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
                        inOutSanitizedToInputMappings);

        Pair<Boolean, List<ChoiceNode>> changeResultRight =
                splitListByValue(
                        changeResultLeft.getSecond(),
                        end,
                        TypeOfEndpoint.RIGHT_ENDPOINT,
                        inOutSanitizedToInputMappings);

        return new Pair<>(changeResultLeft.getFirst() || changeResultRight.getFirst(), changeResultRight.getSecond());
    }

    private static Pair<Boolean, List<ChoiceNode>> splitListByValue(
            List<ChoiceNode> toSplit,
            ChoiceNode val,
            TypeOfEndpoint type,
            ChoiceMappings inOutSanitizedToInputMappings) {

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
                inOutSanitizedToInputMappings.put(it1, inOutSanitizedToInputMappings.get(it));
                inOutSanitizedToInputMappings.put(it2, inOutSanitizedToInputMappings.get(it));
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
            Sat4Clauses outClausesVecInt) { // TODO - input / output

        for (Constraint constraint : initConstraints) {
            parseConstraintToSat(constraint, outExpectedValConstraints, outClausesVecInt);
        }
    }

    private void parseConstraintToSat(
            Constraint constraint,
            ExpectedConstraintsData outExpectedValConstraints,
            Sat4Clauses inOutClausesVecInt) {

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
                                        fFirstFreeIDHolder,
                                        fSat4Clauses,
                                        fAtomicChoices,
                                        fSanitizedChoices,
                                        fSanitizedValToAtomicVal,
                                        fInputChoices,
                                        fArgInputValToSanitizedVal,
                                        fChoiceToSolverIdLessEqMappings,
                                        fChoiceToSolverIdLessThMappings,
                                        fChoiceToSolverIdEqualMappings));

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
                                fFirstFreeIDHolder,
                                fSat4Clauses,
                                fAtomicChoices,
                                fSanitizedChoices,
                                fSanitizedValToAtomicVal,
                                fInputChoices,
                                fArgInputValToSanitizedVal,
                                fChoiceToSolverIdLessEqMappings,
                                fChoiceToSolverIdLessThMappings,
                                fChoiceToSolverIdEqualMappings));

                consequenceID =
                        (Integer) consequence.accept(
                                new ParseConstraintToSATVisitor(
                                        fMethodNode,
                                        fFirstFreeIDHolder,
                                        fSat4Clauses,
                                        fAtomicChoices,
                                        fSanitizedChoices,
                                        fSanitizedValToAtomicVal,
                                        fInputChoices,
                                        fArgInputValToSanitizedVal,
                                        fChoiceToSolverIdLessEqMappings,
                                        fChoiceToSolverIdLessThMappings,
                                        fChoiceToSolverIdEqualMappings));
            } catch (Exception e) {
                e.printStackTrace();
            }

            inOutClausesVecInt.add(new VecInt(new int[]{-premiseID, consequenceID}));
        }
    }

    private static List<Integer> createSolverAssumptions(
            List<ChoiceNode> currentArgumentAssignments, // main input parameter
            Sat4Solver satSolver,
            MethodNode methodNode,
            ParamsWithChInts argChoiceID) {

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
                        argChoiceID.get(methodParameterNode);

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
