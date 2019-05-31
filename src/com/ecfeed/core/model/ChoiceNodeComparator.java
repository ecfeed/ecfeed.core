package com.ecfeed.core.evaluator;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.JavaTypeHelper;
import com.ecfeed.core.utils.RelationMatcher;

import java.util.Comparator;

public class ChoiceNodeComparator implements Comparator<ChoiceNode> {
    public int compare(ChoiceNode leftArg, ChoiceNode rightArg) {
        String substituteType = JavaTypeHelper.getSubstituteType(leftArg.getParameter().getType(), rightArg.getParameter().getType());
        if( RelationMatcher.isRelationMatch(EMathRelation.LESS_THAN, substituteType, leftArg.getValueString(), rightArg.getValueString()) )
            return -1;
        else if( RelationMatcher.isRelationMatch(EMathRelation.GREATER_THAN, substituteType, leftArg.getValueString(), rightArg.getValueString()) )
            return 1;
        else
            return 0;
    }
}
