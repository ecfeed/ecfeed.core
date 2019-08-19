/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

public class SimpleProgressMonitor implements IEcfProgressMonitor {

	private int fTotalProgress = IEcfProgressMonitor.PROGRESS_UNKNOWN;
	private int fCurrentProgress = IEcfProgressMonitor.PROGRESS_UNKNOWN;
	boolean fIsTaskRunning = false;
	boolean fIsCancelled = false;

	@Override
	public void reset() {

		fTotalProgress = 0;
		fCurrentProgress = 0;
		fIsTaskRunning = false;
		fIsCancelled = false;
	}

	@Override
	public void setTaskBegin(String name, int totalProgress) {

		fTotalProgress = totalProgress;
		fCurrentProgress = 0;
		fIsTaskRunning = true;
		fIsCancelled = false;
	}

	@Override
	public void setTaskEnd() {

		fIsTaskRunning = false;
	}

	@Override
	public void setTotalProgress(int totalProgress) {

		fTotalProgress = totalProgress;
	}

	@Override
	public void setCurrentProgress(int currentProgress) {

		fCurrentProgress = currentProgress;
	}

	@Override
	public void incrementProgress(int increment) {

		fCurrentProgress += increment;
	}

	@Override
	public void setCanceled() {

		fIsCancelled = true;
		fIsTaskRunning = false;
	}

	@Override
	public boolean isTaskRunning() {

		return fIsTaskRunning;
	}

	@Override
	public boolean isCanceled() {
		return fIsCancelled;
	}

	@Override
	public boolean canCalculateProgress() {

		if (fTotalProgress == IEcfProgressMonitor.PROGRESS_UNKNOWN) {
			return false;
		}

		return true;
	}

	@Override
	public int getTotalProgress() {

		return fTotalProgress;
	}

	@Override
	public int getCurrentProgress() {

		return fCurrentProgress;
	}

}
