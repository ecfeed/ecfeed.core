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

import com.ecfeed.core.utils.SystemLogger;

public class ModelOperationException extends Exception { // TODO SIMPLE-VIEW - delete ? we have to catch every exception - not only ModelOperationException 

	/**
	 * 
	 */
	private static final long serialVersionUID = -2841889790004375884L;


	protected ModelOperationException(String message) {

		super(message);
		SystemLogger.logThrow(message);
	}

	public static void report(String message) throws ModelOperationException {

		if (message == null) {
			message = "Unknown error.";
		}

		throw new ModelOperationException(message);
	}

}