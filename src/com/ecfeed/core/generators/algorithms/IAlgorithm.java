/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators.algorithms;

import java.util.Collection;
import java.util.List;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public interface IAlgorithm<E> {
	
	public void initialize(List<List<E>> input, 
			Collection<IConstraint<E>> constraints,
			IEcfProgressMonitor fGeneratorProgressMonitor) throws GeneratorException;
	
	public void setTaskBegin(int totalWork);
	public void setTaskEnd();
	public void reset();
	public void incrementProgress(int progressIncrement);
	public void cancel();
	public boolean isCancelled();
	
	public List<E> getNext() throws GeneratorException;
	public void addConstraint(IConstraint<E> constraint);
	public void removeConstraint(IConstraint<E> constraint);
	public Collection<? extends IConstraint<E>> getConstraints();
}
