package com.utils;

public class FunctionNode {
	public int[] nodesValuesArray;
	public double[] functionValues;
	public int[] stepsForVariables;
	public boolean isVisited;
	
	/**
	 * Constructor with initializing several values.
	 * @param nodesArray
	 * @param lineArray
	 * @param noOfEntriesInFunction
	 * @param noOfVariablesFunctionIsDependentOn
	 * @param valuesInFunction 
	 */
	public FunctionNode(NetworkNode[] networkNodesArray, String[] lineArray, double[] valuesInFunction) {
		this.functionValues = valuesInFunction;
		this.nodesValuesArray = getNodesInFunction(lineArray, networkNodesArray);
	}

	public FunctionNode(NetworkNode[] networkNodesArray, int[] variablesArray,
			double[] valuesInFunction) {
		this.functionValues = valuesInFunction;
		this.nodesValuesArray = variablesArray;
		this.stepsForVariables = new int[variablesArray.length];
		int stepSize = 1;
		for(int y = variablesArray.length-1; y >= 0; y--)
		{
			this.stepsForVariables[y] = stepSize;
			stepSize = stepSize * networkNodesArray[variablesArray[y]].domainSize;
		}
	}


	private int[] getNodesInFunction(String[] lineArray, NetworkNode[] networkNodesArray) {
		int[] nodesArray = new int[Integer.parseInt(lineArray[0])];
		this.stepsForVariables = new int[nodesArray.length];
		int stepSize = 1;
		for(int i = 1; i < lineArray.length; i++)
		{
			nodesArray[i-1] = Integer.parseInt(lineArray[i]);
		}
		for(int y = nodesArray.length-1; y >= 0; y--)
		{
			this.stepsForVariables[y] = stepSize;
			stepSize = stepSize * networkNodesArray[nodesArray[y]].domainSize;
		}
		return nodesArray;
	}
	
}
