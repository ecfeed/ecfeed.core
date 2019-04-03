package com.ecfeed.core.parser;

import java.util.List;

public class TestSuitesParser { // TODO - REUSE IN JUNIT5

    private static final String specialValueAllSuites = "ALL";

    private List<String> fTestSuiteNames = null;
    private boolean fIsSpecialValueAllSuites = false;

    @SuppressWarnings("unchecked")
	public TestSuitesParser(Object testSuitesObject) throws Exception {

        if (testSuitesObject == null) {
            setSpecialValueAllSuitesToTrue();
            return;
        }

        if (testSuitesObject instanceof String) {
            setTestSuitesFromString((String) testSuitesObject);
            return;
        }

        try {
            fTestSuiteNames = (List<String>) testSuitesObject;
        } catch (Exception e) {
            throw new Exception("Invalid type of test suites object. Can not convert to list of names.");
        }

        if (fTestSuiteNames.size() == 0) {
            throw new Exception("List of test suite names should not be empty.");
        }
    }

    public boolean isAllTestSuites() {

        return fIsSpecialValueAllSuites;
    }

    public List<String> getTestSuiteNames() {

        return fTestSuiteNames;
    }

    private void setTestSuitesFromString(String testSuitesObject) throws Exception {

        String testSuitesString = testSuitesObject;

        if (testSuitesString.equals(specialValueAllSuites)) {

            setSpecialValueAllSuitesToTrue();
            return;
        }

        throw new Exception("Invalid special value for test suites: " + testSuitesString);
    }

    private void setSpecialValueAllSuitesToTrue() {

        fIsSpecialValueAllSuites = true;
        fTestSuiteNames = null;
    }

}
