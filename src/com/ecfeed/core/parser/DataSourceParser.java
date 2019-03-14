package com.ecfeed.core.parser;

public class DataSourceParser {

    private DataSource fDataSource;

    public DataSourceParser(String dataSourceStr) throws Exception {

        fDataSource = DataSource.parse(dataSourceStr);
    }

    public DataSource getDataSource() {

        return fDataSource;
    }

}
