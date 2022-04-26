import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class shortestPath {

    public static void main(String[] args) {
        /*
        // TODO: find way to grab the json over the internet through a command line arg
        JSONParser parser = new JSONParser();

        String jsonLocation = "src/topology1.json";

        try {
            Object obj = parser.parse(new FileReader(jsonLocation));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray edges = (JSONArray) jsonObject.get("connected");

            long highestNumVertices = 0;
            for (Object o : edges) {
                JSONArray edge = (JSONArray) o;
                long sourceNode = (long) edge.get(0);
                if (sourceNode > highestNumVertices) {
                    highestNumVertices = sourceNode;
                }
            }

            ArrayList<ArrayList<Integer>> graph = new ArrayList<ArrayList<Integer>>((int) highestNumVertices);
            for (int i = 0; i < (int) highestNumVertices; i++) {
                graph.add(new ArrayList<Integer>());
            }
            // TODO: add nested try/catch block to parse IP addresses
            // TODO: couple given cannonical names with processing names
            for (Object edge : edges) {
                JSONArray node = (JSONArray) edge;
                long switch_id = (long) node.get(0);
                long dst_ip = (long) node.get(1);
                long out_port = (long) node.get(2);
                addEdge(graph, (int) switch_id - 1, (int) dst_ip - 1);
            }
            for (int i = 0; i < (int) highestNumVertices; i++) {
                for (int j = 0; j < (int) highestNumVertices; j++) {
                    if (i != j) {
                        System.out.println("Shortest path from node " + i + " to node: " + j);
                        printShortestDist(graph, i, j, (int) highestNumVertices);
                    }
                }
            }
            // TODO: Put the output data into routing tables
            // TODO: Send the routing tables out over the internet to the test network


        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        */
        ArrayList<ArrayList<Integer>> graph = new ArrayList<ArrayList<Integer>>(9);
        for (int i = 0; i < 9; i++) {
            graph.add(new ArrayList<Integer>());
        }
        addEdge(graph, 0, 1);
        addEdge(graph, 0, 5);
        addEdge(graph, 0, 3);
        addEdge(graph, 1, 2);
        addEdge(graph, 3, 5);
        addEdge(graph, 3, 4);
        addEdge(graph, 3, 2);
        addEdge(graph, 5, 4);
        addEdge(graph, 5, 7);
        addEdge(graph, 2, 4);
        addEdge(graph, 2, 6);
        addEdge(graph, 2, 7);
        addEdge(graph, 4, 6);
        addEdge(graph, 4, 7);
        addEdge(graph, 6, 7);
        addEdge(graph, 4, 8);
        addEdge(graph, 5, 8);
        addEdge(graph, 6, 8);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (i != j) {
                    System.out.println("Shortest path from node " + i + " to node: " + j);
                    printShortestDist(graph, i, j, 9);
                }
            }
        }


    }

    private static void addEdge(ArrayList<ArrayList<Integer>> graph, int source, int dest) {
        graph.get(source).add(dest);
        graph.get(dest).add(source);
    }
    // TODO: Need to return some data structure that can be translated to a routing entry
    private static void printShortestDist(ArrayList<ArrayList<Integer>> graph, int source, int dest, int vertices) {
        // predecessor[i] array stores predecessor of
        // i and distance array stores distance of i
        // from s
        int[] pred = new int[vertices];
        int[] dist = new int[vertices];

        if (!BFS(graph, source, dest, vertices, pred, dist)) {
            System.out.println("Given source and destination" +
                    "are not connected");
            return;
        }

        // LinkedList to store path
        LinkedList<Integer> path = new LinkedList<Integer>();
        int crawl = dest;
        path.add(crawl);
        while (pred[crawl] != -1) {
            path.add(pred[crawl]);
            crawl = pred[crawl];
        }

        // Print distance
        System.out.println("Shortest path length is: " + dist[dest]);

        // Print path
        System.out.println("Path is ::");
        for (int i = path.size() - 1; i >= 0; i--) {
            System.out.print(path.get(i) + " ");
        }
        System.out.println("");
    }

    private static boolean BFS(ArrayList<ArrayList<Integer>> graph, int source, int dest, int vertices,
                               int[] predecessor, int[] distance) {
        // a queue to maintain queue of vertices whose
        // adjacency list is to be scanned as per normal
        // BFS algorithm using LinkedList of Integer type
        LinkedList<Integer> queue = new LinkedList<Integer>();

        // boolean array visited[] which stores the
        // information whether ith vertex is reached
        // at least once in the Breadth first search
        boolean[] visited = new boolean[vertices];

        // initially all vertices are unvisited
        // so v[i] for all i is false
        // and as no path is yet constructed
        // dist[i] for all i set to infinity
        for (int i = 0; i < vertices; i++) {
            visited[i] = false;
            distance[i] = Integer.MAX_VALUE;
            predecessor[i] = -1;
        }

        // now source is first to be visited and
        // distance from source to itself should be 0
        visited[source] = true;
        distance[source] = 0;
        queue.add(source);

        // bfs Algorithm
        while (!queue.isEmpty()) {
            int u = queue.remove();
            for (int i = 0; i < graph.get(u).size(); i++) {
                if (visited[graph.get(u).get(i)] == false) {
                    visited[graph.get(u).get(i)] = true;
                    distance[graph.get(u).get(i)] = distance[u] + 1;
                    predecessor[graph.get(u).get(i)] = u;
                    queue.add(graph.get(u).get(i));

                    // stopping condition (when we find
                    // our destination)
                    if (graph.get(u).get(i) == dest)
                        return true;
                }
            }
        }
        return false;
    }


}
