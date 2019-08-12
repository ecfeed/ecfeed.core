package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;


public class GeneratorArgumentAdaptive extends GeneratorArgumentBoolean {

    public GeneratorArgumentAdaptive(boolean adaptive) throws GeneratorException {
        super(new GeneratorParameterAdaptive().getName(), adaptive);
    }

}