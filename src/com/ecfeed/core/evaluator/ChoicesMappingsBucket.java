package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.google.common.collect.Multimap;

public class ChoicesMappingsBucket { // TODO - rename

    public ParamChoiceMappings fArgInputValToSanitizedVal;

    public ChoicesMappingsBucket() {

        fArgInputValToSanitizedVal = new ParamChoiceMappings();
    }

    public void inputToSanPut(
            MethodParameterNode methodParameterNode,
            final Multimap<ChoiceNode, ChoiceNode> value) {

        fArgInputValToSanitizedVal.put(methodParameterNode, value);
    }

    public Multimap<ChoiceNode, ChoiceNode> inputToSanGet(
            MethodParameterNode methodParameterNode) {

        return fArgInputValToSanitizedVal.get(methodParameterNode);
    }
}
