/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.utils;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractParameterSignatureHelper;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.Decorations;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.ExtendedName;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.TypeIncluded;
import com.ecfeed.core.model.AbstractParameterSignatureHelper.TypeOfLink;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;

public class ParameterWithLinkingContextHelper {

	public static String createSignature(ParameterWithLinkingContext parameterWithLinkingContext) {

		AbstractParameterNode parameter = parameterWithLinkingContext.getParameter();		
		AbstractParameterNode context = parameterWithLinkingContext.getLinkingContext();

		//		String signatureOld = 
		//				AbstractParameterSignatureHelper.createSignatureOfParameterWithContext(parameter, context);
		//
		String signature = 
				AbstractParameterSignatureHelper.createSignatureWithLinkNewStandard(
						context,
						ExtendedName.PATH_TO_TOP_CONTAINTER,
						TypeOfLink.NORMAL,
						parameter,
						ExtendedName.PATH_TO_TOP_CONTAINTER,
						Decorations.NO,
						TypeIncluded.NO,
						new ExtLanguageManagerForJava());

		return signature;
	}

}
