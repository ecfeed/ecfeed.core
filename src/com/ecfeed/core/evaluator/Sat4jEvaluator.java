package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.*;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.utils.*;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.*;
import java.util.List;

import static com.ecfeed.core.utils.EMathRelation.*;

public class Sat4jEvaluator implements IConstraintEvaluator<ChoiceNode> {

    private Sat4Clauses fSat4Clauses;
    private IntegerHolder fFirstFreeIDHolder = new IntegerHolder(1);

    private ParamsWithChoices fArgAllInputValues;
    private ParamsWithChoices fArgAllSanitizedValues;
    private ParamsWithChoices fArgAllAtomicValues;
    private Map<ChoiceNode, ChoiceNode> fSanitizedValToInputVal;
    private Map<ChoiceNode, ChoiceNode> fAtomicValToSanitizedVal;
    private Map<MethodParameterNode, Multimap<ChoiceNode, ChoiceNode>> fArgInputValToSanitizedVal;
    private Multimap<ChoiceNode, ChoiceNode> fSanitizedValToAtomicVal;

    private ParamsWithChInts fArgLessEqChoiceID;
    private ParamsWithChInts fArgLessThChoiceID;
    private ParamsWithChInts fArgChoiceID;

    private List<RelationStatement> fAllRelationStatements;
    private List<Pair<Integer, ExpectedValueStatement>> fExpectedValConstraints; //Integer is the variable of pre-condition enforcing postcondition ExpectedValueStatement
    private MethodNode fMethodNode;
    private ISolver fSolver;
    private Boolean fIsContradicting = false;
    private Boolean fNoConstraints = true;


    public Sat4jEvaluator(Collection<Constraint> initConstraints, MethodNode method) {

        fArgLessEqChoiceID = new ParamsWithChInts("LEQ");
        fArgLessThChoiceID = new ParamsWithChInts("LES");
        fArgChoiceID = new ParamsWithChInts("EQ"); // TODO - equal ?
        fSat4Clauses = new Sat4Clauses();
        fArgAllInputValues = new ParamsWithChoices("ALL");
        fArgAllSanitizedValues = new ParamsWithChoices("SAN");
        fArgAllAtomicValues = new ParamsWithChoices("ATM");
        fSanitizedValToInputVal = new HashMap<>();
        fAtomicValToSanitizedVal = new HashMap<>();
        fExpectedValConstraints = new ArrayList<>();
        fAllRelationStatements = new ArrayList<>();
        fArgInputValToSanitizedVal = new HashMap<>();
        fSanitizedValToAtomicVal = HashMultimap.create();
        fMethodNode = method;
        if (fMethodNode == null && !initConstraints.isEmpty()) {
            ExceptionHelper.reportRuntimeException("No method but there were constraints!");
        }

        if (initConstraints != null && !initConstraints.isEmpty()) {
            fNoConstraints = false;

            fArgAllInputValues = collectParametersWithChoices(fMethodNode); // TODO - unify names

            collectSanitizedValues(fArgAllInputValues, fArgAllSanitizedValues, fSanitizedValToInputVal);

            fAllRelationStatements = collectRelationStatements(initConstraints);

            sanitizeRelationStatementsWithRelation(
                    fAllRelationStatements,
                    fArgAllSanitizedValues, fSanitizedValToInputVal);

            todo();

            parseConstraintsToSat(initConstraints, fExpectedValConstraints, fSat4Clauses);
        }
        final int maxVar = fFirstFreeIDHolder.get();
        final int nbClauses = fSat4Clauses.getSize();
        fSolver = SolverFactory.newDefault();


        try {
            fSolver.newVar(maxVar);
            fSolver.setExpectedNumberOfClauses(nbClauses);

            for (int index = 0; index < fSat4Clauses.getSize(); index++) {
                VecInt clause = fSat4Clauses.getClause(index);
                fSolver.addClause(clause);
            }

            System.out.println("variables: " + maxVar + " clauses: " + nbClauses);
        } catch (ContradictionException e) {
            fIsContradicting = true;
        }
    }

    private void todo() {
        for (MethodParameterNode param : fArgAllSanitizedValues.getKeySet()) {
            fArgInputValToSanitizedVal.put(param, HashMultimap.create());
            for (ChoiceNode sanitizedChoice : fArgAllSanitizedValues.get(param)) { //build InputVal -> SanitizedVal mapping
                ChoiceNode inputChoice = fSanitizedValToInputVal.get(sanitizedChoice);
                fArgInputValToSanitizedVal.get(param).put(inputChoice, sanitizedChoice);
            }


            fArgAllAtomicValues.put(param, new HashSet<>());
            for (ChoiceNode it : fArgAllSanitizedValues.get(param)) //build AtomicVal <-> Sanitized Val mappings, build Param -> Atomic Val mapping
                if (it.isRandomizedValue() &&
                        (JavaTypeHelper.isExtendedIntTypeName(param.getType())
                                || JavaTypeHelper.isFloatingPointTypeName(param.getType())
                        )) {
                    List<ChoiceNode> interleaved =
                            ChoiceNodeHelper.interleavedValues(it, fArgAllSanitizedValues.getSize());

                    fArgAllAtomicValues.get(param).addAll(interleaved);
                    for (ChoiceNode c : interleaved) {
                        fAtomicValToSanitizedVal.put(c, it);
                        fSanitizedValToAtomicVal.put(it, c);
                    }
                } else {
                    fArgAllAtomicValues.get(param).add(it);
                    fAtomicValToSanitizedVal.put(it, it);
                    fSanitizedValToAtomicVal.put(it, it);
                }
        }
    }

    private static void collectSanitizedValues(
            ParamsWithChoices inputValues,
            ParamsWithChoices outAllSanitizedValues,
            Map<ChoiceNode, ChoiceNode> outSanitizedValToInputVal) {

        for (MethodParameterNode methodParameterNode : inputValues.getKeySet()) {

            Set<ChoiceNode> copy = new HashSet<>(inputValues.get(methodParameterNode));
            outAllSanitizedValues.put(methodParameterNode, copy);

            for (ChoiceNode choiceNode : copy) //maintaining the dependencies
                outSanitizedValToInputVal.put(choiceNode, choiceNode);
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

    @Override
    public void initialize(List<List<ChoiceNode>> input) {
        if (fNoConstraints)
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
            try {
                fSolver.addClause(clause);
            } catch (ContradictionException e) {
                fIsContradicting = true;
            }
        }
    }

    private static void sanitizeRelationStatementsWithRelation(
            List<RelationStatement> fAllRelationStatements,
            ParamsWithChoices inOutSanitizedValues,
            Map<ChoiceNode, ChoiceNode> inOutSanitizedValToInputVal) {

        while (true) {
            Boolean anyChange = false;
            for (RelationStatement relationStatement : fAllRelationStatements) {
                if (sanitizeValsWithRelation(
                        relationStatement, inOutSanitizedValues, inOutSanitizedValToInputVal)) {
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
            Map<ChoiceNode, ChoiceNode> fSanitizedValToInputVal) {

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
                        splitListWithChoiceNode(allLValsCopy, it, fSanitizedValToInputVal);

                anyChange = anyChange || changeResult.getFirst();
                allLValsCopy = changeResult.getSecond();
            }

            List<ChoiceNode> allRValsCopy = new ArrayList<>(allRVals);
            for (ChoiceNode it : allLVals) {
                Pair<Boolean, List<ChoiceNode>> changeResult =
                        splitListWithChoiceNode(allRValsCopy, it, fSanitizedValToInputVal);
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
                    splitListWithChoiceNode(allLVals, it, fSanitizedValToInputVal);

            inOutSanitizedValues.put(lParam, new HashSet<>(changeResult.getSecond()));
            return changeResult.getFirst();
        }

        ExceptionHelper.reportRuntimeException("Invalid condition type.");
        return true;
    }


    private enum TypeOfEndpoint {
        LEFT_ENDPOINT,
        RIGHT_ENDPOINT
    }

    private static Pair<Boolean, List<ChoiceNode>> splitListWithChoiceNode(
            List<ChoiceNode> toSplit,
            ChoiceNode val,
            Map<ChoiceNode, ChoiceNode> inOutSanitizedValToInputVal) {

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
                        inOutSanitizedValToInputVal);

        Pair<Boolean, List<ChoiceNode>> changeResultRight =
                splitListByValue(
                        changeResultLeft.getSecond(),
                        end,
                        TypeOfEndpoint.RIGHT_ENDPOINT,
                        inOutSanitizedValToInputVal);

        return new Pair<>(changeResultLeft.getFirst() || changeResultRight.getFirst(), changeResultRight.getSecond());
    }

    private static Pair<Boolean, List<ChoiceNode>> splitListByValue(
            List<ChoiceNode> toSplit,
            ChoiceNode val,
            TypeOfEndpoint type,
            Map<ChoiceNode, ChoiceNode> inOutSanitizedValToInputVal) {

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
                inOutSanitizedValToInputVal.put(it1, inOutSanitizedValToInputVal.get(it));
                inOutSanitizedValToInputVal.put(it2, inOutSanitizedValToInputVal.get(it));
                newList.add(it1);
                newList.add(it2);
            }

        return new Pair<>(anyChange, newList);
    }

    private static int newID(IntegerHolder fFirstFreeIDHolder) {
        fFirstFreeIDHolder.increment();
        return fFirstFreeIDHolder.get();
    }

    private static void prepareVariablesForParameter(
            MethodParameterNode methodParameterNode,
            ParamsWithChoices fArgAllAtomicValues,
            IntegerHolder fFirstFreeIDHolder,
            ParamsWithChoices fArgAllSanitizedValues,
            Multimap<ChoiceNode, ChoiceNode> fSanitizedValToAtomicVal,
            Sat4Clauses sat4Clauses,
            ParamsWithChoices fArgAllInputValues,
            Map<MethodParameterNode, Multimap<ChoiceNode, ChoiceNode>> fArgInputValToSanitizedVal,
            ParamsWithChInts fArgLessEqChoiceID,
            ParamsWithChInts fArgLessThChoiceID,
            ParamsWithChInts fArgChoiceID) {

        if (fArgChoiceID.containsKey(methodParameterNode))
            return;

        //we need to create new set of variables, as we are seeing this parameter for the first time
        //choiceVars control whether a choice is taken
        //prefixVars are used to enforce uniqueness of choice
        List<Integer> choiceVars = new ArrayList<>(); //choiceVars[i] ==  (this parameter takes choice i)
        List<Integer> prefixVars = new ArrayList<>(); //prefixVars[i] == (this parameter takes one of choices 0,...,i)
        List<Integer> lessEqVars = new ArrayList<>(); //lessEqVars[i] == (this parameter <= value at i)
        List<Integer> lessThVars = new ArrayList<>(); //lessThVars[i] == (this parameter < value at i)
//        HashMap<ChoiceNode, Integer> inverseEqVars = new HashMap<>();
        HashMap<ChoiceNode, Integer> inverseLEqVars = new HashMap<>();
        HashMap<ChoiceNode, Integer> inverseLThVars = new HashMap<>();
        HashMap<ChoiceNode, Integer> choiceID = new HashMap<>();


        List<ChoiceNode> sortedChoices = new ArrayList<>(fArgAllAtomicValues.get(methodParameterNode));
        int n = sortedChoices.size();

        if (!JavaTypeHelper.isNumericTypeName(methodParameterNode.getType())) {
            for (int i = 0; i < n; i++) {
                choiceVars.add(newID(fFirstFreeIDHolder));
                choiceID.put(sortedChoices.get(i), choiceVars.get(i));
            }
            fArgChoiceID.put(methodParameterNode, choiceID);
            return;
        }


        Collections.sort(sortedChoices, new ChoiceNodeComparator());


        prefixVars.add(newID(fFirstFreeIDHolder));
        for (int i = 0; i < n; i++) {
            choiceVars.add(newID(fFirstFreeIDHolder));
            prefixVars.add(newID(fFirstFreeIDHolder));
            choiceID.put(sortedChoices.get(i), choiceVars.get(i));
        }

        for (ChoiceNode sanitizedValue : fArgAllSanitizedValues.get(methodParameterNode))
            if (!choiceID.containsKey(sanitizedValue)) {
                Integer sanitizedID = newID(fFirstFreeIDHolder);
                choiceID.put(sanitizedValue, sanitizedID);

                List<Integer> bigClause = new ArrayList<>();
                for (ChoiceNode atomicValue : fSanitizedValToAtomicVal.get(sanitizedValue)) {
                    Integer atomicID = choiceID.get(atomicValue);
                    sat4Clauses.add(new VecInt(new int[]{-atomicID, sanitizedID})); // atomicID => sanitizedID
                    bigClause.add(atomicID);
                }
                bigClause.add(-sanitizedID);
                sat4Clauses.add(new VecInt(bigClause.stream().mapToInt(Integer::intValue).toArray())); //sanitizedID => (atomicID1 OR ... OR atomicIDn)
            }

        for (ChoiceNode inputValue : fArgAllInputValues.get(methodParameterNode))
            if (!choiceID.containsKey(inputValue)) {
                Integer inputID = newID(fFirstFreeIDHolder);
                choiceID.put(inputValue, inputID);

                List<Integer> bigClause = new ArrayList<>();
                for (ChoiceNode sanitizedValue : fArgInputValToSanitizedVal.get(methodParameterNode).get(inputValue)) {
                    Integer sanitizedID = choiceID.get(sanitizedValue);
                    sat4Clauses.add(new VecInt(new int[]{-sanitizedID, inputID})); // sanitizedID => inputID
                    bigClause.add(sanitizedID);
                }
                bigClause.add(-inputID);
                sat4Clauses.add(new VecInt(bigClause.stream().mapToInt(Integer::intValue).toArray())); //inputID => (sanitizedID1 OR ... OR sanitizedIDn)
            }


        sat4Clauses.add(new VecInt(new int[]{-prefixVars.get(0)}));
        sat4Clauses.add(new VecInt(new int[]{prefixVars.get(n)})); //at least one value should be taken
        for (int i = 0; i < n; i++) {
            // prefixVars[i+1] == prefixVars[i] OR choiceVars[i]
            sat4Clauses.add(new VecInt(new int[]{-choiceVars.get(i), prefixVars.get(i + 1)})); // choiceVars[i] => prefixVars[i];
            sat4Clauses.add(new VecInt(new int[]{-prefixVars.get(i), prefixVars.get(i + 1)})); // prefixVars[i] => prefixVars[i+1];
            sat4Clauses.add(new VecInt(new int[]{choiceVars.get(i), prefixVars.get(i), -prefixVars.get(i + 1)})); // enforcing that last one is true only when at least one of first+second is true

            sat4Clauses.add(new VecInt(new int[]{-choiceVars.get(i), -prefixVars.get(i)})); // NOT( choiceVars[i] AND prefixVars[i] ), to guarantee uniqueness
        }

        for (int i = 0; i < n; i++) {
            lessEqVars.add(prefixVars.get(i + 1));
            lessThVars.add(prefixVars.get(i));
        }
        for (int i = 1; i < n; i++) //all elements except first one
            if (new ChoiceNodeComparator().compare(sortedChoices.get(i - 1), sortedChoices.get(i)) == 0)
                lessThVars.set(i, lessThVars.get(i - 1));

        for (int i = n - 1; i > 0; i--) //all elements except first one, in reverse
            if (new ChoiceNodeComparator().compare(sortedChoices.get(i - 1), sortedChoices.get(i)) == 0)
                lessEqVars.set(i - 1, lessEqVars.get(i));

        for (int i = 0; i < n; i++) {
            ChoiceNode choice = sortedChoices.get(i);
            inverseLEqVars.put(choice, lessEqVars.get(i));
            inverseLThVars.put(choice, lessThVars.get(i));
        }

        fArgLessEqChoiceID.put(methodParameterNode, inverseLEqVars);
        fArgLessThChoiceID.put(methodParameterNode, inverseLThVars);
        fArgChoiceID.put(methodParameterNode, choiceID);
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

    static class CollectingStatementVisitor implements IStatementVisitor {

        private List<RelationStatement> fInOutRelationStatements;

        public CollectingStatementVisitor(List<RelationStatement> inOutRelationStatements) {
            fInOutRelationStatements = inOutRelationStatements;
        }

        @Override
        public Object visit(StatementArray statement) {
            for (AbstractStatement child : statement.getChildren()) {
                try {
                    child.accept(new CollectingStatementVisitor(fInOutRelationStatements));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public Object visit(RelationStatement statement) {
            fInOutRelationStatements.add(statement);
            return null;
        }

        @Override
        public Object visit(StaticStatement statement) {
            return null;
        }

        @Override
        public Object visit(ExpectedValueStatement statement) {
            reportUnexpectedTypeException();
            return null;
        }

        @Override
        public Object visit(LabelCondition statement) {
            reportUnexpectedTypeException();
            return null;
        }

        @Override
        public Object visit(ChoiceCondition statement) {
            reportUnexpectedTypeException();
            return null;
        }

        @Override
        public Object visit(ParameterCondition statement) {
            reportUnexpectedTypeException();
            return null;
        }

        @Override
        public Object visit(ValueCondition statement) {
            reportUnexpectedTypeException();
            return null;
        }

        private void reportUnexpectedTypeException() {
            ExceptionHelper.reportRuntimeException("Unexpected type.");
        }

    }

    private void parseConstraintsToSat(
            Collection<Constraint> initConstraints,
            List<Pair<Integer, ExpectedValueStatement>> outExpectedValConstraints,
            Sat4Clauses clausesVecInt) {

        for (Constraint constraint : initConstraints) {
            parseConstraintToSat(constraint, outExpectedValConstraints, clausesVecInt);
        }
    }

    private void parseConstraintToSat(
            Constraint constraint,
            List<Pair<Integer, ExpectedValueStatement>> outExpectedValConstraints,
            Sat4Clauses clausesVecInt) {

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
                                        fArgAllAtomicValues,
                                        fArgAllSanitizedValues,
                                        fSanitizedValToAtomicVal,
                                        fArgAllInputValues,
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
                                fArgAllAtomicValues,
                                fArgAllSanitizedValues,
                                fSanitizedValToAtomicVal,
                                fArgAllInputValues,
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
                                        fArgAllAtomicValues,
                                        fArgAllSanitizedValues,
                                        fSanitizedValToAtomicVal,
                                        fArgAllInputValues,
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

    static class ParseConstraintToSATVisitor implements IStatementVisitor {

        private MethodNode fMethodNode;

        private IntegerHolder fFirstFreeIDHolder;
        private Sat4Clauses fSat4Clauses;
        private ParamsWithChoices fArgAllAtomicValues;
        private ParamsWithChoices fArgAllSanitizedValues;
        private Multimap<ChoiceNode, ChoiceNode> fSanitizedValToAtomicVal;
        private ParamsWithChoices fArgAllInputValues;
        private Map<MethodParameterNode, Multimap<ChoiceNode, ChoiceNode>> fArgInputValToSanitizedVal;

        private ParamsWithChInts fArgLessEqChoiceID;
        private ParamsWithChInts fArgLessThChoiceID;
        private ParamsWithChInts fArgChoiceID;


        public ParseConstraintToSATVisitor(
                MethodNode methodNode,
                IntegerHolder firstFreeIDHolder,
                Sat4Clauses sat4Clauses,
                ParamsWithChoices allAtomicValues,
                ParamsWithChoices allSanitizedValues,
                Multimap<ChoiceNode, ChoiceNode> sanitizedValToAtomicVal,
                ParamsWithChoices allInputValues,
                Map<MethodParameterNode, Multimap<ChoiceNode, ChoiceNode>> inputValToSanitizedVal,
                ParamsWithChInts lessEqChoiceID,
                ParamsWithChInts lessThChoiceID,
                ParamsWithChInts choiceID
        ) {

            fMethodNode = methodNode;
            fFirstFreeIDHolder = firstFreeIDHolder;
            fSat4Clauses = sat4Clauses;
            fArgAllAtomicValues = allAtomicValues;
            fArgAllSanitizedValues = allSanitizedValues;
            fSanitizedValToAtomicVal = sanitizedValToAtomicVal;
            fArgAllInputValues = allInputValues;
            fArgInputValToSanitizedVal = inputValToSanitizedVal;

            fArgLessEqChoiceID = lessEqChoiceID;
            fArgLessThChoiceID = lessThChoiceID;
            fArgChoiceID = choiceID;
        }

        @Override
        public Object visit(StatementArray statement) {
            Integer myID = newID(fFirstFreeIDHolder);
            switch (statement.getOperator()) {
                case OR: // y = (x1 OR x2 OR .. OR xn) compiles to: (NOT x1 OR y) AND ... AND (NOT xn OR y) AND (x1 OR ... OR xn OR NOT y)
                {
                    List<Integer> bigClause = new ArrayList<>();
                    for (AbstractStatement child : statement.getChildren()) {
                        Integer childID = null;
                        try {
                            childID = (Integer) child.accept(
                                    new ParseConstraintToSATVisitor(
                                            fMethodNode,
                                            fFirstFreeIDHolder,
                                            fSat4Clauses,
                                            fArgAllAtomicValues,
                                            fArgAllSanitizedValues,
                                            fSanitizedValToAtomicVal,
                                            fArgAllInputValues,
                                            fArgInputValToSanitizedVal,
                                            fArgLessEqChoiceID,
                                            fArgLessThChoiceID,
                                            fArgChoiceID));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        bigClause.add(childID);
                        fSat4Clauses.add(new VecInt(new int[]{-childID, myID})); //small fClauses
                    }
                    bigClause.add(-myID);
                    fSat4Clauses.add(new VecInt(bigClause.stream().mapToInt(Integer::intValue).toArray()));
                    break;
                }
                case AND: // y = (x1 AND x2 AND .. AND xn) compiles to: (x1 OR NOT y) AND ... AND (xn OR NOT y) AND (NOT x1 OR ... OR NOT xn OR y)
                {
                    List<Integer> bigClause = new ArrayList<>();
                    for (AbstractStatement child : statement.getChildren()) {
                        Integer childID = null;
                        try {
                            childID = (Integer) child.accept(
                                    new ParseConstraintToSATVisitor(
                                            fMethodNode,
                                            fFirstFreeIDHolder,
                                            fSat4Clauses,
                                            fArgAllAtomicValues,
                                            fArgAllSanitizedValues,
                                            fSanitizedValToAtomicVal,
                                            fArgAllInputValues,
                                            fArgInputValToSanitizedVal,
                                            fArgLessEqChoiceID,
                                            fArgLessThChoiceID,
                                            fArgChoiceID));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        bigClause.add(-childID);
                        fSat4Clauses.add(new VecInt(new int[]{childID, -myID})); //small fClauses
                    }
                    bigClause.add(myID);
                    fSat4Clauses.add(new VecInt(bigClause.stream().mapToInt(Integer::intValue).toArray()));
                    break;
                }
            }
//            statementToID.put(statement,myID); not really necessary, as we are never reusing the same statements
            return myID;
        }

        @Override
        public Object visit(RelationStatement statement) {
            if (statement.getCondition() instanceof ParameterCondition) {
                return doubleChoiceParamConstraints(statement);
            } else if (statement.getCondition() instanceof ChoiceCondition &&
                    ((ChoiceCondition) statement.getCondition()).getRightChoice().isRandomizedValue() &&
                    (JavaTypeHelper.isExtendedIntTypeName(statement.getLeftParameter().getType())
                            || JavaTypeHelper.isFloatingPointTypeName(statement.getLeftParameter().getType()))
            ) {
                switch (statement.getRelation()) {
                    case GREATER_THAN:
                    case LESS_EQUAL: {
                        RelationStatement statement1 = statement.getCopy();
                        ChoiceNode val = ((ChoiceCondition) statement.getCondition()).getRightChoice();
                        val = ChoiceNodeHelper.rangeSplit(val).getSecond();
                        statement1.setCondition(val);
                        return singleChoiceParamConstraints(statement);
                    }
                    case GREATER_EQUAL:
                    case LESS_THAN: {
                        RelationStatement statement1 = statement.getCopy();
                        ChoiceNode val = ((ChoiceCondition) statement.getCondition()).getRightChoice();
                        val = ChoiceNodeHelper.rangeSplit(val).getFirst();
                        statement1.setCondition(val);
                        return singleChoiceParamConstraints(statement);
                    }
                    case NOT_EQUAL:
                    case EQUAL: {
                        RelationStatement statementLow = statement.getCopy();
                        RelationStatement statementHigh = statement.getCopy();
                        ChoiceNode val = ((ChoiceCondition) statement.getCondition()).getRightChoice();
                        ChoiceNode valLow = ChoiceNodeHelper.rangeSplit(val).getFirst();
                        ChoiceNode valHigh = ChoiceNodeHelper.rangeSplit(val).getSecond();
                        statementLow.setCondition(valLow);
                        statementHigh.setCondition(valHigh);
                        statementLow.setRelation(GREATER_EQUAL);
                        statementHigh.setRelation(LESS_EQUAL);

                        Integer statementLowID = singleChoiceParamConstraints(statementLow);
                        Integer statementHighID = singleChoiceParamConstraints(statementHigh);

                        Integer myID = newID(fFirstFreeIDHolder);

                        fSat4Clauses.add(new VecInt(new int[]{-statementLowID, -statementHighID, myID}));
                        fSat4Clauses.add(new VecInt(new int[]{-myID, statementLowID}));
                        fSat4Clauses.add(new VecInt(new int[]{-myID, statementHighID}));
                        if (statement.getRelation() == EQUAL)
                            return myID; //myID == (statementLowID AND statementHighID)
                        else //NOT_EQUAL
                            return -myID;
                    }
                }
            } else {
                //we need only to iterate over all choices of single lParam
                return singleChoiceParamConstraints(statement);
            }
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null;
        }

        private Integer singleChoiceParamConstraints(RelationStatement statement) {
            MethodParameterNode leftMethodParameterNode = statement.getLeftParameter();

            prepareVariablesForParameter(
                    leftMethodParameterNode,
                    fArgAllAtomicValues,
                    fFirstFreeIDHolder,
                    fArgAllSanitizedValues,
                    fSanitizedValToAtomicVal,
                    fSat4Clauses,
                    fArgAllInputValues,
                    fArgInputValToSanitizedVal,
                    fArgLessEqChoiceID,
                    fArgLessThChoiceID,
                    fArgChoiceID
            );

            Integer myID = newID(fFirstFreeIDHolder);

            int lParamIndex = fMethodNode.getMethodParameters().indexOf(leftMethodParameterNode);
            if (lParamIndex == -1) {
                ExceptionHelper.reportRuntimeException("Parameter not in method!");
            }
            for (ChoiceNode lChoice : fArgAllAtomicValues.get(leftMethodParameterNode)) {
                List<ChoiceNode> dummyValues = new ArrayList<>(Collections.nCopies(fMethodNode.getParametersCount(), null));
                dummyValues.set(lParamIndex, lChoice);
                EvaluationResult result = statement.evaluate(dummyValues);
                Integer idOfLeftArgChoice = fArgChoiceID.get(leftMethodParameterNode).get(lChoice);
                if (result == EvaluationResult.TRUE) {
                    fSat4Clauses.add(new VecInt(new int[]{-idOfLeftArgChoice, myID})); // thisChoice => me
                } else if (result == EvaluationResult.FALSE) {
                    fSat4Clauses.add(new VecInt(new int[]{-idOfLeftArgChoice, -myID})); // thisChoice => NOT me
                } else //INSUFFICIENT_DATA
                {
                    ExceptionHelper.reportRuntimeException("INSUFFICIENT_DATA: You shouldn't be here!");
                }
            }

            return myID;
        }

        private Integer doubleChoiceParamConstraints(RelationStatement statement) {
            MethodParameterNode lParam = statement.getLeftParameter();
            prepareVariablesForParameter(lParam,
                    fArgAllAtomicValues,
                    fFirstFreeIDHolder,
                    fArgAllSanitizedValues,
                    fSanitizedValToAtomicVal,
                    fSat4Clauses,
                    fArgAllInputValues,
                    fArgInputValToSanitizedVal,
                    fArgLessEqChoiceID,
                    fArgLessThChoiceID,
                    fArgChoiceID
            );

            Integer myID = newID(fFirstFreeIDHolder);

            int lParamIndex = fMethodNode.getMethodParameters().indexOf(lParam);
            if (lParamIndex == -1) {
                ExceptionHelper.reportRuntimeException("Parameter not in method!");
            }
            MethodParameterNode rParam = ((ParameterCondition) statement.getCondition()).getRightParameterNode();

            prepareVariablesForParameter(
                    rParam,
                    fArgAllAtomicValues,
                    fFirstFreeIDHolder,
                    fArgAllSanitizedValues,
                    fSanitizedValToAtomicVal,
                    fSat4Clauses,
                    fArgAllInputValues,
                    fArgInputValToSanitizedVal,
                    fArgLessEqChoiceID,
                    fArgLessThChoiceID,
                    fArgChoiceID
            );

            int rParamIndex = fMethodNode.getMethodParameters().indexOf(rParam);
            if (rParamIndex == -1) {
                ExceptionHelper.reportRuntimeException("Parameter not in method!");
            }

            List<ChoiceNode> sortedLChoices = new ArrayList<>(fArgAllAtomicValues.get(lParam));
            Collections.sort(sortedLChoices, new ChoiceNodeComparator());
            int m = sortedLChoices.size();
            List<ChoiceNode> sortedRChoices = new ArrayList<>(fArgAllAtomicValues.get(rParam));
            Collections.sort(sortedRChoices, new ChoiceNodeComparator());
            int n = sortedRChoices.size();

            for (int i = 0, j = 0; i < m; i++) {
                while (j < n && new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) > 0) {
                    j++;
                }

                Integer leftLessTh = fArgLessThChoiceID.get(lParam).get(sortedLChoices.get(i));
                Integer leftLessEq = fArgLessEqChoiceID.get(lParam).get(sortedLChoices.get(i));
                Integer rightLessTh = null;
                Integer rightLessEq = null;
                if (j < n) {
                    rightLessTh = fArgLessThChoiceID.get(rParam).get(sortedRChoices.get(j));
                    rightLessEq = fArgLessEqChoiceID.get(rParam).get(sortedRChoices.get(j));
                }

                switch (statement.getRelation()) {
                    case EQUAL:
                    case NOT_EQUAL: //negated at return
                    {
                        if (j == n) {
                            // NOT(i<x) IMPLIES NOT(myID)
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -myID}));

                            break;
                        } else if (new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {

                            // NOT(i<x) AND i<=x IMPLIES NOT(myID)
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -myID}));
                        } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                        {
                            // NOT(i<x) AND i<=x AND NOT(j<y) AND j<=y IMPLIES myID
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, -rightLessEq, myID}));

                            // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));

                            // NOT(i<x) AND i<=x AND NOT(j<=y) IMPLIES NOT(myID)
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessEq, -myID}));
                        }
                        break;
                    }

                    case LESS_THAN:
                    case GREATER_EQUAL: //negated at return
                    {
                        if (j == n) {
                            // NOT(i<x) IMPLIES NOT(myID)
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -myID}));

                            break;
                        } else if (new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {
                            // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID}));

                            // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));
                        } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                        {
                            // NOT(i<x) AND i<=x AND NOT(j<=y) IMPLIES myID
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessEq, myID}));

                            // NOT(i<x) AND i<=x AND j<=y IMPLIES NOT(myID)
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessEq, -myID}));
                        }
                        break;
                    }
                    case LESS_EQUAL:
                    case GREATER_THAN: //negated at return
                    {
                        if (j == n) {
                            // NOT(i<x) IMPLIES NOT(myID)
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -myID}));

                            break;
                        } else if (new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {
                            // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID}));

                            // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));
                        } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                        {
                            // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID}));

                            // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                            fSat4Clauses.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));
                        }
                        break;
                    }
                }
            }

            if (statement.getRelation() == EQUAL || statement.getRelation() == LESS_THAN || statement.getRelation() == LESS_EQUAL)
                return myID;
            else
                return -myID;
        }

        @Override
        public Object visit(ExpectedValueStatement statement) {
            return null; //TODO
        }

        @Override
        public Object visit(StaticStatement statement) {
            Integer myID = newID(fFirstFreeIDHolder);
            if (statement.getValue() == EvaluationResult.TRUE)
                fSat4Clauses.add(new VecInt(new int[]{myID}));
            else
                fSat4Clauses.add(new VecInt(new int[]{-myID}));
            return myID;
        }

        @Override
        public Object visit(LabelCondition statement) {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!"); // TODO
            return null; //this will never happen
        }

        @Override
        public Object visit(ChoiceCondition statement) {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }

        @Override
        public Object visit(ParameterCondition statement) {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }

        @Override
        public Object visit(ValueCondition statement) {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
    }

    private List<Integer> assumptionsFromValues(List<ChoiceNode> valueAssignment) {

        if (fNoConstraints)
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

    @Override
    public void excludeAssignment(List<ChoiceNode> toExclude) {
        if (fNoConstraints)
            return;
        if (fIsContradicting)
            return;

        List<MethodParameterNode> methodParameterNodes = fMethodNode.getMethodParameters();

        for (MethodParameterNode methodParameterNode : methodParameterNodes)
            prepareVariablesForParameter(
                    methodParameterNode,
                    fArgAllAtomicValues,
                    fFirstFreeIDHolder,
                    fArgAllSanitizedValues,
                    fSanitizedValToAtomicVal,
                    fSat4Clauses,
                    fArgAllInputValues,
                    fArgInputValToSanitizedVal,
                    fArgLessEqChoiceID,
                    fArgLessThChoiceID,
                    fArgChoiceID
            );

        VecInt excludeClause = new VecInt(assumptionsFromValues(toExclude).stream().map(x -> -x).mapToInt(Integer::intValue).toArray());
        final int maxVar = fFirstFreeIDHolder.get();

        try {
            fSolver.newVar(maxVar);
            fSolver.addClause(excludeClause);
        } catch (ContradictionException e) {
            fIsContradicting = true;
        }

    }

    @Override
    public EvaluationResult evaluate(List<ChoiceNode> valueAssignment) {
        if (fNoConstraints) {
            return EvaluationResult.TRUE; //no method so there were no constraints
        }

        if (fIsContradicting)
            return EvaluationResult.FALSE;


        try {
            IProblem problem = fSolver;
            if (problem.isSatisfiable(new VecInt(assumptionsFromValues(valueAssignment).stream().mapToInt(Integer::intValue).toArray()))) {
                return EvaluationResult.TRUE;
            } else {
                return EvaluationResult.FALSE;
            }
        } catch (TimeoutException e) {
            ExceptionHelper.reportRuntimeException("Timeout, sorry!");
            return null;
        }

    }


    @Override
    public List<ChoiceNode> adapt(List<ChoiceNode> valueAssignment) {
        if (fNoConstraints)
            return valueAssignment;

        try {
            IProblem problem = fSolver;
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

}
