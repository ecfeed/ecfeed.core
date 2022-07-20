package com.ecfeed.core.parser.model;

public class DataTypeDefault implements DataType {

	private boolean dataBoolean = true;
	private boolean dataChar = true;
	private boolean dataDouble = true;
	private boolean dataLong = true;
	private boolean dataInteger = true;

	public static DataType create() {
		
		return new DataTypeDefault();
	}
	
	private DataTypeDefault() {
	}
	
	@Override
	public void feed(String data) {
		
		checkChar(data);
		checkBoolean(data);
		checkDouble(data);
		checkInteger(data);
		checkLong(data);
	}
	
	private void checkChar(String data) {
		
		if (!this.dataChar) {
			return;
		}
		
		if (data.length() == 1) {
			this.dataBoolean = false;
			
			return;
		}
		
		this.dataChar = false;
	}
	
	private void checkBoolean(String data) {
	
		if (!this.dataBoolean) {
			return;
		}
		
		if (data.equalsIgnoreCase("true") || data.equalsIgnoreCase("false")) {
			this.dataChar = false;
			this.dataDouble = false;
			this.dataLong = false;
			this.dataInteger = false;
			
			return;
		}
		
		this.dataBoolean = false;
	}
	
	private void checkDouble(String data) {
		
		if (!this.dataDouble) {
			return;
		}
		
		try {
			Double.parseDouble(data);
		} catch (NumberFormatException e) {
			this.dataDouble = false;
			this.dataInteger = false;
			this.dataLong = false;
		}
	}

	private void checkLong(String data) {
		
		if (!this.dataLong) {
			return;
		}
		
		try {
			Long.parseLong(data);
		} catch (NumberFormatException e) {
			this.dataLong = false;
			this.dataInteger = false;
		}
	}

	private void checkInteger(String data) {
		
		if (!this.dataInteger) {
			return;
		}
		
		try {
			Integer.parseInt(data);
		} catch (NumberFormatException e) {
			this.dataInteger = false;
		}
	}
	
	@Override
	public String determine() {
		
		if (this.dataBoolean) {
			return "boolean";
		}
		
		if (this.dataChar) {
			return "char";
		}
		
		if (this.dataInteger) {
			return "int";
		}
		
		if (this.dataLong) {
			return "long";
		}
		
		return "String";
	}

}
