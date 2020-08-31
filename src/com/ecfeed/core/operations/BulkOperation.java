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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.ExtLanguage;

public class BulkOperation extends AbstractModelOperation {

	List<IModelOperation> fOperations;
	List<IModelOperation> fExecutedOperations;
	// either all operation or none. if false, all operations are executed
	// otherwise after first error the reverse operation is called
	private final boolean fAtomic;
	private final List<ICheckOperation> fCheckOperations;
	private AbstractNode fNodeToSelectAfterReverseOperation;

	protected interface ICheckOperation {
		public void check() throws ModelOperationException;
	}

	public BulkOperation(
			String name, 
			boolean atomic,
			AbstractNode nodeToSelect,
			AbstractNode nodeToSelectAfterReverseOperation,
			ExtLanguage extLanguage) {

		this(name, new ArrayList<IModelOperation>(), atomic, 
				nodeToSelect, nodeToSelectAfterReverseOperation, extLanguage);
	}

	public BulkOperation(
			String name, 
			List<IModelOperation> operations, 
			boolean atomic, 
			AbstractNode nodeToSelect,
			AbstractNode nodeToelectAfterReverseOperation, 
			ExtLanguage extLanguage) {

		super(name, extLanguage);

		fOperations = operations;
		fExecutedOperations = new ArrayList<IModelOperation>();
		fCheckOperations = new ArrayList<ICheckOperation>();
		fAtomic = atomic;

		setOneNodeToSelect(nodeToSelect);

		fNodeToSelectAfterReverseOperation = nodeToelectAfterReverseOperation;
	}

	protected void addOperation(IModelOperation operation) {
		fOperations.add(operation);
	}

	protected void addCheckOperation(ICheckOperation operation) {
		fCheckOperations.add(operation);
	}

	public static final String PROBLEM_WITH_BULK_OPERATION(String operation) {

		return "Cannot perform operation: " + operation + ".";
	}	

	@Override
	public void execute() throws ModelOperationException {

		Set<String> errors = new HashSet<String>();
		fExecutedOperations.clear();

		for (IModelOperation operation : fOperations) {
			try {
				operation.execute();
				fExecutedOperations.add(operation);
			} catch(ModelOperationException e) {
				errors.add(e.getMessage());
				if(fAtomic) {
					getReverseOperation().execute();
					break;
				}
			}
		}

		for (ICheckOperation operation : fCheckOperations) {
			try {
				operation.check();
			} catch(ModelOperationException e) {
				errors.add(e.getMessage());
				getReverseOperation().execute();
				break;
			}
		}

		if (errors.size() > 0) {
			String message = PROBLEM_WITH_BULK_OPERATION(getName());
			for(String error : errors) {
				message += "\n" + error;
			}

			ModelOperationException.report(message);
		}
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new BulkOperation(
				"reverse " + getName(), 
				reverseOperations(), 
				fAtomic, 
				fNodeToSelectAfterReverseOperation, 
				null, 
				getExtLanguage());
	}


	protected List<IModelOperation> operations() {
		return fOperations;
	}

	protected List<IModelOperation> executedOperations() {
		return fExecutedOperations;
	}

	protected List<IModelOperation> reverseOperations() {

		List<IModelOperation> reverseOperations = new ArrayList<IModelOperation>();

		for(IModelOperation operation : executedOperations()){
			reverseOperations.add(0, operation.getReverseOperation());
		}

		return reverseOperations;
	}

	@Override
	public boolean modelUpdated() {

		for (IModelOperation operation : fExecutedOperations) {
			if (operation.modelUpdated()) {
				return true;
			}
		}
		return false;
	}

}
