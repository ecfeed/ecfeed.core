package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;

import java.util.HashMap;
import java.util.Map;

public class CMappings {

    public ParamsWithChInts fChoiceToSolverIdLessEqMappings;
    public ParamsWithChInts fChoiceToSolverIdLessThMappings;
    private ParamsWithChInts fChoiceToSolverIdEqualMappings;

    final int fLogLevel = 0;

    public CMappings() {

        fChoiceToSolverIdLessEqMappings = new ParamsWithChInts("LEQ");
        Sat4Logger.log("fChoiceToSolverIdLessEqMappings", fChoiceToSolverIdLessEqMappings, 1, fLogLevel);

        fChoiceToSolverIdLessThMappings = new ParamsWithChInts("LES");
        Sat4Logger.log("fChoiceToSolverIdLessThMappings", fChoiceToSolverIdLessThMappings, 1, fLogLevel);

        fChoiceToSolverIdEqualMappings = new ParamsWithChInts("EQ");
        Sat4Logger.log("fChoiceToSolverIdEqualMappings", fChoiceToSolverIdEqualMappings, 1, fLogLevel);
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
}

