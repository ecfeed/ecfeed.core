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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ecfeed.core.generators.api.GeneratorExceptionHelper;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class RandomAlgorithm<E> extends AbstractAlgorithm<E> implements IAlgorithm<E> {

	static final int NUMBER_OF_CANDIDATES = 100;
	private final int fCandidatesSize;
	private final int fLength;
	private final boolean fDuplicates;
	
	private CartesianProductAlgorithm<E> fCartesianAlgorithm;

	private List<List<E>> fHistory;


	public RandomAlgorithm(int length, boolean duplicates, boolean adaptive) {
		if(adaptive) {
			fCandidatesSize = NUMBER_OF_CANDIDATES;
		} else {
			fCandidatesSize = 1;
		}
		fLength = length;
		fDuplicates = duplicates;

		fHistory = new ArrayList<List<E>>();
		fCartesianAlgorithm = new CartesianProductAlgorithm<E>();
	}

	@Override
	public void initialize(List<List<E>> input,
						   IConstraintEvaluator<E> constraintEvaluator,
			IEcfProgressMonitor generatorProgressMonitor) {

		if (input.size() == 0) {
			GeneratorExceptionHelper.reportException("The method contains no parameters.");
		} 
		
		if(fDuplicates == false){
			for(List<E> assignment : fHistory)
				getConstraintEvaluator().excludeAssignment(assignment);
		}

		fCartesianAlgorithm.initialize(input, constraintEvaluator, generatorProgressMonitor);
		super.initialize(input, constraintEvaluator, generatorProgressMonitor);

		setTaskBegin(fLength);
	}

	@Override
	public List<E> getNext() {
		if(fHistory.size() >= fLength){
			return null;
		}
		if(!fHistory.isEmpty() && fDuplicates == false)
			getConstraintEvaluator().excludeAssignment(fHistory.get(fHistory.size()-1));
		List<List<E>> candidates = getCandidates();
		List<E> optimalCandidate = getOptimalCandidate(candidates, fHistory);
		if(optimalCandidate == null && fDuplicates == false)
			return null;
		fHistory.add(optimalCandidate);
		incrementProgress(1);
		return optimalCandidate;
	}

	@Override
	public void reset(){
		fHistory.clear();
		super.reset();
	}

	public int getLength(){
		return fLength;
	}

	public boolean getDuplicates(){
		return fDuplicates;
	}

	public int getCandidatesSize(){
		return fCandidatesSize;
	}

	public List<List<E>> getHistory(){
		return fHistory;
	}

	protected List<List<E>> getCandidates() {
		Set<List<E>> candidates = new HashSet<List<E>>();

		int idleCounter = 0;
		int idleCounterMax = 100;

		while (candidates.size() < fCandidatesSize && idleCounter < idleCounterMax) {
			List<E> candidate = getCandidate();

			if (candidate == null) {
				break;
			}

			boolean conclusion = candidates.add(candidate);

			if (conclusion) {
				idleCounter = 0;
			} else {
				idleCounter++;
			}
		}

		return new ArrayList<List<E>>(candidates);
	}

	protected List<E> getCandidate() {
	//	fCartesianAlgorithm.addConstraint(blackList); //TODO: addConstraint goes away from IAlgorithm
		List<Integer> random = randomVector(getInput());
		List<Integer> result = fCartesianAlgorithm.getNext(random);
		if(result == null){
			result = fCartesianAlgorithm.getNext(null);
		};
	//	fCartesianAlgorithm.removeConstraint(blackList);  //TODO: removeConstraint goes away from IAlgorithm
		return instance(result);
	}

	protected List<E> getOptimalCandidate(List<List<E>> candidates, List<List<E>> history) {
		if(candidates.size() == 0) return null;
		if(candidates.size() == 1) return candidates.get(0);

		List<E> optimalCandidate = null;
		int optimalCandidateMinDistance = 0;
		for(List<E> candidate : candidates){
			int candidateMinDistance = Integer.MAX_VALUE;
			for(List<E> event : history){
				int distance = distance(candidate, event);
				candidateMinDistance = Math.min(distance, candidateMinDistance);
			}
			if(candidateMinDistance >= optimalCandidateMinDistance){
				optimalCandidate = candidate;
				optimalCandidateMinDistance = candidateMinDistance;
			}
		}
		return optimalCandidate;
	}

	protected int distance(List<E> vector1, List<E> vector2) {
		if(vector1.size() != vector2.size()){
			return Integer.MAX_VALUE;
		}
		int distance = 0;
		for(int i = 0; i < vector1.size(); i++){
			if(!vector1.get(i).equals(vector2.get(i))){
				++distance;
			}
		}
		return distance;
	}

	protected List<Integer> randomVector(List<? extends List<E>> input) {
		List<Integer> result = new ArrayList<Integer>();
		Random random = new Random();
		for(int i = 0; i < input.size(); i++){
			result.add(random.nextInt(input.get(i).size()));
		}
		return result;
	}
}
