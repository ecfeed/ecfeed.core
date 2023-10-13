package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class ModelDataExportJSON implements ModelDataExport {
    private final static String HEADER_SUITE = "suite";

    private final MethodNode method;

    private final ModelDataParser parser;

    private final boolean nested;
    private final int indent;

    public static ModelDataExport getModelDataExport(MethodNode method, Map<String, String> parameters) {

        return new ModelDataExportJSON(method, parameters);
    }

    public static ModelDataExport getModelDataExport(MethodNode method, int indent, boolean nested, boolean explicit) {

        return new ModelDataExportJSON(method, indent, nested, explicit);
    }

    private ModelDataExportJSON(MethodNode method, Map<String, String> parameters) {
        this.method = method;

        this.nested = Boolean.parseBoolean(parameters.getOrDefault("nested", "false"));
        this.indent = Integer.parseInt(parameters.getOrDefault("indent", "2"));

        boolean explicit = Boolean.parseBoolean(parameters.getOrDefault("explicit", "explicit"));

        this.parser = ModelDataParserDefault.get(explicit, nested);
    }

    private ModelDataExportJSON(MethodNode method, int indent, boolean nested, boolean explicit) {
        this.method = method;

        this.nested = nested;
        this.indent = indent;

        this.parser = ModelDataParserDefault.get(explicit, nested);
    }

    @Override
    public String getFile(List<TestCaseNode> suite) {
        JSONObject json = getFileJSON(suite);

        return json.toString(this.indent);
    }

    protected JSONObject getFileJSON(List<TestCaseNode> suite) {

        if (suite.size() == 0) {
            throw new RuntimeException("The test suite should consist of at least one test case!");
        }

        JSONObject json = new JSONObject();
        JSONArray jsonTests = new JSONArray();

        int index = 0;
        for (TestCaseNode test : suite) {
            jsonTests.put(getTestJSON(test, index++));
        }

        json.put(HEADER_SUITE, jsonTests);

        return json;
    }

    @Override
    public Optional<String> getHeader() {

        return Optional.empty();
    }

    @Override
    public Optional<String> getFooter() {

        return Optional.empty();
    }

    @Override
    public String getTest(TestCaseNode test) {
        JSONObject json = getTestJSON(test);

        return json.toString(indent);
    }

    @Override
    public String getTest(TestCaseNode test, int index) {
        JSONObject json = getTestJSON(test, index);

        return json.toString(indent);
    }

    protected JSONObject getTestJSON(TestCaseNode test) {
        Queue<ChoiceNode> choices = new LinkedList<>(test.getChoices());

        if (this.nested) {
            return getTestJSONNested(choices);
        } else {
            return getTestJSONFlat(choices);
        }
    }

    protected JSONObject getTestJSON(TestCaseNode test, int index) {
        JSONObject json = getTestJSON(test);

        json.put("index", index);

        return json;
    }

    protected JSONObject getTestJSONFlat(Queue<ChoiceNode> choices) {
        List<String> names = parser.getParameterNameList(method);

        JSONObject json = new JSONObject();

        names.forEach(e -> json.put(e, choices.poll().getDerandomizedValue()));

        return json;
    }

    protected JSONObject getTestJSONNested(Queue<ChoiceNode> choices) {

        return parser.getJSON(choices, method.getParameters());
    }
}
