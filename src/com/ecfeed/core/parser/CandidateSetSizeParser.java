package com.ecfeed.core.parser;

import com.ecfeed.core.utils.ExceptionHelper;

public class CandidateSetSizeParser {

    private int fCandidateSetSize = 100;

    public CandidateSetSizeParser(String candidateSetSize) {

        if (candidateSetSize == null) {
            fCandidateSetSize = 100;
            return;
        }

        try {
            fCandidateSetSize = Integer.parseInt(candidateSetSize);
        } catch (Exception e) {
            ExceptionHelper.reportRuntimeException("Can not set candidateSetSize.", e);
        }
    }

    public int getCandidateSetSize() {

        return fCandidateSetSize;
    }

}
