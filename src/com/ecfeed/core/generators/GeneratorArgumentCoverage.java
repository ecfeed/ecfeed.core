package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;

public class GeneratorArgumentCoverage extends AbstractGeneratorArgument {

    Integer fCoverage;

    public GeneratorArgumentCoverage(int coverage) throws GeneratorException {
        super(new GeneratorParameterCoverage().getName());
        fCoverage = coverage;
    }
            
    @Override
    public Integer getValue() {
        return fCoverage;
    }
}
