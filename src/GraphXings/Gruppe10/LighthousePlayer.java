package GraphXings.Gruppe10;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Data.Coordinate;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import static GraphXings.Gruppe10.Util.*;

public class LighthousePlayer implements NewPlayer {
    private Graph g;
    private int width;
    private int height;
    private GameState gs;
    private ProjectionPlayer projectionPlayerMinimizer;
    private Random r;
    private int innerLoopStep;
    private int outerLoopStep;
    private int alternator;
    private Vertex previouslyPlacedVertex;
    private Set<Vertex> verticesPartitionA;
    private Set<Vertex> verticesPartitionB;
    private boolean widthIsShorter;

    @Override
    public GameMove maximizeCrossings(GameMove lastMove) {
        return null;
    }

    @Override
    public GameMove minimizeCrossings(GameMove lastMove) {
        return null;
    }

    @Override
    public GameMove maximizeCrossingAngles(GameMove lastMove) {
        applyLastMove(lastMove, gs);
        GameMove move;
        try {
            move = getMaximizeAngleMove();
        } catch (Exception e) {
            move = randomMove(g, gs, r, width, height);
        }
        gs.applyMove(move);
        return move;
    }

    private GameMove getMaximizeAngleMove() {
        Vertex v = getVertexToPlace();
        previouslyPlacedVertex = v;
        //save vertex to be placed into corresponding partition
        if (alternator % 2 == 0) {
            verticesPartitionA.add(v);
        } else {
            verticesPartitionB.add(v);
        }
        Coordinate coordinate = getCoordinate();
        GameMove move = new GameMove(v, coordinate);

        //adjust step and alternator values for next round
        alternator++;
        int modulus = (widthIsShorter ? width : height) / 3;
        if (alternator % 2 == 0) innerLoopStep++;
        outerLoopStep += innerLoopStep / modulus;
        innerLoopStep %= modulus;

        return move;
    }

    private Vertex getVertexToPlace() {
        //get a free neighbor of the previously placed vertex
        //or any free vertex in the first round
        Vertex v;
        if (previouslyPlacedVertex == null) {
            v = getAnyFreeVertex(g, gs);
        } else {
            try {
                v = getFreeNeighbors(previouslyPlacedVertex, g, gs).iterator().next();
            } catch (NoSuchElementException e) {
                v = null;
            }
        }

        //get a free neighbor of the opposing partition if the previously placed vertex has no free neighbors
        if (v == null) {
            v = getFreeNeighborOfOpposingPartition();
        }

        //fallback: get any free vertex
        if (v == null) {
            v = getAnyFreeVertex(g, gs);
        }
        return v;
    }

    private Vertex getFreeNeighborOfOpposingPartition() {
        return getAnyFreeNeighborOfVertexSet(alternator % 2 == 0 ? verticesPartitionB : verticesPartitionA, g, gs);
    }

    private Coordinate getCoordinate() {
        int x, y;
        if (widthIsShorter) {
            x = (alternator % 2) == 0 ? innerLoopStep : width - 1 - innerLoopStep;
            y = (alternator % 2) == 0 ? outerLoopStep : height - 1 - outerLoopStep;
        } else {
            x = (alternator % 2) == 0 ? outerLoopStep : width - 1 - outerLoopStep;
            y = (alternator % 2) == 0 ? innerLoopStep : height - 1 - innerLoopStep;
        }
        Coordinate coordinate = new Coordinate(x, y);
        return findClosestUnusedCoordinate(gs, coordinate, width, height);
    }

    @Override
    public GameMove minimizeCrossingAngles(GameMove lastMove) {
        return this.projectionPlayerMinimizer.minimizeCrossings(lastMove);
    }

    @Override
    public void initializeNextRound(Graph g, int width, int height, Role role) {
        this.g = g;
        this.width = width;
        this.height = height;
        this.gs = new GameState(g, width, height);
        this.r = new Random();
        this.innerLoopStep = 0;
        this.outerLoopStep = 0;
        this.widthIsShorter = width < height;
        this.verticesPartitionA = new HashSet<>();
        this.verticesPartitionB = new HashSet<>();
        if (role.equals(Role.MIN_ANGLE)) {
            this.projectionPlayerMinimizer = new ProjectionPlayer();
            this.projectionPlayerMinimizer.initializeNextRound(g, width, height, Role.MIN);
        } else {
            this.projectionPlayerMinimizer = null;
        }
    }

    @Override
    public String getName() {
        return "Gruppe 10";
    }
}
