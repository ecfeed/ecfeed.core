package com.ecfeed.core.genservice.schema;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RequestUpdateSchema {

	private String id;
	private ArrayList<RequestUpdateErrorSchema> testReport;
	
	public RequestUpdateSchema() {
		id = "0";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<RequestUpdateErrorSchema> getTestReport() {
		return testReport;
	}

	public void setTestReport(ArrayList<RequestUpdateErrorSchema> testReport) {
		this.testReport = testReport;
	}
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		
		result.append(System.lineSeparator());
		result.append("Test report | Suite id: " + id);
		
		for (RequestUpdateErrorSchema argument : testReport) {
			result.append(System.lineSeparator());
			result.append(argument.toString());
		}
		
		return result.toString(); 
	}

}
