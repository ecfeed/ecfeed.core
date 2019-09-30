package com.ecfeed.core.evaluator;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.utils.EvaluationResult;

import java.util.Collection;
import java.util.List;

public class DummyEvaluator<E> implements IConstraintEvaluator<E> {

    //    public DummyEvaluator(Collection<IConstraint<E>> constraints) {}

    public DummyEvaluator() {
    }

    public EvaluationResult evaluate(List<E> valueAssignment) {
        return EvaluationResult.TRUE;
    }

    public List<E> adapt(List<E> valueAssignment) {
        return valueAssignment;
    }

    public void excludeAssignment(List<E> valueAssignment) {
    }

    @Override
    public void initialize(List<List<E>> input) {
    }
}
