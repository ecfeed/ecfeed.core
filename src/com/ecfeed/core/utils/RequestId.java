package com.ecfeed.core.utils;

public class RequestId {

    private final String fId;

    public RequestId(String id) {
        fId = id;
    }

    @Override
    public String toString() {
        return fId;
    }
}