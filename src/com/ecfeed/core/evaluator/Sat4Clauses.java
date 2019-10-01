package com.ecfeed.core.evaluator;

import org.sat4j.core.VecInt;

import java.util.ArrayList;
import java.util.List;

public class Sat4Clauses {

    private List<VecInt> fClausesVecInt;

    public Sat4Clauses() {

        fClausesVecInt = new ArrayList<>();
    }

    public void add(VecInt clause) {

        fClausesVecInt.add(clause);
    }

    public int getSize() {

        return fClausesVecInt.size();
    }

    public VecInt getClause(int index) {

        return fClausesVecInt.get(index);
    }
}
