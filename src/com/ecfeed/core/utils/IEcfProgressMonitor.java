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

	public void setTaskBegin(String name, int totalProgress);
	public void setTaskEnd();
	public void setCurrentProgress(int work);
	boolean isCanceled();
	boolean canCalculateProgress();
}
