package com.ecfeed.core.webservice.client;


import com.ecfeed.core.utils.ExceptionHelper;

public enum GenWebServiceClientType {

    REGULAR("regular"),
    LOCAL_TEST_RUNNER("localTestRunner"),
    LOCAL_TEST_RUNNER_TEAM("localTestRunnerTeam"),
    LOCAL_TEST_RAP("localTestRap"),
    LOCAL_TEST_RAP_TEAM("localTestRapTeam");

    private String fTag;

    GenWebServiceClientType(String tag) {
        fTag = tag;
    }

    public String getDescription() {
        return fTag;
    }

    public static GenWebServiceClientType parse(String clientTypeStr) {

        if (clientTypeStr.equals(REGULAR.getDescription())) {
            return REGULAR;
        }

        if (clientTypeStr.equals(LOCAL_TEST_RUNNER.getDescription())) {
            return LOCAL_TEST_RUNNER;
        }

        if (clientTypeStr.equals(LOCAL_TEST_RUNNER_TEAM.getDescription())) {
            return LOCAL_TEST_RUNNER_TEAM;
        }

        if (clientTypeStr.equals(LOCAL_TEST_RAP.getDescription())) {
            return LOCAL_TEST_RAP;
        }

        if (clientTypeStr.equals(LOCAL_TEST_RAP_TEAM.getDescription())) {
            return LOCAL_TEST_RAP_TEAM;
        }

        ExceptionHelper.reportRuntimeException("Cannot convert string: " + clientTypeStr + " to client type.");
        return null;
    }
}

