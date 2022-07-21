package com.ecfeed.core.parser.model;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.IntStream;

public class ModelDataCSV extends ModelDataAbstract {

    public static ModelData getModelData(Path path) {

        return new ModelDataCSV(path);
    }

    public static ModelData getModelData(String data) {

        return new ModelDataCSV(data);
    }

    private ModelDataCSV(Path path) {

        create(path);
    }

    private ModelDataCSV(String data) {

        create(data);
    }

    @Override
    protected final void initializeHeader() {

        if (this.data.size() < 1) {
            throw new IllegalArgumentException("The data must contain at least one line.");
        }

        this.header = Arrays.asList(this.data.get(0).split(","));
    }

    @Override
    protected final void process() {

        this.data.stream().skip(1).forEach(this::lineParse);
    }

    private void lineParse(String line) {
        String[] arg = line.split(",");

        lineValidate(line, arg.length);

        IntStream.range(0, arg.length).forEach(i -> this.body.get(i).add(arg[i]));
    }

    private void lineValidate(String line, int length) {

        if (length != this.body.size()) {
            throw new IllegalArgumentException("The file is corrupted. "
            		+ "The line '" + line + "' consists of an incorrect number elements. " +
                    "Expected '" + this.body.size() + "'. Got '" + length + "'.");
        }
    }
}
