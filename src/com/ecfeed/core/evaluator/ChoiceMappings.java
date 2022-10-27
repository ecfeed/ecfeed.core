package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;

import java.util.*;

public class ChoiceMappings {

    private Map<ChoiceNode, ChoiceNode> fSanitizedToInput = new HashMap<>();
    private Map<ChoiceNode, Set<ChoiceNode>> fSanitizedToAtomic = new HashMap<>();
    private Map<MethodParameterNode, Map<ChoiceNode, Set<ChoiceNode>>> fInputToSanitized = new HashMap<>();

    public ChoiceMappings() {}

    public void updateSanitizedToInput(Set<ChoiceNode> choices) {

        for (ChoiceNode choice : choices) {
            fSanitizedToInput.put(choice, choice);
        }
    }

// ------------------------------------------------------------------------------------

    public void putInputToSanitized(MethodParameterNode parameter) {

        fInputToSanitized.put(parameter, new HashMap<>());
    }

    public void putInputToSanitized(MethodParameterNode parameter, ChoiceNode keyChoice, ChoiceNode valChoice) {
        Map<ChoiceNode, Set<ChoiceNode>> valMethod = fInputToSanitized.get(parameter);

        if (!valMethod.containsKey(keyChoice)) {
            valMethod.put(keyChoice, new HashSet<>());
        }

        valMethod.get(keyChoice).add(valChoice);
    }

    public Map<ChoiceNode, Set<ChoiceNode>> getInputToSanitized(MethodParameterNode parameter) {

        return fInputToSanitized.get(parameter);
    }

    public void putSanitizedToAtomic(ChoiceNode keyChoice, ChoiceNode valChoice) {
        Set<ChoiceNode> value;

        if (fSanitizedToAtomic.containsKey(keyChoice)) {
            value = fSanitizedToAtomic.get(keyChoice);
        } else {
            value = new HashSet<>();
        }

        value.add(valChoice);
        fSanitizedToAtomic.put(keyChoice, value);
    }

    public Collection<ChoiceNode> getSanitizedToAtomic(ChoiceNode choice) {

        return fSanitizedToAtomic.get(choice);
    }

    public void putSanitizedToInput(ChoiceNode keyChoice, ChoiceNode valChoice) {

        fSanitizedToInput.put(keyChoice, valChoice);
    }

    public ChoiceNode getSanitizedToInput(ChoiceNode choice) {

        return fSanitizedToInput.get(choice);
    }

}
