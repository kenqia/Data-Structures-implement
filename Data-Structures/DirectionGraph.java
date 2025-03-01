
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * 无权图（无权、有向图）
 * 红黑树的实现方式（图的最终实现方式）
 * 在以下 V表示顶点数 E表示图的边数
 * 空间复杂度 O(V + E)   这里V和E都是必要的，极端情况下可能E=0
 * 建图时间   O(ElogV) 
 * 两点是否相邻 O(logV) 
 * 查找所有邻边 O(degree(V)) 默认为顶点的度， 如果是完全图或者稠密图接近O(V)
 */
class DirectionGraph implements Cloneable {
    private int V; // 图的顶点数
    private int E; // 图的边数
    private TreeSet<Integer>[] adj; // 图方隈
    private boolean direction = false; 
    private int[] indegrees; // 入度
    private int[] outdegres; // 出度

    public DirectionGraph(String filename, boolean direction) {
        this.direction = direction;
        File file = new File(filename);
        try(Scanner scanner = new Scanner(file)){
            V = scanner.nextInt();
            if (V < 0) 
                throw new IllegalArgumentException("V must be non-negative");

            adj = new TreeSet[V];
            for (int i = 0; i < V; i++) {
                adj[i] = new TreeSet<Integer>();
            }
            indegrees = new int[V];
            outdegres = new int[V];

            E = scanner.nextInt();
            if (E < 0)
                throw new IllegalArgumentException("E must be non-negative");

            for (int i=0; i < E; i++) {
                int a = scanner.nextInt();
                validateVertex(a);
                int b = scanner.nextInt(); 
                validateVertex(b);

                if (a == b)
                    throw new IllegalArgumentException("Self Loop is Detected.");

                if (adj[a].contains(b)) 
                    throw new IllegalArgumentException("Parallel Edges are Detected.");

                adj[a].add(b);
                if (direction) {
                    indegrees[b] ++;
                    outdegres[a] ++;
                }
                if (!direction)
                    adj[b].add(a);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public DirectionGraph(String filename) {
        this(filename, false);
    }

    public DirectionGraph(TreeSet<Integer>[] adj, boolean directed) {
        this.direction = directed;
        this.adj = adj;
        this.V = adj.length;
        this.E = 0;

        indegrees = new int[V];
        outdegres = new int[V];
        for (int v = 0; v < V; v++) {
            for (int w: adj(v)) {
                outdegres[v] ++;
                indegrees[w] ++;
                this.E ++;
            }
        }
        // 如果是无向图，要除以2
        if (!directed) this.E /= 2;
    }

    public DirectionGraph reverseGraph() {
        TreeSet<Integer>[] rAdj = new TreeSet[V];
        for (int i=0; i<V; i++) {
            rAdj[i] = new TreeSet<>();
        }

        for (int v=0; v<V; v++) {
            for (int w: adj(v)) {
                rAdj[w].add(v);
            }
        }
        return new DirectionGraph(rAdj, true);
    }

    public int V() {
        return V;
    }

    public int E() {
        return E; 
    }

    // 两个顶点是否有边
    public  boolean hasEdge(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return adj[v].contains(w);
    }

    // 获取一个顶点的邻边
    public Iterable<Integer> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    public void validateVertex(int v) {
        if (v < 0 || v >= V) 
            throw new IllegalArgumentException("vertex "+v+" is invalid.");
    }

    public boolean isDirection() {
        return direction;
    }

    // 获取一个项点的度（有多少条邻边）
    public int degree(int v) {
        if (isDirection()) throw new RuntimeException("degree only work on undiretion graph.");
        validateVertex(v);
        return adj[v].size();
    }

    // 获取入度
    public int indegree(int v) {
        if (!isDirection()) throw new RuntimeException("indegree only work on diretion graph.");
        validateVertex(v);
        return indegrees[v];
    }

    // 获取出度
    public int outdegree(int v) {
        if (!isDirection()) throw new RuntimeException("outdegree only work on diretion graph.");
        validateVertex(v);
        return outdegres[v];
    }

    public void removeEdge(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        if (adj[v].contains(w)) {
            E--;
            if (direction) {
                indegrees[w] --;
                outdegres[v] --;
            }
        } 

        adj[v].remove(w);
        if (!isDirection())
            adj[w].remove(v);
    }

    @Override
    public Object clone() {
        try {
            DirectionGraph cloned = (DirectionGraph) super.clone();
            cloned.adj = new TreeSet[V];
            for (int v=0; v < V; v++) {
                cloned.adj[v] = new TreeSet<Integer>();
                for (int w: adj[v]) {
                    cloned.adj[v].add(w);
                }
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("V = %d, E = %d\n", V, E));

        for (int v=0; v < V; v++) {
            sb.append(String.format("%d: ", v));
            for (int w: adj[v]) {
                sb.append(String.format("%d ", w));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        DirectionGraph g = new DirectionGraph("./DirectionGraph/ug.txt", true);
        // System.out.println(adj);

        for (int v=0; v<g.V; v++) {
            System.out.println(g.indegrees[v] + " " + g.outdegres[v]);
        }
    }

}