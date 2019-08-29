package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.IGeneratorArgument;

public class GeneratorArgument implements IGeneratorArgument {

    private String fName;
    private Object fValue = null;

    protected GeneratorArgument(String name) {
        fName = name;
    }
    
    public GeneratorArgument(String name, Object value) {
        fName = name;
        fValue = value;
    }

    @Override
    public String getName() {
        return fName;
    }

	@Override
	public Object getValue() {
		return fValue;
	}

}
