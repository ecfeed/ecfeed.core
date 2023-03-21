package com.ecfeed.core.parser.model;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.IParametersParentNode;

public interface ModelData {

    List<BasicParameterNode> parse(IParametersParentNode node);

    List<String> getRaw();

    List<String> getHeader();

    List<String> getHeaderAffected();

    List<Set<String>> getParameters();

    int getLimit();
    
    void setLimit(int limit);
    
    Optional<String> getWarning();
}
