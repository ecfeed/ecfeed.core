package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.utils.LogHelperCore;

import java.util.HashMap;
import java.util.Map;

public class ChoiceToSolverIdMappings {

    private ParamsWithChInts fChoiceToSolverIdLessEqMappings;
    private ParamsWithChInts fChoiceToSolverIdLessThMappings;
    private ParamsWithChInts fChoiceToSolverIdEqualMappings;

    final int fLogLevel = 0;

    public ChoiceToSolverIdMappings() {

        fChoiceToSolverIdLessEqMappings = new ParamsWithChInts("LEQ");
        LogHelperCore.log("fChoiceToSolverIdLessEqMappings", fChoiceToSolverIdLessEqMappings);

        fChoiceToSolverIdLessThMappings = new ParamsWithChInts("LES");
        LogHelperCore.log("fChoiceToSolverIdLessThMappings", fChoiceToSolverIdLessThMappings);

        fChoiceToSolverIdEqualMappings = new ParamsWithChInts("EQ");
        LogHelperCore.log("fChoiceToSolverIdEqualMappings", fChoiceToSolverIdEqualMappings);
    }

    Map<ChoiceNode, Integer> getEqMapping(BasicParameterNode methodParameterNode) {

        return fChoiceToSolverIdEqualMappings.get(methodParameterNode);
    }

    public boolean eQContainsKey(BasicParameterNode methodParameterNode) {

        return fChoiceToSolverIdEqualMappings.containsKey(methodParameterNode);
    }

    public void eqPut(
            BasicParameterNode methodParameterNode,
            HashMap<ChoiceNode, Integer> choiceID // TODO - name
    ) {

        fChoiceToSolverIdEqualMappings.put(methodParameterNode, choiceID);
    }

    public Map<ChoiceNode, Integer> eqGet(BasicParameterNode methodParameterNode) {

        return fChoiceToSolverIdEqualMappings.get(methodParameterNode);
    }

    public void ltPut(
            BasicParameterNode methodParameterNode,
            HashMap<ChoiceNode, Integer> choiceNodeIntegerMap) {

        fChoiceToSolverIdLessThMappings.put(methodParameterNode, choiceNodeIntegerMap);
    }

    public Map<ChoiceNode, Integer> ltGet(BasicParameterNode methodParameterNode) {

        return fChoiceToSolverIdLessThMappings.get(methodParameterNode);
    }

    public void lePut(
            BasicParameterNode methodParameterNode,
            HashMap<ChoiceNode, Integer> choiceNodeIntegerMap) {

        fChoiceToSolverIdLessEqMappings.put(methodParameterNode, choiceNodeIntegerMap);
    }

    public Map<ChoiceNode, Integer> leGet(BasicParameterNode methodParameterNode) {

        return fChoiceToSolverIdLessEqMappings.get(methodParameterNode);
    }

}

