package com.ecfeed.core.model;

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

}
