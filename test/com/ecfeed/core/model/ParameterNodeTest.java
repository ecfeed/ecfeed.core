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

import static com.ecfeed.core.testutils.TestUtilConstants.SUPPORTED_TYPES;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.ecfeed.core.testutils.RandomModelGenerator;

public class ParameterNodeTest{

	@Test
	public void addChoiceTest() {
		BasicParameterNode parameter = new BasicParameterNode("parameter", "type", "0", false, null);

		assertEquals(0, parameter.getChoices().size());

		ChoiceNode choice = new ChoiceNode("choice", "0", null);
		parameter.addChoice(choice);

		assertEquals(1, parameter.getChoices().size());
	}

	@Test
	public void getChoiceTest(){
		BasicParameterNode parameter = new BasicParameterNode("parameter", "type", "0", false, null);
		ChoiceNode p = new ChoiceNode("p", "0", null);
		ChoiceNode p1 = new ChoiceNode("p1", "0", null);
		ChoiceNode p11 = new ChoiceNode("p11", "0", null);
		parameter.addChoice(p);
		p.addChoice(p1);
		p1.addChoice(p11);

		assertEquals(p, parameter.getChoice("p"));
		assertEquals(p1, parameter.getChoice("p:p1"));
		assertEquals(p11, parameter.getChoice("p:p1:p11"));
		assertEquals(null, parameter.getChoice("p1"));
		assertEquals(null, parameter.getChoice("p11"));
		assertEquals(null, parameter.getChoice("something"));
	}

	@Test
	public void getChoicesTest() {
		BasicParameterNode parameter = new BasicParameterNode("parameter", "type", "0", false, null);
		ChoiceNode choice1 = new ChoiceNode("choice1", "0", null);
		ChoiceNode choice2 = new ChoiceNode("choice2", "0", null);
		parameter.addChoice(choice1);
		parameter.addChoice(choice2);

		List<ChoiceNode> choices = parameter.getChoices();
		assertEquals(2, choices.size());
		assertTrue(choices.contains(choice1));
		assertTrue(choices.contains(choice2));
	}

	@Test
	public void getChildrenTest() {
		BasicParameterNode parameter = new BasicParameterNode("parameter", "type", "0", false, null);
		ChoiceNode choice1 = new ChoiceNode("choice1", "0", null);
		ChoiceNode choice2 = new ChoiceNode("choice2", "0", null);
		parameter.addChoice(choice1);
		parameter.addChoice(choice2);

		List<IAbstractNode> children = parameter.getChildren();
		assertEquals(2, children.size());
		assertTrue(children.contains(choice1));
		assertTrue(children.contains(choice2));
	}

	@Test
	public void getChoiceNames() {
		BasicParameterNode parameter = new BasicParameterNode("parameter", "type", "0", false, null);
		ChoiceNode choice1 = new ChoiceNode("choice1", "0", null);
		ChoiceNode choice2 = new ChoiceNode("choice2", "0", null);
		parameter.addChoice(choice1);
		parameter.addChoice(choice2);

		Set<String> choiceNames = parameter.getChoiceNames();
		assertTrue(choiceNames.contains("choice1"));
		assertTrue(choiceNames.contains("choice2"));
	}

	@Test
	public void getLeafChoiceNamesTest() {
		BasicParameterNode parameter = new BasicParameterNode("parameter", "type", "0", false, null);
		ChoiceNode p1 = new ChoiceNode("p1", "0", null);
		ChoiceNode p11 = new ChoiceNode("p11", "0", null);
		ChoiceNode p12 = new ChoiceNode("p12", "0", null);
		ChoiceNode p2 = new ChoiceNode("p2", "0", null);
		p1.addChoice(p11);
		p1.addChoice(p12);
		parameter.addChoice(p1);
		parameter.addChoice(p2);

		Set<String> leafNames = parameter.getLeafChoiceNames();
		assertTrue(leafNames.contains("p1:p11"));
		assertTrue(leafNames.contains("p1:p12"));
		assertTrue(leafNames.contains("p2"));
		assertFalse(leafNames.contains("p1"));
	}

	@Test
	public void getAllChoiceNamesTest(){
		BasicParameterNode parameter = new BasicParameterNode("parameter", "type", "0", false, null);
		ChoiceNode p1 = new ChoiceNode("p1", "0", null);
		ChoiceNode p11 = new ChoiceNode("p11", "0", null);
		ChoiceNode p12 = new ChoiceNode("p12", "0", null);
		ChoiceNode p2 = new ChoiceNode("p2", "0", null);
		p1.addChoice(p11);
		p1.addChoice(p12);
		parameter.addChoice(p1);
		parameter.addChoice(p2);

		Set<String> names = parameter.getAllChoiceNames();

		assertTrue(names.contains("p1"));
		assertTrue(names.contains("p1:p11"));
		assertTrue(names.contains("p1:p12"));
		assertTrue(names.contains("p2"));
	}


	@Test
	public void getMethodTest() {
		MethodNode method = new MethodNode("method", null);
		BasicParameterNode parameter = new BasicParameterNode("parameter", "type", "0", false, null);
		method.addParameter(parameter);

		assertEquals(method, parameter.getMethod());
	}

	@Test
	public void getLeafChoicesTest(){
		BasicParameterNode parameter = new BasicParameterNode("parameter", "type", "0", false, null);

		ChoiceNode p1 = new ChoiceNode("p1", "0", null);
		ChoiceNode p2 = new ChoiceNode("p1", "0", null);
		ChoiceNode p3 = new ChoiceNode("p1", "0", null);

		ChoiceNode p21 = new ChoiceNode("p21", "0", null);
		ChoiceNode p22 = new ChoiceNode("p22", "0", null);
		ChoiceNode p23 = new ChoiceNode("p23", "0", null);

		ChoiceNode p31 = new ChoiceNode("p31", "0", null);
		ChoiceNode p32 = new ChoiceNode("p32", "0", null);
		ChoiceNode p33 = new ChoiceNode("p33", "0", null);

		ChoiceNode p321 = new ChoiceNode("p321", "0", null);
		ChoiceNode p322 = new ChoiceNode("p322", "0", null);
		ChoiceNode p323 = new ChoiceNode("p323", "0", null);

		parameter.addChoice(p1);
		parameter.addChoice(p2);
		parameter.addChoice(p3);
		p2.addChoice(p21);
		p2.addChoice(p22);
		p2.addChoice(p23);
		p3.addChoice(p31);
		p3.addChoice(p32);
		p3.addChoice(p33);
		p32.addChoice(p321);
		p32.addChoice(p322);
		p32.addChoice(p323);

		assertTrue(parameter.getLeafChoices().contains(p1));
		assertTrue(parameter.getLeafChoices().contains(p21));
		assertTrue(parameter.getLeafChoices().contains(p22));
		assertTrue(parameter.getLeafChoices().contains(p23));
		assertTrue(parameter.getLeafChoices().contains(p31));
		assertTrue(parameter.getLeafChoices().contains(p321));
		assertTrue(parameter.getLeafChoices().contains(p322));
		assertTrue(parameter.getLeafChoices().contains(p323));
		assertTrue(parameter.getLeafChoices().contains(p33));

		assertFalse(parameter.getLeafChoices().contains(p2));
		assertFalse(parameter.getLeafChoices().contains(p3));
		assertFalse(parameter.getLeafChoices().contains(p32));
	}

	@Test
	public void getAllLabelsTest(){
		ChoiceNode p1 = new ChoiceNode("p1", "0", null);
		p1.addLabel("l11");
		p1.addLabel("l12");
		ChoiceNode p2 = new ChoiceNode("p2", "0", null);
		p2.addLabel("l21");
		p2.addLabel("l22");
		ChoiceNode p11 = new ChoiceNode("p11", "0", null);
		p11.addLabel("l111");
		p11.addLabel("l112");
		ChoiceNode p12 = new ChoiceNode("p12", "0", null);
		p12.addLabel("l121");
		p12.addLabel("l122");
		ChoiceNode p21 = new ChoiceNode("p21", "0", null);
		p21.addLabel("l211");
		p21.addLabel("l212");//	protected Set<String> getAllChoiceNames(List<ChoiceNode> choices) {
		//		Set<String> result = new LinkedHashSet<String>();
		//		for(ChoiceNode p : choices){
		//			result.add(p.getQualifiedName());
		//			result.addAll(p.getAllChoiceNames());
		//		}
		//		return result;
		//	}
		//

		ChoiceNode p22 = new ChoiceNode("p22", "0", null);
		p22.addLabel("l221");
		p22.addLabel("l222");

		p1.addChoice(p11);
		p1.addChoice(p12);
		p2.addChoice(p21);
		p2.addChoice(p22);

		BasicParameterNode c = new BasicParameterNode("c", "type", "0", false, null);
		c.addChoice(p1);
		c.addChoice(p2);

		Set<String> labels = c.getLeafLabels();

		assertTrue(labels.contains("l11"));
		assertTrue(labels.contains("l12"));
		assertTrue(labels.contains("l21"));
		assertTrue(labels.contains("l21"));
		assertTrue(labels.contains("l111"));
		assertTrue(labels.contains("l112"));
		assertTrue(labels.contains("l121"));
		assertTrue(labels.contains("l121"));
		assertTrue(labels.contains("l211"));
		assertTrue(labels.contains("l211"));
		assertTrue(labels.contains("l221"));
		assertTrue(labels.contains("l221"));

	}

	/*****************compare()************************/
	//	@Test
	public void compareSmokeTest(){
		for(String type : SUPPORTED_TYPES){
			for(Boolean expected : new Boolean[]{true, false}){
				BasicParameterNode c = new RandomModelGenerator().generateParameter(type, expected, 3, 3, 3);

				assertTrue(c.isMatch(c));
			}
		}
	}

	@Test
	public void setNameTest(){

		BasicParameterNode c1 = new BasicParameterNode("c", "type", "0", true, null);

		try {
			c1.setName("c*1");
			fail();
		} catch (Exception e) {
		}

		try {
			c1.setName("c 1");
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void compareNameTest(){
		BasicParameterNode c1 = new BasicParameterNode("c", "type", "0", true, null);
		BasicParameterNode c2 = new BasicParameterNode("c", "type", "0", true, null);

		assertTrue(c1.isMatch(c2));

		c1.setName("c1");
		assertFalse(c1.isMatch(c2));
		c2.setName("c1");
		assertTrue(c1.isMatch(c2));
	}

	@Test
	public void compareTypeTest(){
		BasicParameterNode c1 = new BasicParameterNode("c", "type", "0", true, null);
		BasicParameterNode c2 = new BasicParameterNode("c", "type", "0", true, null);

		assertTrue(c1.isMatch(c2));

		c1.setType("type1");
		assertFalse(c1.isMatch(c2));
		c2.setType("type1");
		assertTrue(c1.isMatch(c2));
	}

	@Test
	public void compareExpectedTest(){
		BasicParameterNode c1 = new BasicParameterNode("c", "type", "0", true, null);
		BasicParameterNode c2 = new BasicParameterNode("c", "type", "0", true, null);

		assertTrue(c1.isMatch(c2));

		c1.setExpected(false);
		assertFalse(c1.isMatch(c2));
		c2.setExpected(false);
		assertTrue(c1.isMatch(c2));
	}

	@Test
	public void compareDefaultValueTest(){
		BasicParameterNode c1 = new BasicParameterNode("c", "type", "0", true, null);
		BasicParameterNode c2 = new BasicParameterNode("c", "type", "0", true, null);

		assertTrue(c1.isMatch(c2));

		c1.setDefaultValueString("new default value");
		assertFalse(c1.isMatch(c2));
		c2.setDefaultValueString("new default value");
		assertTrue(c1.isMatch(c2));
	}

	@Test
	public void compareChoicesTest(){
		BasicParameterNode c1 = new BasicParameterNode("c", "type", "0", true, null);
		BasicParameterNode c2 = new BasicParameterNode("c", "type", "0", true, null);

		assertTrue(c1.isMatch(c2));

		ChoiceNode p1 = new ChoiceNode("p", "value", null);
		ChoiceNode p2 = new ChoiceNode("p", "value", null);

		c1.addChoice(p1);
		assertFalse(c1.isMatch(c2));
		c2.addChoice(p2);
		assertTrue(c1.isMatch(c2));

		p1.setName("p1");
		assertFalse(c1.isMatch(c2));
		p2.setName("p1");
		assertTrue(c1.isMatch(c2));
	}


	@Test
	public void compareTest(){
		assertTrue(new BasicParameterNode("c", "int", "0", true, null).isMatch(new BasicParameterNode("c", "int", "0", true, null)));
		assertTrue(new BasicParameterNode("c", "int", "0", false, null).isMatch(new BasicParameterNode("c", "int", "0", false, null)));

		assertFalse(new BasicParameterNode("c1", "int", "0", false, null).isMatch(new BasicParameterNode("c", "int", "0", false, null)));
		assertFalse(new BasicParameterNode("c", "boolean", "0", false, null).isMatch(new BasicParameterNode("c", "int", "0", false, null)));
		assertFalse(new BasicParameterNode("c", "int", "0", true, null).isMatch(new BasicParameterNode("c", "int", "0", false, null)));

		BasicParameterNode c1 = new BasicParameterNode("c", "int", "0", false, null);
		BasicParameterNode c2 = new BasicParameterNode("c", "int", "0", false, null);
		assertTrue(c1.isMatch(c2));

		c1.setDefaultValueString("cc");
		assertFalse(c1.isMatch(c2));
		c2.setDefaultValueString("cc");
		assertTrue(c1.isMatch(c2));

		c1.addChoice(new ChoiceNode("p", "x", null));
		c1.setDefaultValueString("cc");
		assertFalse(c1.isMatch(c2));
		c2.addChoice(new ChoiceNode("p1", "x", null));
		assertFalse(c1.isMatch(c2));
		c2.getChoice("p1").setName("p");
		assertTrue(c1.isMatch(c2));
	}
}
