package com.ecfeed.core.genservice.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultStatusSchema implements IMainSchema {
	
	private String id;
	private String status;
	private boolean collectStats;
	
	public ResultStatusSchema() {
		id = "0";
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean getCollectStats() {
		return collectStats;
	}

	public void setCollectStats(boolean collectStats) {
		this.collectStats = collectStats;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Status: " + status);
		builder.append(id != null ? ", id: " + id : "");
		builder.append(collectStats ? ", collectStats: " + collectStats : "");
		
		return builder.toString();	
	}

}
