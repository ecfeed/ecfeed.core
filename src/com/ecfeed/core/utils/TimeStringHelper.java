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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class TimeStringHelper  {

	public static String getTimeLongString() {

		Date currentDate = new Date();

		StringBuilder builder = new StringBuilder();

		builder.append(new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(currentDate));
		builder.append("-");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		long milliseconds = calendar.get(Calendar.MILLISECOND);
		builder.append(milliseconds);

		return builder.toString(); 
	}
	
	public static String getQuasiUniqueString() {

		StringBuilder builder = new StringBuilder();

		builder.append(getTimeLongString());

		builder.append("-");
		Random rand = new Random();
		String randomStr = Integer.toString(rand.nextInt(99999));
		randomStr = StringHelper.insertZerosToLength(randomStr, 5);
		builder.append(randomStr);

		return builder.toString(); 
	}

}
