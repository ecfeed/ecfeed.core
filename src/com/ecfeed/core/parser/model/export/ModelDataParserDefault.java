package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

public class ModelDataParserDefault implements ModelDataParser {
    public final static String SEPARATOR_ROOT = "!";
    public final static String SEPARATOR_CLASS = ":";
    public final static String SEPARATOR_STRUCTURE = "_";

    private final boolean explicit;
    private final boolean nested;

    public static ModelDataParser get(boolean explicit, boolean nested) {

        return new ModelDataParserDefault(explicit, nested);
    }

    private ModelDataParserDefault(boolean explicit, boolean nested) {

        this.explicit = explicit;
        this.nested = nested;
    }

    @Override
    public JSONObject getJSON(Queue<ChoiceNode> choices, Collection<AbstractParameterNode> parameters) {
        JSONObject json = new JSONObject();

        parameters.forEach(e -> getJSON(json, e, choices));

        return json;
    }

    private void getJSON(JSONObject json, AbstractParameterNode parameter, Queue<ChoiceNode> choices) {
        AbstractParameterNode parameterLinked = parameter.getLinkDestination();

        if (parameterLinked instanceof BasicParameterNode) {
            ChoiceNode choice = choices.poll();

            if (choice == null) {
                throw new RuntimeException("The model could not be exported. Incorrect number of choices!");
            }

            json.put(getJsonName(parameter), choice.getDerandomizedValue());
        } else if (parameterLinked instanceof CompositeParameterNode) {
            JSONObject jsonInternal = new JSONObject();

            ((CompositeParameterNode) parameterLinked).getParameters().forEach(e -> getJSON(jsonInternal, e, choices));

            json.put(getJsonName(parameter) , jsonInternal);
        }
    }

    private String getJsonName(AbstractParameterNode parameter) {

        if (!parameter.isLinked()) {
            return parameter.getName();
        }

        AbstractParameterNode parameterLinked = parameter.getLinkDestination();

        if (parameterLinked.isClassParameter()) {
            return this.explicit ? parameter.getName() + SEPARATOR_CLASS + parameterLinked.getName() : parameter.getName();
        } else if (parameterLinked.isRootParameter()) {
            return this.explicit ? parameter.getName() + SEPARATOR_ROOT + parameterLinked.getName() : parameter.getName();
        }

        return "";
    }

    @Override
    public List<String> getParameterNameList(MethodNode method) {
        List<String> names = new ArrayList<>();

        if (nested) {
            method.getParameters().forEach(e -> names.add(getHeaderNameNested(e)));
        } else {
            method.getParameters().forEach(e -> names.addAll(getHeaderNameFlat(e, "")));
        }

        return names;
    }

    private String getHeaderNameNested(AbstractParameterNode parameter) {

        if (!parameter.isLinked()) {
            return getHeaderNameNestedNotLinked(parameter);
        } else {
            return getHeaderNameNestedLinked(parameter);
        }
    }

    private String getHeaderNameNestedLinked(AbstractParameterNode parameter) {
        AbstractParameterNode linked = parameter.getLinkToGlobalParameter();

        if (this.explicit) {
            if (linked.isClassParameter()) {
                return parameter.getName() + ModelDataParserDefault.SEPARATOR_CLASS + linked.getName();
            } else if (linked.isGlobalParameter()) {
                return parameter.getName() + ModelDataParserDefault.SEPARATOR_ROOT + linked.getName();
            }
        }

        return parameter.getName();
    }

    private String getHeaderNameNestedNotLinked(AbstractParameterNode parameter) {

        return parameter.getName();
    }

    private List<String> getHeaderNameFlat(AbstractParameterNode parameter, String prefix) {
        List<String> names = new ArrayList<>();
        String prefixUpdated = prefix + parameter.getName();

        if (!parameter.isLinked()) {
            names.addAll(getHeaderNameFlatNotLinked(parameter, prefixUpdated));
        } else {
            if (this.explicit) {
                names.addAll(getHeaderNameFlatLinked(parameter, prefixUpdated));
            } else {
                names.addAll(getHeaderNameFlatNotLinked(parameter.getLinkDestination(), prefixUpdated));
            }
        }

        return names;
    }

    private List<String> getHeaderNameFlatLinked(AbstractParameterNode parameter, String prefix) {
        List<String> names = new ArrayList<>();
        AbstractParameterNode linked = parameter.getLinkToGlobalParameter();

        if (linked.isClassParameter()) {
            names.addAll(getHeaderNameFlat(linked, prefix + ModelDataParserDefault.SEPARATOR_CLASS));
        } else if (linked.isRootParameter()) {
            names.addAll(getHeaderNameFlat(linked, prefix + ModelDataParserDefault.SEPARATOR_ROOT));
        }

        return names;
    }

    private List<String> getHeaderNameFlatNotLinked(AbstractParameterNode parameter, String prefix) {
        List<String> names = new ArrayList<>();

        if (parameter instanceof BasicParameterNode) {
            names.add(prefix);
        } else if (parameter instanceof CompositeParameterNode) {
            ((CompositeParameterNode) parameter).getParameters()
                    .forEach(e -> names.addAll(getHeaderNameFlat(e, prefix + ModelDataParserDefault.SEPARATOR_STRUCTURE)));
        }

        return names;
    }
}
