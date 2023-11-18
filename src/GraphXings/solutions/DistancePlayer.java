package GraphXings.solutions;

import GraphXings.Algorithms.Player;
import GraphXings.Algorithms.RandomPlayer;
import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.Game;
import GraphXings.Game.GameMove;

import java.util.*;

import static GraphXings.Algorithms.Player.Role.MAX;
import static GraphXings.Algorithms.Player.Role.MIN;

public class DistancePlayer implements Player {
    /**
     * The name of the player.
     */
    private String name;

    /**
     * Creates a player with the assigned name.
     * @param name name of player
     */
    public DistancePlayer(String name){ this.name = name; }

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
    public double distance(Coordinate a, Coordinate b) {
        double ac = Math.abs(b.getY() - a.getY());
        double cb = Math.abs(b.getX() - a.getX());
        return Math.hypot(ac, cb);
    }

    private Coordinate getRandomGridCoordinate(int xLower, int xUpper, int yLower, int yUpper, int[][] usedCoordinates) {
        Random r = new Random();
        int x;
        int y;
        int trys = 0;
        Coordinate tileCoordinate;
        do {
            x = Math.toIntExact(r.nextInt(xUpper + 1 - xLower) + xLower);
            y = Math.toIntExact(r.nextInt(yUpper + 1 - yLower) + yLower);
            tileCoordinate = new Coordinate(x,y);
            trys = trys + 1;
        } while (usedCoordinates[x][y]!=0 || trys > 1000);
        //TODO check null
        return tileCoordinate;
    }

    private HashSet<Coordinate> sampleRandomGrid(int width, int height, int numHorizontalPartitions, int numVerticalPartitions, int[][] usedCoordinates){
        // make sure that partitions size will be bigger or equal to 1
        if (width <= numHorizontalPartitions){ numHorizontalPartitions = width; }
        if (height <= numVerticalPartitions){ numVerticalPartitions = height; }

        int tileWidth = Math.floorDiv(width, numHorizontalPartitions);
        int tileHeight = Math.floorDiv(height, numVerticalPartitions);

        HashSet<Coordinate> randomGridCoordinates = new HashSet<>();
        int xLower = 0;
        int xUpper = 0; //tileWidth == 1 ? 1 : tileWidth - 1;
        int yLower = 0;
        int yUpper = 0; //tileHeight == 1 ? 1 : tileHeight - 1;
        for (xLower = 0; xLower < width -1; xLower = xUpper){
            xUpper = xUpper + tileWidth;
            if (xUpper >= width){ xUpper = width - 1; }
            yUpper = 0;
            for (yLower = 0; yLower < height -1; yLower = yUpper){
                yUpper = yUpper + tileHeight;
                if (yUpper >= height){ yUpper = height - 1; }
                Coordinate gridCoordinate = getRandomGridCoordinate(xLower, xUpper, yLower, yUpper, usedCoordinates);
                // skip if no free coordinate is found
                if (gridCoordinate == null){ continue; }
                randomGridCoordinates.add(gridCoordinate);
            }
        }
        return randomGridCoordinates;
    }

    private GameMove selectMove(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves, int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height, Role role) {
        int numNeighbors = 2;
        int numHorizontalPartitions = 150;
        int numVerticalPartitions = 150;
        // if there is no vertex choose at random
        if (placedVertices.isEmpty()){
            RandomPlayer randomPlayer = new RandomPlayer("rand");
            GameMove move = switch(role){
                case MAX -> randomPlayer.maximizeCrossings(g,vertexCoordinates,gameMoves,usedCoordinates,placedVertices,width,height);
                case MIN -> randomPlayer.minimizeCrossings(g,vertexCoordinates,gameMoves,usedCoordinates,placedVertices,width,height);
            };
            return move;
        }
        // get numNeighbors Neighbors
        HashMap<Vertex,List<Vertex>> neighbors = new HashMap<>();
        Iterator<Vertex> placedVerticesIterator = placedVertices.iterator();
        //System.out.println("placedverticesIterator: "+ placedVerticesIterator.hasNext());
        int i = 0;
        while ( i < numNeighbors ){
            //System.out.println("i: "+ i);
            //System.out.println("placedverticesIterator: "+ placedVerticesIterator.hasNext());
            if (!placedVerticesIterator.hasNext()){ break;}
            Vertex placedVertex = placedVerticesIterator.next();
            Iterable<Edge> incidentEdges = g.getIncidentEdges(placedVertex);
            List<Vertex> otherNeighbors = new ArrayList<>();
            for (Edge e: incidentEdges){
                Vertex s = e.getS();
                Vertex t = e.getT();
                if (s.equals(placedVertex) && !placedVertices.contains(t) ){
                     otherNeighbors.add(t);
                     neighbors.put(placedVertex, otherNeighbors);
                    i++;
                } else if (t.equals(placedVertex) && !placedVertices.contains(s) ){
                    otherNeighbors.add(s);
                    neighbors.put(placedVertex, otherNeighbors);
                    i++;
                }
            }
        }
        //System.out.println("neighbours: "+ neighbors.keySet());
        HashSet<Coordinate> randomGridCoordinates = sampleRandomGrid(width, height, numHorizontalPartitions, numVerticalPartitions, usedCoordinates);


        RandomPlayer randomPlayer = new RandomPlayer("rand");
        GameMove bestMove = switch(role){
            case MAX -> randomPlayer.maximizeCrossings(g,vertexCoordinates,gameMoves,usedCoordinates,placedVertices,width,height);
            case MIN -> randomPlayer.minimizeCrossings(g,vertexCoordinates,gameMoves,usedCoordinates,placedVertices,width,height);
        };
        int minMaxNumCrossings = role == MAX ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        // n root neighbours
        for (Vertex n : neighbors.keySet()){
            List<Vertex> neighborsList = neighbors.get(n);
            Coordinate placedCoordinate = vertexCoordinates.get(n);
            double maxMinDistance = role == MAX ? Double.MIN_VALUE : Double.MAX_VALUE;
            HashMap<Vertex, Coordinate> vertexDistanceToNeighbor = new HashMap<>();
            // test each grid coordinate for shortest longest distance to root vertex
            //Coordinate coordinateMaxMinDistanceToRoot;
            for (Coordinate c : randomGridCoordinates){
                double distance = distance(placedCoordinate, c);
                if (role == MAX && distance > maxMinDistance){
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

    }

    @Override
    public String getName()
    {
        return name;
    }

    }
