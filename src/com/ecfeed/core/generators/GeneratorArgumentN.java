package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;


public class GeneratorArgumentN  extends AbstractGeneratorArgument {

    Integer fN;

    public GeneratorArgumentN(int n) throws GeneratorException {

        super(new GeneratorParameterN().getName());
        fN = n;
    }
            
    @Override
    public Integer getValue() {
        return fN;
    }
}
