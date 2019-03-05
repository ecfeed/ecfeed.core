package com.ecfeed.core.utils;

public enum EvaluationResult {

	TRUE,
	FALSE,
	INSUFFICIENT_DATA;

	public static EvaluationResult convertFromBoolean(boolean bool) {

		if (bool == true) {
			return EvaluationResult.TRUE;
		}

		return EvaluationResult.FALSE;
	}
}
