package com.ecfeed.core.evaluator;

import java.util.HashMap;
import java.util.Map;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;

public class ParamsWithChInts {

	//    private String fDebugCode;
    private Map<MethodParameterNode, Map<ChoiceNode, Integer>> fMap;

    public ParamsWithChInts(String debugCode) {

    	//        fDebugCode = debugCode;
        fMap = new HashMap<>();
    }

    public void put(
            MethodParameterNode methodParameterNode,
            HashMap<ChoiceNode, Integer> choiceIntMap) {

        fMap.put(methodParameterNode, choiceIntMap);
    }

    public Map<ChoiceNode, Integer> get(MethodParameterNode methodParameterNode) {

        return fMap.get(methodParameterNode);
    }

    public boolean containsKey(MethodParameterNode methodParameterNode) {

        return fMap.containsKey(methodParameterNode);
    }
}
