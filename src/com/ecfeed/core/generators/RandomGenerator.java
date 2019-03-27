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
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.generators.api.IGeneratorArgument;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.utils.GeneratorType;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class RandomGenerator<E> extends AbstractGenerator<E> implements
		IGenerator<E> {

	public RandomGenerator() throws GeneratorException{

		addParameterDefinition(new GeneratorParameterLength());
		addParameterDefinition(new GeneratorParameterDuplicates());
	}
	
	@Override
	public void initialize(List<List<E>> inputDomain,
						   IConstraintEvaluator<E> constraintEvaluator,
			Map<String, IGeneratorArgument> parameters,
			IEcfProgressMonitor generatorProgressMonitor) throws GeneratorException{

		super.initialize(inputDomain, constraintEvaluator, parameters, generatorProgressMonitor);
		int length = getIntParameter(new GeneratorParameterLength().getName());
		boolean duplicates = getBooleanParameter(new GeneratorParameterDuplicates().getName());
		setAlgorithm(new RandomAlgorithm<E>(length, duplicates));
	}

	@Override
	public GeneratorType getGeneratorType() {
		
		return GeneratorType.RANDOM;
	}
}
