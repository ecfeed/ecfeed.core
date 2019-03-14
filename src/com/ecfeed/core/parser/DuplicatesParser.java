package com.ecfeed.core.parser;

public class DuplicatesParser {

    private final static String duplicatesTrue = "true";
    private final static String duplicatesFalse = "false";

    private boolean fDuplicates = false;

    public DuplicatesParser(String duplicatesStr) throws Exception {

        if (duplicatesStr == null) {
            fDuplicates = false;
            return;
        }

        if (duplicatesStr.equals(duplicatesTrue)) {
            fDuplicates = true;
            return;
        }

        if (duplicatesStr.equals(duplicatesFalse)) {
            fDuplicates = false;
            return;
        }

        throw new Exception("Invalid value for duplicates tag: " + duplicatesStr + ".");
    }

    public boolean isDuplicates() {

        return fDuplicates;
    }
}
