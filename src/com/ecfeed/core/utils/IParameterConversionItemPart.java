/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

public interface IParameterConversionItemPart  {

	enum ItemPartType {

		CHOICE("C", "choice"),
		LABEL("L", "label"),
		NAME("N", "name"),
		RAW("R", "raw");

		private String fCode;
		private String fDescription;

		ItemPartType(String code, String description) {
			fCode = code;
			fDescription = description;
		}

		public String getCode() {
			return fCode;
		}

		public String getDescription() {
			return fDescription;
		}

		@Override
		public String toString() {
			return fCode;
		}

		public static String convertCodeToDescription(String code) {

			ItemPartType itemTypes[] = ItemPartType.values();

			for (ItemPartType itemPartType : itemTypes) {

				if (StringHelper.isEqual(code, itemPartType.getCode())) {
					return itemPartType.getDescription();
				}
			}

			return "";
		}

		public static String convertDescriptionToCode(String description) {

			ItemPartType itemTypes[] = ItemPartType.values();

			for (ItemPartType itemPartType : itemTypes) {

				if (StringHelper.isEqual(description, itemPartType.getDescription())) {
					return itemPartType.getCode();
				}
			}

			return "";
		}

	}

	public ItemPartType getType();
	public String getStr();
	public String getDescription();
	public void setName(String name);
	public boolean isMatch(IParameterConversionItemPart otherPart); // TODO DE-NO rename to isEqual
	public Integer getTypeSortOrder(); // TODO DE-NO remove ?
	public Integer getSortOrder(); // TODO DE-NO remove ?
	public int compareTo(IParameterConversionItemPart other);
	public IParameterConversionItemPart makeClone();
}

