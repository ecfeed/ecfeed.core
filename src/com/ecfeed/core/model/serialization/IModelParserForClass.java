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

import java.util.Optional;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public interface IModelParserForClass {

	public Optional<ClassNode> parseClass(
			Element classElement, RootNode parent, ListOfStrings errorList) throws ParserException;

}
