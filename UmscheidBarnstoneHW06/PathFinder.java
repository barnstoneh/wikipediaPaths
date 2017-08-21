import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;


//Authors: Valerie Umscheid and Hannah Barnstone

public class PathFinder {
    //Graph loaded from file
    private MysteryUnweightedGraphImplementation graph;
    
    //Map of vertex name to vertex int
    private HashMap<String, Integer> mapNameToInt;
    
    //Map of vertex int to vertex name
    private HashMap<Integer, String> mapIntToName;
    
    
    /**
     * Constructs a PathFinder that represents the graph with nodes (vertices) specified as in
     * nodeFile and edges specified as in edgeFile.
     * @param nodeFile name of the file with the node names
     * @param edgeFile name of the file with the edge names
     */
    public PathFinder(String nodeFile, String edgeFile) {
        graph = new MysteryUnweightedGraphImplementation();
        mapNameToInt = new HashMap<String, Integer>();
        mapIntToName = new HashMap<Integer, String>();
        
        this.load(nodeFile, edgeFile);
    }
        
    /* This method loads the files input by the user. Adds each
    * vertex to the graph, as well as to the dictionaries that assign
    * key-value pairs for the int and the string representation of the
    * vertex. 
    */
    public boolean load(String vertexFile, String edgeFile) {   
        Scanner scannerNode = null;
        Scanner scannerEdge = null;
        String vertex = "";
        
        try {
            scannerNode = new Scanner(new File(vertexFile));
            scannerEdge = new Scanner(new File(edgeFile));
            
            while (scannerNode.hasNextLine()) {
                //Makes sure that the verticies being stored are actually verticies,
                //not comments or empty lines
                String lineNode = scannerNode.nextLine();
            
                if (lineNode.length() > 0) {
                    char firstCharacterVertex = lineNode.charAt(0);
                    if (firstCharacterVertex != '#') {
                        vertex = lineNode;
                        vertex = java.net.URLDecoder.decode(vertex, "UTF-8");
                        int vertexInt = graph.addVertex();
                        mapNameToInt.put(vertex, vertexInt);
                        mapIntToName.put(vertexInt, vertex);
                    }
                }
            }
            while (scannerEdge.hasNextLine()) {
                //Makes sure that the verticies and edges being stored are actually 
                //verticies and edges, not comments or empty lines
                String lineEdge = scannerEdge.nextLine();
                
                if (lineEdge.length() > 0) {
                    char firstCharacterEdge = lineEdge.charAt(0);
                    if (firstCharacterEdge != '#') {
                        String[] edgeComponents = lineEdge.split("\t");
                        edgeComponents[0] = java.net.URLDecoder.decode(edgeComponents[0], "UTF-8");
                        edgeComponents[1] = java.net.URLDecoder.decode(edgeComponents[1], "UTF-8");
                        int vertexOneInt = mapNameToInt.get(edgeComponents[0]);
                        int vertexTwoInt = mapNameToInt.get(edgeComponents[1]);
                        graph.addEdge(vertexOneInt, vertexTwoInt);
                    }
                }
            }  
        } catch(FileNotFoundException e) {
            System.out.println("File not found exception.");
            return false;
        } catch(InputMismatchException e) {
            System.out.println("Input mismatch exception.");
            return false;
        } catch(UnsupportedEncodingException e) {
            System.out.println("Unsupported encoding exception.");
            return false;
        }
        return true;
    }
    

    /**
     * Returns the length of the shortest path from node1 to node2. If no path exists,
     * returns -1. If the two nodes are the same, the path length is 0.
     * @param node1 name of the starting article node
     * @param node2 name of the ending article node
     * @return length of shortest path
     */
    public int getShortestPathLength(String node1, String node2) {
        List<String> path = new ArrayList<String>();
        path = getShortestPath(node1, node2);
        if (node1.equals(node2)) {
            int pathLength = 0;
            return pathLength;
        }
        else if (path.isEmpty()) {
            int pathLength = -1;
            return pathLength;
        }
        else {
            int pathLength = path.size() - 1;
            return pathLength;
        }
    }
    
    
    /**
     * Returns a shortest path from node1 to node2, represented as list that has node1 at
     * position 0, node2 in the final position, and the names of each node on the path
     * (in order) in between. If the two nodes are the same, then the "path" is just a
     * single node. If no path exists, returns an empty list.
     * @param node1 name of the starting article node
     * @param node2 name of the ending article node
     * @return list of the names of nodes on the shortest path
     */
    public List<String> getShortestPath(String node1, String node2) {

        List<String> path = new ArrayList<String>();
        Queue<String> vertexQueue = new ArrayDeque<String>();
        HashSet<String> hasBeenVisited = new HashSet<String>();
        HashMap<String, String> predecessor = new HashMap<String, String>();
        
        if (node1.equals(node2)) {
            path.add(node1);
            return path;
        }
        
        boolean done = false;
        hasBeenVisited.add(node1);
        vertexQueue.add(node1);
       
        //Uses breadth first search to find the shortest path
        while (!vertexQueue.isEmpty() && !done) {
            String frontVertex = vertexQueue.poll();
            int frontVertexInt = mapNameToInt.get(frontVertex);
            
            Iterable<Integer> neighbors = graph.getNeighbors(frontVertexInt);
        
            for (int neighbor : neighbors) {
                String stringNeighbor = mapIntToName.get(neighbor);
                if (!hasBeenVisited.contains(stringNeighbor)) {
                    hasBeenVisited.add(stringNeighbor);
                    predecessor.put(stringNeighbor, frontVertex);
                    vertexQueue.add(stringNeighbor);
                }
                if (stringNeighbor == node1) {
                    done = true;
                }
            }
        }
        
        //This checks to make sure that the path exists
        if (!predecessor.containsKey(node2)) {
            return path;
        } else {
            String vertex = node2;
            while (predecessor.containsKey(vertex)) {
                vertex = predecessor.get(vertex);
                path.add(vertex);
            }
            //Reverses the path so that it isn't backwards
            Collections.reverse(path);
            path.add(node2);
            return path;
        }
    }
      

    /**
     * Returns a shortest path from node1 to node2 that includes the node intermediateNode.
     * This may not be the absolute shortest path between node1 and node2, but should be 
     * a shortest path given the constraint that intermediateNodeAppears in the path. If all
     * three nodes are the same, the "path" is just a single node.  If no path exists, returns
     * an empty list.
     * @param node1 name of the starting article node
     * @param node2 name of the ending article node
     * @return list that has node1 at position 0, node2 in the final position,  
     * and the names of each node on the path (in order) in between. 
     */
    public List<String> getShortestPath(String node1, String intermediateNode, String node2) {
        List<String> firstPath = getShortestPath(node1, intermediateNode);
        List<String> lastPath = getShortestPath(intermediateNode, node2);
        int fullPathLength = getShortestPathLength(node1, intermediateNode) + getShortestPathLength(intermediateNode, node2);
        
        List<String> finalPath = new ArrayList<String>();
        
        for (int i = 0; i < firstPath.size(); i++) {
            finalPath.add(firstPath.get(i));
        }
        for (int i = 1; i < lastPath.size(); i++) {
            finalPath.add(lastPath.get(i));
        }
        
        if ((firstPath.size() == 0) || (lastPath.size() == 0)) {
            finalPath = new ArrayList<String>();
        }
        
        int pathLength = getShortestPathLength(node1, intermediateNode) +  getShortestPathLength(intermediateNode, node2);
        
        return finalPath;
    }
    
    
    /* This method uses the class Random to choose random nodes
    * for the graph to traverse between.
    */
    public String chooseRandomNode(String vertexFile) {
        List <String> verticies = new ArrayList<String>();
        for (String key : mapNameToInt.keySet()) {
            verticies.add(key);
        }
        Random r = new Random();
        int numVerticies = verticies.size();
        String randomNode = verticies.get(r.nextInt(numVerticies));
        return randomNode;
    }    

    
    
    /** This method will print out the path (if it exists) between
    * the random nodes that are selected. 
    */
    public static void main(String[] args) {
        List<String> path = new ArrayList<String>();
        String vertexFile = args[0];
        String edgeFile = args[1];
        
        PathFinder ourPathFinder = new PathFinder(vertexFile, edgeFile);
        
        String randomStartNode = ourPathFinder.chooseRandomNode(vertexFile);
        String randomEndNode = ourPathFinder.chooseRandomNode(vertexFile);
        String printedPath = "";
        
        if (args.length == 2) {
            path = ourPathFinder.getShortestPath(randomStartNode, randomEndNode);
            
            for (int i = 0; i < path.size() - 1; i++) {
                printedPath = printedPath + path.get(i) + " --> ";
            }
            if (path.size() == 0) {
                System.out.println("There is no path between " + randomStartNode + " and " + randomEndNode + ".");
            } else {
                printedPath = printedPath + path.get(path.size() - 1);
                System.out.println("Path from " + randomStartNode + " to " + randomEndNode + " length = " + (path.size() - 1) + ":");
                System.out.println(printedPath);
            }
        } else if (args[2].equals("useIntermediateNode")) {
            String randomIntermediateNode = ourPathFinder.chooseRandomNode(vertexFile);
            path = ourPathFinder.getShortestPath(randomStartNode, randomIntermediateNode, randomEndNode);
            
            for (int i = 0; i < path.size() - 1; i++) {
                printedPath = printedPath + path.get(i) + " --> ";
            }
            if (path.size() == 0) {
                System.out.println("There is no path between " + randomStartNode + " and " + randomEndNode + " going through " + randomIntermediateNode + ".");
            } else {
                printedPath = printedPath + path.get(path.size() - 1);
                System.out.println("Path from " + randomStartNode + " to " + randomEndNode + " going through " + randomIntermediateNode + " has length = " + (path.size() - 1) + ":");
                System.out.println(printedPath);
            }
        } else {
            System.out.println("You did something wrong. Try, try, try again.");
        }
    }
}