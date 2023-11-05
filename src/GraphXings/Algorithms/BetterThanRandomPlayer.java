package GraphXings.Algorithms;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Data.Edge;
import GraphXings.Game.GameMove;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.lang.Iterable;


public class BetterThanRandomPlayer implements Player
{
    /**
     * The name of the player.
     */
    private String name;

    /**
     * Creates a player with the assigned name.
     * @param name
     */
    public BetterThanRandomPlayer(String name)
    {
        this.name = name;
    }

    @Override
    public GameMove maximizeCrossings(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves, int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height)
    {   
        return selectMove(g, vertexCoordinates, gameMoves, usedCoordinates, placedVertices, width, height, true);
    }
    

    @Override
    public GameMove minimizeCrossings(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves, int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height)
    {
        return selectMove(g, vertexCoordinates, gameMoves, usedCoordinates, placedVertices, width, height, false);
    }


private GameMove selectMove(Graph g, HashMap<Vertex, Coordinate> vertexCoordinates, List<GameMove> gameMoves, int[][] usedCoordinates, HashSet<Vertex> placedVertices, int width, int height, boolean maximize)
{
      Random r = new Random();
        int stillToBePlaced = g.getN()- placedVertices.size();
        int next = r.nextInt(stillToBePlaced);
        int skipped = 0;
        Vertex v=null;
        for (Vertex u : g.getVertices())
        {
            if (!placedVertices.contains(u))
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


        if (vertexCoordinates.size()<3)
            {         
                Random randomX = new Random();
                Random randomY = new Random();
                
                int rX = randomX.nextInt(width);
                int rY = randomY.nextInt(height);


                while(usedCoordinates[rX][rY]==1)
                {
                    rX = randomX.nextInt(width);
                    rY = randomY.nextInt(height);
                }

                Coordinate c = new Coordinate(rX, rY);
                return new GameMove(v, c);
                

            }

        else
            {   
                int maxOrMinNumCrossings = 0;
                if(!maximize)
                {
                    maxOrMinNumCrossings = Integer.MAX_VALUE;
                }
                int bestPossibleX = 0;
                int bestPossibleY = 0;

                for (int x=0; x<width; x++)
                {   


                    for(int y=0; y<height; y++)
                    {

                        if(usedCoordinates[x][y]==0)
                        {
                            Coordinate c_temp = new Coordinate(x, y);
                            vertexCoordinates.put(v,c_temp);

                            Graph gPrime = graphWithPlacedVertices(g, vertexCoordinates);

                            CrossingCalculator cc = new CrossingCalculator(gPrime, vertexCoordinates);
                            int numCrossings = cc.computeCrossingNumber();

                            vertexCoordinates.remove(v,c_temp);
                            if(maximize)
                            {   
                                if(numCrossings >= maxOrMinNumCrossings)
                                {
                                    maxOrMinNumCrossings = numCrossings;
                                    bestPossibleX = x;
                                    bestPossibleY = y;
                                }
        
                            }
        
                            else
                            {
                                if(numCrossings <= maxOrMinNumCrossings)
                                {
                                    maxOrMinNumCrossings = numCrossings;
                                    bestPossibleX = x;
                                    bestPossibleY = y;
                                }
                                        
                            } 
            
                        }

                    }
                }

            Coordinate c_final = new Coordinate(bestPossibleX, bestPossibleY);
            //System.out.printf("Coordinates found: (%d/%d)\n",bestPossibleX, bestPossibleY);
            return new GameMove(v, c_final);
        }
      
    }
    

   
   /**
    * Creates a graph with only such vertices of input graph g
    * that have already been positioned in course of the game.
    * @param g
    * @param vertexCoordinates
    * @return a graph gPrime with already placed vertices of g.
    */ 
    private Graph graphWithPlacedVertices(Graph g, HashMap<Vertex,Coordinate>vertexCoordinates)
{
    Graph gPrime = new Graph();

    for (Vertex u: g.getVertices())
    {
        if(vertexCoordinates.containsKey(u))
        {
            gPrime.addVertex(u);

            Iterable<Edge> iter = g.getIncidentEdges(u);

            for(Edge e: iter)
            {
                gPrime.addEdge(e);
            }

        }
                            
    }

    return gPrime;
}
    @Override
    public void initializeNextRound()
    {

    }


    @Override
    public String getName()
    {
        return name;
    }
}