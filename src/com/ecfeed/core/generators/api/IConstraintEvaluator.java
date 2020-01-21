package com.ecfeed.core.generators.api;

import java.util.List;
import com.ecfeed.core.utils.EvaluationResult;

public interface IConstraintEvaluator<E> {
    public EvaluationResult evaluate(List<E> valueAssignment);
    public List<E> adapt(List<E> valueAssignment);
    public void excludeAssignment(List<E> valueAssignment);
    public void initialize(List<List<E>> input);
}
