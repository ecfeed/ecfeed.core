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
import java.util.Optional;

import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;

public class ConstraintNode extends AbstractNode {

	private Constraint fConstraint;

	public ConstraintNode(String name, Constraint constraint, IModelChangeRegistrator modelChangeRegistrator) {

		super(name, modelChangeRegistrator);
		fConstraint = constraint;
	}

	public ConstraintNode(String name, Constraint constraint) {

		this(name, constraint, null);
	}

	@Override
	public String getNonQualifiedName() {
		return getName();
	}

	@Override
	public void verifyName(String nameInIntrLanguage) {
	}

	@Override
	public int getMyIndex() {

		IAbstractNode parent = getParent();

		if (!(parent instanceof IConstraintsParentNode)) {
			return -1;
		}

		IConstraintsParentNode constraintsParentNode = (IConstraintsParentNode) parent;

		return constraintsParentNode.getConstraintNodes().indexOf(this);
	}

	@Override
	public String toString() {

		if (fConstraint == null) {
			return "EMPTY";
		}

		return ConstraintHelper.createSignatureOfConditions(fConstraint, new ExtLanguageManagerForJava());
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

	//	@Override
	//	public ConstraintNode makeClone() {
	//
	//		ConstraintNode copy = new ConstraintNode(getName(), fConstraint.makeClone(), getModelChangeRegistrator() );
	//		copy.setProperties(getProperties());
	//		
	//		return copy;
	//	}

	public ConstraintNode createCopy(NodeMapper mapper) {// TODO MO-RE obsolete

		Constraint copyOfConstraint = fConstraint.createCopy(mapper);

		ConstraintNode copyOfConstraintNode = new ConstraintNode(getName(), copyOfConstraint, getModelChangeRegistrator());

		copyOfConstraintNode.setProperties(getProperties());

		return copyOfConstraintNode;
	}

	@Override
	public ConstraintNode makeClone(Optional<NodeMapper> mapper) {

		Constraint copyOfConstraint = fConstraint.makeClone(mapper);

		ConstraintNode copyOfConstraintNode = new ConstraintNode(getName(), copyOfConstraint, getModelChangeRegistrator());

		copyOfConstraintNode.setProperties(getProperties());

		return copyOfConstraintNode;
	}

	public Constraint getConstraint() {

		return fConstraint;
	}

	public List<ChoiceNode> getListOfChoices() {
		return fConstraint.getChoices(); 
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

	public boolean mentions(BasicParameterNode parameter) {

		if (fConstraint.mentions(parameter)) {
			return true;
		}

		if (parameter.isGlobalParameter()) {
			return mentionsGlobalParameter(parameter);
		}

		return false;
	}

	private boolean mentionsGlobalParameter(BasicParameterNode globalBasicParameterNode) {

		List<AbstractParameterNode> linkedParameters = 
				AbstractParameterNodeHelper.getLinkedParameters(globalBasicParameterNode);

		for (AbstractParameterNode linkedParameter: linkedParameters) {

			if (linkedParameter instanceof BasicParameterNode) {
				return fConstraint.mentions((BasicParameterNode)linkedParameter);
			}
		}

		return false;
	}

	public boolean mentionsAnyOfParameters(
			List<BasicParameterNode> basicParameterNodesToDelete) {

		for (BasicParameterNode basicParameterNode : basicParameterNodesToDelete) {
			if (mentions(basicParameterNode)) {
				return true;
			}
		}

		return false;
	}


	public boolean mentions(BasicParameterNode parameter, String label) {

		return fConstraint.mentions(parameter, label);
	}

	//	public boolean updateReferences(IParametersAndConstraintsParentNode parent) {
	//
	//		if (fConstraint.updateReferences(parent)) {
	//			setParent(parent);
	//			registerChange();
	//			return true;
	//		}
	//
	//		return false;
	//	}

	public ConstraintNode getCopy(IParametersAndConstraintsParentNode parent) { // TODO MO-RE obsolete

		ConstraintNode copy = makeClone(Optional.empty());
		return copy;

		//		if (copy.updateReferences(parent))
		//			return copy;
		//		else {
		//			return null;
		//		}
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

		IAbstractNode parent = getParent();

		if (!(parent instanceof IConstraintsParentNode)) {
			return false;
		}

		MethodNode parentMethodNode = (MethodNode) parent;

		Constraint constraint = getConstraint();

		if (constraint.isConsistent(parentMethodNode)) {
			return true;
		}

		return false;

		//		if (!areParametersConsistent()) {
		//			return false;
		//		}
		//
		//		if (!areChoicesConsistent()) {
		//			return false;
		//		}
		//
		//		if (!constraintsConsistent()) {
		//			return false;
		//		}
		//
		//		return true;
	}

	//	private boolean areParametersConsistent() {
	//
	//		final Set<BasicParameterNode> parametersUsedInConstraint = getConstraint().getReferencedParameters();
	//
	//		IParametersParentNode parentOfConstraint = (IParametersParentNode) getParent();
	//
	//		for (BasicParameterNode basicParameterNode : parametersUsedInConstraint) {
	//
	//			if (!isParameterConsistent(basicParameterNode, parentOfConstraint)) {
	//				return false;
	//			}
	//		}
	//
	//		//		final List<AbstractParameterNode> parametersOfParent = parentOfConstraint.getParameters();
	//		//
	//		//		for (BasicParameterNode referencedParameter : referencedParameters) {
	//		//			if (!isParameterConsistentOld(referencedParameter, parametersOfParent)) {
	//		//				return false;
	//		//			}
	//		//		}
	//
	//		return true;
	//	}

	//	private boolean isParameterConsistent(
	//			BasicParameterNode basicParameterNode, IParametersParentNode parentOfConstraint) {
	//
	//		IParametersParentNode parentOfParameter = basicParameterNode.getParent();
	//
	//		if (parentOfParameter == parentOfConstraint) {
	//
	//			return parentOfConstraintContainsBasicParameter(parentOfConstraint, basicParameterNode);
	//		}
	//
	//		CompositeParameterNode topCompositeOfBasicParameter = 
	//				AbstractParameterNodeHelper.getTopComposite(basicParameterNode);
	//
	//		if (topCompositeOfBasicParameter != null) {
	//
	//			return parentOfConstraintContainsLinkToCompositeParameter(
	//					parentOfConstraint, topCompositeOfBasicParameter);
	//		}
	//
	//		return parentOfConstraintContainsLinkToBasicParameter(parentOfConstraint, basicParameterNode);
	//	}


	//	private boolean parentOfConstraintContainsBasicParameter(IParametersParentNode parentOfConstraint,
	//			BasicParameterNode basicParameterNode) {
	//		List<AbstractParameterNode> parametersOfParent = parentOfConstraint.getParameters();
	//
	//		if (parametersOfParent.contains(basicParameterNode)) {
	//			return true;
	//		}
	//
	//		return false;
	//	}

	//	private boolean parentOfConstraintContainsLinkToCompositeParameter(
	//			IParametersParentNode parentOfConstraint,
	//			CompositeParameterNode topCompositeOfBasicParameter) {
	//
	//		List<AbstractParameterNode> parametersOfParent = parentOfConstraint.getParameters();
	//
	//		for (AbstractParameterNode abstractParameterNode : parametersOfParent) {
	//
	//			AbstractParameterNode linkToGlobalParameter = abstractParameterNode.getLinkToGlobalParameter();
	//
	//			if (linkToGlobalParameter.equals(topCompositeOfBasicParameter)) {
	//				return true;
	//			}
	//		}
	//
	//		return false;
	//	}


	//	private boolean parentOfConstraintContainsLinkToBasicParameter(
	//			IParametersParentNode parentOfConstraint,
	//			BasicParameterNode topCompositeOfBasicParameter) {
	//
	//		List<AbstractParameterNode> parametersOfParent = parentOfConstraint.getParameters();
	//
	//		for (AbstractParameterNode abstractParameterNode : parametersOfParent) {
	//
	//			if (abstractParameterNode.getLinkToGlobalParameter().equals(topCompositeOfBasicParameter)) {
	//				return true;
	//			}
	//		}
	//
	//		return false;
	//	}

	//	private boolean isParameterConsistentOld(
	//			BasicParameterNode argParameter,
	//			List<AbstractParameterNode> methodParameters) {
	//
	//		for (AbstractParameterNode param : methodParameters) {
	//
	//			if (param instanceof BasicParameterNode) {
	//				BasicParameterNode methodParam = (BasicParameterNode) param;
	//
	//				if (methodParam.isLinked() && methodParam.getLinkToGlobalParameter().equals(argParameter)) {
	//					return true;
	//				}
	//			} 			
	//		}
	//
	//		if (methodParameters.contains(argParameter)) {
	//			return true;
	//		}
	//
	//		return false;
	//	}

	//	private boolean areChoicesConsistent() {
	//
	//		Set<ChoiceNode> referencedChoices = getConstraint().getReferencedChoices();
	//
	//		for (ChoiceNode choiceNode : referencedChoices) {
	//
	//			if (!isChoiceConsistent(choiceNode)) {
	//				return false;
	//			}
	//		}
	//
	//		return true;
	//	}

	//	private boolean isChoiceConsistent(ChoiceNode choiceNode) {
	//
	//		if (choiceNode.getQualifiedName() == null) {
	//			return false;
	//		}
	//
	//		if (!isOkForExpectedParameter(choiceNode)) {
	//			return false;
	//		}
	//
	//		BasicParameterNode parameter = choiceNode.getParameter();
	//		List<MethodNode> parameterMethods = parameter.getMethods();
	//
	//		if (parameterMethods == null) {
	//			return false;
	//		}
	//
	//		IParametersParentNode parametersParentNode = (IParametersParentNode) getParent();
	//
	//		if (!parameterMethods.contains(parametersParentNode)) {
	//			return false;
	//		}
	//
	//		return true;
	//	}

	//	private static boolean isOkForExpectedParameter(ChoiceNode choiceNode) {
	//
	//		BasicParameterNode parameter = choiceNode.getParameter();
	//
	//		if (parameter == null && !isMethodParameterNodeExpected(parameter)) {
	//			return false;
	//		}
	//
	//		return true;
	//	}

	//	private static boolean isMethodParameterNodeExpected(BasicParameterNode parameter) {
	//
	//		if (!(parameter instanceof BasicParameterNode)) {
	//			return false;
	//		}
	//
	//		if (((BasicParameterNode)parameter).isExpected()) {
	//			return true;
	//		}
	//
	//		return false;
	//	}

	//	private boolean constraintsConsistent() {
	//
	//		IParametersParentNode parametersParentNode = (IParametersParentNode) getParent();
	//
	//		for (AbstractParameterNode abstractParameterNode : parametersParentNode.getParameters()) {
	//
	//			if (!(abstractParameterNode instanceof BasicParameterNode)) {
	//				continue;
	//			}
	//
	//			BasicParameterNode basicParameterNode = (BasicParameterNode) abstractParameterNode;
	//
	//			if (!isConsistentForParameter(basicParameterNode)) {
	//				return false;
	//			}
	//		}
	//
	//		return true;
	//	}

	//	private boolean isConsistentForParameter(BasicParameterNode parameter) {
	//
	//		String typeName = parameter.getType();
	//
	//		if (parameter.isExpected()) {
	//			return true;
	//		}
	//
	//		if (isForbiddenTypeForOrderRelations(typeName)) {
	//
	//			if (fConstraint.mentionsParameterAndOrderRelation(parameter)) {
	//				return false;
	//			}
	//		}
	//
	//		if (!checkLabels(parameter)) {
	//			return false;
	//		}
	//
	//		return true;
	//	}

	//	private boolean isForbiddenTypeForOrderRelations(String typeName) {
	//
	//		if (JavaLanguageHelper.isUserType(typeName)) {
	//			return true;
	//		}
	//
	//		if (JavaLanguageHelper.isBooleanTypeName(typeName)) {
	//			return true;
	//		}
	//
	//		return false;
	//	}

	//	private boolean checkLabels(BasicParameterNode parameter) {
	//
	//		for (String label : getConstraint().getReferencedLabels(parameter)) {
	//			if (!parameter.getLeafLabels().contains(label)) {
	//				return false;
	//			}
	//		}
	//		return true;
	//	}


	@Override
	public int getMaxIndex() {

		IConstraintsParentNode constraintsParentNode = (IConstraintsParentNode) getParent();

		return constraintsParentNode.getConstraintNodes().size();
	}

	boolean mentionsChoiceOfParameter(BasicParameterNode methodParameter) {

		return fConstraint.mentionsChoiceOfParameter(methodParameter);
	}

	@Override
	public List<IAbstractNode> getDirectChildren() {
		return getChildren();
	}

	@Override
	public boolean canAddChild(IAbstractNode child) {

		return false;
	}

}
