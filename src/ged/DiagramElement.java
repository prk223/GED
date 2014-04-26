/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 *
 * @author Comp
 */
public abstract class DiagramElement
{
  
  public abstract void draw(Graphics g);
  
  public abstract Point getLocation();
  
  public abstract void setLocation(Point loc);
  
  public abstract String getElementType();

  public abstract String getPersistentRepresentation();
  
  public abstract double getDistanceFrom(int x, int y);
  
  public abstract void displayEditGui(MouseEvent evt);
  
  public abstract int getMinX();
  
  public abstract int getMinY();
  
  public abstract int getMaxX();
  
  public abstract int getMaxY();
  
  public abstract void setUniqueId(int id);
  
  public abstract int getUniqueId();
  
  public abstract void setNearElement(DiagramElement e);
  
  public abstract boolean isRelationship();
  
  public abstract void alertDestroyedElement(DiagramElement e);
  
  public abstract DiagramElement cloneElement() throws IOException;
  
  public abstract boolean equivalentTo(DiagramElement e);
  
  public abstract String accept(ElementCheckerVisitor visitor);
  
  public abstract void move(int deltaX, int deltaY);
  
}
