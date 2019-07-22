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


public interface IEcfProgressMonitor {

	public final static int PROGRESS_UNKNOWN = 1;

	void reset();
	
	void setTaskBegin(String name, int totalProgress);
	void setTaskEnd();
	void setCanceled();
	void setCurrentProgress(int currentProgress);
	void incrementProgress(int increment);

	boolean isTaskRunning();
	boolean isCanceled();
	
	boolean canCalculateProgress();
	int getTotalProgress();
	int getCurrentProgress();
	
}
