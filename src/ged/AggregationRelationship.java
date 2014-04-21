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
public class AggregationRelationship extends Relationship
{
  // Cache some variables to avoid excess calculations
  private Polygon diamond;
  private final Point prev_last_vertex;
  private final Point prev_destination;
  
  public AggregationRelationship(int x, int y) throws IOException
  {
    super(x, y);
    
    prev_last_vertex = new Point(0,0);
    prev_destination = new Point(0,0);
    int[] diamondX = {0,1,2,3};
    int[] diamondY = {4,5,6,7};
    diamond = new Polygon(diamondX, diamondY, 4);
  }
  
  @Override
  public String getElementType()
  {
    return "Aggregation";
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
    { // If there is a change, re-calculate diamond
      prev_last_vertex.x = last_vertex.x;
      prev_last_vertex.y = last_vertex.y;
      prev_destination.x = destination_location.x;
      prev_destination.y = destination_location.y;

      double toDestAngle = getAngleToDestination();
      int x1 = destX;
      int y1 = destY;
      int x2 = destX + (int)(-Math.cos(toDestAngle) * (symbol_size / 2)) +
                       (int)( Math.sin(toDestAngle) * (symbol_size / 2));
      int y2 = destY + (int)(-Math.cos(toDestAngle) * (symbol_size / 2)) +
                       (int)(-Math.sin(toDestAngle) * (symbol_size / 2));
      int x3 = destX + (int)( Math.sin(toDestAngle) *  symbol_size);
      int y3 = destY + (int)(-Math.cos(toDestAngle) *  symbol_size);
      int x4 = destX + (int)( Math.cos(toDestAngle) * (symbol_size / 2)) +
                       (int)( Math.sin(toDestAngle) * (symbol_size / 2));
      int y4 = destY + (int)(-Math.cos(toDestAngle) * (symbol_size / 2)) +
                       (int)( Math.sin(toDestAngle) * (symbol_size / 2));

      int[] x = {x1, x2, x3, x4};
      int[] y = {y1, y2, y3, y4};
      diamond = new Polygon(x, y, 4);
    }

    g.drawPolygon(diamond);
  }
  
  public static DiagramElement fromPersistentRepresentation(String s) throws IOException
  {
    AggregationRelationship r = new AggregationRelationship(0, 0);
    return fromPersistentRepresentation(s, r);
  }
  
  @Override
  public DiagramElement cloneElement() throws IOException
  {
    AggregationRelationship clonedAg = new AggregationRelationship(0, 0);
    
    clonedAg.prev_last_vertex.x = prev_last_vertex.x;
    clonedAg.prev_last_vertex.y = prev_last_vertex.y;
    clonedAg.prev_destination.x = prev_destination.x;
    clonedAg.prev_destination.y = prev_destination.y;
    
    int[] x = diamond.xpoints;
    int[] y = diamond.ypoints;
    int[] newX = {x[0], x[1], x[2], x[3]};
    int[] newY = {y[0], y[1], y[2], y[3]};
    clonedAg.diamond = new Polygon(newX, newY, 4);
    
    return cloneRelationship(clonedAg);
  }
  
  @Override
  public String accept(ElementCheckerVisitor visitor)
  {
    return visitor.visit(this);
  }
}
