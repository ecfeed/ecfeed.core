package com.ecfeed.core.generators;

public class GeneratorParameterN extends GeneratorParameterInteger {

    public final static String N_PARAMETER_NAME = "N";

    public GeneratorParameterN() {

        super(N_PARAMETER_NAME, true, 2, 1, Integer.MAX_VALUE);
    }
}
