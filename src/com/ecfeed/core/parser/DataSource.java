package com.ecfeed.core.parser;

public enum DataSource {

    STATIC("static"),
    GEN_N_WISE("genNWise"),
    GEN_CARTESIAN("genCartesian"),
    GEN_ADAPTIVE_RANDOM("genAdaptiveRandom"),
    GEN_RANDOM("genRandom");

    private final String fDataSource;

    DataSource(String dataSource) {

        fDataSource = dataSource;
    }

    @Override
    public String toString() {
        return fDataSource;
    }

    public static DataSource parse(String dataSourceStr) throws Exception {

        if (dataSourceStr.equals(STATIC.toString())) {
            return STATIC;
        }

        if (dataSourceStr.equals(GEN_N_WISE.toString())) {
            return GEN_N_WISE;
        }

        if (dataSourceStr.equals(GEN_CARTESIAN.toString())) {
            return GEN_CARTESIAN;
        }

        if (dataSourceStr.equals(GEN_ADAPTIVE_RANDOM.toString())) {
            return GEN_ADAPTIVE_RANDOM;
        }

        if (dataSourceStr.equals(GEN_RANDOM.toString())) {
            return GEN_RANDOM;
        }

        throw new Exception("Can not convert string: " + dataSourceStr + " to data source." );
    }

}
