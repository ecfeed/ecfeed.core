package com.ecfeed.core.generators.algorithms;

import java.util.List;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;

public interface IScoreEvaluator<E> {

	public int getScore(List<E> tuple);
	public void update(List<E> test);
	public void initialize(List<List<E>> input, IConstraintEvaluator<E> constraintEvaluator)	throws GeneratorException;
}
