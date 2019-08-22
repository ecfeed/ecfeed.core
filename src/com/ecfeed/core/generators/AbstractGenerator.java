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

import java.util.*;

import com.ecfeed.core.generators.algorithms.IAlgorithm;
import com.ecfeed.core.generators.api.*;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IEcfProgressMonitor;

public abstract class AbstractGenerator<E> implements IGenerator<E> {

	private List<IParameterDefinition> fParameterDefinitions = new ArrayList<IParameterDefinition>();
	private Map<IParameterDefinition, IGeneratorValue> fArguments = null;
	private IAlgorithm<E> fAlgorithm = null;
	private List<List<E>> fInput;
	private IConstraintEvaluator<E> fConstraintEvaluator;
	private IEcfProgressMonitor fGeneratorProgressMonitor;
	
	private boolean fInitialized = false;
	
	@Override
	public void initialize(List<List<E>> inputDomain,
						   IConstraintEvaluator<E> constraintEvaluator,
						   List<IGeneratorValue> arguments,
			IEcfProgressMonitor generatorProgressMonitor)
			throws GeneratorException {
		validateInput(inputDomain);
//		validateArguments(arguments);

		fArguments = new HashMap<>();
		for(IGeneratorValue val : arguments)
			fArguments.put(val.getDefinition(), val);

		for(IParameterDefinition paramDef : fParameterDefinitions )
			if(!fArguments.containsKey(paramDef))
				fArguments.put( paramDef, new GeneratorValue(paramDef, null));

		Set<IParameterDefinition> keyset = new HashSet<>(fArguments.keySet());
		for(IParameterDefinition paramDef : fParameterDefinitions)
			if(keyset.contains(paramDef))
				keyset.remove(paramDef);
		if(!keyset.isEmpty())
			GeneratorException.report("Unknown parameters for generator: " + keyset);


		fInput = inputDomain;
		fConstraintEvaluator = constraintEvaluator;
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
	public List<IParameterDefinition> getParameterDefinitions() {
		return fParameterDefinitions;
	}



	@Override
	public IConstraintEvaluator<E> getConstraintEvaluator() {
		return fAlgorithm.getConstraintEvaluator();
	}

	protected void setAlgorithm(IAlgorithm<E> algorithm) throws GeneratorException{
		fAlgorithm = algorithm;
		fAlgorithm.initialize(fInput, fConstraintEvaluator, fGeneratorProgressMonitor);
	}

	protected IAlgorithm<E> getAlgorithm(){
		return fAlgorithm;
	}
	
	@Override
	public void cancel(){
		
		if (fAlgorithm != null) {
			fAlgorithm.cancel();	
		}
		
	}
	
	protected void addParameterDefinition(IParameterDefinition definition){
		for(int i = 0; i < fParameterDefinitions.size(); i++){
			if(fParameterDefinitions.get(i).getName().equals(definition.getName())){
				ExceptionHelper.reportRuntimeException("Repeated name in parameter definition.");
			}
		}
		fParameterDefinitions.add(definition);
	}

	protected IParameterDefinition getParameterDefinition(String name) throws GeneratorException{
		for(IParameterDefinition parameter : fParameterDefinitions){
			if(parameter.getName().equals(name)){
				return parameter;
			}
		}
		GeneratorException.report("Parameter " + name + " is not defined for " + this.getClass().getName());
		return null;
	}


	protected List<E> adapt(List<E> values)
	{
		return fConstraintEvaluator.adapt(values);
	}

	private void validateInput(List<? extends List<E>> inputDomain) throws GeneratorException {
		for(List<E> parameter : inputDomain){
			if(parameter.size() == 0){
				GeneratorException.report("Generator input domain cannot contain empty vectors");
			}
		}
	}

	protected Object getParameterValue(IParameterDefinition definition) {
		return fArguments.get(definition).getValue();
	}

	private Object getProvidedValue(IGeneratorValue generatorArgument) {

		if (generatorArgument == null) {
			return null;
		}

		return generatorArgument.getValue();
	}

}
