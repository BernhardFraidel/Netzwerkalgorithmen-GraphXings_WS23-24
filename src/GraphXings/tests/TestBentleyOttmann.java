package GraphXings.tests;

import GraphXings.solutions.crossingCalculator.BentleyOttmannCrossingCalculator;
import GraphXings.Algorithms.CrossingCalculator;
import GraphXings.Data.Coordinate;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class TestBentleyOttmann {
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
//            if (i % 2 == 1) {
                Edge newEdge = new Edge(previousVertex, newVertex);
                g.addEdge(newEdge);
//            }
            //save new vertex for next iteration
            previousVertex = newVertex;
        }

        test1(g);
        test2(g);
        test3(g);
        test4(g);
        test5(g);
//        test6(g);
//        test7(g);
//        test8(g);
        test9(g);
        test10(g);
//        randomTest(g, width, height);
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

    private static void test6(Graph g) {
        HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>();
        for (Vertex v : g.getVertices()) {
            Coordinate c = switch (v.getId()) {
                // Segment 1
                case "0" -> new Coordinate(1, 8);
                case "1" -> new Coordinate(8, 3);
                // Segment 2
                case "2" -> new Coordinate(4, 5);
                case "3" -> new Coordinate(1, 7);
                // Segment 3
                case "4" -> new Coordinate(6, 5);
                case "5" -> new Coordinate(8, 5);
                // Segment 4
                case "6" -> new Coordinate(3, 1);
                case "7" -> new Coordinate(5, 8);
                // Segment 5
                case "8" -> new Coordinate(5, 3);
                case "9" -> new Coordinate(2, 0);
                default -> null;
            };
            vertexCoordinates.put(v, c);
        }
        test("6", g, vertexCoordinates);
    }

    private static void test7(Graph g) {
        HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>();
        for (Vertex v : g.getVertices()) {
            Coordinate c = switch (v.getId()) {
                // Segment 1
                case "0" -> new Coordinate(0, 7);
                case "1" -> new Coordinate(9, 4);
                // Segment 2
                case "2" -> new Coordinate(3, 0);
                case "3" -> new Coordinate(5, 8);
                // Segment 3
                case "4" -> new Coordinate(4, 4);
                case "5" -> new Coordinate(0, 5);
                // Segment 4
                case "6" -> new Coordinate(4, 3);
                case "7" -> new Coordinate(9, 3);
                // Segment 5
                case "8" -> new Coordinate(6, 4);
                case "9" -> new Coordinate(2, 7);
                default -> null;
            };
            vertexCoordinates.put(v, c);
        }
        test("7", g, vertexCoordinates);
    }

    private static void test8(Graph g) {
        HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>();
        for (Vertex v : g.getVertices()) {
            Coordinate c = switch (v.getId()) {
                // Segment 1
                case "0" -> new Coordinate(5, 0);
                case "1" -> new Coordinate(7, 2);
                // Segment 2
                case "2" -> new Coordinate(9, 8);
                case "3" -> new Coordinate(8, 5);
                // Segment 3
                case "4" -> new Coordinate(4, 5);
                case "5" -> new Coordinate(1, 5);
                // Segment 4
                case "6" -> new Coordinate(8, 6);
                case "7" -> new Coordinate(3, 3);
                // Segment 5
                case "8" -> new Coordinate(4, 6);
                case "9" -> new Coordinate(4, 3);
                default -> null;
            };
            vertexCoordinates.put(v, c);
        }
        test("8", g, vertexCoordinates);
    }

    private static void test9(Graph g) {
        HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>();
        for (Vertex v : g.getVertices()) {
            Coordinate c = switch (v.getId()) {
                // Segment 1
                case "0" -> new Coordinate(5, 0);
                case "1" -> new Coordinate(5, 5);
                // Segment 2
                case "2" -> new Coordinate(6, 9);
                case "3" -> new Coordinate(1, 2);
                // Segment 3
                case "4" -> new Coordinate(5, 4);
                case "5" -> new Coordinate(6, 5);
                // Segment 4
                case "6" -> new Coordinate(8, 3);
                case "7" -> new Coordinate(1, 3);
                // Segment 5
                case "8" -> new Coordinate(7, 3);
                case "9" -> new Coordinate(5, 3);
                default -> null;
            };
            vertexCoordinates.put(v, c);
        }
        test("9", g, vertexCoordinates);
    }

    private static void test10(Graph g) {
        HashMap<Vertex, Coordinate> vertexCoordinates = new HashMap<>();
        for (Vertex v : g.getVertices()) {
            Coordinate c = switch (v.getId()) {
                // Segment 1
                case "0" -> new Coordinate(8, 8);
                case "1" -> new Coordinate(0, 5);
                // Segment 2
                case "2" -> new Coordinate(7, 6);
                case "3" -> new Coordinate(3, 8);
                // Segment 3
                case "4" -> new Coordinate(9, 5);
                case "5" -> new Coordinate(5, 7);
                // Segment 4
                case "6" -> new Coordinate(5, 1);
                case "7" -> new Coordinate(2, 8);
                // Segment 5
                case "8" -> new Coordinate(5, 0);
                case "9" -> new Coordinate(7, 9);
                default -> null;
            };
            vertexCoordinates.put(v, c);
        }
        test("10", g, vertexCoordinates);
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
//        assert oldNumCrossings == bentleyOttmannCrossings : "Bentley-Ottmann fehlerhaft!";
    }
}
