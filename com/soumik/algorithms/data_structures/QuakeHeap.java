package com.soumik.algorithms.data_structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;


/**
 * Implemented methods:
 * 1.  QuakeHeap(int nLevels){}
 * 2.  public void clear(){}
 * 3.  private void merge(){}
 * 4.  public Locator insert(Key x,Value v){}
 * 5.  public Key getMinKey(){}
 * 6.  public int getMaxLevel(Locator l){}
 * 7.  public ArrayList<String> listHeap(){}
 * 8.  public void decreaseKey(Locator l,Key newKey) throws Exception{}
 * 9.  public void setQuakeRatio(double newRatio) throws Exception{}
 * 10. public Value extractMin() throws Exception(){}
 * 11. public int size(){}
 * 12. public void setNLevels(int n1) throws Exception{}
 *  
 **/ 


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
				else if(this.left==null && this.right!=null){
					ret.add("[null]");
				}
				if(this.right!=null){
					ret.addAll(this.right.listHeapHelper());
				}
				else if(this.right==null && this.left!=null){
					ret.add("[null]");
				}
			}
			if (this.level == 0) {
				ret.add("[" + this.K + " " + this.V + "]");
			}
			return ret;
			
		}

		@Override
		public String toString(){
			return this.K+" "+this.V;
		}

		@Override
		public int compareTo(QuakeHeap<Key, Value>.Node n) {
			return this.K.compareTo(n.K);
		}


	}

	// Instance Variables Needed for Quake Heap Implementation
	LinkedList<Node>[] roots; //stores references to roots on level of index
	private int[] nodeCt;		//stores # nodes on level of index
	private double quakeRatio;  //stores the quakeRatio (default value 0.75)
	private int size;			//maintains a count of number of leaf nodes added.
	private boolean isEmpty;

	/*Locator contains a reference to created Nodes, because Nodes are protected objects and
	 *can't be accessed directly*/
	public class Locator {
		Node u;
		Locator(Node u) {this.u = u; }
		Node get() { return u; }
	}
	
	// Initializes the nodeCt & roots array sizes to number of levels given and 
	// initializes linked lists in every level.
	// Method 1
	public QuakeHeap(int nLevels) { 
		nodeCt = new int[nLevels];
		roots = new LinkedList[nLevels];
		quakeRatio = 0.75;
		for (int i = 0; i < nLevels; i++) { //iffy
			roots[i] = new LinkedList<Node>();
		}
		isEmpty = true;

	}
	
	// Resets nodeCt array to 0s and clears every linked list in roots array.
	// Method 2
	public void clear() {
		for (int i = 0; i < nodeCt.length; i++) {
			nodeCt[i] = 0;
			roots[i].clear();
		}
		isEmpty = true;
		size = 0;
	}

	// Inserts Key-Value pair on bottom level, increases node count at level 0 and 
	//returns locator for node
	// Method 3
	public Locator insert(Key x, Value v) {
		isEmpty = false;
		Node n = new Node(x,v,0,null,null,null);
		roots[0].addFirst(n);;
		nodeCt[0]++;
		size++;
		return new Locator(n);
	}
	
	/* Does MergeTree operations from Doc and then returns the minimum key in structure */
	// Method 4
	public Key getMinKey() throws Exception { 
		merge();
		Node minNode = findMinNode();
		return minNode.K;
	}
	
	/* Gets the highest level with a node present */
	// Method 5
	public int getMaxLevel(Locator r) { 
		Node u = r.get();
		int res = u.level;
		while(u.parent != null && u.parent.K.compareTo(u.K) == 0) {
			res++;
			u = u.parent;
		}
		return res;
	}
	
	/* Returns an arrayList with each item in structure represented in string form */
	// Method 6
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

	/** Decreases the key value of a particular existing key in the tree.
	 * Method 7
	 **/ 

	public void decreaseKey(Locator l,Key newKey) throws Exception{
		Node u = l.get();
		if(u.K.compareTo(newKey)<0){
			throw new Exception("Invalid key for decrease-key");
		}
		Node uChild = null;
		do{
			u.K = newKey;
			uChild = u;
			u = u.parent;
		}while(u!=null && uChild.compareTo(u.left)==0);
		if(u!=null){
			cut(u);
		}
	}

	/** Extracts the Minimum Key.
	 * Method which should have been required but is apparantly not important in this assignment
	 **/ 

	public Key extractMinKey()throws Exception{
		if(isEmpty || size==0){
			throw new Exception("Empty Heap");
		}
		Node u = findMinNode();
		Key result = u.K;
		deleteLeftPath(u);
		roots[u.level].remove(u);
		merge();
		quake();
		size--;
		return result;
	}

	/** Extracts the Value corresponding to the minimum key.
	 * Method 8
	 **/

	public Value extractMin() throws Exception{
		if(isEmpty||size==0){
			throw new Exception("Empty Heap");
		}
		Node u = findMinNode();
		Node temp = u;
		while(temp.left!=null){
			temp = temp.left;
		}
		Value result = temp.V;
		if(size==1){
			clear();
			return result;
		}
		else{
			deleteLeftPath(u);
			roots[u.level].remove(u);
			merge();
			quake();
			size--;
			return result;
		}
	}

	/**
	 * Quakes the entire datastructure.
	 * Method 9.
	 **/ 

	public void setQuakeRatio(double newRatio)throws Exception{
		if(newRatio < 0.5 || newRatio > 1){
			throw new Exception("Quake ratio is outside valid bounds");
		}
		this.quakeRatio = newRatio;
	}

	/** 
	 * Returns the size of the datastructure (number of leaf nodes)
	 * Method
	 **/ 

	public int size(){
		return this.size;
	}

	/**
	 * Sets Max level of the datastructure.
	 * Does not work currently.
	 * Method 11.
	 **/

	public void setNLevels(int n1)throws Exception{
		if(n1 < 1){
			throw new Exception("Attempt to set an invalid number of levels");
		}
		if(n1==nodeCt.length){
			return;
		}
		if(n1 < nodeCt.length){
			clearAllAboveLevel(n1-1);
		}
		int[] newNodeCt = new int[n1];
		LinkedList<Node>[] newRoots = new LinkedList[n1];
		for(int i=0;i<n1;i++){
			newRoots[i]=new LinkedList<Node>();
		}
		for(int i=0;i<n1;i++){
			newNodeCt[i]=nodeCt[i];
			for(Node n:roots[i]){
				newRoots[i].add(n);
			}
		}
		nodeCt = null;
		roots = null;
		nodeCt = newNodeCt;
		roots = newRoots;
	}

	/**	
	 * Utility methods
	 **/

	private void makeRoot(Node u){
		u.parent = null;
		roots[u.level].add(u);
	}

	private Node trivialTree(Key k,Value v){
		Node u = new Node(k,v,0,null,null,null);
		nodeCt[0]++;
		makeRoot(u);
		return u;
	}

	private Node link(Node u,Node v)throws Exception{
		if(u.level!=v.level){
			throw new Exception("Levels of given nodes are not same");
		}
		int lev = u.level+1;
		Node w = null;
		if(u.K.compareTo(v.K)<0){
			w = new Node(u.K,null,lev,u,v,null);
		}
		else{
			w = new Node(v.K,null,lev,v,u,null);
		}
		nodeCt[lev]++;
		u.parent = w;
		v.parent = w;
		return w;
	}

	private void cut(Node w){
		Node v = w.right;
		if(v!=null){
			w.right = null;
			makeRoot(v);
		}
	}

	/**
	 * Merges the smaller trees in the heap
	 * Method 12
	 **/

	private void merge() throws Exception {
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
				Node w = link(u,v);
				makeRoot(w);				
			}
		}
    }

    private Node findMinNode(){
    	Node small = null;
		for (LinkedList<Node> list: roots) {
			if (!list.isEmpty()) {
				Node t = list.element();
				if (small == null || t.K.compareTo(small.K) < 0) {
					small = t;
				}
			}
		}
		return small;
    }

    private void deleteLeftPath(Node u){
		while(u!=null){
			cut(u);
			nodeCt[u.level]--;
			u = u.left;
		}
	}

	private void quake(){
		for(int lev = 0;lev<nodeCt.length-1;lev++){
			if(nodeCt[lev+1] > quakeRatio*nodeCt[lev]){
				clearAllAboveLevel(lev);
			}
		}
	}

	private void clearAllAboveLevel(int level){
		if(level==1){
			for(int lev = nodeCt.length-1;lev>=0;lev--){
				for(Node n:roots[lev]){
					if(n.level > level){
						removeNodeRecursive(n,level);
					}
				}
			}
			for(int lev = nodeCt.length-1;lev>level;lev--){
				nodeCt[lev]=0;
				roots[lev].clear();
			}
		}
		else{
			for(int lev = nodeCt.length-1;lev>=0;lev--){
				for(Node n:roots[lev]){
					if(n.level > level){
						removeNodeRecursive(n,level);
					}
				}
			}
			for(int lev = nodeCt.length-1;lev>level;lev--){
				nodeCt[lev]=0;
				roots[lev].clear();
			}
		}
	}

	private void removeNodeRecursive(Node n,int level){
		if(n==null){
			return;
		}
		if(n.level > level){
			Node left = n.left;
			Node right = n.right;
			removeNodeRecursive(left,level);
			removeNodeRecursive(right,level);
		}
		else if(n.level == level){
			makeRoot(n);
		}
		else{
			return;
		}
	}
}