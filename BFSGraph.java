import java.util.*;

public class BFSGraph {
    private int vertices; // Number of vertices
    private LinkedList<Integer>[] adjList; // Adjacency list

    // Constructor
    public BFSGraph(int v) {
        vertices = v;
        adjList = new LinkedList[v];

        for (int i = 0; i < v; ++i) {
            adjList[i] = new LinkedList<>();
        }
    }

    // Method to add an edge to the graph
    public void addEdge(int v, int w) {
        adjList[v].add(w); // For directed graph
        // adjList[w].add(v); // Uncomment for undirected graph
    }

    // BFS traversal from a given source
    public void BFS(int start) {
        boolean[] visited = new boolean[vertices];
        Queue<Integer> queue = new LinkedList<>();

        // Mark the source as visited and enqueue it
        visited[start] = true;
        queue.add(start);

        System.out.print("BFS Traversal starting from vertex " + start + ": ");

        while (!queue.isEmpty()) {
            int current = queue.poll();
            System.out.print(current + " ");

            // Enqueue all adjacent unvisited vertices
            for (int neighbor : adjList[current]) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
        System.out.printl