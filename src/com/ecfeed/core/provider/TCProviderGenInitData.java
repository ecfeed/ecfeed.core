package com.ecfeed.core.provider;

import java.util.Collection;
import java.util.List;

import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.MethodNode;

public class TCProviderGenInitData implements ITCProviderInitData {

	private List<List<ChoiceNode>> fChoiceInput;
	private Collection<Constraint> fConstraints;
	private List<IGeneratorValue> fGeneratorArguments;
	private MethodNode fMethodNode;
	
	public TCProviderGenInitData(
			List<List<ChoiceNode>> choiceInput,
			Collection<Constraint> constraints,
			List<IGeneratorValue> generatorArguments,
			MethodNode methodNode) {
		
		fChoiceInput = choiceInput;
		fConstraints = constraints;
		fGeneratorArguments = generatorArguments;
		fMethodNode = methodNode;
	}
	
	public MethodNode getMethodNode() {
		return fMethodNode;
	}

	public List<List<ChoiceNode>> getChoiceInput() {
		return fChoiceInput;
	}
	
	public Collection<Constraint> getConstraints() {
		return fConstraints;
	}
	
	public List<IGeneratorValue> getGeneratorArguments() {
		return fGeneratorArguments;
	}
	
}
