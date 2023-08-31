package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelDataExportCSV implements ModelDataExport {
    private final static String NAME_SEPARATOR = "_";
    private final static String SEPARATOR = ",";

    public static ModelDataExport getModelDataExport() {
        return new ModelDataExportCSV();
    }

    @Override
    public String getHeader(MethodNode method) {
       List<String> names = new ArrayList<>();

       method.getParameters().forEach(e -> names.addAll(getHeaderName(e, "")));

       return String.join(SEPARATOR, names);
    }

    private List<String> getHeaderName(AbstractParameterNode parameter, String prefix) {
        List<String> names = new ArrayList<>();
        String prefixUpdated = prefix + parameter.getName();

        if (!parameter.isLinked()) {
            if (parameter instanceof BasicParameterNode) {
                names.add(prefixUpdated);
            } else if (parameter instanceof CompositeParameterNode) {
                ((CompositeParameterNode) parameter).getParameters().forEach(e -> names.addAll(getHeaderName(e, prefixUpdated + NAME_SEPARATOR)));
            }
        } else if (parameter instanceof BasicParameterNode) {
            parameter = parameter.getLinkToGlobalParameter();
            names.addAll(getHeaderName(parameter, prefixUpdated + NAME_SEPARATOR));
        }

        return names;
    }

    @Override
    public String getTest(TestCaseNode test) {
        List<String> line = test.getTestData().stream()
                .map(e -> parseValue(e.getDerandomizedValue()))
                .collect(Collectors.toList());

        return String.join(SEPARATOR, line);
    }

    private String parseValue(String value) {
        String parsedValue = value;

        if (parsedValue.contains("\"")) {
            parsedValue = parseValueQuotation(parsedValue);
        }

        if (parsedValue.contains(SEPARATOR)) {
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
