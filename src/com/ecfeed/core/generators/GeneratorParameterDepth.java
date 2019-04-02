package com.ecfeed.core.generators;

public class GeneratorParameterDepth extends GeneratorParameterInteger {

    private static final String HISTORY_DEPTH_PARAMETER_NAME = "Depth";
    private static final int DEFAULT_HISTORY_DEPTH_VALUE = -1;

    public GeneratorParameterDepth() {

        super(HISTORY_DEPTH_PARAMETER_NAME,false, DEFAULT_HISTORY_DEPTH_VALUE,-1, Integer.MAX_VALUE);
    }
}
