package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ObjectHelper;

public class CompositeParameterNodeHelper {

	public static BasicParameterNode addNewBasicParameterToComposite(
			CompositeParameterNode compositeParameterNode, 
			String name, 
			String type,
			String defaultValue,
			boolean setParent,
			IModelChangeRegistrator modelChangeRegistrator) {

		BasicParameterNode basicParameterNode = 
				new BasicParameterNode (name, type, defaultValue, false, modelChangeRegistrator);

		if (setParent) {
			basicParameterNode.setParent(compositeParameterNode);
		}

		compositeParameterNode.addParameter(basicParameterNode);

		return basicParameterNode;
	}

	public static CompositeParameterNode addNewCompositeParameter(
			CompositeParameterNode parentCompositeParameterNode, 
			String childCompositeName,
			boolean setParent,
			IModelChangeRegistrator modelChangeRegistrator) {

		CompositeParameterNode childCompositeParameterNode = 
				new CompositeParameterNode(childCompositeName, modelChangeRegistrator);

		if (setParent) {
			childCompositeParameterNode.setParent(parentCompositeParameterNode);
		}

		parentCompositeParameterNode.addParameter(childCompositeParameterNode);

		return childCompositeParameterNode;
	}

	public static CompositeParameterNode addNewCompositeParameterNodeToCompositeParameter(
			CompositeParameterNode compositeParameterNode, String name, IModelChangeRegistrator modelChangeRegistrator) {

		CompositeParameterNode parameterNode = new CompositeParameterNode (name, modelChangeRegistrator);
		compositeParameterNode.addParameter(parameterNode);

		return parameterNode;
	}

	public static List<BasicParameterNode> getAllChildBasicParameters(CompositeParameterNode compositeParameterNode) {

		List<BasicParameterNode> result = new ArrayList<>();

		getAllChildBasicParametersRecursive(compositeParameterNode, result);

		return result;
	}

	private static void getAllChildBasicParametersRecursive(
			CompositeParameterNode compositeParameterNode,
			List<BasicParameterNode> inOutBasicParameterNodes) {

		List<IAbstractNode> children = compositeParameterNode.getChildren();

		for (IAbstractNode abstractNode : children) {

			if (abstractNode instanceof BasicParameterNode) {
				inOutBasicParameterNodes.add((BasicParameterNode) abstractNode);
			}

			if (abstractNode instanceof CompositeParameterNode) {
				getAllChildBasicParametersRecursive((CompositeParameterNode) abstractNode, inOutBasicParameterNodes);
			}
		}
	}

	public static List<CompositeParameterNode> getLinkedCompositeParameters(
			CompositeParameterNode compositeParameterNode) {

		List<CompositeParameterNode> resultLinkedCompositeParameters = new ArrayList<>();

		RootNode rootNode = RootNodeHelper.findRootNode(compositeParameterNode);

		getLinkedCompositeParametersRecursive(
				compositeParameterNode, rootNode, resultLinkedCompositeParameters);

		return resultLinkedCompositeParameters;
	}

	private static void getLinkedCompositeParametersRecursive(
			CompositeParameterNode targetCompositeParameterNode,
			IAbstractNode currentAbstractNode,
			List<CompositeParameterNode> resultLinkedCompositeParameters) {

		if ((currentAbstractNode instanceof BasicParameterNode) || 
				(currentAbstractNode instanceof TestCaseNode)) {
			return;
		}

		if (currentAbstractNode instanceof CompositeParameterNode) {

			CompositeParameterNode currentCompositeParameterNode = 
					(CompositeParameterNode) currentAbstractNode;

			CompositeParameterNode link = 
					(CompositeParameterNode) currentCompositeParameterNode.getLinkToGlobalParameter();

			if ((link != null) && (link.equals(targetCompositeParameterNode))) {
				resultLinkedCompositeParameters.add(currentCompositeParameterNode);
			}
		}

		List<IAbstractNode> children = currentAbstractNode.getChildren();

		for (IAbstractNode child : children) {
			getLinkedCompositeParametersRecursive(
					targetCompositeParameterNode, child, resultLinkedCompositeParameters);
		}
	}

	public static List<CompositeParameterNode> getLocalCompositeParametersLinkedToGlobal(
			CompositeParameterNode globParameterNode) {

		List<CompositeParameterNode> localCompositeParameterNodes =	new ArrayList<>();

		IAbstractNode startNode = findStartNode(globParameterNode);

		getLocalCompositeParametersLinkedToGlobalRecursive(startNode, globParameterNode, localCompositeParameterNodes);

		return localCompositeParameterNodes;
	}

	private static IAbstractNode findStartNode(AbstractParameterNode globParameterNode) {

		ClassNode classNode = ClassNodeHelper.findClassNode(globParameterNode);

		if (classNode != null) {
			return classNode;
		}

		RootNode rootNode = RootNodeHelper.findRootNode(globParameterNode);

		return rootNode;
	}

	private static void getLocalCompositeParametersLinkedToGlobalRecursive(
			IAbstractNode currentNode,
			CompositeParameterNode globalCompositeParameterNode,
			List<CompositeParameterNode> inOutLocalCompositeParameterNodes) {

		if (currentNode instanceof CompositeParameterNode) {

			CompositeParameterNode compositeParameterNode = (CompositeParameterNode) currentNode;

			CompositeParameterNode linkToGlobalParameter = 
					(CompositeParameterNode) compositeParameterNode.getLinkToGlobalParameter();

			if (ObjectHelper.isEqual(linkToGlobalParameter, globalCompositeParameterNode)) {
				inOutLocalCompositeParameterNodes.add(compositeParameterNode);
			}

		}

		List<IAbstractNode> children = currentNode.getChildren();

		for (IAbstractNode child : children) {

			if (isNodeIgnoredForSearchOfComposites(child)) {
				continue;
			}

			getLocalCompositeParametersLinkedToGlobalRecursive(
					child,
					globalCompositeParameterNode,
					inOutLocalCompositeParameterNodes);
		}

	}

	private static boolean isNodeIgnoredForSearchOfComposites(IAbstractNode node) {

		if (node instanceof BasicParameterNode) {
			return true;
		}

		if (node instanceof ChoiceNode) {
			return true;
		}

		if (node instanceof ConstraintNode) {
			return true;
		}

		if (node instanceof TestSuiteNode) {
			return true;
		}

		if (node instanceof TestCaseNode) {
			return true;
		}

		return false;
	}

	public static List<MethodNode> getMethodsForCompositeParameters(
			List<CompositeParameterNode> compositeLocalParameters) {

		List<MethodNode> methodNodes = new ArrayList<>();

		for (CompositeParameterNode compositeParameterNode : compositeLocalParameters) {

			MethodNode methodNode = MethodNodeHelper.findMethodNode(compositeParameterNode);

			methodNodes.add(methodNode);
		}

		return methodNodes;
	}

	public static boolean parameterMentionsBasicParameter(
			CompositeParameterNode compositeParameterNode,
			BasicParameterNode checkedBasicParameterNode) {

		AbstractParameterNode link = compositeParameterNode.getLinkToGlobalParameter();

		if (link != null) {
			return AbstractParameterNodeHelper.parameterMentionsBasicParameter(
					link,checkedBasicParameterNode);		
		}

		List<AbstractParameterNode> parameters = compositeParameterNode.getParameters();

		for (AbstractParameterNode abstractParameterNode : parameters) {

			if (AbstractParameterNodeHelper.parameterMentionsBasicParameter(abstractParameterNode, checkedBasicParameterNode)) {
				return true;
			}
		}

		return false;
	}

	public static CompositeParameterNode getParameterFromPath(IAbstractNode parameterParent, String parameterName) {

		if (parameterParent == null || parameterName == null) {
			return null;
		}

		List<CompositeParameterNode> parameters = new ArrayList<>();

		parameters.addAll(((IParametersParentNode) parameterParent).getNestedCompositeParameters(true));

		for (CompositeParameterNode parameter : parameters) {
			String name = AbstractParameterSignatureHelper.getQualifiedName(parameter);

			if (name.equals(parameterName)) {
				return parameter;
			}
		}

		return null;
	}

	public static List<CompositeParameterNode> getChildCompositeParameterNodes(IParametersParentNode parentNode) {

		List<AbstractParameterNode> childAbstractParameterNodes = parentNode.getParameters();

		List<CompositeParameterNode> childCompositeParameterNodes = 
				filterCompositeParameterNodes(childAbstractParameterNodes);

		return childCompositeParameterNodes;
	}

	public static List<CompositeParameterNode> filterCompositeParameterNodes(
			List<AbstractParameterNode> abstractParameterNodes) {

		List<CompositeParameterNode> compositeParameterNodes = new ArrayList<>();

		for (AbstractParameterNode abstractParameterNode : abstractParameterNodes) {

			if (abstractParameterNode instanceof CompositeParameterNode) {
				compositeParameterNodes.add((CompositeParameterNode) abstractParameterNode);
			}
		}

		return compositeParameterNodes;
	}

	public static void compareParameters(
			CompositeParameterNode compositeParameterNode1,
			CompositeParameterNode compositeParameterNode2) {

		if (!compositeParameterNode1.getName().equals(compositeParameterNode2.getName())) {
			ExceptionHelper.reportRuntimeException("Composite parameter names do not match.");
		}

		int parametersCount1 = compositeParameterNode1.getParametersCount();
		int parametersCount2 = compositeParameterNode2.getParametersCount();

		if (parametersCount1 != parametersCount2) {
			ExceptionHelper.reportRuntimeException("Count of parameters does not match.");
		}

		compareConstraints(compositeParameterNode1, compositeParameterNode2);

		List<AbstractParameterNode> parameters1 = compositeParameterNode1.getParameters();
		List<AbstractParameterNode> parameters2 = compositeParameterNode2.getParameters();

		compareChildParameters(parameters1, parameters2);
	}

	private static void compareConstraints(
			CompositeParameterNode compositeParameterNode1, 
			CompositeParameterNode compositeParameterNode2) {

		List<ConstraintNode> constraintNodes1 = compositeParameterNode1.getConstraintNodes();
		List<ConstraintNode> constraintNodes2 = compositeParameterNode2.getConstraintNodes();

		AbstractNodeHelper.compareSizes(constraintNodes1, constraintNodes2, "Number of constraints differs.");

		for (int i =0; i < constraintNodes1.size(); ++i) {

			ConstraintNode constraintNode1 = constraintNodes1.get(i);
			ConstraintNode constraintNode2 = constraintNodes2.get(i);

			AbstractNodeHelper.compareParents(constraintNode1, compositeParameterNode1, constraintNode2, compositeParameterNode2);
			ConstraintNodeHelper.compareConstraintNodes(constraintNode1, constraintNode2);
		}
	}

	private static void compareChildParameters(
			List<AbstractParameterNode> parameters1,
			List<AbstractParameterNode> parameters2) {

		AbstractNodeHelper.compareSizes(parameters1, parameters1, "Number of parameters differs.");

		for (int index = 0; index < parameters1.size(); index++) {

			AbstractParameterNode parameter1 = parameters1.get(index);
			AbstractParameterNode parameter2 = parameters2.get(index);

			AbstractParameterNodeHelper.compareParameterTypes(parameter1, parameter2);

			if (parameter1 instanceof BasicParameterNode) {
				BasicParameterNodeHelper.compareParameters(
						(BasicParameterNode)parameter1, (BasicParameterNode)parameter2);
			}

			if (parameter1 instanceof CompositeParameterNode) {
				CompositeParameterNodeHelper.compareParameters(
						(CompositeParameterNode)parameter1, (CompositeParameterNode)parameter2);
			}
		}
	}

	public static List<MethodNode> getMentioningMethodNodes(CompositeParameterNode compositeParameterNode) {

		if (compositeParameterNode == null) {
			ExceptionHelper.reportRuntimeException("Empty composite parameter node is not allowed.");
		}

		List<MethodNode> resultMethodNodes = new ArrayList<>();

		List<AbstractParameterNode> linkedParameters =
				AbstractParameterNodeHelper.getLinkedParameters(compositeParameterNode);

		for (AbstractParameterNode linkedParameterNode : linkedParameters) {

			MethodNode methodNode = MethodNodeHelper.findMethodNode(linkedParameterNode);

			if (methodNode != null) {
				resultMethodNodes.add(methodNode);
			}
		}

		return resultMethodNodes;
	}

	public static CompositeParameterNode findTopComposite(IAbstractNode abstractNode) {

		IAbstractNode currentNode = abstractNode;

		CompositeParameterNode topCompositeParameterNode = null;

		if (abstractNode instanceof CompositeParameterNode) {
			topCompositeParameterNode = (CompositeParameterNode) abstractNode;
		}

		for (;;) {

			IAbstractNode parent = currentNode.getParent();

			if (parent == null || parent instanceof ClassNode || parent instanceof RootNode) {
				return topCompositeParameterNode;
			}

			if (parent instanceof CompositeParameterNode) {
				topCompositeParameterNode = (CompositeParameterNode) parent;
			}

			currentNode = parent;
		}
	}

}
