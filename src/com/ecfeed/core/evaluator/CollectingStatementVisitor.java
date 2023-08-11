package com.ecfeed.core.evaluator;


import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.ExceptionHelper;

import java.util.List;

class CollectingStatementVisitor implements IStatementVisitor {

    private List<RelationStatement> fInOutRelationStatements;

    public CollectingStatementVisitor(List<RelationStatement> inOutRelationStatements) {
        fInOutRelationStatements = inOutRelationStatements;
    }

    @Override
    public Object visit(StatementArray statement) {
        for (AbstractStatement child : statement.getChildren()) {
            try {
                child.accept(new CollectingStatementVisitor(fInOutRelationStatements));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Object visit(RelationStatement statement) {
        fInOutRelationStatements.add(statement);
        return null;
    }

    @Override
    public Object visit(StaticStatement statement) {
        return null;
    }

    @Override
    public Object visit(LabelCondition statement) {
        reportUnexpectedTypeException();
        return null;
    }

    @Override
    public Object visit(ChoiceCondition statement) {
        reportUnexpectedTypeException();
        return null;
    }

    @Override
    public Object visit(ParameterCondition statement) {
        reportUnexpectedTypeException();
        return null;
    }

    @Override
    public Object visit(ValueCondition statement) {
        reportUnexpectedTypeException();
        return null;
    }

    private void reportUnexpectedTypeException() {
        ExceptionHelper.reportRuntimeException("Unexpected type.");
    }

}
