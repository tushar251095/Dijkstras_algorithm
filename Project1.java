/* 
Name : Tushar Vijay Nemade
student id: 801257370
*/
import java.io.File;
import java.util.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.NoSuchElementException;

class GraphException extends RuntimeException {
	public GraphException(String name) {
		super(name);
	}
}

// class for vertex parameters
class vertexParam {
	public String name; // vertex name
	public List<edgeParam> adjacentVertexList; // list that stores adjacent verticies
	public String vertexStatus; // maintain status of vertex
	public Double weight; // weight
	public vertexParam previousVertex; // predecessor vertex
	public String visited;

	// Constructor for intail initailization
	public vertexParam(String nameVertex) {
		name = nameVertex;
		adjacentVertexList = new LinkedList<edgeParam>();
		vertexStatus = "up";
		visited = "white";
		defaultInit();
	}

	// set default weight to infinity and previous vertex as NULL
	public void defaultInit() {
		weight = (double) Project1.INFINITY;
		previousVertex = null;
		visited = "white";
	}
}

// class for edge parameters
class edgeParam {
	public vertexParam Destination; // destination vertex
	public Double weight; // edge weight
	public String edgeStatus; // maintain ststus of edge

	public edgeParam(vertexParam dest, double edgeWeight) {
		Destination = dest;
		weight = edgeWeight;
		edgeStatus = "up";
	}
}

public class Project1 {

	public static final int INFINITY = Integer.MAX_VALUE;
	private Map<String, vertexParam> vertexMap = new HashMap<String, vertexParam>(); // Hashmap to maintain a tree
																						// vertices
	private TreeSet<String> reachableVerticesMap = new TreeSet<String>();

	// Create vertex and add in hashmap
	private vertexParam createVertex(String vertexName) {
		// check if vertex is already in hashmap
		// fetch vertex from hashmap
		vertexParam vertexV = vertexMap.get(vertexName);
		// check if it exist if null create new vertex
		if (vertexV == null) {
			// creating new vertex
			vertexV = new vertexParam(vertexName);
			vertexMap.put(vertexName, vertexV);
		}
		return vertexV;
	}

	// Add edge using destination and source vertex
	public void createEdge(String sourceVertex, String destVertex, double weight) {
		vertexParam startVertex = createVertex(sourceVertex); // create source vertex
		vertexParam endVertex = createVertex(destVertex); // create destination vertex
		int incrementor = 0;
		int flag = 0;
		for (edgeParam vertexItr : startVertex.adjacentVertexList) {
			edgeParam newEdge = vertexItr;
			// check if destination vertex of current edge exist
			if (endVertex.name.equals(newEdge.Destination.name)) {
				startVertex.adjacentVertexList.get(incrementor).weight = weight;
				flag = 1;
			}
			incrementor++;
		}
		if (flag == 0) {
			startVertex.adjacentVertexList.add(new edgeParam(endVertex, weight));
		}
	}

	// print Basic weighted directed graph if vertex is down print DOWN in front of
	// vertex similar for edge down
	public void printGraph() {
		SortedMap<String, vertexParam> printSortedMap = new TreeMap<>(); // Treemap to store keys alphabetically
		for (String vertexitr : vertexMap.keySet()) {
			printSortedMap.put(vertexitr, vertexMap.get(vertexitr));
		}
		for (Map.Entry<String, vertexParam> vertexList : printSortedMap.entrySet()) {
			System.out.print(vertexList.getKey()); // printing Vertex name
			if (vertexMap.get(vertexList.getKey()).vertexStatus != "up") { // check for vertex status
				System.out.print("  DOWN");

			}
			System.out.println();
			SortedMap<String, Double> innerSortedList = new TreeMap<>(); // Treemap for adjacent vertex to sort
																			// alphabetically
			for (edgeParam k : vertexList.getValue().adjacentVertexList) {
				innerSortedList.put(k.Destination.name, k.weight); // adding values to treemap
			}
			for (Map.Entry<String, Double> adjList : innerSortedList.entrySet()) {
				System.out.print("  " + adjList.getKey() + " " + adjList.getValue());
				if (checkEdgeStatus(vertexList.getKey(), adjList.getKey())) { // check edge status
					System.out.print(" DOWN");
				}
				System.out.println();
			}
		}
	}

	// Set default values for each vertex in vertex map.
	private void clearAll() {
		for (vertexParam v : vertexMap.values())
			v.defaultInit();
	}

	// Djkstras algorithm using inbuilt priority queue where priority is based on
	// weight of edge
	public void implementDjkstras(String sourceVertex) {
		try {
			clearAll(); // to set default graph
			vertexParam startVertex = vertexMap.get(sourceVertex);
			if (startVertex == null) { // check if vertex is null
				System.out.println("Enter Valid Vertex");
				return;
			}
			if (startVertex.vertexStatus == "down") { // check if given vertex is down
				System.out.println("Vertex Down");
				return;
			}
			startVertex.previousVertex = null; // set previous vertex of source vertex as null
			startVertex.weight = 0.0; // set dist of starte vertex to 0

			// priority queue to implement djkstras, remove vertex with minimum weight
			// adjacent vertices are enqueue in the queue and depending on weight vertices
			// are removed and then visited. it is Minimun weight priority queue that i.e
			// vertex
			// with minimum weight is of high priority.
			PriorityQueue<vertexParam> vertexList = new PriorityQueue<>(new sort());
			vertexList.add(startVertex);
			while (vertexList.size() != 0) {
				vertexParam v = vertexList.remove();
				for (edgeParam vertexitr : v.adjacentVertexList) {
					if (vertexitr.Destination.vertexStatus == "down") { // check if destination vertex is down
						continue;
					}
					if (checkEdgeStatus(v.name, vertexitr.Destination.name)) { // check if edge is down
						continue;
					}
					if (vertexitr.weight < 0) { // check for negative edge
						throw new GraphException("Negative edge found");
					}
					// check the weight for shortest path and update the weight if it is minimum the
					// previous after adding to current weight
					if (vertexitr.Destination.weight > (v.weight + vertexitr.weight)) {
						vertexitr.Destination.weight = v.weight + vertexitr.weight;
						vertexitr.Destination.previousVertex = v;
						vertexList.add(vertexitr.Destination);
					}
				}
			}

		} catch (Exception e) {
			System.err.println(e);
		}
	}

	// print minimum path generated by djkstras
	private void printPath(vertexParam dest) {
		if (dest.previousVertex != null) {
			printPath(dest.previousVertex);
			System.out.print(" to ");
		}
		System.out.print(dest.name);
	}

	public void printPath(String destName) {
		vertexParam w = vertexMap.get(destName);
		if (w == null)
			throw new NoSuchElementException("Destination vertex not found");
		else if (w.weight == INFINITY)
			System.out.println(destName + " is unreachable");
		else {
			printPath(w);
			System.out.printf("  %.2f", w.weight);
			System.out.println();
		}
	}

	// method to enter quries
	public static boolean processRequest(Scanner in, Project1 p) {
		try {
			String query = in.nextLine();
			String[] choice = query.split(" ");
			switch (choice[0]) {
			case "print":
				p.printGraph();
				break;
			case "vertexup":
				p.changeVertexStatus(choice[1], "up");
				break;
			case "vertexdown":
				p.changeVertexStatus(choice[1], "down");
				break;
			case "path":
				p.implementDjkstras(choice[1]);
				p.printPath(choice[2]);
				break;
			case "edgedown":
				p.changeEdgeStatus(choice[1], choice[2], "edgedown");
				break;
			case "edgeup":
				p.changeEdgeStatus(choice[1], choice[2], "edgeup");
				break;
			case "addedge":
				p.createEdge(choice[1], choice[2], Double.parseDouble(choice[3]));
				break;
			case "deleteedge":
				p.deleteEdge(choice[1], choice[2]);
				break;
			case "reachable":
				p.printReachable();
				break;
			default:
				System.out.println("Enter vaild query");
				break;
			}

		} catch (NoSuchElementException e) {
			System.out.println("invalid query");
			return false;
		} catch (GraphException err1) {
			System.err.println(err1);
		} catch (ArrayIndexOutOfBoundsException err) {
			System.out.println("invalid query");
		}
		return true;
	}

	// Change vertex status to up or down
	public void changeVertexStatus(String vertex, String status) {
		try {
			vertexParam vertexV = vertexMap.get(vertex);
			vertexV.vertexStatus = status;
		} catch (Exception e) {
			System.err.println(e);
			System.out.println("Enter Valid Vertex");
		}

	}

	// change status of edge
	public void changeEdgeStatus(String sourceVertex, String destVertex, String status) {
		try {
			vertexParam startVertex = vertexMap.get(sourceVertex); // get source vertex from vertexmap
			vertexParam endVertex = vertexMap.get(destVertex); // get destination vertex from vertex map
			if (startVertex == null || endVertex == null) { // check if vertices are null
				System.out.println("Enter vaild Vertex");
				return;
			}
			for (edgeParam itr : startVertex.adjacentVertexList) { // iterate over the adjancentlist of source vertex
				if (itr.Destination.name.equals(destVertex)) { // if adj vertex is equal to dest vertex change status
					if (status == "edgedown") {
						itr.edgeStatus = "down";
					} else if (status == "edgeup") {
						itr.edgeStatus = "up";
					}

				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	// check if edge is up or down
	public Boolean checkEdgeStatus(String sourceVertex, String destVertex) {
		vertexParam startVertex = vertexMap.get(sourceVertex);
		int flag = 0;
		for (edgeParam itr : startVertex.adjacentVertexList) {
			if (itr.Destination.name.equals(destVertex)) {
				if (itr.edgeStatus == "down") { // if edge status of destination vertex is down change flag value to 1
					flag = 1;
				} else {
					flag = 0;
				}
			}
		}
		// if egde down return true or else return false
		if (flag == 1) {
			return true;
		} else {
			return false;
		}
	}

	// delete edge of the graph. remove the edge from vertexmap
	public void deleteEdge(String sourceVertex, String destVertex) {
		try {
			vertexParam vertexV = vertexMap.get(sourceVertex); // get source vertex
			vertexParam endvertexV = vertexMap.get(destVertex); // get destination vertex
			if (vertexV == null || endvertexV == null) { // check if either vertex is null
				System.out.println("Enter vaild Vertex");
				return;
			}
			// iterate over adjacent vertex of source vertex
			for (edgeParam vertexitr : vertexV.adjacentVertexList) {
				if (vertexitr.Destination.name.equals(destVertex)) { // check condition foradjacent vertex equal
																		// destination vertex
					vertexV.adjacentVertexList.remove(vertexitr); // remove vertex from adjacent list of source
					break;
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		}

	}

	// check reachable vertices from given vertex, call it recursively to check on
	// each adjacent vertex path
	//As every edge and every vertex is vistied by function its complexity is O(V + E)
	//Logic for reachableVerticies is similar to DFS algortitm
	public void reachableVerticeis(vertexParam V) {
		for (edgeParam j : V.adjacentVertexList) { // iterate over adjacent vertex list
			if (j.Destination.vertexStatus == "down") { // ignore if vertex is down
				continue;
			}
			if (checkEdgeStatus(V.name, j.Destination.name)) { // ignore if edge down
				continue;
			}
			if (j.Destination.visited == "white") { // by defauly unvisited vertex are white
				reachableVerticesMap.add(j.Destination.name); // add adjacent vertex to reachable map
				j.Destination.visited = "black"; // after entering into map vertex is visited so change it value to
													// black
				reachableVerticeis(j.Destination); //call recurively for its adjacent vertices
			}
		}
	}

	//printing all reachable vertices from each vertex of graph
	//used maptree and treeset for sorting it alphabetically
	//here for every vertex reachable is called so complexity is O(V*(V+E))
	//so final time complexity of reachable code is O(V * (V + E))
	public void printReachable() {
		SortedMap<String, vertexParam> printSortedMap = new TreeMap<>();
		for (String vertexitr : vertexMap.keySet()) {
			printSortedMap.put(vertexitr, vertexMap.get(vertexitr));
		}
		for (Map.Entry<String, vertexParam> vertexList : printSortedMap.entrySet()) {
			clearAll();
			if (vertexMap.get(vertexList.getKey()).vertexStatus == "up") {
				System.out.println(vertexList.getKey());
				reachableVerticeis(vertexMap.get(vertexList.getKey()));

				//remove self vertex from map
				reachableVerticesMap.remove(vertexList.getKey());

				Iterator<String> itr = reachableVerticesMap.iterator();
				while (itr.hasNext()) {
					System.out.println("  " + itr.next());
				}
				reachableVerticesMap.clear();
			}

		}
	}

	public static void main(String[] args) {
		Project1 p = new Project1();
		try {
			File file = new File(args[0]);
			Scanner inputfile = new Scanner(file);
			String linetext;
			while (inputfile.hasNextLine()) {
				linetext = inputfile.nextLine();
				StringTokenizer tokenindex = new StringTokenizer(linetext);
				if (tokenindex.countTokens() != 3) {
					System.out.println(
							"Each line should have source,destination,weight. Given " + linetext + "Skipping line");
					continue;
				}
				String sourceVetex = tokenindex.nextToken();
				String destVertex = tokenindex.nextToken();
				double weight = Double.parseDouble(tokenindex.nextToken());
				p.createEdge(sourceVetex, destVertex, weight);
				p.createEdge(destVertex, sourceVetex, weight);
			}

			Scanner in = new Scanner(System.in);
			while (processRequest(in, p))
				;

		} catch (IOException e) {
			System.err.println(e);
		}

	}
}

class sort implements Comparator<vertexParam> {
	@Override
	public int compare(vertexParam a, vertexParam b) {
		return a.weight.compareTo(b.weight);
	}
}
