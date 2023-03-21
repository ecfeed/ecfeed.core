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

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.utils.ListOfStrings;
import nu.xom.Element;

import java.util.Optional;

public interface IModelParserForMethodDeployedParameter {

	Optional<BasicParameterNode> parseMethodDeployedParameter(Element parameterElement, MethodNode method, ListOfStrings errorList);
}
