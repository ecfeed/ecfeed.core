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

import java.util.List;

import com.ecfeed.core.generators.algorithms.RandomAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.generators.api.IParameterDefinition;
import com.ecfeed.core.utils.GeneratorType;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class RandomGenerator<E> extends AbstractGenerator<E> implements
		IGenerator<E> {

	public final static String ADAPTIVE_PARAMETER_NAME = "adaptive";
	public static final String DUPLICATES_PARAMETER_NAME = "duplicates";
	public static final String LENGTH_PARAMETER_NAME = "length";
	public static final int DEFAULT_TEST_SUITE_SIZE_PARAMETER_VALUE = 100;

	private static IParameterDefinition fDefinitionAdaptive;
	private static IParameterDefinition fDefinitionDuplicates;
	private static IParameterDefinition fDefinitionLength;

	public RandomGenerator() throws GeneratorException{

		if(fDefinitionAdaptive==null)
			fDefinitionAdaptive = new ParameterDefinitionBoolean(ADAPTIVE_PARAMETER_NAME, false);
		if(fDefinitionDuplicates==null)
			fDefinitionDuplicates = new ParameterDefinitionBoolean(DUPLICATES_PARAMETER_NAME, false);
		if(fDefinitionLength==null)
			fDefinitionLength = new ParameterDefinitionInteger(LENGTH_PARAMETER_NAME, DEFAULT_TEST_SUITE_SIZE_PARAMETER_VALUE, 0, Integer.MAX_VALUE);
		addParameterDefinition( fDefinitionDuplicates);
		addParameterDefinition(fDefinitionAdaptive);
		addParameterDefinition(fDefinitionLength);
	}

	public static IParameterDefinition getDefinitionAdaptive()
	{
		return fDefinitionAdaptive;
	}

	public static IParameterDefinition getDefinitionDuplicates()
	{
		return fDefinitionDuplicates;
	}

	public static IParameterDefinition getDefinitionLength()
	{
		return fDefinitionLength;
	}

	@Override
	public void initialize(List<List<E>> inputDomain,
						   IConstraintEvaluator<E> constraintEvaluator,
			List<IGeneratorValue> parameters,
			IEcfProgressMonitor generatorProgressMonitor) throws GeneratorException{

		super.initialize(inputDomain, constraintEvaluator, parameters, generatorProgressMonitor);
		int length = (int)getParameterValue(getDefinitionLength());
		boolean duplicates = (boolean)getParameterValue(getDefinitionDuplicates());
		boolean adaptive = (boolean)getParameterValue(getDefinitionAdaptive());
		setAlgorithm(new RandomAlgorithm<E>(length, duplicates, adaptive));
	}

	@Override
	public GeneratorType getGeneratorType() {
		
		return GeneratorType.RANDOM;
	}
}
