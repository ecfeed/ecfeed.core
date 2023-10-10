package com.ecfeed.core.model;

import java.io.ByteArrayInputStream;

import com.ecfeed.core.model.serialization.ModelParser;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ListOfStrings;

public class ModelTestHelper {

	public static RootNode createModel(String modelXml, ListOfStrings listOfErrors) {

		try {
			ModelParser parser = new ModelParser();
			ByteArrayInputStream istream = new ByteArrayInputStream(modelXml.getBytes());

			return parser.parseModel(istream, null, listOfErrors);
		} catch(Exception e) {
			listOfErrors.addIfUnique(e.getMessage());
		}

		return null;
	}

	public static RootNode createModel(String modelXml) {

		try {
			ModelParser parser = new ModelParser();
			ByteArrayInputStream istream = new ByteArrayInputStream(modelXml.getBytes());

			ListOfStrings listOfErrors = new ListOfStrings();
			RootNode rootNode = parser.parseModel(istream, null, listOfErrors);

			if (!listOfErrors.isEmpty()) {
				String firstError = listOfErrors.getFirstString();
				ExceptionHelper.reportRuntimeException(firstError);
			}
			return rootNode;

		} catch(Exception e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}

		return null;
	}

}