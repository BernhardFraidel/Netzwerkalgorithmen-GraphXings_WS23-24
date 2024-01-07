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
     * Is used to alternate between different modes for the choice of the candidate vertex
     */
    private int alternator;

    private GameMove lastRoundLastMove;

    /**
     * Creates a projection player with the assigned name.
     */
    public ProjectionPlayer() {
        this.alternator = 0;
        this.r = new Random();
        this.lastRoundLastMove = null;
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
        int modulus = 10;
        // First: Apply the last move by the opponent if there is one.
        applyLastMove(lastMove, gs);
        GameMove move = randomMove(g, gs, r, width, height);
        // If there is no placed vertex return random move
        if (gs.getPlacedVertices().isEmpty()) {
            return move;
        }

        //get a free neighbor of the previously placed vertex if there is one
        //get any free neighbor of any placed vertex else
        Vertex placedVertex = lastMove.getVertex();
        if (alternator % modulus == 1){
            move = randomMove(g, gs, r, width, height);
            lastRoundLastMove = move;
            return move;
        } else if (alternator % modulus == 2) {
            placedVertex = lastRoundLastMove.getVertex();
        }
        HashSet<Vertex> candidateNeighbors = getFreeNeighbors(placedVertex, g, gs);

        Vertex freeNeighbor;

        if (candidateNeighbors.isEmpty() || alternator % modulus == 0) {
            Map<Vertex, Vertex> placedVertexAndFreeNeighbor = getAnyFreeNeighbor(g, gs);
            placedVertex = placedVertexAndFreeNeighbor.keySet().iterator().next();
            freeNeighbor = placedVertexAndFreeNeighbor.get(placedVertex);
        } else {
            freeNeighbor = candidateNeighbors.iterator().next();
        }
        alternator++;

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
            //decide if left or right depending on position of placed vertex left or right of the middle
            xRational = new Rational(placedVertexCoordinate.getX() > middle.getX() ? width : 0);
            //intersects left and right boundary
            yRational = (placedVertexCoordinate.getX() > middle.getX()) ? b : Rational.plus(Rational.times(a, xRational), b);
        } else {
            //intersects top and bottom boundary
            //decide if top or bottom depending on position of placed vertex above or below the middle
            yRational = new Rational(placedVertexCoordinate.getY() > middle.getY() ? 0 : height);
            //yRational = a*x+b --> (yRational - b) / a = x(Rational)
            xRational = vertical ? new Rational(middle.getX()) : Rational.dividedBy(Rational.minus(yRational, b), a);
        }

        Coordinate nearestValidCoordinate = nearestValidCoordinate(xRational, yRational, width, height);
        return findClosestUnusedCoordinateMiddle(gs, nearestValidCoordinate, width, height);
    }


    @Override
    public GameMove minimizeCrossings(GameMove lastMove) {
        GameMove move;
        try {
            move = getMinimizerMove(lastMove);
        } catch (Exception e) {
            move = randomMove(g, gs, r, width, height);
        }
        gs.applyMove(move);
        return move;
    }

    @Override
    public GameMove maximizeCrossingAngles(GameMove lastMove) {
        return randomMove(g, gs, r, width, height);
    }

    @Override
    public GameMove minimizeCrossingAngles(GameMove lastMove) {
        return randomMove(g, gs, r, width, height);
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
        return "Gruppe 10";
    }
}
