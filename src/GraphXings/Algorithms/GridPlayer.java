package GraphXings.Algorithms;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameMove;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static GraphXings.Algorithms.Player.Role.MAX;
import static GraphXings.Algorithms.Player.Role.MIN;

public class GridPlayer implements Player{
    /**
     * The name of the player.
     */
    private String name;

    /**
     * Creates a player with the assigned name.
     * @param name name of player
     */
    public GridPlayer(String name)
    {
        this.name = name;
    }

    @Override
    public GameMove maximizeCrossings(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves, int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height)
    {
        return selectMove(g, vertexCoordinates, gameMoves, usedCoordinates, placedVertices, width, height, MAX);
    }


    @Override
    public GameMove minimizeCrossings(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves, int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height)
    {
        return selectMove(g, vertexCoordinates, gameMoves, usedCoordinates, placedVertices, width, height, MIN);
    }

    /**
     * @param numVertices number of randomly selected vertices
     * @param g Graph of current game
     * @param placedVertices already placed vertices
     * @return A list of numVertices many not yet placed random vertices
     */
    private HashSet<Vertex> sampleRandomVertices(int numVertices, Graph g, HashSet<Vertex> placedVertices){
        // edge case if there are not enough vertices to place left
        int stillToBePlaced = g.getN()- placedVertices.size();
        if (stillToBePlaced < numVertices){
            numVertices = stillToBePlaced;
        }

        HashSet<Vertex> sampledVertices = new HashSet<>();
        Random r = new Random();
        int next = r.nextInt(numVertices);
        int skipped = 0;
        for (Vertex u : g.getVertices())
        {
            if (!placedVertices.contains(u))
            {
                if (skipped < next)
                {
                    skipped++;
                    continue;
                }
                sampledVertices.add(u);
                break;
            }
        }
        return sampledVertices;
    }

    private HashSet<Coordinate> sampleRandomGrid(int width, int height, int numHorizontalPartitions, int numVerticalPartitions, int[][] usedCoordinates){
        // make sure that partitions size will be bigger or equal to 1
        if (width <= numHorizontalPartitions){ numHorizontalPartitions = width; }
        if (height <= numVerticalPartitions){ numVerticalPartitions = height; }

        int tileWidth = Math.floorDiv(width, numHorizontalPartitions);
        int tileHeight = Math.floorDiv(height, numVerticalPartitions);

        HashSet<Coordinate> randomGridCoordinates = new HashSet<>();
        int xLower = 0;
        int xUpper = tileWidth - 1;
        int yLower = 0;
        int yUpper = tileHeight - 1;
        do {
            randomGridCoordinates.add(getRandomGridCoordinate(xLower, xUpper, yLower, yUpper, usedCoordinates));
            xLower = xUpper + 1;
            yLower = yUpper + 1;
            xUpper = xUpper + tileWidth;
            if (xUpper >= width){ xUpper = width -1; }
            yUpper = yUpper + tileHeight;
            if (yUpper >= height){ yUpper = height - 1; }
        } while (xUpper < width - 1 || yUpper < height - 1 );


        return randomGridCoordinates;
    }

    private Coordinate getRandomGridCoordinate(int xLower, int xUpper, int yLower, int yUpper, int[][] usedCoordinates) {
        Random r = new Random();
        int x;
        int y;
        int trys = 0;
        Coordinate tileCoordinate;
        do {
            x = r.nextInt(xUpper + 1 - xLower) + xLower;
            y = r.nextInt(yUpper + 1 - yLower) + yLower;
            tileCoordinate = new Coordinate(x,y);
            trys = trys + 1;
        } while (usedCoordinates[x][y]!=0 || trys > 1000);

        return tileCoordinate;
    }

    private GameMove selectMove(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves, int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height, Role role) {
        int numVertices = 10;
        //TODO Define partitions depending on width and height parameter ?
        int numHorizontalPartitions = 100;
        int numVerticalPartitions = 100;
        HashSet<Vertex> sampledVertices = sampleRandomVertices(numVertices, g, placedVertices);
        HashSet<Coordinate> randomGridCoordinates = sampleRandomGrid(width, height, numHorizontalPartitions, numVerticalPartitions, usedCoordinates);

        int maxOrMinNumCrossings = 0;
        if(role == MIN)
        {
            maxOrMinNumCrossings = Integer.MAX_VALUE;
        }
        Vertex bestVertex = null;
        Coordinate bestCoordinate = null;

        for (Vertex v : sampledVertices){
            for (Coordinate c : randomGridCoordinates){
                vertexCoordinates.put(v,c);

                Graph gPrime = graphWithPlacedVertices(g, vertexCoordinates);

                CrossingCalculator cc = new CrossingCalculator(gPrime, vertexCoordinates);
                int numCrossings = cc.computeCrossingNumber();

                vertexCoordinates.remove(v,c);

                if((role == MAX && numCrossings >= maxOrMinNumCrossings) || (role == MIN && numCrossings <= maxOrMinNumCrossings )) {
                    maxOrMinNumCrossings = numCrossings;
                    bestCoordinate = c;
                    bestVertex = v;
                }
            }
        }
        return new GameMove(bestVertex, bestCoordinate);
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
    public void initializeNextRound(Graph g, int width, int height, Role role)
    {

    }

    @Override
    public String getName()
    {
        return name;
    }
}
