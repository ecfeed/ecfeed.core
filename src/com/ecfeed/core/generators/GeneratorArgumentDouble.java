package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;


public class GeneratorArgumentDouble extends GeneratorArgument {

    Double fValue;

    public GeneratorArgumentDouble(String name, double value) throws GeneratorException {
        super(name);
        fValue = value;
    }

    @Override
    public Double getValue() {
        return fValue;
    }
}
