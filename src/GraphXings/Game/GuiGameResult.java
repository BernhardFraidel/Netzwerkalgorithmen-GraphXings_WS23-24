package GraphXings.Game;

import GraphXings.Data.*;
import java.util.*;
import java.util.Vector; 
public class GuiGameResult 
{
    private int numVertices;
    private int numEdges;

    private int width;
    private int height;

    private Vector<Coordinate> vertexCoords;
    private Vector<Coordinate> edgeCoords1;
    private Vector<Coordinate> edgeCoords2;

    String maximizerPlayer;
    String minimizerPlayer;


    public GuiGameResult(Graph g, GameState state, String maximizer, String minimizer)
    {
        //HashSet<Vertex> vertices = (HashSet<Vertex>) g.getVertices();
        LinkedHashSet<Vertex> vertices = (LinkedHashSet<Vertex>) state.getPlacedVertices();
        HashSet<Edge> edges = (HashSet<Edge>) g.getEdges();
        LinkedHashMap<Vertex, Coordinate> vertexCoordinates = (LinkedHashMap<Vertex, Coordinate>)state.getVertexCoordinates();

        this.maximizerPlayer = maximizer;
        this.minimizerPlayer = minimizer;

        calcCoords(vertices, edges, vertexCoordinates);
        recalculateWidthHeight();
    }

    public GuiGameResult(Graph g, GameState state)
    {
        //HashSet<Vertex> vertices = (HashSet<Vertex>) g.getVertices();
        LinkedHashSet<Vertex> vertices = (LinkedHashSet<Vertex>) state.getPlacedVertices();
        HashSet<Edge> edges = (HashSet<Edge>) g.getEdges();
        LinkedHashMap<Vertex, Coordinate> vertexCoordinates = (LinkedHashMap<Vertex, Coordinate>)state.getVertexCoordinates();

        this.maximizerPlayer = "Maximizer not set";
        this.minimizerPlayer = "Minimizer not set";

        calcCoords(vertices, edges, vertexCoordinates);
        recalculateWidthHeight();
    }

    private void calcCoords(LinkedHashSet<Vertex> vertices, HashSet<Edge> edges, LinkedHashMap<Vertex, Coordinate> vertexCoordinates)
    {
        vertexCoords = new Vector<Coordinate>();
        edgeCoords1 = new Vector<Coordinate>();
        edgeCoords2 = new Vector<Coordinate>();

        for (Vertex vertex : vertices) 
        {
            this.vertexCoords.add(vertexCoordinates.get(vertex));
        }

        for (Edge edge : edges) 
        {
            if (vertexCoordinates.containsKey(edge.getS()) && vertexCoordinates.containsKey(edge.getT()))
            {
                this.edgeCoords1.add(vertexCoordinates.get(edge.getS()));
                this.edgeCoords2.add(vertexCoordinates.get(edge.getT()));
            }
            
        }

        numVertices = vertexCoordinates.size();
        numEdges = edgeCoords1.size();
    }

    private void recalculateWidthHeight()
    {
        width = 0;
        height = 0;
        for (Coordinate coordinate : this.vertexCoords)
        {
            if (coordinate != null)
            {
                if (coordinate.getX() > width) width = coordinate.getX();
                if (coordinate.getY() > height) height = coordinate.getY();
            }
            else
            {
                System.out.println("GuiGameResult::recalculateWidthHeight(): coordinate has value null");
            }
            
        }
    }

    public int vertexNum()
    {
        return numVertices;
    }

    public int edgeNum()
    {
        return numEdges;
    }


    public int width()
    {
        return width;
    }

    public int height()
    {
        return height;
    }

    public Coordinate vertexCoord(int i)
    {
        return vertexCoords.elementAt(i);
    }

    public Coordinate edgeCoord1(int i)
    {
        return edgeCoords1.elementAt(i);
    }

     public Coordinate edgeCoord2(int i)
    {
        return edgeCoords2.elementAt(i);
    }
}
