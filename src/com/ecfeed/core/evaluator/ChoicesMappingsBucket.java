package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.google.common.collect.Multimap;

import java.util.Collection;

public class ChoicesMappingsBucket { // TODO - rename

    private SimpleChoiceMapping fSanitizedToInputMappings;
    private ChoiceMultiMapping fSanitizedValToAtomicVal;
    private ParamChoiceMappings fArgInputValToSanitizedVal;

    public ChoicesMappingsBucket() {

        fSanitizedToInputMappings = new SimpleChoiceMapping("STI");
        fSanitizedValToAtomicVal = new ChoiceMultiMapping("STA");
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

    public void sanToAtmPut(ChoiceNode keyChoiceNode, ChoiceNode valueChoiceNode) {

        fSanitizedValToAtomicVal.put(keyChoiceNode, valueChoiceNode);
    }

    public Collection<ChoiceNode> sanToAtmGet(ChoiceNode keyChoiceNode) {

        return fSanitizedValToAtomicVal.get(keyChoiceNode);
    }


    public void sanToInputPut(ChoiceNode keyChoiceNode, ChoiceNode valueChoiceNode) {

        fSanitizedToInputMappings.put(keyChoiceNode, valueChoiceNode);
    }

    public ChoiceNode sanToInputGet(ChoiceNode keyChoiceNode) {

        return fSanitizedToInputMappings.get(keyChoiceNode);
    }

}
