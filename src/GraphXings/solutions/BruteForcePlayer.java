package GraphXings.solutions;

import GraphXings.Algorithms.CrossingCalculator;
import GraphXings.Algorithms.Player;
import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameMove;

import java.util.*;

/**
 * A player performing the best move in that instance.
 */
public class BruteForcePlayer implements Player
{
    /**
     * The name of the random player.
     */
    private String name;

    /**
     * Creates a brute force player with the assigned name.
     * @param name
     */
    public BruteForcePlayer(String name)
    {
        this.name = name;
    }

    @Override
    public GameMove maximizeCrossings(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves, int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height)
    {
        return bruteMove(g,usedCoordinates,placedVertices,width,height,vertexCoordinates, true);
    }

    @Override
    public GameMove minimizeCrossings(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves, int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height)
    {
        return bruteMove(g,usedCoordinates,placedVertices,width,height, vertexCoordinates, false);
    }

    @Override
    public void initializeNextRound(Graph g, int width, int height, Role role)
    {

    }

    /**
     * Computes a random valid move.
     * @param g The graph.
     * @param usedCoordinates The used coordinates.
     * @param placedVertices The already placed vertices.
     * @param width The width of the game board.
     * @param height The height of the game board.
     * @return A random valid move.
     *
     */
    private GameMove bruteMove(Graph g, int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height, HashMap<Vertex, Coordinate> vertexCoordinates, boolean maximize)
    {
        if (vertexCoordinates.isEmpty()) {
            return randomMove(g, usedCoordinates, placedVertices, width, height);
        }
        //get unplaced vertices
        Set<Vertex> unplacedVertices = new HashSet<>();
        g.getVertices().forEach(unplacedVertices::add);
        unplacedVertices.removeAll(placedVertices);

        record CoordinatePreference(Coordinate coordinate, int crossings) {}

        Map<Vertex, CoordinatePreference> coordinateForMaximumOrMinimum = new HashMap<>();

        for (Vertex u : unplacedVertices) {
            Map<Coordinate, Integer> maximumOrMinimumCrossings = new HashMap<>();
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (usedCoordinates[i][j] != 1) {
                        //coordinate is unused
                        Coordinate newCoordinate = new Coordinate(i, j);
                        vertexCoordinates.put(u, newCoordinate);
                        Graph graphWithPlacedVertices = graphWithPlacedVertices(g, vertexCoordinates);
                        CrossingCalculator cc = new CrossingCalculator(graphWithPlacedVertices, vertexCoordinates);
                        maximumOrMinimumCrossings.put(newCoordinate, cc.computeCrossingNumber());
                        //reset coordinates
                        vertexCoordinates.remove(u);
                    }
                }
            }

            //coordinate with the best placement for this vertex u
            int minimumOrMaximumCrossings = maximize ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            Coordinate bestCoordinate = null;
            for (Map.Entry<Coordinate, Integer> coordinateAndCrossings : maximumOrMinimumCrossings.entrySet()) {
                if ((maximize && coordinateAndCrossings.getValue() > minimumOrMaximumCrossings) ||
                        (!maximize && coordinateAndCrossings.getValue() < minimumOrMaximumCrossings)) {
                    minimumOrMaximumCrossings = coordinateAndCrossings.getValue();
                    bestCoordinate = coordinateAndCrossings.getKey();
                }
            }
            assert bestCoordinate != null;
            coordinateForMaximumOrMinimum.put(u,new CoordinatePreference(bestCoordinate, minimumOrMaximumCrossings));
        }

        //find the best vertex and its CoordinatePreference
        int minimumOrMaximumCrossings = maximize ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Coordinate bestCoordinate = null;
        Vertex bestVertex = null;
        for (Map.Entry<Vertex, CoordinatePreference> vertexCoordinatePreferenceEntry : coordinateForMaximumOrMinimum.entrySet()) {
            if ((maximize && vertexCoordinatePreferenceEntry.getValue().crossings() > minimumOrMaximumCrossings) ||
                    (!maximize && vertexCoordinatePreferenceEntry.getValue().crossings() < minimumOrMaximumCrossings)) {
                minimumOrMaximumCrossings = vertexCoordinatePreferenceEntry.getValue().crossings();
                bestCoordinate = vertexCoordinatePreferenceEntry.getValue().coordinate();
                bestVertex = vertexCoordinatePreferenceEntry.getKey();
            }
        }
        assert bestCoordinate != null;
        assert bestVertex != null;
        return new GameMove(bestVertex, bestCoordinate);
    }

    private GameMove randomMove(Graph g, int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height)
    {
        Random r = new Random();
        int stillToBePlaced = g.getN()- placedVertices.size();
        int next = r.nextInt(stillToBePlaced);
        int skipped = 0;
        Vertex v=null;
        for (Vertex u : g.getVertices())
        {
            if (!placedVertices.contains(u))
            {
                if (skipped < next)
                {
                    skipped++;
                    continue;
                }
                v=u;
                break;
            }
        }
        Coordinate c = new Coordinate(0,0);
        do
        {
            c = new Coordinate(r.nextInt(width),r.nextInt(height));
        }
        while (usedCoordinates[c.getX()][c.getY()]!=0);
        return new GameMove(v,c);
    }

    /**
     * Creates a graph with only such vertices of input graph g
     * that have already been positioned in course of the game.
     * @return a graph gPrime with already placed vertices of g.
     */
    private Graph graphWithPlacedVertices(Graph g, HashMap<Vertex,Coordinate>vertexCoordinates) {
        Graph gPrime = new Graph();
        for (Vertex u : g.getVertices()) {
            if (vertexCoordinates.containsKey(u)) {
                gPrime.addVertex(u);
                Iterable<Edge> incidentEdges = g.getIncidentEdges(u);
                incidentEdges.forEach(gPrime::addEdge);
            }
        }
        return gPrime;
    }


    @Override
    public String getName()
    {
        return name;
    }
}
