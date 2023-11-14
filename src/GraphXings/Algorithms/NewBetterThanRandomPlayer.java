package GraphXings.Algorithms;

import GraphXings.Data.Coordinate;
import GraphXings.Data.Graph;
import GraphXings.Data.Vertex;
import GraphXings.Data.Edge;
import GraphXings.Game.GameMove;
import GraphXings.Game.GameState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.lang.Iterable;


public class NewBetterThanRandomPlayer implements NewPlayer
{
 	/**
	 * The name of the player.
	 */
	private String name;

	/**
	 * The graph to be drawn.
	 */
	private Graph g;
	/**
	 * The current state of the game;
	 */
	private GameState gs;
	/**
	 * The width of the game board.
	 */
	private int width;
	/**
	 * The height of the game board.
	 */
	private int height;
	/**
	 * Creates a player with the assigned name.
	 * @param name
	 */
    public NewBetterThanRandomPlayer(String name)
    {
        this.name = name;
    }

    @Override
    public GameMove maximizeCrossings(GameMove lastMove)
    {   
    
        if (lastMove != null)
		{
			gs.applyMove(lastMove);
		}

        GameMove newMove = selectMove(true);

        gs.applyMove(newMove);

        return newMove;
    
    }

    @Override
    public GameMove minimizeCrossings(GameMove lastMove)
    {
        if (lastMove != null)
		{
			gs.applyMove(lastMove);
		}

        GameMove newMove = selectMove(false);

        gs.applyMove(newMove);

        return newMove;
    }


private GameMove selectMove(boolean maximize)
{
      Random r = new Random();
        int stillToBePlaced = g.getN()- gs.getPlacedVertices().size();
        int next = r.nextInt(stillToBePlaced);
        int skipped = 0;
        Vertex v=null;
        for (Vertex u : g.getVertices())
        {
            if (!gs.getPlacedVertices().contains(u))
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


        if (gs.getVertexCoordinates().size()<3)
            {         
                Random randomX = new Random();
                Random randomY = new Random();
                
                int rX = randomX.nextInt(width);
                int rY = randomY.nextInt(height);


                while(gs.getUsedCoordinates()[rX][rY]==1)
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

                        if(gs.getUsedCoordinates()[x][y]==0)
                        {
                            Coordinate c_temp = new Coordinate(x, y);
                            gs.getVertexCoordinates().put(v,c_temp);

                            Graph gPrime = graphWithPlacedVertices(g, gs.getVertexCoordinates());

                            CrossingCalculator cc = new CrossingCalculator(gPrime, gs.getVertexCoordinates());
                            int numCrossings = cc.computeCrossingNumber();

                            gs.getVertexCoordinates().remove(v,c_temp);
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
    public String getName()
    {
        return name;
    }

    

    @Override
    public void initializeNextRound(Graph g, int width, int height, GraphXings.Algorithms.NewPlayer.Role role) {
      
        this.g = g;
        this.width = width;
        this.height = height;
        this.gs = new GameState(width, height);
        
    };

    
}
