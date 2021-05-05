package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExceptionHelper;

public class NwiseScoreEvaluator<E> implements IScoreEvaluator<E> {

	
	// TODO - remove println
	
	// TODO - add constraint checking for getBestFullTuple
	
	// TODO - add coverage 
	
	// TODO - move getNext from algorithm here

	// TODO - refactor functions to tuple helper
	
	private List<List<E>> fInput;
	
	private final Map<List<E>, Integer> fScores = new HashMap<>(); // store all the scores
	private final Map<List<E>, Integer> fTupleOccurences = new HashMap<>(); // store all the constructed tables
	private static final int NOT_INCLUDE = -1;
	private int fN; // The number of dimensions to be covered
	private int fDimensionCount; // Total number of dimensions for an input domain

	private IConstraintEvaluator<E> fconstraintEvaluator;
	private TupleDecompressor<E> fTupleDecompressor;
	
	public NwiseScoreEvaluator(int argN) throws GeneratorException {

		this.fN = argN;
	}

	@Override
	public void initialize(
			List<List<E>> input, 
			IConstraintEvaluator<E> constraintEvaluator) throws GeneratorException {

		if (input == null) {
			GeneratorException.report("Input of N-wise score evaluator should not be empty.");
		}
		
		fInput = input;
		System.out.println("Input:" + input);

		this.fDimensionCount = input.size();
		this.fconstraintEvaluator = constraintEvaluator;

		if (fconstraintEvaluator != null) {
			fconstraintEvaluator.initialize(input);
		}

		System.out.println("Initialize BEG");

		int[] encode = IntStream.range(0, input.size()).map(e -> NOT_INCLUDE).toArray();
		int[] range = input.stream().mapToInt(List::size).toArray();
		boolean flag = true;

		while (flag) {
			flag = plus(encode, range);
			if (flag)
				add(encode, input);
		}

		calculateFrequency(); // calculate occurences in the constructed table
		calculateScore(input.size()); //calculate scores for all the tuples in constructed table

		printTuples();
		
		fTupleDecompressor = new TupleDecompressor<>();
		fTupleDecompressor.initialize(input);

		System.out.println("Initialize END");

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

		System.out.println("--- UPDATE --------------------------------------------------------------------------------------------------");
		System.out.println("Test case: " + testCase);
		
//		printNSubTuplesOfUpdatedTuple(testCase);

		// if the test does not cover any argN-tuple, the scores does not update
		if (testCase.size() < fN)
			return;

		// obtain all the argN-tuples covered by the test and remove them
		fTupleOccurences.entrySet().removeIf(e -> e.getKey().size() == fN && testCase.containsAll(e.getKey()));
		// update the occurences (i.e., fTupleOccurences) in the constructed table and scores (i.e., fScores)
		calculateFrequency();
		fScores.clear();
		calculateScore(fN);
		
		printTuples();
	}
	
	@Override
	public List<E> findBestFullTuple() {
	
		List<E> compressedBestTuple = findBestNTuple();
		
		if (compressedBestTuple == null) {
			return null;
		}
		
		List<E> bestTuple = fTupleDecompressor.decompressTuple(compressedBestTuple); 
		
		while(!tupleIsComplete(bestTuple)) {
			
			List<E> bestRefillTuple = findBestRefillTuple(bestTuple);
			
			bestTuple = mergeTuples(bestTuple, bestRefillTuple);
		}
		
		return bestTuple;
	}	
	
	private void printTuples() {

		printOccurences();
		
		printScores();
	}

	private int printOccurences() {
		
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
	
	private void printScores() {
		
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

	private void add(int[] encode, List<List<E>> inputs) {

		List<E> tuple = decodeTuple(encode, inputs);

		List<E> expandedTuple = expandTuple(tuple, encode);

		if (tuple.size() > fN || constraintCheck(expandedTuple) == EvaluationResult.FALSE)
			return;

		int isFullTuple = tuple.size() == fN ? 1 : 0;

		//		if (isFullTuple == 1) {
		//			System.out.println("tuple: " + tuple );
		//		}

		fTupleOccurences.put(tuple, isFullTuple);
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

	private void calculateFrequency() {
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

	private List<E> decodeTuple(int[] encode, List<List<E>> inputs) {
		return IntStream.range(0, encode.length).mapToObj(i -> encode[i] == -1 ? null : inputs.get(i).get(encode[i]))
				.filter(Objects::nonNull).collect(Collectors.toList());
	}

	private void calculateScore(int size) {
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
				.filter(k -> tuple.containsAll(k) && k.size() == tuple.size() - 1).mapToInt(fScores::get).sum();
	}

	public EvaluationResult constraintCheck(List<E> tuple) {

		if (fconstraintEvaluator == null) {
			return EvaluationResult.TRUE;
		}

		return fconstraintEvaluator.evaluate(tuple);
	}

	public int getNumOfTuples() {
		return fTupleOccurences.size();
	}

	public void printSequence() {
		for (int m = fN; m > 0; m--) {
			int finalM = m;
			fTupleOccurences.keySet().stream().filter(s -> s.size() == finalM).forEach(s -> {
				s.forEach(k -> System.out.print(k + ","));
				//				System.out.println(fTupleOccurences.get(s));
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

//	private int printNSubTuplesOfUpdatedTuple(List<E> testCase) {
//		
//		IteratorForSubTuples<E> iterator = new IteratorForSubTuples<>(testCase, fN);
//		
//		int counter = 0;
//		
//		while(iterator.hasNext()) {
//			List<E> subTuple = iterator.next();
//			System.out.println(subTuple);
//			counter++;
//		}
//		
//		System.out.println("Sub tuples count: " + counter + "\n");
//		return counter;
//	}

	public boolean existNDimensions(int argN){
		return fScores.keySet().stream().anyMatch(k-> k.size() == argN);
	}

	public List<List<E>> getNTuples(int argN){
		return fScores.keySet().stream().filter(k -> k.size() == argN).collect(Collectors.toList());
	}
	
	private List<E> mergeTuples(List<E> mainTuple, List<E> tupleToMerge) {
		
		if (mainTuple == null) {
			ExceptionHelper.reportRuntimeException("Empty main tuple when merging tuples.");
		}

		if (tupleToMerge == null) {
			ExceptionHelper.reportRuntimeException("Empty tuple to be merged.");
		}
		
		if (mainTuple.size() != tupleToMerge.size()) {
			ExceptionHelper.reportRuntimeException("Invalid sizes of main and merged tuples.");
		}
		
		for (int index = 0; index < mainTuple.size(); index++) {
		
			E choiceToMerge = tupleToMerge.get(index);
			
			if (choiceToMerge == null) {
				continue;
			}
			
			E mainChoice = mainTuple.get(index);
			
			if (mainChoice != null) {
				ExceptionHelper.reportRuntimeException("Attempt to overwrite tuple value during tuple merging.");
			}
			
			mainTuple.set(index, choiceToMerge);
		}
		
		return mainTuple;
	}

	private boolean tupleIsComplete(List<E> tuple) {
		
		for (int index = 0; index < tuple.size(); index++) {
			
			if (tuple.get(index) == null) {
				return false;
			}
		}
		
		return true;
	}

	private List<E> findBestRefillTuple(List<E> mainTuple) {
		
		int bestScore = 0;
		List<E> bestRefillTupleCompressed = null;
		
		for (Map.Entry<List<E>,Integer> entry : fScores.entrySet()) {

			List<E> compressedTuple = entry.getKey();
			
			List<E> decompressedTuple = fTupleDecompressor.decompressTuple(compressedTuple);
			
			if (!isRefillDimensionsMatch(mainTuple, decompressedTuple)) {
				continue;
			}
			
			int score = entry.getValue();
						
			if (score > bestScore) {
				bestScore = score;
				
				bestRefillTupleCompressed = compressedTuple;
			}
		}
		
		
		if (bestRefillTupleCompressed == null) {
			return getRefillTupleFromTestDomain(mainTuple);
		}
		
		List<E> decompressedBestRefillTuple = fTupleDecompressor.decompressTuple(bestRefillTupleCompressed);
		
		return decompressedBestRefillTuple;
	}
	

	private List<E> getRefillTupleFromTestDomain(List<E> mainTuple) {
		
		System.out.println("Getting refill tuple from test domain. mainTuple: " + mainTuple);
		
		List<E> refillTuple = new ArrayList<>();
		
		for (int dimension = 0; dimension < mainTuple.size(); dimension++) {
			
			System.out.println("Dimension: " + dimension);
			
			E mainChoice = mainTuple.get(dimension);
			
			System.out.println("mainChoice: " + mainChoice);
			E choiceToAdd = null;
			
			if (mainChoice == null) {
				choiceToAdd = chooseRandomChoiceFromTestDomain(dimension);
			}
			
			refillTuple.add(choiceToAdd);
			System.out.println("refillTuple (partial): " + refillTuple);
		}
		
		System.out.println("returned refill tuple: " + refillTuple);
		return refillTuple;
	}

private E chooseRandomChoiceFromTestDomain(int dimension) {
	
		if (dimension < 0 || dimension >= fDimensionCount) {
			ExceptionHelper.reportRuntimeException("Invalid dimension while choosing random choice.");
		}
		
		List<E> choices = fInput.get(dimension);
		
		Random r = new Random(); 
		
		int choiceIndex = r.nextInt(choices.size());
		
		return choices.get(choiceIndex);
	}

//	private int countFreeDimensions(List<E> tuple) {
//		
//		int counter = 0;
//		
//		for (int index = 0; index < tuple.size(); index++) {
//			
//			E choice = tuple.get(index);
//			
//			if (choice == null) {
//				counter++;
//			}
//		}
//		
//		return counter;
//	}

	private List<E> findBestNTuple() {
		
		int bestScore = 0;
		List<E> bestTuple = null;
		
		for (Map.Entry<List<E>,Integer> entry : fScores.entrySet()) {

			int score = entry.getValue();
						
			if (score > bestScore) {
				bestScore = score;
				
				List<E> tuple = entry.getKey();
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
			
			if (!isDimensionsMatch(uncompressedTuple, dimensions)) {
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

	private boolean isDimensionsMatch(List<E> uncompressedTuple, List<Integer> dimensions) {
		
		int dimensionSize = dimensions.size();
		
		
		if (uncompressedTuple.size()  != dimensionSize) {
			return false;
		}
		
		for (int index = 0; index < dimensionSize; index++)  {
			
			E choice = uncompressedTuple.get(index);
			Integer dimension = dimensions.get(index);
			
			if (choice == null && dimension != null) {
				return false;
			}
			
			if (choice != null && dimension == null) {
				return false;
			}
		}
		
		return true;
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
