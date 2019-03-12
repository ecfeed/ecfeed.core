package com.ecfeed.core.parser;

public class DepthParser {

    private int fDepth = -1;

    public DepthParser(String depth) throws Exception {

        if (depth == null) {
            fDepth = -1;
            return;
        }

        try {
            fDepth = Integer.parseInt(depth);
        } catch (Exception e) {
            throw new Exception("Can not set depth. " + e.getMessage());
        }
    }

    public int getDepth() {

        return fDepth;
    }
}
