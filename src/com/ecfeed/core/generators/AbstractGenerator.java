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

	private Map<IParameterDefinition, IGeneratorValue> fArguments = null;
	private IAlgorithm<E> fAlgorithm = null;
	private List<List<E>> fInput;
	private IConstraintEvaluator<E> fConstraintEvaluator;
	private IEcfProgressMonitor fGeneratorProgressMonitor;
	
	@Override
	public void initialize(List<List<E>> inputDomain,
						   IConstraintEvaluator<E> constraintEvaluator,
						   List<IGeneratorValue> generatorParameters,
			IEcfProgressMonitor generatorProgressMonitor) {
		validateInput(inputDomain);
//		validateArguments(arguments);

		fArguments = new HashMap<>();
		for(IGeneratorValue val : generatorParameters)
			fArguments.put(val.getDefinition(), val);

		for(IParameterDefinition paramDef : getParameterDefinitions() )
			if(!fArguments.containsKey(paramDef))
				fArguments.put( paramDef, new GeneratorValue(paramDef, null));

		Set<IParameterDefinition> keyset = new HashSet<>(fArguments.keySet());
		for(IParameterDefinition paramDef : getParameterDefinitions() )
			if(keyset.contains(paramDef))
				keyset.remove(paramDef);
		if(!keyset.isEmpty())
			GeneratorExceptionHelper.reportException("Unknown parameters for generator: " + keyset);


		fInput = inputDomain;
		fConstraintEvaluator = constraintEvaluator;
		fGeneratorProgressMonitor = generatorProgressMonitor;
	}

	@Override
	public List<E> next() {
		List<E> next = fAlgorithm.getNext();
		if(next != null){
			/*
			 * It's necessary to perform adapt operation on the copy.
			 * Otherwise we would affect algorithm internal state (e.g.
			 * if it keeps a history of generated vectors).
			 */
			List<E> copyOfTestCase = new ArrayList<E>(next);
			return adapt(copyOfTestCase);
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
	public IParameterDefinition getParameterDefinition(String name) {

		for(IParameterDefinition parameter : getParameterDefinitions() ){
			if(parameter.getName().equals(name)){
				return parameter;
			}
		}

		GeneratorExceptionHelper.reportException("Parameter " + name + " is not defined for " + this.getClass().getName());
		return null;
	}


	@Override
	public IConstraintEvaluator<E> getConstraintEvaluator() {
		return fAlgorithm.getConstraintEvaluator();
	}

	protected void setAlgorithm(IAlgorithm<E> algorithm) {
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
		for(int i = 0; i < getParameterDefinitions().size(); i++){
			if(getParameterDefinitions().get(i).getName().equals(definition.getName())){
				ExceptionHelper.reportRuntimeException("Repeated name in parameter definition.");
			}
		}
		getParameterDefinitions().add(definition);
	}

	protected List<E> adapt(List<E> testCaseValues)
	{
		return fConstraintEvaluator.setExpectedValues(testCaseValues);
	}

	private void validateInput(List<? extends List<E>> inputDomain) {
		
		for(List<E> parameter : inputDomain){
			if(parameter.size() == 0){
				GeneratorExceptionHelper.reportException("Generator input domain cannot contain empty vectors");
			}
		}
	}

	protected Object getParameterValue(IParameterDefinition definition) {
		return fArguments.get(definition).getValue();
	}

}
