package com.ecfeed.core.export;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class StandardizedExportHelper {
	
	public static Map<String, String> getParameters(String template) {
		Map<String, String> parameters = new HashMap<>();
		
		String[] lines = template.split("\n");
		
		for (String line : lines) {
			
			if (line.contains(":")) {
				line = line.replaceAll(" ", "").replaceAll("\t", "");
				String[] elements = line.split(":");
				parameters.put(elements[0].toLowerCase(), elements[1]);
			}
		}
		
		return parameters;
	}
	
	public static boolean isStandardized(String template) {
		
		return template.startsWith("RFC");
	}
	
	public static IExportTemplate getStandardizedExportTemplate(MethodNode method, String template, IExtLanguageManager extLanguageManager) {
		
		if (StandardizedExportCsvTemplate.isTemplateIdValid(template)) {
			return StandardizedExportCsvTemplate.get(method, template, extLanguageManager);
		}
		
		if (StandardizedExportJsonTemplate.isTemplateIdValid(template)) {
			return StandardizedExportJsonTemplate.get(method, template, extLanguageManager);
		}
		
		throw new RuntimeException("Invalid template standard!");
	}
	
//-----------------------------------------------------------------------------------------------------	
	
	public static MethodNode getMethod() {
		MethodNode method = getBaseMethodNode();

        AbstractParameterNode gr1 = getParameterNode("dest1", "String", "Lorem Ipsum");
        AbstractParameterNode sgr1 = getStructureNode("sgr1", gr1);
        addParameterAsGlobalRoot(sgr1, method);

        AbstractParameterNode gc1 = getParameterNode("dest2", "String", "Lorem \"Ipsum\"");
        AbstractParameterNode sgc1 = getStructureNode("sgc1", gc1);
        addParameterAsGlobalClass(sgc1, method);

        AbstractParameterNode p1 = getLinkedParameterNode("p1", sgr1);
        addParameterAsLocal(p1, method);

        AbstractParameterNode p2 = getLinkedParameterNode("p2", sgc1);
        addParameterAsLocal(p2, method);

        AbstractParameterNode p3 = getParameterNode("dest3", "String", "Lorem, Ipsum");
        AbstractParameterNode s1 = getStructureNode("s1", p3);
        addParameterAsLocal(s1, method);

        AbstractParameterNode p4 = getParameterNode("dest4", "String", "Lorem, \"Ipsum\"");
        addParameterAsLocal(p4, method);

        return method;
	}
	
	public static void addParameterAsLocal(AbstractParameterNode parameter, MethodNode method) {

        method.addParameter(parameter);
    }

    public static void addParameterAsGlobalClass(AbstractParameterNode parameter, MethodNode method) {

        method.getClassNode().addParameter(parameter);
    }

    public static void addParameterAsGlobalRoot(AbstractParameterNode parameter, MethodNode method) {

        ((RootNode) method.getRoot()).addParameter(parameter);
    }
    
    private static MethodNode getBaseMethodNode() {
        RootNode rootNode = new RootNode("root", null);
        ClassNode classNode = new ClassNode("class", null);
        MethodNode methodNode = new MethodNode("method");

        rootNode.addClass(classNode);
        classNode.addMethod(methodNode);

        return methodNode;
    }
    
    private static AbstractParameterNode getParameterNode(String parameterName, String parameterType, String... choiceValue) {
        BasicParameterNode parameter = BasicParameterNode.createLocalStandardParameter(parameterName, parameterType, null, null);

        for (int i = 0 ; i < choiceValue.length ; i++) {
            parameter.addChoice(new ChoiceNode("choice" + i, choiceValue[i], null));
        }

        return parameter;
    }
    
    public static AbstractParameterNode getStructureNode(String parameterName, AbstractParameterNode... parameters) {
        CompositeParameterNode structure = new CompositeParameterNode(parameterName, null);

        Arrays.stream(parameters).forEach(structure::addParameter);

        return structure;
    }
    
    public static AbstractParameterNode getLinkedParameterNode(String parameterName, AbstractParameterNode reference) {
        AbstractParameterNode parameterLinked = null;

        if (reference instanceof BasicParameterNode) {
            parameterLinked = BasicParameterNode.createLocalStandardParameter(parameterName, ((BasicParameterNode) reference).getType(), null, null);
            parameterLinked.setLinkToGlobalParameter(reference);
        } else if (reference instanceof CompositeParameterNode) {
            parameterLinked = new CompositeParameterNode(parameterName, null);
            parameterLinked.setLinkToGlobalParameter(reference);
        }

        return parameterLinked;
    }

    public static BasicParameterNode getParameterNodeRandom(String parameterName, String parameterType, String... choiceValue) {
        BasicParameterNode parameter = BasicParameterNode.createLocalStandardParameter(parameterName, parameterType, null, null);

        for (int i = 0 ; i < choiceValue.length ; i++) {
            ChoiceNode choice = new ChoiceNode("choice" + i, choiceValue[i], null);
            choice.setRandomizedValue(true);
            parameter.addChoice(choice);
        }

        return parameter;
    }
    
    public static TestSuiteNode getTestSuite(MethodNode method) {
        List<BasicParameterNode> parameters = new ArrayList<>();

        method.getParameters().forEach(e -> {
            AbstractParameterNode node = e.getLinkDestination();

            if (node instanceof BasicParameterNode) {
                parameters.add((BasicParameterNode) node);
            } else if (node instanceof CompositeParameterNode) {
                parameters.addAll(((CompositeParameterNode) node).getNestedBasicParameters(true));
            }
        });

        TestSuiteNode suite = new TestSuiteNode("suite", null);

        for (int i = 0 ; i < parameters.get(0).getChoices().size() ; i++) {
            int index = i;

            List<ChoiceNode> choices = parameters.stream().map(e -> e.getChoices().get(index)).collect(Collectors.toList());

            TestCaseNode test = new TestCaseNode("suite", null, choices);

            method.addTestCase(test);
            suite.addTestCase(test);
        }

        return suite;
    }
}
