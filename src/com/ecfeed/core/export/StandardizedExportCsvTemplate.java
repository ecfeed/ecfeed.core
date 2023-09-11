package com.ecfeed.core.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.model.MethodDeployerContainer;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportCSV;
import com.ecfeed.core.utils.IExtLanguageManager;

public class StandardizedExportCsvTemplate extends AbstractExportTemplate {
	private static String ID = "RFC 4180";

	public static IExportTemplate get(MethodNode method, IExtLanguageManager extLanguageManager) {
		
		return new StandardizedExportCsvTemplate(method, extLanguageManager);
	}
	
	public static IExportTemplate get(MethodNode method, String template, IExtLanguageManager extLanguageManager) {
		
		return new StandardizedExportCsvTemplate(method, template, extLanguageManager);
	}
	
	private StandardizedExportCsvTemplate(MethodNode method, IExtLanguageManager extLanguageManager) {

		super(method, createDefaultTemplateText(), extLanguageManager);
	}
	
	private StandardizedExportCsvTemplate(MethodNode method, String template, IExtLanguageManager extLanguageManager) {

		super(method, createDefaultTemplateText(), extLanguageManager);
		setTemplateText(template);
	}
	
	public static boolean isTemplateIdValid(String template) {
		
		return template.startsWith(ID);
	}

	private static String createDefaultTemplateText() {
		String template = 
				ID + "\n" +
				"Delimiter:\t ,\n" +
				"Explicit:\t\t false\n" +
				"Nested:\t\t false";

		return template;
	}
	
	@Override
	public boolean isStandardized() {
		return true;
	}

	@Override
	public String getFileExtension() {
		return "csv";
	}
	
	public static String getTemplateFormatSt() {
		return "RFC 4180 - CSV";
	}

	@Override 
	public String getTemplateFormat() {
		return "RFC 4180";
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
		
		String delimiter = parameters.get("Delimiter");
		boolean nested = Boolean.parseBoolean(parameters.get("Nested"));
		boolean explicit = Boolean.parseBoolean(parameters.get("Explicit"));
		
		ModelDataExport parser;
		
		if (testCases == null) {
			MethodNode method = StandardizedExportHelper.getMethod();
			testCases = StandardizedExportHelper.getTestSuite(method).getTestCaseNodes();
			parser = ModelDataExportCSV.getModelDataExport(method, delimiter, nested, explicit);
		} else {
			parser = ModelDataExportCSV.getModelDataExport(methodDeployerContainer.getReference(), delimiter, nested, explicit);
		}
		
		return parser.getFilePreview(new ArrayList<>(testCases));
	}
}
