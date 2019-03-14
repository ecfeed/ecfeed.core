package com.ecfeed.core.generators;


public enum GeneratorType {

    N_WISE("genNWise"),
    CARTESIAN("genCartesian"),
    ADAPTIVE_RANDOM("genAdaptiveRandom"),
    RANDOM("genRandom");

    private final String fType;

    GeneratorType(String type) {

        fType = type;
    }

    @Override
    public String toString() {
        return fType;
    }

    public static GeneratorType parse(String type) throws Exception {

        if (type.equals(N_WISE.toString())) {
            return N_WISE;
        }

        if (type.equals(CARTESIAN.toString())) {
            return CARTESIAN;
        }

        if (type.equals(ADAPTIVE_RANDOM.toString())) {
            return ADAPTIVE_RANDOM;
        }

        if (type.equals(RANDOM.toString())) {
            return RANDOM;
        }

        throw new Exception("Can not convert string: " + type + " to generator type.");
    }

}
