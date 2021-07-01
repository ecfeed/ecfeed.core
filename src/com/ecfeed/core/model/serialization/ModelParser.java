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
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelChangeRegistrator;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
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
import nu.xom.ParsingException;

public class ModelParser {

	Builder fBuilder = new Builder();
	XomAnalyser fXomAnalyser = null;
	
	public ModelParser() {
	}

	public RootNode parseModel(
			String modelXml, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings outErrorList) throws ParserException {

		InputStream istream = new ByteArrayInputStream(modelXml.getBytes());

		return parseModel(istream, modelChangeRegistrator, outErrorList);
	}

	public RootNode parseModel(
			InputStream istream, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings outErrorList) throws ParserException {

		try {
			Document document = fBuilder.build(istream);
			Element element = document.getRootElement();

			int modelVersion = XomModelVersionDetector.getVersion(element);
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
			
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	private void createXomAnalyser(int version) throws ParserException {
		if (fXomAnalyser == null) {
			fXomAnalyser = XomAnalyserFactory.createXomAnalyser(version);
		}			
	}

	private XomAnalyser getXomAnalyser() throws ParserException {
		if (fXomAnalyser == null) {
			ParserException.report("XomAnalyzer must not be null.");
		}
		return fXomAnalyser;
	}

	// TODO
	public ClassNode parseClass(
			InputStream istream, ListOfStrings outErrorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseClass(document.getRootElement(), null, outErrorList).get();
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	public MethodNode parseMethod(
			InputStream istream, ListOfStrings outErrorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseMethod(document.getRootElement(), null, outErrorList).get();
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	public GlobalParameterNode parseGlobalParameter(
			InputStream istream, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings outErrorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return new ModelParserForGlobalParameter().parseGlobalParameter(document.getRootElement(), modelChangeRegistrator, outErrorList).get();
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	public MethodParameterNode parseMethodParameter(
			InputStream istream, MethodNode method, ListOfStrings outErrorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return new ModelParserForMethodParameter().parseMethodParameter(document.getRootElement(), method, outErrorList).get();
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	public ChoiceNode parseChoice(
			InputStream istream, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings outErrorList) throws ParserException {

		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseChoice(document.getRootElement(), modelChangeRegistrator, outErrorList).get();

		} catch (ParsingException e) {

			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;

		} catch (IOException e) {

			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	public TestCaseNode parseTestCase(
			InputStream istream, MethodNode method, ListOfStrings outErrorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return new ModelParserForTestCase().parseTestCase(document.getRootElement(), method, outErrorList).get();
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	public Optional<ConstraintNode> parseConstraint(
			InputStream istream, MethodNode method, ListOfStrings outErrorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseConstraint(document.getRootElement(), method, outErrorList);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	public AbstractStatement parseStatement(
			InputStream istream, MethodNode method, ListOfStrings outErrorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseStatement(document.getRootElement(), method, outErrorList).get();
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	public StaticStatement parseStaticStatement(
			InputStream istream, IModelChangeRegistrator modelChangeRegistrator, ListOfStrings outErrorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseStaticStatement(document.getRootElement(), modelChangeRegistrator, outErrorList);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	public AbstractStatement parseChoicesParentStatement(
			InputStream istream, MethodNode method, ListOfStrings outErrorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseChoiceStatement(document.getRootElement(), method, outErrorList);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return null;
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return null;
		}
	}

	public ExpectedValueStatement parseExpectedValueStatement(
			InputStream istream, MethodNode method, ListOfStrings outErrorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseExpectedValueStatement(document.getRootElement(), method, outErrorList);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return new ExpectedValueStatement(null, null, null);
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return new ExpectedValueStatement(null, null, null);
		}
	}

	public StatementArray parseStatementArray(
			InputStream istream, MethodNode method, ListOfStrings outErrorList) throws ParserException {
		
		try {
			Document document = fBuilder.build(istream);
			return getXomAnalyser().parseStatementArray(document.getRootElement(), method, outErrorList);
		} catch (ParsingException e) {
			ParserException.report(Messages.PARSING_EXCEPTION(e));
			return new StatementArray(null, method.getModelChangeRegistrator());
		} catch (IOException e) {
			ParserException.report(Messages.IO_EXCEPTION(e));
			return new StatementArray(null, method.getModelChangeRegistrator());
		}
	}
}
