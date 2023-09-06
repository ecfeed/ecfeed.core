package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;
import java.util.Queue;

public interface ModelDataParser {

    JSONObject getJSON(Queue<ChoiceNode> choices, Collection<AbstractParameterNode> parameters);

    List<String> getParameterNameList(MethodNode method);
}
