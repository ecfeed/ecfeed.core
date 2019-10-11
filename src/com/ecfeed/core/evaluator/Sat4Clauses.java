package com.ecfeed.core.evaluator;

import org.sat4j.core.VecInt;

import java.util.ArrayList;
import java.util.List;

public class Sat4Clauses {

    private List<VecInt> fClauses;

    public Sat4Clauses() {

        fClauses = new ArrayList<>();
    }

    public void add(VecInt clause) {

        fClauses.add(clause);
    }

    public int getSize() {

        return fClauses.size();
    }

    public VecInt getClause(int index) {

        return fClauses.get(index);
    }

    List<VecInt> getInternalList() {
        return fClauses;
    }
}
