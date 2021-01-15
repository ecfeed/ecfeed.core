package com.ecfeed.core.generators.algorithms;

import java.util.List;
import java.util.SortedMap;

public interface IScoreEvaluator<E> {
	
	 public int getScore(List<E> tuple); // calculate and measure score values for a given tuple

	 public void update(List<E> tuple); // update the score calculation basing on the test that was just generated.

}
