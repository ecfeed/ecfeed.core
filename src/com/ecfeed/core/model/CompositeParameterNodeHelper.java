package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ObjectHelper;

public class CompositeParameterNodeHelper {

	public static BasicParameterNode addNewBasicParameterNodeToCompositeParameter( // TODO MO-RE rename to addBasicParameter
			CompositeParameterNode compositeParameterNode, 
			String name, 
			String type,
			String defaultValue,
			IModelChangeRegistrator modelChangeRegistrator) {

		BasicParameterNode parameterNode = 
				new BasicParameterNode (name, type, defaultValue, false, modelChangeRegistrator);

		compositeParameterNode.addParameter(parameterNode);

		return parameterNode;
	}

	public static CompositeParameterNode addCompositeParameter(
			CompositeParameterNode parentCompositeParameterNode, 
			String childCompositeName, 
			IModelChangeRegistrator modelChangeRegistrator) {

		CompositeParameterNode childCompositeParameterNode = 
				new CompositeParameterNode(childCompositeName, modelChangeRegistrator);

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

	public static void compareParameters(
			CompositeParameterNode compositeParameterNode1,
			CompositeParameterNode compositeParameterNode2) {
		
		if (!compositeParameterNode1.getName().equals(compositeParameterNode2.getName())) {
			ExceptionHelper.reportRuntimeException("Composite parameter names do not match.");
		}

		if (compositeParameterNode1.getParametersCount() != compositeParameterNode2.getParametersCount()) {
			ExceptionHelper.reportRuntimeException("Count of parameters does not match.");
		}
		
		List<AbstractParameterNode> parameters1 = compositeParameterNode1.getParameters();
		List<AbstractParameterNode> parameters2 = compositeParameterNode2.getParameters();
		
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

}
