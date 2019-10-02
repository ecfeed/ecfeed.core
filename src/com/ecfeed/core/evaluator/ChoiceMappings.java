package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;

import java.util.HashMap;
import java.util.Map;

public class ChoiceMappings {

    private String fDebugCode;
    private Map<ChoiceNode, ChoiceNode> fMap;

    public ChoiceMappings(String debugCode) {

        fDebugCode = debugCode;
        fMap = new HashMap<>();
    }

    public void put(ChoiceNode keyChoiceNode, ChoiceNode valueChoiceNode) {

        fMap.put(keyChoiceNode, valueChoiceNode);
    }

    public ChoiceNode get(ChoiceNode keyChoiceNode) {

        return fMap.get(keyChoiceNode);
    }
}
