package com.ecfeed.core.parser;

public class CandidateSetSizeParser {

    private int fCandidateSetSize = 100;

    public CandidateSetSizeParser(String candidateSetSize) throws Exception {

        if (candidateSetSize == null) {
            fCandidateSetSize = 100;
            return;
        }

        try {
            fCandidateSetSize = Integer.parseInt(candidateSetSize);
        } catch (Exception e) {
            throw new Exception("Can not set candidateSetSize. " + e.getMessage());
        }
    }

    public int getCandidateSetSize() {

        return fCandidateSetSize;
    }

}
