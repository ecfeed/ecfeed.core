package com.ecfeed.core.generators.algorithms;

import java.util.List;

public interface IScoreEvaluator<E> {
	
	 public int getScore(List<E> tuple);
	 public void update(List<E> test); 

}
