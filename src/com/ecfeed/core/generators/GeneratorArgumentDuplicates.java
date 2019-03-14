package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;


public class GeneratorArgumentDuplicates extends GeneratorArgumentBoolean {

    public GeneratorArgumentDuplicates(boolean duplicates) throws GeneratorException {
        super(new GeneratorParameterDuplicates().getName(), duplicates);
    }

}
