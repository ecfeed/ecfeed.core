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

import com.ecfeed.core.generators.algorithms.NWiseAwesomeAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class NWiseGenerator<E> extends NWiseGeneratorBase<E> {

    public NWiseGenerator() throws GeneratorException {
    	super();
    }

    @Override
    public void initialize(List<List<E>> inputDomain,
                           IConstraintEvaluator<E> constraintEvaluator,
                           List<IGeneratorValue> parameters,
                           IEcfProgressMonitor generatorProgressMonitor) throws GeneratorException {

        super.initialize(inputDomain, constraintEvaluator, parameters, generatorProgressMonitor);
        int N = (int) getParameterValue(getDefinitionN());
        int coverage = (int) getParameterValue(getDefinitionCoverage());
        
        setAlgorithm(new NWiseAwesomeAlgorithm<>(N, coverage));
    }

}
