package com.ecfeed.core.generators.api;

import com.ecfeed.core.generators.GeneratorValue;

import java.util.*;

public class ParameterConverter {

    public static List<IGeneratorValue> deserialize(
            Map<String, String> valueMap,
            List<IParameterDefinition> paramList) {

        Set<String> paramNames = new HashSet<>(valueMap.keySet());
        List<IGeneratorValue> retMap = new ArrayList<>();

        for (IParameterDefinition paramDef : paramList) {
            String name = paramDef.getName();

            if (valueMap.containsKey(name)) {
                retMap.add(new GeneratorValue(paramDef, valueMap.get(name)));
                paramNames.remove(name);
            } else {
                retMap.add(new GeneratorValue(paramDef, null)); //will trigger default value
            }
        }

        if (!paramNames.isEmpty()) {
            GeneratorExceptionHelper.reportException("Unknown generator parameters " + paramNames);
        }

        return retMap;
    }
}
