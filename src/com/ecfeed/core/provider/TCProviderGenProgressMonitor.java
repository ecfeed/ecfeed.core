/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.provider;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ecfeed.core.utils.IEcfProgressMonitor;

public class TCProviderGenProgressMonitor implements IEcfProgressMonitor {

	private IProgressMonitor fProgressMonitor;

	public TCProviderGenProgressMonitor(IProgressMonitor progressMonitor) {

		fProgressMonitor = progressMonitor;
	}

	@Override
	public boolean isCanceled() {

		if (fProgressMonitor.isCanceled()) {
			return true;
		}
		return false;
	}

	@Override
	public void setTaskBegin(String name, int totalProgress) {
		fProgressMonitor.beginTask(name, totalProgress);
		
	}

	@Override
	public void setTaskEnd() {
		fProgressMonitor.done();
	}

	@Override
	public void setCurrentProgress(int currentProgress) {
		fProgressMonitor.worked(currentProgress);
	}
}
