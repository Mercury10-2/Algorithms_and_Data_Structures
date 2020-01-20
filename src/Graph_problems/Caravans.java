package Graph_problems;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

/** Graph problem "Корованы": https://acm.timus.ru/problem.aspx?space=1&num=2034
 *
 *  Solution with a couple of BFS and one DFS.
 */

public class Caravans {

    private static int start, finish, robbers;

    private class Village {
        private ArrayList<Integer>
                roads       = new ArrayList<>(),
                listPrev    = new ArrayList<>(),
                listNext    = new ArrayList<>();
        private int[] neighbors, prev, next;
    }

    public static void main(String[] args) {
        Caravans a = new Caravans();
        Village[] map = a.dataReader();
        // print(map);
        int dist = shortestPaths(map);
        ArrayList<Integer> shortestDist = findPaths(map, dist);
        Collections.sort(shortestDist);
        System.out.println(shortestDist.get(shortestDist.size() - 1));
    }

    private Village[] dataReader() {
        String line;
        Village[] map;
        int length, i, j, size;
        boolean tester = System.getProperty("ONLINE_JUDGE") != null;
        try (BufferedReader reader = tester
                ? new BufferedReader(new InputStreamReader(System.in))
                : new BufferedReader(new FileReader("D:/1.txt"))) {
            line = reader.readLine();
            length = Integer.parseInt(line.substring(0, line.indexOf(" ")));
            map = new Village[length];
            for (i = 0; i < length; i++)
                map[i] = new Village();
            length = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
            while (length-- > 0) {
                line = reader.readLine();
                i = Integer.parseInt(line.substring(0, line.indexOf(" "))) - 1;
                j = Integer.parseInt(line.substring(line.indexOf(" ") + 1)) - 1;
                map[i].roads.add(j);
                map[j].roads.add(i);
            }
            length = map.length;
            for (i = 0; i < length; i++) {
                size = map[i].roads.size();
                map[i].neighbors = new int[size];
                for (j = 0; j < size; j++)
                    map[i].neighbors[j] = map[i].roads.get(j);
                map[i].roads = null;
                Arrays.sort(map[i].neighbors);
            }
            line = reader.readLine();
            start = Integer.parseInt(line.substring(0, line.indexOf(" "))) - 1;
            finish = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.lastIndexOf(" "))) - 1;
            robbers = Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1)) - 1;
            return map;
        } catch (IOException e) {
            System.out.println("Reading error.");
        }
        return null;
    }

    private static void print(Village[] map) {
        int length = map.length, size;
        for (int i = 0; i < length; i++) {
            System.out.print((i + 1) + ": ");
            size = map[i].neighbors.length;
            for (int j = 0; j < size; j++)
                System.out.print((map[i].neighbors[j] + 1) + " ");
            System.out.println();
        }
        System.out.println((start + 1) + " " + (finish + 1) + " " + (robbers + 1));
    }

    //  Shortest paths search (BFS) from start to finish + all lists are rewritten to int arrays.
    private static int shortestPaths(Village[] map) {
        int length = map.length, size, node, neighbor, i;
        LinkedList<Integer> queue = new LinkedList<>();
        int[] dist = new int[length];
        for (i = 0; i < length; i++)
            dist[i] = Integer.MAX_VALUE;
        queue.add(start);
        dist[start] = 0;
        while (!queue.isEmpty()) {
            node = queue.pop();
            size = map[node].listPrev.size();
            map[node].prev = new int[size];
            for (i = 0; i < size; i++)
                map[node].prev[i] = map[node].listPrev.get(i);
            Arrays.sort(map[node].prev);
            map[node].listPrev = null;
            if (node == finish)
                return dist[node] + 1;
            size = map[node].neighbors.length;
            for (i = 0; i < size; i++) {
                neighbor = map[node].neighbors[i];
                if (dist[neighbor] > dist[node]) {
                    if (dist[neighbor] == Integer.MAX_VALUE) {
                        queue.add(neighbor);
                        dist[neighbor] = dist[node] + 1;
                    }
                    map[node].listNext.add(neighbor);
                    map[neighbor].listPrev.add(node);
                }
            }
            size = map[node].listNext.size();
            map[node].next = new int[size];
            for (i = 0; i < size; i++)
                map[node].next[i] = map[node].listNext.get(i);
            map[node].listNext = null;
            map[node].neighbors = null;
            Arrays.sort(map[node].next);
        }
        return 0;
    }

    //  Inspects shortest paths with DFS, finds the closest node to robbers in each path, puts it into list and returns
    private static ArrayList<Integer> findPaths(Village[] map, int dist) {
        int length, index = dist - 1, i, node, next;
        int[] path = new int[dist], prev;
        int[] robbersDist = robbersDistances(map);
        LinkedList<Integer> stack = new LinkedList<>();
        ArrayList<Integer> closestVillages = new ArrayList<>();
        path[index--] = finish;
        prev = map[finish].prev;
        length = prev.length;
        for (i = 0; i < length; i++)
            stack.push(prev[i]);
        while (!stack.isEmpty()) {
            node = stack.pop();
            path[index--] = node;
            if (node == start) {
                closestVillages.add(findClosestVillage(robbersDist, path));/*
                for (int v : path)
                    System.out.print((v + 1) + " ");
                System.out.println();*/
                if (!stack.isEmpty()) {
                    next = stack.peek();
                    for (i = 2; i < dist; i++) {
                        if (Arrays.binarySearch(map[path[i]].prev, next) >= 0) {
                            index = i - 1;
                            break;
                        }
                    }
                }
            }
            else {
                prev = map[node].prev;
                length = prev.length;
                for (i = 0; i < length; i++)
                    stack.push(prev[i]);
            }
        }
        return closestVillages;
    }

    //  Finds distances from robbers to every other node.
    private static int[] robbersDistances(Village[] map) {
        int length = map.length, i, node, neighbor;
        int[] dist = new int[length], adj;
        for (i = 0; i < length; i++)
            dist[i] = Integer.MAX_VALUE;
        LinkedList<Integer> queue = new LinkedList<>();
        queue.add(robbers);
        dist[robbers] = 0;
        while (!queue.isEmpty()) {
            node = queue.pop();
            adj = map[node].prev;
            if (adj != null) {
                length = adj.length;
                for (i = 0; i < length; i++) {
                    neighbor = adj[i];
                    if (dist[neighbor] == Integer.MAX_VALUE) {
                        queue.add(neighbor);
                        dist[neighbor] = dist[node] + 1;
                    }
                }
            }
            adj = map[node].next;
            if (adj != null) {
                length = adj.length;
                for (i = 0; i < length; i++) {
                    neighbor = adj[i];
                    if (dist[neighbor] == Integer.MAX_VALUE) {
                        queue.add(neighbor);
                        dist[neighbor] = dist[node] + 1;
                    }
                }
            }
        }/*
        length = map.length;
        for (i = 0; i < length; i++)
            System.out.print((i + 1) + ":" + dist[i] + " ");
        System.out.println();*/
        return dist;
    }

    //  Searches the closest village to robbers.
    private static int findClosestVillage(int[] robbersDist, int[] path) {
        int min = Integer.MAX_VALUE, dist;
        for (int v : path) {
            dist = robbersDist[v];
            if (dist < min)
                min = dist;
        }
        return min;
    }
}
