package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.*;
import com.google.common.collect.*;

public class NWiseScoreEvaluator<E> implements IScoreEvaluator<E> {

	private IConstraintEvaluator<E> fConstraintEvaluator;
	List<SortedMap<Integer, E>> allNTuples = null;

	static final int MAX_TUPLES = 250000;
	static final int fLogLevel = 0;
	int score = 0;

	public void init(List<List<E>> input, IConstraintEvaluator<E> constraintEvaluator, int argN)
			throws GeneratorException {

		this.fConstraintEvaluator = constraintEvaluator;
		allNTuples = getAllNTuples(input, argN);

	}

	@Override
	public int getScore(List<E> tuple) {

		int size = tuple.size();
		int tupleScore = 0;
		SortedMap<Integer, E> temtuple = allNTuples.get(size);

		Set entryset = temtuple.entrySet();
		Iterator i = entryset.iterator();

		while (i.hasNext()) {

			Map.Entry m = (Map.Entry) i.next();
			List<E> checktuple = (List<E>) m.getValue();

			if (checktuple == tuple) {
				tupleScore = (Integer) m.getKey();
				break;
			}

		}

		if (tupleScore == 0) {

			System.out.print("The tuple does not exist in the given input domain!!");
			return 0;

		}

		score = tupleScore;

		if (size > 1) {

			for (List<E> subtuple : combinations(tuple, size - 1)) {
				score += tupleScore * getScore(subtuple);
			}

		}

		return score;
	}

	@Override
	public void update(List<E> tuple) { // not sure if we need this method??

		score = getScore(tuple);

	}

	
//	private List<List<E>> getallsubtuples(List<E> tuple){ // probably useful later on
//		
//		int size = tuple.size();
//		List<List<E>> allsubtuples = null;
//		
//		if (size > 1) {
//			
//			size = size - 1;
//			List<List<E>> subtupleforreducedSize = combinations(tuple, size);
//			allsubtuples.addAll(subtupleforreducedSize);
//			
//		}
//		
//		return allsubtuples;
//		
//	}

	private List<List<E>> combinations(List<E> tuple, int k) {

		if (k == 0 || tuple.isEmpty()) {

			return Collections.emptyList();

		}

		if (k == 1) {

			return tuple.stream().map(e -> Stream.of(e).collect(Collectors.toList())).collect(Collectors.toList());

		}

		Map<Boolean, List<E>> headAndTail = split(tuple, 1);
		List<E> head = headAndTail.get(true);
		List<E> tail = headAndTail.get(false);

		List<List<E>> c1 = combinations(tail, (k - 1)).stream().map(e -> {
			List<E> l = new ArrayList<>();
			l.addAll(head);
			l.addAll(e);
			return l;
		}).collect(Collectors.toList());

		List<List<E>> c2 = combinations(tail, k);
		c1.addAll(c2);

		return c1;

	}

	private Map<Boolean, List<E>> split(List<E> list, int n) {

		return IntStream.range(0, list.size()).mapToObj(i -> new SimpleEntry<>(i, list.get(i)))
				.collect(partitioningBy(entry -> entry.getKey() < n, mapping(SimpleEntry::getValue, toList())));

	}

// from the existing method with some changes
	private List<SortedMap<Integer, E>> getAllNTuples(List<List<E>> input, int argN) {

		int dimensionCount = input.size();

		List<SortedMap<Integer, E>> allValidTuples = new ArrayList<>();

		allValidTuples.add(Maps.newTreeMap());

		for (int tupleSize = 0; tupleSize < argN; tupleSize++) {

			List<SortedMap<Integer, E>> newValidTuples = new ArrayList<>();

			for (SortedMap<Integer, E> tuple : allValidTuples) {

				Integer maxDimension = -1;

				if (!tuple.isEmpty()) {
					maxDimension = tuple.lastKey();
				}

				addValidTuples(input, argN, dimensionCount, tupleSize, maxDimension, tuple, newValidTuples);

				if (newValidTuples.size() > MAX_TUPLES) {
					ExceptionHelper.reportRuntimeException("The number of tuples is limited to " + MAX_TUPLES + ". "
							+ "The current value is: " + newValidTuples.size() + ". "
							+ "To fix this issue, limit the number of arguments, choices or include additional constraint.");
				}
			}

			System.out.println("Tuple size: " + tupleSize + ". Generated tuples: " + newValidTuples.size());
			allValidTuples = newValidTuples;
			
		}

		AlgoLogger.log("All N tuples", allValidTuples, 1, fLogLevel);
		return allValidTuples;
	}

	private void addValidTuples(List<List<E>> input, int argN, int dimensionCount, int tupleSize, Integer maxDimension,
			SortedMap<Integer, E> tuple, List<SortedMap<Integer, E>> inOutValidTuples) {

		for (int dimension = maxDimension + 1; dimension < dimensionCount - (argN - 1 - tupleSize); dimension++) {

			final List<E> inputForOneDimension = input.get(dimension);

			addTuplesForOneDimension(dimension, inputForOneDimension, tuple, dimensionCount, inOutValidTuples);

			tuple.remove(dimension);
		}
	}

	private void addTuplesForOneDimension(int dimension, List<E> inputForOneDimension, SortedMap<Integer, E> tuple,
			int dimensionCount, List<SortedMap<Integer, E>> inOutTuples) {

		for (E v : inputForOneDimension) {

			tuple.put(dimension, v);

			if (evaluateConstraints(AlgorithmHelper.uncompressTuple(tuple, dimensionCount)) == EvaluationResult.TRUE) {
				SortedMap<Integer, E> newTuple = new TreeMap<>(tuple);
				inOutTuples.add(newTuple);
			}
		}
	}

	private EvaluationResult evaluateConstraints(List<E> tuple) {

		return fConstraintEvaluator.evaluate(tuple);

	}

}
