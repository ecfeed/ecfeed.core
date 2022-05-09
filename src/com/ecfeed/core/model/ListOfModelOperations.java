/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.operations.IModelOperation;

public class ListOfModelOperations {

	private List<IModelOperation> fOperations;

	public ListOfModelOperations() {

		fOperations = new ArrayList<>();
	}

	public void add(IModelOperation modelOperation) {

		fOperations.add(modelOperation);
	}

	public void executeFromTail() {

		int size = fOperations.size();

		for (int index = size-1; index >= 0; index--) {

			IModelOperation modelOperation = fOperations.get(index);

			modelOperation.execute();
		}
	}

	public int getSize() {

		return fOperations.size();
	}

}
