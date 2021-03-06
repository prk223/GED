/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.io.IOException;

/**
 *
 * @author Comp
 */
public class InheritanceRelationship extends Relationship
{
  // Cache some variables to avoid excess calculations
  private Polygon triangle;
  private final Point prev_last_vertex;
  private final Point prev_destination;
  
  public InheritanceRelationship(int x, int y) throws IOException
  {
    super(x, y);
    
    prev_last_vertex = new Point(0,0);
    prev_destination = new Point(0,0);
    int[] triangleX = {0,1,2};
    int[] triangleY = {3,4,5};
    triangle = new Polygon(triangleX, triangleY, 3);
  }
  
  @Override
  public String getElementType()
  {
    return "Inheritance";
  }
  
  @Override
  protected void drawEndpoints(Graphics g)
  {
    int destX = destination_location.x;
    int destY = destination_location.y;
    
    Point last_vertex = getLastVertex();
    // Check if vertex or destination changed
    if((prev_last_vertex.x != last_vertex.x)          ||
       (prev_last_vertex.y != last_vertex.y)          ||
       (prev_destination.x != destination_location.x) ||
       (prev_destination.y != destination_location.y))
    { // If there is a change, re-calculate triangle
      prev_last_vertex.x = last_vertex.x;
      prev_last_vertex.y = last_vertex.y;
      prev_destination.x = destination_location.x;
      prev_destination.y = destination_location.y;

      double toDestAngle = getAngleToDestination();
      int x1 = destX + (int)(Math.cos(toDestAngle) * (symbol_size / 2));
      int y1 = destY + (int)(Math.sin(toDestAngle) * (symbol_size / 2));
      int x2 = destX - (int)(Math.cos(toDestAngle) * (symbol_size / 2));
      int y2 = destY - (int)(Math.sin(toDestAngle) * (symbol_size / 2));
      int x3 = destX + (int)(Math.sin(toDestAngle) * symbol_size);
      int y3 = destY - (int)(Math.cos(toDestAngle) * symbol_size);

      int[] x = {x1, x2, x3};
      int[] y = {y1, y2, y3};
      triangle = new Polygon(x, y, 3);
    }

    g.drawPolygon(triangle);
  }
  
  @Override
  public void drawLine(Graphics g, Point A, Point B)
  {
    ClassElement e = getDestinationClass();
    if(e != null && e.getInterface())
    {
      drawDashedLine(g, A, B);
    }
    else
      g.drawLine(A.x, A.y, B.x, B.y);
  }
  
  public static DiagramElement fromPersistentRepresentation(String s) throws IOException
  {
    InheritanceRelationship r = new InheritanceRelationship(0, 0);
    return fromPersistentRepresentation(s, r);
  }
  
  @Override
  public DiagramElement cloneElement() throws IOException
  {
    InheritanceRelationship clonedInher = new InheritanceRelationship(0, 0);
    
    int[] x = triangle.xpoints;
    int[] clonedX = {x[0], x[1], x[2]};
    int[] y = triangle.ypoints;
    int[] clonedY = {y[0], y[1], y[2]};
    
    clonedInher.triangle = new Polygon(clonedX, clonedY, 3);
    clonedInher.prev_last_vertex.x = prev_last_vertex.x;
    clonedInher.prev_last_vertex.y = prev_last_vertex.y;
    clonedInher.prev_destination.x = prev_destination.x;
    clonedInher.prev_destination.y = prev_destination.y;
    
    return cloneRelationship(clonedInher);
  }
  
  @Override
  public String accept(ElementCheckerVisitor visitor)
  {
    return visitor.visit(this);
  }
}
