package com.ecfeed.core.generators;

public class GeneratorArgumentBoolean extends GeneratorArgument {

    boolean fValue;

    public GeneratorArgumentBoolean(String name, boolean value) { // TODO - protected constructor (Do we need to use objects of this class?)

        super(name);
        fValue = value;
    }

    @Override
    public Boolean getValue() {
        return fValue;
    }
}
