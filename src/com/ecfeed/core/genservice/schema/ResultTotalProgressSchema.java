package com.ecfeed.core.genservice.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultTotalProgressSchema implements IMainSchema {

	private int totalProgress;
	
	public ResultTotalProgressSchema() {}
	
	public int getTotalProgress() {
		return totalProgress;
	}

	public void setTotalProgress(int totalProgress) {
		this.totalProgress = totalProgress;
	}

	@Override
	public String toString() {
		return "Total progress: " + totalProgress;
	}                   
	
}
