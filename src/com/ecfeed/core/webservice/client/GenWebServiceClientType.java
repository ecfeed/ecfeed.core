package com.ecfeed.core.webservice.client;


import com.ecfeed.core.utils.ExceptionHelper;

public enum GenWebServiceClientType {

    REGULAR("regular"),
    LOCAL_TEST_RUNNER("localTestRunner"),
    LOCAL_TEST_RAP("localTestRap");

    private String fTag;

    GenWebServiceClientType(String tag) {
        fTag = tag;
    }

    public String getDescription() {
        return fTag;
    }

    public static GenWebServiceClientType parse(String clientTypeStr) {

        if (clientTypeStr.equalsIgnoreCase(REGULAR.toString())) {
            return REGULAR;
        }

        if (clientTypeStr.equalsIgnoreCase(LOCAL_TEST_RUNNER.getDescription())) {
            return LOCAL_TEST_RUNNER;
        }

        if (clientTypeStr.equalsIgnoreCase(LOCAL_TEST_RAP.getDescription())) {
            return LOCAL_TEST_RAP;
        }

        ExceptionHelper.reportRuntimeException("Cannot convert string: " + clientTypeStr + " to client type.");
        return null;
    }
}

