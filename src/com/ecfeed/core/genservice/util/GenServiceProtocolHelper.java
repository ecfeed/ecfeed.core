package com.ecfeed.core.genservice.util;

import com.ecfeed.core.genservice.schema.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class GenServiceProtocolHelper {

    public static String TAG_BEG_DATA = "BEG_DATA";
    public static String TAG_BEG_CHUNK = "BEG_CHUNK";
    public static String TAG_END_DATA = "END_DATA";
    public static String TAG_END_CHUNK = "END_CHUNK";
    public static String TAG_ACKNOWLEDGED = "ACKNOWLEDGED";

    public static ResultStatusSchema parseStatus(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.reader().forType(ResultStatusSchema.class).readValue(json);
    }

    public static ResultTestCaseSchema parseTestCase(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.reader().forType(ResultTestCaseSchema.class).readValue(json);
    }

    public static ResultInfoSchema parseInfo(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.reader().forType(ResultInfoSchema.class).readValue(json);
    }

    public static ResultTotalProgressSchema parseTotalProgress(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.reader().forType(ResultTotalProgressSchema.class).readValue(json);
    }

    public static ResultProgressSchema parseProgress(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.reader().forType(ResultProgressSchema.class).readValue(json);
    }

    public static ResultErrorSchema parseError(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.reader().forType(ResultErrorSchema.class).readValue(json);
    }

    public static boolean isTagBegData(String tag) {

        if (tag.equals(TAG_BEG_DATA)) {
            return true;
        }

        return false;
    }

    public static boolean isTagBegChunk(String tag) {

        if (tag.equals(TAG_BEG_CHUNK)) {
            return true;
        }

        return false;
    }

    public static boolean isTagEndChunk(String tag) {

        if (tag.equals(TAG_END_CHUNK)) {
            return true;
        }

        return false;
    }

    public static boolean isTagEndData(String tag) {

        if (tag.equals(TAG_END_DATA)) {
            return true;
        }

        return false;
    }

}
