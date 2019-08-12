package com.ecfeed.core.parser;

import com.ecfeed.core.utils.ExceptionHelper;

public class AdaptiveParser {

    private final static String adaptiveTrue = "true";
    private final static String adaptiveFalse = "false";

    private boolean fAdaptive = false;

    public AdaptiveParser(String adaptiveStr) {

        if (adaptiveStr == null) {
            fAdaptive = false;
            return;
        }

        if (adaptiveStr.equals(adaptiveTrue)) {
            fAdaptive = true;
            return;
        }

        if (adaptiveStr.equals(adaptiveFalse)) {
            fAdaptive = false;
            return;
        }

        ExceptionHelper.reportRuntimeException("Invalid value for adaptive tag: " + adaptiveStr + ".");
    }

    public boolean isAdaptive() {

        return fAdaptive;
    }
}
