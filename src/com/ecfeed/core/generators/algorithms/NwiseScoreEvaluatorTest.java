package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.utils.ExceptionHelper;

public class NwiseScoreEvaluatorTest {
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			runTest();
		} catch (GeneratorException e) {
			ExceptionHelper.reportRuntimeException("Error: " + e.getMessage());
		}
	}

	private static void runTest() throws GeneratorException {
		
		NwiseScoreEvaluator<String> evaluator = new NwiseScoreEvaluator<>(3, 100);
		
		List<List<String>> input = prepareTestInput(5);
		
		evaluator.initialize(input, null);
		
		evaluator.updateScores(createTestCase("choice11", "choice21", "choice31", "choice41", "choice51"));
		evaluator.updateScores(createTestCase("choice12", "choice22", "choice32", "choice42", "choice52"));
		evaluator.updateScores(createTestCase("choice12", "choice21", "choice31", "choice41", "choice52"));
		evaluator.updateScores(createTestCase("choice11", "choice22", "choice32", "choice42", "choice51"));
		evaluator.updateScores(createTestCase("choice11", "choice22", "choice31", "choice41", "choice52"));
		evaluator.updateScores(createTestCase("choice12", "choice21", "choice31", "choice42", "choice51"));
		evaluator.updateScores(createTestCase("choice11", "choice21", "choice32", "choice41", "choice52"));
		evaluator.updateScores(createTestCase("choice12", "choice22", "choice32", "choice41", "choice51"));
		evaluator.updateScores(createTestCase("choice11", "choice21", "choice31", "choice42", "choice52"));
		evaluator.updateScores(createTestCase("choice12", "choice21", "choice32", "choice42", "choice51"));
		
		evaluator.findBestFullTuple();
	}

	private static List<String> createTestCase(String string1, String string2, String string3, String string4, String string5) {
	
		List<String> testCase = new ArrayList<>();
		
		testCase.add(string1);
		testCase.add(string2);
		testCase.add(string3);
		testCase.add(string4);
		testCase.add(string5);
		
		return testCase;
	}

	private static List<List<String>> prepareTestInput(int inputSize) {
		
		List<List<String>> input = new ArrayList<>();
		
		for (int firstDimension = 1; firstDimension <= inputSize; firstDimension++) {
			
			List<String> choices = prepareChoices(firstDimension);
			
			input.add(choices);
		}
		
		return input;
	}

	private static List<String> prepareChoices(int firstDimension) {
		
		List<String> choices = new ArrayList<>();
		
		choices.add("choice" + firstDimension + 1 );
		choices.add("choice" + firstDimension + 2 );
		
		return choices;
	}

	
	
}
