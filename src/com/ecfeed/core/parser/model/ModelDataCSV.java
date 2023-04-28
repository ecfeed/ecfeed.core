package com.ecfeed.core.parser.model;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ModelDataCSV extends ModelDataAbstract {

    private Character separator;

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
    protected void updateProperties() {
        Map<Character, Long> separators = new HashMap<>();

        separators.put(',', updatePropertiesSeparatorCandidate(','));
        separators.put(';', updatePropertiesSeparatorCandidate(';'));

        Character candidate = null;
        long candidateQuantity = 0;

        for (Map.Entry<Character, Long> entry : separators.entrySet()) {
            if (entry.getValue() > 0) {
                if (entry.getValue() > candidateQuantity) {
                    candidate = entry.getKey();
                    candidateQuantity = entry.getValue();
                }
            }
        }

        if (candidate == null) {
            throw new IllegalArgumentException("It is not possible to determine the separator character!");
        }

        this.separator = candidate;
    }

    private long updatePropertiesSeparatorCandidate(char separator) {

        if (this.raw == null || this.raw.size() < 1) {
            throw new IllegalArgumentException("The data must contain at least one line.");
        }

        long quantity = updatePropertiesSeparatorCandidateQuantity(this.raw.get(0), separator);

        if (quantity == 0) {
            return -1;
        }

        for (String line : this.raw) {
            if (updatePropertiesSeparatorCandidateQuantity(line, separator) != quantity) {
                return -1;
            }
        }

        return quantity;
    }

    private long updatePropertiesSeparatorCandidateQuantity(String line, char separator) {

        return line.chars().filter(e -> e == separator).count();
    }

    @Override
    protected final void initializeHeader() {

        if (this.raw.size() < 1) {
            throw new IllegalArgumentException("The data must contain at least one line!");
        }

        this.header = Arrays.stream(lineUnify(this.raw.get(0))
                        .split(this.separator.toString()))
                            .map(String::trim)
                            .map(this::headerColumnUnify)
                            .collect(Collectors.toList());
    }

    @Override
    protected final void process() {

        this.raw.stream().skip(1).forEach(this::lineParse);
    }

    private void lineParse(String line) {
        String[] arg = lineUnify(line).split(this.separator.toString(), -1);

        lineValidate(line, arg.length);

        IntStream.range(0, arg.length).forEach(i -> {
        	this.body.get(i).add(arg[i]);
        });
    }

    private void lineValidate(String line, int length) {

        if (length != this.body.size()) {
            throw new IllegalArgumentException("The file is corrupted. "
            		+ "The line '" + line + "' consists of an incorrect number of elements. " +
                    "Expected '" + this.body.size() + "'. Got '" + length + "'!");
        }
    }
    
    private String lineUnify(String line) {
    	
    	return line.replace("\r", "");
    }

    private String headerColumnUnify(String name) {
        name = name.replaceAll("[^a-zA-Z0-9_]", "_");

        if (Character.isDigit(name.charAt(0))) {
            name = "_" + name;
        }

        return name;
    }
}
