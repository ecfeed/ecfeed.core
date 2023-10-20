package com.ecfeed.core.parser.model.load;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.IModelChangeRegistrator;

public interface ModelData {

    int getLimit();

    List<String> getRaw();

    List<AbstractParameterNode> parse(IModelChangeRegistrator registrator);

    List<Set<String>> getBody();

    List<String> getHeader();

    List<String> getHeaderOverflow();

    Optional<String> getWarning();
}
