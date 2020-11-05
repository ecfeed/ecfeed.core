package com.ecfeed.core.model;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.ecfeed.core.model.serialization.ModelParser;
import com.ecfeed.core.model.serialization.ParserException;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class ModelTestHelper {

	public static RootNode createModel(String modelXml, List<String> details, IExtLanguageManager extLanguageManager) {

		try {
			ModelParser parser = new ModelParser(extLanguageManager);
			ByteArrayInputStream istream = new ByteArrayInputStream(modelXml.getBytes());

			return parser.parseModel(istream, null, details);
		} catch(ParserException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}

		return null;
	}

}