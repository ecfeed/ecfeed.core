package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;

public class GeneratorArgumentCandidateSetSize extends AbstractGeneratorArgument {

    Integer fCandidateSetSize;

    public GeneratorArgumentCandidateSetSize(int candidateSetSize) throws GeneratorException {
        super(new GeneratorParameterCandidateSetSize().getName());
        fCandidateSetSize = candidateSetSize;
    }
            
    @Override
    public Integer getValue() {
        return fCandidateSetSize;
    }
}
