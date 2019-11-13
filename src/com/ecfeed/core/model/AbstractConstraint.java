package com.ecfeed.core.model;

import com.ecfeed.core.utils.EvaluationResult;

import java.util.List;
import java.util.Set;

public abstract class AbstractConstraint implements IConstraint<ChoiceNode> {
    protected String fName;

    public String getName() {
        return fName;
    }

    public void setName(String name) {
        fName = name;
    }

    public abstract EvaluationResult evaluate(List<ChoiceNode> values);
    public abstract boolean adapt(List<ChoiceNode> values);

    public abstract String toString();
    public abstract boolean mentions(int dimension);
    public abstract boolean mentions(MethodParameterNode parameter);
    public abstract boolean mentions(MethodParameterNode parameter, String label);
    public abstract boolean mentions(ChoiceNode choice);
    public abstract List<ChoiceNode> getListOfChoices();
    public abstract AbstractConstraint getCopy();
    public abstract boolean updateReferences(MethodNode method);
    public abstract Set<ChoiceNode> getReferencedChoices();
    public abstract Set<AbstractParameterNode> getReferencedParameters();
    public abstract Set<String> getReferencedLabels(MethodParameterNode parameter);
    abstract boolean mentionsParameter(MethodParameterNode methodParameter);
}
