package com.ecfeed.core.generators.algorithms;

import java.util.List;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;

public class ScoreBasedNwiseAlgorithm<E> extends AbstractScoreBasedAlgorithm<E> {
	
	public ScoreBasedNwiseAlgorithm(List<List<E>> input, int argCount, IConstraintEvaluator<E> constraintEvaluator) throws GeneratorException {
		super(input, new NwiseScoreEvaluator<E>(input, constraintEvaluator, argCount), argCount, constraintEvaluator);
	}
	
}
