package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;

import java.util.*;
import java.util.stream.Collectors;

public class ParameterChoices {

    private final Map<BasicParameterNode, Set<ChoiceNode>> fSanitized = new HashMap<>();
    private final Map<BasicParameterNode, Set<ChoiceNode>> fAtomic = new HashMap<>();
    private final Map<BasicParameterNode, Set<ChoiceNode>> fInput = new HashMap<>();

    public ParameterChoices() {}

    public void update(List<BasicParameterNode> parameters) {

        for (BasicParameterNode parameter : parameters) {
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

    public void putSanitized(BasicParameterNode parameter, Set<ChoiceNode> choices) {

        fSanitized.put(parameter, choices);
    }

    public void putAtomic(BasicParameterNode parameter) {

        fAtomic.put(parameter, new HashSet<>());
    }

    public void putAtomic(BasicParameterNode parameter, ChoiceNode choice) {

        fAtomic.get(parameter).add(choice);
    }

    public void putAtomic(BasicParameterNode parameter, Collection<ChoiceNode> choices) {

        fAtomic.get(parameter).addAll(choices);
    }

// ------------------------------------------------------------------------------------

    public Set<ChoiceNode> getSanitized(BasicParameterNode parameter) {

        return fSanitized.get(parameter);
    }

    public Set<ChoiceNode> getAtomic(BasicParameterNode parameter) {

        return fAtomic.get(parameter);
    }

    public Set<ChoiceNode> getInput(BasicParameterNode parameter) {

        return fInput.get(parameter);
    }

// ------------------------------------------------------------------------------------

    public Set<BasicParameterNode> getKeySetSanitized() {

        return fSanitized.keySet();
    }

    public int getSizeSanitized() {

        return fSanitized.size();
    }
}


