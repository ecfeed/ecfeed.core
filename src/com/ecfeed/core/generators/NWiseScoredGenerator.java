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

import com.ecfeed.core.generators.algorithms.IScoreEvaluator;
import com.ecfeed.core.generators.algorithms.NwiseScoreEvaluator;
import com.ecfeed.core.generators.algorithms.ScoreBasedNwiseAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class NWiseScoredGenerator<E> extends NWiseGeneratorBase<E> {

	public NWiseScoredGenerator() throws GeneratorException {
		super();
	}

	@Override
	public void initialize(List<List<E>> inputDomain,
			IConstraintEvaluator<E> constraintEvaluator,
			List<IGeneratorValue> generatorParameters,
			IEcfProgressMonitor generatorProgressMonitor) throws GeneratorException {

		super.initialize(inputDomain, constraintEvaluator, generatorParameters, generatorProgressMonitor);
		int N = (int) getParameterValue(getDefinitionN());
		int coverage = (int) getParameterValue(getDefinitionCoverage());

		IScoreEvaluator<E> fScoreEvaluator = new NwiseScoreEvaluator<>(N);
		setAlgorithm(new ScoreBasedNwiseAlgorithm<E>(fScoreEvaluator, coverage));
	}

}
