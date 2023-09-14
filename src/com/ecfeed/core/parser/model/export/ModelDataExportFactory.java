package com.ecfeed.core.parser.model.export;

import com.ecfeed.core.export.IExportTemplate;
import com.ecfeed.core.export.StandardizedExportCsvTemplate;
import com.ecfeed.core.export.StandardizedExportJsonTemplate;
import com.ecfeed.core.model.MethodNode;

import java.util.Map;

public class ModelDataExportFactory {

    public static ModelDataExport get(MethodNode method, IExportTemplate template, Map<String, String> parameters) {

        if (template.isStandardized()) {

            if (template.getTemplateFormat().startsWith(StandardizedExportCsvTemplate.getStandard())) {
                return ModelDataExportCSV.getModelDataExport(method, parameters);
            }

            if (template.getTemplateFormat().startsWith(StandardizedExportJsonTemplate.getStandard())) {
                return ModelDataExportJSON.getModelDataExport(method, parameters);
            }
        }

        throw new RuntimeException("ModelDataFormat can be applied only to standardized data formats!");
    }
}
