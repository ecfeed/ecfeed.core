package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.utils.EvaluationResult;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

//public class Sat4jEvaluator<E> implements IConstraintEvaluator<E> {
public class Sat4jEvaluator{

    public static void main(String[] args) {
        final int MAXVAR = 10000;
        final int NBCLAUSES = 5000;

        ISolver solver = SolverFactory.newDefault();


        try {
            //prepare the solver to accept MAXVAR variables. MANDATORY for MAXSAT solving
            solver.newVar(MAXVAR);
            solver.setExpectedNumberOfClauses(NBCLAUSES);
            solver.addClause(new VecInt(new int[]{1, 3, 7}));
            solver.addClause(new VecInt(new int[]{1, 3, -7}));
            solver.addClause(new VecInt(new int[]{1, -3, 7}));
            solver.addClause(new VecInt(new int[]{1, -3, -7}));
            solver.addClause(new VecInt(new int[]{-1, 3, 7}));
            solver.addClause(new VecInt(new int[]{-1, 3, -7}));
            solver.addClause(new VecInt(new int[]{-1, -3, 7}));
            solver.addClause(new VecInt(new int[]{-1, -3, -7}));
        } catch (ContradictionException e) {
            System.out.println("Unsatisfiable (trivial)!");
        }


        try {
// we are done. Working now on the IProblem interface
        IProblem problem = solver;

            if (problem.isSatisfiable()) {
                System.out.println("satisfiable");
            } else {
                System.out.println("not");
            }
        } catch (TimeoutException e) {
            System.out.println("Timeout, sorry!");
        }
    }
}
