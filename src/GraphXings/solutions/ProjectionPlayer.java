package GraphXings.solutions;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Data.Coordinate;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.Game;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;

import java.util.*;

import static GraphXings.solutions.Util.*;

public class ProjectionPlayer implements NewPlayer {
    /**
     * The name of the random player.
     */
    private final String name;
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
     * @param name name of the player
     */
    public ProjectionPlayer(String name) {
        this.name = name;
        this.r = new Random(name.hashCode());
    }

    @Override
    public GameMove maximizeCrossings(GameMove lastMove) {
        // First: Apply the last move by the opponent if there is one.
        applyLastMove(lastMove, gs);
        GameMove move = randomMove(g, gs, r, width, height);
        // If there is no placed vertex return random move
        if (gs.getPlacedVertices().isEmpty()){
            gs.applyMove(move);
            return move;
        }

        gs.applyMove(move);
        return move;
    }

    @Override
    public GameMove minimizeCrossings(GameMove lastMove) {
        // First: Apply the last move by the opponent if there is one.
        applyLastMove(lastMove, gs);
        GameMove move = randomMove(g, gs, r, width, height);
        // If there is no placed vertex return random move
        if (gs.getPlacedVertices().isEmpty()){
            gs.applyMove(move);
            return move;
        }

        HashMap<Vertex, HashSet<Vertex>> freeNeighboursOfPlacedVertices = getFreeNeighborsOfPlacedVertices(g, gs);
        Iterator<Map.Entry<Vertex, HashSet<Vertex>>> freeNeighboursIterator = freeNeighboursOfPlacedVertices.entrySet().iterator();
        Vertex placedVertex = null;
        Vertex freeVertex = null;
        int i = 0;
        while (!freeNeighboursOfPlacedVertices.entrySet().isEmpty() && freeNeighboursIterator.hasNext()){
            Map.Entry<Vertex, HashSet<Vertex>> entry = freeNeighboursIterator.next();
            placedVertex = entry.getKey();
            if (entry.getValue().iterator().hasNext()){
                freeVertex = entry.getValue().iterator().next();
                break;
            }
        }
        Coordinate closestCoordinate = findClosestUnusedCoordinate(gs, placedVertex, width, height);
        move = new GameMove(freeVertex,closestCoordinate);
        gs.applyMove(move);
        return move;
    }



    @Override
    public void initializeNextRound(Graph g, int width, int height, Role role) {
        // Store graph, width, height and create a new GameState.
        this.g = g;
        this.width = width;
        this.height = height;
        this.gs = new GameState(width, height);
    }

    @Override
    public String getName() {
        return name;
    }
}
