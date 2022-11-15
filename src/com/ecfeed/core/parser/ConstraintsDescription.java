package com.ecfeed.core.parser;

import com.ecfeed.core.utils.ExceptionHelper;

import java.util.List;

public class ConstraintsDescription {

    private enum ConstraintsValueType {

        NORMAL,
        ALL,
        NONE
    }

    private static final String specialValueAllConstraints = "ALL";
    private static final String specialValueNoneConstraints = "NONE";

    private ConstraintsDescription.ConstraintsValueType fConstraintsValueType;
    private List<String> fConstraintNames = null;

    @SuppressWarnings("unchecked")
	public ConstraintsDescription(Object constraintsObject) {

        if (constraintsObject == null) {
            fConstraintsValueType = ConstraintsDescription.ConstraintsValueType.ALL;
            return;
        }

        if (constraintsObject instanceof String) {
            fConstraintsValueType = getConstraintsFromString((String) constraintsObject);
            return;
        }

        try {
            fConstraintNames = (List<String>) constraintsObject;
        } catch (Exception e) {
            ExceptionHelper.reportRuntimeException("Invalid type of constraints object. Can not convert to list of names.");
        }

        if (fConstraintNames.size() == 0) {
            ExceptionHelper.reportRuntimeException("Requested list of constraints should not be empty.");
        }
    }

    public boolean isAllConstraints() {

        if (fConstraintsValueType == ConstraintsValueType.ALL) {
            return true;
        }

        return false;
    }

    public boolean isNoneConstraints() {

        if (fConstraintsValueType == ConstraintsValueType.NONE) {
            return true;
        }

        return false;
    }

    public List<String> getConstraintNames() {

        return fConstraintNames;
    }

    private ConstraintsValueType getConstraintsFromString(String constraintsString) {

        if (constraintsString.equals(specialValueNoneConstraints)) {
            return ConstraintsValueType.NONE;
        }

        if (constraintsString.equals(specialValueAllConstraints)) {
            return ConstraintsValueType.ALL;
        }

        ExceptionHelper.reportClientException("Invalid special value for constraints: " + constraintsString);
        return null;
    }

}
