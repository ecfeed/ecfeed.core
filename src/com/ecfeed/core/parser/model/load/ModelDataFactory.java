package com.ecfeed.core.parser.model.load;

import java.nio.file.Path;

public class ModelDataFactory {

    public enum Type { INHERIT, CSV, JSON }

    public static ModelData create(Path path) {

        return create(path, Type.INHERIT);
    }

    public static ModelData create(Path path, Type type) {

        switch (type) {
            case CSV: return ModelDataCSV.getModelData(path);
            case JSON: return ModelDataJSON.getModelData(path);
            case INHERIT: return determineType(path);
            default: throw new IllegalArgumentException("Could not determine the file type: " + path.toAbsolutePath());
        }
    }

    public static ModelData create(String data, Type type) {

        switch (type) {
            case CSV: return ModelDataCSV.getModelData(data);
            case JSON: return ModelDataJSON.getModelData(data);
            default: throw new IllegalArgumentException("Could not determine the file type.");
        }
    }

    private static ModelData determineType(Path path) {

        if (path.getFileName().toString().endsWith(".csv")) {
            return ModelDataCSV.getModelData(path);
        }

        if (path.getFileName().toString().endsWith(".json")) {
            return ModelDataJSON.getModelData(path);
        }

        throw new IllegalArgumentException("Unknown file extension: " + path.getFileName() + ".");
    }
   
}
