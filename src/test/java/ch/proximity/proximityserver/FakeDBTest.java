package ch.proximity.proximityserver;

import ch.proximity.proximityserver.database.FakeDB;
import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityNode;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FakeDBTest {

    @Test
    public void testBFS_n_read() {
        FakeDB db = FakeDB.fakeInstance();
        SimpleDirectedGraph<ProximityNode, ProximityEdge> graph = db.BFS_n_read("11", 2, 20, 70);
        Set targetSet = Set.of("13","12","11","14","3","4","6","9");

        for(ProximityNode n : graph.vertexSet()){
            assertTrue(targetSet.contains(n.getNodeUUID()));
        }

        boolean b = false;
        boolean c = false;
        boolean d = false;
        for(ProximityEdge e : graph.edgeSet()){
            if(e.getNodeDestUUID().equals("6") && e.getNodeSourceUUID().equals("9")){
                b = true;
            }
            if(e.getNodeDestUUID().equals("9") && e.getNodeSourceUUID().equals("6")){
                c = true;
            }
            if(e.getNodeDestUUID().equals("6") && e.getNodeSourceUUID().equals("7")){
                d = true;
            }

        }

        assertTrue(b && c);
        assertFalse(d);

        assertEquals(graph.vertexSet().size(), targetSet.size());
    }
}
