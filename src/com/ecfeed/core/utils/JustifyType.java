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

public enum JustifyType {
	LEFT,
	RIGHT,
	CENTER,
	ERROR;

	public static JustifyType convertFromString(String string) {

		if (string.equals("LEFT")) {
			return JustifyType.LEFT;
		}

		if (string.equals("RIGHT")) {
			return JustifyType.RIGHT;
		}		

		if (string.equals("CENTER")) {
			return JustifyType.CENTER;
		}		

		return JustifyType.ERROR;
	}

	public static String convertToString(JustifyType justifyType) {

		switch(justifyType) {
		case LEFT:
			return "LEFT";
		case RIGHT:
			return "RIGHT";
		case CENTER:
			return "CENTER";
		case ERROR:
			return "ERROR";
		default:
			return null;
		}
	}
}
