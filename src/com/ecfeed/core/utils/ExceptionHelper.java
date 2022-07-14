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

import com.ecfeed.core.exception.ClientException;

public class ExceptionHelper {

	public static void reportClientException(String message) {

		throw new ClientException(message);
	}

	public static void reportClientException(String message, Throwable e) {

		throw new ClientException(message, e);
	}

	public static void reportRuntimeException(String message) {

		throw new RuntimeException(message);
	}

	public static void reportRuntimeException(String message, Exception e) {

		throw new RuntimeException(message, e);
	}

	public static void reportRuntimeException(Exception e) {

		throw new RuntimeException(e);
	}

	public static void reportRuntimeExceptionCanNotCreateObject() {

		ExceptionHelper.reportRuntimeException("Can not create object.");
	}
}
