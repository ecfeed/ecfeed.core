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

import com.ecfeed.core.generators.algorithms.RandomAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.generators.api.IGeneratorProgressMonitor;
import com.ecfeed.core.model.IConstraint;

public class RandomGenerator<E> extends AbstractGenerator<E> implements
		IGenerator<E> {

	// TODO - make generator parameter strings as private + methods set and get, exchange Object to IGeneratorParameter in parameters map
	public static final String LENGTH_PARAMETER_NAME = "Length";
	public static final int DEFAULT_LENGTH = 1;
	public static final String DUPLICATES_PARAMETER_NAME = "Duplicates";
	public static final boolean DEFAULT_DUPLICATES = false;
	
	public RandomGenerator() throws GeneratorException{
		addParameterDefinition(new IntegerParameter(LENGTH_PARAMETER_NAME, true, DEFAULT_LENGTH, 0, Integer.MAX_VALUE));
		addParameterDefinition(new BooleanParameter(DUPLICATES_PARAMETER_NAME, false, DEFAULT_DUPLICATES));
	}
	
	@Override
	public void initialize(List<List<E>> inputDomain,
			Collection<IConstraint<E>> constraints,
			Map<String, Object> parameters,
			IGeneratorProgressMonitor generatorProgressMonitor) throws GeneratorException{

		super.initialize(inputDomain, constraints, parameters, generatorProgressMonitor);
		int length = getIntParameter(LENGTH_PARAMETER_NAME);
		boolean duplicates = getBooleanParameter(DUPLICATES_PARAMETER_NAME);
		setAlgorithm(new RandomAlgorithm<E>(length, duplicates));
	}
}
