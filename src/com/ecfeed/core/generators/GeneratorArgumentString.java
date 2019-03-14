package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;


public class GeneratorArgumentString extends GeneratorArgument {

    String fValue;

    public GeneratorArgumentString(String name, String value) throws GeneratorException {
        super(name);
        fValue = value;
    }

    @Override
    public String getValue() {
        return fValue;
    }
}
