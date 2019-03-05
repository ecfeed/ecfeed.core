/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.generators.algorithms.RandomizedNWiseAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGeneratorProgressMonitor;
import com.ecfeed.core.model.IConstraint;

public class NWiseGenerator<E> extends AbstractGenerator<E>{
	public final static String N_PARAMETER_NAME = "N";
	public final static String N_PARAMETER_DESCRIPTION = "N";
	public final static String COVERAGE_PARAMETER_NAME = "Coverage";
	public final static String COVERAGE_PARAMETER_DESCRIPTION = "N-wise coverage (%)";	

	public NWiseGenerator() throws GeneratorException{
		addParameterDefinition(new IntegerParameter(N_PARAMETER_NAME, true, 2, 1, Integer.MAX_VALUE));
		addParameterDefinition(new IntegerParameter(COVERAGE_PARAMETER_NAME, false, 100, 1, 100));
	}
	
	@Override
	public void initialize(List<List<E>> inputDomain,
			Collection<IConstraint<E>> constraints,
			Map<String, Object> parameters,
			IGeneratorProgressMonitor generatorProgressMonitor) throws GeneratorException{
		super.initialize(inputDomain, constraints, parameters, generatorProgressMonitor);
		int N = getIntParameter(N_PARAMETER_NAME);
		int coverage = getIntParameter(COVERAGE_PARAMETER_NAME);
//		setAlgorithm(new OptimalNWiseAlgorithm<E>(N, coverage));
		setAlgorithm(new RandomizedNWiseAlgorithm<E>(N, coverage));
	}
}
