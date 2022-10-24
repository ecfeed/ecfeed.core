package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;

import java.util.*;

public class ChoicesMappingsBucket { // TODO - rename

    private Map<ChoiceNode, ChoiceNode> fSanitizedToInputMappings = new HashMap<>();
    private Map<ChoiceNode, Set<ChoiceNode>> fSanitizedValToAtomicVal = new HashMap<>();
    private Map<MethodParameterNode, Map<ChoiceNode, Set<ChoiceNode>>> fArgInputValToSanitizedVal = new HashMap<>();

    public ChoicesMappingsBucket() {}

    public void inputToSanPut(MethodParameterNode keyMethod) {

        fArgInputValToSanitizedVal.put(keyMethod, new HashMap<>());
    }

    public void inputToSanPut(MethodParameterNode keyMethod, ChoiceNode keyChoice, ChoiceNode valChoice) {
        Map<ChoiceNode, Set<ChoiceNode>> valMethod = fArgInputValToSanitizedVal.get(keyMethod);

        if (!valMethod.containsKey(keyChoice)) {
            valMethod.put(keyChoice, new HashSet<>());
        }

        valMethod.get(keyChoice).add(valChoice);
    }

    public Map<ChoiceNode, Set<ChoiceNode>> inputToSanGet(MethodParameterNode methodParameterNode) {

        return fArgInputValToSanitizedVal.get(methodParameterNode);
    }

    public void sanToAtmPut(ChoiceNode keyChoiceNode, ChoiceNode valueChoiceNode) {
        Set<ChoiceNode> value;

        if (fSanitizedValToAtomicVal.containsKey(keyChoiceNode)) {
            value = fSanitizedValToAtomicVal.get(keyChoiceNode);
        } else {
            value = new HashSet<>();
        }

        value.add(valueChoiceNode);
        fSanitizedValToAtomicVal.put(keyChoiceNode, value);
    }

    public Collection<ChoiceNode> sanToAtmGet(ChoiceNode keyChoiceNode) {

        return fSanitizedValToAtomicVal.get(keyChoiceNode);
    }

    public void sanToInpPut(ChoiceNode keyChoiceNode, ChoiceNode valueChoiceNode) {

        fSanitizedToInputMappings.put(keyChoiceNode, valueChoiceNode);
    }

    public ChoiceNode sanToInpGet(ChoiceNode keyChoiceNode) {

        return fSanitizedToInputMappings.get(keyChoiceNode);
    }

}
