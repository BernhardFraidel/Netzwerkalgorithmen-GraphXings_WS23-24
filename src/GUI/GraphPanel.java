package GUI;
import javax.swing.*;
import java.awt.*;
import GraphXings.Game.GuiGameResult;


public class GraphPanel extends JPanel 
{
    Color vertexColorPlayer1;
    Color vertexColorPlayer2;
    Color edgeColor;

    int vertexRadius;


    GuiGameResult guiResult;
    


    public GraphPanel() 
    {
        super();
        vertexColorPlayer1 = Color.red;
        vertexColorPlayer2 = Color.blue;
        edgeColor = Color.black;
        vertexRadius = 4;
    }

    public void draw(GuiGameResult guiResult)
    {
        this.guiResult = guiResult;
        repaint();
    }



    private void drawVertex(Graphics g, int x, int y, double scaleX, double scaleY, Color color)
    {
        g.setColor(color);

        int xd = (int) (x*scaleX-vertexRadius)+vertexRadius/2;
        int yd = (int) (y*scaleY-vertexRadius)+vertexRadius/2;
        

        g.fillOval(xd, yd, 2 * vertexRadius, 2 * vertexRadius);
    }


    private void drawVertices(Graphics g, double scaleX, double scaleY)
    {
        for (int i = 0; i < guiResult.vertexNum(); i++)
            {
                if (i%2 == 0)
                {
                    drawVertex(g, guiResult.vertexCoord(i).getX(), guiResult.vertexCoord(i).getY(),scaleX,scaleY,vertexColorPlayer1);
                }
                else
                {
                    drawVertex(g, guiResult.vertexCoord(i).getX(), guiResult.vertexCoord(i).getY(),scaleX,scaleY,vertexColorPlayer2);
                }
                
            }
    }


    private void drawEdge(Graphics g, int x1, int y1, int x2, int y2, double scaleX, double scaleY)
    {
        g.setColor(edgeColor);

        int x1d = (int) (x1*scaleX+vertexRadius/2);
        int y1d = (int) (y1*scaleY+vertexRadius/2);
        int x2d = (int) (x2*scaleX+vertexRadius/2);
        int y2d = (int) (y2*scaleY+vertexRadius/2);

        g.drawLine(x1d, y1d, x2d, y2d);
    }



    private void drawEdges(Graphics g, double scaleX, double scaleY)
    {
        for (int i = 0; i < guiResult.edgeNum(); i++)
            {
                drawEdge(g, guiResult.edgeCoord1(i).getX(), guiResult.edgeCoord1(i).getY(),  guiResult.edgeCoord2(i).getX(), guiResult.edgeCoord2(i).getY(),scaleX,scaleY);
            }
    }


    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);

        g.clearRect(0,0,this.getWidth(), this.getHeight());

        if (guiResult != null)
        {
            Dimension panelSize = this.getSize();

            //double scale = Math.min(panelSize.getWidth() / result.width(), panelSize.getHeight() / result.height());
            double scaleX = (panelSize.getWidth()-vertexRadius) / guiResult.width();
            double scaleY = (panelSize.getHeight()-vertexRadius) / guiResult.height();
            drawEdges(g,scaleX,scaleY);
            drawVertices(g,scaleX,scaleY);
        }
    }


    

}
