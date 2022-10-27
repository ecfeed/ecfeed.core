package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodParameterNode;

import java.util.*;
import java.util.stream.Collectors;

public class ParameterChoices {

    private final Map<MethodParameterNode, Set<ChoiceNode>> fSanitized = new HashMap<>();
    private final Map<MethodParameterNode, Set<ChoiceNode>> fAtomic = new HashMap<>();
    private final Map<MethodParameterNode, Set<ChoiceNode>> fInput = new HashMap<>();

    public ParameterChoices() {}

    public void update(List<MethodParameterNode> parameters) {

        for (MethodParameterNode parameter : parameters) {
            Set<ChoiceNode> choiceSet = new HashSet<>();

            for (ChoiceNode choice : parameter.getLeafChoicesWithCopies()) {
                choiceSet.add(choice.getOrigChoiceNode());
            }

            fInput.put(parameter, choiceSet);
            fSanitized.put(parameter, new HashSet<>(choiceSet));
        }
    }

    public Set<ChoiceNode> getInputChoices() {

        return fInput.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(HashSet::new));
    }

// ------------------------------------------------------------------------------------

    public void putSanitized(MethodParameterNode parameter, Set<ChoiceNode> choices) {

        fSanitized.put(parameter, choices);
    }

    public void putAtomic(MethodParameterNode parameter, Set<ChoiceNode> choices) {

        fAtomic.put(parameter, choices);
    }

// ------------------------------------------------------------------------------------

    public Set<ChoiceNode> getSanitized(MethodParameterNode parameter) {

        return fSanitized.get(parameter);
    }

    public Set<ChoiceNode> getAtomic(MethodParameterNode parameter) {

        return fAtomic.get(parameter);
    }

    public Set<ChoiceNode> getInput(MethodParameterNode parameter) {

        return fInput.get(parameter);
    }

// ------------------------------------------------------------------------------------

    public Set<MethodParameterNode> getKeySetSanitized() {

        return fSanitized.keySet();
    }

    public int getSizeSanitized() {

        return fSanitized.size();
    }
}


