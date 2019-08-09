/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.management.RuntimeErrorException;

import com.ecfeed.core.evaluator.DummyEvaluator;
import com.ecfeed.core.generators.DimensionedItem;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IEcfProgressMonitor;
import com.ecfeed.core.utils.SystemLogger;

public class RandomizedNWiseAlgorithm<E> extends AbstractNWiseAlgorithm<E> {

	final private int RANDOM_TEST_TRIES = 10;

	private Set<List<Integer>> allDimCombs = null;

	private Set<List<DimensionedItem<E>>> fPotentiallyRemainingTuples = null;

	// The set of all n-tuples for which none of the constraints fail (this set
	// includes both the n-tuples for which all the constraints can be evaluated
	// and pass, as well as the constraints for which at least one of
	// constraints cannot be evaluated).
	private Set<List<DimensionedItem<E>>> fRemainingTuples = null;

	private int fIgnoreCount = 0;

	final private int CONSISTENCY_LOOP_LIM = 10;

	public RandomizedNWiseAlgorithm(int n, int coverage) {
		super(n, coverage);
	}

	@Override
	public void reset() {
		try {
			Map<Boolean, Set<List<DimensionedItem<E>>>> nTuples = getAllNTuples();
			fPotentiallyRemainingTuples = nTuples.get(null);
			fRemainingTuples = nTuples.get(true);
			fRemainingTuples.addAll(fPotentiallyRemainingTuples);

			fIgnoreCount = fRemainingTuples.size() * (100 - getCoverage()) / 100;

		} catch (GeneratorException e) {
			throw new RuntimeErrorException(new Error(e));
		}
		super.reset();
	}

	@Override
	public List<E> getNext() throws GeneratorException {

		IEcfProgressMonitor generatorProgressMonitor = getGeneratorProgressMonitor();

		while (true) {

			if (generatorProgressMonitor != null) {
				if (generatorProgressMonitor.isCanceled()) {
					return null;
				}
			}

			if (fRemainingTuples.size() <= fIgnoreCount)
				return null;


			List<DimensionedItem<E>> nTuple = fRemainingTuples.iterator().next();
			fRemainingTuples.remove(nTuple);

			List<E> randomTest = generateRandomTest(nTuple);

			if (randomTest != null) {
				if (fRemainingTuples.size() <= fIgnoreCount) {
					// no need for optimization
					incrementProgress(1);
					return randomTest;
				}
				List<E> improvedTest = improveCoverageOfTest(randomTest, nTuple);
				int cov = removeCoveredNTuples(improvedTest);
				incrementProgress(cov);
				return improvedTest;
			} else {
				//System.out.println("Cannot generate test for" + toString(nTuple) + "!!! " + fRemainingTuples.size());
				if (!fPotentiallyRemainingTuples.contains(nTuple))
					fRemainingTuples.add(nTuple);
			}
		}
	}

	/*
	 * Removes all the nTuples that are covered by improvedTest from both
	 * fRemainingTuples and fPotentiallyRemainingTuples
	 * 
	 * @param improvedTest
	 */
	private int removeCoveredNTuples(List<E> test) {
		Set<List<DimensionedItem<E>>> coveredTuples = getCoveredNTuples(test);
		int cov = coveredTuples.size();
		fRemainingTuples.removeAll(coveredTuples);
		fPotentiallyRemainingTuples.removeAll(coveredTuples);
		return cov;
	}

	private Set<List<DimensionedItem<E>>> getCoveredNTuples(List<E> test) {
		int k = allDimCombs.size();
		Set<List<DimensionedItem<E>>> coveredTuples = new HashSet<>();

		for (List<DimensionedItem<E>> nTuple : fRemainingTuples) {
			if (k == 0)
				break;

			boolean isCovered = true;
			for (DimensionedItem<E> var : nTuple)
				if (!test.get(var.getDimension()).equals(var.getItem())) {
					isCovered = false;
					break;
				}
			if (isCovered) {
				k--;
				coveredTuples.add(nTuple);
			}
		}
		return coveredTuples;
	}

	private List<E> improveCoverageOfTest(List<E> randomTest, List<DimensionedItem<E>> nTuple) {
		/*
		 * while you can improve coverage, make a random ordering of modifiable
		 * indices for each index in the list, check all available values for
		 * that index and choose the best. If the coverage is improved, use the
		 * newly generated tuple (test).
		 */
		List<E> improvedTest = randomTest;

		Map<Integer, Integer> dims = getModifiableDimensions(randomTest, nTuple);
		boolean progress;

		do {
			progress = false;

			// shuffle dims
			List<Integer> mDims = new ArrayList<>(dims.values());
			Collections.shuffle(mDims);

			for (int i = 0; i < mDims.size(); i++) {
				int coverage;
				int bestCov = getCoverage(improvedTest);
				int dim = mDims.get(i);
				List<E> input = getInput().get(dim);
				List<E> bests = new ArrayList<>();
				bests.add(improvedTest.get(dim));
				for (E feature : input) {
					improvedTest.set(dim, feature);
					if (checkConstraints(improvedTest) == EvaluationResult.TRUE) {
						coverage = getCoverage(improvedTest);

						if (coverage >= bestCov) {
							if (coverage > bestCov) {
								progress = true;
								bestCov = coverage;
								bests.clear();
							}
							bests.add(feature);
						}
					}
				}
				// use the best feature
				improvedTest.set(dim, bests.get((new Random().nextInt(bests.size()))));
			}
		} while (progress);

		return improvedTest;
	}

	private Map<Integer, Integer> getModifiableDimensions(List<E> randomTest, List<DimensionedItem<E>> nTuple) {
		// make a list of dimensions that do not appear in nTuple
		Map<Integer, Integer> dims = new HashMap<>();
		for (int i = 0; i < randomTest.size(); i++)
			dims.put(i, i);

		for (int i = 0; i < nTuple.size(); i++)
			dims.remove(nTuple.get(i).getDimension());
		return dims;
	}

	/*
	 * Randomly generates a test that contains 'nTuple' and satisfies all the
	 * constraints.
	 * nTuple - list of n values selected from available choices
	 */
	private List<E> generateRandomTest(List<DimensionedItem<E>> nTuple) {

		List<E> bestTest = null;
		int bestCoverage = 1;

		for (int cnt = 0; cnt < RANDOM_TEST_TRIES; cnt++) {

			List<E> currentTest = findTestSatisfyingAllConstraints(nTuple);

			if (currentTest == null) {
				continue;
			}

			if (fRemainingTuples.size() <= fIgnoreCount) {
				return currentTest;
			}

			int currentCoverage = getCoverage(currentTest) + 1; // one extra point for the current tuple

			if (currentCoverage >= bestCoverage) {
				bestTest = currentTest;
				bestCoverage = currentCoverage;
			}
		}

		return bestTest;
	}

	private List<E> findTestSatisfyingAllConstraints(List<DimensionedItem<E>> nTuple) {

		List<List<E>> paramsWithChoices = getInput();
		List<E> test = null;
		int itr = 0;

		do {
			test = createOneTest(paramsWithChoices, nTuple);

			if (checkConstraints(test) == EvaluationResult.TRUE) {
				return test;
			}

		} while (++itr < CONSISTENCY_LOOP_LIM);

		return null;
	}

	protected void debugPrintTuple(String message, List<DimensionedItem<E>> nTuple) {
		System.out.println(message);

		for (DimensionedItem<E> var : nTuple) {
			System.out.println("   " + var.toString());
		}
	}

	protected void debugPrintParametersAndChoices(String message, List<List<E>> tInput) {
		System.out.println(message);

		for (List<E> list : tInput) {
			SystemLogger.logListOfElements("list", list);
		}
	}

	private List<E> createOneTest(List<List<E>> tInput, List<DimensionedItem<E>> tuple) {

		List<E> result = createRandomTest(tInput);
		return plugInTupleIntoList(tuple, result);
	}

	private List<E> createRandomTest(List<List<E>> tInput) {

		List<E> result = new ArrayList<>();

		for (int i = 0; i < tInput.size(); i++) {
			List<E> features = tInput.get(i);
			result.add(features.get((new Random()).nextInt(features.size())));
		}

		return result;
	}

	private List<E> plugInTupleIntoList(List<DimensionedItem<E>> nTuple, List<E> listOfItems) {

		for (DimensionedItem<E> var : nTuple) {
			listOfItems.set(var.getDimension(), var.getItem());
		}

		return listOfItems;
	}

	private int getCoverage(List<E> test) {
		return getCoveredNTuples(test).size();
	}

	private Set<List<Integer>> getAllDimensionCombinations() {
		int dimCount = getInput().size();
		List<Integer> dimentions = new ArrayList<>();
		for (int i = 0; i < dimCount; i++)
			dimentions.add(i);

		return (new Tuples<>(dimentions, N)).getAll();
	}

	private Map<Boolean, Set<List<DimensionedItem<E>>>> getAllNTuples() throws GeneratorException {

		Set<List<Integer>> allCombs = getAllDimCombs();
		Map<Boolean, Set<List<DimensionedItem<E>>>> allNTuples = new HashMap<>();
		Set<List<DimensionedItem<E>>> validNTuple = new HashSet<>();
		Set<List<DimensionedItem<E>>> unevaluableNTuples = new HashSet<>();
		allNTuples.put(true, validNTuple);
		allNTuples.put(null, unevaluableNTuples);

		for (List<Integer> comb : allCombs) {

			List<List<DimensionedItem<E>>> tempIn = new ArrayList<>();
			for (int i = 0; i < comb.size(); i++) {
				List<DimensionedItem<E>> values = new ArrayList<>();
				for (E e : getInput().get(comb.get(i)))
					values.add(new DimensionedItem<E>(comb.get(i), e));
				tempIn.add(values);
			}

			CartesianProductAlgorithm<DimensionedItem<E>> cartAlg = new CartesianProductAlgorithm<>();
			cartAlg.initialize(tempIn, new DummyEvaluator(), getGeneratorProgressMonitor());
			List<DimensionedItem<E>> tuple = null;
			while ((tuple = cartAlg.getNext()) != null) {

				// Generate a full tuple from this nTuple to make sure that it
				// is consistent with the constraints
				List<E> fullTuple = new ArrayList<>();
				for (int i = 0; i < getInput().size(); i++)
					fullTuple.add(null);
				for (DimensionedItem<E> var : tuple)
					fullTuple.set(var.getDimension(), var.getItem());

				EvaluationResult check = checkConstraints(fullTuple);

				if (check == EvaluationResult.INSUFFICIENT_DATA) {
					unevaluableNTuples.add(tuple);
				} else if (check == EvaluationResult.TRUE) {
					validNTuple.add(tuple);
				}
			}
		}

		return allNTuples;
	}

	@SuppressWarnings("unchecked")
	void generateNTuples(Set<List<Integer>> allCombs, Set<List<DimensionedItem<E>>> allNTuples) throws GeneratorException {
		for (List<Integer> comb : allCombs) {
			List<List<E>> tempIn = new ArrayList<>();
			for (int i = 0; i < comb.size(); i++)
				tempIn.add(getInput().get(comb.get(i)));

			CartesianProductAlgorithm<E> cartAlg = new CartesianProductAlgorithm<>();
			cartAlg.initialize(tempIn, getConstraintEvaluator(), getGeneratorProgressMonitor());
			List<E> tuple = null;
			while ((tuple = cartAlg.getNext()) != null) {
				List<DimensionedItem<E>> extendedTuple = new ArrayList<>();
				for (int i = 0; i < comb.size(); i++)
					extendedTuple.add(new DimensionedItem<>(comb.get(i), tuple.get(i)));
				allNTuples.add(extendedTuple);
			}
		}
	}

	protected Set<List<Integer>> getAllDimCombs() {

		if (allDimCombs == null)
			allDimCombs = getAllDimensionCombinations();

		return allDimCombs;
	}

	/*
	 * If the incomplete tuple (tuple with null values at some of the indices)
	 * is consistent returns true. If evaluating the constraint requires
	 * accessing some of the indices with a null value, the constraints cannot
	 * be evaluated and the method returns null; otherwise it returns false.
	 */
//	protected EvaluationResult checkConstraintsOnExtendedNTuple(List<E> vector) {
//
//		if (vector == null) {
//			return EvaluationResult.TRUE;
//		}
//
//		boolean insufficientData = false;
//
//		for (IConstraint<E> constraint : getConstraints()) {
//
//			EvaluationResult value = constraint.evaluate(vector);
//			if (value == EvaluationResult.FALSE) {
//				return EvaluationResult.FALSE;
//			}
//
//			if (value == EvaluationResult.INSUFFICIENT_DATA) {
//				insufficientData = true;
//			}
//		}
//
//		if (insufficientData)
//			return EvaluationResult.INSUFFICIENT_DATA;
//
//		return EvaluationResult.TRUE;
//	}
}
