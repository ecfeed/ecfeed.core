package com.ecfeed.core.export;

import com.ecfeed.core.model.MethodDeployerContainer;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportXML;
import com.ecfeed.core.utils.IExtLanguageManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StandardizedExportXMLTemplate extends AbstractExportTemplate {
    private static String ID = "STD - RFC 7303";

    public static IExportTemplate get(MethodNode method, IExtLanguageManager extLanguageManager) {

        return new StandardizedExportXMLTemplate(method, extLanguageManager);
    }

    public static IExportTemplate get(MethodNode method, String template, IExtLanguageManager extLanguageManager) {

        return new StandardizedExportXMLTemplate(method, template, extLanguageManager);
    }

    private StandardizedExportXMLTemplate(MethodNode method, IExtLanguageManager extLanguageManager) {

        super(method, createDefaultTemplateText(), extLanguageManager);
    }

    private StandardizedExportXMLTemplate(MethodNode method, String template, IExtLanguageManager extLanguageManager) {

        super(method, createDefaultTemplateText(), extLanguageManager);

        if (!template.equals(getTemplateFormatSt())) {
            setTemplateText(template);
        }
    }

    public static boolean isTemplateIdValid(String template) {

        return template.startsWith(ID);
    }

    private static String createDefaultTemplateText() {
        String template =
                ID + "\n" +
                        "Indent:            2\n" +
                        "Nested:        false";

        return template;
    }

    @Override
    public boolean isStandardized() {
        return true;
    }

    @Override
    public String getFileExtension() {
        return "xml";
    }

    public static String getStandard() {
        return ID;
    }

    public static String getTemplateFormatSt() {
        return ID + " - XML";
    }

    @Override
    public String getTemplateFormat() {
        return getTemplateFormatSt();
    }

    @Override
    public String getTestCaseTemplate() {
        return "N/A";
    }

    @Override
    public String createPreview(
            Collection<TestCaseNode> testCases,
            MethodDeployerContainer methodDeployerContainer,
            List<ParameterWithLinkingContext> deployedParameters) {

        Map<String, String> parameters = StandardizedExportHelper.getParameters(getTemplateText());

        int indent = Integer.parseInt(StandardizedExportHelper.getParameter(parameters, "indent", "2"));
        boolean nested = Boolean.parseBoolean(StandardizedExportHelper.getParameter(parameters, "nested", "false"));

        ModelDataExport parser;

        if (testCases == null) {
            MethodNode method = StandardizedExportHelper.getMethod();
            testCases = StandardizedExportHelper.getTestSuite(method).getTestCaseNodes();
            parser = ModelDataExportXML.getModelDataExport(method, indent, nested);
        } else {
            parser = ModelDataExportXML.getModelDataExport(methodDeployerContainer.getReference(), indent, nested);
        }

        return parser.getFilePreview(new ArrayList<>(testCases));
    }
}
