/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.serialization;

import com.ecfeed.core.utils.ExceptionHelper;

public class XomAnalyserFactory {

	public static XomAnalyser createXomAnalyser(int version) {

		if (version == 0) {
			ExceptionHelper.reportRuntimeException("Version not supported");
		}

		if (version == 1) {
			return new XomAnalyserVersion1();
		}

		if (version == 2) {
			return new XomAnalyserVersion2();
		}

		if (version == 3) {
			return new XomAnalyserVersion3();
		}

		if (version == 4) {
			return new XomAnalyserVersion4();
		}

		ExceptionHelper.reportRuntimeException("Invalid xom analyser version.");
		return null;
	}
}
