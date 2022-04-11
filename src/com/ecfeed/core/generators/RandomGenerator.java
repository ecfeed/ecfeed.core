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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.generators.algorithms.RandomAlgorithm;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.generators.api.IParameterDefinition;
import com.ecfeed.core.utils.GeneratorType;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class RandomGenerator<E> extends AbstractGenerator<E> implements
		IGenerator<E> {

	private static List<IParameterDefinition> fParameterDefinitions = null;

	public final static String PARAMETER_NAME_ADAPTIVE = "adaptive";
	public static final String PARAMETER_NAME_DUPLICATES = "duplicates";
	public static final String PARAMETER_NAME_LENGTH = "length";

	public static final int DEFAULT_TEST_SUITE_SIZE_PARAMETER_VALUE = 100;


	public RandomGenerator() {

		if(fParameterDefinitions==null) {
			fParameterDefinitions = new ArrayList<>();

			addParameterDefinition(
					new ParameterDefinitionBoolean(
							PARAMETER_NAME_ADAPTIVE, false));

			addParameterDefinition(
					new ParameterDefinitionBoolean(
							PARAMETER_NAME_DUPLICATES, false));

			addParameterDefinition(
					new ParameterDefinitionInteger(
							PARAMETER_NAME_LENGTH, DEFAULT_TEST_SUITE_SIZE_PARAMETER_VALUE,
							0, Integer.MAX_VALUE));
		}
	}

	@Override
	public void initialize(List<List<E>> inputDomain,
						   IConstraintEvaluator<E> constraintEvaluator,
			List<IGeneratorValue> parameters,
			IEcfProgressMonitor generatorProgressMonitor) {

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

	public IParameterDefinition getDefinitionAdaptive() {
		return getParameterDefinition(PARAMETER_NAME_ADAPTIVE);
	}

	public IParameterDefinition getDefinitionDuplicates() {
		return getParameterDefinition(PARAMETER_NAME_DUPLICATES);
	}

	public IParameterDefinition getDefinitionLength() {
		return getParameterDefinition(PARAMETER_NAME_LENGTH);
	}

	public List<IParameterDefinition> getParameterDefinitions() {
		return fParameterDefinitions;
	}

}
