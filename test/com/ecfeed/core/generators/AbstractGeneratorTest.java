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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ecfeed.core.evaluator.DummyEvaluator;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.generators.api.IParameterDefinition;
import com.ecfeed.core.utils.GeneratorType;
import org.junit.Before;
import org.junit.Test;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.model.IConstraint;

public class AbstractGeneratorTest {
	
	Random rand;
	
	private final String INT_PARAMETER_NAME = "int";
	private final String DOUBLE_PARAMETER_NAME = "double";
	private final String BOOLEAN_PARAMETER_NAME = "boolean";
	private final String STRING_PARAMETER_NAME = "string";

	private int fIntParameterValue;
	private int fDefaultIntParameterValue;
	private double fDoubleParameterValue;
	private double fDefaultDoubleParameterValue;
	private boolean fBooleanParameterValue;
	private boolean fDefaultBooleanParameterValue;
	private String fStringParameterValue;
	private String fDefaultStringParameterValue;

	public AbstractGeneratorTest() {
		rand = new Random();
	}
	
	@Before
	public void randomizeParametersValues(){
		fIntParameterValue = rand.nextInt();
		fDefaultIntParameterValue = rand.nextInt();
		fDoubleParameterValue = rand.nextDouble();
		fDefaultDoubleParameterValue = rand.nextDouble();
		fBooleanParameterValue = rand.nextBoolean();
		fDefaultBooleanParameterValue = rand.nextBoolean();
		fStringParameterValue = "value";
		fDefaultStringParameterValue = "default value";
	}

	@Test
	public void initializeWithNoConstraintsOnParametersTest() {
		TestAbstractGenerator generator = new TestAbstractGenerator();

		List<List<String>> inputDomain = new ArrayList<List<String>>();

		ParameterDefinitionInteger paramDefInt = new ParameterDefinitionInteger(INT_PARAMETER_NAME,  fDefaultIntParameterValue);
		generator.addParameterDefinition( paramDefInt );
		ParameterDefinitionBoolean paramDefBool = new ParameterDefinitionBoolean(BOOLEAN_PARAMETER_NAME,  fDefaultBooleanParameterValue);
		generator.addParameterDefinition( paramDefBool );
		ParameterDefinitionDouble paramDefDouble = new ParameterDefinitionDouble(DOUBLE_PARAMETER_NAME,  fDefaultDoubleParameterValue);
		generator.addParameterDefinition(paramDefDouble);
		ParameterDefinitionString paramDefString = new ParameterDefinitionString(STRING_PARAMETER_NAME,  fDefaultStringParameterValue);
		generator.addParameterDefinition(paramDefString);
		
		List<IGeneratorValue> arguments = new ArrayList<>();

		try {

			GeneratorValue generatorValueInteger = new GeneratorValue(paramDefInt, String.valueOf(fIntParameterValue) );
			arguments.add(generatorValueInteger);

			GeneratorValue generatorValueDouble = new GeneratorValue(paramDefDouble, String.valueOf(fDoubleParameterValue) );
			arguments.add(generatorValueDouble);

			GeneratorValue generatorValueBoolean = new GeneratorValue(paramDefBool, String.valueOf(fBooleanParameterValue) );
			arguments.add(generatorValueBoolean);

			GeneratorValue generatorValueString = new GeneratorValue(paramDefString, fStringParameterValue);
			arguments.add(generatorValueString);
		
			generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, null);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}

	@Test
	public void initializeWithBoundsTest() {
		TestAbstractGenerator generator = new TestAbstractGenerator();

		List<List<String>> inputDomain = new ArrayList<List<String>>();
		try{
			ParameterDefinitionInteger paramDefInt = new ParameterDefinitionInteger(INT_PARAMETER_NAME,  0, -1, 1);
			generator.addParameterDefinition(paramDefInt);
			ParameterDefinitionDouble paramDefDouble = new ParameterDefinitionDouble(DOUBLE_PARAMETER_NAME, 0.0, -1.0, 1.0);
			generator.addParameterDefinition( paramDefDouble );

			List<IGeneratorValue> arguments = new ArrayList<>();

			try{

				GeneratorValue generatorValueInteger = new GeneratorValue(paramDefInt, "0");
				arguments.add(generatorValueInteger);

				GeneratorValue generatorValueDouble = new GeneratorValue(paramDefDouble, "0.0");
				arguments.add(generatorValueDouble);

				generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, null);

				GeneratorValue argumentInteger = new GeneratorValue(paramDefInt, "5");
				arguments.add(argumentInteger);

				generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, null);
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
	}

	@Test
	public void initializeWithAllowedValuesTest() {
		TestAbstractGenerator generator = new TestAbstractGenerator();
		List<List<String>> inputDomain = new ArrayList<List<String>>();
		try{
			ParameterDefinitionInteger paramDefInt = new ParameterDefinitionInteger(INT_PARAMETER_NAME,  0, new Integer[]{-1, 0, 1});
			generator.addParameterDefinition(paramDefInt);
			ParameterDefinitionDouble paramDefDouble = new ParameterDefinitionDouble(DOUBLE_PARAMETER_NAME,  0.0, new Double[]{-1.0, 0.0, 1.0});
			generator.addParameterDefinition(paramDefDouble);

			List<IGeneratorValue> values = new ArrayList<>();;

			try{

				GeneratorValue generatorValueInteger = new GeneratorValue(paramDefInt, "0");
				values.add(generatorValueInteger);

				GeneratorValue generatorValueDouble = new GeneratorValue(paramDefDouble, "0.0");
				values.add(generatorValueDouble);

				generator.initialize(inputDomain, new DummyEvaluator<>(), values, null);

				GeneratorValue argumentInteger = new GeneratorValue(paramDefInt, "5");
				values.add(argumentInteger);

				generator.initialize(inputDomain, new DummyEvaluator<>(), values, null);
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
	}

	@Test
	public void initializeWithMissingOptionalParameterTest(){
		TestAbstractGenerator generator = new TestAbstractGenerator();
		List<List<String>> inputDomain = new ArrayList<List<String>>();

		ParameterDefinitionInteger paramDefInt = new ParameterDefinitionInteger(INT_PARAMETER_NAME,  0);
		generator.addParameterDefinition( paramDefInt );
		ParameterDefinitionDouble paramDefDouble = new ParameterDefinitionDouble(DOUBLE_PARAMETER_NAME,  0.0);
		generator.addParameterDefinition( paramDefDouble );

		List<IGeneratorValue> arguments = new ArrayList<>();
		try{
			generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, null);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}

		try{
			GeneratorValue generatorValueInteger = new GeneratorValue(paramDefInt, "0");
			arguments.add(generatorValueInteger);
			generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, null);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}

		try{
			GeneratorValue generatorValueDouble = new GeneratorValue(paramDefDouble, "0.0");
			arguments.add(generatorValueDouble);
			generator.initialize(inputDomain, new DummyEvaluator<>(), arguments, null);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}

	@Test
	public void initializeWithAdditionalParameterTest(){
		TestAbstractGenerator generator = new TestAbstractGenerator();
		List<List<String>> inputDomain = new ArrayList<List<String>>();

		ParameterDefinitionInteger paramDefInt = new ParameterDefinitionInteger(INT_PARAMETER_NAME,  0);
		generator.addParameterDefinition( paramDefInt );

		List<IGeneratorValue> values = new ArrayList<>();


		try{
			GeneratorValue generatorValueInteger = new GeneratorValue(paramDefInt, "0");
			values.add(generatorValueInteger);
			generator.initialize(inputDomain, new DummyEvaluator<>(), values, null);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}

		ParameterDefinitionDouble paramDefDouble = new ParameterDefinitionDouble(DOUBLE_PARAMETER_NAME,  0.0);

		try{
			GeneratorValue generatorValueDouble = new GeneratorValue(paramDefDouble, "0.0");
			values.add(generatorValueDouble);
			generator.initialize(inputDomain, new DummyEvaluator<>(), values, null);
			fail("GeneratorException expected");
		} catch (GeneratorException e) {
		}
	}
	
	@Test
	public void getRequiredParameterTest(){
		TestAbstractGenerator generator = new TestAbstractGenerator();
		List<List<String>> inputDomain = new ArrayList<List<String>>();

		ParameterDefinitionInteger paramDefInt = new ParameterDefinitionInteger(INT_PARAMETER_NAME,  fDefaultIntParameterValue);
		generator.addParameterDefinition( paramDefInt );
		ParameterDefinitionDouble paramDefDouble = new ParameterDefinitionDouble(DOUBLE_PARAMETER_NAME,  fDefaultDoubleParameterValue);
		generator.addParameterDefinition( paramDefDouble );
		ParameterDefinitionBoolean paramDefBoolean = new ParameterDefinitionBoolean(BOOLEAN_PARAMETER_NAME,  fDefaultBooleanParameterValue);
		generator.addParameterDefinition( paramDefBoolean );
		ParameterDefinitionString paramDefString = new ParameterDefinitionString(STRING_PARAMETER_NAME,  fDefaultStringParameterValue);
		generator.addParameterDefinition( paramDefString );
		
		List<IGeneratorValue> values = new ArrayList<>();


		try {
			GeneratorValue generatorValueInteger = new GeneratorValue(paramDefInt, String.valueOf(fIntParameterValue) );
			values.add(generatorValueInteger);

			GeneratorValue generatorValueDouble = new GeneratorValue(paramDefDouble, String.valueOf(fDoubleParameterValue));
			values.add(generatorValueDouble);

			GeneratorValue generatorValueBoolean = new GeneratorValue(paramDefBoolean, String.valueOf(fBooleanParameterValue));
			values.add(generatorValueBoolean);

			GeneratorValue generatorValueString = new GeneratorValue(paramDefString, fStringParameterValue);
			values.add(generatorValueString);
			generator.initialize(inputDomain, new DummyEvaluator<>(), values, null);
			
			int intParameter = (int)generator.getParameterValue(paramDefInt);
			double doubleParameter = (double)generator.getParameterValue(paramDefDouble);
			boolean booleanParameter = (boolean)generator.getParameterValue(paramDefBoolean);
			String stringParameter = (String)generator.getParameterValue(paramDefString);
			
			assertEquals(fIntParameterValue, intParameter);
			assertEquals(fDoubleParameterValue, doubleParameter, 0.0);
			assertEquals(fBooleanParameterValue, booleanParameter);
			assertEquals(fStringParameterValue, stringParameter);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}
	
	@Test
	public void getOptionalParameterTest(){
		TestAbstractGenerator generator = new TestAbstractGenerator();
		List<List<String>> inputDomain = new ArrayList<List<String>>();

		ParameterDefinitionInteger paramDefInt = new ParameterDefinitionInteger(INT_PARAMETER_NAME,  fDefaultIntParameterValue);
		generator.addParameterDefinition(paramDefInt);
		ParameterDefinitionDouble paramDefDouble = new ParameterDefinitionDouble(DOUBLE_PARAMETER_NAME,  fDefaultDoubleParameterValue);
		generator.addParameterDefinition(paramDefDouble);
		ParameterDefinitionBoolean paramDefBoolean = new ParameterDefinitionBoolean(BOOLEAN_PARAMETER_NAME,  fDefaultBooleanParameterValue);
		generator.addParameterDefinition(paramDefBoolean);
		ParameterDefinitionString paramDefString = new ParameterDefinitionString(STRING_PARAMETER_NAME,  fDefaultStringParameterValue);
		generator.addParameterDefinition( paramDefString );

		List<IGeneratorValue> values = new ArrayList<>();
		
		try {
			generator.initialize(inputDomain, new DummyEvaluator<>(), values, null);
			
			int intParameter = (int)generator.getParameterValue(paramDefInt);
			double doubleParameter = (double)generator.getParameterValue(paramDefDouble);
			boolean booleanParameter = (boolean)generator.getParameterValue(paramDefBoolean);
			String stringParameter = (String)generator.getParameterValue(paramDefString);
			
			assertEquals(fDefaultIntParameterValue, intParameter);
			assertEquals(fDefaultDoubleParameterValue, doubleParameter, 0.0);
			assertEquals(fDefaultBooleanParameterValue, booleanParameter);
			assertEquals(fDefaultStringParameterValue, stringParameter);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}


		try {
			GeneratorValue generatorValueInteger = new GeneratorValue(paramDefInt, String.valueOf(fIntParameterValue));
			values.add(generatorValueInteger);

			GeneratorValue generatorValueDouble = new GeneratorValue(paramDefDouble, String.valueOf(fDoubleParameterValue));
			values.add(generatorValueDouble);

			GeneratorValue generatorValueBoolean = new GeneratorValue(paramDefBoolean, String.valueOf(fBooleanParameterValue));
			values.add(generatorValueBoolean);

			GeneratorValue generatorValueString = new GeneratorValue(paramDefString, fStringParameterValue);
			values.add(generatorValueString);

			generator.initialize(inputDomain, new DummyEvaluator<>(), values, null);
			
			int intParameter = (int)generator.getParameterValue(paramDefInt);
			double doubleParameter = (double)generator.getParameterValue(paramDefDouble);
			boolean booleanParameter = (boolean)generator.getParameterValue(paramDefBoolean);
			String stringParameter = (String)generator.getParameterValue(paramDefString);
			
			assertEquals(fIntParameterValue, intParameter);
			assertEquals(fDoubleParameterValue, doubleParameter, 0.0);
			assertEquals(fBooleanParameterValue, booleanParameter);
			assertEquals(fStringParameterValue, stringParameter);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}
	
	@Test 
	public void initializeWithWrongInputDomainTest(){
		TestAbstractGenerator generator = new TestAbstractGenerator();
		List<List<String>> inputDomain = new ArrayList<List<String>>();
		inputDomain.add(new ArrayList<String>());

		try{
			generator.initialize(inputDomain, new DummyEvaluator<>(), null, null);
			fail("GeneratorException expected");
		} catch (GeneratorException e) {
		}
	}

	private static class TestAbstractGenerator extends AbstractGenerator<String> {

		private List<IParameterDefinition> fParameterDefinitions = null;

		@Override
		public GeneratorType getGeneratorType()
		{
			return null;
		}

		public TestAbstractGenerator()
		{
			if (fParameterDefinitions == null) {
				fParameterDefinitions = new ArrayList<>();
			}
		}

		public List<IParameterDefinition> getParameterDefinitions() {
			return fParameterDefinitions;
		}

	}
//		
//	@Test
//	public void testGetRequiredParameterWithBounds(){
//		AbstractGenerator<String> generator = new AbstractGenerator<String>();
//		
//		generator.addParameterDefinition(new AbstractParameterDefinition("parameter", TYPE.INTEGER, true, 2, new Integer[]{1,2,3}));
//		Map<String, Object> values = new HashMap<String, Object>();
//		try {
//			generator.getIntParameter("parameter");
//			fail("GeneratorException expected");
//		} catch (GeneratorException e) {
//		}
//		
//		values.put("parameter", 2);
//		try {
//			int parameter = generator.getIntParameter("parameter");
//			assertEquals(2, parameter);
//		} catch (GeneratorException e) {
//			fail("Unexpected GeneratorException");
//		}
//
//		//put forbidden value
//		values.put("parameter", 0);
//		try {
//			generator.getIntParameter("parameter");
//			fail("GeneratorException expected");
//		} catch (GeneratorException e) {
//		}
//	}
//		
//	@Test
//	public void testGetOptionalIntParameter(){
//		AbstractGenerator<String> generator = new AbstractGenerator<String>();
//		int parameterValue = rand.nextInt();
//		int defaultParameterValue = rand.nextInt();
//		
//		generator.addParameterDefinition(new AbstractParameterDefinition("parameter", TYPE.INTEGER, false, defaultParameterValue, null));
//		Map<String, Object> values = new HashMap<String, Object>();
//		try {
//			int parameter = generator.getIntParameter("parameter", values);
//			assertEquals(defaultParameterValue, parameter);
//		} catch (GeneratorException e) {
//			fail("Unexpected GeneratorException");
//		}
//		
//		values.put("parameter", parameterValue);
//		try {
//			int parameter = generator.getIntParameter("parameter", values);
//			assertEquals(parameterValue, parameter);
//		} catch (GeneratorException e) {
//			fail("Unexpected GeneratorException");
//		}
//	}
//		
//	@Test
//	public void testGetNoExistingIntParameter(){
//		AbstractGenerator<String> generator = new AbstractGenerator<String>();
//		Map<String, Object> values = new HashMap<String, Object>();
//		try {
//			//try to fetch a parameter that is not defined for this generator
//			generator.getIntParameter("parameter", values);
//			fail("GeneratorException expected");
//		} catch (GeneratorException e) {
//		}
//	}
//	
//	@Test
//	public void testGetIntParameterWithBounds(){
//		AbstractGenerator<String> generator = new AbstractGenerator<String>();
//		int parameterValue = rand.nextInt();
//		
//		try {
//			generator.addParameterDefinition(new AbstractParameterDefinition("parameter", TYPE.INTEGER, true, 0, -5, 5));
//		} catch (GeneratorException e) {
//			e.printStackTrace();
//		}
//	}
//	
}
