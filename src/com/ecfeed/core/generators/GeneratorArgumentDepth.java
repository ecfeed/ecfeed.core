package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;

public class GeneratorArgumentDepth extends GeneratorArgumentInteger {

    public GeneratorArgumentDepth(int depth) throws GeneratorException {
        super(new GeneratorParameterDepth().getName(), depth);
    }
            
}
