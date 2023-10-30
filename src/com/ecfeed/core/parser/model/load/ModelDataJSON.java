package com.ecfeed.core.parser.model.load;

import com.ecfeed.core.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ModelDataJSON extends ModelDataAbstract {
    private JSONObject rawJSON;

    public static ModelData getModelData(Path path) {

        return new ModelDataJSON(path);
    }

    public static ModelData getModelData(String data) {

        return new ModelDataJSON(data);
    }

    private ModelDataJSON(Path path) {

        create(path);
    }

    private ModelDataJSON(String data) {

        create(data);
    }

    @Override
    protected void createInternal() {

        createJsonObject();

    }

    private void createJsonObject() {

        this.rawJSON = new JSONObject(String.join("", this.raw).replaceAll(" ", "").replaceAll("\t",""));

        extractHeader();
        extractBody();
    }

    private void extractHeader() {
        Set<String> parameters = new HashSet<>();

        extractHeader(parameters, getTopArray(), "");

        this.header = new ArrayList<>(parameters);
    }

    private void extractHeader(Set<String> parameters, JSONArray test, String prefix) {

        for (int i = 0 ; i < test.length() ; i++ ) {
            extractHeader(parameters, test.getJSONObject(i), prefix);
        }
    }

    private void extractHeader(Set<String> parameters, JSONObject test, String prefix) {

        for (String element : test.keySet()) {
            Object elementParsed = test.get(element);

            if (elementParsed instanceof JSONObject) {
                extractHeader(parameters, (JSONObject) elementParsed, prefix + element + "&");
            } else if (elementParsed instanceof JSONArray) {

                if (isArrayOfObjects((JSONArray) elementParsed)) {
                    extractHeader(parameters, (JSONArray) elementParsed, prefix + element + "&");
                }
            } else {
                parameters.add(prefix + element);
            }
        }
    }

    private void extractBody() {
        initializeBody();

        getTopArray().forEach(e -> {

            for (int i = 0 ; i < this.header.size() ; i++) {
                String[] path = this.header.get(i).split("&");

                Set<String> choices = this.body.get(i);
                traverseJSON((JSONObject) e, path).ifPresent(f -> addValue(f, choices, path[path.length - 1]));
            }
        });
    }

    private Optional<JSONObject> traverseJSON(JSONObject test, String[] path) {

        for (int j = 0 ; j < path.length - 1 ; j++) {

            if (test.has(path[j])) {
                test = test.getJSONObject(path[j]);
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(test);
    }

    private void initializeBody() {

        this.body = new ArrayList<>();

        for (int i = 0 ; i < this.header.size() ; i++) {
            this.body.add(new HashSet<>());
        }
    }

    private void addValue(JSONObject test, Set<String> choices, String key) {

        if (test.has(key)) {
            Object element = test.get(key);

            if (element instanceof JSONArray) {
                addValueArray(choices, (JSONArray) element);
            } else {
                addValuePrimitive(choices, element);
            }
        }
    }

    private void addValueArray(Set<String> choices, JSONArray element) {

        if (!isArrayOfObjects(element)) {
            for (int i = 0; i < element.length() ; i++) {
                if (choices.size() < this.limit) {
                    choices.add(element.get(i).toString());
                } else {
                    break;
                }
            }
        }
    }

    private void addValuePrimitive(Set<String> choices, Object element) {

        if (choices.size() < this.limit) {
            choices.add(element.toString());
        }
    }

    private JSONArray getTopArray() {

        if (this.rawJSON.keySet().size() == 1) {

            for (String element : this.rawJSON.keySet()) {
                Object node = this.rawJSON.get(element);

                return node instanceof JSONArray ? (JSONArray) node : new JSONArray().put(node);
            }
        } else {

            return new JSONArray().put(this.rawJSON);
        }

        throw new RuntimeException("The JSON object could not be parsed!");
    }

    private boolean isArrayOfObjects(JSONArray array) {

        for (int i = 0 ; i < array.length() ; i++) {

            if (array.get(i) instanceof JSONObject) {
                continue;
            }

            return false;
        }

        return true;
    }

    @Override
    public List<AbstractParameterNode> parse(IModelChangeRegistrator registrator) {
        List<AbstractParameterNode> list = new ArrayList<>();

        for (int i = 0 ; i < this.header.size() ; i++) {
            List<ChoiceNode> choices = new ArrayList<>();
            DataType type = DataTypeFactory.create(false);

            int j = 0;
            for (String choice : this.body.get(i)) {

                if (j >= this.limit) {
                    break;
                }

                type.feed(choice);
                choices.add(new ChoiceNode("choice" + (j++), choice, null));
            }

            BasicParameterNode parameter = parseParameter(list, this.header.get(i), type, registrator);

            choices.forEach(parameter::addChoice);
        }

        return list;
    }

    private BasicParameterNode parseParameter(List<AbstractParameterNode> parameters, String path, DataType type, IModelChangeRegistrator registrator) {
        BasicParameterNode result;

        if (path.contains("&")) {
            String[] elements = path.split("&");

            IParametersParentNode composite = null;
            Map<String, AbstractParameterNode> compositeNames;

            for (int i = 0 ; i < elements.length - 1 ; i++) {

                if (composite == null) {
                    compositeNames = parameters.stream().collect(Collectors.toMap(AbstractNode::getName, e -> e));
                } else {
                    compositeNames = composite.getParameters().stream().collect(Collectors.toMap(AbstractNode::getName, e -> e));
                }

                if (compositeNames.containsKey(elements[i])) {
                    composite = (CompositeParameterNode) compositeNames.get(elements[i]);
                } else {
                    CompositeParameterNode compositeCandidate = new CompositeParameterNode(elements[i], registrator);

                    if (composite == null) {
                        parameters.add(compositeCandidate);
                    } else {
                        composite.addParameter(compositeCandidate);
                    }

                    composite = compositeCandidate;
                }
            }

            result = new BasicParameterNode(elements[elements.length -1], type.determine(), "0", false, registrator);

            composite.addParameter(result);
        } else {
            result = new BasicParameterNode(path, type.determine(), "0", false, registrator);

            parameters.add(result);
        }

        return result;
    }
}
