package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;

import java.util.HashSet;
import java.util.Set;

public class ParamChoiceSets {

    public ParamsWithChoices fInputChoices;
    public ParamsWithChoices fSanitizedChoices;
    public ParamsWithChoices fAtomicChoices;

    public ParamChoiceSets() {

        fAtomicChoices = new ParamsWithChoices("ATM");
    }

    public void atomicPut(MethodParameterNode methodParameterNode, Set<ChoiceNode> setOfChoices) {

        fAtomicChoices.put(methodParameterNode, setOfChoices);
    }

    public Set<ChoiceNode> atomicGet(MethodParameterNode methodParameterNode) {

        return fAtomicChoices.get(methodParameterNode);
    }
}


