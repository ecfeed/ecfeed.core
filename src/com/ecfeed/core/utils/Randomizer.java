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

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Randomizer {

	private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);

	public long createSeed() {

		return seedUniquifier() ^ System.nanoTime();
	}

	private static long seedUniquifier() {
		for (;;) {
			long current = seedUniquifier.get();
			long next = current * 181783497276652981L;
			if (seedUniquifier.compareAndSet(current, next))
				return next;
		}
	}

	public Random createRandom(long seed) {

		return new Random(seed);
	}

}
