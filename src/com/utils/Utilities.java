package com.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Utilities {
	
	public static HashSet<Integer> getHashSetForVariablesInFunctions(
			FunctionNode node1, FunctionNode node2) {
		HashSet<Integer> hashSetOfCommonVariables = new HashSet<Integer>();
		for(int i = 0; i <= node1.nodesValuesArray.length-1; i++)
		{
			hashSetOfCommonVariables.add(node1.nodesValuesArray[i]);
		}
		for(int i = 0; i <= node2.nodesValuesArray.length-1; i++)
		{
			hashSetOfCommonVariables.add(node2.nodesValuesArray[i]);
		}
		return hashSetOfCommonVariables;
	}
	
	/**
	 * This method returns true if the nodeElement is present in the function array
	 * @param nodeValue
	 * @param 
	 * @return
	 */
	public static boolean isVariablePresentInFunction(int nodeValue,
			int[] nodesValuesArray) {
		int length = nodesValuesArray.length;
		if(length > 0)
		{
			for(int i = 0; i < length; i++)
			{
				if(nodeValue == nodesValuesArray[i])
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * This method adds the elements in nodesValuesArray to hashset.
	 * @param adjacencySet 
	 * @param nodesValuesArray
	 */
	public static void addVariablesIntoAdjacencySet(HashSet<Integer> adjacencySet, int[] nodesValuesArray) {
		int length = nodesValuesArray.length;
		for(int i = 0; i < length; i++)
		{
			adjacencySet.add(nodesValuesArray[i]);
		}
	}
	
	/**
	 * This method updates the adjacency nodes.
	 * @param node
	 * @param networkNodesArray
	 */
	public static void updateAdjacentNodes(NetworkNode node,
			NetworkNode[] networkNodesArray) {
		for(Integer k: node.adjacentNodes)
		{
			networkNodesArray[k].adjacentNodes.remove(node.nodeValue);
			for(Integer j : node.adjacentNodes)
			{
				if(j.intValue() != k.intValue())
				{
					networkNodesArray[k].adjacentNodes.add(networkNodesArray[j].nodeValue);
				}
			}
		}
	}
	
	/**
	 * This method returns the new array of variables in the function
	 * @param variableToBeSummed
	 * @param nodesValuesArray
	 * @return
	 */
	public static int[] getNewVariablesArray(int variableToBeSummed,
			int[] nodesValuesArray) {
		int[] newNodeValuesArray = new int[nodesValuesArray.length -1];
		for(int i = 0, j = 0; i < nodesValuesArray.length; i++)
		{
			if(nodesValuesArray[i] != variableToBeSummed)
			{
				newNodeValuesArray[j] = nodesValuesArray[i];
				j++;
			}
		}
		return newNodeValuesArray;
	}
	
	
	/**
	 * 
	 * @param nodeVariableStrideMap
	 * @param node
	 * @return 
	 */
	public static HashMap<Integer, Integer> constructVariableStrideMap(FunctionNode node) {
		HashMap<Integer, Integer> variableStrideMap = new HashMap<Integer, Integer>();
		for(int i = 0; i <= node.nodesValuesArray.length - 1; i++)
		{
			variableStrideMap.put(node.nodesValuesArray[i], node.stepsForVariables[i]);
		}
		return variableStrideMap;
	}
	
	/**
	 * This method returns the stride of the variables in a function/factor.
	 * @param node
	 * @param variable
	 * @return
	 */
	public static int getStrideForVariableInFunction(FunctionNode node,
			int variable) {
		for(int i = 0; i < node.nodesValuesArray.length; i++)
		{
			if(node.nodesValuesArray[i] == variable)
			{
				return node.stepsForVariables[i];
			}
		}
		return 0;
	}

	public static LinkedList<FunctionNode> removeElementsFromList(
			LinkedList<FunctionNode> removalElements,
			LinkedList<FunctionNode> temporaryFunctions) {
//		System.out.println("Before - " + temporaryFunctions.size());
		for(FunctionNode removalNode: removalElements)
		{
			temporaryFunctions.remove(removalNode);
		}
//		System.out.println("After - " + temporaryFunctions.size());
		return temporaryFunctions;
	}

	public static int getStrideForPreviousVariableInFunction(
			FunctionNode node, int variable) {
		for(int i = 0; i < node.nodesValuesArray.length; i++)
		{
			if(node.nodesValuesArray[i] == variable)
			{
				if(i == 0)
				{
					return 0; 
				}
				else
				{
					return node.stepsForVariables[i-1];
				}
			}
			
		}
		return 0;
	}

	public static int getDomainSizeOfPreviousVariableInFunction(
			FunctionNode node, int variable, NetworkNode[] networkNodesArray) {
		
		for(int i = 0; i < node.nodesValuesArray.length; i++)
		{
			if(node.nodesValuesArray[i] == variable)
			{
				if(i == 0)
				{
					return 0; 
				}
				else
				{
					return networkNodesArray[node.nodesValuesArray[i-1]].domainSize;
				}
			}
			
		}
		return 0;
	}
	
	/*
	 * This method returns true if variable is present in the evidence variables or else false.
	 * @param networkNode
	 * @param evidenceVariables
	 * @return
	 */
	public static boolean isVariablePresentInEvidenceVariables(
			int variable, int[] evidenceVariables) {
		for(int i = 0; i < evidenceVariables.length; i++)
		{
			if(variable == evidenceVariables[i])
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * This method builds adjacency list for variables in a function.
	 * @param functionNode
	 * @param networkNodesArray
	 * @param evidenceVariables
	 */
	public static void buildAdjacencyListForEachVariableInFunction(
			FunctionNode functionNode, NetworkNode[] networkNodesArray,
			int[] evidenceVariables) {
		int[] nodesValuesArray = functionNode.nodesValuesArray;
		for(int i = 0; i < nodesValuesArray.length; i++)
		{
			if(!isVariablePresentInEvidenceVariables(nodesValuesArray[i], evidenceVariables))
			{
				for(int j = 0; j < nodesValuesArray.length; j++)
				{
					if(i != j && !isVariablePresentInEvidenceVariables(nodesValuesArray[j], evidenceVariables))
					{
						networkNodesArray[nodesValuesArray[i]].adjacentNodes.add(nodesValuesArray[j]);
					}
				}
			}
		}
	}

	public static void printElementsInIntegerArray(int[] bucketsMinDegreeOrder) {
		for(int i = 0; i < bucketsMinDegreeOrder.length; i++)
		{
			System.out.print(bucketsMinDegreeOrder[i] + ", ");
		}
		System.out.println("");
	}
}
