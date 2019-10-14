package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IntegerHolder;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.google.common.collect.Multimap;
import org.sat4j.core.VecInt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ecfeed.core.utils.EMathRelation.*;
import static com.ecfeed.core.utils.EMathRelation.LESS_EQUAL;

class ParseConstraintToSATVisitor implements IStatementVisitor {

    private MethodNode fMethodNode;

    private IntegerHolder fFirstFreeIDHolder;
    private EcSatSolver fSat4Solver;
    private ParamsWithChoices fArgAllAtomicValues;
    private ParamsWithChoices fArgAllSanitizedValues;
    private ChoiceMultiMappings fSanitizedValToAtomicVal;
    private ParamsWithChoices fArgAllInputValues;
    private Map<MethodParameterNode, Multimap<ChoiceNode, ChoiceNode>> fArgInputValToSanitizedVal;

    private ParamsWithChInts fArgLessEqChoiceID;
    private ParamsWithChInts fArgLessThChoiceID;
    private ParamsWithChInts fChoiceToSolverIdMappings;


    public ParseConstraintToSATVisitor(
            MethodNode methodNode,
            IntegerHolder firstFreeIDHolder,
            EcSatSolver sat4Solver,
            ParamsWithChoices allAtomicValues,
            ParamsWithChoices allSanitizedValues,
            ChoiceMultiMappings sanitizedValToAtomicVal,
            ParamsWithChoices allInputValues,
            Map<MethodParameterNode, Multimap<ChoiceNode, ChoiceNode>> inputValToSanitizedVal,
            ParamsWithChInts lessEqChoiceID,
            ParamsWithChInts lessThChoiceID,
            ParamsWithChInts choiceToSolverIdMappings
    ) {

        fMethodNode = methodNode;
        fFirstFreeIDHolder = firstFreeIDHolder;
        fSat4Solver = sat4Solver;
        fArgAllAtomicValues = allAtomicValues;
        fArgAllSanitizedValues = allSanitizedValues;
        fSanitizedValToAtomicVal = sanitizedValToAtomicVal;
        fArgAllInputValues = allInputValues;
        fArgInputValToSanitizedVal = inputValToSanitizedVal;

        fArgLessEqChoiceID = lessEqChoiceID;
        fArgLessThChoiceID = lessThChoiceID;
        fChoiceToSolverIdMappings = choiceToSolverIdMappings;
    }

    @Override
    public Object visit(StatementArray statement) {
        Integer myID = EvaluatorHelper.newId(fFirstFreeIDHolder);
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
                                        fSat4Solver,
                                        fArgAllAtomicValues,
                                        fArgAllSanitizedValues,
                                        fSanitizedValToAtomicVal,
                                        fArgAllInputValues,
                                        fArgInputValToSanitizedVal,
                                        fArgLessEqChoiceID,
                                        fArgLessThChoiceID,
                                        fChoiceToSolverIdMappings));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bigClause.add(childID);
                    fSat4Solver.addSat4Clause(new VecInt(new int[]{-childID, myID})); //small fClauses
                }
                bigClause.add(-myID);
                fSat4Solver.addSat4Clause(new VecInt(bigClause.stream().mapToInt(Integer::intValue).toArray()));
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
                                        fSat4Solver,
                                        fArgAllAtomicValues,
                                        fArgAllSanitizedValues,
                                        fSanitizedValToAtomicVal,
                                        fArgAllInputValues,
                                        fArgInputValToSanitizedVal,
                                        fArgLessEqChoiceID,
                                        fArgLessThChoiceID,
                                        fChoiceToSolverIdMappings));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bigClause.add(-childID);
                    fSat4Solver.addSat4Clause(new VecInt(new int[]{childID, -myID})); //small fClauses
                }
                bigClause.add(myID);
                fSat4Solver.addSat4Clause(new VecInt(bigClause.stream().mapToInt(Integer::intValue).toArray()));
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

                    Integer myID = EvaluatorHelper.newId(fFirstFreeIDHolder);

                    fSat4Solver.addSat4Clause(new VecInt(new int[]{-statementLowID, -statementHighID, myID}));
                    fSat4Solver.addSat4Clause(new VecInt(new int[]{-myID, statementLowID}));
                    fSat4Solver.addSat4Clause(new VecInt(new int[]{-myID, statementHighID}));
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
        ExceptionHelper.reportRuntimeException("Invalid relation type.");
        return null;
    }

    private Integer singleChoiceParamConstraints(RelationStatement statement) {
        MethodParameterNode leftMethodParameterNode = statement.getLeftParameter();

        EvaluatorHelper.prepareVariablesForParameter(
                leftMethodParameterNode,
                fArgAllAtomicValues,
                fFirstFreeIDHolder,
                fArgAllSanitizedValues,
                fSanitizedValToAtomicVal,
                fSat4Solver,
                fArgAllInputValues,
                fArgInputValToSanitizedVal,
                fArgLessEqChoiceID,
                fArgLessThChoiceID,
                fChoiceToSolverIdMappings
        );

        Integer myID = EvaluatorHelper.newId(fFirstFreeIDHolder);

        int lParamIndex = fMethodNode.getMethodParameters().indexOf(leftMethodParameterNode);
        if (lParamIndex == -1) {
            reportParamWithoutMethodException();
        }
        for (ChoiceNode lChoice : fArgAllAtomicValues.get(leftMethodParameterNode)) {
            List<ChoiceNode> dummyValues = new ArrayList<>(Collections.nCopies(fMethodNode.getParametersCount(), null));
            dummyValues.set(lParamIndex, lChoice);
            EvaluationResult result = statement.evaluate(dummyValues);
            Integer idOfLeftArgChoice = fChoiceToSolverIdMappings.get(leftMethodParameterNode).get(lChoice);
            if (result == EvaluationResult.TRUE) {
                fSat4Solver.addSat4Clause(new VecInt(new int[]{-idOfLeftArgChoice, myID})); // thisChoice => me
            } else if (result == EvaluationResult.FALSE) {
                fSat4Solver.addSat4Clause(new VecInt(new int[]{-idOfLeftArgChoice, -myID})); // thisChoice => NOT me
            } else //INSUFFICIENT_DATA
            {
                ExceptionHelper.reportRuntimeException("Insufficient data.");
            }
        }

        return myID;
    }

    private Integer doubleChoiceParamConstraints(RelationStatement statement) {
        MethodParameterNode lParam = statement.getLeftParameter();
        EvaluatorHelper.prepareVariablesForParameter(lParam,
                fArgAllAtomicValues,
                fFirstFreeIDHolder,
                fArgAllSanitizedValues,
                fSanitizedValToAtomicVal,
                fSat4Solver,
                fArgAllInputValues,
                fArgInputValToSanitizedVal,
                fArgLessEqChoiceID,
                fArgLessThChoiceID,
                fChoiceToSolverIdMappings
        );

        Integer myID = EvaluatorHelper.newId(fFirstFreeIDHolder);

        int lParamIndex = fMethodNode.getMethodParameters().indexOf(lParam);
        if (lParamIndex == -1) {
            reportParamWithoutMethodException();
        }
        MethodParameterNode rParam = ((ParameterCondition) statement.getCondition()).getRightParameterNode();

        EvaluatorHelper.prepareVariablesForParameter(
                rParam,
                fArgAllAtomicValues,
                fFirstFreeIDHolder,
                fArgAllSanitizedValues,
                fSanitizedValToAtomicVal,
                fSat4Solver,
                fArgAllInputValues,
                fArgInputValToSanitizedVal,
                fArgLessEqChoiceID,
                fArgLessThChoiceID,
                fChoiceToSolverIdMappings
        );

        int rParamIndex = fMethodNode.getMethodParameters().indexOf(rParam);
        if (rParamIndex == -1) {
            reportParamWithoutMethodException();
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
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -myID}));

                        break;
                    } else if (new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {

                        // NOT(i<x) AND i<=x IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -leftLessEq, -myID}));
                    } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                    {
                        // NOT(i<x) AND i<=x AND NOT(j<y) AND j<=y IMPLIES myID
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, -rightLessEq, myID}));

                        // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));

                        // NOT(i<x) AND i<=x AND NOT(j<=y) IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessEq, -myID}));
                    }
                    break;
                }

                case LESS_THAN:
                case GREATER_EQUAL: //negated at return
                {
                    if (j == n) {
                        // NOT(i<x) IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -myID}));

                        break;
                    } else if (new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {
                        // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID}));

                        // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));
                    } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                    {
                        // NOT(i<x) AND i<=x AND NOT(j<=y) IMPLIES myID
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessEq, myID}));

                        // NOT(i<x) AND i<=x AND j<=y IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessEq, -myID}));
                    }
                    break;
                }
                case LESS_EQUAL:
                case GREATER_THAN: //negated at return
                {
                    if (j == n) {
                        // NOT(i<x) IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -myID}));

                        break;
                    } else if (new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {
                        // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID}));

                        // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));
                    } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                    {
                        // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID}));

                        // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));
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
        Integer myID = EvaluatorHelper.newId(fFirstFreeIDHolder);
        if (statement.getValue() == EvaluationResult.TRUE)
            fSat4Solver.addSat4Clause(new VecInt(new int[]{myID}));
        else
            fSat4Solver.addSat4Clause(new VecInt(new int[]{-myID}));
        return myID;
    }

    @Override
    public Object visit(LabelCondition statement) {
        reportInvalidConditionTypeException();
        return null; //this will never happen
    }

    @Override
    public Object visit(ChoiceCondition statement) {
        reportInvalidConditionTypeException();
        return null; //this will never happen
    }

    @Override
    public Object visit(ParameterCondition statement) {
        reportInvalidConditionTypeException();
        return null; //this will never happen
    }

    @Override
    public Object visit(ValueCondition statement) {
        reportInvalidConditionTypeException();
        return null; //this will never happen
    }

    private void reportInvalidConditionTypeException() {
        ExceptionHelper.reportRuntimeException("Invalid condition type.");
    }

    private void reportParamWithoutMethodException() {
        ExceptionHelper.reportRuntimeException("Parameter without method found.");
    }

}
