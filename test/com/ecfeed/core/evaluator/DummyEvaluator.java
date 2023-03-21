package com.ecfeed.core.evaluator;

import java.util.List;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.EvaluationResult;

public class DummyEvaluator<E> implements IConstraintEvaluator<E> {

    public DummyEvaluator() {
    }

    public EvaluationResult evaluate(List<E> valueAssignment) {
        return EvaluationResult.TRUE;
    }

    public List<E> setExpectedValues(List<E> valueAssignment) {
        return valueAssignment;
    }

    public void excludeAssignment(List<E> valueAssignment) {
    }

    @Override
    public void initialize(List<List<E>> input) {
    }
}
