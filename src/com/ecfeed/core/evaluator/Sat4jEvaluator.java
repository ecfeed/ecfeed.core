package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.*;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.utils.*;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import org.sat4j.core.VecInt;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.TimeoutException;

import java.util.*;
import java.util.List;

public class Sat4jEvaluator implements IConstraintEvaluator<ChoiceNode> {

    private Sat4Clauses fSat4Clauses;
    private IntegerHolder fFirstFreeIDHolder = new IntegerHolder(1);

    private ParamsWithChoices fInputChoices;
    private ParamsWithChoices fSanitizedChoices;
    private ParamsWithChoices fAtomicChoices;
    private ChoiceMappings fSanitizedToInputMappings;
    private ChoiceMappings fAtomicToSanitizedMappings;
    private Map<MethodParameterNode, Multimap<ChoiceNode, ChoiceNode>> fArgInputValToSanitizedVal;
    private Multimap<ChoiceNode, ChoiceNode> fSanitizedValToAtomicVal;

    private ParamsWithChInts fArgLessEqChoiceID;
    private ParamsWithChInts fArgLessThChoiceID;
    private ParamsWithChInts fArgChoiceID;

    private List<RelationStatement> fAllRelationStatements;
    private List<Pair<Integer, ExpectedValueStatement>> fExpectedValConstraints; //Integer is the variable of pre-condition enforcing postcondition ExpectedValueStatement
    private MethodNode fMethodNode;
    private SatSolver fSatSolver;

    private enum TypeOfEndpoint {
        LEFT_ENDPOINT,
        RIGHT_ENDPOINT
    }

    public Sat4jEvaluator(Collection<Constraint> initConstraints, MethodNode method) {

        fArgLessEqChoiceID = new ParamsWithChInts("LEQ");
        fArgLessThChoiceID = new ParamsWithChInts("LES");
        fArgChoiceID = new ParamsWithChInts("EQ"); // TODO - equal ?
        fSat4Clauses = new Sat4Clauses();
        fInputChoices = new ParamsWithChoices("ALL");
        fSanitizedChoices = new ParamsWithChoices("SAN");
        fAtomicChoices = new ParamsWithChoices("ATM");
        fSanitizedToInputMappings = new ChoiceMappings("STI");
        fAtomicToSanitizedMappings = new ChoiceMappings("ATS");
        fExpectedValConstraints = new ArrayList<>();
        fAllRelationStatements = new ArrayList<>();
        fArgInputValToSanitizedVal = new HashMap<>();
        fSanitizedValToAtomicVal = HashMultimap.create();
        fMethodNode = method;
        fSatSolver = new SatSolver();

        if (fMethodNode == null && !initConstraints.isEmpty()) {
            ExceptionHelper.reportRuntimeException("No method but there were constraints!");
        }

        if (initConstraints != null && !initConstraints.isEmpty()) {
            fSatSolver.setHasConstraints();

            fInputChoices = collectParametersWithChoices(fMethodNode); // TODO - unify names

            collectSanitizedValues(
                    fInputChoices,
                    fSanitizedChoices,
                    fSanitizedToInputMappings);

            fAllRelationStatements = collectRelationStatements(initConstraints);

            sanitizeRelationStatementsWithRelation(
                    fAllRelationStatements,
                    fSanitizedChoices,
                    fSanitizedToInputMappings);

            todo1(fSanitizedChoices,
                    fArgInputValToSanitizedVal,
                    fSanitizedToInputMappings,
                    fAtomicChoices,
                    fAtomicToSanitizedMappings,
                    fSanitizedValToAtomicVal);

            todo2(fSanitizedChoices,
                    fArgInputValToSanitizedVal,
                    fSanitizedToInputMappings,
                    fAtomicChoices,
                    fAtomicToSanitizedMappings,
                    fSanitizedValToAtomicVal);

            parseConstraintsToSat(initConstraints, fExpectedValConstraints, fSat4Clauses);
        }

        fSatSolver.initialize(
                fFirstFreeIDHolder.get(),
                fSat4Clauses);
    }

    @Override
    public void initialize(List<List<ChoiceNode>> input) {

        if (!fSatSolver.hasConstraints())
            return;

        List<MethodParameterNode> params = fMethodNode.getMethodParameters();

        //iterate params and valueAssignment simultanously
        if (input.size() != params.size()) {
            ExceptionHelper.reportRuntimeException("Lists were supposed to be of equal length!");
            return;
        }
        for (int i = 0; i < input.size(); i++) {
            List<Integer> vars = new ArrayList<>();
            MethodParameterNode p = params.get(i);
            if (p.isExpected())
                continue;
            for (ChoiceNode c : input.get(i)) {
                Integer idOfParamChoiceVar = fArgChoiceID.get(p).get(c.getOrigChoiceNode());
                vars.add(idOfParamChoiceVar);
            }
            VecInt clause = new VecInt(vars.stream().mapToInt(Integer::intValue).toArray()); //one of the input values has to be taken, for each variable
            fSat4Clauses.add(clause);
            fSatSolver.addClause(clause);
        }
    }

    @Override
    public void excludeAssignment(List<ChoiceNode> toExclude) {

        if (!fSatSolver.hasConstraints())
            return;

        if (fSatSolver.isContradicting())
            return;

        List<MethodParameterNode> methodParameterNodes = fMethodNode.getMethodParameters();

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
                    fArgLessEqChoiceID,
                    fArgLessThChoiceID,
                    fArgChoiceID
            );

        VecInt excludeClause = new VecInt(assumptionsFromValues(toExclude).stream().map(x -> -x).mapToInt(Integer::intValue).toArray());
        final int maxVar = fFirstFreeIDHolder.get();

        fSatSolver.newVar(maxVar);

        fSatSolver.addClause(excludeClause);
    }

    @Override
    public EvaluationResult evaluate(List<ChoiceNode> valueAssignment) {

        if (!fSatSolver.hasConstraints()) {
            return EvaluationResult.TRUE;
        }

        if (fSatSolver.isContradicting())
            return EvaluationResult.FALSE;

        final VecInt assumps =
                new VecInt(assumptionsFromValues(valueAssignment).
                stream().
                mapToInt(Integer::intValue).
                toArray());

        if (fSatSolver.isProblemSatisfiable(assumps)) {
            return EvaluationResult.TRUE;
        } else {
            return EvaluationResult.FALSE;
        }
    }

    @Override
    public List<ChoiceNode> adapt(List<ChoiceNode> valueAssignment) {

        if (!fSatSolver.hasConstraints())
            return valueAssignment;

        try {
            IProblem problem = fSatSolver.getSolver();
            boolean b = problem.isSatisfiable(new VecInt(assumptionsFromValues(valueAssignment).stream().mapToInt(Integer::intValue).toArray())); //necessary to make a call so solver can prepare a model
            if (!b) {
                ExceptionHelper.reportRuntimeException("Cannot adapt, it's unsatisfiable!");
                return null;
            }

            Set<Integer> vars = new HashSet<>(Ints.asList(problem.model()));
            for (Pair<Integer, ExpectedValueStatement> p : fExpectedValConstraints) {
                if (vars.contains(p.getFirst())) {
                    p.getSecond().adapt(valueAssignment);
                }
            }
            for (int i = 0; i < valueAssignment.size(); i++) {
                ChoiceNode p = valueAssignment.get(i);
                MethodParameterNode parameter = fMethodNode.getMethodParameters().get(i);
                if (parameter.isExpected()) {
                    valueAssignment.set(i, p.makeClone());
                }
            }

        } catch (TimeoutException e) {
            ExceptionHelper.reportRuntimeException("Timeout, sorry!");
            return null;
        }
        return valueAssignment;
    }

    private static void todo1(
            ParamsWithChoices sanitizedChoices,
            Map<MethodParameterNode, Multimap<ChoiceNode, ChoiceNode>> inOutInputValToSanitizedVal,
            ChoiceMappings sanitizedToInputMappings,
            ParamsWithChoices atomicChoices,
            ChoiceMappings atomicToSanitizedMappings,
            Multimap<ChoiceNode, ChoiceNode> sanitizedValToAtomicVal
    ) { // TODO - input / output

        for (MethodParameterNode methodParameterNode : sanitizedChoices.getKeySet()) {

            inOutInputValToSanitizedVal.put(methodParameterNode, HashMultimap.create());

            for (ChoiceNode sanitizedChoice : sanitizedChoices.get(methodParameterNode)) { //build InputVal -> SanitizedVal mapping
                ChoiceNode inputChoice = sanitizedToInputMappings.get(sanitizedChoice);
                inOutInputValToSanitizedVal.get(methodParameterNode).put(inputChoice, sanitizedChoice);
            }
        }
    }

    private static void todo2(
            ParamsWithChoices sanitizedChoices,
            Map<MethodParameterNode, Multimap<ChoiceNode, ChoiceNode>> inOutInputValToSanitizedVal,
            ChoiceMappings sanitizedToInputMappings,
            ParamsWithChoices atomicChoices,
            ChoiceMappings atomicToSanitizedMappings,
            Multimap<ChoiceNode, ChoiceNode> sanitizedValToAtomicVal
    ) { // TODO - input / output

        for (MethodParameterNode methodParameterNode : sanitizedChoices.getKeySet()) {

            atomicChoices.put(methodParameterNode, new HashSet<>());

            for (ChoiceNode it : sanitizedChoices.get(methodParameterNode)) //build AtomicVal <-> Sanitized Val mappings, build Param -> Atomic Val mapping
                if (it.isRandomizedValue() &&
                        (JavaTypeHelper.isExtendedIntTypeName(methodParameterNode.getType())
                                || JavaTypeHelper.isFloatingPointTypeName(methodParameterNode.getType())
                        )) {
                    List<ChoiceNode> interleaved =
                            ChoiceNodeHelper.interleavedValues(it, sanitizedChoices.getSize());

                    atomicChoices.get(methodParameterNode).addAll(interleaved);
                    for (ChoiceNode c : interleaved) {
                        atomicToSanitizedMappings.put(c, it);
                        sanitizedValToAtomicVal.put(it, c);
                    }
                } else {
                    atomicChoices.get(methodParameterNode).add(it);
                    atomicToSanitizedMappings.put(it, it);
                    sanitizedValToAtomicVal.put(it, it);
                }
        }
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

    private static ParamsWithChoices collectParametersWithChoices(
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
                        val1 = ChoiceNodeHelper.precedingVal(val1);
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
            List<Pair<Integer, ExpectedValueStatement>> outExpectedValConstraints,
            Sat4Clauses clausesVecInt) { // TODO - input / output

        for (Constraint constraint : initConstraints) {
            parseConstraintToSat(constraint, outExpectedValConstraints, clausesVecInt);
        }
    }

    private void parseConstraintToSat(
            Constraint constraint,
            List<Pair<Integer, ExpectedValueStatement>> outExpectedValConstraints,
            Sat4Clauses clausesVecInt) { // TODO - input / output

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
                                        fArgLessEqChoiceID,
                                        fArgLessThChoiceID,
                                        fArgChoiceID));

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
                                fArgLessEqChoiceID,
                                fArgLessThChoiceID,
                                fArgChoiceID));

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
                                        fArgLessEqChoiceID,
                                        fArgLessThChoiceID,
                                        fArgChoiceID));
            } catch (Exception e) {
                e.printStackTrace();
            }

            clausesVecInt.add(new VecInt(new int[]{-premiseID, consequenceID}));
        }
    }

    private List<Integer> assumptionsFromValues(List<ChoiceNode> valueAssignment) {

        if (!fSatSolver.hasConstraints())
            return new ArrayList<>();

        List<MethodParameterNode> params = fMethodNode.getMethodParameters();

        List<Integer> assumptions = new ArrayList<>();

        //iterate params and valueAssignment simultanously
        if (valueAssignment.size() != params.size()) {
            ExceptionHelper.reportRuntimeException("Lists were supposed to be of equal length!");
            return null;
        }
        for (int i = 0; i < params.size(); i++) {
            MethodParameterNode p = params.get(i);
            ChoiceNode c = valueAssignment.get(i);
            if (c != null) {
                if (fArgChoiceID.get(p) == null)
                    continue; //no constraint on this method parameter
                Integer idOfParamChoiceVar = fArgChoiceID.get(p).get(c.getOrigChoiceNode());
                assumptions.add(idOfParamChoiceVar);
            }
        }

        return assumptions;
    }

}
