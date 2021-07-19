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
			Element element, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings outErrorList) throws ParserException {

		// TODO PARSER
		
		// TODO move construction to constructor

		IModelParserForChoice modelParserForChoice = new ModelParserForChoice(modelChangeRegistrator);
		
		IModelParserForGlobalParameter modelParserForGlobalParameter = 
				new ModelParserForGlobalParameter(modelParserForChoice);
		
		IModelParserForMethodParameter modelParserForMethodParameter = new ModelParserForMethodParameter();		
		
		IModelParserForTestCase modelParserForTestCase = new ModelParserForTestCase();
		
		IModelParserForConstraint modelParserForConstraint = new ModelParserForConstraint();
		
		IModelParserForMethod modelParserForMethod = 
				new ModelParserForMethod(modelParserForMethodParameter, modelParserForTestCase, modelParserForConstraint);
		
		IModelParserForClass modelParserForClass = 
				new ModelParserForClass(modelParserForGlobalParameter, modelParserForMethod);

		IModelParserForRoot modelParserForRoot = 
				new ModelParserForRoot(
						getModelVersion(), 
						modelParserForGlobalParameter,
						modelParserForClass,
						modelChangeRegistrator);

		return modelParserForRoot.parseRoot(
				element, 
				outErrorList);
	}

}
