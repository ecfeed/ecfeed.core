package com.ecfeed.core.operations.nodes;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.type.adapter.ITypeAdapter;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class OnChoiceOperationSetRandomizedValue extends AbstractModelOperation { 

	private boolean fNewRandomized;
	private boolean fOriginalRandomized;
	private ChoiceNode fChoiceNode;


	public OnChoiceOperationSetRandomizedValue(
			ChoiceNode choiceNode, 
			boolean newRandomized, 
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.SET_CHOICE_RANDOMIZED_FLAG, extLanguageManager);

		fNewRandomized = newRandomized;
		fChoiceNode = choiceNode;

		fOriginalRandomized = choiceNode.isRandomizedValue();
	}

	@Override
	public void execute() {
		adaptChoice(fNewRandomized);
		markModelUpdated();
	}

	private void adaptChoice(boolean newRandomized) {

		String newValue = adaptChoiceValue(newRandomized);
		fChoiceNode.setValueString(newValue);
		fChoiceNode.setRandomizedValue(newRandomized);
	}

	private String adaptChoiceValue(boolean randomized) {

		String type = fChoiceNode.getParameter().getType();

		ITypeAdapter<?> typeAdapter = JavaLanguageHelper.getAdapter(type); 

		try {
			return typeAdapter.adapt(
					fChoiceNode.getValueString(), 
					randomized, 
					ERunMode.QUIET, 
					getExtLanguageManager());

		} catch (RuntimeException ex) {
			ExceptionHelper.reportRuntimeException(ex.getMessage());
		}

		return null;
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ReverseOperation(getExtLanguageManager());
	}

	private class ReverseOperation extends AbstractModelOperation {
		public ReverseOperation(IExtLanguageManager extLanguageManager) {
			super(OnChoiceOperationSetRandomizedValue.this.getName(), extLanguageManager);
		}

		@Override
		public void execute() {
			adaptChoice(fOriginalRandomized);
			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return new OnChoiceOperationSetRandomizedValue(fChoiceNode, fNewRandomized, getExtLanguageManager());
		}

		@Override
		public String getName() {
			return null;
		}

	}

}

