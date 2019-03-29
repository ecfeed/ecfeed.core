package com.ecfeed.core.generators.api;

import java.util.List;
import com.ecfeed.core.utils.EvaluationResult;

public interface IConstraintEvaluator<E> {
    public EvaluationResult evaluate(List<E> valueAssignment);
    public List<E> adapt(List<E> valueAssignment);
}
