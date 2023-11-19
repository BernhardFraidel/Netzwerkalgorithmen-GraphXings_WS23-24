package GraphXings.solutions;

import GraphXings.Algorithms.CrossingCalculator;
import GraphXings.Algorithms.NewPlayer;
import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;

import static GraphXings.Algorithms.NewPlayer.Role.MAX;
import static GraphXings.Algorithms.NewPlayer.Role.MIN;

public class GridPlayer implements NewPlayer {
    /**
     * The name of the player.
     */
    private final String name;
    private Graph g;
    private int height;
    private int width;
    private Role role;
    private GameState gameState;

    /**
     * Creates a player with the assigned name.
     *
     * @param name name of player
     */
    public GridPlayer(String name) {
        this.name = name;
    }


    /**
     * @param numVertices    number of randomly selected vertices
     * @param g              Graph of current game
     * @param placedVertices already placed vertices
     * @return A list of numVertices many not yet placed random vertices
     */
    private HashSet<Vertex> sampleRandomVertices(int numVertices, Graph g, HashSet<Vertex> placedVertices) {
        // edge case if there are not enough vertices to place left
        int stillToBePlaced = g.getN() - placedVertices.size();
        if (stillToBePlaced < numVertices) {
            numVertices = stillToBePlaced;
        }

        HashSet<Vertex> sampledVertices = new HashSet<>();
        Random r = new Random();
        int randomInt = r.nextInt(numVertices);
        int skipped = 0;
        for (Vertex u : g.getVertices()) {
            if (!placedVertices.contains(u)) {
                if (skipped < randomInt) {
                    skipped++;
                    continue;
                }
                sampledVertices.add(u);
                numVertices--;
                if (numVertices == 0) break;
            }
        }
        return sampledVertices;
    }

    private HashSet<Coordinate> sampleRandomGrid(int width, int height, int numHorizontalPartitions, int numVerticalPartitions, int[][] usedCoordinates) {
        // make sure that partitions size will be bigger or equal to 1
        if (width <= numHorizontalPartitions) {
            numHorizontalPartitions = width;
        }
        if (height <= numVerticalPartitions) {
            numVerticalPartitions = height;
        }

        int tileWidth = Math.floorDiv(width, numHorizontalPartitions);
        int tileHeight = Math.floorDiv(height, numVerticalPartitions);

        HashSet<Coordinate> randomGridCoordinates = new HashSet<>();
        int xLower = 0;
        int xUpper = 0;
        int yLower = 0;
        int yUpper = 0;
        for (xLower = 0; xLower < width - 1; xLower = xUpper) {
            xUpper = xUpper + tileWidth;
            if (xUpper >= width) {
                xUpper = width - 1;
            }
            yUpper = 0;
            for (yLower = 0; yLower < height - 1; yLower = yUpper) {
                yUpper = yUpper + tileHeight;
                if (yUpper >= height) {
                    yUpper = height - 1;
                }
                Optional<Coordinate> gridCoordinate = getRandomGridCoordinate(xLower, xUpper, yLower, yUpper, usedCoordinates);
                // skip if no free coordinate is found
                if (gridCoordinate.isEmpty()) continue;
                randomGridCoordinates.add(gridCoordinate.get());
            }
        }
        return randomGridCoordinates;
    }

    private Optional<Coordinate> getRandomGridCoordinate(int xLower, int xUpper, int yLower, int yUpper, int[][] usedCoordinates) {
        Random r = new Random();
        int x;
        int y;
        Coordinate tileCoordinate = null;
        int tries = 0;
        int numCoordinatesInTile = 1000;

        do {
            x = Math.toIntExact(r.nextInt(xUpper + 1 - xLower) + xLower);
            y = Math.toIntExact(r.nextInt(yUpper + 1 - yLower) + yLower);
            tries++;
            if (usedCoordinates[x][y] == 0) {
                tileCoordinate = new Coordinate(x, y);
                break;
            }
        } while (tries < numCoordinatesInTile);

        return Optional.ofNullable(tileCoordinate);
    }

    private GameMove selectMove(GameMove lastMove) {

        // First: Apply the last move by the opponent if there is one.
        if (lastMove != null) {
            gameState.applyMove(lastMove);
        }

        int numVertices = 5;
        //TODO Define partitions depending on width and height parameter ?
        int numHorizontalPartitions = 50;
        int numVerticalPartitions = 50;
        HashSet<Vertex> sampledVertices = sampleRandomVertices(numVertices, g, gameState.getPlacedVertices());
        HashSet<Coordinate> randomGridCoordinates = sampleRandomGrid(width, height, numHorizontalPartitions, numVerticalPartitions, gameState.getUsedCoordinates());

        int maxOrMinNumCrossings = 0;
        if (role == MIN) {
            maxOrMinNumCrossings = Integer.MAX_VALUE;
        }
        Vertex bestVertex = null;
        Coordinate bestCoordinate = null;
        for (Vertex v : sampledVertices) {
            for (Coordinate c : randomGridCoordinates) {
                gameState.getVertexCoordinates().put(v, c);

                Graph gPrime = graphWithPlacedVertices(g, gameState.getVertexCoordinates());

                CrossingCalculator cc = new CrossingCalculator(gPrime, gameState.getVertexCoordinates());
                int numCrossings = cc.computeCrossingNumber();

                gameState.getVertexCoordinates().remove(v, c);
                if (role == MIN && numCrossings == 0) {
                    GameMove move = new GameMove(v, c);
                    gameState.applyMove(move);
                    return move;
                }
                if ((role == MAX && numCrossings >= maxOrMinNumCrossings) || (role == MIN && numCrossings <= maxOrMinNumCrossings)) {
                    maxOrMinNumCrossings = numCrossings;
                    bestCoordinate = c;
                    bestVertex = v;
                }
            }
        }
        GameMove move = new GameMove(bestVertex, bestCoordinate);
        gameState.applyMove(move);
        return move;
    }


    /**
     * Creates a graph with only such vertices of input graph g
     * that have already been positioned in course of the game.
     *
     * @return a graph gPrime with already placed vertices of g.
     */
    private Graph graphWithPlacedVertices(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates) {
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

    public double distance(Coordinate a, Coordinate b) {
        double ac = Math.abs(b.getY() - a.getY());
        double cb = Math.abs(b.getX() - a.getX());
        return Math.hypot(ac, cb);
    }

    @Override
    public GameMove maximizeCrossings(GameMove lastMove) {
        return selectMove(lastMove);
    }

    @Override
    public GameMove minimizeCrossings(GameMove lastMove) {
        return selectMove(lastMove);
    }

    @Override
    public void initializeNextRound(Graph g, int width, int height, Role role) {
        this.g = g;
        this.width = width;
        this.height = height;
        this.role = role;
        this.gameState = new GameState(width, height);
    }

    @Override
    public String getName() {
        return name;
    }
}
