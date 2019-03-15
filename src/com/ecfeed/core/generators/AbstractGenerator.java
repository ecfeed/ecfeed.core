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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.generators.algorithms.IAlgorithm;
import com.ecfeed.core.generators.api.*;
import com.ecfeed.core.model.IConstraint;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public class AbstractGenerator<E> implements IGenerator<E> {

	private List<IGeneratorParamDefinition> fParameterDefinitions = new ArrayList<IGeneratorParamDefinition>();
	private Map<String, IGeneratorArgument> fArguments = null;
	private IAlgorithm<E> fAlgorithm = null;
	private List<List<E>> fInput;
	private Collection<IConstraint<E>> fConstraints;
	private IEcfProgressMonitor fGeneratorProgressMonitor;
	
	private boolean fInitialized = false;
	
	@Override
	public void initialize(List<List<E>> inputDomain,
			Collection<IConstraint<E>> constraints,
            Map<String, IGeneratorArgument> arguments,
			IEcfProgressMonitor generatorProgressMonitor)
			throws GeneratorException {
		validateInput(inputDomain);
		validateArguments(arguments);
		fArguments = arguments;
		fInput = inputDomain;
		fConstraints = constraints;
		fGeneratorProgressMonitor = generatorProgressMonitor;
	
		fInitialized = true; 
	}

	@Override
	public List<E> next() throws GeneratorException {
		List<E> next = fAlgorithm.getNext();
		if(next != null){
			/*
			 * It's necessary to perform adapt operation on the copy.
			 * Otherwise we would affect algorithm internal state (e.g.
			 * if it keeps a history of generated vectors).
			 */
			List<E> copy = new ArrayList<E>(next);
			return adapt(copy);
		}
		else{
			return null;
		}
	}

	@Override
	public void reset(){
		fAlgorithm.reset();
	}

	@Override
	public List<IGeneratorParamDefinition> getParameterDefinitions() {
		return fParameterDefinitions;
	}

	@Override
	public int totalWork() {
		return fAlgorithm.totalWork();
	}

	@Override
	public int workProgress() {
		return fAlgorithm.workProgress();
	}

	@Override
	public int totalProgress() {
		return fAlgorithm.totalProgress();
	}

	@Override
	public void addConstraint(IConstraint<E> constraint) {
		fAlgorithm.addConstraint(constraint);
	}

	@Override
	public void removeConstraint(IConstraint<E> constraint) {
		fAlgorithm.removeConstraint(constraint);
	}

	@Override
	public Collection<? extends IConstraint<E>> getConstraints() {
		return fAlgorithm.getConstraints();
	}

	protected void validateArguments(Map<String, IGeneratorArgument> arguments) throws GeneratorException {
		int requiredParameters = 0;
		
		for(IGeneratorParamDefinition definition : fParameterDefinitions){
			IGeneratorArgument generatorArgument = arguments.get(definition.getName());
			Object providedValue = generatorArgument.getValue();
			if(providedValue == null){
				if(definition.isRequired()){
					GeneratorException.report("Value of required parameret " + definition.getName() + " is not provided");
				}
			}
			else if(!definition.test(providedValue)){
				GeneratorException.report("Value " + providedValue + " is not allowed for parameter " + definition.getName());
			}
			
			if(definition.isRequired()){
				++requiredParameters;
			}
		}
		
		if(arguments != null){
			for(String parameterName : arguments.keySet()){
				IGeneratorParamDefinition definition = getParameterDefinition(parameterName);
				if(definition == null){
					GeneratorException.report("Unknown parameter " + parameterName);
				}
			}
		}
		else if(requiredParameters > 0){
			GeneratorException.report("Unexpected null value");
		}
	}
	
	protected void setAlgorithm(IAlgorithm<E> algorithm) throws GeneratorException{
		fAlgorithm = algorithm;
		fAlgorithm.initialize(fInput, fConstraints, fGeneratorProgressMonitor);
	}

	protected IAlgorithm<E> getAlgorithm(){
		return fAlgorithm;
	}
	
	@Override
	public void cancel(){
		fAlgorithm.cancel();
	}
	
	protected void addParameterDefinition(IGeneratorParamDefinition definition){
		for(int i = 0; i < fParameterDefinitions.size(); i++){
			if(fParameterDefinitions.get(i).getName().equals(definition.getName())){
				fParameterDefinitions.set(i, definition);
			}
		}
		fParameterDefinitions.add(definition);
	}

	protected IGeneratorParamDefinition getParameterDefinition(String name) throws GeneratorException{
		for(IGeneratorParamDefinition parameter : fParameterDefinitions){
			if(parameter.getName().equals(name)){
				return parameter;
			}
		}
		GeneratorException.report("Parameter " + name + " is not defined for " + this.getClass().getName());
		return null;
	}


	protected int getIntParameter(String name) throws GeneratorException {
		if(!fInitialized){
			GeneratorException.report("Parameter values can be obtained after the generator is initialized");
		}
		Object value = getParameterValue(name, fArguments);
		if(value instanceof Integer == false){
			GeneratorException.report("Parameter type must be integer: " + name);
		}
		return (int)value;
	}

	protected boolean getBooleanParameter(String name) throws GeneratorException {
		Object value = getParameterValue(name, fArguments);
		if(value instanceof Boolean == false){
			GeneratorException.report("Parameter type must be boolean: " + name);
		}
		return (boolean)value;
	}

	protected double getDoubleParameter(String name) throws GeneratorException {
		Object value = getParameterValue(name, fArguments);
		if(value instanceof Double == false){
			GeneratorException.report("Parameter type must be double: " + name);
		}
		return (double)value;
	}
	
	protected String getStringParameter(String name) throws GeneratorException {
		Object value = getParameterValue(name, fArguments);
		if(value instanceof String == false){
			GeneratorException.report("Parameter type must be integer: " + name);
		}
		return (String)value;
	}


	protected List<E> adapt(List<E> values){
		if(values != null){
			for(IConstraint<E> constraint : getConstraints()){
				constraint.adapt(values);
			}
		}
		return values;
	}

	private void validateInput(List<? extends List<E>> inputDomain) throws GeneratorException {
		for(List<E> parameter : inputDomain){
			if(parameter.size() == 0){
				GeneratorException.report("Generator input domain cannot contain empty vectors");
			}
		}
	}

	private Object getParameterValue(String name, Map<String, IGeneratorArgument> arguments) throws GeneratorException {

		IGeneratorParamDefinition definition = null;
		for(IGeneratorParamDefinition def : fParameterDefinitions){
			if(def.getName().equals(name)){
				definition = def;
			}
		}
		if(definition == null){
			GeneratorException.report("Unknown parameter: " + name);
		}
		Object value = arguments.get(name).getValue();
		if(value == null){
			if(definition.isRequired()){
				GeneratorException.report("Required parameter not defined: " + name);
			}
			else{
				return definition.defaultValue();
			}
		}
		
		if(definition.allowedValues() != null){
			Object[] allowedValues = definition.allowedValues();
			boolean valueAllowed = false;
			for(Object allowed : allowedValues){
				if(value.equals(allowed)){
					valueAllowed = true;
				}
			}
			if(!valueAllowed){
				GeneratorException.report("Value " + value + " is not allowed for parameter " + name);
			}
		}
		return value;
	}
}
