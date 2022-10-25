package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.ExceptionHelper;

import java.util.List;
import java.util.Set;

public class CollectingMethodVisitor  implements IStatementVisitor {

    private Set<MethodNode> fMethodNodes;

    public CollectingMethodVisitor(Set<MethodNode> methodNodes) {
        fMethodNodes = methodNodes;
    }

    @Override
    public Object visit(StaticStatement statement) {

        for (AbstractStatement child : statement.getChildren()) {
            try {
                child.accept(new CollectingMethodVisitor(fMethodNodes));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public Object visit(StatementArray statement) {
        reportUnexpectedTypeException();
        return null;
    }

    @Override
    public Object visit(ExpectedValueStatement statement) {
        fMethodNodes.addAll(statement.getLeftMethodParameterNode().getMethods());

        return null;
    }

    @Override
    public Object visit(RelationStatement statement) {
        fMethodNodes.addAll(statement.getLeftParameter().getMethods());

        return null;
    }

    @Override
    public Object visit(LabelCondition condition) {
        reportUnexpectedTypeException();
        return null;
    }

    @Override
    public Object visit(ChoiceCondition condition) {
        reportUnexpectedTypeException();
        return null;
    }

    @Override
    public Object visit(ParameterCondition condition) {
        reportUnexpectedTypeException();
        return null;
    }

    @Override
    public Object visit(ValueCondition condition) {
        reportUnexpectedTypeException();
        return null;
    }

    private void reportUnexpectedTypeException() {
        ExceptionHelper.reportRuntimeException("Unexpected type.");
    }
}
