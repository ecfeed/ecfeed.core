package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;


public class GeneratorArgumentInteger extends GeneratorArgument {

    Integer fValue;

    public GeneratorArgumentInteger(String name, int value) throws GeneratorException {
        super(name);
        fValue = value;
    }

    @Override
    public Integer getValue() {
        return fValue;
    }
}
