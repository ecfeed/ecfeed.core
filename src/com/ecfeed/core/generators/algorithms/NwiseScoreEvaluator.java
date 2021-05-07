package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;

public class NwiseScoreEvaluator<E> implements IScoreEvaluator<E> {

	private List<List<E>> fInput;

	private final Map<List<E>, Integer> fTupleOccurences = new HashMap<>();
	private final Map<List<E>, Integer> fScores = new HashMap<>();

	private static final int NOT_INCLUDE = -1;
	private int fN;
	private int fDimensionCount;
	private int fInitialNTupleCount;
	private int fCoverage;

	private IConstraintEvaluator<E> fConstraintEvaluator;
	private TupleDecompressor<E> fTupleDecompressor;

	public NwiseScoreEvaluator(int argN, int coverage) throws GeneratorException {

		this.fN = argN;
		this.fCoverage = coverage;
	}

	@Override
	public void initialize(
			List<List<E>> input, 
			IConstraintEvaluator<E> constraintEvaluator) throws GeneratorException {

		if (input == null) {
			GeneratorException.report("Input of N-wise score evaluator should not be empty.");
		}

		fInput = input;
		fDimensionCount = input.size();
		fConstraintEvaluator = constraintEvaluator;

		if (fConstraintEvaluator != null) {
			fConstraintEvaluator.initialize(input);
		}

		int[] encode = IntStream.range(0, input.size()).map(e -> NOT_INCLUDE).toArray();
		int[] arrayOfDimensions = input.stream().mapToInt(List::size).toArray();

		boolean flag = true;

		while (flag) {
			flag = plus(encode, arrayOfDimensions);
			if (flag)
				addTupleToOccurencesTab(encode, input);
		}

		calculateOccurenciesOfTuples();
		calculateScoresForTuples(input.size()); //calculate scores for all the tuples in constructed table

		fInitialNTupleCount = countNTuples();

		fTupleDecompressor = new TupleDecompressor<>();
		fTupleDecompressor.initialize(input);
	}

	@Override
	public int getScore(List<E> tuple) {

		if (tuple.size() > fN) {
			return calculateScoreForTupleLongerThanN(tuple, fN);
		}

		if (fScores.containsKey(tuple)) {
			return fScores.get(tuple);
		}

		return 0;
	}


	@Override
	public void updateScores(List<E> testCase) {

		if (testCase.size() < fN)
			return;

		fTupleOccurences.entrySet().removeIf(e -> e.getKey().size() == fN && testCase.containsAll(e.getKey()));

		calculateOccurenciesOfTuples();
		fScores.clear();
		calculateScoresForTuples(fN);
	}

	@Override
	public List<E> findBestFullTuple() {

		List<E> compressedBestTuple = findBestNTuple();

		if (compressedBestTuple == null) {
			return null;
		}

		List<E> bestTuple = fTupleDecompressor.decompressTuple(compressedBestTuple); 

		while(!TuplesHelper.tupleIsComplete(bestTuple)) {

			List<E> bestRefillTuple = findBestRefillTuple(bestTuple);

			if (bestRefillTuple == null) {
				return null;
			}

			bestTuple = TuplesHelper.mergeTuples(bestTuple, bestRefillTuple);
		}

		return bestTuple;
	}	

	@Override
	public boolean allNTuplesCovered() {

		int tuplesToCover = fInitialNTupleCount * fCoverage / 100;

		int tuplesCovered = fInitialNTupleCount - countNTuples();

		if (tuplesCovered >= tuplesToCover) {
			return true;
		}

		return false;
	}


	private void addTupleToOccurencesTab(int[] dimensions, List<List<E>> input) {

		List<E> tuple = decodeTuple(dimensions, input);

		List<E> expandedTuple = expandTuple(tuple, dimensions);

		if (tuple.size() > fN || constraintCheck(expandedTuple) == EvaluationResult.FALSE)
			return;

		int isFullTuple = tuple.size() == fN ? 1 : 0;

		fTupleOccurences.put(tuple, isFullTuple);
	}


	private List<E> decodeTuple(int[] dimensions, List<List<E>> input) {

		return IntStream.range(0, dimensions.length).mapToObj(i -> dimensions[i] == -1 ? null : input.get(i).get(dimensions[i]))
				.filter(Objects::nonNull).collect(Collectors.toList());
	}

	private int countNTuples() {

		int count = 0;

		for (Map.Entry<List<E>,Integer> entry : fScores.entrySet()) {

			List<E> tuple = entry.getKey();

			if (tuple.size() == fN) {
				count++;
			}
		}

		return count;

	}

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

	public int getdimensionCount() {
		return fDimensionCount;
	}

	private boolean plus(int[] encode, int[] range) {
		for (int j = encode.length - 1; j >= 0; j--) {
			if (encode[j] + 1 == range[j]) {
				encode[j] = NOT_INCLUDE;
			} else {
				encode[j] = encode[j] + 1;
				return true;
			}
		}
		return false;
	}

	private List<E> expandTuple(List<E> compressedTuple, int[] encodePattern) {

		List<E> expandedTuple = new ArrayList<>();

		int compressedTupleIndex = 0;

		for (int index = 0; index < encodePattern.length; index++) {

			int positionCode = encodePattern[index];

			if (positionCode == NOT_INCLUDE) {
				expandedTuple.add(null);
			} else {
				expandedTuple.add(compressedTuple.get(compressedTupleIndex));
				compressedTupleIndex++;
			}
		}

		return expandedTuple;
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

	public EvaluationResult constraintCheck(List<E> tuple) {

		if (fConstraintEvaluator == null) {
			return EvaluationResult.TRUE;
		}

		return fConstraintEvaluator.evaluate(tuple);
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

	private List<E> findBestRefillTuple(List<E> mainTuple) {

		int bestScore = 0;
		List<E> decompressedBestRefillTuple = null;

		for (Map.Entry<List<E>,Integer> entry : fScores.entrySet()) {

			List<E> compressedTuple = entry.getKey();

			List<E> decompressedTuple = fTupleDecompressor.decompressTuple(compressedTuple);

			if (!isRefillDimensionsMatch(mainTuple, decompressedTuple)) {
				continue;
			}

			List<E> mergedTuple = TuplesHelper.mergeTuples(mainTuple, decompressedTuple);

			if (constraintCheck(mergedTuple) == EvaluationResult.FALSE) {
				continue;
			}

			int score = entry.getValue();

			if (score > bestScore) {
				bestScore = score;

				decompressedBestRefillTuple = decompressedTuple;
			}
		}

		if (decompressedBestRefillTuple == null) {
			return findRefillTupleFromTestDomain(mainTuple);
		}

		return decompressedBestRefillTuple;
	}

	private List<E> findRefillTupleFromTestDomain(List<E> mainTuple) {

		List<E> refillTuple = new ArrayList<>();

		for (int dimension = 0; dimension < mainTuple.size(); dimension++) {

			E mainChoice = mainTuple.get(dimension);

			if (mainChoice != null) {
				refillTuple.add(null);
				continue;
			}

			E choice = chooseChoiceFromTestDomainMatchingConstraints(dimension, mainTuple);

			if (choice == null) {
				return null;
			}

			refillTuple.add(choice);
		}

		return refillTuple;
	}

	private E chooseChoiceFromTestDomainMatchingConstraints(int dimension, List<E> mainTuple) {

		List<E> choices = fInput.get(dimension);

		List<E> shuffledChoices = TuplesHelper.createCloneOfTuple(choices);
		Collections.shuffle(shuffledChoices);

		List<E> constraintTestTuple = TuplesHelper.createCloneOfTuple(mainTuple);

		for (int index = 0; index < shuffledChoices.size(); index++) {

			E choiceToAdd = choices.get(index);

			constraintTestTuple.set(dimension, choiceToAdd);

			if (constraintCheck(constraintTestTuple) != EvaluationResult.FALSE) {
				return choiceToAdd;
			}
		}

		return null;
	}

	private List<E> findBestNTuple() {

		int bestScore = 0;
		List<E> bestTuple = null;

		for (Map.Entry<List<E>,Integer> entry : fScores.entrySet()) {

			List<E> tuple = entry.getKey();

			if (tuple.size() != fN) {
				continue;
			}

			List<E> decompressedTuple = fTupleDecompressor.decompressTuple(tuple);

			if (constraintCheck(decompressedTuple) == EvaluationResult.FALSE) {
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

	private boolean isRefillDimensionsMatch(List<E> mainTuple, List<E> refillTuple) {

		int dimensionCount = mainTuple.size();

		if (dimensionCount != refillTuple.size()) {
			ExceptionHelper.reportRuntimeException("Invalid sizes of tuples while matching dimensions.");
		}

		for (int index = 0; index < dimensionCount; index++)  {

			E refillItem = refillTuple.get(index);

			if (refillItem != null) {

				E mainItem = mainTuple.get(index);

				if (mainItem != null) {
					return false;
				}
			}
		}

		return true;
	}

}
