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

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.ecfeed.core.generators.api.GeneratorExceptionHelper;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class RandomAlgorithm<E> extends AbstractAlgorithm<E> implements IAlgorithm<E> {

	private static final int NUMBER_OF_CANDIDATES = 100;
	private static final int IDLE_COUNTER = 100;

	private final int fCandidatesSize;
	private final int fLength;
	private final boolean fAdaptive;
	private final boolean fDuplicates;

	private final CartesianProductAlgorithm<E> fCartesianAlgorithm;
	private final List<List<E>> fHistory;


	public RandomAlgorithm(int length, boolean duplicates, boolean adaptive) {
		fDuplicates = duplicates;
		fAdaptive = adaptive;
		fLength = length;

		fCandidatesSize = fAdaptive ? NUMBER_OF_CANDIDATES : 1;

		fCartesianAlgorithm = new CartesianProductAlgorithm<>();
		fHistory = new ArrayList<>();
	}

	@Override
	public void initialize(List<List<E>> input,
						   IConstraintEvaluator<E> constraintEvaluator,
						   IEcfProgressMonitor generatorProgressMonitor) {

		if (input.size() == 0) {
			GeneratorExceptionHelper.reportException("The method contains no parameters!");
		} 

		// To avoid duplicates, we need to add all history results as 'exclusion constraints'.
		if (!fDuplicates) {
			fHistory.forEach(e -> getConstraintEvaluator().excludeAssignment(e));
		}

		fCartesianAlgorithm.initialize(input, constraintEvaluator, generatorProgressMonitor);

		super.initialize(input, constraintEvaluator, generatorProgressMonitor);

		setTaskBegin(fLength);
	}

	@Override
	public List<E> getNext() {

		if (fHistory.size() >= fLength) {
			return null;
		}

		// To avoid duplicates, we need to add the most recent result to the list of 'exclusion constraints'.
		if (!fHistory.isEmpty() && !fDuplicates) {
			getConstraintEvaluator().excludeAssignment(fHistory.get(fHistory.size() - 1));
		}

		List<List<E>> candidates = getCandidates();
		Optional<List<E>> optimalCandidate = getOptimalCandidate(candidates);

		if (!optimalCandidate.isPresent()) {
			return null;
		}

		fHistory.add(optimalCandidate.get());

		incrementProgress(1);

		return optimalCandidate.get();
	}

	@Override
	public void reset() {
		super.reset();

		fHistory.clear();
	}

	public int getLength() {

		return fLength;
	}

	public boolean getDuplicates() {

		return fDuplicates;
	}

	protected List<List<E>> getCandidates() {
		Set<List<E>> candidates = new HashSet<>();
		int idleCounter = 0;

		while (candidates.size() < fCandidatesSize && idleCounter < IDLE_COUNTER) {
			List<E> candidate = getCandidate();

			if (candidate == null) {
				break;
			}

			boolean duplicate = candidates.add(candidate);

			idleCounter = duplicate ? 0 : idleCounter + 1;
		}

		return new ArrayList<>(candidates);
	}

	protected List<E> getCandidate() {
		List<Integer> random = randomVectorAbstract(getInput());
		List<Integer> result = fCartesianAlgorithm.getNext(random);

		if (result == null) {
			result = fCartesianAlgorithm.getNext(null);
		}

		return instance(result);
	}

	private Optional<List<E>> getOptimalCandidate(List<List<E>> candidates) {

		if (candidates == null || candidates.size() == 0) {
			return Optional.empty();
		}

		if (candidates.size() == 1) {
			return getOptimalCandidateSimple(candidates);
		}

		return getOptimalCandidateAdaptive(candidates);

	}

	private Optional<List<E>> getOptimalCandidateSimple(List<List<E>> candidates) {

		return Optional.ofNullable(candidates.get(0));
	}

	private Optional<List<E>> getOptimalCandidateAdaptive(List<List<E>> candidates) {

		List<E> optimalCandidate = null;
		int optimalCandidateDistance = 0;

		for (List<E> candidate : candidates) {
			int candidateMinDistance = distanceAgainstHistory(candidate);

			if (candidateMinDistance > optimalCandidateDistance) {
				optimalCandidate = candidate;
				optimalCandidateDistance = candidateMinDistance;
			}
		}

		return Optional.ofNullable(optimalCandidate);
	}

	private int distanceAgainstHistory(List<E> vector) {
		int distance = Integer.MAX_VALUE;

		for (List<E> event : fHistory) {
			distance = Math.min(distance(vector, event), distance);
		}

		return distance;
	}

	private int distance(List<E> vector1, List<E> vector2) {

		if (vector1.size() != vector2.size()) {
			return Integer.MAX_VALUE;
		}

		int distance = 0;

		for (int i = 0 ; i < vector1.size() ; i++) {
			if (!vector1.get(i).equals(vector2.get(i))) {
				++distance;
			}
		}

		return distance;
	}

	private List<Integer> randomVectorAbstract(List<? extends List<E>> input) {

		return input.stream()
				.map(List::size)							// Get the number of choices for each parameter.
				.map(ThreadLocalRandom.current()::nextInt)	// Select a choice at random.
				.collect(Collectors.toList());
	}
}
