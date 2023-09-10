package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class ModelDataExportCSV implements ModelDataExport {
    private final MethodNode method;

    private final ModelDataParser parser;

    private final String separator;
    private final boolean nested;

    public static ModelDataExport getModelDataExport(MethodNode method, String separator, boolean nested, boolean explicit) {

        return new ModelDataExportCSV(method, separator, nested, explicit);
    }

    private ModelDataExportCSV(MethodNode method, String separator, boolean nested, boolean explicit) {
        this.method = method;

        this.separator = separator;
        this.nested = nested;

        this.parser = ModelDataParserDefault.get(explicit, nested);
    }

    @Override
    public String getFile(List<TestCaseNode> suite) {

        if (suite.size() == 0) {
            throw new RuntimeException("The test suite should consist of at least one test case!");
        }

        List<String> file = new ArrayList<>();

        file.add(getHeader().orElse(""));

        suite.forEach(e -> file.add(getTest(e)));

        return String.join("\n", file);
    }

    @Override
    public Optional<String> getHeader() {
       List<String> names = parser.getParameterNameList(method);

       return Optional.of(String.join(separator, names));
    }

    @Override
    public Optional<String> getFooter() {

        return Optional.empty();
    }

    @Override
    public String getTest(TestCaseNode test) {
        Queue<ChoiceNode> choices = new LinkedList<>(test.getTestData());

        if (nested) {
            return getTestNested(choices);
        } else {
            return getTestFlat(choices);
        }
    }

    @Override
    public String getTest(TestCaseNode test, int index) {

        return getTest(test);
    }

    public String getTestNested(Queue<ChoiceNode> choices) {
        List<String> line = new ArrayList<>();

        method.getParameters().forEach(e -> {
            if (e instanceof BasicParameterNode) {
                ChoiceNode choice = choices.poll();

                if (choice == null) {
                    throw new RuntimeException("The test could not be exported to CSV!");
                }

                line.add(parseValue(choice.getDerandomizedValue()));
            } else if (e instanceof CompositeParameterNode) {
                line.add(parser.getJSON(choices, Collections.singletonList(e)).toString());
            }
        });

        return String.join(separator, line);
    }

    public String getTestFlat(Queue<ChoiceNode> choices) {

        List<String> line = choices.stream()
                .map(e -> parseValue(e.getDerandomizedValue()))
                .collect(Collectors.toList());

        return String.join(separator, line);
    }

    private String parseValue(String value) {
        String parsedValue = value;

        if (parsedValue.contains("\"")) {
            parsedValue = parseValueQuotation(parsedValue);
        }

        if (parsedValue.contains(separator)) {
            parsedValue = parseValueSeparator(parsedValue);
        }

        return parsedValue;
    }

    private String parseValueSeparator(String value) {

        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value;
        }

        return "\"" + value + "\"";
    }

    private String parseValueQuotation(String value) {

        return "\"" + value.replaceAll("\"", "\"\"") + "\"";
    }
}
