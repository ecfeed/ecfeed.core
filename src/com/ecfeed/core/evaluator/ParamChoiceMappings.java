package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.HashMap;
import java.util.Map;

public class ParamChoiceMappings {

    private Map<MethodParameterNode, Multimap<ChoiceNode, ChoiceNode>> fArgInputValToSanitizedVal; // TODO - rename

    public ParamChoiceMappings() {

        fArgInputValToSanitizedVal = new HashMap<>();

    }

    public void put(MethodParameterNode methodParameterNode, HashMultimap<ChoiceNode, ChoiceNode> value) {

        fArgInputValToSanitizedVal.put(methodParameterNode, value);
    }

    public Multimap<ChoiceNode, ChoiceNode> get(MethodParameterNode methodParameterNode) {

        return fArgInputValToSanitizedVal.get(methodParameterNode);
    }
}
