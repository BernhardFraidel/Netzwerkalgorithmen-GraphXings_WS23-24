package GraphXings.Algorithms;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.Game;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameResult;

import java.util.HashMap;

public class Test_Algorithms {
    public static void Test_BentleyOttman(){
        // Create a graph g. This time it is a 10-cycle!
        Graph g = new Graph();
        //create and save first vertex
        Vertex firstVertex = new Vertex("0");
        g.addVertex(firstVertex);
        Vertex previousVertex = firstVertex;
        int numVertices = 10;

        for (int i = 1; i < numVertices; i++) {
            //create new vertex
            Vertex newVertex = new Vertex(Integer.toString(i));
            g.addVertex(newVertex);
            //create edge between new vertex and previous vertex for every second vertex to create segments
            if (i%2 == 1){
                Edge newEdge = new Edge(previousVertex, newVertex);
                g.addEdge(newEdge);
            }
            //save new vertex for next iteration
            previousVertex = newVertex;

        }

        Iterable<Vertex> u = g.getVertices();
        HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>();
        for (Vertex v : u) {
            Coordinate c = switch (v.getId()){
                // Segment 1
                case "0" -> new Coordinate(0,0);
                case "1" -> new Coordinate(3,0);
                // Segment 2
                case "2" -> new Coordinate(2,1);
                case "3" -> new Coordinate(4,1);
                // Segment 3
                case "4" -> new Coordinate(1,2);
                case "5" -> new Coordinate(4,2);
                // Segment 4
                case "6" -> new Coordinate(5,0);
                case "7" -> new Coordinate(5,2);
                // Segment 5
                case "8" -> new Coordinate(5,1);
                case "9" -> new Coordinate(5,3);
                default -> null;
            };
            vertexCoordinates.put(v,c);

        }
        BentleyOttmannCrossingCalculator bocc = new BentleyOttmannCrossingCalculator(g,vertexCoordinates);
        int numCrossings = bocc.calculate();

        CrossingCalculator cc = new CrossingCalculator(g,vertexCoordinates);
        int oldnumCrossings = cc.computeCrossingNumber();
        // Display the result!
        System.out.println("old: " + oldnumCrossings + "new: "+ numCrossings);
    }
}
