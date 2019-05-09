package com.ecfeed.core.parser;

import com.ecfeed.core.utils.ExceptionHelper;

public class GeneratorsNParser {

    private int fN = 2;

    public GeneratorsNParser(String nStr) {

        if (nStr == null) {
            fN = 2;
            return;
        }

        try {
            fN = Integer.parseInt(nStr);
        } catch (Exception e) {
            ExceptionHelper.reportRuntimeException("Can not set N. " + e.getMessage());
        }
    }

    public int getN() {

        return fN;
    }

}
