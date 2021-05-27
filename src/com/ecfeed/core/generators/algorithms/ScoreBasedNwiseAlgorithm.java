package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class ScoreBasedNwiseAlgorithm<E> extends AbstractAlgorithm<E> {

	private int fDimensionCount; // Total number of dimensions for an input domain
	private List<Integer> fInputIndex; // Store index of parameters in input domain
	private IAwesomeScoreEvaluator<E> fScoreEvaluator;
	protected List<List<Integer>> fHistoryDimensionOrder = new ArrayList<>(); // Store historical dimension orders (each
	private int fCoverage;
	// randomly generated dimension order
	// could be different)

	public ScoreBasedNwiseAlgorithm(IAwesomeScoreEvaluator<E> fScoreEvaluator, int coverage) throws GeneratorException {

		this.fScoreEvaluator = fScoreEvaluator;
		fCoverage = coverage;
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
		return getTupleWithBestScore();
	}

	private List<E> getTupleWithBestScore() {

		if (allRequiredNTuplesCovered()) {
			return null;
		}

		List<E> resultTuple = initializeTuple(fDimensionCount);

		if (evaluateConstraint(resultTuple) == EvaluationResult.FALSE) {
			return null; // conflicting constraints
		}

		List<Integer> shuffledDimensions = createShuffledIndicesToDimensions(); 

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

				if (score > maxScore) {                  
					maxScore = score;
					bestCandidate = choice;
				}
			}

			if (bestCandidate != null) {

				resultTuple.set(dimension, bestCandidate);

			} else {

				E choice = getChoiceFromInputDomain(resultTuple, dimension);

				if (choice == null) {
					return null;
				}

				resultTuple.set(dimension, choice);
			}    
		}

		updateScoreEvaluator(resultTuple);
		
		return resultTuple;                              
	}

	private void updateScoreEvaluator(List<E> resultTuple) {
		
		SortedMap<Integer, E> sortedMapTuple = 
				TuplesHelper.convertExtendedTupleToSortedMapTuple(resultTuple);
		
		fScoreEvaluator.update(sortedMapTuple);
	}

	
	private E getChoiceFromInputDomain(List<E> sourceTuple, int dimension) {

		List<E> choicesForDimension = getInput().get(dimension);

		List<E> candidateTuple = TuplesHelper.createCloneOfTuple(sourceTuple);

		for (int index = 0; index < choicesForDimension.size(); index++) {

			E choice = choicesForDimension.get(index);

			candidateTuple.set(dimension, choice);

			if (checkConstraints(candidateTuple) != EvaluationResult.FALSE) {
				return choice;                   
			}
		}

		return null;
	}

	public boolean allRequiredNTuplesCovered() {

		int initialNTupleCount = fScoreEvaluator.getCountOfInitialNTuples();
		
		int tuplesCovered = initialNTupleCount - fScoreEvaluator.getCountOfRemainingNTuples();
		
		int tuplesToCover = calculateNumberOfTuplesToCover(fCoverage, initialNTupleCount);

		if (tuplesCovered >= tuplesToCover) {
			return true;
		}

		return false;
	}

	protected int calculateNumberOfTuplesToCover(int coverage, int initialNTupleCount) {
		
		return (int) Math.ceil((double)initialNTupleCount * coverage / 100);
	}
	
//	private int getScore(List<E> extendedTuple) {
//
//		List<E> compressedTuple = new ArrayList<>();
//
//		for (E choice : extendedTuple) {
//
//			if (choice != null) {
//				compressedTuple.add(choice);
//			}
//		}
//
//		int score = fScoreEvaluator.getScore(compressedTuple);
//		return score;
//	}

	private int getScore(List<E> extendedTuple) {

		SortedMap<Integer, E> sortedMapTuple = 
				TuplesHelper.convertExtendedTupleToSortedMapTuple(extendedTuple);
		
		int score = fScoreEvaluator.getScore(sortedMapTuple);
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
