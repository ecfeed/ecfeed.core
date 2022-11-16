package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.BasicParameterNode;

import java.util.HashMap;
import java.util.Map;

public class ChoiceToSolverIdMappings {

    private Map<BasicParameterNode, Map<ChoiceNode, Integer>> fLessEqMappings = new HashMap<>();
    private Map<BasicParameterNode, Map<ChoiceNode, Integer>> fLessThMappings = new HashMap<>();
    private Map<BasicParameterNode, Map<ChoiceNode, Integer>> fEqualMappings = new HashMap<>();

    Map<ChoiceNode, Integer> getEqMapping(BasicParameterNode parameter) {

        return fEqualMappings.get(parameter);
    }

    public boolean eQContainsKey(BasicParameterNode parameter) {

        return fEqualMappings.containsKey(parameter);
    }

    // TODO - name
    public void eqPut(BasicParameterNode parameter, HashMap<ChoiceNode, Integer> choiceID) {

        fEqualMappings.put(parameter, choiceID);
    }

    public Map<ChoiceNode, Integer> eqGet(BasicParameterNode parameter) {

        return fEqualMappings.get(parameter);
    }

    public void ltPut(BasicParameterNode parameter, HashMap<ChoiceNode, Integer> choiceNodeIntegerMap) {

        fLessThMappings.put(parameter, choiceNodeIntegerMap);
    }

    public Map<ChoiceNode, Integer> ltGet(BasicParameterNode parameter) {

        return fLessThMappings.get(parameter);
    }

    public void lePut(BasicParameterNode parameter, HashMap<ChoiceNode, Integer> choiceNodeIntegerMap) {

        fLessEqMappings.put(parameter, choiceNodeIntegerMap);
    }

    public Map<ChoiceNode, Integer> leGet(BasicParameterNode parameter) {

        return fLessEqMappings.get(parameter);
    }

}

