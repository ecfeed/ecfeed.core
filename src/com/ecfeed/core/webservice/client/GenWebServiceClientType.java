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

    @Override
    public String toString() {
        return fTag;
    }

    public static GenWebServiceClientType parse(String clientTypeStr) {

        if (clientTypeStr.equals(REGULAR.toString())) {
            return REGULAR;
        }

        if (clientTypeStr.equals(LOCAL_TEST_RUNNER.toString())) {
            return LOCAL_TEST_RUNNER;
        }

        if (clientTypeStr.equals(LOCAL_TEST_RAP.toString())) {
            return LOCAL_TEST_RAP;
        }

        ExceptionHelper.reportRuntimeException("Cannot convert string: " + clientTypeStr + " to client type.");
        return null;
    }
}

