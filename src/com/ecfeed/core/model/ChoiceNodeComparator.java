package com.ecfeed.core.model;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.RelationMatcher;

import java.util.Comparator;

public class ChoiceNodeComparator implements Comparator<ChoiceNode> {

    public int compare(ChoiceNode leftArg, ChoiceNode rightArg) {

        if (leftArg.isRandomizedValue() || rightArg.isRandomizedValue()) {
            ExceptionHelper.reportRuntimeException("Randomized values should not be compared!");
        }

        String commonType = JavaLanguageHelper.getSubstituteType(leftArg.getParameter().getType(), rightArg.getParameter().getType());
// Left value is smaller (-1).
        if (RelationMatcher.isRelationMatch(EMathRelation.LESS_THAN, commonType, leftArg.getValueString(), rightArg.getValueString())) {
            return -1;
        }
// Left value is greater (+1).
        if (RelationMatcher.isRelationMatch(EMathRelation.GREATER_THAN, commonType, leftArg.getValueString(), rightArg.getValueString())) {
            return 1;
        }
// Values are equal.
        return 0;
    }
}
