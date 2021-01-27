package com.ecfeed.core.generators.api;

import java.util.List;
import com.ecfeed.core.utils.EvaluationResult;

public interface IConstraintEvaluator<E> {
    public EvaluationResult evaluate(List<E> testCaseValues);
    public List<E> setExpectedValues(List<E> testCaseValues);
    public void excludeAssignment(List<E> valueAssignment);
    public void initialize(List<List<E>> input);
}
