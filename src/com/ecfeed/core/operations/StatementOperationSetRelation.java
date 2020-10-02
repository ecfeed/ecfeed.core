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

import java.util.Arrays;

import com.ecfeed.core.model.IRelationalStatement;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.IExtLanguageManager;

public class StatementOperationSetRelation extends AbstractModelOperation {

	private IRelationalStatement fTarget;
	private EMathRelation fNewRelation;
	private EMathRelation fCurrentRelation;

	public StatementOperationSetRelation(IRelationalStatement target, EMathRelation relation, IExtLanguageManager extLanguage) {
		super(OperationNames.SET_STATEMENT_RELATION, extLanguage);
		fTarget = target;
		fNewRelation = relation;
		fCurrentRelation = target.getRelation();
	}

	@Override
	public void execute() throws ModelOperationException {
		if(Arrays.asList(fTarget.getAvailableRelations()).contains(fNewRelation) == false){
			ModelOperationException.report(OperationMessages.DIALOG_UNALLOWED_RELATION_MESSAGE);
		}
		fTarget.setRelation(fNewRelation);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new StatementOperationSetRelation(fTarget, fCurrentRelation, getExtLanguage());
	}

}
