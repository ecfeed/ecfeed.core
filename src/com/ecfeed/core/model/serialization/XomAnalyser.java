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

import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Element;

public abstract class XomAnalyser {

	// TODO remove protected methods
	protected abstract int getModelVersion();
	protected abstract String getChoiceNodeName();
	protected abstract String getChoiceAttributeName();
	protected abstract String getStatementChoiceAttributeName();
	protected abstract String getParameterNodeName();
	protected abstract String getStatementParameterAttributeName();
	protected abstract ConstraintType getConstraintType(Element element, ListOfStrings errorList) throws ParserException;

	public XomAnalyser() {
	}

	public RootNode parseRoot(
			Element element, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings outErrorList) throws ParserException {

		// TODO move construction to constructor

		IModelParserForClass modelParserForClass = new ModelParserForClass();

		IModelParserForRoot modelParserForRoot = 
				new ModelParserForRoot(
						getModelVersion(), 
						modelParserForClass,
						modelChangeRegistrator);

		return modelParserForRoot.parseRoot(
				element, 
				outErrorList);
	}

}
