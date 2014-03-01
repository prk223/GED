/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

/**
 *
 * @author Comp
 */
public class DiagramElement
{
  private Coordinate location;
  
  public class Coordinate
  {
    private int x_coord, y_coord;
    
    public Coordinate(int X, int Y)
    {
      x_coord = X;
      y_coord = Y;
    }
    
    public int getX()
    {
      return x_coord;
    }
    
    public int getY()
    {
      return y_coord;
    }
    
    public void setX(int x)
    {
      x_coord = x;
    }
    
    public void setY(int y)
    {
      y_coord = y;
    }
    
    public void setCoordinates(Coordinate c)
    {
      x_coord = c.getX();
      y_coord = c.getY();
    }
  }
  
  public DiagramElement(int x, int y)
  {
    location = new Coordinate(x, y);
  }
  
  public void draw()
  {
    
  }
  
  public Coordinate getLocation()
  {
    return location;
  }
  
  public void setLocation(Coordinate loc)
  {
    location = loc;
  }
          
  
}
