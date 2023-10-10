package com.ecfeed.core.model;

import java.util.Comparator;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.RelationMatcher;

public class ChoiceNodeComparator implements Comparator<ChoiceNode> {

    public int compare(ChoiceNode leftArg, ChoiceNode rightArg) {

        if (leftArg.isRandomizedValue() || rightArg.isRandomizedValue()) {
            return 0;
//          ExceptionHelper.reportRuntimeException("Randomized values should not be compared!");
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
