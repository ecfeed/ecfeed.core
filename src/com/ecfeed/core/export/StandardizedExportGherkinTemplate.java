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
import com.ecfeed.core.parser.model.export.ModelDataExportGherkin;
import com.ecfeed.core.utils.IExtLanguageManager;

public class StandardizedExportGherkinTemplate extends AbstractExportTemplate {
	private static String ID = "STD - Gherkin";
	
	public static IExportTemplate get(MethodNode method, IExtLanguageManager extLanguageManager) {
		
		return new StandardizedExportGherkinTemplate(method, extLanguageManager);
	}
	
	public static IExportTemplate get(MethodNode method, String template, IExtLanguageManager extLanguageManager) {
		
		return new StandardizedExportGherkinTemplate(method, template, extLanguageManager);
	}
	
	private StandardizedExportGherkinTemplate(MethodNode method, IExtLanguageManager extLanguageManager) {

		super(method, createDefaultTemplateText(), extLanguageManager);
	}
	
	private StandardizedExportGherkinTemplate(MethodNode method, String template, IExtLanguageManager extLanguageManager) {

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
				"Explicit:      false";

		return template;
	}
	
	@Override
	public boolean isStandardized() {
		return true;
	}
	
	@Override
	public String getFileExtension() {
		return "feature";
	}

	public static String getStandard() {
		return ID;
	}

	public static String getTemplateFormatSt() {
		return ID;
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
		
		boolean explicit = Boolean.parseBoolean(StandardizedExportHelper.getParameter(parameters, "explicit", "false"));
		
		ModelDataExport parser;
		
		if (testCases == null) {
			MethodNode method = StandardizedExportHelper.getMethod();
			testCases = StandardizedExportHelper.getTestSuite(method).getTestCaseNodes();
			parser = ModelDataExportGherkin.getModelDataExport(method, explicit);
		} else {
			parser = ModelDataExportGherkin.getModelDataExport(methodDeployerContainer.getReference(), explicit);
		}
		
		return parser.getFilePreview(new ArrayList<>(testCases));
	}
	
}
