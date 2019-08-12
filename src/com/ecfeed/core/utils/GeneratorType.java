package com.ecfeed.core.utils;


public enum GeneratorType {

    N_WISE("genNWise"),
    CARTESIAN("genCartesian"),
    RANDOM("genRandom");

    private final String fType;

    GeneratorType(String type) {

        fType = type;
    }

    @Override
    public String toString() {
        return fType;
    }

    public static GeneratorType parse(String type) {

        if (type.equals(N_WISE.toString())) {
            return N_WISE;
        }

        if (type.equals(CARTESIAN.toString())) {
            return CARTESIAN;
        }

        if (type.equals(RANDOM.toString())) {
            return RANDOM;
        }

        ExceptionHelper.reportRuntimeException("Can not convert string: " + type + " to generator type.");
        return null;
    }

}
