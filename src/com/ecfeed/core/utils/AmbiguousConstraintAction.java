package com.ecfeed.core.utils;

public enum AmbiguousConstraintAction {

    EXCLUDE("EXCLUDE", "Exclude"),
    EVALUATE("EVALUATE", "Evaluate"),
    INCLUDE("INCLUDE", "Include");

    private String fCode;
    private String fDescription;

    AmbiguousConstraintAction(String code, String description) {

        fCode =  code;
        fDescription = description;
    }

    public String getCode() {
        return fCode;
    }

    public String getDescription() {
        return fDescription;
    }

    public static String[] getDescriptions() {
        return new String[] {
                AmbiguousConstraintAction.EXCLUDE.getDescription(),
                AmbiguousConstraintAction.EVALUATE.getDescription(),
                AmbiguousConstraintAction.INCLUDE.getDescription()};
    }

    public static AmbiguousConstraintAction parse(String description) {

        try {
            AmbiguousConstraintAction result = AmbiguousConstraintAction.valueOf(description);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

}
