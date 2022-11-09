package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.BasicParameterNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ParamsWithChoices {

	//    private String fDebugCode;
    private Map<BasicParameterNode, Set<ChoiceNode>> fParamsWithChoices;

    public ParamsWithChoices(String debugCode) {

    	//        fDebugCode = debugCode;
        fParamsWithChoices = new HashMap<>();
    }

    public void put(BasicParameterNode methodParameterNode, Set<ChoiceNode> choiceNodeSet) {
        fParamsWithChoices.put(methodParameterNode, choiceNodeSet);
    }

    public Set<ChoiceNode> get(BasicParameterNode methodParameterNode) {
        return fParamsWithChoices.get(methodParameterNode);
    }

    public Set<BasicParameterNode> getKeySet() {
        return fParamsWithChoices.keySet();
    }

    public int getSize() {
        return fParamsWithChoices.size();
    }

}
