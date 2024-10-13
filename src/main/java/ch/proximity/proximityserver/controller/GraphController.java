package ch.proximity.proximityserver.controller;

import ch.proximity.proximityserver.database.FakeDB;
import ch.proximity.proximityserver.explorer.TrustFunction;
import ch.proximity.proximityserver.explorer.TrustTransferNumberFunction;
import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityGraph;
import ch.proximity.proximityserver.model.ProximityNode;
import jakarta.servlet.http.HttpServletResponse;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@RestController
public class GraphController {

    @GetMapping("/rawNeighborhood")
    @CrossOrigin(origins = "*")
    public ProximityGraph getRawNodeNeighborhood(
            @RequestParam(required = true, value = "source") String source,
            @RequestParam(value = "depth", defaultValue = "1") Integer depth,
            @RequestParam(value = "minTimeStamp", defaultValue = "-1") Long minTimeStamp,
            @RequestParam(value = "maxTimeStamp", defaultValue = "-1") Long maxTimeStamp
    ){
        return rawNeighborhood(source, depth, minTimeStamp, maxTimeStamp, false);
    }

    @GetMapping("/trustedNeighborhood")
    @CrossOrigin(origins = "*")
    public ProximityGraph getTrustedNodeNeighborhood(
            @RequestParam(required = true, value = "source") String source,
            @RequestParam(value = "depth", defaultValue = "1") Integer depth,
            @RequestParam(value = "minTimeStamp", defaultValue = "-1") Long minTimeStamp,
            @RequestParam(value = "maxTimeStamp", defaultValue = "-1") Long maxTimeStamp
    ){
        if(minTimeStamp == -1){
            minTimeStamp = System.currentTimeMillis() - 300000;
        }
        if(maxTimeStamp == -1){
            maxTimeStamp = System.currentTimeMillis();
        }

        SimpleDirectedGraph<ProximityNode, ProximityEdge> graph =
                (FakeDB.getInstance()).BFS_n_read(source, depth, minTimeStamp, maxTimeStamp);


        if(graph == null) throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Source node not found");
        TrustFunction function = new TrustTransferNumberFunction();
        function.applyFunction(
                graph,
                List.of(source),
                3
                );
        int edgesSetSize = graph.edgeSet().size();
        int nodesSetSize = graph.vertexSet().size();

        return new ProximityGraph(graph.edgeSet().toArray(new ProximityEdge[edgesSetSize]), graph.vertexSet().toArray(new ProximityNode[nodesSetSize]));
    }

    @GetMapping("/fakeRawNeighborhood")
    @CrossOrigin(origins = "*")
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

        if(graph == null) throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Source node not found");

        int edgesSetSize = graph.edgeSet().size();
        int nodesSetSize = graph.vertexSet().size();

        return new ProximityGraph(graph.edgeSet().toArray(new ProximityEdge[edgesSetSize]), graph.vertexSet().toArray(new ProximityNode[nodesSetSize]));
    }
}
