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

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;

public class GeneratorFactoryWithCodes {

	public IGenerator<ChoiceNode> createGenerator(String code) throws GeneratorException {

		if (code.equals(DataSourceHelper.dataSourceGenNWise)) {
			return new NWiseGenerator<ChoiceNode>();
		}

		if (code.equals(DataSourceHelper.dataSourceGenCartesian)) {
			return new CartesianProductGenerator<ChoiceNode>();
		}

		if (code.equals(DataSourceHelper.dataSourceGenAdaptiveRandom)) {
			return new AdaptiveRandomGenerator<ChoiceNode>();
		}

		if (code.equals(DataSourceHelper.dataSourceGenRandom)) {
			return new RandomGenerator<ChoiceNode>();
		}

		GeneratorException.report("Cannot create generator for code:" + code );
		return null;
	}
}
