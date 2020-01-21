package com.ecfeed.core.provider;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.TestCase;

public class TCAccumulatingProcessor implements ITCProcessor {

	List<TestCase> fTestCases;

	public TCAccumulatingProcessor() {
		fTestCases = new ArrayList<>();
	}

	@Override
	public void processTestCase(TestCase testCase) {

		fTestCases.add(testCase);
	}

	@Override
	public int getGeneratedDataSize() {

		return fTestCases.size();
	}

	public List<TestCase> getTestCases() {

		return fTestCases;
	}

}

