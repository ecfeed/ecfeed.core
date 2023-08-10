package com.ecfeed.core.parser.model;

public class DataTypeFactory {
	
	public static DataType create(boolean unified) {
		
		return DataTypeDefault.create(unified);
	}

}
