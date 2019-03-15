package com.ecfeed.core.provider;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.generators.api.IGeneratorArgument;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.IConstraint;

public class TCProviderGenInitData implements ITCProviderInitData {

	private List<List<ChoiceNode>> fChoiceInput;
	private Collection<IConstraint<ChoiceNode>> fConstraints;
	private Map<String, IGeneratorArgument> fGeneratorArguments;
	
	public TCProviderGenInitData(
			List<List<ChoiceNode>> choiceInput,
			Collection<IConstraint<ChoiceNode>> constraints,
			Map<String, IGeneratorArgument> generatorArguments) {
		
		fChoiceInput = choiceInput;
		fConstraints = constraints;
		fGeneratorArguments = generatorArguments;
	}

	public List<List<ChoiceNode>> getChoiceInput() {
		return fChoiceInput;
	}
	
	public Collection<IConstraint<ChoiceNode>> getConstraints() {
		return fConstraints;
	}
	
	public Map<String, IGeneratorArgument> getGeneratorArguments() {
		return fGeneratorArguments;
	}
	
}
