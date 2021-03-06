package com.ecfeed.core.evaluator;

import static com.ecfeed.core.utils.EMathRelation.EQUAL;
import static com.ecfeed.core.utils.EMathRelation.GREATER_EQUAL;
import static com.ecfeed.core.utils.EMathRelation.LESS_EQUAL;
import static com.ecfeed.core.utils.EMathRelation.LESS_THAN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceCondition;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoiceNodeComparator;
import com.ecfeed.core.model.ChoiceNodeHelper;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.IStatementVisitor;
import com.ecfeed.core.model.LabelCondition;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ParameterCondition;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.ValueCondition;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;

class ParseConstraintToSATVisitor implements IStatementVisitor {

    private MethodNode fMethodNode;

    private EcSatSolver fSat4Solver;
    private ParamChoiceSets fParamChoiceSets;
//    private ChoiceMultiMapping fSanitizedValToAtomicVal;
    private ChoicesMappingsBucket fChoicesMappingBucket;
    private ChoiceToSolverIdMappings fChoiceToSolverIdMappings;


    public ParseConstraintToSATVisitor(
            MethodNode methodNode,
            EcSatSolver sat4Solver,
            ParamChoiceSets paramChoiceSets,
            ChoicesMappingsBucket choicesMappingsBucket,
            ChoiceToSolverIdMappings choiceToSolverIdMappings) {

        fMethodNode = methodNode;
        fSat4Solver = sat4Solver;
        fParamChoiceSets = paramChoiceSets;
        fChoicesMappingBucket = choicesMappingsBucket;

        fChoiceToSolverIdMappings = choiceToSolverIdMappings;
    }

    @Override
    public Object visit(StatementArray statement) {
        Integer myID = fSat4Solver.newId();
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
                                        fSat4Solver,
                                        fParamChoiceSets,
                                        fChoicesMappingBucket,
                                        fChoiceToSolverIdMappings));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bigClause.add(childID);
                    fSat4Solver.addSat4Clause(new int[]{-childID, myID}); //small fClauses
                }
                bigClause.add(-myID);
                fSat4Solver.addSat4Clause(bigClause.stream().mapToInt(Integer::intValue).toArray());
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
                                        fSat4Solver,
                                        fParamChoiceSets,
                                        fChoicesMappingBucket,
                                        fChoiceToSolverIdMappings));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bigClause.add(-childID);
                    fSat4Solver.addSat4Clause(new int[]{childID, -myID}); //small fClauses
                }
                bigClause.add(myID);
                fSat4Solver.addSat4Clause(bigClause.stream().mapToInt(Integer::intValue).toArray());
                break;
            }
            case ASSIGN:
            	break;
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
                (JavaLanguageHelper.isExtendedIntTypeName(statement.getLeftParameter().getType())
                        || JavaLanguageHelper.isFloatingPointTypeName(statement.getLeftParameter().getType()))
        ) {
            switch (statement.getRelation()) {
            	case ASSIGN:
            		return null;
                case GREATER_THAN:
                case LESS_EQUAL: {
                    RelationStatement statement1 = statement.makeClone();
                    ChoiceNode val = ((ChoiceCondition) statement.getCondition()).getRightChoice();
                    val = ChoiceNodeHelper.rangeSplit(val).getSecond();
                    statement1.setCondition(val);
                    return singleChoiceParamConstraints(statement);
                }
                case GREATER_EQUAL:
                case LESS_THAN: {
                    RelationStatement statement1 = statement.makeClone();
                    ChoiceNode val = ((ChoiceCondition) statement.getCondition()).getRightChoice();
                    val = ChoiceNodeHelper.rangeSplit(val).getFirst();
                    statement1.setCondition(val);
                    return singleChoiceParamConstraints(statement);
                }
                case NOT_EQUAL:
                case EQUAL: {
                    RelationStatement statementLow = statement.makeClone();
                    RelationStatement statementHigh = statement.makeClone();
                    ChoiceNode val = ((ChoiceCondition) statement.getCondition()).getRightChoice();
                    ChoiceNode valLow = ChoiceNodeHelper.rangeSplit(val).getFirst();
                    ChoiceNode valHigh = ChoiceNodeHelper.rangeSplit(val).getSecond();
                    statementLow.setCondition(valLow);
                    statementHigh.setCondition(valHigh);
                    statementLow.setRelation(GREATER_EQUAL);
                    statementHigh.setRelation(LESS_EQUAL);

                    Integer statementLowID = singleChoiceParamConstraints(statementLow);
                    Integer statementHighID = singleChoiceParamConstraints(statementHigh);

                    Integer myID = fSat4Solver.newId();

                    fSat4Solver.addSat4Clause(new int[]{-statementLowID, -statementHighID, myID});
                    fSat4Solver.addSat4Clause(new int[]{-myID, statementLowID});
                    fSat4Solver.addSat4Clause(new int[]{-myID, statementHighID});
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
                fParamChoiceSets,
                fSat4Solver,
                fChoicesMappingBucket,
                fChoiceToSolverIdMappings);

        Integer myID = fSat4Solver.newId();

        int lParamIndex = fMethodNode.getMethodParameters().indexOf(leftMethodParameterNode);
        if (lParamIndex == -1) {
            reportParamWithoutMethodException();
        }
        for (ChoiceNode lChoice : fParamChoiceSets.atomicGet(leftMethodParameterNode)) {
            List<ChoiceNode> dummyValues = new ArrayList<>(Collections.nCopies(fMethodNode.getParametersCount(), null));
            dummyValues.set(lParamIndex, lChoice);
            EvaluationResult result = statement.evaluate(dummyValues);
            Integer idOfLeftArgChoice = fChoiceToSolverIdMappings.eqGet(leftMethodParameterNode).get(lChoice);
            if (result == EvaluationResult.TRUE) {
                fSat4Solver.addSat4Clause(new int[]{-idOfLeftArgChoice, myID}); // thisChoice => me
            } else if (result == EvaluationResult.FALSE) {
                fSat4Solver.addSat4Clause(new int[]{-idOfLeftArgChoice, -myID}); // thisChoice => NOT me
            } else //INSUFFICIENT_DATA
            {
                ExceptionHelper.reportRuntimeException("Insufficient data.");
            }
        }

        return myID;
    }

    private Integer doubleChoiceParamConstraints(RelationStatement statement) {
        MethodParameterNode lParam = statement.getLeftParameter();
        EvaluatorHelper.prepareVariablesForParameter(
                lParam,
                fParamChoiceSets,
                fSat4Solver,
                fChoicesMappingBucket,
                fChoiceToSolverIdMappings);

        Integer myID = fSat4Solver.newId();

        int lParamIndex = fMethodNode.getMethodParameters().indexOf(lParam);
        if (lParamIndex == -1) {
            reportParamWithoutMethodException();
        }
        MethodParameterNode rParam = ((ParameterCondition) statement.getCondition()).getRightParameterNode();

        EvaluatorHelper.prepareVariablesForParameter(
                rParam,
                fParamChoiceSets,
                fSat4Solver,
                fChoicesMappingBucket,
                fChoiceToSolverIdMappings);

        int rParamIndex = fMethodNode.getMethodParameters().indexOf(rParam);
        if (rParamIndex == -1) {
            reportParamWithoutMethodException();
        }

        List<ChoiceNode> sortedLChoices = new ArrayList<>(fParamChoiceSets.atomicGet(lParam));
        Collections.sort(sortedLChoices, new ChoiceNodeComparator());
        int m = sortedLChoices.size();
        List<ChoiceNode> sortedRChoices = new ArrayList<>(fParamChoiceSets.atomicGet(rParam));
        Collections.sort(sortedRChoices, new ChoiceNodeComparator());
        int n = sortedRChoices.size();

        for (int i = 0, j = 0; i < m; i++) {
            while (j < n && new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) > 0) {
                j++;
            }

            Integer leftLessTh = fChoiceToSolverIdMappings.ltGet(lParam).get(sortedLChoices.get(i));
            Integer leftLessEq = fChoiceToSolverIdMappings.leGet(lParam).get(sortedLChoices.get(i));
            Integer rightLessTh = null;
            Integer rightLessEq = null;
            if (j < n) {
                rightLessTh = fChoiceToSolverIdMappings.ltGet(rParam).get(sortedRChoices.get(j));
                rightLessEq = fChoiceToSolverIdMappings.leGet(rParam).get(sortedRChoices.get(j));
            }

            switch (statement.getRelation()) {
        	case ASSIGN:
        		break;

                case EQUAL:
                case NOT_EQUAL: //negated at return
                {
                    if (j == n) {
                        // NOT(i<x) IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -myID});

                        break;
                    } else if (new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {

                        // NOT(i<x) AND i<=x IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -leftLessEq, -myID});
                    } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                    {
                        // NOT(i<x) AND i<=x AND NOT(j<y) AND j<=y IMPLIES myID
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -leftLessEq, rightLessTh, -rightLessEq, myID});

                        // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID});

                        // NOT(i<x) AND i<=x AND NOT(j<=y) IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -leftLessEq, rightLessEq, -myID});
                    }
                    break;
                }

                case LESS_THAN:
                case GREATER_EQUAL: //negated at return
                {
                    if (j == n) {
                        // NOT(i<x) IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -myID});

                        break;
                    } else if (new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {
                        // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID});

                        // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID});
                    } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                    {
                        // NOT(i<x) AND i<=x AND NOT(j<=y) IMPLIES myID
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -leftLessEq, rightLessEq, myID});

                        // NOT(i<x) AND i<=x AND j<=y IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -leftLessEq, -rightLessEq, -myID});
                    }
                    break;
                }
                case LESS_EQUAL:
                case GREATER_THAN: //negated at return
                {
                    if (j == n) {
                        // NOT(i<x) IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -myID});

                        break;
                    } else if (new ChoiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {
                        // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID});

                        // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID});
                    } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                    {
                        // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID});

                        // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                        fSat4Solver.addSat4Clause(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID});
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
        Integer myID = fSat4Solver.newId();
        if (statement.getValue() == EvaluationResult.TRUE)
            fSat4Solver.addSat4Clause(new int[]{myID});
        else
            fSat4Solver.addSat4Clause(new int[]{-myID});
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
