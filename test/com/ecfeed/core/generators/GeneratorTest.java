package com.ecfeed.core.generators;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.Test;
import com.ecfeed.core.generators.algorithms.AbstractAlgorithm;
import com.ecfeed.core.generators.algorithms.GeneratorHelper;
import com.ecfeed.core.generators.algorithms.NWiseAwesomeAlgorithm;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.ConstraintsParentNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;

public class GeneratorTest {

	@Test
	public void generateWithParameterCondition() {

		performTestWithParameterConditionForFixedParameterType("int");
		performTestWithParameterConditionForFixedParameterType("String");
	}

	private void performTestWithParameterConditionForFixedParameterType(String parameterType) {

		RootNode rootNode = createModel(parameterType);

		AbstractAlgorithm<ChoiceNode> algorithm = new NWiseAwesomeAlgorithm<>(2, 100);

		MethodNode methodNode = rootNode.getClasses().get(0).getMethods().get(0);

		List<List<ChoiceNode>> tests = GeneratorHelper.generateTestCasesForMethod(methodNode, algorithm);

		assertEquals(1, tests.size());
	}

	private RootNode createModel(String parameterType) {

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "Class",  true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "Method", true, null);

		// two basic parameters

		BasicParameterNode basicParameterNode1 =
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "String", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode1, "choice1", "test", false, true, null);

		BasicParameterNode basicParameterNode2 =
				MethodNodeHelper.addNewBasicParameter(methodNode, "par2", "String", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				basicParameterNode2, "choice2", "test", false, true, null);

		// constraint with parameter condition

		StaticStatement precondition = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement postcondition =
				RelationStatement.createRelationStatementWithParameterCondition(
						basicParameterNode1, null,
						EMathRelation.EQUAL,
						basicParameterNode2, null);

		Constraint constraint = new Constraint(
				"constraint", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);

		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint, true, null);

		return rootNode;
	}

}
