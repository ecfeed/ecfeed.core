/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators.api;

import java.util.List;

import com.ecfeed.core.utils.GeneratorType;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public interface IGenerator<E> {
	/*
	 * returns list of parameters used by this generator.
	 */
	public List<IParameterDefinition> getParameterDefinitions();
	/*
	 * Should be called prior to first call of next()
	 */
	public void initialize(List<List<E>> inputDomain,
						   IConstraintEvaluator<E> constraintEvaluator,
		    List<IGeneratorValue> arguments,
			IEcfProgressMonitor generatorProgressMonitor) throws GeneratorException;
	
//	public void addConstraint(IConstraint<E> constraint);
//
//	public void removeConstraint(IConstraint<E> constraint);
	
	public IConstraintEvaluator<E> getConstraintEvaluator();
	
	/*
	 * Returns null if no more data can be generated, e.g.if the test generation should end 
	 * all data according to the used algorithm or provided parameter has been generated. 
	 * Blocking method.
	 */
	public List<E> next() throws GeneratorException;
	
	/*
	 * Resets generator to its initial state.
	 */
	public void reset();

	public void cancel();
	
	public GeneratorType getGeneratorType();
	
}
