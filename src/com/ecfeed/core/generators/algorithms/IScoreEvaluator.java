package com.ecfeed.core.generators.algorithms;

import java.util.List;
import java.util.SortedMap;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;

public interface IScoreEvaluator<E> {

	public void initialize(List<List<E>> input, IConstraintEvaluator<E> constraintEvaluator) throws GeneratorException;

	public int getInitialNTupleCount();
	public int getCurrentNTupleCount();

	public int getScore(SortedMap<Integer, E> tuple);
	public void update(List<E> test);
}
