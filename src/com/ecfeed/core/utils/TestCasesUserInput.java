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

	private String fCoverage;
	private String fN;
	private String fDuplicates;
	private String fAdaptive;
	private String fLength;
	private String fCandidates;

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

	@JsonGetter("coverage")
	public String getCoverage() {

		return fCoverage;
	}

	@JsonSetter("coverage")
	public void setCoverage(String coverage) {

		fCoverage = coverage;
	}

	public void setCoverage(int coverage) {

		String coverageStr = Integer.toString(coverage);
		setCoverage(coverageStr);
	}

	@JsonGetter("n")
	public String getN() {

		return fN;
	}

	@JsonSetter("n")
	public void setN(String n) {

		fN = n;
	}

	public void setN(int n) {

		String nStr = Integer.toString(n);
		setN(nStr);
	}

	@JsonGetter("duplicates")
	public String getDuplicates() {

		return fDuplicates;
	}

	@JsonSetter("duplicates")
	public void setDuplicates(String duplicates) {

		fDuplicates = duplicates;
	}

	public void setDuplicates(boolean duplicates) {

		if (duplicates) {
			setDuplicates("true"); // TODO - magic string
		} else {
			setDuplicates("false"); // TODO - magic string
		}
	}

	@JsonGetter("adaptive")
	public String getAdaptive() {

		return fAdaptive;
	}

	@JsonSetter("adaptive")
	public void setAdaptive(String adaptive) {

		fAdaptive = adaptive;
	}

	public void setAdaptive(boolean adaptive) {

		if (adaptive) {
			setAdaptive("true"); // TODO - magic string
		} else {
			setAdaptive("false"); // TODO - magic string
		}
	}

	@JsonGetter("length")
	public String getLength() {

		return fLength;
	}

	@JsonSetter("length")
	public void setLength(String length) {

		fLength = length;
	}

	public void setLength(int length) {

		String str = Integer.toString(length);
		setLength(str);
	}

	@JsonGetter("candidates")
	public String getCandidates() {

		return fCandidates;
	}

	@JsonSetter("candidates")
	public void setCandidates(String candidates) { // TODO - rename to setCandidateSetSize

		fCandidates = candidates;
	}

	public void setCandidates(int candidateSetSize) {

		String str = Integer.toString(candidateSetSize);
		setCandidates(str);
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

	@SuppressWarnings("unchecked")
	@JsonSetter("properties")
	public void setProperties(Object properties)
	{
		fProperties = (Map<String,String>) properties;
	}

}
