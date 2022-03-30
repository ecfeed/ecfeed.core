package com.ecfeed.core.utils;


public enum DataSource {

    STATIC("static"),
    GEN_N_WISE(GeneratorType.N_WISE.toString()),
    GEN_CARTESIAN(GeneratorType.CARTESIAN.toString()),
    GEN_RANDOM(GeneratorType.RANDOM.toString());

    private final String fDataSource;

    DataSource(String dataSource) {

        fDataSource = dataSource;
    }

    @Override
    public String toString() {
        return fDataSource;
    }

    public static DataSource parse(String dataSourceStr) {

        if (dataSourceStr.equals(STATIC.toString())) {
            return STATIC;
        }

        if (dataSourceStr.equals(GEN_N_WISE.toString())) {
            return GEN_N_WISE;
        }

        if (dataSourceStr.equals(GEN_CARTESIAN.toString())) {
            return GEN_CARTESIAN;
        }

        if (dataSourceStr.equals(GEN_RANDOM.toString())) {
            return GEN_RANDOM;
        }

        ExceptionHelper.reportClientException("Can not convert string: " + dataSourceStr + " to data source." );
        return null;
    }

    public GeneratorType toGeneratorType()
    {
        switch(this)
        {
            case GEN_CARTESIAN:
                return GeneratorType.CARTESIAN;
            case GEN_N_WISE:
                return GeneratorType.N_WISE;
            case GEN_RANDOM:
                return GeneratorType.RANDOM;
            default:
                ExceptionHelper.reportRuntimeException("Invalid generator name");
                return null;
        }
    }

}
