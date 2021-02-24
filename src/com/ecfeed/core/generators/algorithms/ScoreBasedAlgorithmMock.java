package com.ecfeed.core.generators.algorithms;

import java.util.List;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.EvaluationResult;

public class ScoreBasedAlgorithmMock<E> extends AbstractScoreBasedAlgorithm<E> {
	
	public ScoreBasedAlgorithmMock(List<List<E>> input, int argCount, IConstraintEvaluator<E> constraintEvaluator) throws GeneratorException {
		super(input, new NwiseScoreEvaluatorMock<E>(input, constraintEvaluator, argCount), argCount, constraintEvaluator);
		}

	@Override
	public EvaluationResult constraintCheck(List<E> tuple) {
		if (!tuple.contains("c1") && (!tuple.contains("a1") || tuple.contains("b1")))
			return EvaluationResult.TRUE;
		else
			return EvaluationResult.FALSE;
	}
}
