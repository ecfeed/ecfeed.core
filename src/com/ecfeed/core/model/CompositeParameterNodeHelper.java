package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.List;

public class CompositeParameterNodeHelper {

    public static BasicParameterNode addNewBasicParameterNodeToCompositeParameter(
            CompositeParameterNode compositeParameterNode, String name, String type, IModelChangeRegistrator modelChangeRegistrator) {

        BasicParameterNode parameterNode = new BasicParameterNode (name, type, modelChangeRegistrator);
        compositeParameterNode.addParameter(parameterNode);

        return parameterNode;
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

}
