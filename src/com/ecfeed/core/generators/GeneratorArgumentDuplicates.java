package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;


public class GeneratorArgumentDuplicates extends AbstractGeneratorArgument {

    boolean fDuplicates;

    public GeneratorArgumentDuplicates(boolean duplicates) throws GeneratorException {

        super(new GeneratorParameterDuplicates().getName());
        fDuplicates = duplicates;
    }

    @Override
    public Boolean getValue() {
        return fDuplicates;
    }
}
