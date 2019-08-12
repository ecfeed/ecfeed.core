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

import com.ecfeed.core.generators.algorithms.AwesomeNWiseAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.GeneratorType;

public class GeneratorFactoryWithCodes {

	public IGenerator<ChoiceNode> createGenerator(GeneratorType type) throws GeneratorException {

		if (type == GeneratorType.N_WISE) {
			return new NWiseGenerator<ChoiceNode>();
		}

		if (type == GeneratorType.CARTESIAN) {
			return new CartesianProductGenerator<ChoiceNode>();
		}

		if (type == GeneratorType.RANDOM) {
			return new RandomGenerator<ChoiceNode>();
		}

		GeneratorException.report("Can not create generator. Unsupported generator type.");
		return null;
	}
}
