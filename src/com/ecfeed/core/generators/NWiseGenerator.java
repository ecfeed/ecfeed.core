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
import java.util.Map;

import com.ecfeed.core.generators.algorithms.AwesomeNWiseAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.generators.api.IParameterDefinition;
import com.ecfeed.core.utils.GeneratorType;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class NWiseGenerator<E> extends AbstractGenerator<E> {

    public final static String COVERAGE_PARAMETER_NAME = "coverage";
    public final static String N_PARAMETER_NAME = "n";

    private static IParameterDefinition fDefinitionN;
    private static IParameterDefinition fDefinitionCoverage;

    public NWiseGenerator() throws GeneratorException {

        if (fDefinitionN == null)
            fDefinitionN = new ParameterDefinitionInteger(N_PARAMETER_NAME, 2, 1, Integer.MAX_VALUE);

        addParameterDefinition(fDefinitionN);

        if (fDefinitionCoverage == null)
            fDefinitionCoverage = new ParameterDefinitionInteger(COVERAGE_PARAMETER_NAME, 100, 1, 100);

        addParameterDefinition(fDefinitionCoverage);
    }

    public static IParameterDefinition getDefinitionN() {
        return fDefinitionN;
    }

    public static IParameterDefinition getDefinitionCoverage() {
        return fDefinitionCoverage;
    }

    @Override
    public void initialize(List<List<E>> inputDomain,
                           IConstraintEvaluator<E> constraintEvaluator,
                           List<IGeneratorValue> parameters,
                           IEcfProgressMonitor generatorProgressMonitor) throws GeneratorException {

        super.initialize(inputDomain, constraintEvaluator, parameters, generatorProgressMonitor);
        int N = (int) getParameterValue(getDefinitionN());
        int coverage = (int) getParameterValue(getDefinitionCoverage());
        setAlgorithm(new AwesomeNWiseAlgorithm<>(N, coverage));
    }

    @Override
    public GeneratorType getGeneratorType() {

        return GeneratorType.N_WISE;
    }
}
