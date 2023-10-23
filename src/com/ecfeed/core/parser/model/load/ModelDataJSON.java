package com.ecfeed.core.parser.model.load;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        this.rawJSON = new JSONObject(String.join("", raw));

        this.header = new ArrayList<>(extractHeader(this.rawJSON));

        System.out.println("test");
    }

    private Set<String> extractHeader(JSONObject test) {
        Set<String> parameters = new HashSet<>();

        extractHeader(parameters, test, "");

        return parameters;
    }
    private void extractHeader(Set<String> parameters, JSONObject test, String prefix) {

        for (String element : test.keySet()) {
            Object elementParsed = test.get(element);

            if (elementParsed instanceof JSONObject) {
                JSONObject elementObject = (JSONObject) elementParsed;

                extractHeader(parameters, elementObject, prefix + element + "&");
            } else if (elementParsed instanceof JSONArray) {
                JSONArray elementArray = (JSONArray) elementParsed;
//                TODO - It does not need to be an object.
                elementArray.forEach(e -> extractHeader(parameters, (JSONObject) e, prefix));
            } else {
                parameters.add(prefix + element);
            }
        }
    }

    @Override
    public List<AbstractParameterNode> parse(IModelChangeRegistrator registrator) {
        return null;
    }


}
