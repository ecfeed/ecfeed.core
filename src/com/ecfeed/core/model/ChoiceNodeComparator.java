package com.ecfeed.core.model;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.RelationMatcher;

import java.util.Comparator;

public class ChoiceNodeComparator implements Comparator<ChoiceNode> {

    public int compare(ChoiceNode leftArg, ChoiceNode rightArg) {

        if(leftArg.isRandomizedValue() || rightArg.isRandomizedValue())
            ExceptionHelper.reportRuntimeException("Randomized values should not be compared!");

        String substituteType = JavaTypeHelper.getSubstituteType(leftArg.getParameter().getType(), rightArg.getParameter().getType());

        if ( RelationMatcher.isRelationMatch(EMathRelation.LESS_THAN, substituteType, leftArg.getValueString(), rightArg.getValueString()) ) {
            return -1;
        }

        if ( RelationMatcher.isRelationMatch(EMathRelation.GREATER_THAN, substituteType, leftArg.getValueString(), rightArg.getValueString()) ) {
            return 1;
        }

        return 0;
    }
}
