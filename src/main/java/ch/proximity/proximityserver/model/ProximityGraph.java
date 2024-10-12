package ch.proximity.proximityserver.model;

public class ProximityGraph {

    private ProximityEdge[] edges;
    private ProximityNode[] nodes;

    public ProximityGraph(ProximityEdge[] edges, ProximityNode[] nodes) {
        this.edges = edges;
        this.nodes = nodes;
    }

    public ProximityEdge[] getEdges() {
        return edges;
    }

    public void setEdges(ProximityEdge[] edges) {
        this.edges = edges;
    }

    public ProximityNode[] getNodes() {
        return nodes;
    }

    public void setNodes(ProximityNode[] nodes) {
        this.nodes = nodes;
    }
}
