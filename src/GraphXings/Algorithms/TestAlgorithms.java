package GraphXings.Algorithms;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class TestAlgorithms {
    public static void testBentleyOttman(int numVertices, int width, int height) {
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
            if (i % 2 == 1) {
                Edge newEdge = new Edge(previousVertex, newVertex);
                g.addEdge(newEdge);
            }
            //save new vertex for next iteration
            previousVertex = newVertex;
        }

//        test1(g);
//        test2(g);
//        test3(g);
//        test4(g);
//        test5(g);
        randomTest(g, width, height);
    }

    private static void test1(Graph g) {
        HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>();
        for (Vertex v : g.getVertices()) {
            Coordinate c = switch (v.getId()) {
                // Segment 1
                case "0" -> new Coordinate(0, 0);
                case "1" -> new Coordinate(3, 0);
                // Segment 2
                case "2" -> new Coordinate(2, 1);
                case "3" -> new Coordinate(4, 1);
                // Segment 3
                case "4" -> new Coordinate(1, 2);
                case "5" -> new Coordinate(4, 2);
                // Segment 4
                case "6" -> new Coordinate(5, 0);
                case "7" -> new Coordinate(5, 2);
                // Segment 5
                case "8" -> new Coordinate(5, 1);
                case "9" -> new Coordinate(5, 3);
                default -> null;
            };
            vertexCoordinates.put(v, c);

        }
        test("1", g, vertexCoordinates);
    }

    private static void test2(Graph g) {
        HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>();
        for (Vertex v : g.getVertices()) {
            Coordinate c = switch (v.getId()) {
                // Segment 1
                case "0" -> new Coordinate(0, 0);
                case "1" -> new Coordinate(2, 2);
                // Segment 2
                case "2" -> new Coordinate(0, 2);
                case "3" -> new Coordinate(2, 0);
                // Segment 3
                case "4" -> new Coordinate(4, 0);
                case "5" -> new Coordinate(6, 2);
                // Segment 4
                case "6" -> new Coordinate(4, 2);
                case "7" -> new Coordinate(6, 0);
                // Segment 5
                case "8" -> new Coordinate(4, 1);
                case "9" -> new Coordinate(6, 1);
                default -> null;
            };
            vertexCoordinates.put(v, c);
        }
        test("2", g, vertexCoordinates);
    }

    private static void test3(Graph g) {
        HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>();
        for (Vertex v : g.getVertices()) {
            Coordinate c = switch (v.getId()) {
                // Segment 1
                case "0" -> new Coordinate(0, 7);
                case "1" -> new Coordinate(2, 0);
                // Segment 2
                case "2" -> new Coordinate(0, 3);
                case "3" -> new Coordinate(8, 6);
                // Segment 3
                case "4" -> new Coordinate(0, 5);
                case "5" -> new Coordinate(8, 2);
                // Segment 4
                case "6" -> new Coordinate(1, 6);
                case "7" -> new Coordinate(7, 4);
                // Segment 5
                case "8" -> new Coordinate(5, 1);
                case "9" -> new Coordinate(8, 5);
                default -> null;
            };
            vertexCoordinates.put(v, c);
        }
        test("3", g, vertexCoordinates);
    }

    private static void test4(Graph g) {
        HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>();
        for (Vertex v : g.getVertices()) {
            Coordinate c = switch (v.getId()) {
                // Segment 1
                case "0" -> new Coordinate(5, 9);
                case "1" -> new Coordinate(7, 0);
                // Segment 2
                case "2" -> new Coordinate(4, 8);
                case "3" -> new Coordinate(1, 8);
                // Segment 3
                case "4" -> new Coordinate(7, 9);
                case "5" -> new Coordinate(6, 2);
                // Segment 4
                case "6" -> new Coordinate(8, 9);
                case "7" -> new Coordinate(2, 1);
                // Segment 5
                case "8" -> new Coordinate(7, 6);
                case "9" -> new Coordinate(0, 6);
                default -> null;
            };
            vertexCoordinates.put(v, c);
        }
        test("4", g, vertexCoordinates);
    }

    private static void test5(Graph g) {
        HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>();
        for (Vertex v : g.getVertices()) {
            Coordinate c = switch (v.getId()) {
                // Segment 1
                case "0" -> new Coordinate(6, 8);
                case "1" -> new Coordinate(6, 1);
                // Segment 2
                case "2" -> new Coordinate(2, 3);
                case "3" -> new Coordinate(1, 9);
                // Segment 3
                case "4" -> new Coordinate(5, 4);
                case "5" -> new Coordinate(3, 9);
                // Segment 4
                case "6" -> new Coordinate(3, 7);
                case "7" -> new Coordinate(7, 0);
                // Segment 5
                case "8" -> new Coordinate(9, 1);
                case "9" -> new Coordinate(5, 0);
                default -> null;
            };
            vertexCoordinates.put(v, c);
        }
        test("5", g, vertexCoordinates);
    }

    private static void randomTest(Graph g, int width, int height) {
        HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>();
        int[][] usedCoordinates = new int[width][height];
        for (int[] usedCoordinate : usedCoordinates) {
            Arrays.fill(usedCoordinate, 0);
        }
        Coordinate c;
        for (Vertex v : g.getVertices()) {
            Random r = new Random();
            do {
                c = new Coordinate(r.nextInt(width), r.nextInt(height));
            } while (usedCoordinates[c.getX()][c.getY()] != 0);
            usedCoordinates[c.getX()][c.getY()] = 1;
            vertexCoordinates.put(v, c);
            System.out.println("id: " + v.getId() + " X: " + c.getX() + " Y: " + c.getY());
        }
        test("random", g, vertexCoordinates);
    }


    private static void test(String testName, Graph g, HashMap<Vertex, Coordinate> vertexCoordinates) {
        BentleyOttmannCrossingCalculator bocc = new BentleyOttmannCrossingCalculator(g, vertexCoordinates);
        int bentleyOttmannCrossings = bocc.calculate();

        CrossingCalculator cc = new CrossingCalculator(g, vertexCoordinates);
        int oldNumCrossings = cc.computeCrossingNumber();
        // Display the result!
        System.out.println("Test " + testName + ": old: " + oldNumCrossings + " new: " + bentleyOttmannCrossings);
        assert oldNumCrossings == bentleyOttmannCrossings : "Bentley-Ottmann fehlerhaft!";
    }
}
