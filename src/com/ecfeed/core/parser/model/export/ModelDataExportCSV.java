package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class ModelDataExportCSV implements ModelDataExport {

    private final String separator;
    private final boolean nested;
    private final boolean explicit;

    public static ModelDataExport getModelDataExport(String separator, boolean nested, boolean explicit) {

        return new ModelDataExportCSV(separator, nested, explicit);
    }

    private ModelDataExportCSV(String separator, boolean nested, boolean explicit) {

        this.separator = separator;
        this.nested = nested;
        this.explicit = explicit;
    }

    @Override
    public List<String> getFile(TestSuiteNode suite) {
        List<String> file = new ArrayList<>();

        file.add(getHeader(suite.getMethod()).orElse(""));

        suite.getTestCaseNodes().forEach(e -> file.add(getTest(e)));

        return file;
    }

    @Override
    public Optional<String> getHeader(MethodNode method) {
       List<String> names = new ArrayList<>();

       if (nested) {
           method.getParameters().forEach(e -> names.add(getHeaderNameNested(e)));
       } else {
           method.getParameters().forEach(e -> names.addAll(getHeaderNameFlat(e, "")));
       }


       return Optional.of(String.join(separator, names));
    }

    @Override
    public Optional<String> getFooter(MethodNode method) {

        return Optional.empty();
    }

    private String getHeaderNameNested(AbstractParameterNode parameter) {

        if (!parameter.isLinked()) {
            return getHeaderNameNestedNotLinked(parameter);
        } else {
            return getHeaderNameNestedLinked(parameter);
        }
    }

    private String getHeaderNameNestedLinked(AbstractParameterNode parameter) {
        AbstractParameterNode linked = parameter.getLinkToGlobalParameter();

        if (explicit) {
            if (linked.isClassParameter()) {
                return parameter.getName() + ModelDataParser.SEPARATOR_CLASS + linked.getName();
            } else if (linked.isGlobalParameter()) {
                return parameter.getName() + ModelDataParser.SEPARATOR_ROOT + linked.getName();
            }
        }

        return parameter.getName();
    }

    private String getHeaderNameNestedNotLinked(AbstractParameterNode parameter) {

        return parameter.getName();
    }

    private List<String> getHeaderNameFlat(AbstractParameterNode parameter, String prefix) {
        List<String> names = new ArrayList<>();
        String prefixUpdated = prefix + parameter.getName();

        if (!parameter.isLinked()) {
            names.addAll(getHeaderNameFlatNotLinked(parameter, prefixUpdated));
        } else {
            if (explicit) {
                names.addAll(getHeaderNameFlatLinked(parameter, prefixUpdated));
            } else {
                names.addAll(getHeaderNameFlatNotLinked(parameter.getLinkDestination(), prefixUpdated));
            }
        }

        return names;
    }

    private List<String> getHeaderNameFlatLinked(AbstractParameterNode parameter, String prefix) {
        List<String> names = new ArrayList<>();
        AbstractParameterNode linked = parameter.getLinkToGlobalParameter();

        if (linked.isClassParameter()) {
            names.addAll(getHeaderNameFlat(linked, prefix + ModelDataParser.SEPARATOR_CLASS));
        } else if (linked.isRootParameter()) {
            names.addAll(getHeaderNameFlat(linked, prefix + ModelDataParser.SEPARATOR_ROOT));
        }

        return names;
    }

    private List<String> getHeaderNameFlatNotLinked(AbstractParameterNode parameter, String prefix) {
        List<String> names = new ArrayList<>();

        if (parameter instanceof BasicParameterNode) {
            names.add(prefix);
        } else if (parameter instanceof CompositeParameterNode) {
            ((CompositeParameterNode) parameter).getParameters()
                    .forEach(e -> names.addAll(getHeaderNameFlat(e, prefix + ModelDataParser.SEPARATOR_STRUCTURE)));
        }

        return names;
    }

    @Override
    public String getTest(TestCaseNode test) {
        Queue<ChoiceNode> choices = new LinkedList<>(test.getTestData());

        if (nested) {
            return getTestNested(test.getMethod(), choices);
        } else {
            return getTestFlat(choices);
        }
    }

    @Override
    public String getTest(TestCaseNode test, int index) {

        return getTest(test);
    }

    public String getTestNested(MethodNode method, Queue<ChoiceNode> choices) {
        List<String> line = new ArrayList<>();

        method.getParameters().forEach(e -> {
            if (e instanceof BasicParameterNode) {
                ChoiceNode choice = choices.poll();
                line.add(parseValue(choice.getDerandomizedValue()));
            } else if (e instanceof CompositeParameterNode) {
                line.add(ModelDataParser.getJSON(e, choices, explicit));
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
