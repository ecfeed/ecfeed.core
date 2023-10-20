package com.ecfeed.core.parser.model.load;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public abstract class ModelDataAbstract implements ModelData {
    protected int limit = 100;

    protected List<String> raw;

    protected List<String> header = new ArrayList<>();
    protected List<String> headerOverflow = new ArrayList<>();

    protected List<Set<String>> body;

    @Override
    public int getLimit() {

        return limit;
    }

    @Override
    public List<String> getRaw() {

        return raw;
    }

    @Override
    public List<String> getHeader() {

        return header;
    }

    @Override
    public List<String> getHeaderOverflow() {

        return headerOverflow;
    }

    @Override
    public List<Set<String>> getBody() {

        return body;
    }

    @Override
    public Optional<String> getWarning() {

        if (this.headerOverflow.size() > 0) {
            return Optional.of("The hard limit for the number of choices is " + this.limit +".\n"
                    + "Affected parameters: " + String.join(", ", this.headerOverflow) + ".");
        }

        return Optional.empty();
    }

    protected final void create(Path path) {
        validateFile(path);
        initializeDataFile(path);

        createInternal();
    }

    protected final void create(String data) {
        validateText(data);
        initializeDataText(data);

        createInternal();
    }

    protected final void validateFile(Path path) {

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

    private final void validateText(String data) {

        if (data == null) {
            throw new IllegalArgumentException("The input data is corrupted.");
        }
    }

    protected final void initializeDataFile(Path path) {

        try {
            this.raw = Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException("An I/O error occurred while opening the file: '" + path.toAbsolutePath() + "'.", e);
        }
    }

    protected final void initializeDataText(String data) {

        this.raw = Arrays.asList(data.split("\n"));
    }

//----------------------------------------------------------------------------------------------------------------------

    abstract protected void createInternal();
}