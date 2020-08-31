package com.ecfeed.core.operations;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExtLanguage;

public class ChoiceOperationSetRandomizedValue extends AbstractModelOperation { 

	private boolean fNewRandomized;
	private boolean fOriginalRandomized;
	private ChoiceNode fChoiceNode;
	private ITypeAdapterProvider fAdapterProvider;


	public ChoiceOperationSetRandomizedValue(
			ChoiceNode choiceNode, 
			boolean newRandomized, 
			ITypeAdapterProvider adapterProvider,
			ExtLanguage viewMode) {

		super(OperationNames.SET_CHOICE_RANDOMIZED_FLAG, viewMode);

		fNewRandomized = newRandomized;
		fChoiceNode = choiceNode;
		fAdapterProvider = adapterProvider;

		fOriginalRandomized = choiceNode.isRandomizedValue();
	}

	@Override
	public void execute() throws ModelOperationException {
		adaptChoice(fNewRandomized);
		markModelUpdated();
	}

	private void adaptChoice(boolean newRandomized) throws ModelOperationException {

		String newValue = adaptChoiceValue(newRandomized);
		fChoiceNode.setValueString(newValue);
		fChoiceNode.setRandomizedValue(newRandomized);
	}

	private String adaptChoiceValue(boolean randomized) throws ModelOperationException {

		String type = fChoiceNode.getParameter().getType();

		ITypeAdapter<?> typeAdapter = fAdapterProvider.getAdapter(type); 

		try {
			return typeAdapter.convert(
					fChoiceNode.getValueString(), randomized, ERunMode.QUIET);

		} catch (RuntimeException ex) {
			ModelOperationException.report(ex.getMessage());
		}

		return null;
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getViewMode());
	}

	private class ReverseOperation extends AbstractModelOperation {
		public ReverseOperation(ExtLanguage viewMode) {
			super(ChoiceOperationSetRandomizedValue.this.getName(), viewMode);
		}

		@Override
		public void execute() throws ModelOperationException {
			adaptChoice(fOriginalRandomized);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ChoiceOperationSetRandomizedValue(fChoiceNode, fNewRandomized, fAdapterProvider, getViewMode());
		}

		@Override
		public String getName() {
			return null;
		}

	}

}

