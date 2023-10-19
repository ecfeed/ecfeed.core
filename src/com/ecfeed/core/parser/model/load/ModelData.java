package com.ecfeed.core.parser.model.load;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.IParametersParentNode;

public interface ModelData {

    int getLimit();

    void setLimit(int limit);

    List<AbstractParameterNode> parse(IModelChangeRegistrator registrator);

    List<String> getRaw();

    List<String> getHeader();

    List<String> getHeaderAffected();

    List<Set<String>> getParameters();

    Optional<String> getWarning();
}
