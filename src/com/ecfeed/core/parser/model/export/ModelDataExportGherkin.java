package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;

import java.util.*;
import java.util.stream.Collectors;

public class ModelDataExportGherkin implements ModelDataExport {
    private final MethodNode method;

    private final ModelDataParser parser;

    public static ModelDataExport getModelDataExport(MethodNode method, Map<String, String> parameters) {

        return new ModelDataExportGherkin(method, parameters);
    }

    public static ModelDataExport getModelDataExport(MethodNode method, boolean explicit) {

        return new ModelDataExportGherkin(method, explicit);
    }

    private ModelDataExportGherkin(MethodNode method, Map<String, String> parameters) {
        this.method = method;

        boolean explicit = Boolean.parseBoolean(parameters.getOrDefault("explicit", "explicit"));

        this.parser = ModelDataParserDefault.get(explicit, explicit);
    }

    private ModelDataExportGherkin(MethodNode method, boolean explicit) {
        this.method = method;

        this.parser = ModelDataParserDefault.get(explicit, explicit);
    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public String getFile(List<TestCaseNode> suite) {
        List<List<String>> parametersRaw = getFileParametersRaw(suite);
        List<List<String>> parametersTrimmed = getFileParametersTrimmed(parametersRaw);
        List<List<String>> parametersInverted = getFileParametersInverted(parametersTrimmed);

        String header = getHeader().get();
        String body = parametersInverted.stream().map(e -> String.join(" | ", e)).reduce("", (acc, p) -> acc + "    "  + p + "\n");

        return header + body;
    }

    private List<List<String>> getFileParametersRaw(List<TestCaseNode> suite) {
        List<List<String>> columns = getFileParametersRawHeader();

        for (TestCaseNode testCaseNode : suite) {
            List<String> choices = getTestRaw(testCaseNode);

            for (int j = 0; j < choices.size(); j++) {
                columns.get(j).add(choices.get(j));
            }
        }

        return columns;
    }

    private List<List<String>> getFileParametersRawHeader() {
        List<String> names = parser.getParameterNameList(method);

        return names.stream()
                .map(e -> new ArrayList<>(Collections.singletonList(e)))
                .collect(Collectors.toList());
    }

    private List<List<String>> getFileParametersTrimmed(List<List<String>> parameters) {
        List<Integer> parametersSize = getFileParametersSize(parameters);
        List<List<String>> parametersParsed = new ArrayList<>();

        for (int i = 0 ; i < parameters.size() ; i++) {
            int sizeColumn = parametersSize.get(i);

            parametersParsed.add(parameters.get(i).stream().map(e -> String.format("%-" + sizeColumn + "s", e)).collect(Collectors.toList()));
        }

        return parametersParsed;
    }

    private List<Integer> getFileParametersSize(List<List<String>> parameters) {

        return parameters.stream()
                .map(e -> e.stream().map(String::length).reduce(0, Integer::max))
                .collect(Collectors.toList());
    }

    private List<List<String>> getFileParametersInverted(List<List<String>> parametersTrimmed) {
        List<List<String>> parametersInverted = new ArrayList<>();

        for (int i = 0 ; i < parametersTrimmed.get(0).size() ; i++) {
            List<String> line = new ArrayList<>();

            for (int j = 0; j < parametersTrimmed.size(); j++) {
                line.add(parametersTrimmed.get(j).get(i));
            }

            parametersInverted.add(line);
        }

        return parametersInverted;
    }

//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Optional<String> getHeader() {
        List<String> names = parser.getParameterNameList(method);

        StringBuilder header = new StringBuilder();

        header.append("Feature: EcFeed\n");
        header.append("\n");
        header.append("  Scenario Outline: Execute method '" + method.getName() + "'\n");

        for (int i = 0 ; i < names.size() ; i++) {
            String name = names.get(i);

            if (i == 0) {
                header.append("    Given the value of " + name + " is <" + name + ">.\n");
            } else {
                header.append("    And the value of " + name + " is <" + name + ">.\n");
            }
        }

        header.append("\n");

        header.append("    Examples:\n");

        return Optional.of(header.toString());
    }

    @Override
    public Optional<String> getFooter() {
        return Optional.empty();
    }

    private List<String> getTestRaw(TestCaseNode test) {
        List<String> choices = new ArrayList<>();

        for (int i = 0 ; i < test.getChoices().size() ; i++) {
            ChoiceNode choice = test.getChoices().get(i);

            if (choice.getParameter().getType() == "String" || choice.getParameter().getType() == "char") {
                choices.add("\"" + choice.getDerandomizedValue() + "\"");
            } else {
                choices.add(choice.getDerandomizedValue());
            }
        }

        return choices;
    }

    @Override
    public String getTest(TestCaseNode test) {
        List<String> choices = getTestRaw(test);

        return String.join(" | ", choices);
    }

    @Override
    public String getTest(TestCaseNode test, int index) {

        return getTest(test);
    }
}