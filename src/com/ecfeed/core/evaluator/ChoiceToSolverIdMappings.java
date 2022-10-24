package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;

import java.util.HashMap;
import java.util.Map;

public class ChoiceToSolverIdMappings {

    private Map<MethodParameterNode, Map<ChoiceNode, Integer>> fLessEqMappings = new HashMap<>();
    private Map<MethodParameterNode, Map<ChoiceNode, Integer>> fLessThMappings = new HashMap<>();
    private Map<MethodParameterNode, Map<ChoiceNode, Integer>> fEqualMappings = new HashMap<>();

    Map<ChoiceNode, Integer> getEqMapping(MethodParameterNode methodParameterNode) {

        return fEqualMappings.get(methodParameterNode);
    }

    public boolean eQContainsKey(MethodParameterNode methodParameterNode) {

        return fEqualMappings.containsKey(methodParameterNode);
    }

    public void eqPut(
            MethodParameterNode methodParameterNode,
            HashMap<ChoiceNode, Integer> choiceID // TODO - name
    ) {

        fEqualMappings.put(methodParameterNode, choiceID);
    }

    public Map<ChoiceNode, Integer> eqGet(MethodParameterNode methodParameterNode) {

        return fEqualMappings.get(methodParameterNode);
    }

    public void ltPut(
            MethodParameterNode methodParameterNode,
            HashMap<ChoiceNode, Integer> choiceNodeIntegerMap) {

        fLessThMappings.put(methodParameterNode, choiceNodeIntegerMap);
    }

    public Map<ChoiceNode, Integer> ltGet(MethodParameterNode methodParameterNode) {

        return fLessThMappings.get(methodParameterNode);
    }

    public void lePut(
            MethodParameterNode methodParameterNode,
            HashMap<ChoiceNode, Integer> choiceNodeIntegerMap) {

        fLessEqMappings.put(methodParameterNode, choiceNodeIntegerMap);
    }

    public Map<ChoiceNode, Integer> leGet(MethodParameterNode methodParameterNode) {

        return fLessEqMappings.get(methodParameterNode);
    }

}

