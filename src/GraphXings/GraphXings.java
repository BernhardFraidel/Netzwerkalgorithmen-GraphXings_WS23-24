package GraphXings;

import GraphXings.Algorithms.NewRandomPlayer;
import GraphXings.Data.Edge;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Game.GameInstance.GameInstance;
import GraphXings.Game.GameInstance.RandomCycleFactory;
import GraphXings.Game.NewGame;
import GraphXings.Game.NewGameResult;
import GraphXings.solutions.DistancePlayer;
import GraphXings.solutions.GridPlayer;
import GraphXings.solutions.NewBetterFasterPlayer;
import GraphXings.tests.TestBentleyOttmann;

public class GraphXings {
    public static void main(String[] args) {
        testRandomInstances();
    }

    private static void testRandomInstances() {
        RandomCycleFactory randomCycleFactory = new RandomCycleFactory();
        GameInstance gi = randomCycleFactory.getGameInstance();
        System.out.println("height: " + gi.getHeight() + " width: "+ gi.getWidth() + " #vertices: "+ gi.getG().getN());
        // Run the game with two players.
        NewGame newGame = new NewGame(gi.getG(), gi.getWidth(), gi.getHeight(), new NewRandomPlayer("Player 1"), new DistancePlayer("Player 2"));
        long t0 = System.currentTimeMillis();
        NewGameResult res = newGame.play();
        long runningTime = System.currentTimeMillis() - t0;
        // Display the result!
        System.out.println("running time: " + runningTime + "ms");
        System.out.println(res.announceResult());
    }

    private static void custom() {
        // Create a graph g. This time it is a 10-cycle!
        Graph g = new Graph();
        //create and save first vertex
        Vertex firstVertex = new Vertex("0");
        g.addVertex(firstVertex);
        Vertex previousVertex = firstVertex;
        int numVertices = 1000;

        for (int i = 1; i < numVertices; i++) {
            //create new vertex
            Vertex newVertex = new Vertex(Integer.toString(i));
            g.addVertex(newVertex);
            //create edge between new vertex and previous vertex
            Edge newEdge = new Edge(previousVertex, newVertex);
            g.addEdge(newEdge);
            //save new vertex for next iteration
            previousVertex = newVertex;
        }
        //connect last and first vertex to get cycle
        Edge newEdge = new Edge(previousVertex, firstVertex);
        g.addEdge(newEdge);

        // Run the game with two players.
        NewGame newGame = new NewGame(g, 1000, 1000, new NewRandomPlayer("Player 1"), new GridPlayer("Player 2"));
        long t0 = System.currentTimeMillis();
        NewGameResult res = newGame.play();
        long runningTime = System.currentTimeMillis() - t0;
        // Display the result!
        System.out.println("running time: " + runningTime + "ms");
        System.out.println(res.announceResult());
    }
}
