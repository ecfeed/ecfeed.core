package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;

public class GeneratorParameterCoverage extends GeneratorParameterInteger {

    private final static String COVERAGE_PARAMETER_NAME = "Coverage";

    GeneratorParameterCoverage() throws GeneratorException {

        super(COVERAGE_PARAMETER_NAME, false, 100, 1, 100);
    }
}
