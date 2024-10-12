package ch.proximity.model;

import org.jgrapht.graph.DefaultEdge;

public class ProximityEdge extends DefaultEdge {
    private final long timestamp;
    private final String edgeUUID;
    private final String nodeSourceUUID;
    private final String nodeDestUUID;

    public ProximityEdge(String UUID, long timestamp, String edgeUUID, String nodeSourceUUID, String nodeDestUUID) {
        super();
        this.timestamp = timestamp;
        this.edgeUUID = UUID;
        this.nodeSourceUUID = nodeSourceUUID;
        this.nodeDestUUID = nodeDestUUID;
    }

    public String getNodeSourceUUID(){
        return nodeSourceUUID;
    }
    public String getNodeDestUUID(){
        return nodeDestUUID;
    }


}
