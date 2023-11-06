package GraphXings.Algorithms;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class TestAlgorithms {
    public static void testBentleyOttman(int numVertices, int width, int height){
        // Create a graph g. This time it is a 10-cycle!
        Graph g = new Graph();
        //create and save first vertex
        Vertex firstVertex = new Vertex("0");
        g.addVertex(firstVertex);
        Vertex previousVertex = firstVertex;
        //int numVertices = 10;

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
        System.out.println("Test 1: old: " + oldnumCrossings + " new: "+ numCrossings);



        vertexCoordinates = new HashMap<>();
        for (Vertex v : u) {
            Coordinate c = switch (v.getId()){
                // Segment 1
                case "0" -> new Coordinate(0,0);
                case "1" -> new Coordinate(2,2);
                // Segment 2
                case "2" -> new Coordinate(0,2);
                case "3" -> new Coordinate(2,0);
                // Segment 3
                case "4" -> new Coordinate(4,0);
                case "5" -> new Coordinate(6,2);
                // Segment 4
                case "6" -> new Coordinate(4,2);
                case "7" -> new Coordinate(6,0);
                // Segment 5
                case "8" -> new Coordinate(4,1);
                case "9" -> new Coordinate(6,1);
                default -> null;
            };
            vertexCoordinates.put(v,c);

        }
        bocc = new BentleyOttmannCrossingCalculator(g,vertexCoordinates);
        numCrossings = bocc.calculate();

        cc = new CrossingCalculator(g,vertexCoordinates);
        oldnumCrossings = cc.computeCrossingNumber();
        // Display the result!
        System.out.println("Test 2: old: " + oldnumCrossings + " new: "+ numCrossings);




        vertexCoordinates = new HashMap<>();
        int[][] usedCoordinates = new int[width][height];
        for(int i = 0; i < usedCoordinates.length; i++) {
            for(int j = 0; j < usedCoordinates[i].length; j++) {
                usedCoordinates[i][j] = 0;
            }
        }
        Coordinate c = new Coordinate(0,0);
        for (Vertex v : u){
            Random r =  new Random();
            do
            {
                c = new Coordinate(r.nextInt(width),r.nextInt(height));
            }
            while (usedCoordinates[c.getX()][c.getY()]!=0);
            usedCoordinates[c.getX()][c.getY()] = 1;
            vertexCoordinates.put(v,c);
            System.out.println("id: "+ v.getId() +" X: " + c.getX() + " Y: "+ c.getY());
        }

        bocc = new BentleyOttmannCrossingCalculator(g,vertexCoordinates);
        numCrossings = bocc.calculate();

        cc = new CrossingCalculator(g,vertexCoordinates);
        oldnumCrossings = cc.computeCrossingNumber();
        // Display the result!
        System.out.println("Random Test: old: " + oldnumCrossings + " new: "+ numCrossings);


    }
}
