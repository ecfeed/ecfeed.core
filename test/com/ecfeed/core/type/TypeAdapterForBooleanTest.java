/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.type;

import com.ecfeed.core.type.adapter.TypeAdapterForBoolean;
import com.ecfeed.core.utils.DiskPathHelper;
import com.ecfeed.core.utils.ERunMode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TypeAdapterForBooleanTest {

	@Test
	public void convertForQuietModeTest() {

		assertEquals("true", TypeAdapterForBoolean.convertForQuietMode("1", "true"));
		assertEquals("true", TypeAdapterForBoolean.convertForQuietMode("1.0", "true"));
		assertEquals("false", TypeAdapterForBoolean.convertForQuietMode("0", "false"));
		assertEquals("false", TypeAdapterForBoolean.convertForQuietMode("0.0", "false"));

		assertEquals("false", TypeAdapterForBoolean.convertForQuietMode("ABC", "false"));
		assertEquals("true", TypeAdapterForBoolean.convertForQuietMode("ABC", "true"));
	}

}
