package com.ecfeed.core.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportCSV;
import com.ecfeed.core.utils.IExtLanguageManager;

public class StandardizedExportCsvTemplate extends AbstractExportTemplate {

	public StandardizedExportCsvTemplate(MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		super(methodNode, createDefaultTemplateText(methodNode.getParametersCount()), extLanguageManager);
	}

	private static String createDefaultTemplateText(int methodParametersCount) {
		String template = 
				"RFC 4180\n" +
				"Delimiter:\t ,\n" +
				"Explicit:\t\t false\n" +
				"Nested:\t\t false";

		return template;
	}
	
	@Override
	public boolean isStanderdized() {
		return true;
	}

	@Override
	public String getFileExtension() {
		return "csv";
	}
	
	public static String getTemplateFormatSt() {
		return "CSV - RFC 4180";
	}

	@Override 
	public String getTemplateFormat() {
		return "RFC 4180";
	}
	
	@Override
	public String createPreview(
			Collection<TestCaseNode> selectedTestCases, 
			MethodNode methodNode,
			List<ParameterWithLinkingContext> deployedParameters) {
		
		Map<String, String> parameters = StanderdizedExportHelper.getParameters(getTemplateText());
		
		String delimiter = parameters.get("Delimiter");
		boolean nested = Boolean.parseBoolean(parameters.get("Nested"));
		boolean explicit = Boolean.parseBoolean(parameters.get("Explicit"));
		
		ModelDataExport parser = ModelDataExportCSV.getModelDataExport(methodNode, delimiter, nested, explicit);
		
		return parser.getFilePreview(new ArrayList<>(selectedTestCases));
	}
}
