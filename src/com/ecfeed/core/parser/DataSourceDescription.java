package com.ecfeed.core.parser;

import com.ecfeed.core.utils.DataSource;

public class DataSourceDescription {

    private DataSource fDataSource;

    public DataSourceDescription(String dataSourceStr) {

        fDataSource = DataSource.parse(dataSourceStr);
    }

    public DataSource getDataSource() {

        return fDataSource;
    }

}
