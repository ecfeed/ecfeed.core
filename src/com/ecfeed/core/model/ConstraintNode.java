/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.List;
import java.util.Set;

import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class ConstraintNode extends AbstractNode{

	private Constraint fConstraint;

	@Override
	protected String getNonQualifiedName() {
		return getName();
	}

	@Override
	public void verifyName(String nameInIntrLanguage) {
	}

	@Override
	public int getMyIndex() {

		if (getMethodNode() == null) {
			return -1;
		}

		return getMethodNode().getConstraintNodes().indexOf(this);
	}

	@Override
	public String toString() {

		if (fConstraint == null) {
			return "EMPTY";
		}

		return ConstraintHelper.createSignature(fConstraint, new ExtLanguageManagerForJava());
	}

	@Override
	public void setName(String name) {

		super.setName(name);
		fConstraint.setName(name);
	}

	@Override
	public int getChildrenCount() {
		return 0;
	}

	@Override
	public ConstraintNode makeClone() {

		ConstraintNode copy = new ConstraintNode(getName(), fConstraint.makeClone(), getModelChangeRegistrator() );
		copy.setProperties(getProperties());
		return copy;
	}

	public ConstraintNode(String name, Constraint constraint, IModelChangeRegistrator modelChangeRegistrator) {

		super(name, modelChangeRegistrator);
		fConstraint = constraint;
	}

	public ConstraintNode(String name, Constraint constraint) {

		this(name, constraint, null);
	}

	public Constraint getConstraint() {

		return fConstraint;
	}

	public List<ChoiceNode> getListOfChoices() {
		return fConstraint.getChoices(); 
	}

	public MethodNode getMethodNode() {

		IAbstractNode parent = getParent();
		if (parent == null) {
			return null;
		}

		if (parent instanceof MethodNode) {
			return (MethodNode)parent;
		}

		return null;
	}

	public void setMethod(MethodNode method) {

		setParent(method);
		registerChange();
	}

	public EvaluationResult evaluate(List<ChoiceNode> values) {

		if (fConstraint != null) {
			return fConstraint.evaluate(values);
		}

		return EvaluationResult.FALSE;
	}

	public void derandomize() {
		fConstraint.derandomize();
	}

	public boolean mentions(ChoiceNode choice) {

		if (fConstraint.mentions(choice)) {
			return true;
		}

		return false;
	}

	public boolean mentions(MethodParameterNode parameter) {

		return fConstraint.mentions(parameter);
	}

	public boolean mentions(AbstractParameterNode parameter) {

		if (parameter instanceof MethodParameterNode) {
			MethodParameterNode param = (MethodParameterNode)parameter;
			return fConstraint.mentions(param);
		}

		if (parameter instanceof GlobalParameterNode) {
			GlobalParameterNode global = (GlobalParameterNode)parameter;
			for (MethodParameterNode methodParam: global.getLinkedMethodParameters()) {
				return fConstraint.mentions(methodParam);
			}
		}

		return false;
	}

	public boolean mentions(MethodParameterNode parameter, String label) {

		return fConstraint.mentions(parameter, label);
	}

	public boolean updateReferences(MethodNode method) {

		if (fConstraint.updateReferences(method)) {
			setParent(method);
			registerChange();
			return true;
		}

		return false;
	}

	public ConstraintNode getCopy(MethodNode method) {

		ConstraintNode copy = makeClone();

		if (copy.updateReferences(method))
			return copy;
		else {

			return null;
		}
	}

	@Override
	public boolean isMatch(IAbstractNode node) {

		if (node instanceof ConstraintNode == false) {
			return false;
		}

		ConstraintNode compared = (ConstraintNode)node;
		if (getConstraint().getPrecondition().isEqualTo(compared.getConstraint().getPrecondition()) == false) {
			return false;
		}

		if (getConstraint().getPostcondition().isEqualTo(compared.getConstraint().getPostcondition()) == false) {
			return false;
		}

		return super.isMatch(node);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {

		return visitor.visit(this);
	}

	public boolean isConsistent() {

		if (!areParametersConsistent()) {
			return false;
		}

		if (!areChoicesConsistent()) {
			return false;
		}

		if (!constraintsConsistent()) {
			return false;
		}

		return true;
	}

	private boolean areParametersConsistent() {

		final Set<AbstractParameterNode> referencedParameters = getConstraint().getReferencedParameters();
		final List<AbstractParameterNode> methodParameters = getMethodNode().getParameters();

		for (AbstractParameterNode referencedParameter : referencedParameters) {
			if (!isParameterConsistent(referencedParameter, methodParameters)) {
				return false;
			}
		}

		return true;
	}

	private boolean isParameterConsistent(

			AbstractParameterNode argParameter,
			List<AbstractParameterNode> methodParameters) {

		for (AbstractParameterNode param : methodParameters) {
			MethodParameterNode methodParam = (MethodParameterNode) param;

			if (methodParam.isLinked() && methodParam.getLinkToGlobalParameter().equals(argParameter)) {
				return true;
			}
		}

		if (!methodParameters.contains(argParameter)) {
			return false;
		}

		return true;
	}

	private boolean areChoicesConsistent() {

		Set<ChoiceNode> referencedChoices = getConstraint().getReferencedChoices();

		for (ChoiceNode choiceNode : referencedChoices) {

			if (!isChoiceConsistent(choiceNode)) {
				return false;
			}
		}

		return true;
	}

	private boolean isChoiceConsistent(ChoiceNode choiceNode) {

		if (choiceNode.getQualifiedName() == null) {
			return false;
		}

		if (!isOkForExpectedParameter(choiceNode)) {
			return false;
		}

		AbstractParameterNode parameter = choiceNode.getParameter();
		List<MethodNode> parameterMethods = parameter.getMethods();

		if (parameterMethods == null) {
			return false;
		}

		MethodNode methodNode = getMethodNode();

		if (parameterMethods.contains(methodNode) == false) {
			return false;
		}

		return true;
	}

	private static boolean isOkForExpectedParameter(ChoiceNode choiceNode) {

		AbstractParameterNode parameter = choiceNode.getParameter();

		if (parameter == null && !isMethodParameterNodeExpected(parameter)) {
			return false;
		}

		return true;
	}

	private static boolean isMethodParameterNodeExpected(AbstractParameterNode parameter) {

		if (!(parameter instanceof MethodParameterNode)) {
			return false;
		}

		if (((MethodParameterNode)parameter).isExpected()) {
			return true;
		}

		return false;
	}

	private boolean constraintsConsistent() {

		for (MethodParameterNode parameter : getMethodNode().getMethodParameters()) {
			if (!isConsistentForParameter(parameter)) {
				return false;
			}
		}
		return true;
	}

	private boolean isConsistentForParameter(MethodParameterNode parameter) {

		String typeName = parameter.getType();

		if (parameter.isExpected()) {
			return true;
		}

		if (isForbiddenTypeForOrderRelations(typeName)) {

			if (fConstraint.mentionsParameterAndOrderRelation(parameter)) {
				return false;
			}
		}

		if (!checkLabels(parameter)) {
			return false;
		}

		return true;
	}

	private boolean isForbiddenTypeForOrderRelations(String typeName) {

		if (JavaLanguageHelper.isUserType(typeName)) {
			return true;
		}

		if (JavaLanguageHelper.isBooleanTypeName(typeName)) {
			return true;
		}

		return false;
	}

	private boolean checkLabels(MethodParameterNode parameter) {

		for (String label : getConstraint().getReferencedLabels(parameter)) {
			if (!parameter.getLeafLabels().contains(label)) {
				return false;
			}
		}
		return true;
	}


	@Override
	public int getMaxIndex() {
		if (getMethodNode() != null) {
			return getMethodNode().getConstraintNodes().size();
		}
		return -1;
	}

	boolean mentionsChoiceOfParameter(MethodParameterNode methodParameter) {

		return fConstraint.mentionsChoiceOfParameter(methodParameter);
	}

}
