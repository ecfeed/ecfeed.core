package com.ecfeed.core.data;

import java.util.List;
import java.util.Map;

public class ArgsAndChoicesData {  // TODO - REUSE IN JUNIT5

    private enum ChoicesValueType {

        NORMAL,
        ALL,
        NONE
    }

    private static final String specialValueAllChoices = "ALL";
    private static final String specialValueNoneChoices = "NONE";

    private ArgsAndChoicesData.ChoicesValueType fChoicesValueType;
    private Map<String, List<String>> fArgAndChoiceNames = null;

    public ArgsAndChoicesData(Object choicesObject) throws Exception {

        if (choicesObject == null) {
            fChoicesValueType = ArgsAndChoicesData.ChoicesValueType.ALL;
            return;
        }

        if (choicesObject instanceof String) {
            fChoicesValueType = getConstraintsFromString((String) choicesObject);
            return;
        }

        try {
            fArgAndChoiceNames = (Map<String, List<String>>) choicesObject;
        } catch (Exception e) {
            throw new Exception("Invalid type of choices object. Can not convert to map of arguments with choice names.");
        }

        if (fArgAndChoiceNames.size() == 0) {
            throw new Exception("Requested list of choices should not be empty.");
        }

        validateMap(fArgAndChoiceNames);
    }

    private void validateMap(Map<String, List<String>> argAndChoiceNames) throws Exception {

        for (Map.Entry<String, List<String>> mapEntry : argAndChoiceNames.entrySet()) {

            validateMapEntry(mapEntry);
        }
    }

    private void validateMapEntry(Map.Entry<String, List<String>> mapEntry) throws Exception {

        String parameterName = mapEntry.getKey();
        if (parameterName == null) {
            throw new Exception("Parameter name in choices must not be empty.");
        }

        List<String> choices = mapEntry.getValue();

        if (choices.size() == 0) {
            throw new Exception("List of choices for parameter: " + parameterName + " must not be empty.");
        }
    }

    public boolean isAllArgsAndChoices() {

        if (fChoicesValueType == ArgsAndChoicesData.ChoicesValueType.ALL) {
            return true;
        }

        return false;
    }

    public boolean isNoneChoices() {

        if (fChoicesValueType == ArgsAndChoicesData.ChoicesValueType.NONE) {
            return true;
        }

        return false;
    }

    public Map<String, List<String>> getParametersWithChoiceNames() {

        return fArgAndChoiceNames;
    }

    private ArgsAndChoicesData.ChoicesValueType getConstraintsFromString(String constraintsString) throws Exception {

        if (constraintsString.equals(specialValueNoneChoices)) {
            return ArgsAndChoicesData.ChoicesValueType.NONE;
        }

        if (constraintsString.equals(specialValueAllChoices)) {
            return ArgsAndChoicesData.ChoicesValueType.ALL;
        }

        throw new Exception("Invalid special value for choices: " + constraintsString);
    }

}
