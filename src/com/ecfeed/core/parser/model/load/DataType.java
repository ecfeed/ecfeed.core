package com.ecfeed.core.parser.model.load;

public interface DataType {

	void feed(String data);
	
    String determine();
}
