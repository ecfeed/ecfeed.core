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
import java.util.Set;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGenerator;
import com.ecfeed.core.model.ChoiceNode;

public class GeneratorFactoryForDialog<E> {

	public IGenerator<E> getGenerator(String code) throws GeneratorException { // TODO - rename to create generator

		if (code.equals("N-wise generator")) { // TODO
			return new NWiseGenerator();
		}

		if (code.equals("Cartesian Product generator")) { // TODO
			return new CartesianProductGenerator();
		}

		if (code.equals("Adaptive random generator")) { // TODO
			return new AdaptiveRandomGenerator();
		}

		if (code.equals("Random generator")) { // TODO
			return new RandomGenerator();
		}

		GeneratorException.report("Cannot create generator for code:" + code );
		return null;
	}

}
