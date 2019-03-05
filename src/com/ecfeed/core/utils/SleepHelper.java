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

public class SleepHelper {

	public static void sleep(int seconds) {
		sleep((float)seconds);
	}

	public static void sleep(float seconds) {

		try {
			Thread.sleep((long)(1000 * seconds));
		} catch (InterruptedException e) {

		}		
	}

}
