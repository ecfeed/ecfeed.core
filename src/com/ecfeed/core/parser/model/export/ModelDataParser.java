package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.CompositeParameterNode;
import org.json.JSONObject;

import java.util.Queue;

public class ModelDataParser {
    public final static String SEPARATOR_ROOT = "!";
    public final static String SEPARATOR_CLASS = ":";
    public final static String SEPARATOR_STRUCTURE = "_";

    public static String getJSON(AbstractParameterNode parameter, Queue<ChoiceNode> choices, boolean explicit) {
        JSONObject json = new JSONObject();

        getJSON(json, parameter, choices, explicit);

        return json.toString();
    }

    public static void getJSON(JSONObject json, AbstractParameterNode parameter, Queue<ChoiceNode> choices, boolean explicit) {
        AbstractParameterNode parameterLinked = parameter.getLinkDestination();

        if (parameterLinked instanceof BasicParameterNode) {
            ChoiceNode choice = choices.poll();

            if (choice == null) {
                throw new RuntimeException("The model could not be exported. Incorrect number of choices!");
            }

            json.put(getJsonName(parameter, explicit), choice.getDerandomizedValue());
        } else if (parameterLinked instanceof CompositeParameterNode) {
            JSONObject jsonInternal = new JSONObject();

            ((CompositeParameterNode) parameterLinked).getParameters().forEach(e -> getJSON(jsonInternal, e, choices, explicit));

            json.put(getJsonName(parameter, explicit) , jsonInternal);
        }
    }

    private static String getJsonName(AbstractParameterNode parameter, boolean explicit) {

        if (!parameter.isLinked()) {
            return parameter.getName();
        }

        AbstractParameterNode parameterLinked = parameter.getLinkDestination();

        if (parameterLinked.isClassParameter()) {
            return explicit ? parameter.getName() + SEPARATOR_CLASS + parameterLinked.getName() : parameter.getName();
        } else if (parameterLinked.isRootParameter()) {
            return explicit ? parameter.getName() + SEPARATOR_ROOT + parameterLinked.getName() : parameter.getName();
        }

        return "";
    }
}
