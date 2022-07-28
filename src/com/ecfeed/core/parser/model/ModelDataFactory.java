package com.ecfeed.core.parser.model;

import java.nio.file.Path;

public class ModelDataFactory {

    public enum Type { INHERIT, CSV }

    public static ModelData create(Path path) {

        return create(path, Type.INHERIT);
    }

    public static ModelData create(Path path, Type type) {

        switch (type) {
            case CSV: return ModelDataCSV.getModelData(path);
            case INHERIT: return determineType(path);
            default: throw new IllegalArgumentException("Could not determine the file type: " + path.toAbsolutePath());
        }
    }

    public static ModelData create(String data, Type type) {

        switch (type) {
            case CSV: return ModelDataCSV.getModelData(data);
            default: throw new IllegalArgumentException("Could not determine the file type.");
        }
    }

    private static ModelData determineType(Path path) {

        if (path.getFileName().toString().endsWith(".csv")) {
            return ModelDataCSV.getModelData(path);
        }

        throw new IllegalArgumentException("Unknown file extension: " + path.getFileName() + ".");
    }
}
