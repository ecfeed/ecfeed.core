package com.ecfeed.core.generators;

public class GeneratorParameterCoverage extends GeneratorParameterInteger {

    private final static String COVERAGE_PARAMETER_NAME = "Coverage";

    public GeneratorParameterCoverage() {

        super(COVERAGE_PARAMETER_NAME, false, 100, 1, 100);
    }
}
