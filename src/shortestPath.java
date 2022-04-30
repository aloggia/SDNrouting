import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.javatuples.Pair;
import org.javatuples.Triplet;

public class shortestPath {

    public static void main(String[] args) {

        // TODO: find way to grab the json over the internet through a command line arg
        JSONParser parser = new JSONParser();

        String jsonLocation = args[1];
        try {
            Object obj = parser.parse(new FileReader(jsonLocation));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray edges = (JSONArray) jsonObject.get("connected");

            /*
            TODO: port list should be a triplet of Int source, int Destination, and Int Out port
             */
            ArrayList<Triplet<Integer, Integer, Integer>> portList = new ArrayList<Triplet<Integer, Integer, Integer>>();

            long highestNumVertices = 0;
            int srcNode = 0;
            int dstNode = 0;
            for (Object o : edges) {
                JSONArray edge = (JSONArray) o;
                long sourceNode = 0;
                // This try/catch block should account for hosts
                // If the host IP addr can't be parsed into a long an error is thrown, and we know we have a string IP addr
                // The way we handle it is set the val of sourceNode to highestNumVertices + 1 because hosts should
                // be tracked as a vertex in the network graph
                try {
                    sourceNode = (long) edge.get(0);
                } catch (Exception e) {
                    sourceNode = highestNumVertices;
                }
                if (sourceNode > highestNumVertices) {
                    highestNumVertices = sourceNode;
                }
            }
            highestNumVertices -= 1;
            // We can do this because we know there are 3 hosts on the network
            highestNumVertices += 3;

            ArrayList<ArrayList<Integer>> graph = new ArrayList<ArrayList<Integer>>((int) highestNumVertices);
            for (int i = 0; i < (int) highestNumVertices; i++) {
                graph.add(new ArrayList<Integer>());
            }
            // TODO: couple given cannonical names with processing names
            for (Object edge : edges) {
                JSONArray node = (JSONArray) edge;
                // Pair is in form (Cannonical name, Processing name)
                // Process source node
                Pair<String, Integer> sourceIpPair;
                long outPortLong = (long) node.get(2);
                int outPortInt = (int) outPortLong;
                if (node.get(0) instanceof Long) {
                    long source_ip = (long) node.get(0);
                    int source_ip_int = 0;
                    try {
                        source_ip_int = (int) source_ip;
                    } catch (Exception e) {
                        //source_ip_int = (int) (highestNumVertices + processingNameIncrementer);
                    }
                    sourceIpPair = Pair.with(String.valueOf(source_ip), source_ip_int - 1);
                    //portList.add(Triplet.with(sourceIpPair.getValue0(), sourceIpPair.getValue1(), outPortInt));
                } else {
                    String source_ip = (String) node.get(0);
                    int source_ip_int = 0;
                    try {
                        source_ip_int = Integer.parseInt(source_ip);
                    } catch (Exception e) {
                        if (source_ip.equals("169.254.20.158")) {
                            source_ip_int = (int) (highestNumVertices);
                        } else if (source_ip.equals("169.254.173.130")) {
                            source_ip_int = (int) (highestNumVertices - 1);
                        } else if (source_ip.equals("169.254.240.121")) {
                            source_ip_int = (int) (highestNumVertices - 2);
                        }
                    }
                    sourceIpPair = Pair.with(String.valueOf(source_ip), source_ip_int - 1);
                    srcNode = sourceIpPair.getValue1();
                    //portList.add(Triplet.with(sourceIpPair.getValue0(), sourceIpPair.getValue1(), outPortInt));
                }

                // Pair is in form (Cannonical Name, processing name)
                //process dst node
                Pair<String, Integer> dstIpPair;
                if (node.get(1) instanceof Long) {
                    long dst_ip = (long) node.get(1);
                    int dst_ip_int = 0;
                    try {
                        dst_ip_int = (int) dst_ip;
                    } catch (Exception e) {
                        //dst_ip_int = (int) (highestNumVertices + processingNameIncrementer);
                    }
                    dstIpPair = Pair.with(String.valueOf(dst_ip), dst_ip_int - 1);
                    //portList.add(Triplet.with(sourceIpPair.getValue0(), sourceIpPair.getValue1(), outPortInt));
                } else {
                    String dst_ip = (String) node.get(1);
                    int dst_ip_int = 0;
                    try {
                        dst_ip_int = Integer.parseInt(dst_ip);
                    } catch (Exception e) {
                        if (dst_ip.equals("169.254.20.158")) {
                            dst_ip_int = (int) (highestNumVertices);
                        } else if (dst_ip.equals("169.254.173.130")) {
                            dst_ip_int = (int) (highestNumVertices - 1);
                        } else if (dst_ip.equals("169.254.240.121")) {
                            dst_ip_int = (int) (highestNumVertices - 2);
                        }
                    }
                    dstIpPair = Pair.with(dst_ip, dst_ip_int - 1);
                    dstNode = dstIpPair.getValue1();
                    //portList.add(Triplet.with(sourceIpPair.getValue0(), sourceIpPair.getValue1(), outPortInt));
                }

                portList.add(Triplet.with(sourceIpPair.getValue1(), dstIpPair.getValue1(), outPortInt));
                addEdge(graph, sourceIpPair.getValue1(), dstIpPair.getValue1());

            }
            for (int i = 0; i < (int) highestNumVertices; i++) {
                for (int j = 0; j < (int) highestNumVertices; j++) {
                    if (i != j) {
                        System.out.println("Shortest path from node " + (i + 1) + " to node: " + (j + 1));
                        printShortestDist(graph, i, j, (int) highestNumVertices, portList);
                    }
                }
            }
            // TODO: Put the output data into routing tables
            // TODO: Send the routing tables out over the internet to the test network


        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        /*
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
        */

    }

    private static void addEdge(ArrayList<ArrayList<Integer>> graph, int source, int dest) {
        graph.get(source).add(dest);
        graph.get(dest).add(source);
    }
    // TODO: Need to return some data structure that can be translated to a routing entry
    private static void printShortestDist(ArrayList<ArrayList<Integer>> graph, int source, int dest, int vertices,
                                          ArrayList<Triplet<Integer, Integer, Integer>> allPorts) {
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
        //System.out.println("Shortest path length is: " + dist[dest]);

        // Print path
        /* TODO: loop through allPorts
            When we find an entry with allPorts.getValue0 == source && allPorts.getValue1 == dest
            Do something with allPorts.getValue2
         */
        for (Triplet<Integer, Integer, Integer> port : allPorts) {
            if (port.getValue0().equals(path.get(path.size() - 1)) &&
                    port.getValue1().equals(path.get(path.size() - 2)) &&
                    port.getValue2() != -1) {
                System.out.println("Shortest path from " + (source + 1) + " to " + (dest + 1) + " goes out port: " + port.getValue2());
            }
        }


        for (int i = path.size() - 1; i >= 0; i--) {
            System.out.print((path.get(i) + 1) + " ");
        }
        System.out.println();
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
                if (!visited[graph.get(u).get(i)]) {
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
