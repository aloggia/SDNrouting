import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.javatuples.Quintet;

public class shortestPath {

    public static void main(String[] args) {

        JSONParser parser = new JSONParser();

        String jsonLocation = args[0];
        //String jsonLocation = "topology1.json";
        //File jsonLocation = new File("./topology1.json");
        try {
            Object obj = parser.parse(new FileReader(jsonLocation));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray edges = (JSONArray) jsonObject.get("connected");


            //Port list is of the form (Canonical source device name, processing source device name,
            //                          Canonical dest device name, processing dest device name, Port num)
            ArrayList<Quintet<String, Integer, String, Integer, Integer>> portList = new ArrayList<Quintet<String,
                    Integer, String, Integer, Integer>>();

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
            //highestNumVertices -= 1;
            // We can do this because we know there are 3 hosts on the network
            highestNumVertices += 3;

            ArrayList<ArrayList<Integer>> graph = new ArrayList<ArrayList<Integer>>((int) highestNumVertices);
            for (int i = 0; i < (int) highestNumVertices; i++) {
                graph.add(new ArrayList<Integer>());
            }
            for (Object edge : edges) {
                JSONArray node = (JSONArray) edge;
                // Pair is in form (Canonical name, Processing name)
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

                //portList.add(Triplet.with(sourceIpPair.getValue1(), dstIpPair.getValue1(), outPortInt));
                portList.add(Quintet.with(sourceIpPair.getValue0(), sourceIpPair.getValue1(),
                        dstIpPair.getValue0(), dstIpPair.getValue1(), outPortInt));
                addEdge(graph, sourceIpPair.getValue1(), dstIpPair.getValue1());

            }
            int host_158 = 0;
            int host_130 = 0;
            int host_121 = 0;
            for(Quintet<String, Integer, String, Integer, Integer> device : portList) {
                if (device.getValue0().equals("169.254.20.158")) {
                    host_158 = device.getValue1();
                }
                if (device.getValue0().equals("169.254.173.130")) {
                    host_130 = device.getValue1();
                }
                if (device.getValue0().equals("169.254.240.121")) {
                    host_121 = device.getValue1();
                }
            }
            System.out.println("routing for");
            // TODO: Need to figure out a way to dynamically link hosts through a shortest path depending on the topology
            // TODO: Turn the array list returned by shortestDist into entries into routingTable
            ArrayList<Triplet<Integer, String, Integer>> routingTable = new ArrayList<Triplet<Integer, String, Integer>>();
            routingTable.addAll(shortestDist(graph, host_158, host_130, (int) highestNumVertices, portList));
            routingTable.addAll(shortestDist(graph, host_130, host_158, (int) highestNumVertices, portList));
            routingTable.addAll(shortestDist(graph, host_158, host_121, (int) highestNumVertices, portList));
            routingTable.addAll(shortestDist(graph, host_121, host_158, (int) highestNumVertices, portList)); // This one prints port
            routingTable.addAll(shortestDist(graph, host_130, host_121, (int) highestNumVertices, portList));
            routingTable.addAll(shortestDist(graph, host_121, host_130, (int) highestNumVertices, portList)); // THis on prints port

            // TODO: Put the output data into routing tables
            // TODO: Save the created json routing table so it can be sent by the python program
            JSONObject jsonRoutingTable = new JSONObject();
            //jsonRoutingTable.put("title", "Forwarding Tables Schema");
            JSONArray tableEntries = new JSONArray();
            // TODO: for each element in routingTable, turn that element into an object, with an integer, a string, and an integer
            // TODO: once the element is turned into a correct object, push that object into tableEntries
            for (Triplet<Integer, String, Integer> tableEntry : routingTable) {
                JSONObject entry = new JSONObject();
                entry.put("switch_id", tableEntry.getValue0());
                entry.put("dst_ip", tableEntry.getValue1());
                entry.put("out_port", tableEntry.getValue2());
                tableEntries.add(entry);
            }
            jsonRoutingTable.put("table_entries", tableEntries);

            try {
                //routingTable.addAll(
                FileWriter fileWriter = new FileWriter("routingTable.json");
                fileWriter.write(jsonRoutingTable.toJSONString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


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
    private static ArrayList<Triplet<Integer, String, Integer>> shortestDist(ArrayList<ArrayList<Integer>> graph, int source, int dest, int vertices,
                                     ArrayList<Quintet<String, Integer, String, Integer, Integer>> allPorts) {
        // predecessor[i] array stores predecessor of
        // i and distance array stores distance of i
        // from s
        int[] pred = new int[vertices];
        int[] dist = new int[vertices];

        int outPort = 0;
        ArrayList<Triplet<Integer, String, Integer>> routingTuples = new ArrayList<Triplet<Integer, String, Integer>>();

        String sourceAsString = "";
        for (Quintet<String, Integer, String, Integer, Integer> possibleSource : allPorts) {
            if (possibleSource.getValue1() == source) {
                sourceAsString = possibleSource.getValue0();
            }
        }
        String destAsString = "";
        for (Quintet<String, Integer, String, Integer, Integer> possibleDest : allPorts) {
            if (possibleDest.getValue1() == dest) {
                destAsString = possibleDest.getValue0();
            }
        }

        if (!BFS(graph, source, dest, vertices, pred, dist)) {
            System.out.println("Given source and destination" +
                    "are not connected");
            ArrayList<Triplet<Integer, String, Integer>> nullConnection = new ArrayList<Triplet<Integer, String, Integer>>();
            return nullConnection;
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
        for (Quintet<String, Integer, String, Integer, Integer> port : allPorts) {
            if (port.getValue1() == (path.get(path.size() - 1)) &&
                    port.getValue3() == (path.get(path.size() - 2))) {
                if (port.getValue4() == -1) {
                    // In this case we need to find the out port for the first switch connected to the host
                    // This is the case of a host connected to a switch
                    System.out.print("Shortest path from " + sourceAsString + " to " + destAsString+ " goes out port: ");
                   for (Quintet<String, Integer, String, Integer, Integer> firstHop : allPorts) {
                       if (firstHop.getValue1() == path.get(path.size() - 2) &&
                       firstHop.getValue3() == path.get(path.size() - 3)) {
                           outPort = firstHop.getValue4();
                           System.out.print(firstHop.getValue4() + "\n");
                       }

                    }
                } else {
                    outPort = port.getValue4();
                    System.out.println("Shortest path from " + sourceAsString + " to " + destAsString + " goes out port: " + port.getValue4());
                }
            }
        }


        for (int i = path.size() - 1; i >= 0; i--) {
            System.out.print((path.get(i) + 1) + " ");
        }
        System.out.println();

        // The number of ports a datagram will go through = path length - 2
        for (int i = path.size() - 2; i >= 0; i--) {
            for (Quintet<String, Integer, String, Integer, Integer> connection : allPorts) {
                if (path.get(i + 1) == connection.getValue1() && path.get(i) == connection.getValue3()) {
                    // We have the edge between the 2 nodes we want
                    if (connection.getValue4() != -1) {
                        System.out.print(connection.getValue0() + " ");
                        System.out.print(destAsString + " ");
                        System.out.print(connection.getValue4() + " ");
                        System.out.print("\n");
                        routingTuples.add(Triplet.with(Integer.parseInt(connection.getValue0()),
                                destAsString,
                                connection.getValue4()));
                    }
                }
            }
        }
        System.out.print("\n");
        return routingTuples;
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
