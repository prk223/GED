/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 *
 * @author Comp
 */
public class ElementDecorator extends DiagramElement
{
  private final DiagramElement element;
  private final Color color;
  
  public ElementDecorator(DiagramElement e, Color elementColor)
  {
    element = e;
    color   = elementColor;
  }
  
  public DiagramElement getElement()
  {
    return element;
  }
  
  @Override
  public void draw(Graphics g)
  {
    // Draw element with decorated color then put color back
    Color oldColor = g.getColor();
    g.setColor(color);
    element.draw(g);
    g.setColor(oldColor);
  }
  
  @Override
  public Point getLocation(){return element.getLocation();}
  
  @Override
  public void setLocation(Point loc){element.setLocation(loc);}
  
  @Override
  public String getElementType(){return element.getElementType();}

  @Override
  public String getPersistentRepresentation()
  {return element.getPersistentRepresentation();}
  
  @Override
  public double getDistanceFrom(int x, int y)
  {return element.getDistanceFrom(x,y);}
  
  @Override
  public void displayEditGui(MouseEvent evt){element.displayEditGui(evt);}
  
  @Override
  public int getMinX(){return element.getMinX();}
  
  @Override
  public int getMinY(){return element.getMinY();}
  
  @Override
  public int getMaxX(){return element.getMaxX();}
  
  @Override
  public int getMaxY(){return element.getMaxY();}
  
  @Override
  public void setUniqueId(int id){element.setUniqueId(id);}
  
  @Override
  public int getUniqueId(){return element.getUniqueId();}
  
  @Override
  public void setNearElement(DiagramElement e){element.setNearElement(e);}
  
  @Override
  public boolean isRelationship(){return element.isRelationship();}
  
  @Override
  public void alertDestroyedElement(DiagramElement e)
  {element.alertDestroyedElement(e);}
  
  @Override
  public DiagramElement cloneElement() throws IOException
  {return element.cloneElement();}
  
  @Override
  public boolean equivalentTo(DiagramElement e){return element.equivalentTo(e);}
  
  @Override
  public String accept(ElementCheckerVisitor visitor)
  {return element.accept(visitor);}
  
  @Override
  public void move(int deltaX, int deltaY){element.move(deltaX, deltaY);}
  
}
