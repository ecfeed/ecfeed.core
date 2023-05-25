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

import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public interface IModelParserForRoot {

	public RootNode parseRoot(
			Element element, ListOfStrings outErrorList);

}
