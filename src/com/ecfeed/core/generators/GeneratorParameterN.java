package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;

public class GeneratorParameterN extends GeneratorParameterInteger {

    public final static String N_PARAMETER_NAME = "N";

    GeneratorParameterN() throws GeneratorException {

        super(N_PARAMETER_NAME, true, 2, 1, Integer.MAX_VALUE);
    }
}
