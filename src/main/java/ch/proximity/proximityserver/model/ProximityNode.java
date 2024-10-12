package ch.proximity.proximityserver.model;

public class ProximityNode {
    private final String nodeUUID;
    private final String ownerWallet;

    private double trust;

    public ProximityNode(String nodeUUID, String ownerWallet) {
        this.nodeUUID = nodeUUID;
        this.ownerWallet = ownerWallet;
    }

    public void setTrust(double trust) {
        this.trust = trust;
    }

    public String getNodeUUID() {
        return nodeUUID;
    }

    public String getOwnerWallet() {
        return ownerWallet;
    }

    public double getTrust() {
        return trust;
    }


}
