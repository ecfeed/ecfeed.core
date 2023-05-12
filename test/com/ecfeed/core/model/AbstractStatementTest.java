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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.MessageStack;
import com.ecfeed.core.utils.ParameterConversionItem;

public class AbstractStatementTest {

	private class StatementImplementation extends AbstractStatement {

		StatementImplementation() {
			super(null);
		}

		@Override
		public CompositeParameterNode getLeftParameterLinkingContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public AbstractStatement makeClone(){
			return null;
		}

		@Override
		public AbstractStatement makeClone(Optional<NodeMapper> nodeMapper) {
			return null;
		}

		@Override
		public AbstractStatement createCopy(NodeMapper mapper) {
			return null;
		}

		//		@Override
		//		public boolean updateReferences(IParametersAndConstraintsParentNode method){
		//			return true;
		//		}

		@Override
		public String createSignature(IExtLanguageManager extLanguageManager) {
			return null;
		}

		@Override
		public boolean isEqualTo(IStatement statement) {
			return false;
		}

		@Override
		public Object accept(IStatementVisitor visitor) {
			return null;
		}

		@Override
		public boolean mentions(int methodParameterIndex) {
			return false;
		}

		@Override
		public boolean isAmbiguous(List<List<ChoiceNode>> values, MessageStack messageStack, IExtLanguageManager extLanguageManager) {
			return false;
		}

		@Override
		public void derandomize() {
		}

		@Override
		public boolean isAmbiguous(List<List<ChoiceNode>> values) {
			return false;
		}

		@Override
		protected void convert(ParameterConversionItem parameterConversionItem) {
		}

		@Override
		public boolean mentionsChoiceOfParameter(BasicParameterNode parameter) {
			return false;
		}

		@Override
		public List<ChoiceNode> getChoices() {
			return null;
		}

		@Override
		public List<String> getLabels(BasicParameterNode methodParameterNode) {
			return null;
		}

		@Override
		public List<ChoiceNode> getChoices(BasicParameterNode methodParameterNode) {
			return null;
		}

		@Override
		public String getLeftOperandName() {
			return null;
		}

		@Override
		public BasicParameterNode getLeftParameter() {
			return null;
		}

		@Override
		public boolean isConsistent(IParametersAndConstraintsParentNode parentMethodNode) {
			return false;
		}

	}

	@Test
	public void testParent() {
		AbstractStatement statement1 = new StatementImplementation();
		AbstractStatement statement2 = new StatementImplementation();

		statement2.setParent(statement1);
		assertEquals(statement1, statement2.getParent());
	}

	@Test
	public void testGetChildren() {
		StatementArray array = new StatementArray(StatementArrayOperator.AND, null);
		AbstractStatement statement2 = new StatementImplementation();
		AbstractStatement statement3 = new StatementImplementation();

		array.addStatement(statement2);
		array.addStatement(statement3);

		List<AbstractStatement> children = array.getChildren();
		assertEquals(2, children.size());
		assertTrue(children.contains(statement2));
		assertTrue(children.contains(statement3));
	}

	@Test
	public void testReplaceChild() {
		StatementArray array = new StatementArray(StatementArrayOperator.AND, null);
		AbstractStatement statement2 = new StatementImplementation();
		AbstractStatement statement3 = new StatementImplementation();

		array.addStatement(statement2);
		List<AbstractStatement> children = array.getChildren();
		assertEquals(1, children.size());
		assertTrue(children.contains(statement2));

		array.replaceChild(statement2, statement3);
		children = array.getChildren();
		assertEquals(1, children.size());
		assertTrue(children.contains(statement3));
	}

	@Test
	public void testRemoveChild() {
		StatementArray array = new StatementArray(StatementArrayOperator.AND, null);
		AbstractStatement statement2 = new StatementImplementation();
		AbstractStatement statement3 = new StatementImplementation();

		array.addStatement(statement2);
		array.addStatement(statement3);
		List<AbstractStatement> children = array.getChildren();
		assertEquals(2, children.size());
		assertTrue(children.contains(statement2));
		assertTrue(children.contains(statement3));

		array.removeChild(statement2);
		children = array.getChildren();
		assertEquals(1, children.size());
		assertFalse(children.contains(statement2));
		assertTrue(children.contains(statement3));

	}
}
