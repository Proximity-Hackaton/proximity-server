package ch.proximity.proximityserver.explorer;

import ch.proximity.proximityserver.model.ProximityEdge;
import ch.proximity.proximityserver.model.ProximityNode;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.*;

public class TrustTransferNumberFunction implements TrustFunction{


    private SimpleGraph<ProximityNode, ProximityEdge> validateBidirection(
            Graph<ProximityNode, ProximityEdge> graph
    ){
        SimpleGraph<ProximityNode, ProximityEdge> uGraph = new SimpleGraph<>(ProximityEdge.class);
        graph.vertexSet().forEach(uGraph::addVertex);
        graph.edgeSet().stream().forEach((e)->{
            ProximityNode n1 = graph.getEdgeSource(e);
            ProximityNode n2 = graph.getEdgeTarget(e);
            if(!(uGraph.containsEdge(n1,n2)) && graph.containsEdge(n1, n2) && graph.containsEdge(n2, n1)){
                uGraph.addEdge(n1, n2, e);
            }
        });
        return uGraph;
    }

    private boolean certifyLevel(
            SimpleGraph<ProximityNode, ProximityEdge> graph,
            ProximityNode origin,
            Map<ProximityNode, Integer> edgeDeletedCount,
            int level,
            int k){
        BreadthFirstIterator<ProximityNode, ProximityEdge> bfs = new BreadthFirstIterator<>(graph, origin);
        List<ProximityEdge> toDelete = new ArrayList<>();
        Set<ProximityNode> referees = new HashSet<>();
        boolean levelReached = false;
        boolean maxLevelReached = false;
        while(!(maxLevelReached) && bfs.hasNext()){
            ProximityNode n = bfs.next();
            int currentLevel = bfs.getDepth(n);
            if(currentLevel == (level-1)){
                referees.add(n);
            }else if(currentLevel == level){
                levelReached = true;
                List<ProximityEdge> refEdges = new ArrayList<>();
                refEdges.addAll(
                        graph.edgesOf(n).stream().filter((e) ->
                            referees.contains(graph.getEdgeSource(e)) || referees.contains(graph.getEdgeTarget(e))
                        ).toList()
                );
                if((refEdges.size()+edgeDeletedCount.getOrDefault(n, 0)) < k){
                    edgeDeletedCount.put(
                            n,
                            edgeDeletedCount.getOrDefault(n, 0) + refEdges.size()
                    );
                    toDelete.addAll(refEdges);
                }
            }else if(currentLevel > level){
                levelReached = true;
                maxLevelReached = true;
            }
        }

        toDelete.forEach((e) ->{
            graph.removeEdge(e);
        });
        return levelReached;
    }

    private ProximityNode attachOrigin(SimpleGraph<ProximityNode, ProximityEdge> graph, List<ProximityNode> trustees){
        ProximityNode originN = new ProximityNode("origin", "origin");
        graph.addVertex(originN);
        trustees.forEach((t)->{
            graph.addEdge(originN, t, new ProximityEdge(0, "originEdge", "originEdge"));
        });
        return originN;
    }

    @Override
    public void applyFunction(Graph<ProximityNode, ProximityEdge> graph, List<String> trustees, int k) {
        SimpleGraph<ProximityNode, ProximityEdge> uGraph = validateBidirection(graph);
        List<ProximityNode> trusteesNodes = uGraph.vertexSet().stream().filter((n) ->{
            return trustees.contains(n.getOwnerWallet());
        }).toList();

        ProximityNode origin= attachOrigin(uGraph, trusteesNodes);
        Map<ProximityNode, Integer> edgeDeletedCount = new HashMap<>();
        boolean hasNext = certifyLevel(
                uGraph,
                origin,
                edgeDeletedCount,
                1,
                1
        );
        int level = 2;
        while(hasNext){
            hasNext = certifyLevel(
                    uGraph,
                    origin,
                    edgeDeletedCount,
                    level,
                    level == 2 ? 1 : k
            );
            level++;
        }
        int totalDepth = level - 2;
        BreadthFirstIterator<ProximityNode, ProximityEdge> bfs = new BreadthFirstIterator<>(uGraph, origin);
        Set<ProximityNode> nodes = new HashSet<>();
        nodes.addAll(uGraph.vertexSet());
        while(bfs.hasNext()){
            ProximityNode n = bfs.next();
            int depth = bfs.getDepth(n);
            n.setTrust(1.0 - ((depth*1.0-1.0)/(totalDepth)));
            nodes.remove(n);
        }
        // zero trust for remaining eliminated ones
        nodes.forEach((n)->{
            n.setTrust(0.0);
        });
    }
}
