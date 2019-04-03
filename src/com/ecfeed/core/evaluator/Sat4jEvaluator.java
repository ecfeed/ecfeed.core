package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.EvaluationResult;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.lang.reflect.Array;
import java.util.*;

public class Sat4jEvaluator implements IConstraintEvaluator<ChoiceNode> {

    private ArrayList<ArrayList<Integer>> clauses;
    private int lastUsedID = 1;
    private HashMap<MethodParameterNode, HashMap<ChoiceNode, Integer>> argChoiceToID;



    public Sat4jEvaluator(Collection<Constraint> initConstraints) {
        argChoiceToID = new HashMap<>();
        clauses = new ArrayList<>();
        if(initConstraints != null) {
            for (Constraint constraint : initConstraints) {
                ParseConstraint(constraint);
            }
        }
    }

    private int newID()
    {
        lastUsedID++;
        return lastUsedID;
    }


    private void ParseConstraint(Constraint constraint) {
        if(constraint != null) {
            AbstractStatement premise = constraint.getPremise(), consequence = constraint.getConsequence();
            Integer premiseID=null, consequenceID=null;
            try {
                premiseID = (Integer)premise.accept(new ParseStatementVisitor());
                consequenceID = (Integer)consequence.accept(new ParseStatementVisitor());
            } catch (Exception e) {}
            clauses.add(new ArrayList<>(Arrays.asList(-premiseID,consequenceID)));
        }
    }

    class ParseStatementVisitor implements IStatementVisitor
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
                            childID = (Integer) child.accept(new ParseStatementVisitor());
                        } catch (Exception e) {
                        }
                        bigClause.add(childID);
                        clauses.add(new ArrayList<>(Arrays.asList(-childID, myID))); //small clauses
                    }
                    bigClause.add(-myID);
                    clauses.add(bigClause);
                    break;
                }
                case AND: // y = (x1 AND x2 AND .. AND xn) compiles to: (x1 OR NOT y) AND ... AND (xn OR NOT y) AND (NOT x1 OR ... OR NOT xn OR y)
                {
                    ArrayList<Integer> bigClause = new ArrayList<>();
                    for (AbstractStatement child : statement.getChildren()) {
                        Integer childID = null;
                        try {
                            childID = (Integer) child.accept(new ParseStatementVisitor());
                        } catch (Exception e) {
                        }
                        bigClause.add(-childID);
                        clauses.add(new ArrayList<>(Arrays.asList(childID, -myID))); //small clauses
                    }
                    bigClause.add(myID);
                    clauses.add(bigClause);
                    break;
                }
            }
//            statementToID.put(statement,myID); not really necessary, as we are never reusing the same statements
            return myID;
        }
        @Override
        public Object visit(RelationStatement statement)
        {
            MethodParameterNode arg = statement.getLeftParameter();
            if(!argChoiceToID.containsKey(arg)) { //we need to create new set of variables, as we are seeing this parameter for the first time
                ArrayList<Integer> xList = new ArrayList<>(); //xList[i] ==  (this parameter takes value i)
                ArrayList<Integer> yList = new ArrayList<>(); //yList[i] == (this parameter takes one of values 0,...,i
                HashMap<ChoiceNode, Integer> choiceIDs = new HashMap<>();
                for (ChoiceNode choice : arg.getLeafChoices()) {
                    Integer xID = newID();
                    Integer yID = newID();
                    xList.add(xID);
                    yList.add(yID);
                    choiceIDs.put(choice, xID);
                }
                int n = xList.size();

                clauses.add(xList); //at least one value should be taken
                for(int i=0;i<n;i++)
                    clauses.add(new ArrayList<>(Arrays.asList(-xList.get(i),yList.get(i)))); // xList[i] => yList[i];
                for(int i=0;i<n-1;i++)
                    clauses.add(new ArrayList<>(Arrays.asList(-yList.get(i),yList.get(i+1)))); // yList[i] => yList[i+1];
                for(int i=0;i<n-1;i++)
                    clauses.add(new ArrayList<>(Arrays.asList(-xList.get(i+1),-yList.get(i)))); // NOT( xList[i+1] AND yList[i] ), to guarantee uniqueness

                argChoiceToID.put(arg, choiceIDs);
            }


            Integer myID = newID();

            MethodNode methodNode = arg.getMethod();
            if (methodNode == null) {
                return null;
            }

            int index = methodNode.getParameters().indexOf(arg);
            if (index == -1) {
                return null;
            }

            int size = methodNode.getParametersCount();

            ArrayList<ChoiceNode> dummyValues = new ArrayList<>(size);

            for(ChoiceNode choice : arg.getLeafChoices())
            {
                dummyValues.set(index, choice);
                EvaluationResult result = statement.evaluate(dummyValues);
                Integer idOfArgChoice = argChoiceToID.get(arg).get(choice);
                if(result == EvaluationResult.TRUE) {
                    clauses.add(new ArrayList<>(Arrays.asList(-idOfArgChoice, myID))); // thisChoice => me
                }
                else if(result == EvaluationResult.FALSE)
                {
                    clauses.add(new ArrayList<>(Arrays.asList(-idOfArgChoice, -myID))); // thisChoice => NOT me
                }
                else //INSUFFICIENT.DATA
                {
                    System.out.println("dupadupadupa, we have no idea on how to handle this case");
                    return null; //FIXME

                }
            }

            return myID;
        }

        @Override
        public Object visit(ExpectedValueStatement statement)
        {
            return 0; //TODO
        }
        @Override
        public Object visit(StaticStatement statement)
        {
            Integer myID = newID();
            if(statement.getValue() == EvaluationResult.TRUE)
                clauses.add(new ArrayList<>(Collections.singletonList(myID)));
            else
                clauses.add(new ArrayList<>(Collections.singletonList(-myID)));
            return myID;
        }
        @Override
        public Object visit(LabelCondition statement)
        {
            return null; //this will never happen
        }
        @Override
        public Object visit(ChoiceCondition statement)
        {
            return null; //this will never happen
        }
        @Override
        public Object visit(ParameterCondition statement)
        {
            return null; //this will never happen
        }
        @Override
        public Object visit(ValueCondition statement)
        {
            return null; //this will never happen
        }
    }

    @Override
    public EvaluationResult evaluate(List<ChoiceNode> valueAssignment)
    {
        return EvaluationResult.INSUFFICIENT_DATA; //TODO
    }


    @Override
    public List<ChoiceNode> adapt(List<ChoiceNode> valueAssignment)
    {
        return new ArrayList<>(); //TODO
    }

//    public static void main(String[] args) {
//        final int MAXVAR = 10000;
//        final int NBCLAUSES = 5000;
//
//        ISolver solver = SolverFactory.newDefault();
//
//
//        try {
//            //prepare the solver to accept MAXVAR variables. MANDATORY for MAXSAT solving
//            solver.newVar(MAXVAR);
//            solver.setExpectedNumberOfClauses(NBCLAUSES);
//            solver.addClause(new VecInt(new int[]{1, 3, 7}));
//            solver.addClause(new VecInt(new int[]{1, 3, -7}));
//            solver.addClause(new VecInt(new int[]{1, -3, 7}));
//            solver.addClause(new VecInt(new int[]{1, -3, -7}));
//            solver.addClause(new VecInt(new int[]{-1, 3, 7}));
//            solver.addClause(new VecInt(new int[]{-1, 3, -7}));
//            solver.addClause(new VecInt(new int[]{-1, -3, 7}));
//            solver.addClause(new VecInt(new int[]{-1, -3, -7}));
//        } catch (ContradictionException e) {
//            System.out.println("Unsatisfiable (trivial)!");
//        }
//
//
//        try {
//// we are done. Working now on the IProblem interface
//        IProblem problem = solver;
//
//            if (problem.isSatisfiable()) {
//                System.out.println("satisfiable");
//            } else {
//                System.out.println("not");
//            }
//        } catch (TimeoutException e) {
//            System.out.println("Timeout, sorry!");
//        }
//    }
}
