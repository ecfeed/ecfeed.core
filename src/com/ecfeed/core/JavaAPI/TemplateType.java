package com.ecfeed.core.JavaAPI;

public class TemplateType {
	
	int CSV = 1;
	int XML = 2;
	int Gherkin =3;
	int JSON =4;
	
	int getCSV() {
		return this.CSV;
	}
	
	int getXML() {
		return this.XML;
	}	

	int getGherkin() {
		return this.Gherkin;
	}
	
	int getJSON() {
		return this.JSON;
	}

}
