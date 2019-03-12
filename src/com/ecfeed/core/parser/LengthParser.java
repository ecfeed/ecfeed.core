package com.ecfeed.core.parser;

public class LengthParser {

    private int fLength = 1;

    public LengthParser(String length) throws Exception {

        if (length == null) {
            fLength = 1;
            return;
        }

        try {
            fLength = Integer.parseInt(length);
        } catch (Exception e) {
            throw new Exception("Can parse length. " + e.getMessage());
        }
    }

    public int getLength() {

        return fLength;
    }
}
