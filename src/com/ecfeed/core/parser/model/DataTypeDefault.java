package com.ecfeed.core.parser.model;

public class DataTypeDefault implements DataType {

	private boolean dataBoolean = true;

	private boolean dataChar = true;

	private boolean dataFloat = true;
	private boolean dataDouble = true;

	private boolean dataByte = true;
	private boolean dataShort = true;
	private boolean dataInteger = true;
	private boolean dataLong = true;


	public static DataType create(boolean unified) {

		return new DataTypeDefault(unified);
	}
	
	private DataTypeDefault(boolean unified) {

		if (unified) {
			this.dataByte = false;
			this.dataShort = false;
			this.dataFloat = false;
		}
	}
	
	@Override
	public void feed(String data) {
		
		checkChar(data);
		checkBoolean(data);
		checkFloat(data);
		checkDouble(data);
		checkByte(data);
		checkShort(data);
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
			this.dataFloat = false;
			this.dataDouble = false;
			this.dataByte = false;
			this.dataShort = false;
			this.dataInteger = false;
			this.dataLong = false;

			return;
		}
		
		this.dataBoolean = false;
	}

	private void checkFloat(String data) {

		if (!this.dataFloat) {
			return;
		}

		try {
			if (Float.parseFloat(data) != Double.parseDouble(data)) {
				this.dataFloat = false;
			}
		} catch (NumberFormatException e) {
			this.dataFloat = false;
			this.dataByte = false;
			this.dataShort = false;
			this.dataInteger = false;
			this.dataLong = false;
		}
	}

	private void checkDouble(String data) {
		
		if (!this.dataDouble) {
			return;
		}
		
		try {
			Double.parseDouble(data);
		} catch (NumberFormatException e) {
			this.dataDouble = false;
			this.dataByte = false;
			this.dataShort = false;
			this.dataInteger = false;
			this.dataLong = false;
		}
	}

	private void checkByte(String data) {

		if (!this.dataByte) {
			return;
		}

		try {
			Byte.parseByte(data);
		} catch (NumberFormatException e) {
			this.dataByte = false;
		}
	}

	private void checkShort(String data) {

		if (!this.dataShort) {
			return;
		}

		try {
			Short.parseShort(data);
		} catch (NumberFormatException e) {
			this.dataShort = false;
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
	
	@Override
	public String determine() {
		
		if (this.dataBoolean) {
			return "boolean";
		}
		
		if (this.dataChar) {
			return "char";
		}

		if (this.dataByte) {
			return "byte";
		}

		if (this.dataShort) {
			return "short";
		}

		if (this.dataInteger) {
			return "int";
		}
		
		if (this.dataLong) {
			return "long";
		}

		if (this.dataFloat) {
			return "float";
		}

		if (this.dataDouble) {
			return "double";
		}
		
		return "String";
	}

}
