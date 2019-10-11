package com.ecfeed.core.evaluator;

import com.ecfeed.core.utils.ExceptionHelper;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.List;

public class Sat4Solver {

    // TODO - what else could be included in this class
    private ISolver fSolver;
    private Boolean fIsContradicting;
    private Boolean fHasConstraints;

    public Sat4Solver() {
        fSolver = SolverFactory.newDefault();
        fIsContradicting = false;
        fHasConstraints = false;
    }

    public void initialize(
            final int maxVar,
            Sat4Clauses fSat4Clauses) {

        fSolver = SolverFactory.newDefault();

        try {
            fSolver.newVar(maxVar);
            fSolver.setExpectedNumberOfClauses(fSat4Clauses.getSize());

            for (int index = 0; index < fSat4Clauses.getSize(); index++) {
                VecInt clause = fSat4Clauses.getClause(index);
                fSolver.addClause(clause);
            }

            //System.out.println("variables: " + maxVar + " clauses: " + countOfClauses);
        } catch (ContradictionException e) {
            fIsContradicting = true;
        }
    }

    public void addClause(VecInt clause) {

        // clause consists of positive or negative ints
        // if int is negative then it translates to logical negation
        // absolute value of int translates to index of variable in some user tab
        // e.g. ~A1 v A2 v ~A3 translates to -1, 2, -3

        try {
            fSolver.addClause(clause);
        } catch (ContradictionException e) {
            fIsContradicting = true;
        }
    }

    public void newVar(int var) {
        fSolver.newVar(var);
    }

    public boolean isProblemSatisfiable(final List<Integer> assumptionsList) {

        // list of assumptions consists of positive of negative integers
        // it sets values (true or false) to variables
        // e.g. [ 1, -3 ] means set A1 to true to and set A3 to false

        final int[] assumps =
                assumptionsList
                        .stream()
                        .mapToInt(Integer::intValue)
                        .toArray();

        IProblem problem = fSolver;
        VecInt vecInt = new VecInt(assumps);

        try {
            return problem.isSatisfiable(vecInt);
        } catch (TimeoutException e) {
            ExceptionHelper.reportRuntimeException("Timeout occured. Can not check if problem is satisfiable.");
            return false;
        }
    }

    public int[] getModel() {

        IProblem problem = fSolver;

        return problem.model();
    }

    public Boolean isContradicting() {
        return fIsContradicting;
    }

    public void setHasConstraints() {
        fHasConstraints = true;
    }

    public Boolean hasConstraints() {
        return fHasConstraints;
    }

}
