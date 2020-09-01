package com.ecfeed.core.JavaAPI;

public class DataSource {

	int STATIC_DATA = 0;
	int NWISE = 1;
	int CARTESIAN = 2;
	int RANDOM = 3;
	
	String to_url_param(int datasource) {
		
		String data_source = "No data source";
		
		if(datasource == this.STATIC_DATA) {
			data_source = "static";
		}
		else if (datasource == this.NWISE) {
			data_source = "genNWise";
		}
		else if (datasource == this.CARTESIAN) {
			data_source = "genCartesian";
		}
		else if (datasource == this.RANDOM) {
			data_source = "genRandom";
		}
		return data_source;
	}

}
