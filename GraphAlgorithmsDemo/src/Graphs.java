/*
 * GRAPHS
 * 
 * connections of nodes
 * very open-ended and can be used to represent any data or ideas
 * involving interconnected things
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */

import java.util.*;
import processing.core.*;

public class Lesson10_Graphs extends PApplet{

	public static void main(String[] args) {
		new Lesson10_Graphs().runSketch();
	}
	
	public void settings() {
		size(800,600);
		
	}
	
	List<Node> nodes = new ArrayList<>();
	Node selected = null;
	Set<Node> blue = new HashSet<>();
	
	Map<Node, Node> purple = new HashMap<>();
	//key: node that we got to in a particular search alg
	//value: node we used to get to the key node - the node before the key
	public void setup() {
		for(int i = 0; i < 5; i++) {
			nodes.add(new Node());
		}
		
		
	}
	
	public void draw() {
		background(0);
		
		for(Node n : nodes) {
			n.displayEdges();
		}
		
		strokeWeight(10);
		stroke(255,0,255);
		for(Node n : purple.keySet()) {
			Node n2 = purple.get(n);
			
			if(n2 != null) {
				line(n.x, n.y, n2.x, n2.y);
			}
		}
		
		for(Node n : nodes) {
			n.display();
		}
	}
	
	public void mousePressed() {
		Node clicked = clicked();
		if(clicked == null) {
			if(mouseButton == LEFT) {
				Node n = new Node(mouseX, mouseY);
				selected = n;
				nodes.add(n);
			}
		}else {
			if(mouseButton == LEFT) {
				if(selected == clicked) {//deselect
					selected = null;
				}else {
					selected = clicked;
				}
			}else {
				if(selected != null && selected != clicked) {
					clicked.toggleNeighbor(selected);
				}
			}
		}
	}
	
	public void mouseDragged() {
		if(selected != null && mouseButton == LEFT) {
			selected.x = mouseX;
			selected.y = mouseY;
		}
	}
	
	public void keyPressed() {
		if(key == 'n') {
			selected = new Node();
			nodes.add(selected);
		}
		if(selected != null) {
			if(key == 'e') {
				for(Node n : nodes) {
					if(!selected.neighbors.contains(n) && Math.random() < 0.3) {
						selected.toggleNeighbor(n);
					}
				}
			}
			if(key == 'd') {
				//depth-first search
				blue.clear();
				dfs(selected);
				println();
			}
			if(key == 'b') {
				//breadth-first search
				blue.clear();
				bfs(selected,1000);
			}
			if(key >= '0' && key<= '9') {
				int degree = key - '0';
				blue.clear();
				bfs(selected, degree);
			}
			if(key == 'k') {
				//Dijkstra
				purple.clear();
				dijkstra(selected);
			}
		}
	}
	
	void dijkstra(Node start) {
		PriorityQueue<Step> pq = new PriorityQueue<>(nodes.size(),
				(a,b)->{if(a.pathLength<b.pathLength) return -1;
				if(a.pathLength>b.pathLength)return +1; return 0;});
		pq.add(new Step(start, 0));
		
		while(pq.size() > 0) {
			Step s = pq.poll();
			
			Node n = s.n;
			if(purple.containsKey(n)) {
				continue;
			}
			
			purple.put(n, s.from);
			
			for(Node n2 : n.neighbors) {
				pq.add(new Step(n2, s));
			}
		}
	}
	
	void bfs(Node start, int degreeLimit) {
		Deque<Step> toTake = new ArrayDeque<>();
		//don't count as visited until out of queue
		toTake.add(new Step(start, 0));
		
		while(toTake.size() > 0) {
			Step s = toTake.poll();
			if(blue.contains(s.n)) {
				continue;
			}//already visited so skip
			if(s.d > degreeLimit) break;//stop because no more lower nodes
			blue.add(s.n);
			println(s.n + " from " + s.from);
			// TODO: add this data to a map, so we can use it later
			//to reconstruct the full path working backwards from any node
			for(Node n2 : s.n.neighbors) {
				toTake.add(new Step(n2, s.d+1, s.n));
			}
		}
	}
	
	//represents a step at the end of a path starting from start ppint of some search
	class Step{
		Node n;
		Node from; //node we come from to get to Node n
		int d; //degree
		
		float pathLength;
		//not edge length - total length from start through multiple other nodes to node n
		
		Step(Node nn, Step prev){
			n = nn;
			from = prev.n;
			pathLength = prev.pathLength + dist(n.x, n.y, from.x, from.y);
		}
		
		Step(Node nn, int dd){
			n = nn;
			d = dd;
		}
		Step(Node nn, int dd, Node ff){
			this(nn,dd);//this calls step constructor
			from = ff;
		}
	}
	
	

	void dfs(Node n) {
		//don't need to check out of bounds
		if(blue.contains(n)) {//if we already have been here
			return;
		}
		blue.add(n);
		print(n.id + ", ");
		for(Node n2 : n.neighbors) {
			dfs(n2);
		}
	}
	
	
	
	Node clicked() {
		for(Node n : nodes) {
			if(dist(n.x, n.y, mouseX, mouseY) <= 25) {
				return n;
			}
		}
		return null;
	}
	//degree of a node = # of edges it's connecting to
	//degree of seperation between 2 nodes = # of edges you need to move over to get from one to the other  
	//adjacency: whether 2 nodes have an edge or not (are they neighbors)
	//adjacency matrix:
       //  0 1 2 3 4    	<-- nodes we are going to with an edge		//0 is connected to 0 1 2 3 4
//      0    T T T
//      1  T       T
//      2  T
//      3  T       T
//      4    T   T
//		^
	//  we are cdoming from using an edge	
// instead of T/F (boolean), you could also use 0/1 (int), or even #s above 1
       
	int nextId = 1;
	
	class Node{
		float x, y;
		int id; 
		
		Set<Node> neighbors = new HashSet<>();
		
		Node(float xx, float yy){
			x = xx;
			y = yy;
			id = nextId++; 
			//++ after uses original var and then increments
			
		}
		Node(){
			this(random(0,width), random(0, height));
		}
		
		void display() {
			if(this == selected) {
				stroke(255,0,0);
				strokeWeight(5);
			}else if(blue.contains(this)){
				strokeWeight(5);
				stroke(0,0,255);
			}else {
				noStroke();
			}
			fill(255);
			ellipse(x,y,50,50);
			fill(0);
			textSize(40);
			textAlign(CENTER, CENTER);
			text(id, x, y);
		}
		
		void displayEdges() {
			stroke(128);
			strokeWeight(10);
			
			for(Node n : neighbors) {
				line(this.x, this.y, n.x, n.y);
			}
		}
		
		void toggleNeighbor(Node n) {
			if(neighbors.contains(n)) {
				this.neighbors.remove(n);
				n.neighbors.remove(this);
			}else {
				this.neighbors.add(n);
				n.neighbors.add(this);
			}
		}
		
		public String toString() {
			return "" + id;
		}
		
	}
	
}
