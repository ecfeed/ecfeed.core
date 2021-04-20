package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.ecfeed.core.evaluator.SatSolver_ConstraintEvaluator;
import com.ecfeed.core.generators.api.GeneratorException;

public class ScoreBasedAlgorithmTest {
	
	@Test
	public void testproduceTestSuite() throws GeneratorException {
		
		List<List<String>> input = Arrays.asList(Arrays.asList("a1", "a2"), Arrays.asList("b1", "b2"),
				Arrays.asList("c1", "c2"), Arrays.asList("d1", "d2"));
		SatSolver_ConstraintEvaluator constraintEvaluator = new SatSolver_ConstraintEvaluator(input);
		NwiseScoreEvaluatorMock<String> fScores = new NwiseScoreEvaluatorMock<>(input, constraintEvaluator, 3);
		// check all the 3-tuples
		assert (15 == fScores.getNTuples(3).size());
		
		ScoreBasedAlgorithmMock fTestSuite = new ScoreBasedAlgorithmMock(input, 3, constraintEvaluator);
		
		// Generate a test suite which can cover all the fArgCount-tuples
		List<List<?>> testSuite = new ArrayList<>();
	      while (fScores.existNDimensions(3)){
	            List<?> test = fTestSuite.nextTest();
	            if (test!= null){
	            	testSuite.add(test);
	            }
	        }
	      Set<List<?>> set = new HashSet<List<?>>(testSuite);
	      testSuite = new ArrayList<List<?>>(set);
		
		// print the generated test suite
		for (int i = 0; i < testSuite.size(); i++) {
			System.out.println (testSuite.get(i));
		}
		// check if all the 3-tuples have been covered and removed
		assert (0 == fScores.getNTuples(3).size());
		
	}
}
