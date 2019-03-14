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
import com.ecfeed.core.generators.api.IGeneratorArgument;
import com.ecfeed.core.generators.api.IGeneratorProgressMonitor;
import com.ecfeed.core.model.IConstraint;

public class NWiseGenerator<E> extends AbstractGenerator<E>{

	public NWiseGenerator() throws GeneratorException{
		addParameterDefinition(new GeneratorParameterN());
		addParameterDefinition(new GeneratorParameterCoverage());
	}
	
	@Override
	public void initialize(List<List<E>> inputDomain,
			Collection<IConstraint<E>> constraints,
			Map<String, IGeneratorArgument> parameters,
			IGeneratorProgressMonitor generatorProgressMonitor) throws GeneratorException{
		super.initialize(inputDomain, constraints, parameters, generatorProgressMonitor);
		int N = getIntParameter(new GeneratorParameterN().getName());
		int coverage = getIntParameter(new GeneratorParameterCoverage().getName());
//		setAlgorithm(new OptimalNWiseAlgorithm<E>(N, coverage));
		setAlgorithm(new RandomizedNWiseAlgorithm<E>(N, coverage));
	}
}
