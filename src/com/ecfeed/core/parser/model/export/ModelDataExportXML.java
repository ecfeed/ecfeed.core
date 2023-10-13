package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModelDataExportXML implements ModelDataExport {
    private final static String HEADER_SUITE = "suite";
    private final static String HEADER_TEST = "test";

    private final ModelDataExportJSON parser;

    private final int indent;

    public static ModelDataExport getModelDataExport(MethodNode method, Map<String, String> parameters) {

        return new ModelDataExportXML(method, parameters);
    }

    public static ModelDataExport getModelDataExport(MethodNode method, int indent, boolean nested) {

        return new ModelDataExportXML(method, indent, nested);
    }

    private ModelDataExportXML(MethodNode method, Map<String, String> parameters) {
        this.indent = Integer.parseInt(parameters.getOrDefault("indent", "2"));

        this.parser = (ModelDataExportJSON) ModelDataExportJSON.getModelDataExport(method, parameters);
    }

    private ModelDataExportXML(MethodNode method, int indent, boolean nested) {
        this.indent = indent;

        this.parser = (ModelDataExportJSON) ModelDataExportJSON.getModelDataExport(method, indent, nested, false);
    }

    @Override
    public String getFile(List<TestCaseNode> suite) {

        if (suite.size() == 0) {
            throw new RuntimeException("The test suite should consist of at least one test case!");
        }

        JSONObject json = new JSONObject();
        JSONArray jsonSuite = new JSONArray();

        int index = 0;
        for (TestCaseNode test : suite) {
            jsonSuite.put(parser.getTestJSON(test, index++));
        }

        json.put(HEADER_TEST, jsonSuite);

        return XML.unescape(XML.toString(json, HEADER_SUITE, this.indent));
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

        return XML.unescape(XML.toString(parser.getTestJSON(test), HEADER_TEST, this.indent));
    }

    @Override
    public String getTest(TestCaseNode test, int index) {

        return XML.unescape(XML.toString(parser.getTestJSON(test, index), HEADER_TEST, this.indent));
    }
}
