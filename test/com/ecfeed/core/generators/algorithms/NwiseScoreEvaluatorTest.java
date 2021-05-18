package com.ecfeed.core.generators.algorithms;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.utils.ExceptionHelper;

public class NwiseScoreEvaluatorTest {


	@Test
	public void basicTest() {

		try {
			decreaseScoreTest();
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
	
	@Test
	public void findBestTupleTest() {
		
//		try {
////			runBestTupleTest();
//		} catch (GeneratorException e) {
//			fail();
//		}		
	}

//	private void runBestTupleTest() throws GeneratorException {
//		
//		NwiseScoreEvaluator<String> evaluator = new NwiseScoreEvaluator<>(3, 100);
//		
//		List<List<String>> input = prepareTestInput(5);
//		
//		evaluator.initialize(input, null);
//		
//		List<String> bestTuple = null;
//		
//		for (int counter = 0; counter < 20; counter++) {
//			
//			int nTuplesBeforeUpdate = evaluator.countNTuples();
//			
//			if (nTuplesBeforeUpdate == 0) {
//				return;
//			}
//	
//			bestTuple = evaluator.findFullTupleWithGoodScore();
//			
//			evaluator.updateScores(bestTuple);
//			
//			int nTuplesAfterUpdate = evaluator.countNTuples();
//			
//			assertTrue(nTuplesAfterUpdate < nTuplesBeforeUpdate);
//		}
//		
//		fail("Some N-tuples remain in table.");
//		
//	}

//	private static List<List<String>> prepareTestInput(int inputSize) {
//		
//		List<List<String>> input = new ArrayList<>();
//		
//		for (int firstDimension = 1; firstDimension <= inputSize; firstDimension++) {
//			
//			List<String> choices = prepareChoices(firstDimension);
//			
//			input.add(choices);
//		}
//		
//		return input;
//	}
	
//	private static List<String> prepareChoices(int firstDimension) {
//		
//		List<String> choices = new ArrayList<>();
//		
//		choices.add("choice" + firstDimension + 1 );
//		choices.add("choice" + firstDimension + 2 );
//		
//		return choices;
//	}
	
	private static void decreaseScoreTest() throws GeneratorException {

		List<String> dim1 = new ArrayList<String>();
		dim1.add("V11");
		dim1.add("V12");
		dim1.add("V13");

		List<String> dim2 = new ArrayList<String>();
		dim2.add("V21");
		dim2.add("V22");
		dim2.add("V23");

		List<String> dim3 = new ArrayList<String>();
		dim3.add("V31");
		dim3.add("V32");
		dim3.add("V33");

		List<List<String>> testInput = new ArrayList<>();

		testInput.add(dim1);
		testInput.add(dim2);
		testInput.add(dim3);

		NwiseScoreEvaluator<String> evaluator = new NwiseScoreEvaluator<String>(2);
		evaluator.initialize(testInput, null);

		List<String> tuple1 = createTuple("V11", "V21", "V31");
		List<String> tuple2 = createTuple("V11", "V22", "V32"); // one choice from tuple1 used
		List<String> tuple3 = createTuple("V11", "V21", "V32"); // two choices from tuple1 used
		List<String> tuple4 = createTuple("V13", "V23", "V33"); // choices not used before

		int score1 = evaluator.getScore(tuple1);

		int score2BeforeUpdate = evaluator.getScore(tuple2);	
		checkScoreAfterUpdate(evaluator, tuple1);
		int score2AfterUpdate = evaluator.getScore(tuple2);

		checkScoreDecrease(score2BeforeUpdate, score2AfterUpdate);

		int score3BeforeUpdate = evaluator.getScore(tuple3);
		checkScoreAfterUpdate(evaluator, tuple2);
		int score3AfterUpdate = evaluator.getScore(tuple3);

		checkScoreDecrease(score3BeforeUpdate, score3AfterUpdate);

		checkScoreAfterUpdate(evaluator, tuple3);

		int score4 = evaluator.getScore(tuple4);

		if (score1 != score4) {
			ExceptionHelper.reportRuntimeException("Scores 1 and 4 should be equal.");
		}
	}

	private static void checkScoreDecrease(int scoreBeforeUpdate, int scoreAfterUpdate) {

		if (scoreBeforeUpdate > scoreAfterUpdate) {
			return;
		}

		fail("Score before should be greater than score after.");

	}

	private static void checkScoreAfterUpdate(NwiseScoreEvaluator<String> evaluator, List<String> tuple) {

		int scoreBefore = evaluator.getScore(tuple);

		if (scoreBefore == 0) {
			fail("Score must not be 0.");
		}

		evaluator.updateScores(tuple);

		int scoreAfter = evaluator.getScore(tuple);

		if (scoreAfter != 0) {
			fail("Score must be 0 after update.");
		}
	}

	private static List<String> createTuple(String value1, String value2, String value3) {

		List<String> tuple = new ArrayList<>();

		tuple.add(value1);

		if (value2 != null) {
			tuple.add(value2);
		}

		if (value3 != null) {
			tuple.add(value3);
		}

		return tuple;
	}

}
