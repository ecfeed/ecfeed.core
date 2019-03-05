/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.Stack;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.SystemLogger;

public class ModelOperationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2841889790004375884L;
	private static Boolean fLoggingEnabledState = true;
	private static Stack<Boolean> fLoggingStates = new Stack<Boolean>();


	protected ModelOperationException(String message) {
		super(message);
		if (fLoggingEnabledState) {
			SystemLogger.logThrow(message);
		}
	}

	public static void report(String message) throws ModelOperationException {
		throw new ModelOperationException(message);
	}

	public static void pushLoggingState(boolean newLoggingEnabledState) {
		fLoggingStates.push(fLoggingEnabledState);
		fLoggingEnabledState = newLoggingEnabledState;
	}

	public static void popLoggingState() {
		if (fLoggingStates.isEmpty()) {
			ExceptionHelper.reportRuntimeException("Invalid remove of logging state.");
		}

		fLoggingEnabledState = fLoggingStates.peek();
		fLoggingStates.pop();
	}
}