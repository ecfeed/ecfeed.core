package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class ScoreBasedNwiseAlgorithm<E> extends AbstractAlgorithm<E> {

	// TODO - remove println
	
	private int fDimensionCount; // Total number of dimensions for an input domain
	private List<Integer> fInputIndex; // Store index of parameters in input domain
	private IScoreEvaluator<E> fScoreEvaluator;
	protected List<List<Integer>> fHistoryDimensionOrder = new ArrayList<>(); // Store historical dimension orders (each
																				// randomly generated dimension order
																				// could be different)

	public ScoreBasedNwiseAlgorithm(IScoreEvaluator<E> fScoreEvaluator) throws GeneratorException {

		this.fScoreEvaluator = fScoreEvaluator;
	}
	
	@Override
	public void initialize(
			List<List<E>> input,
			IConstraintEvaluator<E> constraintEvaluator,
			IEcfProgressMonitor generatorProgressMonitor) throws GeneratorException {
		
		this.fScoreEvaluator.initialize(input, constraintEvaluator);
		this.fDimensionCount = input.size();
		this.fInputIndex = IntStream.range(0, input.size()).boxed().collect(Collectors.toList());
		
		super.initialize(input, constraintEvaluator, generatorProgressMonitor);
	}
	
	@Override
	public List<E> getNext() throws GeneratorException {
		return getNextTest();
	}
	
	private List<E> getNextTest() {

		System.out.println("Get next ****************************************************************************************************************");
		
		if (fScoreEvaluator.allNTuplesCovered()) {
			return null;
		}
		
		List<E> resultTuple = initializeTuple(fDimensionCount);

		if (evaluateConstraint(resultTuple) == EvaluationResult.FALSE) { // TODO - move initial constraint checking to initialization
			return null; // conflicting constraints
		}

		List<Integer> shuffledDimensions = createShuffledIndicesToDimensions(); 
		
		System.out.println("Shuffled dimensions: " + shuffledDimensions);
		
		for (int dimension : shuffledDimensions) { 

			int maxScore = 0;
			E bestCandidate = null;

			loopForChoices: for (E choice : getListOfChoicesForParameter(dimension)) {

				List<E> candidateTuple = resultTuple;
				candidateTuple.set(dimension, choice); 
				   
				if (evaluateConstraint(candidateTuple) == EvaluationResult.FALSE) {
					continue loopForChoices;                   
				}

				int score = getScore(candidateTuple);
				System.out.println("Candidate tuple: " + candidateTuple + " Score: " + score);

				if (score > maxScore) {                  
					maxScore = score;
					bestCandidate = choice;
				}
			}

			if (bestCandidate != null) {
				resultTuple.set(dimension, bestCandidate);
			} else {
				System.out.println("BEST CANDIDATE IS NULL");
				//no choice got score bigger than 0 - we covered all N-tuples
				
				List<E> bestFullTuple = fScoreEvaluator.findBestFullTuple();
				
				if (bestFullTuple == null) {
					return null;
				}
				
				fScoreEvaluator.updateScores(bestFullTuple);
				return bestFullTuple;                            
			}    
		}

		fScoreEvaluator.updateScores(resultTuple);
		return resultTuple;                              
	}
	
	private int getScore(List<E> extendedTuple) {
		
		List<E> compressedTuple = new ArrayList<>();
		
		for (E choice : extendedTuple) {
			
			if (choice != null) {
				compressedTuple.add(choice);
			}
		}

		int score = fScoreEvaluator.getScore(compressedTuple);
		return score;
	}
	
//	private List<E> nextTest() {
//
//		debugCounter++;
//		
//		if (debugCounter > 10) {
//			return null;
//		}
//		
//		List<E> test = new ArrayList<>();
//		List<Integer> shuffledDimensions = createShuffledDimensions(); 
//		List<E> expandedTest = initializeTuple(shuffledDimensions.size());
//		
//		for (int i = 0; i < fDimensionCount; i++) {
//			
//			int dimension = shuffledDimensions.get(i);
//			int maxScore = 0;
//			E bestCandidateChoice = null;
//			
//			// checking choices for current dimension
//			List<E> choices = choices(dimension);
//			
//			loopForChoices: for (E choice : choices) {
//				
//				if (test.size() == i) {
//					test.add(i, choice);
//				} else {
//					test.set(i, choice);
//				}
//				
//				expandedTest.set(dimension, choice);
//				
//				if (constraintCheck(expandedTest) == EvaluationResult.FALSE) {
//					continue loopForChoices;
//				}
//				
//				int score = fScoreEvaluator.getScore(test);
//				
//				if (score > maxScore) {
//					maxScore = score;
//					bestCandidateChoice = choice;
//				}
//			}
//			if (bestCandidateChoice != null) {
//				test.set(i, bestCandidateChoice);
//			} else {
//				System.out.println("Returning null from nextTest");
//				return null;
//			}
//		}
//		test = format(shuffledDimensions, test);
//		fScoreEvaluator.update(test);
//		return test;
//	}

	private List<E> initializeTuple(int countOfDimensions) {

		List<E> expandedTest = new ArrayList<>();
		
		for (int counter = 0; counter < countOfDimensions; counter++) {
			expandedTest.add(null);
		}
		
		return expandedTest;
	}

	// to generate one test case, evaluate in total k dimension orders 
	// and pick up the one with the best score
//	public List<E> nextTestwithKdimensionOrder(int k) {
//		List<E> test = new ArrayList<>();
//		List<E> tempTest = new ArrayList<>();
//		int bestScore = 0;
//		for (int i = 0; i < k; i++) {
//			tempTest = getNextTest();
//			if (bestScore < fScoreEvaluator.getScore(tempTest)) {
//				test = tempTest;
//			}
//		}
//		return test;
//	}

	// returns a shuffled list of indices of input parameters
	private List<Integer> createShuffledIndicesToDimensions() {
		List<Integer> dimensions = new ArrayList<>();
		dimensions.addAll(fInputIndex);
		int attempt = 3;
		do {
			attempt--;
			Collections.shuffle(dimensions);
		} while (fHistoryDimensionOrder.contains(dimensions) && attempt > 0);

		fHistoryDimensionOrder.add(dimensions);
		return dimensions.subList(0, fDimensionCount);
	}

	private List<E> getListOfChoicesForParameter(int indexOfParameter) {
		
		return getInput().get(indexOfParameter);
	}

//	private List<E> format(List<Integer> dimension, List<E> test) {
//		return test.stream().sorted(Comparator.comparingInt(o -> dimension.get(test.indexOf(o))))
//				.collect(Collectors.toList());
//	}

	public EvaluationResult evaluateConstraint(List<E> tuple) {
		
		return getConstraintEvaluator().evaluate(tuple);
	}

}
