package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.*;
import com.google.common.primitives.Ints;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.*;

import static com.ecfeed.core.utils.EMathRelation.*;

public class Sat4jEvaluator implements IConstraintEvaluator<ChoiceNode> {

    private ArrayList<VecInt>  fClausesVecInt; //internal type for Sat4j
    private int lastUsedID = 1;
    private Map<MethodParameterNode, SortedSet<ChoiceNode>> argAllValues;
    private Map<MethodParameterNode, Map<ChoiceNode, Integer>> argEqualChoiceID;
    private Map<MethodParameterNode, Map<ChoiceNode, Integer>> argLessEqChoiceID;
    private Map<MethodParameterNode, Map<ChoiceNode, Integer>> argLessThChoiceID;
    private List<Pair<Integer, ExpectedValueStatement>> expValConstraints;
    private MethodNode fMethod;
    private ISolver fSolver;
    private Boolean isContradicting = false;



    public Sat4jEvaluator(Collection<Constraint> initConstraints, MethodNode method) {
        argEqualChoiceID = new HashMap<>();
        argLessEqChoiceID = new HashMap<>();
        argLessThChoiceID = new HashMap<>();
        fClausesVecInt = new ArrayList<>();
        argAllValues = new HashMap<>();
        expValConstraints = new ArrayList<>();
        fMethod = method;
        if(fMethod == null && !initConstraints.isEmpty()) {
            ExceptionHelper.reportRuntimeException("No method but there were constraints!");
        }
        if(initConstraints != null) {
            for (Constraint constraint : initConstraints) {
                ParseConstraintForValues(constraint);
            }

            for (Constraint constraint : initConstraints) {
                ParseConstraintToSAT(constraint);
            }
        }
        final int maxVar = lastUsedID;
        final int nbClauses = fClausesVecInt.size();
        fSolver = SolverFactory.newDefault();


        try {
            fSolver.newVar(maxVar);
            fSolver.setExpectedNumberOfClauses(nbClauses);
            for(VecInt clause : fClausesVecInt)
                fSolver.addClause(clause);
            System.out.println("variables: " + maxVar + " clauses: " + nbClauses);
        } catch (ContradictionException e)
        {
            isContradicting = true;
        }
    }

    private int newID()
    {
        lastUsedID++;
        return lastUsedID;
    }

    private void variablesForParameter(MethodParameterNode arg)
    {
        if(argEqualChoiceID.containsKey(arg))
            return;

        //we need to create new set of variables, as we are seeing this parameter for the first time
        //choiceVars control whether a choice is taken
        //prefixVars are used to enforce uniqueness of choice
        ArrayList<Integer> choiceVars = new ArrayList<>(); //choiceVars[i] ==  (this parameter takes choice i)
        ArrayList<Integer> prefixVars = new ArrayList<>(); //prefixVars[i] == (this parameter takes one of choices 0,...,i)
        ArrayList<Integer> lessEqVars = new ArrayList<>(); //lessEqVars[i] == (this parameter <= value at i)
        ArrayList<Integer> lessThVars = new ArrayList<>(); //lessThVars[i] == (this parameter < value at i)
        HashMap<ChoiceNode, Integer> inverseEqVars = new HashMap<>();
        HashMap<ChoiceNode, Integer> inverseLEqVars = new HashMap<>();
        HashMap<ChoiceNode, Integer> inverseLThVars = new HashMap<>();

        ArrayList<ChoiceNode> sortedChoices = new ArrayList<>(argAllValues.get(arg));
        Collections.sort(sortedChoices, new choiceNodeComparator());

        int n = sortedChoices.size();

        prefixVars.add(newID());
        for(int i=0; i<n; i++)
        {
            choiceVars.add(newID());
            prefixVars.add(newID());
        }

        fClausesVecInt.add(new VecInt(new int[]{-prefixVars.get(0)}));
        fClausesVecInt.add(new VecInt(new int[]{prefixVars.get(n)})); //at least one value should be taken
        for (int i = 0; i < n; i++) {
            // prefixVars[i+1] == prefixVars[i] OR choiceVars[i]
            fClausesVecInt.add(new VecInt(new int[]{-choiceVars.get(i), prefixVars.get(i+1)})); // choiceVars[i] => prefixVars[i];
            fClausesVecInt.add(new VecInt(new int[]{-prefixVars.get(i), prefixVars.get(i + 1)})); // prefixVars[i] => prefixVars[i+1];
            fClausesVecInt.add(new VecInt(new int[]{choiceVars.get(i), prefixVars.get(i), -prefixVars.get(i+1)})); // enforcing that last one is true only when at least one of first+second is true

            fClausesVecInt.add(new VecInt(new int[]{-choiceVars.get(i), -prefixVars.get(i)})); // NOT( choiceVars[i] AND prefixVars[i] ), to guarantee uniqueness
        }

        for(int i=0;i<n;i++)
        {
            lessEqVars.add(prefixVars.get(i+1));
            lessThVars.add(prefixVars.get(i));
        }
        for(int i=1;i<n;i++) //all elements except first one
            if(new choiceNodeComparator().compare(sortedChoices.get(i-1),sortedChoices.get(i))==0)
                lessThVars.set(i, lessThVars.get(i-1));

        for(int i=n-1;i>0;i--) //all elements except first one, in reverse
            if(new choiceNodeComparator().compare(sortedChoices.get(i-1),sortedChoices.get(i))==0)
                lessEqVars.set(i-1, lessEqVars.get(i));

        for(int i=0;i<n;i++)
        {
            ChoiceNode choice = sortedChoices.get(i);
            inverseEqVars.put(choice, choiceVars.get(i));
            inverseLEqVars.put(choice, lessEqVars.get(i));
            inverseLThVars.put(choice, lessThVars.get(i));
        }

        argEqualChoiceID.put(arg, inverseEqVars);
        argLessEqChoiceID.put(arg, inverseLEqVars);
        argLessThChoiceID.put(arg, inverseLThVars);
    }

    class choiceNodeComparator implements Comparator<ChoiceNode> {
        public int compare(ChoiceNode leftArg, ChoiceNode rightArg) {
            String substituteType = JavaTypeHelper.getSubstituteType(leftArg.getParameter().getType(), rightArg.getParameter().getType());
            if( RelationMatcher.isRelationMatch(EMathRelation.LESS_THAN, substituteType, leftArg.getValueString(), rightArg.getValueString()) )
                return -1;
            else if( RelationMatcher.isRelationMatch(EMathRelation.GREATER_THAN, substituteType, leftArg.getValueString(), rightArg.getValueString()) )
                return 1;
            else
                return 0;
        }
    }

    private void ParseConstraintForValues(Constraint constraint) {
        if(constraint != null) {
            AbstractStatement premise = constraint.getPremise(), consequence = constraint.getConsequence();
            if(consequence instanceof ExpectedValueStatement)
            {
                try {
                    premise.accept(new ParseConstraintForValuesVisitor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    premise.accept(new ParseConstraintForValuesVisitor());
                    consequence.accept(new ParseConstraintForValuesVisitor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    class ParseConstraintForValuesVisitor implements IStatementVisitor
    {
        @Override
        public Object visit(StatementArray statement)
        {
            for (AbstractStatement child : statement.getChildren()) {
                try {
                    child.accept(new ParseConstraintToSATVisitor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        public Object visit(RelationStatement statement)
        {
            MethodParameterNode lParam = statement.getLeftParameter();
            if(!argAllValues.containsKey(lParam)) {
                SortedSet<ChoiceNode> tmp = new TreeSet<>(new choiceNodeComparator());
                tmp.addAll(lParam.getLeafChoices());
                argAllValues.put(lParam, tmp);
            }
            if(statement.getCondition() instanceof ParameterCondition) {

                MethodParameterNode rParam = ((ParameterCondition) statement.getCondition()).getRightParameterNode();
                if(!argAllValues.containsKey(rParam)) {
                    SortedSet<ChoiceNode> tmp = new TreeSet<>(new choiceNodeComparator());
                    tmp.addAll(lParam.getLeafChoices());
                    argAllValues.put(rParam, tmp);
                }
            }
            return null;
        }
        @Override
        public Object visit(StaticStatement statement)
        {
            return null;
        }


        @Override
        public Object visit(ExpectedValueStatement statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null;
        }
        @Override
        public Object visit(LabelCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
        @Override
        public Object visit(ChoiceCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
        @Override
        public Object visit(ParameterCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
        @Override
        public Object visit(ValueCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
    }


    private void ParseConstraintToSAT(Constraint constraint) {
        if(constraint != null) {
            AbstractStatement premise = constraint.getPremise(), consequence = constraint.getConsequence();
            if(consequence instanceof ExpectedValueStatement) {
                Integer premiseID = null;
                try {
                    premiseID = (Integer) premise.accept(new ParseConstraintToSATVisitor());
                    expValConstraints.add(new Pair<>(premiseID, (ExpectedValueStatement) consequence));
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else {
                Integer premiseID = null, consequenceID = null;
                try {
                    premiseID = (Integer) premise.accept(new ParseConstraintToSATVisitor());
                    consequenceID = (Integer) consequence.accept(new ParseConstraintToSATVisitor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fClausesVecInt.add(new VecInt(new int[] {-premiseID,consequenceID}));
            }
        }
    }

    class ParseConstraintToSATVisitor implements IStatementVisitor
    {
        @Override
        public Object visit(StatementArray statement)
        {
            Integer myID = newID();
            switch(statement.getOperator()){
                case OR: // y = (x1 OR x2 OR .. OR xn) compiles to: (NOT x1 OR y) AND ... AND (NOT xn OR y) AND (x1 OR ... OR xn OR NOT y)
                {
                    ArrayList<Integer> bigClause = new ArrayList<>();
                    for (AbstractStatement child : statement.getChildren()) {
                        Integer childID = null;
                        try {
                            childID = (Integer) child.accept(new ParseConstraintToSATVisitor());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        bigClause.add(childID);
                        fClausesVecInt.add(new VecInt(new int[] {-childID, myID})); //small fClauses
                    }
                    bigClause.add(-myID);
                    fClausesVecInt.add(new VecInt(bigClause.stream().mapToInt(Integer::intValue).toArray()));
                    break;
                }
                case AND: // y = (x1 AND x2 AND .. AND xn) compiles to: (x1 OR NOT y) AND ... AND (xn OR NOT y) AND (NOT x1 OR ... OR NOT xn OR y)
                {
                    ArrayList<Integer> bigClause = new ArrayList<>();
                    for (AbstractStatement child : statement.getChildren()) {
                        Integer childID = null;
                        try {
                            childID = (Integer) child.accept(new ParseConstraintToSATVisitor());
                        } catch (Exception e) {
                        }
                        bigClause.add(-childID);
                        fClausesVecInt.add(new VecInt(new int[] {childID, -myID})); //small fClauses
                    }
                    bigClause.add(myID);
                    fClausesVecInt.add(new VecInt(bigClause.stream().mapToInt(Integer::intValue).toArray()));
                    break;
                }
            }
//            statementToID.put(statement,myID); not really necessary, as we are never reusing the same statements
            return myID;
        }
        @Override
        public Object visit(RelationStatement statement)
        {
            if(statement.getCondition() instanceof ParameterCondition) {
                return doubleChoiceParamConstraints(statement);
            }
            else { //we need only to iterate over all choices of single lParam
                return singleChoiceParamConstraints(statement);
            }
        }

        private Integer singleChoiceParamConstraints(RelationStatement statement)
        {
            MethodParameterNode lParam = statement.getLeftParameter();
            variablesForParameter(lParam);
            Integer myID = newID();

            int lParamIndex = fMethod.getMethodParameters().indexOf(lParam);
            if (lParamIndex == -1) {
                ExceptionHelper.reportRuntimeException("Parameter not in method!");
            }
            for (ChoiceNode lChoice : argAllValues.get(lParam)) {
                ArrayList<ChoiceNode> dummyValues = new ArrayList<>(Collections.nCopies(fMethod.getParametersCount(), null));
                dummyValues.set(lParamIndex, lChoice);
                EvaluationResult result = statement.evaluate(dummyValues);
                Integer idOfLeftArgChoice = argEqualChoiceID.get(lParam).get(lChoice);
                if (result == EvaluationResult.TRUE) {
                    fClausesVecInt.add(new VecInt(new int[]{-idOfLeftArgChoice, myID})); // thisChoice => me
                } else if (result == EvaluationResult.FALSE) {
                    fClausesVecInt.add(new VecInt(new int[]{-idOfLeftArgChoice, -myID})); // thisChoice => NOT me
                } else //INSUFFICIENT_DATA
                {
                    ExceptionHelper.reportRuntimeException("INSUFFICIENT_DATA: You shouldn't be here!");
                }
            }

            return myID;
        }

        private Integer doubleChoiceParamConstraints(RelationStatement statement)
        {
            MethodParameterNode lParam = statement.getLeftParameter();
            variablesForParameter(lParam);
            Integer myID = newID();

            int lParamIndex = fMethod.getMethodParameters().indexOf(lParam);
            if (lParamIndex == -1) {
                ExceptionHelper.reportRuntimeException("Parameter not in method!");
            }
            MethodParameterNode rParam = ((ParameterCondition) statement.getCondition()).getRightParameterNode();

            variablesForParameter(rParam);

            int rParamIndex = fMethod.getMethodParameters().indexOf(rParam);
            if (rParamIndex == -1) {
                ExceptionHelper.reportRuntimeException("Parameter not in method!");
            }

            List<ChoiceNode> sortedLChoices = new ArrayList<>(argAllValues.get(lParam));
            int m = sortedLChoices.size();
            List<ChoiceNode> sortedRChoices = new ArrayList<>(argAllValues.get(rParam));
            int n = sortedRChoices.size();

            for(int i=0,j=0; i<m; i++) {
                while (j < n && new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) > 0) {
                    j++;
                }

                Integer leftLessTh = argLessThChoiceID.get(lParam).get(sortedLChoices.get(i));
                Integer leftLessEq = argLessEqChoiceID.get(lParam).get(sortedLChoices.get(i));
                Integer rightLessTh = argLessThChoiceID.get(rParam).get(sortedRChoices.get(j));
                Integer rightLessEq = argLessEqChoiceID.get(rParam).get(sortedRChoices.get(j));

                switch (statement.getRelation()) {
                    case EQUAL:
                    case NOT_EQUAL: //negated at return
                    {
                        if (j == n) {
                            // NOT(i<x) IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -myID}));

                            break;
                        } else if (new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {

                            // NOT(i<x) AND i<=x IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -myID}));
                        } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                        {
                            // NOT(i<x) AND i<=x AND NOT(j<y) AND j<=y IMPLIES myID
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, -rightLessEq, myID}));

                            // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));

                            // NOT(i<x) AND i<=x AND NOT(j<=y) IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessEq, -myID}));
                        }
                        break;
                    }

                    case LESS_THAN:
                    case GREATER_EQUAL: //negated at return
                    {
                        if (j == n) {
                            // NOT(i<x) IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -myID}));

                            break;
                        } else if (new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {
                            // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID}));

                            // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));
                        } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                        {
                            // NOT(i<x) AND i<=x AND NOT(j<=y) IMPLIES myID
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessEq, myID}));

                            // NOT(i<x) AND i<=x AND j<=y IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessEq, -myID}));
                        }
                        break;
                    }
                    case LESS_EQUAL:
                    case GREATER_THAN: //negated at return
                    {
                        if (j == n) {
                            // NOT(i<x) IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -myID}));

                            break;
                        } else if (new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) < 0) {
                            // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID}));

                            // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));
                        } else // new choiceNodeComparator().compare(sortedLChoices.get(i), sortedRChoices.get(j)) == 0
                        {
                            // NOT(i<x) AND i<=x AND NOT(j<y) IMPLIES myID
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, rightLessTh, myID}));

                            // NOT(i<x) AND i<=x AND j<y IMPLIES NOT(myID)
                            fClausesVecInt.add(new VecInt(new int[]{leftLessTh, -leftLessEq, -rightLessTh, -myID}));
                        }
                        break;
                    }
                }
            }

            if(statement.getRelation() == EQUAL || statement.getRelation() == LESS_THAN || statement.getRelation() == LESS_EQUAL)
                return myID;
            else
                return -myID;
        }

        @Override
        public Object visit(ExpectedValueStatement statement)
        {
            return null; //TODO
        }

        @Override
        public Object visit(StaticStatement statement)
        {
            Integer myID = newID();
            if(statement.getValue() == EvaluationResult.TRUE)
                fClausesVecInt.add(new VecInt(new int[] {myID}));
            else
                fClausesVecInt.add(new VecInt(new int[] {-myID}));
            return myID;
        }

        @Override
        public Object visit(LabelCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
        @Override
        public Object visit(ChoiceCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
        @Override
        public Object visit(ParameterCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
        @Override
        public Object visit(ValueCondition statement)
        {
            ExceptionHelper.reportRuntimeException("You shouldn't be here!");
            return null; //this will never happen
        }
    }

    private List<Integer> assumptionsFromValues(List<ChoiceNode> valueAssignment)
    {

        List<MethodParameterNode> params = fMethod.getMethodParameters();

        List<Integer> assumptions = new ArrayList<>();

        //iterate params and valueAssignment simultanously
        Iterator<ChoiceNode> cChoiceNode = valueAssignment.iterator();
        for(MethodParameterNode p : params) {
            if(!cChoiceNode.hasNext())
            {
                ExceptionHelper.reportRuntimeException("Lists were supposed to be of equal length!");
                return null;
            }
            ChoiceNode c = cChoiceNode.next();
            if(c!=null) {
                if(argEqualChoiceID.get(p)==null)
                    continue; //no constraint on this method parameter
                Integer idOfParamChoiceVar = argEqualChoiceID.get(p).get(c);
                assumptions.add(idOfParamChoiceVar);
            }
        }

        if(cChoiceNode.hasNext()) {
            ExceptionHelper.reportRuntimeException("Lists were supposed to be of equal length!");
            return null;
        }

        return assumptions;
    }

    @Override
    public EvaluationResult evaluate(List<ChoiceNode> valueAssignment)
    {
        if(fMethod == null)
        {
            return EvaluationResult.TRUE; //no method so there were no constraints
        }

        if(isContradicting)
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
    public List<ChoiceNode> adapt(List<ChoiceNode> valueAssignment)
    {
        List<Integer> assumptions = assumptionsFromValues(valueAssignment);
            try {

                IProblem problem = fSolver;
                boolean b = problem.isSatisfiable(new VecInt(assumptionsFromValues(valueAssignment).stream().mapToInt(Integer::intValue).toArray())); //necessary to make a call so solver can prepare a model
                if (!b) {
                    ExceptionHelper.reportRuntimeException("Cannot adapt, it's unsatisfiable!");
                    return null;
                }
                Set<Integer> vars = new HashSet<>(Ints.asList(problem.model()));
                for (Pair<Integer, ExpectedValueStatement> p : expValConstraints) {
                    if (vars.contains(p.getFirst())) {
                        p.getSecond().adapt(valueAssignment);
                    }
                }
            } catch (TimeoutException e) {}
        return valueAssignment;
    }

}
