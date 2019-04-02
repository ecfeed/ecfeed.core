package com.ecfeed.core.generators;

public class GeneratorParameterCandidateSetSize extends GeneratorParameterInteger {

	private static final String CANDIDATE_SET_SIZE_PARAMETER_NAME = "Candidate set size";
	private static final int DEFAULT_CANDIDATE_SET_SIZE_PARAMETER_VALUE = 100;

	public GeneratorParameterCandidateSetSize() {

		super(CANDIDATE_SET_SIZE_PARAMETER_NAME,false, DEFAULT_CANDIDATE_SET_SIZE_PARAMETER_VALUE, 0, Integer.MAX_VALUE);
	}
}
