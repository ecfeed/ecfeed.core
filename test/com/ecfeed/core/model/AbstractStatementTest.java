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

import com.ecfeed.core.utils.IExtLanguageManager;
import org.junit.Test;

import com.ecfeed.core.utils.MessageStack;

public class AbstractStatementTest {

	private class StatementImplementation extends AbstractStatement {

		StatementImplementation() {
			super(null);
		}

		@Override
		public String getLeftOperandName() {
			return null;
		}
		@Override
		public AbstractStatement getCopy(){
			return null;
		}
		@Override
		public boolean updateReferences(MethodNode method){
			return true;
		}

		@Override
		public String createSignature(IExtLanguageManager extLanguageManager) {
			return null;
		}

		@Override
		public boolean compare(IStatement statement) {
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
		public List<ChoiceNode> getListOfChoices() {
			return null;
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
		StatementArray array = new StatementArray(EStatementOperator.AND, null);
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
		StatementArray array = new StatementArray(EStatementOperator.AND, null);
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
		StatementArray array = new StatementArray(EStatementOperator.AND, null);
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
