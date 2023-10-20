package com.ecfeed.core.parser.model.load;

import com.ecfeed.core.model.*;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;

public class ModelDataCSV extends ModelDataAbstract {
    private List<List<String>> rawParsed;

    private ModelDataCSVLineProcessor processor;

    @Override
    protected final void createInternal() {
        updateProperties();

        initializeHeader();
        initializeBody();

        process();

        validateSize();
    }

    private final void initializeBody() {
        this.body = new ArrayList<>();

        for (int i = 0 ; i < this.header.size() ; i++) {
            this.body.add(new HashSet<>());
        }
    }

    private final void validateSize() {
        this.headerOverflow.clear();

        IntStream.range(0, this.body.size()).forEach(i -> {
            if (this.body.get(i).size() > this.limit) {
                this.headerOverflow.add(this.header.get(i));
            }
        });
    }

    @Override
    public List<AbstractParameterNode> parse(IModelChangeRegistrator registrator) {
        List<AbstractParameterNode> list = new ArrayList<>();

        for (int i = 0 ; i < this.header.size() ; i++) {
            List<ChoiceNode> choices = new ArrayList<>();
            DataType type = DataTypeFactory.create(false);

            int j = 0;
            for (String choice : this.body.get(i)) {

                if (j >= this.limit) {
                    break;
                }

                type.feed(choice);
                choices.add(new ChoiceNode("choice" + (j++), choice, null));
            }

            BasicParameterNode parameter = new BasicParameterNode(this.header.get(i), type.determine(), "0", false, registrator);

            choices.forEach(parameter::addChoice);

            list.add(parameter);
        }

        return list;
    }

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

    private void updateProperties() {

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

    private void initializeHeader() {

        this.header = this.rawParsed.get(0);
    }

    private void process() {

        this.rawParsed.stream().skip(1).forEach(this::lineParse);
    }

    private void lineParse(List<String> arg) {

        IntStream.range(0, arg.size()).forEach(i -> {
        	this.body.get(i).add(arg.get(i));
        });
    }
}
