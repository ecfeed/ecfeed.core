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

import com.ecfeed.core.generators.api.GeneratorExceptionHelper;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.utils.GeneratorType;

public class GeneratorFactoryWithCodes {

	public IGenerator<ChoiceNode> createGenerator(GeneratorType type) {

		if (type == GeneratorType.N_WISE) {
			
			if (GeneratorFactoryForDialog.isScoredNWiseGeneratorActive()) {
				return new NWiseScoredGenerator<ChoiceNode>();
			}
			return new NWiseGenerator<ChoiceNode>();
		}

		if (type == GeneratorType.CARTESIAN) {
			return new CartesianProductGenerator<ChoiceNode>();
		}

		if (type == GeneratorType.RANDOM) {
			return new RandomGenerator<ChoiceNode>();
		}

		GeneratorExceptionHelper.reportException("Can not create generator. Unsupported generator type.");
		return null;
	}
}
