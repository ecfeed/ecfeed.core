/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.nodes;

import java.util.Collection;

import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnChoiceOperationAddLabels extends CompositeOperation {
	public OnChoiceOperationAddLabels(
			ChoiceNode target, 
			Collection<String> labels, 
			IAbstractNode nodeToSelect,
			IAbstractNode nodeToSelectAfterReverseOperation, 
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.ADD_PARTITION_LABELS, false, 
				nodeToSelect, nodeToSelectAfterReverseOperation, extLanguageManager);

		for(String label : labels){
			if(target.getInheritedLabels().contains(label) == false){
				addOperation(new OnChoiceOperationAddLabel(target, label, extLanguageManager));
			}
		}
	}
}
