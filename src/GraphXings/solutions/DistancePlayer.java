package GraphXings.solutions;

import GraphXings.Algorithms.NewPlayer;
import GraphXings.Algorithms.NewRandomPlayer;
import GraphXings.Algorithms.RandomPlayer;
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

    public double distance(Coordinate a, Coordinate b) {
        double ac = Math.abs(b.getY() - a.getY());
        double cb = Math.abs(b.getX() - a.getX());
        return Math.hypot(ac, cb);
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
        int x;
        int y;
        Coordinate tileCoordinate = null;
        int tries = 0;
        int numCoordinatesInTile = 5 * (xUpper - xLower) * (yUpper - yLower);

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

    /**
     *
     * @return get the neighbors to all placed vertices with a list to the already placed vertices that are connected to the neighbor
     */
    private HashMap<Vertex, List<Vertex>> getNeighborsOfPlacedVertices (){
        HashMap<Vertex,List<Vertex>> neighborsOfPlacedVertices = new HashMap<>();
        Iterator<Vertex> placedVertices = gameState.getPlacedVertices().iterator();
        while(placedVertices.hasNext()){
            Vertex v = placedVertices.next();
            Iterable<Edge> incidentEdges = g.getIncidentEdges(v);
            for(Edge e : incidentEdges){
                Vertex n = e.getS() == v ? e.getT(): e.getS();
                neighborsOfPlacedVertices.get(v).add(n);
            }
        }
        return neighborsOfPlacedVertices;
    }

    private GameMove randomMove(GameMove lastMove){
        NewRandomPlayer newRandomPlayer = new NewRandomPlayer("rand");
        GameMove move = switch(role){
            case MAX -> newRandomPlayer.maximizeCrossings(lastMove);
            case MIN -> newRandomPlayer.minimizeCrossings(lastMove);
        };
        return move;
    }
    private GameMove selectMove(GameMove lastMove) {
        // First: Apply the last move by the opponent if there is one.
        if (lastMove != null) {
            gameState.applyMove(lastMove);
        }

        int numHorizontalPartitions = 50;
        int numVerticalPartitions = 50;

        // if there is no placed vertex choose at random
        if (gameState.getPlacedVertices().isEmpty()){
            GameMove randomMove = randomMove(lastMove);
            gameState.applyMove(randomMove);
            return randomMove;
        }

        int numNeighbors = 2;
        // get numNeighbors Neighbors
        HashMap<Vertex,List<Vertex>> neighbors = new HashMap<>();
        neighbors = getNeighborsOfPlacedVertices();
        neighbors.size();

        //GameMove move = new GameMove(bestVertex, bestCoordinate);
        //gameState.applyMove(move);
        //return move;







        Iterator<Vertex> placedVerticesIterator = gameState.getPlacedVertices().iterator();
        int i = 0;
        while ( i < numNeighbors ){
            if (!placedVerticesIterator.hasNext()){ break;}
            Vertex placedVertex = placedVerticesIterator.next();
            Iterable<Edge> incidentEdges = g.getIncidentEdges(placedVertex);
            List<Vertex> otherNeighbors = new ArrayList<>();
            for (Edge e: incidentEdges){
                Vertex s = e.getS();
                Vertex t = e.getT();
                if (s.equals(placedVertex) && !gameState.getPlacedVertices().contains(t) ){
                     otherNeighbors.add(t);
                     neighbors.put(placedVertex, otherNeighbors);
                    i++;
                } else if (t.equals(placedVertex) && !gameState.getPlacedVertices().contains(s) ){
                    otherNeighbors.add(s);
                    neighbors.put(placedVertex, otherNeighbors);
                    i++;
                }
            }
        }
        //System.out.println("neighbours: "+ neighbors.keySet());
        HashSet<Coordinate> randomGridCoordinates = sampleRandomGrid(width, height, numHorizontalPartitions, numVerticalPartitions, gameState.getUsedCoordinates());
        NewRandomPlayer newRandomPlayer = new NewRandomPlayer("rand");
        GameMove bestMove = switch(role){
            case MAX -> newRandomPlayer.maximizeCrossings(lastMove);
            case MIN -> newRandomPlayer.minimizeCrossings(lastMove);
        };
        int minMaxNumCrossings = role == MAX ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        // n root neighbours
        for (Vertex n : neighbors.keySet()){
            List<Vertex> neighborsList = neighbors.get(n);
            Coordinate placedCoordinate = gameState.getVertexCoordinates().get(n);
            double maxMinDistance = role == MAX ? Double.MIN_VALUE : Double.MAX_VALUE;
            HashMap<Vertex, Coordinate> vertexDistanceToNeighbor = new HashMap<>();
            // test each grid coordinate for shortest longest distance to root vertex
            //Coordinate coordinateMaxMinDistanceToRoot;
            for (Coordinate c : randomGridCoordinates){
                double distance = distance(placedCoordinate, c);
                if (role == MAX  && distance > maxMinDistance){
                    maxMinDistance = distance;
                    //coordinateMaxMinDistanceToRoot = c;
                    bestMove = new GameMove(neighborsList.get(0),c);
                } else if (role == MIN && distance < maxMinDistance) {
                    maxMinDistance = distance;
                    //coordinateMaxMinDistanceToRoot = c;
                    bestMove = new GameMove(neighborsList.get(0),c);
                }
            }


            // find num crossings for each vertex that is closest/farthest. Select move depending on min/max crossing num
            //for (Vertex v : neighborsList){
            //    Coordinate c_temp = coordinateMaxMinDistanceToRoot;
            //    vertexCoordinates.put(v,c_temp);
            //    Graph gPrime = graphWithPlacedVertices(g, vertexCoordinates);
            //    CrossingCalculator cc = new CrossingCalculator(gPrime, vertexCoordinates);
//
            //    int numCrossings = cc.computeCrossingNumber();
//
            //    System.out.println("t");
            //    vertexCoordinates.remove(v,c_temp);
//
            //    if (role == MAX && numCrossings > minMaxNumCrossings){
            //        minMaxNumCrossings = numCrossings;
            //        bestMove = new GameMove(v,c_temp);
            //    } else if (role == MIN && numCrossings < minMaxNumCrossings) {
            //        minMaxNumCrossings = numCrossings;
            //        bestMove = new GameMove(v,c_temp);
            //    }

            //}

            // for each neighbour of a root neighbour
            //for (Vertex v : neighborsList){
            //    // test each grid coordinate for shortest longest distance
            //    for (Coordinate c : randomGridCoordinates){
            //        double distance = distance(placedCoordinate, c);
            //        if (role == MAX && distance > maxMinDistance){
            //            maxMinDistance = distance;
            //            vertexDistanceToNeighbor.put(v,c);
            //        } else if (role == MIN && distance > maxMinDistance) {
            //            maxMinDistance = distance;
            //            vertexDistanceToNeighbor.put(v,c);
            //        }
            //    }
            //}

        }
        return bestMove;
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
