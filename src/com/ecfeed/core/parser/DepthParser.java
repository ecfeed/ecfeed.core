package com.ecfeed.core.parser;

import com.ecfeed.core.utils.ExceptionHelper;

public class DepthParser {

    private int fDepth = -1;

    public DepthParser(String depth) {

        if (depth == null) {
            fDepth = -1;
            return;
        }

        try {
            fDepth = Integer.parseInt(depth);
        } catch (Exception e) {
            ExceptionHelper.reportRuntimeException("Can not set depth. " + e.getMessage());
        }
    }

    public int getDepth() {

        return fDepth;
    }
}
