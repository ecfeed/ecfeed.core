package com.ecfeed.core.evaluator;

import com.ecfeed.core.utils.ExceptionHelper;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class SatSolver {

    private ISolver fSolver;
    private Boolean fIsContradicting;
    private Boolean fHasConstraints;

    public SatSolver() {
        fSolver = SolverFactory.newDefault();
        fIsContradicting = false;
        fHasConstraints = false;
    }

    public void initialize(
            final int maxVar,
            final int countOfClauses,
            Sat4Clauses fSat4Clauses) {

        fSolver = SolverFactory.newDefault();

        try {
            fSolver.newVar(maxVar);
            fSolver.setExpectedNumberOfClauses(countOfClauses);

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

        try {
            fSolver.addClause(clause);
        } catch (ContradictionException e) {
            fIsContradicting = true;
        }
    }

    public void newVar(int var) {
        fSolver.newVar(var);
    }

    public ISolver getSolver() { // TODO - REMOVE
        return fSolver;
    }

    public boolean isProblemSatisfiable(final VecInt assumps) {

        IProblem problem = fSolver;
        try {
            return problem.isSatisfiable(assumps);
        }
        catch (TimeoutException e) {
            ExceptionHelper.reportRuntimeException("Timeout occured. Can not check if problem is satisfiable.");
            return false;
        }
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
