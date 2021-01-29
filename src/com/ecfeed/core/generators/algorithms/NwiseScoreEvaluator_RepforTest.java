package com.ecfeed.core.generators.algorithms;

import java.util.List;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.EvaluationResult;

public class NwiseScoreEvaluator_RepforTest<E> extends NwiseScoreEvaluator_Rep<E> {

	public NwiseScoreEvaluator_RepforTest(List<List<E>> input, IConstraintEvaluator<E> constraintEvaluator, int argN) throws GeneratorException {
		super(input, constraintEvaluator, argN);
	}
	
	 @Override
	public EvaluationResult constraintCheck(List<E> tuple) {
			if (tuple.size() != 3 || (!tuple.contains("c1") && (!tuple.contains("a1") || tuple.contains("b1"))))
				return EvaluationResult.TRUE;
			else
				return EvaluationResult.FALSE;
		}

}
