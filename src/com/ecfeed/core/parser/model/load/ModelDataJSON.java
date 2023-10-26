package com.ecfeed.core.parser.model.load;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.*;

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
                choices.add(element.get(i).toString());
            }
        }
    }

    private void addValuePrimitive(Set<String> choices, Object element) {

        choices.add(element.toString());
    }

    private JSONArray getTopArray() {

        if (this.rawJSON.keySet().size() != 1) {
            throw new RuntimeException("The root node must consist of exactly one parameter (array type).");
        }

        JSONArray tests = null;
        for (String element : this.rawJSON.keySet()) {

            if (this.rawJSON.get(element) instanceof JSONArray) {
                tests = (JSONArray) this.rawJSON.get(element);
            } else {
                throw new RuntimeException("The root node must consist of exactly one parameter (array type).");
            }
        }

        return tests;
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
        return null;
    }


}
