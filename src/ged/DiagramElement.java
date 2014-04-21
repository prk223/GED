/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;

/**
 *
 * @author Comp
 */
public interface DiagramElement
{
  
  public void draw(Graphics g);
  
  public Point getLocation();
  
  public void setLocation(Point loc);
  
  public String getElementType();

  public String getPersistentRepresentation();
  
  public double getDistanceFrom(int x, int y);
  
  public void displayEditGui();
  
  public int getMaxX();
  
  public int getMaxY();
  
  public void setUniqueId(int id);
  
  public int getUniqueId();
  
  public void setNearElement(DiagramElement e);
  
  public boolean isRelationship();
  
  public void alertDestroyedElement(DiagramElement e);
  
  public DiagramElement cloneElement() throws IOException;
  
  public boolean equivalentTo(DiagramElement e);
  
  public String accept(ElementCheckerVisitor visitor);
  
}
