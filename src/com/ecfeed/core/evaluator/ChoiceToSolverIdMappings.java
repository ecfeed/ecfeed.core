package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.BasicParameterNode;

import java.util.HashMap;
import java.util.Map;

public class ChoiceToSolverIdMappings {

    private Map<BasicParameterNode, Map<ChoiceNode, Integer>> fLessEqMappings = new HashMap<>();
    private Map<BasicParameterNode, Map<ChoiceNode, Integer>> fLessThMappings = new HashMap<>();
    private Map<BasicParameterNode, Map<ChoiceNode, Integer>> fEqualMappings = new HashMap<>();

    Map<ChoiceNode, Integer> getEqMapping(BasicParameterNode methodParameterNode) {

        return fEqualMappings.get(methodParameterNode);
    }

    public boolean eQContainsKey(BasicParameterNode methodParameterNode) {

        return fEqualMappings.containsKey(methodParameterNode);
    }

    public void eqPut(
            BasicParameterNode methodParameterNode,
            HashMap<ChoiceNode, Integer> choiceID // TODO - name
    ) {

        fEqualMappings.put(methodParameterNode, choiceID);
    }

    public Map<ChoiceNode, Integer> eqGet(BasicParameterNode methodParameterNode) {

        return fEqualMappings.get(methodParameterNode);
    }

    public void ltPut(
            BasicParameterNode methodParameterNode,
            HashMap<ChoiceNode, Integer> choiceNodeIntegerMap) {

        fLessThMappings.put(methodParameterNode, choiceNodeIntegerMap);
    }

    public Map<ChoiceNode, Integer> ltGet(BasicParameterNode methodParameterNode) {

        return fLessThMappings.get(methodParameterNode);
    }

    public void lePut(
            BasicParameterNode methodParameterNode,
            HashMap<ChoiceNode, Integer> choiceNodeIntegerMap) {

        fLessEqMappings.put(methodParameterNode, choiceNodeIntegerMap);
    }

    public Map<ChoiceNode, Integer> leGet(BasicParameterNode methodParameterNode) {

        return fLessEqMappings.get(methodParameterNode);
    }

}

