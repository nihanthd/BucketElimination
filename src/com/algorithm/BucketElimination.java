package com.algorithm;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import com.utils.AdjacencyListComparator;
import com.utils.FunctionNode;
import com.utils.InputReader;
import com.utils.NetworkNode;
import com.utils.Utilities;

public class BucketElimination {
	
	String networkType = null;
	NetworkNode[] networkNodesArray = null;
	FunctionNode[] functionNodesArray = null;
	int[] evidenceVariables = null;
	int[] evidenceVariablesValues = null;
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		BucketElimination be = new BucketElimination();
		InputReader.readInput(be, args[0]);
		InputReader.readEvidence(be, args[0]);
		
		instantiateEvidences(be, be.evidenceVariables, be.evidenceVariablesValues, be.functionNodesArray, be.networkNodesArray);
		generateInteractionGraph(be.functionNodesArray, be.networkNodesArray, be.evidenceVariables);
		int[] bucketsMinDegreeOrder = generateMinDegree(be.networkNodesArray, be.evidenceVariables);
		bucketElimination(bucketsMinDegreeOrder, be, be.networkType);
	}
	
	/**
	 * This function instantiates all the evidences for a given function.
	 * @param be
	 * @param evidenceVariables
	 * @param evidenceVariablesValues
	 * @param functionNodesArray
	 * @param networkNodesArray
	 */
	private static void instantiateEvidences(BucketElimination be, int[] evidenceVariables,
			int[] evidenceVariablesValues, FunctionNode[] functionNodesArray, NetworkNode[] networkNodesArray) {
		for(int i = 0; i < evidenceVariables.length; i++)
		{
			for(int j = 0; j < functionNodesArray.length; j++)
			{
				int domainSizeOfVariable = networkNodesArray[evidenceVariables[i]].domainSize;
				if(Utilities.isVariablePresentInFunction(evidenceVariables[i], functionNodesArray[j].nodesValuesArray))
				{
					be.functionNodesArray[j] = instantiateEvidenceForVariableAndFunction(evidenceVariables[i], evidenceVariablesValues[i], functionNodesArray[j], domainSizeOfVariable, networkNodesArray);
				}
			}
		}
	}
	
	/**
	 * This method instantiates the variable on a function for a variable.
	 * @param be 
	 * @param evidenceVariable
	 * @param evidenceVariableValue
	 * @param functionNode
	 * @param domainSizeOfVariable 
	 * @param networkNodesArray 
	 * @return 
	 */
	private static FunctionNode instantiateEvidenceForVariableAndFunction(int evidenceVariable, int evidenceVariableValue,
			FunctionNode functionNode, int domainSizeOfVariable, NetworkNode[] networkNodesArray) {
		int strideofVariable = Utilities.getStrideForVariableInFunction(functionNode, evidenceVariable);
		int stepIncrement = 0;
		int domainIncrementer = 0;
		for(int i = 0; i < functionNode.functionValues.length; i++)
		{
			if(strideofVariable == stepIncrement)
			{
				stepIncrement = 1;
				domainIncrementer++;
			}
			else
			{
				stepIncrement++;
			}
			if(domainIncrementer == domainSizeOfVariable)
			{
				domainIncrementer = 0;
			}
			if(domainIncrementer != evidenceVariableValue)
			{
				functionNode.functionValues[i] = 0.0;
			}
			else
			{
				i = i+ strideofVariable-1;
				stepIncrement = stepIncrement + strideofVariable - 1;
			}
		}
		functionNode = factorSum(functionNode, evidenceVariable, networkNodesArray);
		return functionNode;
	}

	/**
	 * This method does the bucket elimination.
	 * @param bucketsMinDegreeOrder
	 * @param be
	 * @param networkType 
	 */
	private static void bucketElimination(int[] bucketsMinDegreeOrder,
			BucketElimination be, String networkType) {
		LinkedList<FunctionNode> temporaryFunctions = new LinkedList<FunctionNode>();
		for(int b: bucketsMinDegreeOrder)
		{
//			System.out.println("Processing : " + b);
			updateFunctionsForBucket(be.functionNodesArray, b, be.networkNodesArray, temporaryFunctions);
		}
		/*if(networkType.equalsIgnoreCase("markov"))
		{
			System.out.println(temporaryFunctions.get(temporaryFunctions.size()-1).functionValues[0]);
		}
		else
		{
			
			System.out.println(getProductOfEmptyVariableFunction(be.functionNodesArray, temporaryFunctions));
		}*/
		double probabilityOfEvidence = getProductOfEmptyVariableFunction(be.functionNodesArray, temporaryFunctions);
		System.out.println("Probability of Evidence is : " + probabilityOfEvidence);
	}
	
	/**
	 * This function gives product of the functions that have no variables in it and are not visited..
	 * @param functionNodesArray
	 * @param temporaryFunctions
	 * @return
	 */
	private static double getProductOfEmptyVariableFunction(
			FunctionNode[] functionNodesArray,
			LinkedList<FunctionNode> temporaryFunctions) {
		double product = 1.0;
		for(int i = 0; i < functionNodesArray.length; i++)
		{
			if(!functionNodesArray[i].isVisited && functionNodesArray[i].nodesValuesArray.length == 0 && functionNodesArray[i].functionValues.length == 1)
			{
				product = product * functionNodesArray[i].functionValues[0];
			}
		}
		for(FunctionNode tempFunction : temporaryFunctions)
		{
			if(!tempFunction.isVisited && tempFunction.nodesValuesArray.length == 0 && tempFunction.functionValues.length == 1)
			{
				product = product * tempFunction.functionValues[0];
			}
		}
		return product;
	}

	public static void updateFunctionsForBucket(FunctionNode[] functionNodesArray, int b, NetworkNode[] networkNodesArray, LinkedList<FunctionNode> temporaryFunctions) {
		FunctionNode node = null;
		for(int i = 0; i < functionNodesArray.length; i++)
		{
			if(!functionNodesArray[i].isVisited && functionNodesArray[i].nodesValuesArray.length > 0 &&Utilities.isVariablePresentInFunction(b, functionNodesArray[i].nodesValuesArray))
			{
				if(node != null)
				{
					node = factorProductOfTwoFunctions(node, functionNodesArray[i], networkNodesArray);
				}
				else
				{
					node = functionNodesArray[i];
				}
				functionNodesArray[i].isVisited = true;
			}
		}
		for(FunctionNode tempNode : temporaryFunctions)
		{
			if(!tempNode.isVisited && tempNode.nodesValuesArray.length > 0 &&Utilities.isVariablePresentInFunction(b, tempNode.nodesValuesArray))
			{
				if(node != null)
				{
					node = factorProductOfTwoFunctions(node, tempNode, networkNodesArray);
				}
				else
				{
					node = tempNode;
				}
				tempNode.isVisited = true;
			}
		}
		if(node != null)
		{
			node = factorSum(node, b, networkNodesArray);
			temporaryFunctions.add(node);
		}
	}
	
	/**
	 * This method generates the min order computation of the given graph
	 * @param networkNodesArray 
	 * @param evidenceVariables 
	 * @return 
	 */
	private static int[] generateMinDegree(NetworkNode[] networkNodesArray, int[] evidenceVariables) {
		int length = networkNodesArray.length;
		Comparator<NetworkNode> comparator = new AdjacencyListComparator();
        PriorityQueue<NetworkNode> queue = 
            new PriorityQueue<NetworkNode>(length, comparator);
        for(int i = 0; i < length; i++)
        {
        	if(!Utilities.isVariablePresentInEvidenceVariables(networkNodesArray[i].nodeValue, evidenceVariables))
        	{
        		queue.add(networkNodesArray[i]);
        	}
        }
        int[] bucketsMinDegreeOrder = new int[queue.size()];
        int i = 0;
        while(!queue.isEmpty())
        {
        	NetworkNode node = queue.poll();
        	Utilities.updateAdjacentNodes(node, networkNodesArray);
        	for(int k: node.adjacentNodes)
        	{
        		if(queue.remove(networkNodesArray[k]))
        		{
        			queue.add(networkNodesArray[k]);
        		}
        	}
        	bucketsMinDegreeOrder[i++] = node.nodeValue;
        }
        return bucketsMinDegreeOrder;
	}

	/**
	 * This method generates interaction graph for given nodes and factors
	 * @param evidenceVariables 
	 * @param functionNodesArray2
	 * @param networkNodesArray2
	 */
	private static void generateInteractionGraph(
			FunctionNode[] functionNodesArray, NetworkNode[] networkNodesArray, int[] evidenceVariables) {

		for(int j = 0; j < functionNodesArray.length; j++)
		{
			Utilities.buildAdjacencyListForEachVariableInFunction(functionNodesArray[j], networkNodesArray, evidenceVariables);
		}
		
		/*for(int i = 0; i < networkNodesArray.length; i++)
		{
			if(!Utilities.isVariablePresentInEvidenceVariables(networkNodesArray[i], evidenceVariables))
			{
				HashSet<Integer> adjacencySet = new HashSet<Integer>();
				for(int j = 0; j < functionNodesArray.length; j++)
				{
					if(Utilities.isVariablePresentInFunction(networkNodesArray[i].nodeValue, functionNodesArray[j].nodesValuesArray))
					{
						Utilities.addVariablesIntoAdjacencySet(adjacencySet, functionNodesArray[j].nodesValuesArray);
					}
				}
				adjacencySet.remove(networkNodesArray[i].nodeValue);
				networkNodesArray[i].adjacentNodes = adjacencySet;
			}
		}*/
	}

	/**
	 * This method returns the summation of a factor over a given range of variables
	 * @param node
	 * @param variableToBeSummed
	 * @param networkNodesArray
	 * @return
	 */
	private static FunctionNode factorSum(FunctionNode node, int variableToBeSummed, NetworkNode[] networkNodesArray) {
		int j = 0;
		int noOfValuesInFunction = node.functionValues.length/ networkNodesArray[variableToBeSummed].domainSize;
		double[] valuesInFunction = new double[noOfValuesInFunction];
		int[] newVariablesArray = Utilities.getNewVariablesArray(variableToBeSummed, node.nodesValuesArray);
		int variableStride = Utilities.getStrideForVariableInFunction(node, variableToBeSummed);
		boolean[] visitedIndexes = new boolean[node.functionValues.length];
		for(int i = 0; i <= node.functionValues.length - 1; i++)
		{
			if(visitedIndexes[i] == false)
			{
				for(int k = 0; k < networkNodesArray[variableToBeSummed].domainSize; k++)
				{
					int stepIncrementer = i + (k * variableStride);
					valuesInFunction[j] = valuesInFunction[j] + node.functionValues[stepIncrementer];
					visitedIndexes[stepIncrementer] = true;
				}
//				System.out.println(j + "   ----->   " +valuesInFunction[j]);
				j++;
			}
		}
		return new FunctionNode(networkNodesArray, newVariablesArray, valuesInFunction);
	}

	/**
	 * This function returns factor product of two functions.
	 * @param node2 
	 * @param node1 
	 * @param networkNodesArray2 
	 * @return valuesinFunction
	 */
	private static FunctionNode factorProductOfTwoFunctions(FunctionNode node1, FunctionNode node2, NetworkNode[] networkNodesArray2) {
		System.out.println("hi");
		int j = 0;
		int k = 0;
		HashSet<Integer> variablesSet = Utilities.getHashSetForVariablesInFunctions(node1, node2);
		int noOfValuesInFunction = getNoOfValuesinFunction(variablesSet, networkNodesArray2);
		double[] valuesInFunction = new double[noOfValuesInFunction];
		int[] variablesArray = new int[variablesSet.size()];
		int y = 0;
		int maxElement = 0;
		for(int l : variablesSet)
		{
			if(l > maxElement)
			{
				maxElement = l;
			}
			variablesArray[y] = l;
			y++;
		}
		int[] assignment = new int[maxElement+1];
		int temp = -100;
		for(int i = 0; i <= noOfValuesInFunction - 1; i++)
		{
			valuesInFunction[i] = node1.functionValues[j] * node2.functionValues[k];
//  			System.out.println( node1.functionValues[j] + " * " + node2.functionValues[k] + " ---> " + valuesInFunction[i]);
//  			System.out.println(i + " - " + valuesInFunction[i]);
  			for(int x = variablesArray.length-1; x >= 0; x--)
			{
  				int l = variablesArray[x];
//				System.out.println(i);
				assignment[l] = assignment[l]+1;
				if(assignment[l] == networkNodesArray2[l].domainSize)
				{
					assignment[l] = 0;
					temp = -100;
					if((temp = Utilities.getStrideForVariableInFunction(node1, l)) > 0 && j > 0)
					{
						j = j - (networkNodesArray2[l].domainSize - 1 ) * temp;
					}
					temp = -100;
					if((temp = Utilities.getStrideForVariableInFunction(node2, l)) > 0  && k > 0)
					{
						k = k - (networkNodesArray2[l].domainSize - 1 ) * temp;
					}
				}
				else
				{
					temp = -100;
					if((temp = Utilities.getStrideForVariableInFunction(node1, l)) > 0 )
					{
						j = j + temp;
					}
					temp = -100;
					if((temp = Utilities.getStrideForVariableInFunction(node2, l)) > 0)
					{
						k = k + temp;
					}
					break;
				}
			}
//			System.out.println(i + " - " + j + " - " + k);
		}
		return new FunctionNode(networkNodesArray2, variablesArray, valuesInFunction);
	}

	/**
	 * This function returns the size of the function table calculated for product of two factors
	 * @param variablesSet
	 * @param networkNodesArray2
	 * @return
	 */
	private static int getNoOfValuesinFunction(HashSet<Integer> variablesSet,
			NetworkNode[] networkNodesArray2) {
		int noOfValuesInFunction = 1;
		for(int i : variablesSet)
		{
			if((noOfValuesInFunction * networkNodesArray2[i].domainSize) < 0)
			{
				System.out.println(noOfValuesInFunction + "" + networkNodesArray2[i].domainSize);
			}
			noOfValuesInFunction = noOfValuesInFunction * networkNodesArray2[i].domainSize;
			if(noOfValuesInFunction  < 0)
			{
				System.out.println(noOfValuesInFunction);
			}
		}
		return noOfValuesInFunction;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public void setNetworkNodesArray(NetworkNode[] networkNodesArray) {
		this.networkNodesArray = networkNodesArray;
	}

	public void setFunctionNodesArray(FunctionNode[] functionNodesArray) {
		this.functionNodesArray = functionNodesArray;
	}


	public void setEvidenceVariables(int[] evidenceVariables) {
		this.evidenceVariables = evidenceVariables;
	}

	public void setEvidenceVariablesValues(int[] evidenceVariablesValues) {
		this.evidenceVariablesValues = evidenceVariablesValues;
	}
	
}
