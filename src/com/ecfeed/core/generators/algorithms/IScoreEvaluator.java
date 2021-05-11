package com.ecfeed.core.generators.algorithms;

import java.util.List;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;

public interface IScoreEvaluator<E> {

	public void initialize(List<List<E>> input, IConstraintEvaluator<E> constraintEvaluator)	throws GeneratorException;
	public int getScore(List<E> tuple);
	public void updateScores(List<E> test);
	public List<E> findFullTupleWithGoodScore(); // not used so far but probably may by usable in some cases
	public boolean allNTuplesCovered();
	public int getCountOfNTuples();
	public E getChoiceFromInputDomain(List<E> resultTuple, int dimension);
}
