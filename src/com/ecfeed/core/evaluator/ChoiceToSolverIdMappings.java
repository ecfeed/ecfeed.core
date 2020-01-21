package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;

import java.util.HashMap;
import java.util.Map;

public class ChoiceToSolverIdMappings {

    private ParamsWithChInts fChoiceToSolverIdLessEqMappings;
    private ParamsWithChInts fChoiceToSolverIdLessThMappings;
    private ParamsWithChInts fChoiceToSolverIdEqualMappings;

    final int fLogLevel = 0;

    public ChoiceToSolverIdMappings() {

        fChoiceToSolverIdLessEqMappings = new ParamsWithChInts("LEQ");
        Sat4jLogger.log("fChoiceToSolverIdLessEqMappings", fChoiceToSolverIdLessEqMappings, 1, fLogLevel);

        fChoiceToSolverIdLessThMappings = new ParamsWithChInts("LES");
        Sat4jLogger.log("fChoiceToSolverIdLessThMappings", fChoiceToSolverIdLessThMappings, 1, fLogLevel);

        fChoiceToSolverIdEqualMappings = new ParamsWithChInts("EQ");
        Sat4jLogger.log("fChoiceToSolverIdEqualMappings", fChoiceToSolverIdEqualMappings, 1, fLogLevel);
    }

    Map<ChoiceNode, Integer> getEqMapping(MethodParameterNode methodParameterNode) {

        return fChoiceToSolverIdEqualMappings.get(methodParameterNode);
    }

    public boolean eQContainsKey(MethodParameterNode methodParameterNode) {

        return fChoiceToSolverIdEqualMappings.containsKey(methodParameterNode);
    }

    public void eqPut(
            MethodParameterNode methodParameterNode,
            HashMap<ChoiceNode, Integer> choiceID // TODO - name
    ) {

        fChoiceToSolverIdEqualMappings.put(methodParameterNode, choiceID);
    }

    public Map<ChoiceNode, Integer> eqGet(MethodParameterNode methodParameterNode) {

        return fChoiceToSolverIdEqualMappings.get(methodParameterNode);
    }

    public void ltPut(
            MethodParameterNode methodParameterNode,
            HashMap<ChoiceNode, Integer> choiceNodeIntegerMap) {

        fChoiceToSolverIdLessThMappings.put(methodParameterNode, choiceNodeIntegerMap);
    }

    public Map<ChoiceNode, Integer> ltGet(MethodParameterNode methodParameterNode) {

        return fChoiceToSolverIdLessThMappings.get(methodParameterNode);
    }

    public void lePut(
            MethodParameterNode methodParameterNode,
            HashMap<ChoiceNode, Integer> choiceNodeIntegerMap) {

        fChoiceToSolverIdLessEqMappings.put(methodParameterNode, choiceNodeIntegerMap);
    }

    public Map<ChoiceNode, Integer> leGet(MethodParameterNode methodParameterNode) {

        return fChoiceToSolverIdLessEqMappings.get(methodParameterNode);
    }

}

