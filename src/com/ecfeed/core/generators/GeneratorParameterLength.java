package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;

public class GeneratorParameterLength extends GeneratorParameterInteger {

    public static final String TEST_SUITE_SIZE_PARAMETER_NAME = "Length";
    public static final int DEFAULT_TEST_SUITE_SIZE_PARAMETER_VALUE = 1;

    GeneratorParameterLength() throws GeneratorException {

        super(TEST_SUITE_SIZE_PARAMETER_NAME,
              true, DEFAULT_TEST_SUITE_SIZE_PARAMETER_VALUE, 0, Integer.MAX_VALUE);
    }
}
