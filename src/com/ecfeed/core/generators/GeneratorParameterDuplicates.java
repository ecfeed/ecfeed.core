package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;

public class GeneratorParameterDuplicates extends GeneratorParameterBoolean {

    private static final String DUPLICATES_PARAMETER_NAME = "Duplicates";

    GeneratorParameterDuplicates() throws GeneratorException {

        super(DUPLICATES_PARAMETER_NAME,false, false);
    }
}
