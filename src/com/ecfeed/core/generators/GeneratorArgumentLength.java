package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;

public class GeneratorArgumentLength extends GeneratorArgumentInteger {

    public GeneratorArgumentLength(int length) throws GeneratorException {
        super(new GeneratorParameterLength().getName(), length);
    }
            
}
