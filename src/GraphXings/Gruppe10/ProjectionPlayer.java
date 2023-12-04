package GraphXings.Gruppe10;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Data.Coordinate;
import GraphXings.Data.Graph;
import GraphXings.Data.Rational;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import static GraphXings.Gruppe10.Util.*;

public class ProjectionPlayer implements NewPlayer {
    /**
     * A random number generator.
     */
    private final Random r;
    /**
     * The graph to be drawn.
     */
    private Graph g;
    /**
     * The current state of the game;
     */
    private GameState gs;
    /**
     * The width of the game board.
     */
    private int width;
    /**
     * The height of the game board.
     */
    private int height;

    /**
     * Creates a projection player with the assigned name.
     */
    public ProjectionPlayer() {
        this.r = new Random();
    }

    @Override
    public GameMove maximizeCrossings(GameMove lastMove) {
        GameMove move;
        try {
            move = getMaximizerMove(lastMove);
        } catch (Exception e) {
            move = randomMove(g, gs, r, width, height);
        }
        gs.applyMove(move);
        return move;
    }

    private GameMove getMaximizerMove(GameMove lastMove) throws Exception {
        // First: Apply the last move by the opponent if there is one.
        applyLastMove(lastMove, gs);
        GameMove move = randomMove(g, gs, r, width, height);
        // If there is no placed vertex return random move
        if (gs.getPlacedVertices().isEmpty()) {
            return move;
        }

        //get a free neighbor of the previously placed vertex if there is one
        //get any free neighbor of any placed vertex else
        HashSet<Vertex> candidateNeighbors = getFreeNeighbors(lastMove.getVertex(), g, gs);
        Vertex freeNeighbor;
        Vertex placedVertex = lastMove.getVertex();
        if (candidateNeighbors.isEmpty()) {
            Map<Vertex, Vertex> placedVertexAndFreeNeighbor = getAnyFreeNeighbor(g, gs);
            placedVertex = placedVertexAndFreeNeighbor.keySet().iterator().next();
            freeNeighbor = placedVertexAndFreeNeighbor.get(placedVertex);
        } else {
            freeNeighbor = candidateNeighbors.iterator().next();
        }


        //find projection through the middle
        Coordinate newCoordinate = getProjectionOnBorderThroughMiddle(placedVertex);

        if (!isValidCoordinate(newCoordinate, width, height) || gs.getUsedCoordinates()[newCoordinate.getX()][newCoordinate.getY()] != 0) {
            throw new Exception();
        }
        move = new GameMove(freeNeighbor, newCoordinate);
        return move;
    }

    private Coordinate getProjectionOnBorderThroughMiddle(Vertex placedVertex) {
        Rational xRational;
        Rational yRational;
        Coordinate placedVertexCoordinate = gs.getVertexCoordinates().get(placedVertex);
        Coordinate middle = new Coordinate(width / 2, height / 2);
        //y = a*x + b
        boolean vertical = middle.getX() == placedVertexCoordinate.getX();
        Rational a;
        Rational b;
        if (vertical) {
            a = null;
            b = null;
        } else {
            int deltaY = middle.getY() - placedVertexCoordinate.getY();
            int deltaX = middle.getX() - placedVertexCoordinate.getX();
            a = new Rational(deltaY, deltaX);
            b = Rational.minus(new Rational(middle.getY()), Rational.times(a, new Rational(middle.getX())));
        }

        Rational zero = new Rational(0);
        Rational heightRational = new Rational(height);
        if (!vertical && (!Rational.lesserEqual(b, zero) || Rational.equals(b, zero)) && Rational.lesserEqual(b, heightRational)) {
            //intersects left and right boundary
            yRational = b;
            //decide if left or right depending on position of placed vertex left or right of the middle
            xRational = new Rational(placedVertexCoordinate.getX() > middle.getX() ? width : 0);
        } else {
            //intersects top and bottom boundary
            //decide if top or bottom depending on position of placed vertex above or below the middle
            yRational = new Rational(placedVertexCoordinate.getY() > middle.getY() ? 0 : height);
            //yRational = a*x+b --> (yRational - b) / a = x(Rational)
            xRational = vertical ? new Rational(middle.getX()) : Rational.dividedBy(Rational.minus(yRational, b), a);
        }

        Coordinate nearestValidCoordinate = nearestValidCoordinate(xRational, yRational, width, height);
        return findClosestUnusedCoordinate(gs, nearestValidCoordinate, width, height);
    }


    @Override
    public GameMove minimizeCrossings(GameMove lastMove) {
        GameMove move;
        try {
            move = getMinimizerMove(lastMove);
        } catch (Exception e) {
            System.err.println("Exception! Performing random move...");
            move = randomMove(g, gs, r, width, height);
        }
        gs.applyMove(move);
        return move;
    }

    private GameMove getMinimizerMove(GameMove lastMove) {
        // First: Apply the last move by the opponent if there is one.
        applyLastMove(lastMove, gs);
        GameMove move = randomMove(g, gs, r, width, height);
        // If there is no placed vertex return random move
        if (gs.getPlacedVertices().isEmpty()) {
            return move;
        }

        HashMap<Vertex, HashSet<Vertex>> freeNeighboursOfPlacedVertices = getFreeNeighborsOfPlacedVertices(g, gs);
        Vertex placedVertex = null;
        Vertex freeVertex = null;
        int i = 0;
        for (Map.Entry<Vertex, HashSet<Vertex>> entry : freeNeighboursOfPlacedVertices.entrySet()) {
            placedVertex = entry.getKey();
            if (entry.getValue().iterator().hasNext()) {
                freeVertex = entry.getValue().iterator().next();
                break;
            }
        }
        Coordinate closestCoordinate = findClosestUnusedCoordinate(gs, placedVertex, width, height);
        move = new GameMove(freeVertex, closestCoordinate);
        return move;
    }


    @Override
    public void initializeNextRound(Graph g, int width, int height, Role role) {
        // Store graph, width, height and create a new GameState.
        this.g = g;
        this.width = width;
        this.height = height;
        this.gs = new GameState(g, width, height);
    }

    @Override
    public String getName() {
        return "Group 10 - ProjectionPlayer";
    }
}
