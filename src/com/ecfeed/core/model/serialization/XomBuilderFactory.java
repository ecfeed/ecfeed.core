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

public class XomBuilderFactory {

	public static XomBuilder createXomBuilder(int modelVersion, SerializatorParams serializatorParams) {

		if (modelVersion == 0) {
			return new XomBuilderVersion0(serializatorParams);
		}
		if (modelVersion == 1) {
			return new XomBuilderVersion1(serializatorParams);
		}
		if (modelVersion == 2) {
			return new XomBuilderVersion2(serializatorParams);
		}

		if (modelVersion == 3) {
			return new XomBuilderVersion3(serializatorParams);
		}

		if (modelVersion == 4) {
			return new XomBuilderVersion4(serializatorParams);
		}

		ExceptionHelper.reportRuntimeException("Invalid xom builder version.");
		return null;
	}
}
