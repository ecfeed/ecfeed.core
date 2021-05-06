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

		if (evaluateConstraint(resultTuple) == EvaluationResult.FALSE) {
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

	private List<E> initializeTuple(int countOfDimensions) {

		List<E> expandedTest = new ArrayList<>();
		
		for (int counter = 0; counter < countOfDimensions; counter++) {
			expandedTest.add(null);
		}
		
		return expandedTest;
	}

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

	public EvaluationResult evaluateConstraint(List<E> tuple) {
		
		return getConstraintEvaluator().evaluate(tuple);
	}

}
