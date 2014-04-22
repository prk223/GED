/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import static ged.Util.getValueFromTag;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;

/**
 *
 * @author Comp
 */
public class AssociationRelationship extends Relationship
{
  private String association;
  private final Point association_location;
  private FontMetrics metrics;
  
  public AssociationRelationship(int x, int y) throws IOException
  {
    super(x, y);
    association = "";
    association_location = new Point(0, 0);
    metrics = null;
  }
  
  @Override
  public String getElementType()
  {
    return "Association";
  }
  
  public String getAssociation()
  {
    return association;
  }
  
  public void setAssociation(String ass, int x, int y)
  {
    if(!ass.equals(association))
    {
      association = ass;
      setAssociationLocation(x, y);
    }
  }
  
  public void setAssociationLocation(int x, int y)
  {
    association_location.x = x;
    association_location.y = y;
  }
  
  @Override
  public void draw(Graphics g)
  {
    super.draw(g);
    g.drawString(association, association_location.x, association_location.y);
    metrics = g.getFontMetrics();
  }
  
  @Override
  protected void drawLine(Graphics g, Point A, Point B)
  {
    g.drawLine(A.x, A.y, B.x, B.y);
  }
  
  @Override
  public void setLocation(Point loc)
  {
    if(selected_point == association_location)
    {
      setAssociationLocation(loc.x, loc.y);
    }
    else if(selected_point == null)
    {
      int distX = loc.x - source_location.x;
      int distY = loc.y - source_location.y;
      association_location.x += distX;
      association_location.y += distY;
      super.setLocation(loc);
    }
    else
      super.setLocation(loc);
  }
  
  @Override
  public double getDistanceFrom(int x, int y)
  {
    double distance = -1.0;
    // Check if association text was selected
    if(metrics != null)
    {
      int textMinX = association_location.x;
      int textMaxX = association_location.x + metrics.stringWidth(association);
      int textMinY = association_location.y - metrics.getHeight();
      int textMaxY = association_location.y;
      
      if((x <= textMaxX) && (x >= textMinX) &&
         (y <= textMaxY) && (y >= textMinY))
      {
        distance = 0.0;
        selected_point = association_location;
      }
    }
    if(distance < 0.0)
    {
      distance = super.getDistanceFrom(x, y);
    }
    
    return distance;
  }
  
  @Override
  public String getPersistentRepresentation()
  {
    String rep = super.getPersistentRepresentation();
    
    rep += "<assString>"+association+"</assString>";
    rep += "<assX>"+association_location.x+"</assX>";
    rep += "<assY>"+association_location.y+"</assY>";
    
    return rep;
  }
  
  public static DiagramElement fromPersistentRepresentation(String s) throws IOException
  {
    AssociationRelationship r = new AssociationRelationship(0, 0);
    
    r.association = getValueFromTag(s, "assString");
    String coord  = getValueFromTag(s, "assX");
    if(!coord.equals(""))
      r.association_location.x = Integer.parseInt(coord);
     coord = getValueFromTag(s, "assY");
    if(!coord.equals(""))
      r.association_location.y = Integer.parseInt(coord);
    
    return fromPersistentRepresentation(s, r);
  }
  
  @Override
  public DiagramElement cloneElement() throws IOException
  {
    AssociationRelationship clonedAss = new AssociationRelationship(0, 0);
    clonedAss.association = association;
    clonedAss.association_location.x = association_location.x;
    clonedAss.association_location.y = association_location.y;
    return cloneRelationship(clonedAss);
  }
  
  @Override
  public boolean equivalentTo(DiagramElement e)
  {
    boolean equal = true;
    if(e.getElementType().equals(getElementType()))
    {
      AssociationRelationship a = (AssociationRelationship)e;
      if(!a.getAssociation().equals(getAssociation())) equal = false;
      if(a.association_location.x != association_location.x) equal = false;
      if(a.association_location.y != association_location.y) equal = false;
    }
    else
      equal = false;
    
    return equal && super.equivalentTo(e);
  }
  
  @Override
  public String accept(ElementCheckerVisitor visitor)
  {
    return visitor.visit(this);
  }
}
