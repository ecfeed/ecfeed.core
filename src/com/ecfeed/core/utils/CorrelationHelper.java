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

public class CorrelationHelper {

	public static double calculateCorrelation(int[] arrayX, int[] arrayY) {

		if (arrayX == null) {
			ExceptionHelper.reportRuntimeException("Empty array 1.");
		}

		if (arrayY == null) {
			ExceptionHelper.reportRuntimeException("Empty array 2.");
		}

		if (arrayX.length != arrayY.length) {
			ExceptionHelper.reportRuntimeException("Arrays have different lengths.");
		}

		double result = calculateCorrelationBetweenEvenArraysOfIntegers(arrayX, arrayY);

		if (Double.isNaN(result)) {
			return 0;
		}

		return result;
	}

	public static double calculateCorrelationBetweenEvenArraysOfIntegers(int[] arrayX, int[] arrayY) {

		double sumOfX = 0.0;
		double sumOfY = 0.0;
		double sumOfXX = 0.0;
		double sumOfYY = 0.0;
		double sumOfXY = 0.0;

		int arrayLength = arrayX.length;

		for (int index = 0; index < arrayLength; ++index) {

			double x = arrayX[index];
			double y = arrayY[index];

			sumOfX += x;
			sumOfY += y;
			sumOfXX += x * x;
			sumOfYY += y * y;
			sumOfXY += x * y;
		}

		double covariation = sumOfXY / arrayLength - sumOfX * sumOfY / arrayLength / arrayLength;

		double standardErrorOfX = Math.sqrt(sumOfXX / arrayLength -  sumOfX * sumOfX / arrayLength / arrayLength);

		double standardErrorOfY = Math.sqrt(sumOfYY / arrayLength -  sumOfY * sumOfY / arrayLength / arrayLength);

		double normalizedCovariation = covariation / standardErrorOfX / standardErrorOfY;

		return normalizedCovariation;
	}	


	public static void main(String args[]) 
	{  
		double result = calculateCorrelation(new int[] {1, 2, 3, 4}, new int[] {1, 2, 3, 4});
		System.out.println(result);

		result = calculateCorrelation(new int[] {1, 2, 2, 4}, new int[] {1, 2, 3, 4});
		System.out.println(result);

		result = calculateCorrelation(new int[] {1, 3, 2, 4}, new int[] {1, 2, 3, 4});
		System.out.println(result);

		result = calculateCorrelation(new int[] {1, 1, 1, 4}, new int[] {1, 2, 3, 4});
		System.out.println(result);

		result = calculateCorrelation(new int[] {1, 1, 2, 1}, new int[] {1, 2, 3, 4});
		System.out.println(result);

		result = calculateCorrelation(new int[] {1, 1, 1, 1}, new int[] {1, 2, 3, 4});
		System.out.println(result);
	}  
}
