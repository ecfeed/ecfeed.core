package com.ecfeed.core.parser;

public class CoverageParser {

    private int fCoverage = 100;

    public CoverageParser(String coverage) throws Exception {

        if (coverage == null) {
            fCoverage = 100;
            return;
        }

        try {
            fCoverage = Integer.parseInt(coverage);
        } catch (Exception e) {
            throw new Exception("Can not set coverage. " + e.getMessage());
        }
    }

    public int getCoverage() {

        return fCoverage;
    }
}
