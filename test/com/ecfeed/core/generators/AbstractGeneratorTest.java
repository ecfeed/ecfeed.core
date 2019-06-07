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

import com.ecfeed.core.evaluator.HomebrewConstraintEvaluator;
import com.ecfeed.core.evaluator.Sat4jEvaluator;
import com.ecfeed.core.generators.api.IGeneratorArgument;
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
		Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>(); 
		
		generator.addParameterDefinition(new GeneratorParameterInteger(INT_PARAMETER_NAME, true, fDefaultIntParameterValue));
		generator.addParameterDefinition(new GeneratorParameterBoolean(BOOLEAN_PARAMETER_NAME, true, fDefaultBooleanParameterValue));
		generator.addParameterDefinition(new GeneratorParameterDouble(DOUBLE_PARAMETER_NAME, true, fDefaultDoubleParameterValue));
		generator.addParameterDefinition(new GeneratorParameterString(STRING_PARAMETER_NAME, true, fDefaultStringParameterValue));
		
		Map<String, IGeneratorArgument> arguments = new HashMap<>();

		GeneratorArgumentInteger generatorArgumentInteger = null;
		try {
			generatorArgumentInteger = new GeneratorArgumentInteger(INT_PARAMETER_NAME, fIntParameterValue);
		} catch (GeneratorException e) {
			fail();
		}
		arguments.put(INT_PARAMETER_NAME, generatorArgumentInteger);

		GeneratorArgumentDouble generatorArgumentDouble = null;
		try {
			generatorArgumentDouble = new GeneratorArgumentDouble(DOUBLE_PARAMETER_NAME, fDoubleParameterValue);
		} catch (GeneratorException e) {
			fail();
		}
		arguments.put(DOUBLE_PARAMETER_NAME, generatorArgumentDouble);

		GeneratorArgumentBoolean generatorArgumentBoolean = new GeneratorArgumentBoolean(BOOLEAN_PARAMETER_NAME, fBooleanParameterValue);
		arguments.put(BOOLEAN_PARAMETER_NAME, generatorArgumentBoolean);

		GeneratorArgumentString generatorArgumentString = null;
		try {
			generatorArgumentString = new GeneratorArgumentString(STRING_PARAMETER_NAME, fStringParameterValue);
		} catch (GeneratorException e) {
			fail();
		}
		arguments.put(STRING_PARAMETER_NAME, generatorArgumentString);
		
		try {
			generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), arguments, null);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}

	@Test
	public void initializeWithBoundsTest() {
		TestAbstractGenerator generator = new TestAbstractGenerator();

		List<List<String>> inputDomain = new ArrayList<List<String>>();
		Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>(); 
		try{
			generator.addParameterDefinition(new GeneratorParameterInteger(INT_PARAMETER_NAME, true, 0, -1, 1));
			generator.addParameterDefinition(new GeneratorParameterDouble(DOUBLE_PARAMETER_NAME, true, 0.0, -1.0, 1.0));

			Map<String, IGeneratorArgument> arguments = new HashMap<>();

			GeneratorArgumentInteger generatorArgumentInteger = new GeneratorArgumentInteger(INT_PARAMETER_NAME, 0);
			arguments.put(INT_PARAMETER_NAME, generatorArgumentInteger);

			GeneratorArgumentDouble generatorArgumentDouble = new GeneratorArgumentDouble(DOUBLE_PARAMETER_NAME, 0.0);
			arguments.put(DOUBLE_PARAMETER_NAME, generatorArgumentDouble);

			generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), arguments, null);

			GeneratorArgumentInteger argumentInteger = new GeneratorArgumentInteger(INT_PARAMETER_NAME, 5);
			arguments.put(INT_PARAMETER_NAME, argumentInteger);

			try{
				generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), arguments, null);
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
		Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>(); 
		try{
			generator.addParameterDefinition(new GeneratorParameterInteger(INT_PARAMETER_NAME, true, 0, new Integer[]{-1, 0, 1}));
			generator.addParameterDefinition(new GeneratorParameterDouble(DOUBLE_PARAMETER_NAME, true, 0.0, new Double[]{-1.0, 0.0, 1.0}));

			Map<String, IGeneratorArgument> values = new HashMap<>();

			GeneratorArgumentInteger generatorArgumentInteger = new GeneratorArgumentInteger(INT_PARAMETER_NAME, 0);
			values.put(INT_PARAMETER_NAME, generatorArgumentInteger);

			GeneratorArgumentDouble generatorArgumentDouble = new GeneratorArgumentDouble(DOUBLE_PARAMETER_NAME, 0.0);
			values.put(DOUBLE_PARAMETER_NAME, generatorArgumentDouble);

			generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), values, null);

			GeneratorArgumentInteger argumentInteger = new GeneratorArgumentInteger(INT_PARAMETER_NAME, 5);
			values.put(INT_PARAMETER_NAME, argumentInteger);

			try{
				generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), values, null);
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException");
		}
	}

	@Test
	public void initializeWithMissingRequiredParameterTest(){
		TestAbstractGenerator generator = new TestAbstractGenerator();
		List<List<String>> inputDomain = new ArrayList<List<String>>();
		Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>(); 
		
		generator.addParameterDefinition(new GeneratorParameterInteger(INT_PARAMETER_NAME, true, 0));
		generator.addParameterDefinition(new GeneratorParameterDouble(DOUBLE_PARAMETER_NAME, true, 0.0));

		Map<String, IGeneratorArgument> values = new HashMap<>();

		GeneratorArgumentInteger generatorArgumentInteger = null;
		try {
			generatorArgumentInteger = new GeneratorArgumentInteger(INT_PARAMETER_NAME, 0);
		} catch (GeneratorException e) {
			fail();
		}
		values.put(INT_PARAMETER_NAME, generatorArgumentInteger);

		try{
			generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), values, null);
			fail("GeneratorException expected");
		} catch (GeneratorException e) {
		}

		GeneratorArgumentDouble generatorArgumentDouble = null;
		try {
			generatorArgumentDouble = new GeneratorArgumentDouble(DOUBLE_PARAMETER_NAME, 0.0);
		} catch (GeneratorException e) {
			fail();
		}
		values.put(DOUBLE_PARAMETER_NAME, generatorArgumentDouble);
		try{
			generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), values, null);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}

	@Test
	public void initializeWithMissingOptionalParameterTest(){
		TestAbstractGenerator generator = new TestAbstractGenerator();
		List<List<String>> inputDomain = new ArrayList<List<String>>();
		Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>(); 
		
		generator.addParameterDefinition(new GeneratorParameterInteger(INT_PARAMETER_NAME, false, 0));
		generator.addParameterDefinition(new GeneratorParameterDouble(DOUBLE_PARAMETER_NAME, false, 0.0));

		Map<String, IGeneratorArgument> arguments = new HashMap<>();
		try{
			generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), arguments, null);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}

		GeneratorArgumentInteger generatorArgumentInteger = null;
		try {
			generatorArgumentInteger = new GeneratorArgumentInteger(INT_PARAMETER_NAME, 0);
		} catch (GeneratorException e) {
			fail();
		}
		arguments.put(INT_PARAMETER_NAME, generatorArgumentInteger);
		try{
			generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), arguments, null);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}

		GeneratorArgumentDouble generatorArgumentDouble = null;
		try {
			generatorArgumentDouble = new GeneratorArgumentDouble(DOUBLE_PARAMETER_NAME, 0.0);
		} catch (GeneratorException e) {
			fail();
		}
		arguments.put(DOUBLE_PARAMETER_NAME, generatorArgumentDouble);
		try{
			generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), arguments, null);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}

	@Test
	public void initializeWithAdditionalParameterTest(){
		TestAbstractGenerator generator = new TestAbstractGenerator();
		List<List<String>> inputDomain = new ArrayList<List<String>>();
		Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>(); 
		
		generator.addParameterDefinition(new GeneratorParameterInteger(INT_PARAMETER_NAME, true, 0));

		Map<String, IGeneratorArgument> values = new HashMap<>();

		GeneratorArgumentInteger generatorArgumentInteger = null;
		try {
			generatorArgumentInteger = new GeneratorArgumentInteger(INT_PARAMETER_NAME, 0);
		} catch (GeneratorException e) {
			fail();
		}
		values.put(INT_PARAMETER_NAME, generatorArgumentInteger);

		try{
			generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), values, null);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}

		GeneratorArgumentDouble generatorArgumentDouble = null;
		try {
			generatorArgumentDouble = new GeneratorArgumentDouble(DOUBLE_PARAMETER_NAME, 0.0);
		} catch (GeneratorException e) {
			fail();
		}
		values.put(DOUBLE_PARAMETER_NAME, generatorArgumentDouble);
		try{
			generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), values, null);
			fail("GeneratorException expected");
		} catch (GeneratorException e) {
		}
	}
	
	@Test
	public void getRequiredParameterTest(){
		TestAbstractGenerator generator = new TestAbstractGenerator();
		List<List<String>> inputDomain = new ArrayList<List<String>>();
		Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>(); 

		generator.addParameterDefinition(new GeneratorParameterInteger(INT_PARAMETER_NAME, true, fDefaultIntParameterValue));
		generator.addParameterDefinition(new GeneratorParameterDouble(DOUBLE_PARAMETER_NAME, true, fDefaultDoubleParameterValue));
		generator.addParameterDefinition(new GeneratorParameterBoolean(BOOLEAN_PARAMETER_NAME, true, fDefaultBooleanParameterValue));
		generator.addParameterDefinition(new GeneratorParameterString(STRING_PARAMETER_NAME, true, fDefaultStringParameterValue));
		
		Map<String, IGeneratorArgument> values = new HashMap<>();

		GeneratorArgumentInteger generatorArgumentInteger = null;
		try {
			generatorArgumentInteger = new GeneratorArgumentInteger(INT_PARAMETER_NAME, fIntParameterValue);
		} catch (GeneratorException e) {
			fail();
		}
		values.put(INT_PARAMETER_NAME, generatorArgumentInteger);

		GeneratorArgumentDouble generatorArgumentDouble = null;
		try {
			generatorArgumentDouble = new GeneratorArgumentDouble(DOUBLE_PARAMETER_NAME, fDoubleParameterValue);
		} catch (GeneratorException e) {
			fail();
		}
		values.put(DOUBLE_PARAMETER_NAME, generatorArgumentDouble);

		GeneratorArgumentBoolean generatorArgumentBoolean = new GeneratorArgumentBoolean(BOOLEAN_PARAMETER_NAME, fBooleanParameterValue);
		values.put(BOOLEAN_PARAMETER_NAME, generatorArgumentBoolean);

		GeneratorArgumentString generatorArgumentString = null;
		try {
			generatorArgumentString = new GeneratorArgumentString(STRING_PARAMETER_NAME, fStringParameterValue);
		} catch (GeneratorException e) {
			fail();
		}
		values.put(STRING_PARAMETER_NAME, generatorArgumentString);
		
		try {
			generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), values, null);
			
			int intParameter = generator.getIntParameter(INT_PARAMETER_NAME);
			double doubleParameter = generator.getDoubleParameter(DOUBLE_PARAMETER_NAME);
			boolean booleanParameter = generator.getBooleanParameter(BOOLEAN_PARAMETER_NAME);
			String stringParameter = generator.getStringParameter(STRING_PARAMETER_NAME);
			
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
		Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>(); 

		generator.addParameterDefinition(new GeneratorParameterInteger(INT_PARAMETER_NAME, false, fDefaultIntParameterValue));
		generator.addParameterDefinition(new GeneratorParameterDouble(DOUBLE_PARAMETER_NAME, false, fDefaultDoubleParameterValue));
		generator.addParameterDefinition(new GeneratorParameterBoolean(BOOLEAN_PARAMETER_NAME, false, fDefaultBooleanParameterValue));
		generator.addParameterDefinition(new GeneratorParameterString(STRING_PARAMETER_NAME, false, fDefaultStringParameterValue));

		Map<String, IGeneratorArgument> values = new HashMap<>();
		
		try {
			generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), values, null);
			
			int intParameter = generator.getIntParameter(INT_PARAMETER_NAME);
			double doubleParameter = generator.getDoubleParameter(DOUBLE_PARAMETER_NAME);
			boolean booleanParameter = generator.getBooleanParameter(BOOLEAN_PARAMETER_NAME);
			String stringParameter = generator.getStringParameter(STRING_PARAMETER_NAME);
			
			assertEquals(fDefaultIntParameterValue, intParameter);
			assertEquals(fDefaultDoubleParameterValue, doubleParameter, 0.0);
			assertEquals(fDefaultBooleanParameterValue, booleanParameter);
			assertEquals(fDefaultStringParameterValue, stringParameter);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}

		GeneratorArgumentInteger generatorArgumentInteger = null;
		try {
			generatorArgumentInteger = new GeneratorArgumentInteger(INT_PARAMETER_NAME, fIntParameterValue);
		} catch (GeneratorException e) {
			fail();
		}
		values.put(INT_PARAMETER_NAME, generatorArgumentInteger);

		GeneratorArgumentDouble generatorArgumentDouble = null;
		try {
			generatorArgumentDouble = new GeneratorArgumentDouble(DOUBLE_PARAMETER_NAME, fDoubleParameterValue);
		} catch (GeneratorException e) {
			fail();
		}
		values.put(DOUBLE_PARAMETER_NAME, generatorArgumentDouble);

		GeneratorArgumentBoolean generatorArgumentBoolean = new GeneratorArgumentBoolean(BOOLEAN_PARAMETER_NAME, fBooleanParameterValue);
		values.put(BOOLEAN_PARAMETER_NAME, generatorArgumentBoolean);

		GeneratorArgumentString generatorArgumentString = null;
		try {
			generatorArgumentString = new GeneratorArgumentString(STRING_PARAMETER_NAME, fStringParameterValue);
		} catch (GeneratorException e) {
			fail();
		}
		values.put(STRING_PARAMETER_NAME, generatorArgumentString);
		
		try {
			generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), values, null);
			
			int intParameter = generator.getIntParameter(INT_PARAMETER_NAME);
			double doubleParameter = generator.getDoubleParameter(DOUBLE_PARAMETER_NAME);
			boolean booleanParameter = generator.getBooleanParameter(BOOLEAN_PARAMETER_NAME);
			String stringParameter = generator.getStringParameter(STRING_PARAMETER_NAME);
			
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
		Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>(); 

		try{
			generator.initialize(inputDomain, new HomebrewConstraintEvaluator<>(constraints), null, null);
			fail("GeneratorException expected");
		} catch (GeneratorException e) {
		}
	}

	private static class TestAbstractGenerator extends AbstractGenerator<String> {

		@Override
		public GeneratorType getGeneratorType() {
			return null;
		}
	}
//		
//	@Test
//	public void testGetRequiredParameterWithBounds(){
//		AbstractGenerator<String> generator = new AbstractGenerator<String>();
//		
//		generator.addParameterDefinition(new AbstractParameter("parameter", TYPE.INTEGER, true, 2, new Integer[]{1,2,3}));
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
//		generator.addParameterDefinition(new AbstractParameter("parameter", TYPE.INTEGER, false, defaultParameterValue, null));
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
//			generator.addParameterDefinition(new AbstractParameter("parameter", TYPE.INTEGER, true, 0, -5, 5));
//		} catch (GeneratorException e) {
//			e.printStackTrace();
//		}
//	}
//	
}
