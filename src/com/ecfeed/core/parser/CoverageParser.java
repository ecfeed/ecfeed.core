package com.ecfeed.core.parser;

import com.ecfeed.core.utils.ExceptionHelper;

public class CoverageParser {

    private int fCoverage = 100;

    public CoverageParser(String coverage) {

        if (coverage == null) {
            fCoverage = 100;
            return;
        }

        try {
            fCoverage = Integer.parseInt(coverage);
        } catch (Exception e) {
            ExceptionHelper.reportRuntimeException("Can not set coverage. " + e.getMessage());
        }
    }

    public int getCoverage() {

        return fCoverage;
    }
}
