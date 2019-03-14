package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;

public class GeneratorArgumentDepth extends AbstractGeneratorArgument {

    Integer fDepth;

    public GeneratorArgumentDepth(int depth) throws GeneratorException {

        super(new GeneratorParameterDepth().getName());
        fDepth = depth;
    }
            
    @Override
    public Integer getValue() {
        return fDepth;
    }
}
