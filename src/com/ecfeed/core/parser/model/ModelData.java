package com.ecfeed.core.parser.model;

import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ParametersParentNode;

public interface ModelData {

    List<AbstractParameterNode> parse(ParametersParentNode node);

    List<String> getRaw();

    List<String> getHeader();

    List<String> getHeaderAffected();

    List<Set<String>> getParameters();

    int getLimit();
}
