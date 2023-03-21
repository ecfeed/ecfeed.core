package com.ecfeed.core.parser.model;

public interface DataType {

	void feed(String data);
	
    String determine();
}
