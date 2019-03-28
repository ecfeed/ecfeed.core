package com.ecfeed.core.genservice.schema;

import com.ecfeed.core.genservice.util.GenServiceProtocolHelper;
import com.ecfeed.core.utils.ExceptionHelper;

import java.io.IOException;

public class MainSchemaParser {

    public static IMainSchema parse(String json) {

        IMainSchema result;

        result = parseStatus(json);
        if (result != null) {
            return result;
        }

        result = parseTestCase(json);
        if (result != null) {
            return result;
        }

        result = parseInfo(json);
        if (result != null) {
            return result;
        }

        result = parseTotalProgress(json);
        if (result != null) {
            return result;
        }

        result = parseProgress(json);
        if (result != null) {
            return result;
        }

        result = parseError(json);
        if (result != null) {
            return result;
        }

        ExceptionHelper.reportRuntimeException("Can not parse json.");
        return null;
    }

    private static IMainSchema parseStatus(String json) {

        try {
            return GenServiceProtocolHelper.parseStatus(json);
        } catch (IOException e) {
            return null;
        }
    }

    private static IMainSchema parseTestCase(String json) {

        try {
            return GenServiceProtocolHelper.parseTestCase(json);
        } catch (IOException e) {
            return null;
        }
    }

    private static IMainSchema parseInfo(String json) {

        try {
            return GenServiceProtocolHelper.parseInfo(json);
        } catch (IOException e) {
            return null;
        }
    }

    private static IMainSchema parseTotalProgress(String json) {

        try {
            return GenServiceProtocolHelper.parseTotalProgress(json);
        } catch (IOException e) {
            return null;
        }
    }

    private static IMainSchema parseProgress(String json) {

        try {
            return GenServiceProtocolHelper.parseProgress(json);
        } catch (IOException e) {
            return null;
        }
    }

    private static IMainSchema parseError(String json) {

        try {
            return GenServiceProtocolHelper.parseError(json);
        } catch (IOException e) {
            return null;
        }
    }
}
