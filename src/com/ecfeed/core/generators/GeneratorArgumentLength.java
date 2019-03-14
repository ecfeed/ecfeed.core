package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;

public class GeneratorArgumentLength extends AbstractGeneratorArgument {

    Integer fLength;

    public GeneratorArgumentLength(int length) throws GeneratorException {
        super(new GeneratorParameterLength().getName());
        fLength = length;
    }
            
    @Override
    public Integer getValue() {
        return fLength;
    }
}
