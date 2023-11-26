package GraphXings.solutions;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Algorithms.NewRandomPlayer;
import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.Game;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;

import java.util.*;

import static GraphXings.Algorithms.NewPlayer.Role.MAX;
import static GraphXings.Algorithms.NewPlayer.Role.MIN;

public class DistancePlayer implements NewPlayer {
    /**
     * The name of the player.
     */
    private String name;
    private Graph g;
    private int height;
    private int width;
    private Role role;
    private GameState gameState;
    private Random r = new Random();
    /**
     * Creates a player with the assigned name.
     * @param name name of player
     */
    public DistancePlayer(String name){ this.name = name; }

    @Override
    public GameMove maximizeCrossings(GameMove lastMove) { return selectMove(lastMove); }

    @Override
    public GameMove minimizeCrossings(GameMove lastMove) { return selectMove(lastMove); }

    /**
     * Calculate the distance between two coordinates
     * @param a coordinate a
     * @param b coordinate b
     * @return distance between a and b
     */
    public double distance(Coordinate a, Coordinate b) {
        double ac = Math.abs(b.getY() - a.getY());
        double cb = Math.abs(b.getX() - a.getX());
        return Math.hypot(ac, cb);
    }

    /**
     * Get one random free coordinate from each tile in a imagined grid over the given playing field
     *
     * @param width                   width of the playing field
     * @param height                  height of the playing field
     * @param numHorizontalPartitions horizontal granularity of the gird
     * @param numVerticalPartitions   vertical granularity of the grid
     * @return Set of free coordinates, one coordinate for each tile in the grid.
     */
    private HashSet<Coordinate> sampleRandomGrid(int width, int height, int numHorizontalPartitions, int numVerticalPartitions) {
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
                Optional<Coordinate> gridCoordinate = getRandomGridCoordinate(xLower, xUpper, yLower, yUpper);
                // skip if no free coordinate is found
                if (gridCoordinate.isEmpty()) continue;
                randomGridCoordinates.add(gridCoordinate.get());
            }
        }
        return randomGridCoordinates;
    }

    /**
     * given a range of coordinates return one random coordinate in the range that is still free.
     * If there are no more free coordinates empty will be returned
     * @return optionally empty random coordinate
     */
    private Optional<Coordinate> getRandomGridCoordinate(int xLower, int xUpper, int yLower, int yUpper) {
        int x;
        int y;
        Coordinate tileCoordinate = null;
        int tries = 0;
        int numCoordinatesInTile = 5 * (xUpper - xLower) * (yUpper - yLower);

        do {
            x = Math.toIntExact(r.nextInt(xUpper + 1 - xLower) + xLower);
            y = Math.toIntExact(r.nextInt(yUpper + 1 - yLower) + yLower);
            tries++;
            if (gameState.getUsedCoordinates()[x][y] == 0) {
                tileCoordinate = new Coordinate(x, y);
                break;
            }
        } while (tries < numCoordinatesInTile);

        return Optional.ofNullable(tileCoordinate);
    }

    /**
     * For all placed Vertices P get the Neighbourhood N(P) where n in N(P), but not in P
     * @return Map every vertex p in P to its Neighbourhood N(p)
     */
    private HashMap<Vertex, List<Vertex>> getFreeNeighborsOfPlacedVertices(){
        HashMap<Vertex,List<Vertex>> neighborsOfPlacedVertices = new HashMap<>();
        // TODO output wieder entfernen oder besser machen
        if (gameState.getPlacedVertices().isEmpty()) System.out.println("There are no placed vertices to get neighbours");

        Iterator<Vertex> placedVertices = gameState.getPlacedVertices().iterator();
        while(placedVertices.hasNext()){
            Vertex v = placedVertices.next();
            Iterable<Edge> incidentEdges = g.getIncidentEdges(v);
            for(Edge e : incidentEdges){
                Vertex n = e.getS() == v ? e.getT(): e.getS();
                if (!gameState.getPlacedVertices().contains(n)) neighborsOfPlacedVertices.get(v).add(n);
            }
        }
        return neighborsOfPlacedVertices;
    }

    private GameMove randomMove(GameMove lastMove){
        //NewRandomPlayer newRandomPlayer = new NewRandomPlayer("rand");
        //newRandomPlayer.initializeNextRound(g,width,height,role);
        //GameMove move = switch(role){
        //    case MAX -> newRandomPlayer.maximizeCrossings(lastMove);
        //    case MIN -> newRandomPlayer.minimizeCrossings(lastMove);
        //};
        //return move;

        int stillToBePlaced = g.getN()- gameState.getPlacedVertices().size();
        int next = r.nextInt(stillToBePlaced);
        int skipped = 0;
        Vertex v=null;
        for (Vertex u : g.getVertices())
        {
            if (!gameState.getPlacedVertices().contains(u))
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
        Coordinate c;
        do
        {
            c = new Coordinate(r.nextInt(width),r.nextInt(height));
        }
        while (gameState.getUsedCoordinates()[c.getX()][c.getY()]!=0);
        return new GameMove(v,c);

    }
    private GameMove selectMove(GameMove lastMove) {
        // First: Apply the last move by the opponent if there is one.
        if (lastMove != null) {
            gameState.applyMove(lastMove);
        }

        int numHorizontalPartitions = 50;
        int numVerticalPartitions = 50;

        GameMove bestMove = randomMove(lastMove);

        //double minMaxDistance = 0;
        //minMaxDistance = (role == MAX) ? Double.MIN_VALUE : Double.MAX_VALUE;

        double minMaxCrossings = 0;
        minMaxCrossings = (role == MAX) ? Double.MIN_VALUE : Double.MAX_VALUE;

        // if there is no placed vertex choose at random
        if (gameState.getPlacedVertices().isEmpty()){
            gameState.applyMove(bestMove);
            return bestMove;
        }

        // Set of possible coordinates to place a vertex.
        HashSet<Coordinate> candidateCoordinates = sampleRandomGrid(width, height, numHorizontalPartitions,numVerticalPartitions);
        // the number of coordinates to be compared for each candidate vertex
        int numCoordinatesToCompare = 2;

        HashMap<Vertex,List<Vertex>> freeNeighborsOfPlacedVertices = new HashMap<>();
        freeNeighborsOfPlacedVertices = getFreeNeighborsOfPlacedVertices();
        Iterator<Map.Entry<Vertex,List<Vertex>>> iterator = freeNeighborsOfPlacedVertices.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Vertex,List<Vertex>> entry = iterator.next();
            Vertex         placedVertex = entry.getKey();
            List<Vertex> freeNeighbours = entry.getValue();
            for (Vertex freeNeighbour : freeNeighbours){
                HashMap<Coordinate, Double> distanceToCandidateCoordinates = getDistances(placedVertex, candidateCoordinates);
                // Get numCoordinatesToCompare many coordinates that have the maximum/minimum distance to the placed coordinate
                HashMap<Coordinate,Double> minMaxDistanceOfCoordinates = getMinMaxDistanceOfCoordinates(distanceToCandidateCoordinates, numCoordinatesToCompare);
                // Find best coordinate by comparing the crossing number between the candidates
                GameMove move = getMinMaxCrossingsOfCoordinates(minMaxDistanceOfCoordinates.keySet(), freeNeighbour);
                gameState.applyMove(move);
                return move;
            }
        }
        gameState.applyMove(bestMove);
        return bestMove;
    }

    private GameMove getMinMaxCrossingsOfCoordinates(Set<Coordinate> coordinates, Vertex freeNeighbour) {
        int minOrMax = role.equals(MAX) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Coordinate candidate = null;
        for (Coordinate c : coordinates) {
            //place vertex
            gameState.getVertexCoordinates().put(freeNeighbour, c);

            //calculate new crossing number
            Graph gPrime = graphWithPlacedVertices(g, gameState.getVertexCoordinates());
            BentleyOttmannCrossingCalculatorLite cc = new BentleyOttmannCrossingCalculatorLite(gPrime, gameState.getVertexCoordinates());
            int numCrossings = cc.calculate();
            if ((role.equals(MAX) && numCrossings > minOrMax) || (role.equals(MIN) && numCrossings < minOrMax)) {
                minOrMax = numCrossings;
                candidate = c;
            }

            //remove placed vertex to reset game state
            gameState.getVertexCoordinates().remove(freeNeighbour, c);
        }

        return new GameMove(freeNeighbour, candidate);
    }

    /**
     *
     * @param distanceToCandidateCoordinates already computed distance from placed neighbour for coordinate
     * @param numCoordinatesToCompare what the name imply
     * @return numCoordinatesToCompare many coordinates, that are closest for MIN and farthest away from neighbour for MAX
     */
    private HashMap<Coordinate, Double> getMinMaxDistanceOfCoordinates(HashMap<Coordinate, Double> distanceToCandidateCoordinates, int numCoordinatesToCompare) {
        // Create a list from the entries of the input map
        List<Map.Entry<Coordinate, Double>> entryList = new ArrayList<>(distanceToCandidateCoordinates.entrySet());

        // Sort the list using a comparator based on values
        entryList.sort((role.equals(MAX) ? Map.Entry.comparingByValue() : Map.Entry.<Coordinate, Double>comparingByValue().reversed()));
        // Create a new map to store the result
        HashMap<Coordinate, Double> result = new HashMap<>();

        // Copy the desired number of entries based on the role
        for (int i = 0; i < numCoordinatesToCompare && i < entryList.size(); i++) {
            Map.Entry<Coordinate, Double> entry = entryList.get(i);
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * @param placedVertex vertex from which distance is measured
     * @param candidateCoordinates positions to which the distance is measured
     * @return set of distances for each candidate coordinates to the already placed vertex
     */
    private HashMap<Coordinate, Double> getDistances(Vertex placedVertex, HashSet<Coordinate> candidateCoordinates) {
        HashMap<Coordinate, Double> distanceToCandidateCoordinates = new HashMap<>();
        for (Coordinate candidateCoordinate : candidateCoordinates){
            Coordinate placedVertexCoordinate = gameState.getVertexCoordinates().get(placedVertex);
            distanceToCandidateCoordinates.put(candidateCoordinate, distance(candidateCoordinate,placedVertexCoordinate));
        }
        return distanceToCandidateCoordinates;
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
        this.g = g;
        this.width = width;
        this.height = height;
        this.role = role;
        this.gameState = new GameState(width, height);
    }

    @Override
    public String getName()
    {
        return name;
    }

    }
