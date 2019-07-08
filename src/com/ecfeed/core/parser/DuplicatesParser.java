package com.ecfeed.core.parser;

import com.ecfeed.core.utils.ExceptionHelper;

public class DuplicatesParser {

    private final static String duplicatesTrue = "true";
    private final static String duplicatesFalse = "false";

    private boolean fDuplicates = false;

    public DuplicatesParser(String duplicatesStr) {

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

        ExceptionHelper.reportRuntimeException("Invalid value for duplicates tag: " + duplicatesStr + ".");
    }

    public boolean isDuplicates() {

        return fDuplicates;
    }
}
