package ch.proximity.proximityserver;

import ch.proximity.proximityserver.database.FakeDB;
import ch.proximity.proximityserver.explorer.TrustTransferNumberFunction;
import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityNode;
import org.jgrapht.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TrustTransferNumberFunctionTest {
    @Test
    public void test() {
        FakeDB db = FakeDB.fakeInstance();

        Graph<ProximityNode, ProximityEdge> graph = db.BFS_n_read("a14a", 100, 0, System.currentTimeMillis());
        System.out.println(graph.vertexSet().size());
        TrustTransferNumberFunction func = new TrustTransferNumberFunction();
        func.applyFunction(
                graph,
                List.of(db.walletToNodeConvert("a14a")),
                2
        );

        graph.vertexSet().forEach((n)->{
            System.out.println(n.getTrust());
        });



    }
}
