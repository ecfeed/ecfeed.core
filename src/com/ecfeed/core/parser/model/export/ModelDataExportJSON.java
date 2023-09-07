package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class ModelDataExportJSON implements ModelDataExport {
    private final ModelDataParser parser;

    private final boolean nested;
    private final int indent;

    public static ModelDataExport getModelDataExport(int indent, boolean nested, boolean explicit) {

        return new ModelDataExportJSON(indent, nested, explicit);
    }

    private ModelDataExportJSON(int indent, boolean nested, boolean explicit) {

        this.nested = nested;
        this.indent = indent;

        this.parser = ModelDataParserDefault.get(explicit, nested);
    }

    @Override
    public String getFile(List<TestCaseNode> suite) {

        if (suite.size() == 0) {
            throw new RuntimeException("The test suite should consist of at least one test case!");
        }

        JSONObject json = new JSONObject();
        JSONArray jsonTests = new JSONArray();

        int index = 0;
        for (TestCaseNode test : suite) {
            jsonTests.put(getTestJSON(test, index++));
        }

        json.put("tests", jsonTests);

        return json.toString(this.indent);
    }

    @Override
    public Optional<String> getHeader(MethodNode method) {

        return Optional.empty();
    }

    @Override
    public Optional<String> getFooter(MethodNode method) {

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

    private JSONObject getTestJSON(TestCaseNode test) {
        Queue<ChoiceNode> choices = new LinkedList<>(test.getChoices());

        if (this.nested) {
            return getTestJSONNested(test, choices);
        } else {
            return getTestJSONFlat(test, choices);
        }
    }

    private JSONObject getTestJSON(TestCaseNode test, int index) {
        JSONObject json = getTestJSON(test);

        json.put("index", index);

        return json;
    }

    private JSONObject getTestJSONFlat(TestCaseNode test, Queue<ChoiceNode> choices) {
        List<String> names = parser.getParameterNameList(test.getMethod());

        JSONObject json = new JSONObject();

        names.forEach(e -> json.put(e, choices.poll().getDerandomizedValue()));

        return json;
    }

    private JSONObject getTestJSONNested(TestCaseNode test, Queue<ChoiceNode> choices) {

        return parser.getJSON(choices, test.getMethod().getParameters());
    }
}
