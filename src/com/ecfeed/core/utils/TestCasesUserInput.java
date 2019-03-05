package com.ecfeed.core.utils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter;

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
	private String fDepth;
	private String fLength;
	private String fCandidates;
	private Object fTestSuites;
	private Object fConstraints;
	private Object fChoices;

	public TestCasesUserInput() {}

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

	@JsonGetter("N")
	public String getN() {
		return fN;
	}

	@JsonSetter("N")
	public void setN(String n) {
		fN = n;
	}

	@JsonGetter("duplicates")
	public String getDuplicates() {
		return fDuplicates;
	}

	@JsonSetter("duplicates")
	public void setDuplicates(String duplicates) {
		fDuplicates = duplicates;
	}

	@JsonGetter("depth")
	public String getDepth() {
		return fDepth;
	}

	@JsonSetter("depth")
	public void setDepth(String depth) {
		fDepth = depth;
	}

	@JsonGetter("length")
	public String getLength() {
		return fLength;
	}

	@JsonSetter("length")
	public void setLength(String length) {
		fLength = length;
	}

	@JsonGetter("candidates")
	public String getCandidates() {
		return fCandidates;
	}

	@JsonSetter("candidates")
	public void setCandidates(String candidates) {
		fCandidates = candidates;
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

	@JsonGetter("choices")
	public Object getChoices() {
		return fChoices;
	}

	@JsonSetter("choices")
	public void setChoices(Object choices) {
		fChoices = choices;
	}

}
