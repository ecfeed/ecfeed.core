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

import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class CompositeOperation extends AbstractModelOperation {

	private static final String ATTEMPT_TO_ADD_EMPTY_OPERATION = "Attempt to add empty operation.";
	List<IModelOperation> fOperations;
	List<IModelOperation> fExecutedOperations;
	// either all operation or none. if false, all operations are executed
	// otherwise after first error the reverse operation is called
	private final boolean fAtomic;
	private final List<ICheckOperation> fCheckOperations;
	private IAbstractNode fNodeToSelectAfterReverseOperation;

	protected interface ICheckOperation {
		public void check();
	}

	public CompositeOperation(
			String name, 
			boolean atomic,
			IAbstractNode nodeToSelect,
			IAbstractNode nodeToSelectAfterReverseOperation,
			IExtLanguageManager extLanguageManager) {

		this(name, new ArrayList<IModelOperation>(), atomic, 
				nodeToSelect, nodeToSelectAfterReverseOperation, extLanguageManager);
	}

	public CompositeOperation(
			String name, 
			List<IModelOperation> operations, 
			boolean atomic, 
			IAbstractNode nodeToSelect,
			IAbstractNode nodeToelectAfterReverseOperation, 
			IExtLanguageManager extLanguageManager) {

		super(name, extLanguageManager);

		fOperations = operations;
		fExecutedOperations = new ArrayList<IModelOperation>();
		fCheckOperations = new ArrayList<ICheckOperation>();
		fAtomic = atomic;

		setOneNodeToSelect(nodeToSelect);

		fNodeToSelectAfterReverseOperation = nodeToelectAfterReverseOperation;
	}
	
	@Override
	public String toString() {
		
		return getName() + " " + fOperations.toString();
	}

	protected void addOperation(IModelOperation operation) {
		
		if (operation == null) {
			ExceptionHelper.reportRuntimeException(ATTEMPT_TO_ADD_EMPTY_OPERATION);
		}
		
		fOperations.add(operation);
	}

	protected void addCheckOperation(ICheckOperation operation) {

		if (operation == null) {
			ExceptionHelper.reportRuntimeException(ATTEMPT_TO_ADD_EMPTY_OPERATION);
		}
		
		fCheckOperations.add(operation);
	}

	public static final String PROBLEM_WITH_BULK_OPERATION(String operation) {

		return "Cannot perform operation: " + operation + ".";
	}	

	@Override
	public void execute() {

		Set<String> errors = new HashSet<String>();
		fExecutedOperations.clear();

		for (IModelOperation operation : fOperations) {
			try {
				operation.execute();
				fExecutedOperations.add(operation);
			} catch(Exception e) {
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
			} catch(Exception e) {
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

			ExceptionHelper.reportRuntimeException(message);
		}
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new CompositeOperation(
				"reverse " + getName(), 
				getReverseOperations(), 
				fAtomic, 
				fNodeToSelectAfterReverseOperation, 
				null, 
				getExtLanguageManager());
	}


	public List<IModelOperation> getOperations() {
		return fOperations;
	}

	protected List<IModelOperation> executedOperations() {
		return fExecutedOperations;
	}

	protected List<IModelOperation> getReverseOperations() {

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
