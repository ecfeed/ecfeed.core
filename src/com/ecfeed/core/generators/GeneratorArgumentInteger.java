package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;


public class GeneratorArgumentInteger extends GeneratorArgument {

    Integer fValue;

    public GeneratorArgumentInteger(String name, int value) throws GeneratorException { // TODO - protected constructor (Do we need to use objects of this class?)
        super(name);
        fValue = value;
    }

    @Override
    public Integer getValue() {
        return fValue;
    }
}
