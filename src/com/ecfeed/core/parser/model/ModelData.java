package com.ecfeed.core.parser.model;

import java.util.List;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ParametersParentNode;

public interface ModelData {

    List<AbstractParameterNode> parse(ParametersParentNode node);
}
