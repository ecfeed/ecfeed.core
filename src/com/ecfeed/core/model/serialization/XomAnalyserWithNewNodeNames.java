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

public abstract class XomAnalyserWithNewNodeNames extends XomAnalyser {

	public XomAnalyserWithNewNodeNames() {
		super();
	}

	@Override
	protected String getChoiceNodeName() {
		return SerializationHelperVersion1.getChoiceNodeName();
	}

	@Override	
	protected String getChoiceAttributeName() {
		return SerializationHelperVersion1.getChoiceAttributeName();
	}

	@Override
	protected String getStatementChoiceAttributeName() {
		return SerializationHelperVersion1.getStatementChoiceAttributeName();
	}

	@Override
	protected String getParameterNodeName() {
		return SerializationHelperVersion1.getParameterNodeName();
	}

	@Override
	protected String getStatementParameterAttributeName() {
		return SerializationHelperVersion1.getStatementParameterAttributeName();
	}
}
