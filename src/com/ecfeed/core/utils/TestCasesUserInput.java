package com.ecfeed.core.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(Include.NON_NULL)
public class TestCasesUserInput {

	private String fDataSource;
	private String fMethod;
	private String fSuiteSize;

	private Map<String, String> fProperties;

	// takes list of test suites or List<String> special string ALL
	private Object fTestSuites;
	
	 // takes list of constraint names List<String> 
	// or special Strings ALL, NONE
	private Object fConstraints;
	
	// takes special strings ALL, NONE
	// or map of entries of type Map<String, List<String>> or 
	// each entry consists of method's parameter name and list of choices
	private Object fChoices; 

	public TestCasesUserInput()
	{
		fProperties = new HashMap<>();
	}

	@JsonGetter("dataSource")
	public String getDataSource() {
		
		return fDataSource;
	}

	@JsonSetter("dataSource")
	public void setDataSource(String dataSource) {
		
		fDataSource = dataSource;
	}

	@JsonGetter("method")
	public String getMethod() {
		
		return fMethod;
	}

	@JsonSetter("method")
	public void setMethod(String method) {
		
		fMethod = method;
	}

	@JsonGetter("suiteSize")
	public String getSuiteSize() {
		
		return fSuiteSize;
	}

	@JsonSetter("suiteSize")
	public void setSuiteSize(String suiteSize) {
		
		fSuiteSize = suiteSize;
	}


	@JsonGetter("testSuites")
	public Object getTestSuites() {
		
		return fTestSuites;
	}

	@JsonSetter("testSuites")
	public void setTestSuites(Object testSuites) {
		
		fTestSuites = testSuites;
	}

	@JsonGetter("constraints")
	public Object getConstraints() {
		
		return fConstraints;
	}

	@JsonSetter("constraints")
	public void setConstraints(Object constraints) {
		
		fConstraints = constraints;
	}
	
	public void setAllConstraints() {
		
		setConstraints("ALL"); // TODO - magic string
	}
	
	public void setNoConstraints() {
		
		setConstraints("NONE"); // TODO - magic string
	}	
	
	public void setConstraints(List<String> constraintNames) {
		
		fConstraints = constraintNames;
	}

	@JsonGetter("choices")
	public Object getChoices() {
		
		return fChoices;
	}

	@JsonSetter("choices")
	public void setChoices(Object choices) {
		
		fChoices = choices;
	}


	@JsonAnyGetter
	public Map<String, String> getProperties() {
		return fProperties;
	}

	@JsonAnySetter
	public void setProperties(String key, String value)
	{
		fProperties.put(key, value);
	}

}
