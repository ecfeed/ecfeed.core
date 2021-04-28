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

import com.ecfeed.core.generators.algorithms.AwesomeNWiseAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraintEvaluator;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.generators.api.IParameterDefinition;
import com.ecfeed.core.utils.GeneratorType;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class NWiseGenerator<E> extends AbstractGenerator<E> {

    private static List<IParameterDefinition> fParameterDefinitions = null;

    public final static String PARAMETER_NAME_COVERAGE = "coverage";
    public final static String PARAMETER_NAME_N = "n";

    public NWiseGenerator() throws GeneratorException {

        if(fParameterDefinitions==null) {
            fParameterDefinitions = new ArrayList<>();
            addParameterDefinition(
                    new ParameterDefinitionInteger(
                            PARAMETER_NAME_N, 2, 1, Integer.MAX_VALUE));

            addParameterDefinition(
                    new ParameterDefinitionInteger(
                            PARAMETER_NAME_COVERAGE, 100, 1, 100));
        }
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

    public IParameterDefinition getDefinitionN() throws GeneratorException {

        return getParameterDefinition(PARAMETER_NAME_N);
    }

    public IParameterDefinition getDefinitionCoverage() throws GeneratorException {

        return getParameterDefinition(PARAMETER_NAME_COVERAGE);
    }

    public List<IParameterDefinition> getParameterDefinitions() {
        return fParameterDefinitions;
    }

}
