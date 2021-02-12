package com.ecfeed.core.generators.algorithms;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.evaluator.HomebrewConstraintEvaluator;
import com.ecfeed.core.generators.api.GeneratorException;

public class ScoreBasedAlgorithmTest {
	
	@Test
	public void testproduceTestSuite() throws GeneratorException {
		
		List<List<String>> input = Arrays.asList(Arrays.asList("a1", "a2"), Arrays.asList("b1", "b2"),
				Arrays.asList("c1", "c2"), Arrays.asList("d1", "d2"));
		NwiseScoreEvaluatorMock<String> fScores = new NwiseScoreEvaluatorMock<>(input, new HomebrewConstraintEvaluator(input), 3);
		// check all the 3-tuples
		assert (15 == fScores.getNTuples(3).size());
		
		ScoreBasedAlgorithmMock fTestSuite = new ScoreBasedAlgorithmMock(input, fScores, 3, new HomebrewConstraintEvaluator(input));
		List<List<String>> testSuite = fTestSuite.generateTestSuite();
		// print the generated test suite
		for (int i = 0; i < testSuite.size(); i++) {
			System.out.println (testSuite.get(i));
		}
		// check if all the 3-tuples have been covered and removed
		assert (0 == fScores.getNTuples(3).size());
		
	}
}
