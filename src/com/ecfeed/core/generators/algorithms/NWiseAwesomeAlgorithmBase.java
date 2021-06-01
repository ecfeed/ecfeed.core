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

import java.util.List;
import java.util.Set;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.utils.IEcfProgressMonitor;
import com.ecfeed.core.utils.SimpleProgressMonitor;

public abstract class NWiseAwesomeAlgorithmBase<E> extends AbstractAlgorithm<E> {

	private CartesianProductAlgorithm<E> fCartesianAlgorithm;
	protected int N;
	private int fTuplesToGenerate;
	protected int fCoverage;

	public NWiseAwesomeAlgorithmBase(int n, int coverage) {
		fCoverage = coverage;
		N = n;
	}

	public void initialize(List<List<E>> input,
						   IConstraintEvaluator<E> constraintEvaluator,
			IEcfProgressMonitor generatorProgressMonitor) throws GeneratorException {

		if(N < 1 || N > input.size()){
			GeneratorException.report("Value of N for this input must be between 1 and " + input.size());
		}
		if (fCoverage > 100 || fCoverage < 0) {
			GeneratorException.report("Coverage must be between 1 and 100");
		}

		fCartesianAlgorithm = new CartesianProductAlgorithm<E>();
		SimpleProgressMonitor notUsedProgressMonitor = new SimpleProgressMonitor();
		fCartesianAlgorithm.initialize(input, constraintEvaluator, notUsedProgressMonitor);

		super.initialize(input, constraintEvaluator, generatorProgressMonitor);
		generatorProgressMonitor.setTotalProgress((int)tuplesToGenerate());
	}

	@Override
	public void reset(){
		fCartesianAlgorithm.reset();
		fTuplesToGenerate = calculateTotalTuples();
		setTaskBegin(fTuplesToGenerate);
		super.reset();
	}

	public void reset(int value){
		fCartesianAlgorithm.reset();
		fTuplesToGenerate = value;
		setTaskBegin(fTuplesToGenerate);
		super.reset();
	}

	@Override
	public void cancel() {
		fCartesianAlgorithm.cancel();
	}

	public int getN(){
		return N;
	}

	public int getCoverage() {
		return fCoverage;
	}

	protected List<E> cartesianNext() throws GeneratorException{
		return fCartesianAlgorithm.getNext();
	}

	protected int maxTuples(List<List<E>> input, int n){
		return (new Tuples<List<E>>(input, n)).getAll().size();
	}

	protected Set<List<E>> getTuples(List<E> vector){
		return (new Tuples<E>(vector, N)).getAll();
	}

	protected long tuplesToGenerate() {
		return fTuplesToGenerate;
	}

	protected void cartesianReset(){
		fCartesianAlgorithm.reset();
	}

	private int calculateTotalTuples(){
		int totalWork = 0;
		Tuples<List<E>> tuples = new Tuples<List<E>>(getInput(), N);
		while(tuples.hasNext()){
			long combinations = 1;
			List<List<E>> tuple = tuples.next();
			for(List<E> parameter : tuple){
				combinations *= parameter.size();
			}
			totalWork += combinations;
		}
		return (int) Math.ceil(((double) (fCoverage * totalWork)) / 100);
	}

}
