package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;

public class NwiseShScoreEvaluator<E> implements IScoreEvaluator<E> {

	private final Map<List<E>, Integer> fTupleOccurences = new HashMap<>();
	private final Map<List<E>, Integer> fScores = new HashMap<>();

	private static final int NOT_INCLUDE = -1;
	private int fCountOfDimensions;
	private int fN;
	private int fInitialNTupleCount;

	private IConstraintEvaluator<E> fConstraintEvaluator;
	private TupleDecompressor<E> fTupleDecompressor;

	public NwiseShScoreEvaluator(int argN) {

		this.fN = argN;
	}

	@Override
	public void initialize(
			List<List<E>> input,
			IConstraintEvaluator<E> constraintEvaluator) throws GeneratorException {

		if (input == null) {
			GeneratorException.report("Input of N-wise score evaluator should not be empty.");
		}

		fCountOfDimensions = input.size();

		fConstraintEvaluator = constraintEvaluator;

		if (fConstraintEvaluator != null) {
			fConstraintEvaluator.initialize(input);
		}

		createOccurenciesTab(input);

		calculateOccurenciesOfTuples();
		calculateScoresForTuples(input.size()); //calculate scores for all the tuples in constructed table

		fInitialNTupleCount = countNTuples();

		fTupleDecompressor = new TupleDecompressor<>();
		fTupleDecompressor.initialize(input);
	}

	@Override
	public int getCountOfInitialNTuples() {
		return fInitialNTupleCount;
	}

	@Override
	public int getCountOfRemainingNTuples() {
		return countNTuples();
	}

	@Override
	public boolean contains(SortedMap<Integer, E> tuple) {

		List<E> tupleWithoutDimensions = 
				TuplesHelper.convertSortedMapTupleToTupleWithoutDimensions(tuple);

		boolean result = fTupleOccurences.containsKey(tupleWithoutDimensions);

		return result;
	}

	@Override
	public int getCountOfTuples(SortedMap<Integer, E> tuple) {

		List<E> tupleWithoutDimensions = 
				TuplesHelper.convertSortedMapTupleToTupleWithoutDimensions(tuple);

		// fTupleOccurences.entrySet().forEach(e -> System.out.println(e));

		Integer occurences = fTupleOccurences.get(tupleWithoutDimensions);

		if (occurences == null) {
			return 0;
		}

		return occurences;
	}

	@Override
	public int getScoreForTestCase(SortedMap<Integer, E> testCase) {

		if (testCase.size() != fCountOfDimensions) {
			ExceptionHelper.reportRuntimeException("Invalid size of test case.");
		}

		int score = getScore(testCase);
		return score;
	}

	@Override
	public int getScore(
			SortedMap<Integer, E> tuple // this can be either a full or shorter tuple
			) {

		List<E> tupleWithoutDimensions = TuplesHelper.convertSortedMapTupleToTupleWithoutDimensions(tuple);

		int score = getScore(tupleWithoutDimensions);

		return score;
	}

	private int getScore(List<E> tuple) {

		if (tuple.size() > fN) {
			return calculateScoreForTupleLongerThanN(tuple, fN);
		}

		if (fScores.containsKey(tuple)) {
			return fScores.get(tuple);
		}

		return 0;
	}

	private void update(List<E> testCase) {

		if (testCase.size() < fN)
			return;

		fTupleOccurences.entrySet().removeIf(e -> e.getKey().size() == fN && testCase.containsAll(e.getKey()));

		calculateOccurenciesOfTuples();	
		fScores.clear();
		calculateScoresForTuples(fN);
	}

	@Override
	public void update(SortedMap<Integer, E> tuple) {

		List<E> tupleWithoutDimensions = TuplesHelper.convertSortedMapTupleToTupleWithoutDimensions(tuple);

		update(tupleWithoutDimensions);
	}

	//	@Override
	//	public E getChoiceFromInputDomain(List<E> sourceTuple, int dimension) {
	//
	//		List<E> choicesForDimension = fInput.get(dimension);
	//
	//		List<E> candidateTuple = TuplesHelper.createCloneOfTuple(sourceTuple);
	//
	//		for (int index = 0; index < choicesForDimension.size(); index++) {
	//
	//			E choice = choicesForDimension.get(index);
	//
	//			candidateTuple.set(dimension, choice);
	//
	//			if (checkConstraints(candidateTuple) != EvaluationResult.FALSE) {
	//				return choice;                   
	//			}
	//		}
	//
	//		return null;
	//	}


	//	@Override
	//	public List<E> findFullTupleWithGoodScore() {
	//
	//		List<E> compressedBestTuple = findBestNTuple();
	//
	//		if (compressedBestTuple == null) {
	//			return null;
	//		}
	//
	//		List<E> bestTuple = fTupleDecompressor.decompressTuple(compressedBestTuple); 
	//
	//		while(!TuplesHelper.tupleIsComplete(bestTuple)) {
	//
	//			List<E> bestRefillTuple = findBestRefillTuple(bestTuple);
	//
	//			if (bestRefillTuple == null) {
	//				return null;
	//			}
	//
	//			bestTuple = TuplesHelper.mergeTuples(bestTuple, bestRefillTuple);
	//		}
	//
	//		return bestTuple;
	//	}	

	//	@Override
	//	public boolean allNTuplesCovered() {
	//
	//		int tuplesToCover = fInitialNTupleCount * fCoverage / 100;
	//
	//		int countNTuples = countNTuples();
	//
	//		int tuplesCovered = fInitialNTupleCount - countNTuples;
	//
	//		if (tuplesCovered >= tuplesToCover) {
	//			return true;
	//		}
	//
	//		return false;
	//	}

	private void createOccurenciesTab(List<List<E>> input) {

		int[] countsOfChoicesForAllDimensions = input.stream().mapToInt(List::size).toArray();

		boolean isNextTuple = true;
		int[] currentIndexesOfTuple = IntStream.range(0, input.size()).map(e -> NOT_INCLUDE).toArray();

		while (isNextTuple) {

			isNextTuple = 
					isNextTuple(
							countsOfChoicesForAllDimensions, 
							currentIndexesOfTuple); // in-out parameter !!

			if (isNextTuple)
				addTupleToOccurencesTab(currentIndexesOfTuple, input);
		}
	}

	private boolean isNextTuple(int[] range, int[] indexesOfTuple) {

		for (int j = indexesOfTuple.length - 1; j >= 0; j--) {
			if (indexesOfTuple[j] + 1 == range[j]) {
				indexesOfTuple[j] = NOT_INCLUDE;
			} else {
				indexesOfTuple[j] = indexesOfTuple[j] + 1;
				return true;
			}
		}
		return false;
	}

	private void addTupleToOccurencesTab(int[] indexesOfTuple, List<List<E>> input) {

		List<E> tuple = decodeTuple(indexesOfTuple, input);

		List<E> expandedTuple = expandTuple(tuple, indexesOfTuple);

		if (tuple.size() > fN || checkConstraints(expandedTuple) == EvaluationResult.FALSE)
			return;

		int isFullTuple = tuple.size() == fN ? 1 : 0;

		fTupleOccurences.put(tuple, isFullTuple);
	}

	private List<E> decodeTuple(int[] indexesOfTuple, List<List<E>> input) {

		List<E> tuple = IntStream
				.range(0, indexesOfTuple.length)
				.mapToObj(i -> indexesOfTuple[i] == -1 ? null : input.get(i).get(indexesOfTuple[i]))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		return tuple;
	}

	private void calculateOccurenciesOfTuples() {

		for (int m = fN - 1; m > 0; m--) {
			List<List<E>> remove = new ArrayList<>();
			for (List<E> key : fTupleOccurences.keySet()) {
				if (key.size() == m) {
					int freq = (int) fTupleOccurences.keySet().stream()
							.filter(s -> s.size() == fN && s.containsAll(key)).count();
					if (freq > 0)
						fTupleOccurences.put(key, freq);
					else
						remove.add(key);
				}
			}
			remove.forEach(s -> fTupleOccurences.remove(s));
		}
	}

	private void calculateScoresForTuples(int size) {

		IntStream.range(1, size + 1).forEach(l -> {
			fTupleOccurences.keySet().forEach(k -> {
				if (k.size() == l) {
					int update = fTupleOccurences.get(k);
					if (l > 1)
						update = calculateScore(k);
					fScores.put(k, update);
				}
			});
		});
	}

	private int calculateScore(List<E> tuple) {
		return fTupleOccurences.get(tuple) * fTupleOccurences.keySet().stream()
				.filter(k -> tuple.containsAll(k) && k.size() == tuple.size() - 1).
				mapToInt(fScores::get).sum();
	}

	private EvaluationResult checkConstraints(List<E> tuple) {

		if (fConstraintEvaluator == null) {
			return EvaluationResult.TRUE;
		}

		return fConstraintEvaluator.evaluate(tuple);
	}

	public int countNTuples() {

		int count = 0;

		for (Map.Entry<List<E>,Integer> entry : fScores.entrySet()) {

			List<E> tuple = entry.getKey();

			if (tuple.size() == fN) {
				count++;
			}
		}

		return count;

	}

	private List<E> expandTuple(List<E> compressedTuple, int[] indexesOfTuple) {

		List<E> expandedTuple = new ArrayList<>();

		int compressedTupleIndex = 0;

		for (int index = 0; index < indexesOfTuple.length; index++) {

			int positionCode = indexesOfTuple[index];

			if (positionCode == NOT_INCLUDE) {
				expandedTuple.add(null);
			} else {
				expandedTuple.add(compressedTuple.get(compressedTupleIndex));
				compressedTupleIndex++;
			}
		}

		return expandedTuple;
	}

	public int getNumOfTuples() {
		return fTupleOccurences.size();
	}

	public void printSequence() {

		for (int m = fN; m > 0; m--) {
			int finalM = m;
			fTupleOccurences.keySet().stream().filter(s -> s.size() == finalM).forEach(s -> {
				s.forEach(k -> System.out.print(k + ","));
			});
		}
	}

	private int calculateScoreForTupleLongerThanN(List<E> tuple, int N) {

		IteratorForSubTuples<E> iterator = new IteratorForSubTuples<E>(tuple, N);

		int totalScore = 0;

		while(iterator.hasNext()) {

			List<E> subTuple = (List<E>) iterator.next();

			int scoreForSubTuple = getScoreForTuple(subTuple);
			totalScore += scoreForSubTuple;
		}

		return totalScore;
	}

	private int getScoreForTuple(List<E> tuple) {

		if (fScores.containsKey(tuple)) {
			return fScores.get(tuple);
		} 

		return 0;
	}

	//	private List<E> findBestRefillTuple(List<E> mainTuple) {
	//
	//		int bestScore = 0;
	//		List<E> decompressedBestRefillTuple = null;
	//
	//		for (Map.Entry<List<E>,Integer> entry : fScores.entrySet()) {
	//
	//			List<E> compressedTuple = entry.getKey();
	//
	//			List<E> decompressedTuple = fTupleDecompressor.decompressTuple(compressedTuple);
	//
	//			if (!isRefillDimensionsMatch(mainTuple, decompressedTuple)) {
	//				continue;
	//			}
	//
	//			List<E> mergedTuple = TuplesHelper.mergeTuples(mainTuple, decompressedTuple);
	//
	//			if (constraintCheck(mergedTuple) == EvaluationResult.FALSE) {
	//				continue;
	//			}
	//
	//			int score = entry.getValue();
	//
	//			if (score > bestScore) {
	//				bestScore = score;
	//
	//				decompressedBestRefillTuple = decompressedTuple;
	//			}
	//		}
	//
	//		if (decompressedBestRefillTuple == null) {
	//			return findRefillTupleFromTestDomain(mainTuple);
	//		}
	//
	//		return decompressedBestRefillTuple;
	//	}

	//	private List<E> findRefillTupleFromTestDomain(List<E> mainTuple) {
	//
	//		List<E> refillTuple = new ArrayList<>();
	//
	//		for (int dimension = 0; dimension < mainTuple.size(); dimension++) {
	//
	//			E mainChoice = mainTuple.get(dimension);
	//
	//			if (mainChoice != null) {
	//				refillTuple.add(null);
	//				continue;
	//			}
	//
	//			E choice = chooseChoiceFromTestDomainMatchingConstraints(dimension, mainTuple);
	//
	//			if (choice == null) {
	//				return null;
	//			}
	//
	//			refillTuple.add(choice);
	//		}
	//
	//		return refillTuple;
	//	}

	//	private E chooseChoiceFromTestDomainMatchingConstraints(int dimension, List<E> mainTuple) {
	//
	//		List<E> choices = fInput.get(dimension);
	//
	//		List<E> shuffledChoices = TuplesHelper.createCloneOfTuple(choices);
	//		Collections.shuffle(shuffledChoices);
	//
	//		List<E> constraintTestTuple = TuplesHelper.createCloneOfTuple(mainTuple);
	//
	//		for (int index = 0; index < shuffledChoices.size(); index++) {
	//
	//			E choiceToAdd = choices.get(index);
	//
	//			constraintTestTuple.set(dimension, choiceToAdd);
	//
	//			if (constraintCheck(constraintTestTuple) != EvaluationResult.FALSE) {
	//				return choiceToAdd;
	//			}
	//		}
	//
	//		return null;
	//	}

	//	private List<E> findBestNTuple() {
	//
	//		int bestScore = 0;
	//		List<E> bestTuple = null;
	//
	//		for (Map.Entry<List<E>,Integer> entry : fScores.entrySet()) {
	//
	//			List<E> tuple = entry.getKey();
	//
	//			if (tuple.size() != fN) {
	//				continue;
	//			}
	//
	//			List<E> decompressedTuple = fTupleDecompressor.decompressTuple(tuple);
	//
	//			if (constraintCheck(decompressedTuple) == EvaluationResult.FALSE) {
	//				continue;
	//			}
	//
	//			int score = entry.getValue();
	//
	//			if (score > bestScore) {
	//				bestScore = score;
	//				bestTuple = tuple;
	//			}
	//		}
	//
	//		return bestTuple;
	//	}

	public List<E> findBestSimpleTuple(List<Integer> dimensions) {

		List<E> bestTuple = null;
		int bestScore = 0;

		for (Map.Entry<List<E>,Integer> entry : fScores.entrySet()) {

			List<E> tuple = entry.getKey();

			List<E> uncompressedTuple = fTupleDecompressor.decompressTuple(tuple);

			if (!TuplesHelper.isDimensionsMatch(uncompressedTuple, dimensions)) {
				continue;
			}

			int score = entry.getValue();

			if (score > bestScore) {
				bestScore = score;
				bestTuple = tuple;
			}
		}

		return bestTuple;

	}

	//	private boolean isRefillDimensionsMatch(List<E> mainTuple, List<E> refillTuple) {
	//
	//		int dimensionCount = mainTuple.size();
	//
	//		if (dimensionCount != refillTuple.size()) {
	//			ExceptionHelper.reportRuntimeException("Invalid sizes of tuples while matching dimensions.");
	//		}
	//
	//		for (int index = 0; index < dimensionCount; index++)  {
	//
	//			E refillItem = refillTuple.get(index);
	//
	//			if (refillItem != null) {
	//
	//				E mainItem = mainTuple.get(index);
	//
	//				if (mainItem != null) {
	//					return false;
	//				}
	//			}
	//		}
	//
	//		return true;
	//	}

	public void printTuples() {

		//		printOccurences();

		//		printScores();
	}

	public int printOccurences() {

		System.out.println("Tuple occurences: ");

		int counter = 0;

		for (Map.Entry<List<E>,Integer> entry : fTupleOccurences.entrySet()) {

			List<E> key = entry.getKey();

			if (key.size() == fN) {
				counter++;
			}

			System.out.println("[" + entry.getValue() + "]  " + key );
		}

		System.out.println("End of tuple occurences");
		return counter;
	}

	public void printScores() {

		System.out.println("\nScores: ");

		for (Map.Entry<List<E>,Integer> entry : fScores.entrySet()) {

			System.out.println("[" + entry.getValue() + "]  " + entry.getKey() );
		}

		System.out.println("End of scores");

	}

}
