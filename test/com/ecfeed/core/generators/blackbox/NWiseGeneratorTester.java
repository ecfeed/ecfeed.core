package com.ecfeed.core.generators.blackbox;

import com.ecfeed.core.evaluator.Sat4jEvaluator;
import com.ecfeed.core.generators.GeneratorValue;
import com.ecfeed.core.generators.NWiseGenerator;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.ExceptionHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NWiseGeneratorTester { // TODO - extract common parts to AbstractGeneratorTester

    RootNode fRootNode;
    ClassNode fClassNode;
    MethodNode fMethodNode;

    NWiseGenerator<ChoiceNode> fGenerator;
    List<List<ChoiceNode>> fGenResults;

    public NWiseGeneratorTester(String modelXml) {

        try {
            initializeTester(modelXml);

        } catch (GeneratorException e) {
            ExceptionHelper.reportRuntimeException("Generator initialisation failed.", e);
        }
    }

    public void runGeneration() {

        try {
            runGenerationIntr();

        } catch (Exception e) {
            ExceptionHelper.reportRuntimeException("Generation failed.", e);
        }
    }

    private void runGenerationIntr() throws GeneratorException {

        fGenResults = new ArrayList<>();

        while (true) {

            List<ChoiceNode> testCase = fGenerator.next();

            if (testCase == null) {
                break;
            }

            fGenResults.add(testCase);
        }
    }

    private void initializeTester(String modelXml) throws GeneratorException {

        fRootNode = ModelTestHelper.createModel(modelXml);

        fClassNode = getClassNode(fRootNode);

        fMethodNode = getMethodNode(fClassNode);

        fGenerator = createGenerator(fMethodNode);
    }

    private static MethodNode getMethodNode(ClassNode classNode) {

        if (classNode.getMethods().size() > 1) {
            ExceptionHelper.reportRuntimeException("Too many methods. Only one allowed for test.");
        }

        return classNode.getMethods().get(0);
    }

    private static ClassNode getClassNode(RootNode rootNode) {

        if (rootNode.getClasses().size() > 1) {
            ExceptionHelper.reportRuntimeException("Too many classes. Only one allowed for test.");
        }

        return rootNode.getClasses().get(0);
    }

    private static NWiseGenerator<ChoiceNode> createGenerator(MethodNode methodNode) throws GeneratorException {

        List<List<ChoiceNode>> generatorInput = getAlgorithmInput(methodNode);

        NWiseGenerator<ChoiceNode> generator = new NWiseGenerator<>();

        List<IGeneratorValue> generatorParameters = createGeneratorDefaultParameters(generator);

        Collection<Constraint> constraints = methodNode.getAllConstraints();

        Sat4jEvaluator sat4jEvaluator = new Sat4jEvaluator(constraints, methodNode);

        generator.initialize(
                generatorInput,
                sat4jEvaluator,
                generatorParameters,
                null);

        return generator;
    }

    private static List<List<ChoiceNode>> getAlgorithmInput(MethodNode methodNode) {

        List<List<ChoiceNode>> input = new ArrayList<>();

        for (MethodParameterNode arg : methodNode.getMethodParameters())
            if (arg.isExpected()) {
                input.add(Collections.singletonList(null));
            } else {
                input.add(arg.getLeafChoicesWithCopies());
            }

        return input;
    }

    private static List<IGeneratorValue> createGeneratorDefaultParameters(
            NWiseGenerator<ChoiceNode> nWiseGenerator) throws GeneratorException {

        List<IGeneratorValue> result = new ArrayList<>();

        result.add(new GeneratorValue(nWiseGenerator.getDefinitionN(), "2"));
        result.add(new GeneratorValue(nWiseGenerator.getDefinitionCoverage(), "100"));

        return result;
    }

}
