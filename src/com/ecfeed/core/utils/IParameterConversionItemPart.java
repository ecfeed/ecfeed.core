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

import com.ecfeed.core.model.AbstractParameterNode;

public interface IParameterConversionItemPart  {

	enum ItemPartType {

		CHOICE("C", "choice"),
		LABEL("L", "label"),
		VALUE("V", "value"),
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

	public AbstractParameterNode getParameter(); // XYX can BasicParameterNode be used ?
	public ItemPartType getType();
	public String getTypeDescription();
	public String getStr();
	public String getDescription();
	public void setName(String name);
	public Integer getTypeSortOrder();
	public Integer getSortOrder();
	public int compareTo(IParameterConversionItemPart other);
	public IParameterConversionItemPart makeClone();
}

