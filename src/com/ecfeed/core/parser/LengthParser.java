package com.ecfeed.core.parser;

import com.ecfeed.core.utils.ExceptionHelper;

public class LengthParser {

    private int fLength = 1;

    public LengthParser(String length) {

        if (length == null) {
            fLength = 1;
            return;
        }

        try {
            fLength = Integer.parseInt(length);
        } catch (Exception e) {
            ExceptionHelper.reportRuntimeException("Can parse length.", e);
        }
    }

    public int getLength() {

        return fLength;
    }
}
