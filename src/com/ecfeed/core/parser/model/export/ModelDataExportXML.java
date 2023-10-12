package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import org.json.JSONObject;
import org.json.XML;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModelDataExportXML implements ModelDataExport {
    private final ModelDataExportJSON parser;

    private final int indent;

    public static ModelDataExport getModelDataExport(MethodNode method, Map<String, String> parameters) {

        return new ModelDataExportXML(method, parameters);
    }

    public static ModelDataExport getModelDataExport(MethodNode method, int indent, boolean nested, boolean explicit) {

        return new ModelDataExportXML(method, indent, nested, explicit);
    }

    private ModelDataExportXML(MethodNode method, Map<String, String> parameters) {
        this.indent = Integer.parseInt(parameters.getOrDefault("indent", "2"));

        this.parser = (ModelDataExportJSON) ModelDataExportJSON.getModelDataExport(method, parameters);
    }

    private ModelDataExportXML(MethodNode method, int indent, boolean nested, boolean explicit) {
        this.indent = indent;

        this.parser = (ModelDataExportJSON) ModelDataExportJSON.getModelDataExport(method, indent, nested, explicit);
    }

    @Override
    public String getFile(List<TestCaseNode> suite) {

        return XML.toString(parser.getFile(suite));
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

        return XML.toString(parser.getTestJSON(test));
    }

    @Override
    public String getTest(TestCaseNode test, int index) {

        return XML.unescape(XML.toString(parser.getTestJSON(test, index), this.indent));
    }
}
