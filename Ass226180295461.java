/* 
Thinzar Htet 180295461

Some things to note:
1. All .csv files are to be in the same folder as the .java file
2. To run the program, type in this order: java Ass226180295461 stations stations_lon_lat randomGraph
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

class Ass226180295461 {

    static int N = 2100; //need to increase if station IDs are more than 2100
    static double[][] edges = new double[N][N]; //adjacency matrix for normal weighted graph
    static double[][] edgesKM = new double[N][N]; //adjacency matrix for km weighted graph
    static TreeMap<Integer, String> stationName = new TreeMap<Integer, String>();
    static Set<Integer> stationID = stationName.keySet();
    static HashMap<Integer, double[]> stationLonLat = new HashMap<Integer, double[]>();
    static Graph g;
    static ArrayList<Integer> maxShortestPaths = new ArrayList<Integer>();
    static String z;
    static long startTime, endTime, totalTime;

    public static void main(String[] args) throws FileNotFoundException {
        //start recording program execution time
        startTime = System.nanoTime();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                edges[i][j] = 0.0;
            }
        }

        //pass filenames from command line into system
        String filename1 = args[0];
        String filename2 = args[1];
        String filename3 = args[2];

        //scan all .csv files 
        scanStationNames(filename1);
        scanStationLonLat(filename2);
        scanAdjacencyMatrix(filename3);

        //create graph object
        g = new Graph(edges);

        //output
        printResults();

        //stop recording and then print programme execution time 
        endTime = System.nanoTime();
        totalTime = (endTime - startTime) / 1000000;
        System.out.println("Execution Time : " + totalTime + " milliseconds");
    }

    static void printResults() {
        System.out.println("Name: Thinzar Htet");
        System.out.println("Student ID: 180295461");
        System.out.println("");
        System.out.println("Question 1: " + q1Answer());
        System.out.println(q234Answers());
        System.out.println("Question 5: " + q5Answer());
        System.out.println("Question 6: " + q6Answer());
        System.out.println("Question 7: " + q7Answer());
        System.out.println("");
    }

    //scan all station name from station file
    static void scanStationNames(String filename1) throws FileNotFoundException {
        Scanner s = new Scanner(new FileReader(filename1 + ".csv"));
        z = s.nextLine();
        while (s.hasNext()) {
            z = s.nextLine();
            String[] results = z.split(",");
            stationName.put(Integer.parseInt(results[0]), results[1]);
        }
    }

    //scan all station longtitude and latitude from station_lon_lat file
    static void scanStationLonLat(String filename2) throws FileNotFoundException {
        Scanner s = new Scanner(new FileReader(filename2 + ".csv"));
        z = s.nextLine();
        while (s.hasNext()) {
            z = s.nextLine();
            String[] results = z.split(",");
            double[] lonLat = new double[2];
            lonLat[0] = Double.parseDouble(results[1]);
            lonLat[1] = Double.parseDouble(results[2]);
            stationLonLat.put(Integer.parseInt(results[0]), lonLat);
        }
    }

    //scan adjacency matrix from randomGraph file
    static void scanAdjacencyMatrix(String filename3) throws FileNotFoundException {
        Scanner s = new Scanner(new FileReader(filename3 + ".csv"));
        z = s.nextLine();
        while (s.hasNext()) {
            z = s.nextLine();
            String[] results = z.split(",");
            edges[Integer.parseInt(results[0])][Integer.parseInt(results[1])] = Double.parseDouble(results[2]);
            edgesKM[Integer.parseInt(results[0])][Integer.parseInt(results[1])] = realDistance(
                    stationLonLat.get(Integer.parseInt(results[0]))[1], stationLonLat.get(Integer.parseInt(results[0]))[0],
                    stationLonLat.get(Integer.parseInt(results[1]))[1], stationLonLat.get(Integer.parseInt(results[1]))[0]);
        }
    }

    static int q1Answer() {
        int station1 = getStationID("W 52 St & 9 Ave");
        int station2 = getStationID("5 Ave & E 29 St");
        HashSet<ArrayList<Integer>> shortestPaths = g.shortestPaths(station1, station2);
        int numberOfShortestPaths = shortestPaths.size();
        return numberOfShortestPaths;
    }

    static String q234Answers() {
        calculateMaxShortestPaths();

        //q2
        String q2Answer = maxShortestPaths.get(0) + " & " + maxShortestPaths.get(1);

        //q3
        Integer numberOfShortestPaths = maxShortestPaths.get(2);
        String q3Answer = Integer.toString(numberOfShortestPaths);

        //q4
        HashSet<ArrayList<Integer>> shortestPaths = g.shortestPaths(maxShortestPaths.get(0), maxShortestPaths.get(1));
        int lengthOfEachShortestPath = firstElement(shortestPaths).size();
        String q4Answer = Integer.toString(lengthOfEachShortestPath);

        //put answers into a single String for printing
        StringBuilder sb = new StringBuilder();
        sb.append("Question 2: ").append(q2Answer);
        sb.append("\n");
        sb.append("Question 3: ").append(q3Answer);
        sb.append("\n");
        sb.append("Question 4: ").append(q4Answer);
        return sb.toString();
    }

    static ArrayList<Integer> q5Answer() {
        int startStation = getStationID("1 Ave & E 44 St");
        ArrayList<Integer> furthestStations = furthestStation(startStation);
        return furthestStations;
    }

    static double q6Answer() {
        int station1 = getStationID("E 56 St & 3 Ave");
        int station2 = getStationID("W 20 St & 8 Ave");
        double shortestPathLength = lengthInSumOfEdgesWeights(station1, station2);
        return shortestPathLength;
    }

    static double q7Answer() {
        int station1 = getStationID("Clinton St & Joralemon St");
        int station2 = getStationID("State St & Smith St");
        double shortestPathLength = lengthInKM(station1, station2);
        return shortestPathLength;
    }

    //calculate shortest paths values in q2, q3 and q4
    static void calculateMaxShortestPaths() {
        int startStation = 0, endStation = 0, maxNum = 0, currentSize = 0;
        HashSet<ArrayList<Integer>> shortestPaths;
        
        for (int startID : stationID) {
            for (int endID : stationID) {
                shortestPaths = g.shortestPaths(startID, endID);
                if (!shortestPaths.isEmpty()) {
                    currentSize = shortestPaths.size();
                    if (maxNum < currentSize) {
                        maxNum = currentSize;
                        startStation = startID;
                        endStation = endID;
                    }
                }
            }
        }
        //store values
        maxShortestPaths.add(startStation);
        maxShortestPaths.add(endStation);
        maxShortestPaths.add(maxNum);
    }

    //calculate the furthest station in q5
    static ArrayList<Integer> furthestStation(int startStation) {
        int max = 0, currentSize = 0;
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Integer> tempList = new ArrayList<>();
        HashSet<ArrayList<Integer>> tempHash = new HashSet<>();

        for (int endID : stationID) {
            if (startStation != endID) {
                tempHash = g.shortestPaths(startStation, endID);
                if (!tempHash.isEmpty()) {
                    for (ArrayList temp : tempHash) {
                        currentSize = temp.size();
                        if (max <= currentSize) {
                            max = currentSize;
                            if (!tempList.contains(endID)) {
                                //store values
                                tempList.add(endID);
                            }
                        }
                    }
                }
            }
        }
        for (int endID : tempList) {
            if (startStation != endID) {
                tempHash = g.shortestPaths(startStation, endID);
                if (!tempHash.isEmpty()) {
                    for (ArrayList temp : tempHash) {
                        currentSize = temp.size();
                        if (max == currentSize) {
                            if (!result.contains(endID)) {
                                //store values
                                result.add(endID);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    //calculate the path length by using the sum of the weights of edges
    static double lengthInSumOfEdgesWeights(int start, int end) {
        double distance = 0.0, temp;
        ArrayList<Integer> path = g.dijkstra(start, end);

        if (path.size() == 2) {
            temp = edges[path.get(0)][path.get(1)];
            edges[path.get(0)][path.get(1)] = 0.0;
            ArrayList<Integer> pathFinal = g.dijkstra(start, end);
            edges[path.get(0)][path.get(1)] = temp;
            distance = g.Q.get(pathFinal.get(pathFinal.size() - 1));
        } else {
            distance = g.Q.get(path.get(path.size() - 1));
        }
        return distance;
    }

    //given method from brief
    static double realDistance(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371;
        // km (change this constant to get miles)
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;
    }

    //calculate the path length in kilometres
    static double lengthInKM(int start, int end) {
        double[] firstStation = null, secondStation = null;
        double distance = 0.0, temp = 0.0;
        edges = edgesKM;
        g = new Graph(edges);
        ArrayList<Integer> path = g.dijkstra(start, end);

        if (path.size() == 2) {
            ArrayList<Integer> pathFinal = g.dijkstra(start, end);
            edges[path.get(0)][path.get(1)] = temp;
            for (int i = 0; i < path.size(); i++) {
                if (secondStation == null) {
                    secondStation = stationLonLat.get(pathFinal.get(i));
                } else {
                    firstStation = stationLonLat.get(pathFinal.get(i));
                    distance += realDistance(firstStation[1], firstStation[0], secondStation[1], secondStation[0]);
                    secondStation = firstStation;
                }
            }
        } else {
            for (int i = 0; i < path.size(); i++) {
                if (secondStation == null) {
                    secondStation = stationLonLat.get(path.get(i));
                } else {
                    firstStation = stationLonLat.get(path.get(i));
                    distance += realDistance(firstStation[1], firstStation[0], secondStation[1], secondStation[0]);
                    secondStation = firstStation;
                }
            }
        }
        return distance;
    }

    //extract the ID of station name from stations.csv 
    static int getStationID(String value) {
        int stationID = 0;

        for (Entry<Integer, String> entry : stationName.entrySet()) {
            if (entry.getValue().equals(value)) {
                stationID = entry.getKey();
            }
        }
        return stationID;
    }

    //given method from brief
    static ArrayList<Integer> firstElement(HashSet<ArrayList<Integer>> s) {
        return (ArrayList<Integer>) s.toArray()[0];
    }

    //given method from brief
    static ArrayList<String> convert(ArrayList<Integer> m) {
        ArrayList<String> z = new ArrayList<String>();
        for (Integer i : m) {
            z.add(stationName.get(i));
        }
        return z;
    }

    //given method from brief
    static HashSet<ArrayList<String>> convert(HashSet<ArrayList<Integer>> paths) {
        HashSet<ArrayList<String>> k = new HashSet<ArrayList<String>>();
        for (ArrayList<Integer> p : paths) {
            k.add(convert(p));
        }
        return k;
    }

    //inner graph class, given from brief
    static class Graph {

        double[][] adj;
        static HashMap<Integer, Double> Q = new HashMap<Integer, Double>();

        //given class constructor from brief
        Graph(double[][] a) {
            adj = new double[a.length][a.length];
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a.length; j++) {
                    adj[i][j] = a[i][j];
                }
            }
        }

        //given method from brief
        public HashSet<Integer> neighbours(int v) {
            HashSet<Integer> h = new HashSet<Integer>();
            for (int i = 0; i < adj.length; i++) {
                if (adj[v][i] != 0) {
                    h.add(i);
                }
            }
            return h;
        }

        //given method from brief
        public HashSet<Integer> vertices() {
            HashSet<Integer> h = new HashSet<Integer>();
            for (int i = 0; i < adj.length; i++) {
                h.add(i);
            }
            return h;
        }

        //given method from brief
        ArrayList<Integer> addToEnd(int i, ArrayList<Integer> path) // returns a new path with i at the end of path
        {
            ArrayList<Integer> k;
            k = (ArrayList<Integer>) path.clone();
            k.add(i);
            return k;
        }

        //given method from brief, updated 
        HashSet<ArrayList<Integer>> shortestPathsRecursion(HashSet<ArrayList<Integer>> sofar, HashSet<Integer> visited, int end) {
            HashSet<ArrayList<Integer>> more = new HashSet<ArrayList<Integer>>();
            HashSet<ArrayList<Integer>> result = new HashSet<ArrayList<Integer>>();
            HashSet<Integer> newVisited = (HashSet<Integer>) visited.clone();
            boolean done = false;
            boolean carryon = false;
            for (ArrayList<Integer> p : sofar) {
                for (Integer z : neighbours(p.get(p.size() - 1))) {
                    if (!visited.contains(z)) {
                        carryon = true;
                        newVisited.add(z);
                        if (z == end) {
                            done = true;
                            result.add(addToEnd(z, p));
                        } else {
                            more.add(addToEnd(z, p));
                        }
                    }
                }
            }
            if (done) {
                return result;
            } else if (carryon) {
                return shortestPathsRecursion(more, newVisited, end);
            } else {
                return new HashSet<ArrayList<Integer>>();
            }
        }

        //given method from brief, updated
        public HashSet<ArrayList<Integer>> shortestPaths(int first,
                int end) {
            HashSet<ArrayList<Integer>> sofar = new HashSet<ArrayList<Integer>>();
            HashSet<Integer> visited = new HashSet<Integer>();
            ArrayList<Integer> starting = new ArrayList<Integer>();
            starting.add(first);
            sofar.add(starting);
            if (first == end) {
                return sofar;
            }
            visited.add(first);
            return shortestPathsRecursion(sofar, visited, end);
        }

        //given method from brief, updated
        public ArrayList<Integer> dijkstra(int start, int end) {
            int N = adj.length;
            ArrayList<Integer>[] paths = new ArrayList[N];
            for (int i = 0; i < N; i++) {
                Q.put(i, Double.POSITIVE_INFINITY);
                paths[i] = new ArrayList<Integer>();
                paths[i].add(Integer.valueOf(start));
            }
            HashSet<Integer> S = new HashSet();
            S.add(start);
            Q.put(start, 0.0);
            while (!Q.isEmpty()) {
                int v = findSmallest(Q);
                if (v == end && Q.get(v) != Double.POSITIVE_INFINITY) {
                    return paths[end];
                }
                double w = Q.get(v);
                S.add(v);
                for (int u : neighbours(v)) {
                    if (v == start && u == end) {
                        continue;
                    }
                    if (!S.contains(u)) {
                        double w1 = w + adj[v][u];
                        if (w1 < Q.get(u)) {
                            Q.put(u, w1);
                            paths[u] = addToEnd(u, paths[v]);
                        }
                    }
                }
                Q.remove(v);
            }
            return new ArrayList<Integer>();
        }

        //given method from brief
        int findSmallest(HashMap<Integer, Double> t) {
            Object[] things = t.keySet().toArray();
            double val = t.get(things[0]);
            int least = (int) things[0];
            Set<Integer> k = t.keySet();
            for (Integer i : k) {
                if (t.get(i) < val) {
                    least = i;
                    val = t.get(i);
                }
            }
            return least;
        }
    }
}
