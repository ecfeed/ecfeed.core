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

import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public abstract class XomAnalyser {

	protected abstract int getModelVersion();

	public XomAnalyser() {
	}

	public RootNode parseRoot(
			Element element, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings outErrorList) {

		ModelParserForRoot modelParserForRoot = 
				ModelParserHelper.createStandardModelParserForRoot(getModelVersion(), modelChangeRegistrator);

		return modelParserForRoot.parseRoot(element, outErrorList);
	}

}
