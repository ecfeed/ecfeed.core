package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class ModelDataExportJSON implements ModelDataExport {
    private final boolean nested;
    private final boolean explicit;
    private final int indent = 2;

    public static ModelDataExport getModelDataExport(boolean nested, boolean explicit) {

        return new ModelDataExportJSON(nested, explicit);
    }

    private ModelDataExportJSON(boolean nested, boolean explicit) {

        this.nested = nested;
        this.explicit = explicit;
    }

    @Override
    public List<String> getFile(TestSuiteNode suite) {
        return null;
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
        JSONObject json = getTestJSON(test);

        json.put("index", index);

        return json.toString(indent);
    }

    private JSONObject getTestJSON(TestCaseNode test) {
        Queue<ChoiceNode> choices = new LinkedList<>(test.getChoices());

        JSONObject json = new JSONObject();

        test.getMethod().getParameters().forEach(e -> {
            ModelDataParser.getJSON(json, e, choices, explicit);
        });

        return json;
    }
}
