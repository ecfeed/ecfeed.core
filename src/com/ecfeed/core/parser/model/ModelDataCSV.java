package com.ecfeed.core.parser.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class ModelDataCSV extends ModelDataAbstract {

    private ModelDataCSVLineProcessor processor;
    private List<List<String>> rawParsed;

    public static ModelData getModelData(Path path) {

        return new ModelDataCSV(path);
    }

    public static ModelData getModelData(String data) {

        return new ModelDataCSV(data);
    }

    private ModelDataCSV(Path path) {

        processor = ModelDataCSVLineProcessor.get();

        create(path);
    }

    private ModelDataCSV(String data) {

        processor = ModelDataCSVLineProcessor.get();

        create(data);
    }

    @Override
    protected void updateProperties() {

        updatePropertiesSeparator(true, ',');
        updatePropertiesSeparator(false, ';');

        if (this.rawParsed == null) {
            throw new IllegalArgumentException("The separator could not be determined!");
        }
    }

    private void updatePropertiesSeparator(boolean init, Character separator) {
        Optional<List<List<String>>> candidate = updatePropertiesData(separator);

        if (candidate.isPresent()) {
            if (init || (this.rawParsed != null && candidate.get().get(0).size() > this.rawParsed.get(0).size())) {
                this.rawParsed = candidate.get();
            }
        }
    }

    private Optional<List<List<String>>> updatePropertiesData(char separator) {

        if (this.raw == null || this.raw.size() < 1) {
            throw new IllegalArgumentException("The data must contain of at least one line!");
        }

        List<List<String>> results = new ArrayList<>();

        List<String> header = this.processor.parseHeader(this.raw.get(0), separator);

        results.add(header);

        if (header.size() == 0) {
            throw new IllegalArgumentException("The file should consist of at least one column!");
        }

        if (this.raw.size() < 2) {
            return Optional.of(results);
        }

        for (int i = 1 ; i < this.raw.size() ; i++) {
            List<String> body = this.processor.parseBody(this.raw.get(i), separator);

            if (body.size() != header.size()) {
                return Optional.empty();
            }

            results.add(body);
        }

        return Optional.of(results);
    }

    @Override
    protected final void initializeHeader() {

        this.header = this.rawParsed.get(0);
    }

    @Override
    protected final void process() {

        this.rawParsed.stream().skip(1).forEach(this::lineParse);
    }

    private void lineParse(List<String> arg) {

        IntStream.range(0, arg.size()).forEach(i -> {
        	this.body.get(i).add(arg.get(i));
        });
    }
}
