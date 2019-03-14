package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;


public class GeneratorArgumentN  extends GeneratorArgumentInteger {

    public GeneratorArgumentN(int n) throws GeneratorException {
        super(new GeneratorParameterN().getName(), n);
    }
            
}
