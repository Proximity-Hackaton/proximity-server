package ch.proximity.proximityserver.controller;

import ch.proximity.proximityserver.database.FakeDB;
import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityGraph;
import ch.proximity.proximityserver.model.ProximityNode;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Iterator;

@RestController
public class GraphController {

    @GetMapping("/rawNeighborhood")
    public ProximityGraph getRawNodeNeighborhood(
            @RequestParam(required = true, value = "source") String source,
            @RequestParam(value = "depth", defaultValue = "1") Integer depth,
            @RequestParam(value = "minTimeStamp", defaultValue = "-1") Long minTimeStamp,
            @RequestParam(value = "maxTimeStamp", defaultValue = "-1") Long maxTimeStamp
    ){
        return rawNeighborhood(source, depth, minTimeStamp, maxTimeStamp, false);
    }

    @GetMapping("/fakeRawNeighborhood")
    public ProximityGraph getRawNodeNeighborhoodFake(
            @RequestParam(required = true, value = "source") String source,
            @RequestParam(value = "depth", defaultValue = "1") Integer depth,
            @RequestParam(value = "minTimeStamp", defaultValue = "-1") Long minTimeStamp,
            @RequestParam(value = "maxTimeStamp", defaultValue = "-1") Long maxTimeStamp
    ){
        return rawNeighborhood(source, depth, minTimeStamp, maxTimeStamp, true);
    }

    private ProximityGraph rawNeighborhood(
            @RequestParam(required = true, value = "source") String source,
            @RequestParam(value = "depth", defaultValue = "1") Integer depth,
            @RequestParam(value = "minTimeStamp", defaultValue = "-1") Long minTimeStamp,
            @RequestParam(value = "maxTimeStamp", defaultValue = "-1") Long maxTimeStamp,
            boolean fake
    ){
        if(minTimeStamp == -1){
            minTimeStamp = System.currentTimeMillis() - 300000;
        }
        if(maxTimeStamp == -1){
            maxTimeStamp = System.currentTimeMillis();
        }

        SimpleDirectedGraph<ProximityNode, ProximityEdge> graph =
                (fake ? FakeDB.fakeInstance() : FakeDB.getInstance()).BFS_n_read(source, depth, minTimeStamp, maxTimeStamp);

        if(graph == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Source node not found");

        int edgesSetSize = graph.edgeSet().size();
        int nodesSetSize = graph.vertexSet().size();

        return new ProximityGraph(graph.edgeSet().toArray(new ProximityEdge[edgesSetSize]), graph.vertexSet().toArray(new ProximityNode[nodesSetSize]));
    }
}
