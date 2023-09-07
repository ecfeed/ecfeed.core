package com.ecfeed.core.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.parser.model.export.ModelDataExport;
import com.ecfeed.core.parser.model.export.ModelDataExportJSON;
import com.ecfeed.core.utils.IExtLanguageManager;

public class StandardizedExportJsonTemplate extends AbstractExportTemplate {

	public StandardizedExportJsonTemplate(MethodNode methodNode, IExtLanguageManager extLanguageManager) {

		super(methodNode, createDefaultTemplateText(methodNode.getParametersCount()), extLanguageManager);
	}

	private static String createDefaultTemplateText(int methodParametersCount) {
		String template = 
				"RFC 4627\n" +
				"Indent:\t\t 2\n" +
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
		return "json";
	}
	
	public static String getTemplateFormatSt() {
		return "JSON - RFC 4627";
	}

	@Override 
	public String getTemplateFormat() {
		return "RFC 4627";
	}
	
	@Override
	public String createPreview(
			Collection<TestCaseNode> selectedTestCases, 
			MethodNode methodNode,
			List<ParameterWithLinkingContext> deployedParameters) {
		
		Map<String, String> parameters = StanderdizedExportHelper.getParameters(getTemplateText());
		
		int indent = Integer.parseInt(parameters.get("Indent"));
		boolean nested = Boolean.parseBoolean(parameters.get("Nested"));
		boolean explicit = Boolean.parseBoolean(parameters.get("Explicit"));
		
		ModelDataExport parser = ModelDataExportJSON.getModelDataExport(indent, nested, explicit);
		
		return parser.getFilePreview(new ArrayList<>(selectedTestCases));
	}
}
