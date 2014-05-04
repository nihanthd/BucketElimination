package com.utils;

import java.util.HashSet;


public class NetworkNode{
	public int nodeValue;
	public int domainSize;
	public int domainValues[];
	public HashSet<Integer> adjacentNodes;
	public int[] functionsArray;
	
	
	public NetworkNode(int i, String domainSize) {
		this.nodeValue = i;
		this.domainSize = Integer.parseInt(domainSize);
		this.domainValues = new int[this.domainSize];
		this.adjacentNodes = new HashSet<Integer>();
		for(int j = 0; j < this.domainSize; j++)
		{
			domainValues[j] = j;
		}
	}
}
