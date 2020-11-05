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

public class XomAnalyserVersion3 extends XomAnalyserWithNewNodeNames {

	public XomAnalyserVersion3(IExtLanguageManager extLanguageManager) {
		super(extLanguageManager);
	}

	@Override
	protected int getModelVersion() {
		return 3;
	}

}
