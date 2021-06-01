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

public class GeneratorFactoryForDialog<E> { // TODO - merge with GeneratorFactoryWithCodes (get/create generator using enum GeneratorType, not by String)

	private static final String N_WISE = "N-wise generator";
	private static final String CARTESIAN = "Cartesian Product generator";
	private static final String RANDOM = "Random generator";
	
	public IGenerator<E> createGenerator(String code) throws GeneratorException {

		if (code.equals(N_WISE)) {
			
			if (isScoredNWiseGeneratorActive()) {
				return new NWiseScoredGenerator<E>();
			}
			
			return new NWiseGenerator<E>();
		}

		if (code.equals(CARTESIAN)) {
			return new CartesianProductGenerator<E>();
		}

		if (code.equals(RANDOM)) {
			return new RandomGenerator<E>();
		}

		GeneratorException.report("Cannot create generator for code:" + code );
		return null;
	}

	public static boolean isScoredNWiseGeneratorActive() {
		return false;
	}
	
	public String[] getGeneratorNames() {
		
		return new String[] { N_WISE, CARTESIAN, RANDOM };
	}
	
	public int getGeneratorCount() {
		return 3;
	}

	public boolean isGenWiseGenerator(String generatorName) {
		
		if (generatorName.equals(N_WISE)) {
			return true;
		}
		
		return false;
	}
	
}
