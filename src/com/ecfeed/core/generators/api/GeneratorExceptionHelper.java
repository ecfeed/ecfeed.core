package com.ecfeed.core.generators.api;

import com.ecfeed.core.utils.ExceptionHelper;

public class GeneratorExceptionHelper {
    public static void reportException(String s) {

        ExceptionHelper.reportClientException(s);
    }
}
