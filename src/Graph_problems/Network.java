package Graph_problems;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**  Graph problem "Network": https://acm.timus.ru/problem.aspx?space=1&num=1160
 *
 *   Solution with Prim's minimum spanning tree (MST) algorithm.
 */

public class Network {

    private class MST {
        private Set<Integer> nodes;
        //private int[] nodes;
        private Edge[] edges;
        private int nodeNumber, lenEdges = 0;
    }

    private class Edge implements Comparable<Edge> {
        private int node1, node2, weight;

        private Edge() {
            this.node1 = -1;
        }

        private Edge(int node1, int node2) {
            this.node1 = node1;
            this.node2 = node2;
        }

        @Override
        public int compareTo(Edge y) {
            int value = this.weight - y.weight;
            if (value == 0)
                value = this.node1 - y.node1;
            if (value == 0)
                value = this.node2 - y.node2;
            return value;
        }
    }

    private class Result {
        private int maxCableSize, edgesUsed;
        Edge[] cablesList;
    }

    public static void main(String[] args) {
        Network t = new Network();
        MST mst = t.dataReader();
        // print(mst);
        Result result = t.mstSearch(mst);
        printResult(result);
    }

    private MST dataReader() {
        MST mst = new MST();
        Edge edge;
        char[] line, buffer = new char[6];
        char c;
        int length, index = 0, value = 0, i, j, count;
        boolean tester = System.getProperty("ONLINE_JUDGE") != null;
        try (BufferedReader reader = tester
                ? new BufferedReader(new InputStreamReader(System.in))
                : new BufferedReader(new FileReader("D:/1.txt"))) {
            line = reader.readLine().toCharArray();
            length = line.length;
            for (i = 0; i < length; i++) {
                c = line[i];
                if (c != ' ')
                    buffer[index++] = c;
                else {
                    for (j = 0; j < index; j++)
                        value = value * 10 + (buffer[j] - '0');
                    mst.nodeNumber = value;
                    mst.nodes = new HashSet<>();
                    index = value = 0;
                }
            }
            for (j = 0; j < index; j++)
                value = value * 10 + (buffer[j] - '0');
            mst.edges = new Edge[value];
            count = value;
            index = value = 0;
            while (count-- > 0) {
                edge = mst.edges[mst.lenEdges++] = new Edge();
                line = reader.readLine().toCharArray();
                length = line.length;
                for (i = 0; i < length; i++) {
                    c = line[i];
                    if (c != ' ')
                        buffer[index++] = c;
                    else {
                        for (j = 0; j < index; j++)
                            value = value * 10 + (buffer[j] - '0');
                        if (edge.node1 == -1)
                            edge.node1 = value - 1;
                        else
                            edge.node2 = value - 1;
                        index = value = 0;
                    }
                }
                for (j = 0; j < index; j++)
                    value = value * 10 + (buffer[j] - '0');
                edge.weight = value;
                index = value = 0;
            }
            Arrays.sort(mst.edges);
            return mst;
        } catch (IOException e) {
            System.out.println("Reading error.");
        }
        return null;
    }

    private static void print(MST mst) {
        for (Edge edge : mst.edges) {
            if (edge == null)
                System.out.println("Null");
            else
                System.out.println((edge.node1 + 1) + " " + (edge.node2 + 1) + " " + edge.weight);
        }
    }

    private Result mstSearch(MST mst) {
        Result result = new Result();
        Set<Integer> nodes = mst.nodes;
        int size = mst.nodeNumber, lenEdges = mst.lenEdges, index = 0, i;
        result.cablesList = new Edge[lenEdges];
        Edge[] edges = mst.edges, cablesList = result.cablesList;
        nodes.add(edges[0].node1);
        nodes.add(edges[0].node2);
        result.maxCableSize = edges[0].weight;
        result.edgesUsed = 1;
        cablesList[index++] = new Edge(edges[0].node1, edges[0].node2);
        edges[0] = null;
        while (nodes.size() < size) {
            for (i = 0; i < lenEdges; i++) {
                if (edges[i] != null) {
                    if (nodes.contains(edges[i].node1) && !nodes.contains(edges[i].node2)) {
                        nodes.add(edges[i].node2);
                        if (edges[i].weight > result.maxCableSize)
                            result.maxCableSize = edges[i].weight;
                        result.edgesUsed++;
                        cablesList[index++] = new Edge(edges[i].node1, edges[i].node2);
                        edges[i] = null;
                        break;
                    } else if (!nodes.contains(edges[i].node1) && nodes.contains(edges[i].node2)) {
                        nodes.add(edges[i].node1);
                        if (edges[i].weight > result.maxCableSize)
                            result.maxCableSize = edges[i].weight;
                        result.edgesUsed++;
                        cablesList[index++] = new Edge(edges[i].node1, edges[i].node2);
                        edges[i] = null;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private static void printResult(Result result) {
        Writer writer = new OutputStreamWriter(System.out);
        PrintWriter out = new PrintWriter(writer);
        out.println(result.maxCableSize);
        out.println(result.edgesUsed);
        int length = result.edgesUsed;
        Edge[] edges = result.cablesList;
        for (int i = 0; i < length; i++)
            out.println((edges[i].node1 + 1) + " " + (edges[i].node2 + 1));
        out.flush();
    }
}