package com.ecfeed.core.parser.model.load;

public class DataTypeFactory {
	
	public static DataType create(boolean unified) {
		
		return DataTypeDefault.create(unified);
	}

}
