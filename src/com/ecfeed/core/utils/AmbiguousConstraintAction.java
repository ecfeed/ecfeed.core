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

	public static AmbiguousConstraintAction parseDescription(String description) {

		for (AmbiguousConstraintAction action : AmbiguousConstraintAction.values()) {

			if (action.getDescription().equals(description)) {
				return action;
			}
		}

		return null;
	}

	public static AmbiguousConstraintAction parseCode(String description) {

		for (AmbiguousConstraintAction action : AmbiguousConstraintAction.values()) {

			if (action.getCode().equals(description)) {
				return action;
			}
		}

		return null;
	}

}
