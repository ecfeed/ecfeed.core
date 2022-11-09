package com.ecfeed.core.evaluator;

import java.util.HashMap;
import java.util.Map;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.google.common.collect.Multimap;

public class ParamChoiceMappings {

    private Map<BasicParameterNode, Multimap<ChoiceNode, ChoiceNode>> fArgInputValToSanitizedVal; // TODO - rename

    public ParamChoiceMappings() {

        fArgInputValToSanitizedVal = new HashMap<>();

    }

    public void put(BasicParameterNode methodParameterNode, Multimap<ChoiceNode, ChoiceNode> value) {

        fArgInputValToSanitizedVal.put(methodParameterNode, value);
    }

    public Multimap<ChoiceNode, ChoiceNode> get(BasicParameterNode methodParameterNode) {

        return fArgInputValToSanitizedVal.get(methodParameterNode);
    }
}
