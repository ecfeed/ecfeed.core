package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.EvaluationResult;

public class NwiseScoreEvaluator<E> implements IScoreEvaluator<E> {

	private final Map<List<E>, Integer> fScores = new HashMap<>(); // store all the scores
	private final Map<List<E>, Integer> fTupleOccurences = new HashMap<>(); // store all the constructed tables
	private static final int NOT_INCLUDE = -1;
	private int fArgCount; // The number of dimensions to be covered
	private int fDimensionCount; // Total number of dimensions for an input domain

	private IConstraintEvaluator<E> fconstraintEvaluator;

	public NwiseScoreEvaluator(int argN) throws GeneratorException {

		this.fArgCount = argN;
//		this.fDimensionCount = input.size();
//		this.fconstraintEvaluator = constraintEvaluator;
	}
	
	@Override
	public void initialize(
			List<List<E>> input, 
			IConstraintEvaluator<E> constraintEvaluator) throws GeneratorException {
		
		this.fDimensionCount = input.size();
		this.fconstraintEvaluator = constraintEvaluator;
		
		if (input == null || constraintEvaluator == null) {
			GeneratorException.report("input or constraints cannot be null");
		}
		
		fconstraintEvaluator.initialize(input);

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
		
		if (tuple.size() > fArgCount || constraintCheck(expandedTuple) == EvaluationResult.FALSE)
			return;
		
		fTupleOccurences.put(tuple, (tuple.size() == fArgCount ? 1 : 0));
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
		for (int m = fArgCount - 1; m > 0; m--) {
			List<List<E>> remove = new ArrayList<>();
			for (List<E> key : fTupleOccurences.keySet()) {
				if (key.size() == m) {
					int freq = (int) fTupleOccurences.keySet().stream()
							.filter(s -> s.size() == fArgCount && s.containsAll(key)).count();
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
		return fconstraintEvaluator.evaluate(tuple);
	}

	public int getNumOfTuples() {
		return fTupleOccurences.size();
	}

	public void printSequence() {
		for (int m = fArgCount; m > 0; m--) {
			int finalM = m;
			fTupleOccurences.keySet().stream().filter(s -> s.size() == finalM).forEach(s -> {
				s.forEach(k -> System.out.print(k + ","));
				System.out.println(fTupleOccurences.get(s));
			});
		}
	}

	public int getScore(List<E> tuple) {
		if (fScores.containsKey(tuple))
			return fScores.get(tuple);
		return -1;
	}
	
	public void update(List<E> test) {
		// if the test does not cover any argN-tuple, the scores does not update
		if (test.size() < fArgCount)
			return;
		// obtain all the argN-tuples covered by the test and remove them
		fTupleOccurences.entrySet().removeIf(e -> e.getKey().size() == fArgCount && test.containsAll(e.getKey()));
		// update the occurences (i.e., fTupleOccurences) in the constructed table and scores (i.e., fScores)
		calculateFrequency();
		fScores.clear();
		calculateScore(fArgCount);
	}
	
	   public boolean existNDimensions(int argN){
	        return fScores.keySet().stream().anyMatch(k-> k.size() == argN);
	    }
	   
	   public List<List<E>> getNTuples(int argN){
	        return fScores.keySet().stream().filter(k -> k.size() == argN).collect(Collectors.toList());
	    }

}
