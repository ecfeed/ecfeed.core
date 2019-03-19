package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;


public class GeneratorArgumentDouble extends GeneratorArgument {

    Double fValue;

    public GeneratorArgumentDouble(String name, double value) throws GeneratorException { // TODO - protected constructor (Do we need to use objects of this class?)
        super(name);
        fValue = value;
    }

    @Override
    public Double getValue() {
        return fValue;
    }
}
