package com.ecfeed.core.parser;

public class GeneratorsNParser {

    private int fN = 2;

    public GeneratorsNParser(String nStr) throws Exception {

        if (nStr == null) {
            fN = 2;
            return;
        }

        try {
            fN = Integer.parseInt(nStr);
        } catch (Exception e) {
            throw new Exception("Can not set N. " + e.getMessage());
        }
    }

    public int getN() {

        return fN;
    }

}
