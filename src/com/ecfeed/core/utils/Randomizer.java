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

	private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

	private long fSeed;
	private Random fRandom;

	public Randomizer() {

		this(seedUniquifier() ^ System.nanoTime());
	}

	public Randomizer(long seed) {

		fSeed = seed;
		fRandom = new Random(fSeed);
	}

	public long getSeed() {
		return fSeed;
	}

	public Random getRandom() {
		return fRandom;
	}

	private static long seedUniquifier() {
		for (;;) {
			long current = SEED_UNIQUIFIER.get();
			long next = current * 181783497276652981L;
			if (SEED_UNIQUIFIER.compareAndSet(current, next))
				return next;
		}
	}

}
