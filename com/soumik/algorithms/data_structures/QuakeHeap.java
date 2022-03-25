package com.soumik.algorithms.data_structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;


public class QuakeHeap<Key extends Comparable<Key>, Value> {

	public class Node implements Comparable<Node> {
		Node left,right,parent;
		Integer level;
		Key K;
		Value V;
		public Node(Key K,Value V,Integer lev,Node left,Node rig, Node par) {
			this.K = K;
			this.V = V;
			this.level = lev;
			this.left = left;
			this.right = rig;
			this.parent = par;
		}

		
		public ArrayList<String> listHeapHelper() {
			ArrayList<String> ret = new ArrayList<String>();
			if (this.level > 0) {
				ret.add("(" + this.K + ")");
				
				if (this.left != null) {
					ret.addAll(this.left.listHeapHelper());
				}
				if (this.right != null) {
					ret.addAll(this.right.listHeapHelper());
				}
			}
			if (this.level == 0) {
				ret.add("[" + this.K + " " + this.V + "]");
			}
			return ret;
			
		}

		@Override
		public int compareTo(QuakeHeap<Key, Value>.Node n) {
			return this.K.compareTo(n.K);
		}

		@Override
		public String toString(){
			return this.K+" "+this.V;
		} 


	}

	// Instance Variables Needed for Quake Heap Implementation
	LinkedList<Node>[] roots; //stores references to roots on level of index
	private int[] nodeCt;		//stores # nodes on level of index


	/*Locator contains a reference to created Nodes, because Nodes are protected objects and
	 *can't be accessed directly*/
	public class Locator {
		Node u;
		Locator(Node u) {this.u = u; }
		Node get() { return u; }
	}
	
	// Initializes the nodeCt & roots array sizes to number of levels given and 
	// initializes linked lists in every level.
	public QuakeHeap(int nLevels) { 
		nodeCt = new int[nLevels];
		roots = new LinkedList[nLevels];
		for (int i = 0; i < nLevels; i++) { //iffy
			roots[i] = new LinkedList<Node>();
		}

	}
	
	// REsets nodeCt array to 0s and clears every linked list in roots array.
	public void clear() {
		for (int i = 0; i < nodeCt.length; i++) {
			nodeCt[i] = 0;
			roots[i].clear();
		}
	}

	// Inserts Key-Value pair on bottom level, increases node count at level 0 and 
	//returns locator for node
	public Locator insert(Key x, Value v) {
		Node n = new Node(x,v,0,null,null,null);
		roots[0].addFirst(n);;
		nodeCt[0]++;

		return new Locator(n);
	}
	
	/* Does MergeTree operations from Doc and then returns the minimum key in structure */
	public Key getMinKey() throws Exception { 
		if (nodeCt[0] == 0) {
			throw new Exception("Empty heap"); //throw exception if heap empty
		}
		
		for (int m = 0; m < nodeCt.length-1; m++) {
			if (nodeCt[m] == 0) {
				continue;
			}
			Collections.sort(roots[m]);
			while (roots[m].size() >= 2) {
				Node u = roots[m].pop();
				Node v = roots[m].pop();
				Node w = new Node(u.K,null,m+1,u,v,null);
				u.parent = w;
				v.parent = w;
				roots[m+1].add(w);
				nodeCt[m+1]++;
			}

		}
		Node small = null;
		for (LinkedList<Node> list: roots) {
			if (!list.isEmpty()) {
				Node t = list.element();
				if (small == null || t.K.compareTo(small.K) < 0) {
					small = t;
				}
			}
		}
		return small.K;
	}
	
	/* Gets the highest level with a node present */
	public int getMaxLevel(Locator r) { 
		Node n = r.u;
		Key comp = n.K;
		int lev = 0;
		while (n != null) {
			n = n.parent;

			if (n != null && comp == n.K) {
				lev++;
			}
			if ( n!= null && comp != n.K) {
				break;
			}
		}
		return lev;
	}
	
	/* Returns an arrayList with each item in structure represented in string form */
	public ArrayList<String> listHeap() { 
		ArrayList<String> ret = new ArrayList<String>();

		for (int m = 0; m < nodeCt.length; m++) {
			if (nodeCt[m] == 0) {
				continue;
			}
			Collections.sort(roots[m]);
			String header = "{lev: " +m+" nodeCt: " +nodeCt[m]+"}";
			ret.add(header);
			for (Node n: roots[m]) {
				if (n == null) {
					ret.add("[null]");
				}
				ret.addAll(n.listHeapHelper()); // Calls recursive helper in Node class for n >=0
			}
		}
		return ret;
	}
}