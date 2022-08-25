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
import java.util.Collections;
import java.util.List;

import com.ecfeed.core.generators.api.GeneratorExceptionHelper;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class CartesianProductAlgorithm<E> extends AbstractAlgorithm<E>{

	private boolean fInitialized;
	protected List<Integer> fLastGenerated;

	@Override
	public void initialize(
			List<List<E>> input,
			IConstraintEvaluator<E> constraintEvaluator,
			IEcfProgressMonitor generatorProgressMonitor) {
		
		if (input.size() == 0) {
			GeneratorExceptionHelper.reportException("The method contains no parameters.");
		} 
		
		super.initialize(input, constraintEvaluator, generatorProgressMonitor);
			int totalProgress = calculateProductSize(input);

		setTaskBegin(totalProgress);
		fInitialized = true;
	}

	@Override
	public List<E> getNext() {

		if (!fInitialized) {
			GeneratorExceptionHelper.reportException("Generator not initialized");
		}

		if (isCancelled()) {
			return null;
		}

		List<Integer> next = getNext(fLastGenerated);
		fLastGenerated = next;

		if (next == null) {
			setTaskEnd();
			return null;
		}

		incrementProgress(1);
		return instance(next);
	}

	public void reset(){
		fLastGenerated = null;
		super.reset();
	}

	protected List<Integer> getNext(List<Integer> last) {
		List<Integer> nextElement;
		if(last!=null)
			nextElement = new ArrayList<>(last);
		else
			nextElement = null;
		while((nextElement = incrementVector(nextElement)) != null){
			if (checkConstraints( instance(nextElement)) == EvaluationResult.TRUE) {
				return nextElement;
			}
		}
		return null;
	}

	protected List<Integer> incrementVector(List<Integer> vector) {
		if(vector == null){
			return fillNull(new ArrayList<>(Collections.nCopies(getInput().size(), null)));
		}
		for(int i = vector.size() - 1; i >= 0; i--){
			int k = vector.get(i)+1;
			for(;;k++)
			{
				if(k==getInput().get(i).size())
				{
					vector.set(i, null);
					break;
				}
				vector.set(i,k);

				if( checkConstraints( instance(vector)) == EvaluationResult.TRUE)
					return fillNull( vector );
			}
		}
		return null;
	}

	protected List<Integer> fillNull(List<Integer> vector) {
		if( checkConstraints( instance(vector)) == EvaluationResult.FALSE )
			return null;
		for(int i = 0; i < getInput().size(); i++)
			if(vector.get(i)==null)
			{
				for(int k = 0; ;k++)
				{
					if(k==getInput().get(i).size())
						return null;
					vector.set(i,k);
					if( checkConstraints( instance(vector)) == EvaluationResult.TRUE )
						break;
				}
			}
		return vector;
	}

	protected int calculateProductSize(List<? extends List<E>> input) {
		int result = 1;
		for(List<E> vector : input){
			result *= vector.size();
		}
		return result;
	}
}
