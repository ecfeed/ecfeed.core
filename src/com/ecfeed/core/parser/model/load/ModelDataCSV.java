package com.ecfeed.core.parser.model.load;

import com.ecfeed.core.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;

public class ModelDataCSV implements ModelData {
    private int limit = 100;

    private List<String> raw;
    private List<List<String>> rawParsed;
    private List<String> header;
    private List<String> headerAffected;
    private List<Set<String>> parameters;

    private ModelDataCSVLineProcessor processor;

    @Override
    public List<String> getRaw() {

        return raw;
    }

    @Override
    public List<String> getHeader() {

        return header;
    }

    @Override
    public List<String> getHeaderAffected() {

        return headerAffected;
    }

    @Override
    public List<Set<String>> getParameters() {

        return parameters;
    }

    @Override
    public int getLimit() {

        return limit;
    }

    @Override
    public void setLimit(int limit) {

        this.limit = limit;

        validateSize();
    }

    private final void create(Path path) {
        this.headerAffected = new ArrayList<>();

        validateFile(path);

        initializeDataFile(path);

        updateProperties();

        initializeHeader();
        initializeBody();

        process();

        validateSize();
    }

    private final void create(String data) {
        this.headerAffected = new ArrayList<>();

        validateText(data);

        initializeDataText(data);

        updateProperties();

        initializeHeader();
        initializeBody();

        process();

        validateSize();
    }

    private final void validateFile(Path path) {

        if (Files.notExists(path)) {
            throw new IllegalArgumentException("The file '" + path.toAbsolutePath() + "' does not exist.");
        }

        if (Files.isDirectory(path)) {
            throw new IllegalArgumentException("The file '" + path.toAbsolutePath() + "' is a directory.");
        }

        if (Files.isSymbolicLink(path)) {
            throw new IllegalArgumentException("The file '" + path.toAbsolutePath() + "' is a symbolic link.");
        }

        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException("The file '" + path.toAbsolutePath() + "' is not readable.");
        }
    }

    private final void initializeDataFile(Path path) {

        try {
            this.raw = Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException("An I/O error occurred while opening the file: '" + path.toAbsolutePath() + "'.", e);
        }
    }

    private final void validateText(String data) {
    }

    private final void initializeDataText(String data) {

        this.raw = Arrays.asList(data.split("\n"));
    }

    private final void initializeBody() {
        this.parameters = new ArrayList<>();

        for (int i = 0 ; i < this.header.size() ; i++) {
            this.parameters.add(new HashSet<>());
        }
    }

    private final void validateSize() {
        this.headerAffected.clear();

        IntStream.range(0, this.parameters.size()).forEach(i -> {
            if (this.parameters.get(i).size() > this.limit) {
                this.headerAffected.add(this.header.get(i));
            }
        });
    }

    @Override
    public List<AbstractParameterNode> parse(IParametersParentNode node) {
        List<AbstractParameterNode> list = new ArrayList<>();

        for (int i = 0 ; i < this.header.size() ; i++) {
            List<ChoiceNode> choices = new ArrayList<>();
            DataType type = DataTypeFactory.create(false);

            int j = 0;
            for (String choice : this.parameters.get(i)) {

                if (j >= this.limit) {
                    break;
                }

                type.feed(choice);
                choices.add(new ChoiceNode("choice" + (j++), choice, null));
            }

            BasicParameterNode parameter;

//            if (node instanceof MethodNode) {
//                parameter = new BasicParameterNode(this.header.get(i), type.determine(), "", false, node.getModelChangeRegistrator());
//            } else if (node instanceof ClassNode) {
//                parameter = new BasicParameterNode(this.header.get(i), type.determine(), "0", false, node.getModelChangeRegistrator());
//            } else if (node instanceof RootNode) {
                parameter = new BasicParameterNode(this.header.get(i), type.determine(), "0", false, node.getModelChangeRegistrator());
//            } else {
//                throw new IllegalArgumentException("The node type is not supported.");
//            }

            choices.forEach(parameter::addChoice);

            list.add(parameter);
        }

        return list;
    }

    @Override
    public Optional<String> getWarning() {

        if (this.headerAffected.size() > 0) {
            return Optional.of("The hard limit for the number of choices is " + this.limit +".\n"
                    + "Affected parameters: " + String.join(", ", this.headerAffected) + ".");
        }

        return Optional.empty();
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

    private final void initializeHeader() {

        this.header = this.rawParsed.get(0);
    }

    private final void process() {

        this.rawParsed.stream().skip(1).forEach(this::lineParse);
    }

    private void lineParse(List<String> arg) {

        IntStream.range(0, arg.size()).forEach(i -> {
        	this.parameters.get(i).add(arg.get(i));
        });
    }
}
