package com.ecfeed.core.parser;

import java.util.List;

public class ConstraintsParser { // TODO - REUSE IN JUNIT5

    private enum ConstraintsValueType {

        NORMAL,
        ALL,
        NONE
    }

    private static final String specialValueAllConstraints = "ALL";
    private static final String specialValueNoneConstraints = "NONE";

    private ConstraintsParser.ConstraintsValueType fConstraintsValueType;
    private List<String> fConstraintNames = null;

    @SuppressWarnings("unchecked")
	public ConstraintsParser(Object constraintsObject) throws Exception {

        if (constraintsObject == null) {
            fConstraintsValueType = ConstraintsParser.ConstraintsValueType.ALL;
            return;
        }

        if (constraintsObject instanceof String) {
            fConstraintsValueType = getConstraintsFromString((String) constraintsObject);
            return;
        }

        try {
            fConstraintNames = (List<String>) constraintsObject;
        } catch (Exception e) {
            throw new Exception("Invalid type of constraints object. Can not convert to list of names.");
        }

        if (fConstraintNames.size() == 0) {
            throw new Exception("Requested list of constraints should not be empty.");
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

    private ConstraintsValueType getConstraintsFromString(String constraintsString) throws Exception {

        if (constraintsString.equals(specialValueNoneConstraints)) {
            return ConstraintsValueType.NONE;
        }

        if (constraintsString.equals(specialValueAllConstraints)) {
            return ConstraintsValueType.ALL;
        }

        throw new Exception("Invalid special value for constraints: " + constraintsString);
    }

}
