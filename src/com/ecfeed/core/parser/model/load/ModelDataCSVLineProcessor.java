package com.ecfeed.core.parser.model.load;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ModelDataCSVLineProcessor {
    private static final String nameTemplate = "COL_";

    private Character separator;

    private int nameIndex = 0;

    private final StringBuilder valueCandidate = new StringBuilder();
    private final Queue<Character> characterQueue = new LinkedList<>();

    private boolean isQuotation = false;

    private ModelDataCSVLineProcessor() {
    }

    public static ModelDataCSVLineProcessor get() {

        return new ModelDataCSVLineProcessor();
    }

    public List<String> parseHeader(String line, Character separator) {

        if (line.trim().length() == 0) {
            throw new RuntimeException("The header line cannot be empty!");
        }

        parseHeaderSet(separator);

        List<String> results = new ArrayList<>();

        for (String element : parseBody(line, separator)) {
            element = prepareHeaderEmpty(results, element);
            element = prepareHeaderPattern(element);

            results.add(element);
        }

        return results;
    }

    public List<String> parseBody(String line, Character separator) {

        if (line.trim().length() == 0) {
            throw new RuntimeException("The body line cannot be empty!");
        }

        parseBodySet(separator);

        List<String> results = new ArrayList<>();

        prepareCharQueue(line);
        processCharQueue(results);

        return results;
    }

    private void parseHeaderSet(Character separator) {

        this.separator = separator;

        nameIndex = 0;
    }

    private void parseBodySet(Character separator) {

        this.separator = separator;

        isQuotation = false;

        valueCandidate.setLength(0);
        characterQueue.clear();
    }

    private String prepareHeaderPattern(String element) {
        element = element.replaceAll("[^a-zA-Z0-9_]", "_");

        if (Character.isDigit(element.charAt(0))) {
            element = "_" + element;
        }

        return element;
    }

    private String prepareHeaderEmpty(Collection<String> elements, String element) {
        element = element.trim();

        if (element.isEmpty()) {
            String name;

            do {
                name = nameTemplate + nameIndex;
                nameIndex++;
            } while (elements.contains(name));

            return name;
        } else {

            if (elements.contains(element)) {
                throw new RuntimeException("The header cannot have duplicate names - '" + element + "'!");
            }
        }

        return element;
    }

    private void processCharQueue(Collection<String> elements) {

        while (characterQueue.size() > 0) {
            processCharacter(elements);
        }

        elements.add(valueCandidate.toString());
    }

    private void processCharacter(Collection<String> elements) {
        Character character = characterQueue.remove();

        if (isQuotation(character)) {
            processQuotation();
        } else if (isSeparator(character)) {
            processSeparator(elements);
        } else if (isWhiteSpace(character)) {
            processWhiteSpace(character);
        } else {
            processDefault(character);
        }
    }

    private boolean isQuotation(Character character) {

        return character.equals('"');
    }

    private boolean isWhiteSpace(Character character) {

        return character.equals(' ') || character.equals('\t');
    }

    private boolean isSeparator(Character character) {

        return character.equals(separator);
    }

    private void processQuotation() {

        if (characterQueue.size() > 1) {
            Character followUp = characterQueue.peek();

            if (followUp.equals('"')) {
                characterQueue.remove();
                valueCandidate.append('"');
            } else {
                isQuotation = !isQuotation;
            }
        } else {
            isQuotation = !isQuotation;
        }
    }

    private void processWhiteSpace(Character character) {

        if (isQuotation) {
            valueCandidate.append(character);
        }
    }

    private void processSeparator(Collection<String> elements) {

        if (!isQuotation) {
            elements.add(valueCandidate.toString());
            valueCandidate.setLength(0);
        } else {
            valueCandidate.append(separator);
        }
    }

    private void processDefault(Character character) {

        valueCandidate.append(character);
    }

    private String parseLine(String line) {

        if (line.endsWith("\n")) {
            line = line.substring(0, line.length() - 1);
        }

        if (line.endsWith("\r")) {
            line = line.substring(0, line.length() - 1);
        }

        return line;
    }

    private void prepareCharQueue(String line) {
        line = parseLine(line);

        for (int i = 0 ; i < line.length() ; i++) {
            characterQueue.add(line.charAt(i));
        }
    }
}
