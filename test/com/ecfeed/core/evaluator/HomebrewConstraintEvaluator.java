package com.ecfeed.core.evaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;

public class HomebrewConstraintEvaluator<E> implements IConstraintEvaluator<E> {

    private List<IConstraint<E>> fConstraints = null;


    public HomebrewConstraintEvaluator(Collection<IConstraint<E>> constraints) {
        fConstraints = new ArrayList<IConstraint<E>>(constraints);
    }

    @Override
    public void excludeAssignment(List<E> toExclude) {
        ExceptionHelper.reportRuntimeException("this evaluator does not handle excludeAssignment()");
    }

    @Override
    public void initialize(List<List<E>> input) {
    }

    @Override
    public EvaluationResult evaluate(List<E> valueAssignment) {
        if (valueAssignment == null) {
            return EvaluationResult.TRUE;
        }

        boolean insufficientData = false;

        for (IConstraint<E> constraint : fConstraints) {

            EvaluationResult value = constraint.evaluate(valueAssignment);
            if (value == EvaluationResult.FALSE) {
                return EvaluationResult.FALSE;
            }

            if (value == EvaluationResult.INSUFFICIENT_DATA) {
                insufficientData = true;
            }
        }

        if (insufficientData)
            return EvaluationResult.INSUFFICIENT_DATA;

        return EvaluationResult.TRUE;
    }

    @Override
    public List<E> setExpectedValues(List<E> valueAssignment) {
        if (valueAssignment != null) {
            for (IConstraint<E> constraint : fConstraints) {
                constraint.setExpectedValues(valueAssignment);
            }
        }
        return valueAssignment;
    }
}
