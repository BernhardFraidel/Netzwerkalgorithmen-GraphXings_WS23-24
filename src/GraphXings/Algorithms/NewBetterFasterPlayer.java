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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.lang.Iterable;


public class NewBetterFasterPlayer implements NewPlayer 
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
     * A random number generator.
	 */
	private Random r;
	/**
	 * Creates a player with the assigned name.
	 * @param name
	 */
    public NewBetterFasterPlayer(String name)
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
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<GameMove> future = executor.submit(() -> {

        this.r = new Random();
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
                
                int rX = r.nextInt(width);
                int rY = r.nextInt(height);


                while(gs.getUsedCoordinates()[rX][rY]==1)
                {
                    rX = r.nextInt(width);
                    rY = r.nextInt(height);
                }

                Coordinate c = new Coordinate(rX, rY);
                return new GameMove(v, c);
                

            }

        else
            { 
                {
                int maxOrMinNumCrossings = 0;
                if(!maximize)
                {
                    maxOrMinNumCrossings = Integer.MAX_VALUE;
                }
                int bestPossibleX = 0;
                int bestPossibleY = 0;

                int x = 0;
                int y = 0;
                int shiftX = 1;
                int shiftY = 0;
                int length = 0;
                Boolean coordinatesFound = false;

                if(!maximize)
                {
                    for(int screenEdge = 0; screenEdge<4; ++screenEdge)
                    {
                        if(shiftX == 1)
                        {
                            length = Math.round(width/3);
                        }

                        else
                        {
                            length = Math.round(height/3);
                        }

                        

                        for(int i=1; i<length; ++i)
                        {    
                            
                            if(gs.getUsedCoordinates()[x][y]==0)
                            {   
                                Coordinate c_temp = new Coordinate(x, y);
                                gs.getVertexCoordinates().put(v,c_temp);

                                Graph gPrime = graphWithPlacedNeighbourVertices(v, gs.getVertexCoordinates());

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

                                        coordinatesFound = true;
                                    }
                                            
                                } 
                
                            }

                            int flippedDirection = shiftX;
                            shiftX = -shiftY;
                            shiftY = flippedDirection;
                        }


                    }

            
                    if(!coordinatesFound)
                        {
                            return randomMove();
                        }
            }

            else
                {
                    for (x=Math.round(width/3); x<Math.round((2*width)/3); x++)
                    {   
    
    
                        for(y=Math.round(height/3); y<Math.round((2*height)/3); y++)
                        {
    
                            if(gs.getUsedCoordinates()[x][y]==0)
                            {
                                Coordinate c_temp = new Coordinate(x, y);
                                gs.getVertexCoordinates().put(v,c_temp);
    
                                Graph gPrime = graphWithPlacedNeighbourVertices(v, gs.getVertexCoordinates());
    
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

                          if(!coordinatesFound)
                        {
                            return randomMove();
                        }
                }

            Coordinate c_final = new Coordinate(bestPossibleX, bestPossibleY);
            //System.out.printf("Coordinates found: (%d/%d)\n",bestPossibleX, bestPossibleY);
            return new GameMove(v, c_final);
        }
    
    }

});

GameMove selectedMove;
try {
    // Set a timeout of 4.9 minutes (294,000 milliseconds)
    selectedMove = future.get(270000 , TimeUnit.MILLISECONDS);
} catch (InterruptedException | ExecutionException | TimeoutException e) {
    // If timeout occurs, call the method that returns a random move
   selectedMove = randomMove();
} finally {
    // Make sure to shut down the executor
    future.cancel(true);
    executor.shutdownNow(); }

    return selectedMove;
}

       

    
    

   
   /** 
    * Creates a graph with only such vertices of input graph g
    * that have already been positioned in course of the game and
    * are neighbours of the placed vertex v.
    * @param v 
    * @param vertexCoordinates
    * @return a graph gPrime with already placed vertices of g.
*/
    private Graph graphWithPlacedNeighbourVertices(Vertex v, HashMap<Vertex,Coordinate>vertexCoordinates)
{
    Graph gPrime = new Graph();

        if(vertexCoordinates.containsKey(v))
        {
            gPrime.addVertex(v);

            Iterable<Edge> iter = g.getIncidentEdges(v);

            for(Edge e: iter)
            {
                gPrime.addEdge(e);
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

    private GameMove randomMove()
	{
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
		Coordinate c;
		do
		{
			c = new Coordinate(r.nextInt(width),r.nextInt(height));
		}
		while (gs.getUsedCoordinates()[c.getX()][c.getY()]!=0);
		return new GameMove(v,c);
	}

    
}
