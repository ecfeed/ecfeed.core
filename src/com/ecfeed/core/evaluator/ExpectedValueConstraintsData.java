package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class ExpectedValueConstraintsData {

    //Integer is the variable of pre-condition enforcing postcondition ExpectedValueStatement
    private List<Pair<Integer, ExpectedValueStatement>> fExpectedValConstraints;

    public ExpectedValueConstraintsData() {
        fExpectedValConstraints = new ArrayList<>();
    }

    public List<Pair<Integer, ExpectedValueStatement>> getList() {
        return fExpectedValConstraints;
    }

    public void add(Pair<Integer, ExpectedValueStatement> pair) {
        fExpectedValConstraints.add(pair);
    }

}
