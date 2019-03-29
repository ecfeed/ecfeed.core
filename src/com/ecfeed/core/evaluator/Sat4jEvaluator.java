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

import java.util.*;

public class Sat4jEvaluator implements IConstraintEvaluator<ChoiceNode> {

    private List<ArrayList<Integer>> clauses;
    private int firstUnusedID = 1;
    private HashMap<AbstractStatement, Integer> statementToID;



    public Sat4jEvaluator(Collection<Constraint> initConstraints) {
        statementToID = new HashMap<>();
        if(initConstraints != null) {
            for (Constraint constraint : initConstraints) {
                ParseConstraint(constraint);
            }
        }

    }

    private int newID()
    {
        int ret = firstUnusedID;
        firstUnusedID++;
        return ret;
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
            return 0; //TODO
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
            return 0; //TODO
        }
        @Override
        public Object visit(ChoiceCondition statement)
        {
            return 0; //TODO
        }
        @Override
        public Object visit(ParameterCondition statement)
        {
            return 0; //TODO
        }
        @Override
        public Object visit(ValueCondition statement)
        {
            return 0; //TODO
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
