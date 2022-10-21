package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParamChoiceSets {

    public ParamsWithChoices fInputChoices;
    public ParamsWithChoices fSanitizedChoices;
    public ParamsWithChoices fAtomicChoices;

    public ParamChoiceSets(List<MethodParameterNode> methodParameters) {

        fAtomicChoices = new ParamsWithChoices("ATM");
        fSanitizedChoices = new ParamsWithChoices("SAN");
        fInputChoices = new ParamsWithChoices("ALL");

        fInputChoices = createInputChoices(methodParameters);
    }

    public void atomicPut(MethodParameterNode methodParameterNode, Set<ChoiceNode> setOfChoices) {

        fAtomicChoices.put(methodParameterNode, setOfChoices);
    }

    public Set<ChoiceNode> atomicGet(MethodParameterNode methodParameterNode) {

        return fAtomicChoices.get(methodParameterNode);
    }

    public void sainitizedPut(MethodParameterNode methodParameterNode, Set<ChoiceNode> setOfChoices) {

        fSanitizedChoices.put(methodParameterNode, setOfChoices);
    }

    public Set<ChoiceNode> sainitizedGet(MethodParameterNode methodParameterNode) {

        return fSanitizedChoices.get(methodParameterNode);
    }

    public Set<MethodParameterNode> sanitizedGetKeySet() {

        return fSanitizedChoices.getKeySet();
    }

    public int sanitizedGetSize() {

        return fSanitizedChoices.getSize();
    }

    public Set<ChoiceNode> inputGet(MethodParameterNode methodParameterNode) {

        return fInputChoices.get(methodParameterNode);
    }

    public Set<MethodParameterNode> inputGetKeySet() {

        return fInputChoices.getKeySet();
    }

    private static ParamsWithChoices createInputChoices(
            List<MethodParameterNode> methodParameters) {

        ParamsWithChoices inputValues = new ParamsWithChoices("TMP");

        for (MethodParameterNode methodParameterNode : methodParameters) {

            Set<ChoiceNode> choiceNodeSet = new HashSet<>();
            for (ChoiceNode choiceNode : methodParameterNode.getLeafChoicesWithCopies())
                choiceNodeSet.add(choiceNode.getOrigChoiceNode());

            inputValues.put(methodParameterNode, choiceNodeSet);
        }

        return inputValues;
    }

}


