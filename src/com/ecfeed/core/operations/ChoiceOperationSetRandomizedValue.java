package com.ecfeed.core.operations;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class ChoiceOperationSetRandomizedValue extends AbstractModelOperation { 

	private boolean fNewRandomized;
	private boolean fOriginalRandomized;
	private ChoiceNode fChoiceNode;
	private ITypeAdapterProvider fAdapterProvider;


	public ChoiceOperationSetRandomizedValue(
			ChoiceNode choiceNode, 
			boolean newRandomized, 
			ITypeAdapterProvider adapterProvider,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.SET_CHOICE_RANDOMIZED_FLAG, extLanguageManager);

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
			return typeAdapter.adapt(
					fChoiceNode.getValueString(), 
					randomized, 
					ERunMode.QUIET, 
					getExtLanguageManager());

		} catch (RuntimeException ex) {
			ModelOperationException.report(ex.getMessage());
		}

		return null;
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	private class ReverseOperation extends AbstractModelOperation {
		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(ChoiceOperationSetRandomizedValue.this.getName(), extLanguageManager);
		}

		@Override
		public void execute() throws ModelOperationException {
			adaptChoice(fOriginalRandomized);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new ChoiceOperationSetRandomizedValue(fChoiceNode, fNewRandomized, fAdapterProvider, getExtLanguageManager());
		}

		@Override
		public String getName() {
			return null;
		}

	}

}

