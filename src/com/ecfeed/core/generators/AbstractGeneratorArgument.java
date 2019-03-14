package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGeneratorArgument;

public abstract class AbstractGeneratorArgument implements IGeneratorArgument {

    private String fName;

    public AbstractGeneratorArgument(String name) throws GeneratorException {
        fName = name;
    }
            
    @Override
    public String getName() {
        return fName;
    }

}
