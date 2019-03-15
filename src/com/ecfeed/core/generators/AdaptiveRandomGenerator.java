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

import com.ecfeed.core.generators.algorithms.AdaptiveRandomAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGeneratorArgument;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class AdaptiveRandomGenerator<E> extends AbstractGenerator<E> {

	public AdaptiveRandomGenerator() throws GeneratorException{

		addParameterDefinition(new GeneratorParameterDepth());
		addParameterDefinition(new GeneratorParameterCandidateSetSize());
		addParameterDefinition(new GeneratorParameterLength());
		addParameterDefinition(new GeneratorParameterDuplicates());
	}
	
	@Override
	public void initialize(List<List<E>> inputDomain,
			Collection<IConstraint<E>> constraints,
			Map<String, IGeneratorArgument> parameters,
			IEcfProgressMonitor generatorProgressMonitor) throws GeneratorException{
		
		super.initialize(inputDomain, constraints, parameters, generatorProgressMonitor);
		int executedSetSize = getIntParameter(new GeneratorParameterDepth().getName());
		int candidateSetSize = getIntParameter(new GeneratorParameterCandidateSetSize().getName());
		int testSuiteSize = getIntParameter(new GeneratorParameterLength().getName());
		boolean duplicates = getBooleanParameter(new GeneratorParameterDuplicates().getName());

		setAlgorithm(new AdaptiveRandomAlgorithm<E>(executedSetSize, 
				candidateSetSize, testSuiteSize, duplicates));
	}
}
