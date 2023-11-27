package GraphXings.solutions;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;

import java.util.*;

public class Util {
    /**
     * Computes a random valid move.
     * @return A random valid move.
     */
    public static GameMove randomMove(Graph g, GameState gs, Random r, int width, int height) {
        int stillToBePlaced = g.getN() - gs.getPlacedVertices().size();
        int next = r.nextInt(stillToBePlaced);
        int skipped = 0;
        Vertex v = null;
        for (Vertex u : g.getVertices()) {
            if (!gs.getPlacedVertices().contains(u)) {
                if (skipped < next) {
                    skipped++;
                    continue;
                }
                v = u;
                break;
            }
        }
        Coordinate c;
        do {
            c = new Coordinate(r.nextInt(width), r.nextInt(height));
        }
        while (gs.getUsedCoordinates()[c.getX()][c.getY()] != 0);
        return new GameMove(v, c);
    }

    /**
     * Apply the last move by the opponent if there is one.
     *
     * @param lastMove the last move the opponent made
     */
    public static void applyLastMove(GameMove lastMove, GameState gs) {
        // First: Apply the last move by the opponent if there is one.
        if (lastMove != null) {
            gs.applyMove(lastMove);
        }
    }

    /**
     * Calculate the distance between two coordinates
     * @param a coordinate a
     * @param b coordinate b
     * @return distance between a and b
     */
    public static double distance(Coordinate a, Coordinate b) {
        double ac = Math.abs(b.getY() - a.getY());
        double cb = Math.abs(b.getX() - a.getX());
        return Math.hypot(ac, cb);
    }

    /**
     * For all placed Vertices P get the Neighbourhood N(P) where n in N(P), but not in P
     * @return Map every vertex p in P to its Neighbourhood N(p) (List of Vertex)
     */
    public static HashMap<Vertex, HashSet<Vertex>> getFreeNeighborsOfPlacedVertices(Graph g, GameState gs){
        HashMap<Vertex,HashSet<Vertex>> neighborsOfPlacedVertices = new HashMap<>();
        Iterator<Vertex> placedVertices = gs.getPlacedVertices().iterator();
        //init neighborhood
        placedVertices.forEachRemaining(vertex -> neighborsOfPlacedVertices.put(vertex, new HashSet()));
        while(placedVertices.hasNext()){
            Vertex v = placedVertices.next();
            Iterable<Edge> incidentEdges = g.getIncidentEdges(v);
            for(Edge e : incidentEdges){
                Vertex n = e.getS() == v ? e.getT(): e.getS();
                if (!gs.getPlacedVertices().contains(n)) neighborsOfPlacedVertices.get(v).add(n);
            }
        }
        return neighborsOfPlacedVertices;
    }

    /**
     *
     * @param gs game state
     * @param placedVertex vertex in whose close vicinity a free coordinate id to be found
     * @return coordinate that is one of the closest free coordinates to the given placed vertex
     */
    public static Coordinate findClosestUnusedCoordinate(GameState gs, Vertex placedVertex, int width, int height) {
        Coordinate result = new Coordinate(-1, -1);
        HashMap<Vertex, Coordinate> vertexCoordinates = gs.getVertexCoordinates();
        Coordinate placedVertexCoordinate = vertexCoordinates.get(placedVertex);
        int[][] usedCoordinates = gs.getUsedCoordinates();
        int maxRange = Math.max(width, height);

        for (int i = 0; i <= maxRange; i++) {
            for (int j = -i; j <= i && j < width && j > -width ; j++) {
                for (int k = -i; k <= i && k < height && k > -height ; k++) {
                    int newX = placedVertexCoordinate.getX() + j;
                    int newY = placedVertexCoordinate.getY() + k;

                    Coordinate currentCoordinate = new Coordinate(newX, newY);

                    if (isValidCoordinate(currentCoordinate, width, height) && (usedCoordinates[newX][newY] == 0)) {
                        return currentCoordinate;
                    }
                }
            }
        }
        // Fall-back: Keine unbenutzte Koordinate gefunden
        return result;
    }

    public static boolean isValidCoordinate(Coordinate coordinate, int width, int height) {
        int x = coordinate.getX();
        int y = coordinate.getY();
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}

