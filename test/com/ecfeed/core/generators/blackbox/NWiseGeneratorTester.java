package com.ecfeed.core.generators.blackbox;

import com.ecfeed.core.evaluator.Sat4jEvaluator;
import com.ecfeed.core.generators.AbstractGenerator;
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

    public NWiseGeneratorTester(String modelXml) {

        try {
            initializeTester(modelXml);
        } catch (GeneratorException e) {
            ExceptionHelper.reportRuntimeException("Generator initialisation failed.", e);
        }
    }

    private void initializeTester(String modelXml) throws GeneratorException {

        fRootNode = ModelTestHelper.createModel(modelXml);

        if (fRootNode.getClasses().size() > 1) {
            ExceptionHelper.reportRuntimeException("Too many classes. Only one allowed for test.");
        }

        fClassNode = fRootNode.getClasses().get(0);

        if (fClassNode.getMethods().size() > 1) {
            ExceptionHelper.reportRuntimeException("Too many methods. Only one allowed for test.");
        }

        fMethodNode = fClassNode.getMethods().get(0);

        List<List<ChoiceNode>> generatorInput = getAlgorithmInput(fMethodNode);

        fGenerator = new NWiseGenerator<>();

        List<IGeneratorValue> generatorParameters = createGeneratorDefaultParameters(fGenerator);

        Collection<Constraint> constraints = fMethodNode.getAllConstraints();

        Sat4jEvaluator sat4jEvaluator = new Sat4jEvaluator(constraints, fMethodNode);

        fGenerator.initialize(
                generatorInput,
                sat4jEvaluator,
                generatorParameters,
                null);
    }

    public void runGeneration() {

        try {
            while (fGenerator.next() != null) {
            }

        } catch (GeneratorException e){

        }
    }

    private List<List<ChoiceNode>> getAlgorithmInput(MethodNode methodNode) {

        List<List<ChoiceNode>> input = new ArrayList<>();

        for (MethodParameterNode arg : methodNode.getMethodParameters())
            if (arg.isExpected()) {
                input.add(Collections.singletonList(null));
            } else {
                input.add(arg.getLeafChoicesWithCopies());
            }

        return input;
    }

    private List<IGeneratorValue> createGeneratorDefaultParameters(
            NWiseGenerator<ChoiceNode> nWiseGenerator) throws GeneratorException {

        List<IGeneratorValue> result = new ArrayList<>();

        result.add(new GeneratorValue(nWiseGenerator.getDefinitionN(), "2"));
        result.add(new GeneratorValue(nWiseGenerator.getDefinitionCoverage(), "100"));

        return result;
    }

}
