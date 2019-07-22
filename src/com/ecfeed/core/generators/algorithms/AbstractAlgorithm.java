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
import java.util.List;
import java.util.Optional;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public abstract class AbstractAlgorithm<E> implements IAlgorithm<E> {

	private IEcfProgressMonitor fGeneratorProgressMonitor;

	private List<List<E>> fInput;
	private IConstraintEvaluator<E> fConstraintEvaluator;

	@Override
	public void initialize(List<List<E>> input,
						   IConstraintEvaluator<E> constraintEvaluator,
			IEcfProgressMonitor generatorProgressMonitor) throws GeneratorException {


		if(input == null || constraintEvaluator == null){
			GeneratorException.report("input or constraints of algorithm cannot be null");
		}

		if (generatorProgressMonitor == null) {
			GeneratorException.report("Progress monitor of algorithm must not be null.");
		}

		fInput = input;
		fConstraintEvaluator = constraintEvaluator;
		fGeneratorProgressMonitor = generatorProgressMonitor;
		reset();
	}

	public IEcfProgressMonitor getGeneratorProgressMonitor() {
		return fGeneratorProgressMonitor;
	}

	@Override
	public void setTaskBegin(int totalWork) {

		fGeneratorProgressMonitor.setTaskBegin("Generator", totalWork);
	}

	@Override
	public void setTaskEnd() {

		fGeneratorProgressMonitor.setTaskEnd();
	}

	@Override
	public void reset(){

		fGeneratorProgressMonitor.reset();
	}

	@Override
	public void incrementProgress(int progressIncrement) {

		fGeneratorProgressMonitor.incrementProgress(progressIncrement);
	}

	@Override
	public void cancel() {
		fGeneratorProgressMonitor.setCanceled();
	}

//	@Override
//	public void addConstraint(IConstraint<E> constraint) {
//		fConstraints.add(constraint);
//	}

//	@Override
//	public void removeConstraint(IConstraint<E> constraint) {
//		fConstraints.remove(constraint);
//	}

	@Override
	public IConstraintEvaluator<E> getConstraintEvaluator() {
		return fConstraintEvaluator;
	}

	protected int getCurrentProgress() {

		return fGeneratorProgressMonitor.getCurrentProgress();
	}

	public List<List<E>> getInput(){
		return fInput;
	}

	protected List<E> instance(List<Integer> vector) {
		if (vector == null) return null;
		List<E> instance = new ArrayList<E>();
		for(int i = 0; i < vector.size(); i++){
			E element = fInput.get(i).get(vector.get(i));
			instance.add(element);
		}
		return instance;
	}

	protected List<Integer> representation(List<E> vector){
		if(vector == null) return null;
		List<Integer> representation = new ArrayList<Integer>();
		for(int i = 0; i < vector.size(); i++){
			E element = vector.get(i);
			int index = fInput.get(i).indexOf(element);
			if(index < 0){
				index = 0;
			}
			representation.add(index);
		}
		return representation;
	}

	protected EvaluationResult checkConstraints(List<E> test) {
		return fConstraintEvaluator.evaluate(test);
	}

//	protected boolean isDimensionMentionedInConstraints(int dimension) {
//
//		for (IConstraint<E> constraint : fConstraints) {
//			if (constraint.mentions(dimension)) {
//				return true;
//			}
//		}
//
//		return false;
//	}

	@Override
	public boolean isCancelled() {

		if (fGeneratorProgressMonitor.isCanceled()) {
			return true;
		}

		return false;
	}
}
