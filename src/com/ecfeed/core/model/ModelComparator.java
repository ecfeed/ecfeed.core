/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;


import java.io.ByteArrayOutputStream;

import com.ecfeed.core.model.serialization.ModelSerializer;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;

public class ModelComparator { 

	public static void compareRootNodes(RootNode rootNode1, RootNode rootNode2) {

		RootNodeHelper.compareRootNodes(rootNode1, rootNode2);

		compareSerializedModelsAsLastResort(rootNode1, rootNode2);
	}

	private static void compareSerializedModelsAsLastResort(RootNode model1, RootNode model2) {

		String xml1 = serializeModel(model1);
		String xml2 = serializeModel(model2);

		String[] lines1 = xml1.split("\n");
		String[] lines2 = xml2.split("\n");

		//		if (xml1.equals(xml2)) {
		//			return;
		//		}

		String errorMessage = StringHelper.isEqualByLines(lines1, lines2);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException("Model comparison failed with message: " + errorMessage);
		}

		//ExceptionHelper.reportRuntimeException("Comparison of serialized models failed.");
	}

	private static String serializeModel(RootNode model1) {

		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		ModelSerializer serializer = new ModelSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());

		try {
			serializer.serialize(model1);
		} catch (Exception e) {
			ExceptionHelper.reportRuntimeException("Failed to serialize model.", e);
		}

		return ostream.toString();
	}

}
