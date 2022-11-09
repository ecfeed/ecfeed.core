package com.ecfeed.core.evaluator;

import java.util.HashMap;
import java.util.Map;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.BasicParameterNode;

public class ParamsWithChInts {

	//    private String fDebugCode;
    private Map<BasicParameterNode, Map<ChoiceNode, Integer>> fMap;

    public ParamsWithChInts(String debugCode) {

    	//        fDebugCode = debugCode;
        fMap = new HashMap<>();
    }

    public void put(
            BasicParameterNode methodParameterNode,
            HashMap<ChoiceNode, Integer> choiceIntMap) {

        fMap.put(methodParameterNode, choiceIntMap);
    }

    public Map<ChoiceNode, Integer> get(BasicParameterNode methodParameterNode) {

        return fMap.get(methodParameterNode);
    }

    public boolean containsKey(BasicParameterNode methodParameterNode) {

        return fMap.containsKey(methodParameterNode);
    }

    Map<BasicParameterNode, Map<ChoiceNode, Integer>> getInternalMap() {

        return fMap;
    }
}
