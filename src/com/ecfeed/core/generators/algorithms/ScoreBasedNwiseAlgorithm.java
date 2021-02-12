package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;

public class ScoreBasedNwiseAlgorithm<E> extends AbstractScoreBasedAlgorithm<E> {

	private int fArgCount;
	private NwiseScoreEvaluator<E> fNwiseScoreEvaluator;
	
	public ScoreBasedNwiseAlgorithm(List<List<E>> input, NwiseScoreEvaluator<E> nwiseScoreEvaluator, int argCount, IConstraintEvaluator<E> constraintEvaluator) throws GeneratorException {
		super(input, nwiseScoreEvaluator, argCount, constraintEvaluator);
		this.fArgCount = argCount;
		this.fNwiseScoreEvaluator = nwiseScoreEvaluator;
	}

	// Generate a test suite which can cover all the fArgCount-tuples
	public List<List<E>> generateTestSuite () throws GeneratorException {
		fHistorydimensionOrder.clear();
	      List<List<E>> testSuite = new ArrayList<>();
	      while (fNwiseScoreEvaluator.existNDimensions(fArgCount)){
	            List<E> test = nextTest();
	            if (test!= null){
	            	testSuite.add(test);
	            }
	        }
	      Set<List<E>> set = new HashSet<List<E>>(testSuite);
	      testSuite = new ArrayList<List<E>>(set);
	      
	      return testSuite;
	}
	
}
