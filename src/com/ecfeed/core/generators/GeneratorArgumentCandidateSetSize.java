package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;

public class GeneratorArgumentCandidateSetSize extends GeneratorArgumentInteger {

    public GeneratorArgumentCandidateSetSize(int candidateSetSize) throws GeneratorException {
        super(new GeneratorParameterCandidateSetSize().getName(), candidateSetSize);
    }
            
}
