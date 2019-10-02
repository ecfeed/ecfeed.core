package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;

import java.util.HashMap;
import java.util.Map;

public class ChoiceMappings {

    private String fDebugCode;
    private Map<ChoiceNode, ChoiceNode> fSanitizedValToInputVal;

    public ChoiceMappings(String debugCode) {

        fDebugCode = debugCode;
        fSanitizedValToInputVal = new HashMap<>();
    }

    public void put(ChoiceNode keyChoiceNode, ChoiceNode valueChoiceNode) {

        fSanitizedValToInputVal.put(keyChoiceNode, valueChoiceNode);
    }

    public ChoiceNode get(ChoiceNode keyChoiceNode) {

        return fSanitizedValToInputVal.get(keyChoiceNode);
    }
}
