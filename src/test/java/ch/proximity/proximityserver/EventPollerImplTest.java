package ch.proximity.proximityserver;

import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityNode;
import ch.proximity.proximityserver.suipoller.EventPollerImpl;
import org.junit.jupiter.api.Test;

public class EventPollerImplTest {
    @Test
    void testGrapg() {
        EventPollerImpl poller = new EventPollerImpl();
        poller.registerOnNewEdges((edges) -> {
            System.out.println("New edges fetched");
            for(ProximityEdge edge : edges){
                System.out.println("edge wallet of source  : "+edge.getNodeSourceUUID());
                System.out.println("edge wallet of dest : " + edge.getNodeDestUUID());
            }
        });
        poller.registerOnNewNodes((nodes) ->{
            System.out.println("New nodes fetched");
            for(ProximityNode node : nodes){
                System.out.println("Node UUID  : "+node.getNodeUUID());
                System.out.println("Wallet ID : " + node.getOwnerWallet());
            }
        });
        poller.start();
        try {
            Thread.sleep(1000*60*1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        poller.stop();

    }
}
