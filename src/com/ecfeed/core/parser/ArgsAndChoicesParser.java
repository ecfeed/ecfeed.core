package com.ecfeed.core.parser; // TODO - rename package to com.ecfeed.core.genservice.parser ? 

import com.ecfeed.core.utils.ExceptionHelper;

import java.util.List;
import java.util.Map;

public class ArgsAndChoicesParser {  // TODO - REUSE IN JUNIT5

    private enum ChoicesValueType {

        NORMAL,
        ALL,
        NONE
    }

    private static final String specialValueAllChoices = "ALL";
    private static final String specialValueNoneChoices = "NONE";

    private ArgsAndChoicesParser.ChoicesValueType fChoicesValueType;
    private Map<String, List<String>> fArgAndChoiceNames = null;

    @SuppressWarnings("unchecked")
	public ArgsAndChoicesParser(Object choicesObject) {

        if (choicesObject == null) {
            fChoicesValueType = ArgsAndChoicesParser.ChoicesValueType.ALL;
            return;
        }

        if (choicesObject instanceof String) {
            fChoicesValueType = getChoiceValueType((String) choicesObject);
            return;
        }

        try {
            fArgAndChoiceNames = (Map<String, List<String>>) choicesObject;
        } catch (Exception e) {
            ExceptionHelper.reportRuntimeException("Invalid type of choices object. Can not convert to map of arguments with choice names.");
        }

        if (fArgAndChoiceNames.size() == 0) {
            ExceptionHelper.reportRuntimeException("Requested list of choices should not be empty.");
        }

        validateMap(fArgAndChoiceNames);
    }

    private void validateMap(Map<String, List<String>> argAndChoiceNames) {

        for (Map.Entry<String, List<String>> mapEntry : argAndChoiceNames.entrySet()) {

            validateMapEntry(mapEntry);
        }
    }

    private void validateMapEntry(Map.Entry<String, List<String>> mapEntry) {

        String parameterName = mapEntry.getKey();
        if (parameterName == null) {
            ExceptionHelper.reportRuntimeException("Parameter name in choices must not be empty.");
        }

        List<String> choices = mapEntry.getValue();

        if (choices.size() == 0) {
            ExceptionHelper.reportRuntimeException("List of choices for parameter: " + parameterName + " must not be empty.");
        }
    }

    public boolean isAllArgsAndChoices() {

        if (fChoicesValueType == ArgsAndChoicesParser.ChoicesValueType.ALL) {
            return true;
        }

        return false;
    }

    public boolean isNoneChoices() {

        if (fChoicesValueType == ArgsAndChoicesParser.ChoicesValueType.NONE) {
            return true;
        }

        return false;
    }

    public Map<String, List<String>> getParametersWithChoiceNames() {

        return fArgAndChoiceNames;
    }

    private ArgsAndChoicesParser.ChoicesValueType getChoiceValueType(String choicesString) {

        if (choicesString.equals(specialValueNoneChoices)) {
            return ArgsAndChoicesParser.ChoicesValueType.NONE;
        }

        if (choicesString.equals(specialValueAllChoices)) {
            return ArgsAndChoicesParser.ChoicesValueType.ALL;
        }

        ExceptionHelper.reportRuntimeException("Invalid special value for choices: " + choicesString);
        return null;
    }

}
