/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

public class DragAndDropHelper {

	//	Drag and drop - DRAFT of use cases
	//
	//	Moving parameter:
	//
	//		1) Changing order of parameters in method.
	//		
	//			Production: 
	//				- Test cases are updated to reflect new order of parameters.
	//				- Constraints are left with no change.
	//			
	//			New version:
	//				- Test cases will be deleted.
	//				- Constraints will be left with no change.
	//			
	//		2) Local parameter is moved outside of method.
	//		
	//			Production:
	//				- The parameter is deleted from method. 
	//				- In test cases moved parameter is deleted.
	//				- Constraints are deleted
	//				
	//			New version:
	//				- The parameter will be converted to linked parameter 
	//				- Test cases will be left without change.
	//				- Constraints will be left without change.
	//			
	//		3) Global parameter linked in two methods (Method1, Method2) is moved into Method1.
	//		
	//			Production:
	//				- Error. Parameter is moved but Method2 still keeps reference to it, even if it is already local.
	//				- Constraints are deleted.
	//				
	//			New version:
	//				- The parameters will converted to local in both methods.
	//				- Test cases will be left without change.
	//				- Constraints will be left without change.
	//				
	//		4) Local parameter is moved from Method1 to Method2
	//		
	//			Production:
	//				- In source method the parameter is deleted.
	//				- Test cases are corrected for deleted parameter.
	//				- Constraints are deleted.
	//				
	//			New version:
	//				- ??? Deleting constraints without warning ???
	//
	//
	//	Main parts:
	//		1) Moving structures (plain Drag&Drop)
	//		2) Copying structures (Drag&Drop with Ctrl key)
	//		3) Converting local structure to global structure (Drag&Drop with Ctrl&Shift keys)
	//
	//	Ad 1) - Moving structures
	//
	//	   On start of drag detect all methods which have links to moved structures (link changed or lost).
	//	   On start of drop detect all methods which have links to destination structures.
	//	   After moving cleanup affected methods - remove test cases and deployed parameters.
	//	   
	//	   Use cases 
	//	   
	//	   1) Changing order of child structures - for Root, Class, Method
	//	   
	//			Root 		    -->  		Root 
	//				Structure1					Structure2
	//				Structure2					Structure1
	//			
	//		2) Converting structure to child structure
	//		
	//			Root			--> 		Root
	//				Structure1					Structure1
	//				Structure2						Structure2
	//				
	//			If in model there are links to Structure2, content of Structure2 should be cloned to local structures.
	//		
	//		3) Moving global structure from Root to Class and from Class to Root.
	//		
	//			Root			--> 		Root
	//				Structure1					Class
	//				Class							Structure1
	//
	//			When moving from Root to Class some methods may loose visibility of structure Structure1.
	//			For them Structure1 should be converted to local structure.
	//			
	//		4) Moving structure between classes.
	//		
	//			Root			-->			Root
	//				Class1						Class1
	//					Structure1				Class2	
	//				Class2							Structure1
	//		
	//			Methods of Class1 may lose visibility of Structure1
	//			
	//		5) Moving global structure to method (from Root or Class)
	//		
	//			Root			-->			Root
	//				Structure					Method
	//				Method							Structure
	//			
	//		6) Moving local structure to global (Root or Class)
	//		
	//			Root				-->			Root
	//				Method							Structure
	//					Structure					Method
	//					
	//			Create local structure with the same name linked to moved global structure
	//		
	//		
	//		7) Moving structures between methods

	public static boolean dragAndDropFunctionalityIsActive() {

		return true;
	}

}
