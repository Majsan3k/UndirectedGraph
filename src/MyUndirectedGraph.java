import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Stack;
import java.util.Queue;
import java.util.PriorityQueue;

public class MyUndirectedGraph<T> implements UndirectedGraph<T> {

    private HashMap<T, Node> nodes = new HashMap<>();
    private int totalCost = 0;
    private int numberOfEdges = 0;


    public int getTotalCost(){
        return totalCost;
    }

    @Override
    public int getNumberOfNodes() {
        return nodes.size();
    }

    @Override
    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    @Override
    public boolean add(T newNode) {

        if (nodes.containsKey(newNode)) {
            return false;
        }else{
            Node node = new Node(newNode);
            nodes.put(newNode, node);
            return true;
        }
    }

    @Override
    public boolean connect(T node1, T node2, int cost) {

        if(cost <= 0){
            return false;
        }
        if(!nodes.containsKey(node1) || !nodes.containsKey(node2)){
            return false;
        }

        Node nodeFrom = nodes.get(node1);
        Node nodeTo = nodes.get(node2);

        Edge edgeToNode1 = new Edge(nodeFrom, nodeTo, cost);
        Edge edgeToNode2 = new Edge(nodeTo, nodeFrom, cost);

        LinkedList<Edge> edgesToNode1 = nodes.get(node1).getEdges();
        LinkedList<Edge> edgesToNode2 = nodes.get(node2).getEdges();

        for (Edge edge : edgesToNode1) {
            if (edge.getNodeTo().getValue() == node2) {
                edge.setCost(cost);

                for(Edge edgeX : edgesToNode2) {
                    if (edgeX.getNodeTo().getValue() == node1) {
                        edgeX.setCost(cost);
                        return true;
                    }
                }
            }
        }
        edgesToNode1.add(edgeToNode1);
        if(!node1.equals(node2)) {
            edgesToNode2.add(edgeToNode2);
        }
        totalCost += cost;
        numberOfEdges ++;

        return true;
    }

    @Override
    public boolean isConnected(T node1, T node2) {

        if(!nodes.containsKey(node1) || !nodes.containsKey(node2)){
            return false;
        }else{
            LinkedList<Edge> list = nodes.get(node1).getEdges();

            for(Edge edge : list) {
                if (edge.getNodeTo().getValue().equals(node2)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getCost(T node1, T node2) {

        if(isConnected(node1, node2)){
            LinkedList<Edge> list = nodes.get(node1).getEdges();

            for(Edge edge : list){
                if(edge.getNodeTo().getValue().equals(node2)){
                    return edge.getCost();
                }
            }
        }
        return -1;
    }

    @Override
    public List<T> depthFirstSearch(T start, T end) {

        LinkedList<T> visitedNodes = new LinkedList<>();
        List<Node> visitedNodesToReset = new ArrayList<Node>();

        if (start == null || end == null){
            return visitedNodes;
        }

        Node startNode = nodes.get(start);
        Node endNode = nodes.get(end);

        Stack<Node> stack = new Stack<>();
        stack.push(startNode);

        startNode.setVisited(true);
        visitedNodesToReset.add(startNode);

        while (!stack.isEmpty()) {

            Node curr = stack.peek();
            boolean pushed = false;
            List<Edge> edges = curr.getEdges();

            if(curr.equals(endNode)){
                break;
            }
            for(Edge edge : edges){

                if(!edge.getNodeTo().getVisited()){
                    stack.push(edge.getNodeTo());
                    edge.getNodeTo().setVisited(true);
                    visitedNodesToReset.add(edge.getNodeTo());
                    pushed = true;
                    break;
                }
            }
            if(!pushed){
                stack.pop();
            }
        }
        while(!stack.isEmpty()){
            Node n = stack.pop();
            visitedNodes.addFirst(n.getValue());
        }
        for(Node n : visitedNodesToReset){
            n.setVisited(false);
        }

        return visitedNodes;
    }

    @Override
    public List<T> breadthFirstSearch(T start, T end) {

        Queue<Node> q = new LinkedList<>();
        List<Node> visitedNodesToReset = new ArrayList<>();
        HashMap<Node, Node> findPath = new HashMap<>();
        ArrayList<T> listToReturn = new ArrayList<>();

        if(start == null || end == null){
            return (LinkedList<T>) q;
        }

        Node startNode = nodes.get(start);
        Node endNode = nodes.get(end);

        q.add(startNode);
        startNode.setVisited(true);
        visitedNodesToReset.add(startNode);

        while(!q.isEmpty()){

            Node curr = q.poll();
            LinkedList<Edge> currList = curr.getEdges();

            if(curr == endNode ){
                listToReturn.add(endNode.getValue());
                break;
            }

            for(Edge edge : currList){

                Node n = edge.getNodeTo();

                if (!n.getVisited()) {
                    q.add(n);
                    n.setVisited(true);
                    visitedNodesToReset.add(n);
                    findPath.put(n, curr);
                }
            }
        }
        for(Node n : visitedNodesToReset){
            n.setVisited(false);
        }

        Node from = findPath.get(endNode);

        while(from != null){
            listToReturn.add(0,from.getValue());
            endNode = from;
            from = findPath.get(endNode);
        }
        return listToReturn;
    }

    @Override
    public UndirectedGraph<T> minimumSpanningTree() {

        MyUndirectedGraph<T> newGraph = new MyUndirectedGraph<>();
        PriorityQueue<Edge> queue = new PriorityQueue<>();
        ArrayList<Node> unvisitedNodes = new ArrayList<>(nodes.values());

        if(unvisitedNodes.isEmpty()){
            return newGraph;
        }

        Node startNode = unvisitedNodes.get(0);
        unvisitedNodes.remove(startNode);
        newGraph.add(startNode.getValue());

        while(!unvisitedNodes.isEmpty()) {

            List<Edge> currList = startNode.getEdges();

            for(Edge edge : currList){

                if(unvisitedNodes.contains(edge.getNodeTo())){
                    queue.add(edge);
                }
            }
            Edge edgeToAdd = queue.poll();
            Node nodeFrom = edgeToAdd.getNodeFrom();
            Node nodeTo = edgeToAdd.getNodeTo();

            boolean containsNodeFrom = newGraph.nodes.containsKey(edgeToAdd.getNodeFrom().getValue());
            boolean containsNodeTo = newGraph.nodes.containsKey(edgeToAdd.getNodeTo().getValue());

            while(containsNodeFrom && containsNodeTo){

                edgeToAdd = queue.poll();
                nodeFrom = edgeToAdd.getNodeFrom();
                nodeTo = edgeToAdd.getNodeTo();
                containsNodeFrom = newGraph.nodes.containsKey(edgeToAdd.getNodeFrom().getValue());
                containsNodeTo = newGraph.nodes.containsKey(edgeToAdd.getNodeTo().getValue());
            }

            if(!containsNodeFrom){
                newGraph.add(nodeFrom.getValue());
            }
            if(!containsNodeTo){
                newGraph.add(nodeTo.getValue());
            }
            newGraph.connect(nodeFrom.getValue(), nodeTo.getValue(), edgeToAdd.getCost());

            startNode = edgeToAdd.getNodeTo();
            unvisitedNodes.remove(startNode);
        }
        return newGraph;
    }

    //Node class
    private class Node {

        private LinkedList<Edge> edges = new LinkedList<>();
        private T value;
        private boolean visited = false;

        Node(T value) {
            this.value = value;
        }

        T getValue() {
            return value;
        }

        LinkedList<Edge> getEdges() {
            return edges;
        }

        boolean getVisited() {
            return visited;
        }

        void setVisited(boolean visited) {
            this.visited = visited;
        }
    }

    //Edge class
    private class Edge implements Comparable{

        private int cost;
        private Node nodeFrom;
        private Node nodeTo;

        Edge(Node nodeFrom, Node nodeTo, int cost){
            this.nodeFrom = nodeFrom;
            this.nodeTo = nodeTo;
            this.cost = cost;
        }

        int getCost(){
            return cost;
        }

        Node getNodeFrom(){
            return nodeFrom;
        }

        Node getNodeTo(){
            return nodeTo;
        }

        void setCost(int newCost){
            this.cost = newCost;
        }

        @Override
        public int compareTo(Object o){

            Edge otherEdge = (Edge) o;

            if(this.cost < otherEdge.cost){
                return -1;
            }
            if(this.cost > otherEdge.cost){
                return 1;
            }
            return 0;
        }
    }
}