package com.ecfeed.core.genservice.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RequestUpdateErrorSchema {

	private String id;
	private String errorClass;
	private String errorMessage;
	
	public RequestUpdateErrorSchema() {
		id = "0";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getErrorClass() {
		return errorClass;
	}

	public void setErrorClass(String errorClass) {
		this.errorClass = errorClass;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	@Override
	public String toString() {
		return "    Class: " + errorClass.substring(errorClass.lastIndexOf(".") + 1) + ", Message: " + errorMessage + ", Test id: " + id;
	}

}
