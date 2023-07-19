/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.serialization;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.ListOfStrings;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

public class ModelParser {

	Builder fBuilder = new Builder();
	XomAnalyser fXomAnalyser = null;

	public ModelParser() {
	}

	public RootNode parseModel(
			String modelXml, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings outErrorList) {

		InputStream istream = new ByteArrayInputStream(modelXml.getBytes());

		return parseModel(istream, modelChangeRegistrator, outErrorList);
	}

	public RootNode parseModel(
			InputStream istream, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings outErrorList) {

		try {
			Document document = fBuilder.build(istream);
			Element element = document.getRootElement();

			int modelVersion = XomModelVersionDetector.getVersion(element, outErrorList);
			int softwareVersion = ModelVersionDistributor.getCurrentSoftwareVersion(); 

			if (modelVersion > softwareVersion) {
				ExceptionHelper.reportRuntimeException(
						"Can not read ect file. It has newer version: " + modelVersion +
						" than this software: " + softwareVersion + 
						". Please install newer version of the software.");
			}

			createXomAnalyser(modelVersion);

			RootNode rootNode = getXomAnalyser().parseRoot(element, modelChangeRegistrator, outErrorList);
			return rootNode;

		} catch (Exception e) {
			outErrorList.add(e.getMessage());
			return null;
		} 
	}

	private void createXomAnalyser(int version) {
		if (fXomAnalyser == null) {
			fXomAnalyser = XomAnalyserFactory.createXomAnalyser(version);
		}			
	}

	private XomAnalyser getXomAnalyser() {
		if (fXomAnalyser == null) {
			ExceptionHelper.reportRuntimeException("XomAnalyzer must not be null.");
		}
		return fXomAnalyser;
	}

	public ClassNode parseClass(
			InputStream istream, ListOfStrings outErrorList) {

		try {
			Document document = fBuilder.build(istream);

			ModelParserForClass modelParserForClass = ModelParserHelper.createStandardModelParserForClass();
			return modelParserForClass.parseAndAddClass(
					document.getRootElement(), null, outErrorList).get();

		} catch (Exception e) {
			outErrorList.add(e.getMessage());
			return null;
		} 
	}

	public MethodNode parseMethod(
			InputStream istream, ListOfStrings outErrorList) {

		try {
			Document document = fBuilder.build(istream);

			ModelParserForMethod modelParserForMethod = ModelParserHelper.createStandardModelParserForMethod();

			return modelParserForMethod.parseMethod(document.getRootElement(), null, outErrorList).get();
		} catch (Exception e) {
			outErrorList.add(e.getMessage());
			return null;
		} 
	}

	public BasicParameterNode parseGlobalParameter(
			InputStream istream, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings outErrorList) {

		try {
			Document document = fBuilder.build(istream);

			ModelParserForGlobalParameter modelParserForGlobalParameter
			= new ModelParserForGlobalParameter();

			return modelParserForGlobalParameter.parseGlobalBasicParameter(
					document.getRootElement(), 
					modelChangeRegistrator, 
					outErrorList).get();

		} catch (Exception e) {
			outErrorList.add(e.getMessage());
			return null;
		} 
	}

	public BasicParameterNode parseMethodParameter(
			InputStream istream, MethodNode method, ListOfStrings outErrorList) {

		try {
			Document document = fBuilder.build(istream);
			return new ModelParserForMethodParameter().parseMethodParameter(document.getRootElement(), method, method, outErrorList).get();
		} catch (Exception e) {
			outErrorList.add(e.getMessage());
			return null;
		} 
	}

	public ChoiceNode parseChoice(
			InputStream istream, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings outErrorList) {

		try {
			Document document = fBuilder.build(istream);
			return new ModelParserForChoice(modelChangeRegistrator).parseChoice(document.getRootElement(), outErrorList).get();

		} catch (Exception e) {
			outErrorList.add(e.getMessage());
			return null;

		} 
	}

	public TestCaseNode parseTestCase(
			InputStream istream, MethodNode method, ListOfStrings outErrorList) {

		try {
			Document document = fBuilder.build(istream);
			return new ModelParserForTestCase().parseTestCase(document.getRootElement(), method, outErrorList).get();
		} catch (Exception e) {
			outErrorList.add(e.getMessage());
			return null;
		} 
	}

	public Optional<ConstraintNode> parseConstraint(
			InputStream istream, MethodNode method, ListOfStrings outErrorList) {

		try {
			Document document = fBuilder.build(istream);
			return new ModelParserForConstraint().parseConstraint(document.getRootElement(), method, outErrorList);
		} catch (Exception e) {
			outErrorList.add(e.getMessage());
			return null;
		} 
	}

	public AbstractStatement parseStatement(
			InputStream istream, MethodNode method, ListOfStrings outErrorList) {

		try {
			Document document = fBuilder.build(istream);
			return new ModelParserForConstraint().parseStatement(document.getRootElement(), method, outErrorList).get();
		} catch (Exception e) {
			outErrorList.add(e.getMessage());
			return null;
		} 
	}

	public StaticStatement parseStaticStatement(
			InputStream istream, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings outErrorList) {

		try {
			Document document = fBuilder.build(istream);
			return new ModelParserForConstraint().parseStaticStatement(document.getRootElement(), modelChangeRegistrator, outErrorList);
		} catch (Exception e) {
			outErrorList.add(e.getMessage());
			return null;
		} 
	}

	public AbstractStatement parseChoicesParentStatement(
			InputStream istream, MethodNode method, ListOfStrings outErrorList) {

		try {
			Document document = fBuilder.build(istream);
			return new ModelParserForConstraint().parseChoiceStatement(document.getRootElement(), method, outErrorList);
		} catch (Exception e) {
			outErrorList.add(e.getMessage());
			return null;
		} 
	}

	public ExpectedValueStatement parseExpectedValueStatement(
			InputStream istream, MethodNode method, ListOfStrings outErrorList) {

		try {
			Document document = fBuilder.build(istream);
			return new ModelParserForConstraint().parseExpectedValueStatement(document.getRootElement(), method, outErrorList);
		} catch (Exception e) {
			outErrorList.add(e.getMessage());
			return new ExpectedValueStatement(null, null, null, null);
		} 
	}

	public StatementArray parseStatementArray(
			InputStream istream, MethodNode method, ListOfStrings outErrorList) {

		try {
			Document document = fBuilder.build(istream);
			return new ModelParserForConstraint().parseStatementArray(document.getRootElement(), method, outErrorList);
		} catch (Exception e) {
			outErrorList.add(e.getMessage());
			return new StatementArray(null, method.getModelChangeRegistrator());
		} 
	}
}
