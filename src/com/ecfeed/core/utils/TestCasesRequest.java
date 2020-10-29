package com.ecfeed.core.utils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TestCasesRequest {

	private String fSessionId;
	private String fMethod;
	private String fModel;
	private String fExportTemplate;

	private String fUserData;

	public TestCasesRequest() {};

	@JsonGetter("sessionId")
	public String getSessionId() {
		return fSessionId;
	}

	@JsonSetter("sessionId")
	public void setSessionId(String sessionId) {
		fSessionId = sessionId;
	}
	
	@JsonGetter("method")
	public String getMethod() {
		return fMethod;
	}

	@JsonSetter("method")
	public void setMethod(String method) {
		fMethod = method;
	}

	@JsonGetter("model")
	public String getModel() {
		return fModel;
	}

	@JsonSetter("model")
	public void setModelIdentificationStr(String model) {
		fModel = model;
	}

	@JsonGetter("userData")
	public String getUserData() {
		return fUserData;
	}

	@JsonSetter("userData")
	public void setUserData(String userData) {
		fUserData = userData;
	}

	@JsonGetter("template")
	public String getExportTemplate() {
		return fExportTemplate;
	}

	@JsonSetter("template")
	public void setExportTemplate(String exportTemplate) {
		fExportTemplate = exportTemplate;
	}

}
