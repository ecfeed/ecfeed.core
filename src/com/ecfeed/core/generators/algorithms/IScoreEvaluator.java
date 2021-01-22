package com.ecfeed.core.generators.algorithms;

import java.util.List;
import java.util.SortedMap;

import com.ecfeed.core.generators.api.GeneratorException;

public interface IScoreEvaluator<E> {
	
	 public int getScore(List<E> tuple);
	 public void update(List<E> test); 
	 
}
