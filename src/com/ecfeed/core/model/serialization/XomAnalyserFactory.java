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

import com.ecfeed.core.utils.IExtLanguageManager;

public class XomAnalyserFactory {

	public static XomAnalyser createXomAnalyser(int version, IExtLanguageManager extLanguageManager) {

		if (version == 0) {
			return new XomAnalyserVersion0(extLanguageManager);
		}

		if (version == 1) {
			return new XomAnalyserVersion1(extLanguageManager);
		}

		return new XomAnalyserVersion3(extLanguageManager);
	}
}
