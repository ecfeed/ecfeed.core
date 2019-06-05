package com.ecfeed.core.parser;

import com.ecfeed.core.utils.DataSource;

public class DataSourceParser {

    private DataSource fDataSource;

    public DataSourceParser(String dataSourceStr) {

        fDataSource = DataSource.parse(dataSourceStr);
    }

    public DataSource getDataSource() {

        return fDataSource;
    }

}
