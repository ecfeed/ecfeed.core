/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;

public class GeneratorFactoryWithCodes {

	private Map<String, Class<? extends IGenerator<ChoiceNode>>> fAvailableGenerators;

	@SuppressWarnings("unchecked")
	public GeneratorFactoryWithCodes(){
		fAvailableGenerators = new LinkedHashMap<String, Class<? extends IGenerator<ChoiceNode>>>();

		registerGenerator(
				DataSourceHelper.dataSourceGenNWise, 
				(Class<? extends IGenerator<ChoiceNode>>) NWiseGenerator.class);

		registerGenerator(
				DataSourceHelper.dataSourceGenCartesian, 
				(Class<? extends IGenerator<ChoiceNode>>) CartesianProductGenerator.class);

		registerGenerator(
				DataSourceHelper.dataSourceGenAdaptiveRandom, 
				(Class<? extends IGenerator<ChoiceNode>>) AdaptiveRandomGenerator.class);

		registerGenerator(
				DataSourceHelper.dataSourceGenRandom, 
				(Class<? extends IGenerator<ChoiceNode>>) RandomGenerator.class);
	}

	public IGenerator<ChoiceNode> getGenerator(String code) throws GeneratorException{
		try {
			return fAvailableGenerators.get(code).newInstance();
		} catch (Exception e) {
			GeneratorException.report("Cannot instantiate " + code + ": " + e);
			return null;
		}
	}

	private void registerGenerator(String name, Class<? extends IGenerator<ChoiceNode>> generatorClass) {
		fAvailableGenerators.put(name, generatorClass);
	}
}
