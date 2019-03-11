package com.ecfeed.core.model;

import java.io.ByteArrayInputStream;

import com.ecfeed.core.model.serialization.ModelParser;
import com.ecfeed.core.model.serialization.ParserException;
import com.ecfeed.core.utils.ExceptionHelper;

public class ModelTestHelper {  // TODO - rename / move to ModelHelper

	public static RootNode createModel(String modelXml) {

		try {
			ModelParser parser = new ModelParser();
			ByteArrayInputStream istream = new ByteArrayInputStream(modelXml.getBytes());

			return parser.parseModel(istream, null);

		} catch(ParserException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}

		return null;
	}

}