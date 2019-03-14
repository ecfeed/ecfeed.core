package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;

public class GeneratorArgumentCoverage extends GeneratorArgumentInteger {

    public GeneratorArgumentCoverage(int coverage) throws GeneratorException {
        super(new GeneratorParameterCoverage().getName(), coverage);
    }
            
}
