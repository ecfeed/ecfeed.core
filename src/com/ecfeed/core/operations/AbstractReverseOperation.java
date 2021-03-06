/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

import com.ecfeed.core.utils.IExtLanguageManager;

public abstract class AbstractReverseOperation extends AbstractModelOperation {

	public AbstractReverseOperation(AbstractModelOperation baseOperation, IExtLanguageManager extLanguageManager) {
		super("reverse " + baseOperation, extLanguageManager);
	}
}
