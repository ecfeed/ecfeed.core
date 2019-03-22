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

import java.util.List;

import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class TCProviderGenerator implements ITCProvider {

	private MethodNode fMethodNode;
	private IGenerator<ChoiceNode> fGenerator;
	
	public TCProviderGenerator(MethodNode methodNode, IGenerator<ChoiceNode> generator) {
		
		fMethodNode = methodNode;
		fGenerator = generator;
	}

	@Override
	public void initialize(ITCProviderInitData initData, IEcfProgressMonitor progressMonitor) throws Exception {
		
		TCProviderGenInitData genInitData = (TCProviderGenInitData)initData;
		
		fGenerator.initialize(
				genInitData.getChoiceInput(), 
				genInitData.getConstraints(), 
				genInitData.getGeneratorArguments(), 
				progressMonitor);

	}

	@Override
	public void close() {
	}
	
	@Override
	public MethodNode getMethodNode() {
		return fMethodNode;
	}

	@Override
	public TestCaseNode getNextTestCase() throws Exception {
		
		List<ChoiceNode> choices = fGenerator.next();
		
		if (choices == null) {
			return null;
		}
		
		return new TestCaseNode("", null, choices);
	}

	@Override
	public boolean canCalculateProgress() {
		return true;
	}

	@Override
	public int getTotalProgress() {
		return fGenerator.totalProgress();
	}

	@Override
	public int getActualProgress() {
		return fGenerator.workProgress();
	}

}
