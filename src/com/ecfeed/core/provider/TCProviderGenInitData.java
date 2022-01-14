package com.ecfeed.core.provider;

import java.util.List;

import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.AmbiguousConstraintAction;

public class TCProviderGenInitData implements ITCProviderInitData {

	private List<List<ChoiceNode>> fChoiceInput;
	private List<Constraint> fConstraints;
	private AmbiguousConstraintAction fAmbiguousConstraintAction;
	private List<IGeneratorValue> fGeneratorArguments;
	private MethodNode fMethodNode;
	
	public TCProviderGenInitData(
			List<List<ChoiceNode>> choiceInput,
			List<Constraint> constraints,
			AmbiguousConstraintAction ambiguousConstraintAction,
			List<IGeneratorValue> generatorArguments,
			MethodNode methodNode) {
		
		fChoiceInput = choiceInput;
		fConstraints = constraints;
		fAmbiguousConstraintAction = ambiguousConstraintAction;
		fGeneratorArguments = generatorArguments;
		fMethodNode = methodNode;
	}
	
	public MethodNode getMethodNode() {
		return fMethodNode;
	}

	public List<List<ChoiceNode>> getChoiceInput() {
		return fChoiceInput;
	}
	
	public List<Constraint> getConstraints() {
		return fConstraints;
	}
	
	public AmbiguousConstraintAction getAmbiguousConstraintAction()  {
		return fAmbiguousConstraintAction;
	}
	
	public List<IGeneratorValue> getGeneratorArguments() {
		return fGeneratorArguments;
	}
	
}
