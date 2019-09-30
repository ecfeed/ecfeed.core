package com.ecfeed.core.generators;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGeneratorParamDefinition;
import com.ecfeed.core.utils.ExceptionHelper;

public class GeneratorArgumentHelper  {

	public static GeneratorArgument createGeneratorArgument(
			IGeneratorParamDefinition definition, Object value) {
		
		try {
			return createGeneratorArgumentIntr(definition, value);
		} catch (GeneratorException e) {
			ExceptionHelper.reportRuntimeException(e);
			return null;
		}
	}
	
	private static GeneratorArgument createGeneratorArgumentIntr(
			IGeneratorParamDefinition definition, Object value) throws GeneratorException {
		
		if (definition instanceof GeneratorParameterBoolean) {
			return new GeneratorArgumentBoolean(definition.getName(), (boolean)value);
		}
		
		if (definition instanceof GeneratorParameterDouble) {
			return new GeneratorArgumentDouble(definition.getName(), (double)value);
		}
		
		if (definition instanceof GeneratorParameterInteger) {
			return new GeneratorArgumentInteger(definition.getName(), (int)value);
		}
		
		if (definition instanceof GeneratorParameterString) {
			return new GeneratorArgumentString(definition.getName(), (String)value);
		}
		
		ExceptionHelper.reportRuntimeException("Invalid type of generator parameter.");
		return null;
	}
	
}
