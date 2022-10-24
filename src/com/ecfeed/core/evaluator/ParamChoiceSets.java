package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;

import java.util.*;

public class ParamChoiceSets {

    private final Map<MethodParameterNode, Set<ChoiceNode>> fSanitizedChoices = new HashMap<>();
    private final Map<MethodParameterNode, Set<ChoiceNode>> fAtomicChoices = new HashMap<>();
    private final Map<MethodParameterNode, Set<ChoiceNode>> fInputChoices = new HashMap<>();

    public ParamChoiceSets(List<MethodParameterNode> parameters) {

        createInputChoices(parameters);
    }

    public void atomicPut(MethodParameterNode methodParameterNode, Set<ChoiceNode> setOfChoices) {

        fAtomicChoices.put(methodParameterNode, setOfChoices);
    }

    public Set<ChoiceNode> atomicGet(MethodParameterNode methodParameterNode) {

        return fAtomicChoices.get(methodParameterNode);
    }

    public void sanitizedPut(MethodParameterNode methodParameterNode, Set<ChoiceNode> setOfChoices) {

        fSanitizedChoices.put(methodParameterNode, setOfChoices);
    }

    public Set<ChoiceNode> sanitizedGet(MethodParameterNode methodParameterNode) {

        return fSanitizedChoices.get(methodParameterNode);
    }

    public Set<MethodParameterNode> sanitizedGetKeySet() {

        return fSanitizedChoices.keySet();
    }

    public int sanitizedGetSize() {

        return fSanitizedChoices.size();
    }

    public Set<ChoiceNode> inputGet(MethodParameterNode methodParameterNode) {

        return fInputChoices.get(methodParameterNode);
    }

    public Set<MethodParameterNode> inputGetKeySet() {

        return fInputChoices.keySet();
    }

    private void createInputChoices(List<MethodParameterNode> parameters) {

        for (MethodParameterNode parameter : parameters) {
            Set<ChoiceNode> choiceSet = new HashSet<>();

            for (ChoiceNode choice : parameter.getLeafChoicesWithCopies()) {

                choiceSet.add(choice.getOrigChoiceNode());
            }

            fInputChoices.put(parameter, choiceSet);
        }
    }
}


