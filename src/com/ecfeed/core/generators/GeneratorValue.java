package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IParameterDefinition;
import com.ecfeed.core.generators.api.IGeneratorValue;

public class GeneratorValue implements IGeneratorValue {

    private Object fValue;
    private IParameterDefinition fDefinition;

    public GeneratorValue(IParameterDefinition definition, String value) throws GeneratorException {
        fDefinition = definition;
        fValue = fDefinition.parse(value);
        fDefinition.test(fValue);
    }

    @Override
    public Object getValue() {
        return fValue;
    }

    @Override
    public IParameterDefinition getDefinition() {
        return fDefinition;
    }

}
