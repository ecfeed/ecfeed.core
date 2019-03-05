/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.serialization;



public class SerializatorParams {

	ISerializerPredicate fSerializerPredicate;
	boolean fSerializeProperties;
	boolean fSerializeComments;


	SerializatorParams(
			ISerializerPredicate serializerPredicate, 
			boolean serializeProperties, 
			boolean serializeComments) {

		fSerializerPredicate = serializerPredicate;
		fSerializeProperties = serializeProperties;
		fSerializeComments = serializeComments;
	}
	
	
	public ISerializerPredicate getSerializerPredicate() {
		return fSerializerPredicate;
	}


	public boolean getSerializeProperties() {
		return fSerializeProperties;
	}


	public boolean getSerializeComments() {
		return fSerializeComments;
	}

}
