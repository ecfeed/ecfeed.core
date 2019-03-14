package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;

public class GeneratorParameterDepth extends GeneratorParameterInteger {

    private static final String HISTORY_DEPTH_PARAMETER_NAME = "Depth";
    private static final int DEFAULT_HISTORY_DEPTH_VALUE = -1;

    GeneratorParameterDepth() throws GeneratorException {

        super(HISTORY_DEPTH_PARAMETER_NAME,false, DEFAULT_HISTORY_DEPTH_VALUE,-1, Integer.MAX_VALUE);
    }
}
