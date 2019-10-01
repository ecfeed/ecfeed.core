package com.ecfeed.core.evaluator;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;

public class SatSolver {

    private ISolver fSolver;
    public Boolean fIsContradicting;
    public Boolean fNoConstraints;

    public SatSolver() {
        fSolver = SolverFactory.newDefault();
        fIsContradicting = false;
        fNoConstraints = true;
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
}
