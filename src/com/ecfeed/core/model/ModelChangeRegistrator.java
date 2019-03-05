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


public class ModelChangeRegistrator implements IModelChangeRegistrator {

	private int fChangeCounter = 0;
	private int fChangeCounterWhenSaved = 0;
	private int fChangeCounterWhenReadoutStarted = 0;

	@Override
	public void registerChange() {

		fChangeCounter++;
		//		System.out.println("Change registered.");
	}

	@Override
	public void registerModelSaved() {

		fChangeCounterWhenSaved = fChangeCounter;
	}

	@Override
	public boolean isModelChangedSinceLastSave() {

		if (fChangeCounterWhenSaved != fChangeCounter) {
			return true;
		}

		return false;
	}

	@Override
	public void registerStartOfDataReadout() {

		fChangeCounterWhenReadoutStarted = fChangeCounter;

	}

	@Override
	public boolean isModelChangedDuringReadout() {

		if (fChangeCounterWhenReadoutStarted != fChangeCounter) {
			return true;
		}

		return false;
	}

}
