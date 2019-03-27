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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public interface IGenerator<E> {
	/*
	 * returns list of parameters used by this generator.
	 */
	public List<IGeneratorParamDefinition> getParameterDefinitions();
	/*
	 * Should be called prior to first call of next()
	 */
	public void initialize(List<List<E>> inputDomain, 
			Collection<IConstraint<E>> constraints,
		    Map<String, IGeneratorArgument> arguments,
			IEcfProgressMonitor generatorProgressMonitor) throws GeneratorException;
	
	public void addConstraint(IConstraint<E> constraint);

	public void removeConstraint(IConstraint<E> constraint);
	
	public Collection<? extends IConstraint<E>> getConstraints();
	
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

	// TODO - remove obsolete methods

	// Obsolete - totalWorkShould be taken from progress monitor
	public int totalWork();

	// Obsolete - totalWorkShould be taken from progress monitor
	public int workProgress();

	// Obsolete - totalWorkShould be taken from progress monitor
	public int totalProgress();
	
	public void cancel();
	
}
