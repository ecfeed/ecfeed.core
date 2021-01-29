package com.ecfeed.core.generators.algorithms;

import org.junit.Test;

import com.ecfeed.core.evaluator.HomebrewConstraintEvaluator;
import com.ecfeed.core.generators.api.GeneratorException;

import java.util.Arrays;
import java.util.List;

public class NwiseScoreEvaluator_RepTest {

	/*
	 * input = [a1, a2], [b1, b2], [c1, c2], [d1, d2], a1=>b1 AND !c1
	 * 
	 * Note: the example from the space page has some mistakes: ([a1, c2, d1], 1),
	 * ([a1, c2, d2], 1) should be removed due to the constraint a1=>b1. Also,
	 * [b2,c2] should be 3 in the constructed table S2. Therefore, all the
	 * constructed tables S1, S2, S3 are updated as below. Space page link:
	 * https://testifyas.atlassian.net/wiki/spaces/ECFEED/pages/777945091/Score+
	 * based+NWise+generator
	 * 
	 * S3 = ([a1, b1, c2], 1), ([a2, b1, c2], 1), ([a2, b2, c2], 1), ([a1, b1, d1],
	 * 1), ([a1, b1, d2], 1), ([a2, b1, d1], 1), ([a2, b1, d2], 1), ([a2, b2, d1],
	 * 1), ([a2, b2, d2], 1), ([a2, c2, d1], 1), ([a2, c2, d2], 1), ([b1, c2, d1],
	 * 1), ([b1, c2, d2], 1), ([b2, c2, d1], 1), ([b2, c2, d2], 1)
	 * 
	 * S2 = ([a1, b1], 3), ([a2, b1], 3), ([a2, b2], 3), ([a1, c2], 1), ([a2, c2],
	 * 3), ([a1, d1], 1), ([a1, d2], 1), ([a2, d1], 3), ([a2, d2], 3), ([b1, c2],
	 * 4), ([b1, d1], 3), ([b1, d2], 3), ([b2, c2], 3), ([b2, d1], 2), ([b2, d2],
	 * 2), ([c2, d1], 3), ([c2, d2], 3)
	 * 
	 * S1 = ([a1], 3), ([a2], 8), ([b1], 8), ([b2], 5), ([c2], 9), ([d1], 6), ([d2],
	 * 6)
	 * 
	 */

	@Test
	public void testgetScore() throws GeneratorException {

		/*
		 * Score[b2, c2, d1] = 3*(5+9) + 2*(5+6) + 3*(9+6) = 109
		 * 
		 * Score[a1, b1, c2] = 3*(3+8) + 1*(3+9) + 4*(8+9) = 113
		 * 
		 * also test if the score returned -1 when the input tuple is invalid
		 */

		List<List<String>> input = Arrays.asList(Arrays.asList("a1", "a2"), Arrays.asList("b1", "b2"),
				Arrays.asList("c1", "c2"), Arrays.asList("d1", "d2"));

		NwiseScoreEvaluator_RepforTest<String> fScores = new NwiseScoreEvaluator_RepforTest<>(input, new HomebrewConstraintEvaluator(input), 3);

		assert (109 == fScores.getScore(Arrays.asList("b2", "c2", "d1")));
		assert (113 == fScores.getScore(Arrays.asList("a1", "b1", "c2")));
		assert (-1 == fScores.getScore(Arrays.asList("e1")));
	}

	@Test
	public void testUpdate() throws GeneratorException {

		List<List<String>> input = Arrays.asList(Arrays.asList("a1", "a2"), Arrays.asList("b1", "b2"),
				Arrays.asList("c1", "c2"), Arrays.asList("d1", "d2"));

		NwiseScoreEvaluator_RepforTest<String> fScores = new NwiseScoreEvaluator_RepforTest<>(input, new HomebrewConstraintEvaluator(input), 3);

		/*
		 * Update the scores when the test [a1,b1,c2] (three dimensions) is generated, Score[a1,b1,c2] = 0 as the tuple [a1, b1, c2] should
		 * be removed;
		 */

		// before updating: Score[b2,c2,d1] = 109 
		assert (109 == fScores.getScore(Arrays.asList("b2", "c2", "d1")));
		
		fScores.update((Arrays.asList("a1", "b1", "c2")));

		// after updating: Score[b2,c2,d1] = 103
		assert (103 == fScores.getScore(Arrays.asList("b2", "c2", "d1")));
		assert (-1 == fScores.getScore(Arrays.asList("a1", "b1", "c2"))); // [a1,b1,c2] has been removed so the score is -1
		// check the score for another tuple after updating
		assert (65 == fScores.getScore(Arrays.asList("a1", "b1", "d1")));

		// update after generating a test case [b1,d1] with only two dimensions. The scores should remain the same 
		fScores.update((Arrays.asList("b1", "d1")));
		assert (103 == fScores.getScore(Arrays.asList("b2", "c2", "d1")));
		assert (65 == fScores.getScore(Arrays.asList("a1", "b1", "d1")));

		/*
		 * update with a test case with four dimensions: [a2,b2,c2,d1] 
		 * four 3-tuples should be removed from S3, i.e., [a2,b2,c2],[a2,b2,d1],[a2,c2,d1],[b2,c2,d1]
		 * the score for [a1,b1,d1] is updated as 53 (before the score is 65)
		 */

		fScores.update((Arrays.asList("a2", "b2", "c2", "d1")));
		assert (53 == fScores.getScore(Arrays.asList("a1", "b1", "d1")));
		assert (-1 == fScores.getScore(Arrays.asList("a2", "b2", "c2"))); //[a2, b2, c2] has been removed so the score is -1
	}
}
