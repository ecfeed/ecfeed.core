package com.ecfeed.core.generators;

public class GeneratorArgumentBoolean extends GeneratorArgument {

    boolean fValue;

    public GeneratorArgumentBoolean(String name, boolean value) {

        super(name);
        fValue = value;
    }

    @Override
    public Boolean getValue() {
        return fValue;
    }
}
