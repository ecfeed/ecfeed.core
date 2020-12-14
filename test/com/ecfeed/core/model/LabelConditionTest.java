/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class LabelConditionTest {

	//nothing?
	
	@Test
	public void evaluateTest(){
		ChoiceNode q = new ChoiceNode("q", "0", null);

		ChoiceNode p = new ChoiceNode("p", "0", null);
		ChoiceNode p1 = new ChoiceNode("p1", "0", null);
		ChoiceNode p2 = new ChoiceNode("p2", "0", null);
		ChoiceNode p11 = new ChoiceNode("p11", "0", null);
		ChoiceNode p12 = new ChoiceNode("p12", "0", null);
		ChoiceNode p21 = new ChoiceNode("p21", "0", null);
		ChoiceNode p22 = new ChoiceNode("p22", "0", null);

		p.addChoice(p1);
		p.addChoice(p2);
		p1.addChoice(p11);
		p1.addChoice(p12);
		p2.addChoice(p21);
		p2.addChoice(p22);

		p.addLabel("p");
		p1.addLabel("p1");
		p2.addLabel("p2");
		p11.addLabel("p11");
		p12.addLabel("p12");
		p21.addLabel("p21");
		p22.addLabel("p22");

		MethodParameterNode c1 = new MethodParameterNode("c1", "type", "0", false, null);
		c1.addChoice(p);
		MethodParameterNode c2 = new MethodParameterNode("c2", "type", "0", false, null);
		c2.addChoice(q);

		MethodNode method = new MethodNode("method", null);
		method.addParameter(c1);
		method.addParameter(c2);

		List<ChoiceNode> pq = new ArrayList<ChoiceNode>();
		pq.add(p); pq.add(q);

		List<ChoiceNode> p1q = new ArrayList<ChoiceNode>();
		p1q.add(p1); p1q.add(q);

		List<ChoiceNode> p2q = new ArrayList<ChoiceNode>();
		p2q.add(p2); p2q.add(q);

		List<ChoiceNode> p11q = new ArrayList<ChoiceNode>();
		p11q.add(p11); p11q.add(q);

		List<ChoiceNode> p21q = new ArrayList<ChoiceNode>();
		p21q.add(p21); p21q.add(q);

		RelationStatement pEqual = 
				RelationStatement.createRelationStatementWithLabelCondition(c1, EMathRelation.EQUAL, "p");

		RelationStatement pNotEqual = 
				RelationStatement.createRelationStatementWithLabelCondition(c1, EMathRelation.NOT_EQUAL, "p");

		RelationStatement p1Equal = 
				RelationStatement.createRelationStatementWithLabelCondition(c1, EMathRelation.EQUAL, "p1");

		RelationStatement p1NotEqual = 
				RelationStatement.createRelationStatementWithLabelCondition(c1, EMathRelation.NOT_EQUAL, "p1");

		RelationStatement p11Equal = 
				RelationStatement.createRelationStatementWithLabelCondition(c1, EMathRelation.EQUAL, "p11");

		RelationStatement p11NotEqual = 
				RelationStatement.createRelationStatementWithLabelCondition(c1, EMathRelation.NOT_EQUAL, "p11");

		//Check that all pEqual statements evaluates to true for all vectors
		assertTrue(pEqual.evaluate(pq) == EvaluationResult.TRUE);
		assertTrue(pEqual.evaluate(p1q) == EvaluationResult.TRUE);
		assertTrue(pEqual.evaluate(p2q) == EvaluationResult.TRUE);
		assertTrue(pEqual.evaluate(p11q) == EvaluationResult.TRUE);
		assertTrue(pEqual.evaluate(p21q) == EvaluationResult.TRUE);

		//Check that all pNotEqual statements evaluates to true for all vectors
		assertTrue(pNotEqual.evaluate(pq) == EvaluationResult.FALSE);
		assertTrue(pNotEqual.evaluate(p1q) == EvaluationResult.FALSE);
		assertTrue(pNotEqual.evaluate(p11q) == EvaluationResult.FALSE);
		assertTrue(pNotEqual.evaluate(p2q) == EvaluationResult.FALSE);
		assertTrue(pNotEqual.evaluate(p21q) == EvaluationResult.FALSE);

		//Check that p1qEqual evaluates to true only for vectors with p1 and deriving
		assertTrue(p1Equal.evaluate(pq) == EvaluationResult.FALSE);
		assertTrue(p1Equal.evaluate(p1q) == EvaluationResult.TRUE);
		assertTrue(p1Equal.evaluate(p2q) == EvaluationResult.FALSE);
		assertTrue(p1Equal.evaluate(p11q) == EvaluationResult.TRUE);
		assertTrue(p1Equal.evaluate(p21q) == EvaluationResult.FALSE);

		//Check that p1qNotEqual evaluates to false for vectors with p1 and deriving
		assertTrue(p1NotEqual.evaluate(pq) == EvaluationResult.TRUE);
		assertTrue(p1NotEqual.evaluate(p1q) == EvaluationResult.FALSE);
		assertTrue(p1NotEqual.evaluate(p2q) == EvaluationResult.TRUE);
		assertTrue(p1NotEqual.evaluate(p11q) == EvaluationResult.FALSE);
		assertTrue(p1NotEqual.evaluate(p21q) == EvaluationResult.TRUE);

		//Check that p11qEqual evaluates to true only for vectors with p11
		assertTrue(p11Equal.evaluate(pq) == EvaluationResult.FALSE);
		assertTrue(p11Equal.evaluate(p1q) == EvaluationResult.FALSE);
		assertTrue(p11Equal.evaluate(p2q) == EvaluationResult.FALSE);
		assertTrue(p11Equal.evaluate(p11q) == EvaluationResult.TRUE);
		assertTrue(p11Equal.evaluate(p21q) == EvaluationResult.FALSE);

		//Check that p11qNotEqual evaluates to false for vectors with p11
		assertTrue(p11NotEqual.evaluate(pq) == EvaluationResult.TRUE);
		assertTrue(p11NotEqual.evaluate(p1q) == EvaluationResult.TRUE);
		assertTrue(p11NotEqual.evaluate(p2q) == EvaluationResult.TRUE);
		assertTrue(p11NotEqual.evaluate(p11q) == EvaluationResult.FALSE);
		assertTrue(p11NotEqual.evaluate(p21q) == EvaluationResult.TRUE);
	}

	@Test
	public void updateReferencesTest() {
		MethodNode method1 = new MethodNode("method1", null);
		MethodParameterNode method1ParameterNode = new MethodParameterNode("par1", JavaLanguageHelper.TYPE_NAME_STRING, "", false, null);
		method1.addParameter(method1ParameterNode);

		RelationStatement statement = 
				RelationStatement.createRelationStatementWithLabelCondition(
						method1ParameterNode, EMathRelation.EQUAL, "ABC");

		MethodNode method2 = new MethodNode("method2", null);
		MethodParameterNode method2ParameterNode = new MethodParameterNode("par1", JavaLanguageHelper.TYPE_NAME_STRING, "", false, null);
		method2.addParameter(method2ParameterNode);

		assertNotEquals(method2ParameterNode.hashCode(), statement.getLeftParameter().hashCode());

		statement.updateReferences(method2);

		assertEquals(method2ParameterNode.hashCode(), statement.getLeftParameter().hashCode());
	}	
}
