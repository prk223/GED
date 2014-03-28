/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Point;

/**
 *
 * @author Comp
 */
public class TetherElementData
{
  private DiagramElement element;
  private float percent_down;
  private float percent_right;
  int elemX; // cache element's location
  int elemY;
  int elemWidth;
  int elemHeight;
  Point calculated_point; // Cache point instead of calculating always

  public TetherElementData(DiagramElement e, Point p)
  {
    if(e == null || p == null)
    {
      System.err.println("ERR:TetherElementData NULL in constructor!");
    }
    else
    {
      element = e;
      calculatePercentages(e.getLocation(), p);
      calculatePoint();
    }
  }

  public Point getPoint()
  {
    Point elemLoc = element.getLocation();
    int width = element.getMaxX() - elemLoc.x;
    int height = element.getMaxY() - elemLoc.y;
    Point p;

    // If something changed, recalculate the point
    if((elemLoc.x != elemX) || (elemLoc.y != elemY) ||
        (width != elemWidth) || (height != elemHeight))
    {
      calculatePoint();
    }
    p = new Point(calculated_point.x, calculated_point.y);

    return p;
  }

  public void changePoint(DiagramElement e, Point p)
  {
    element = e;
    calculatePercentages(e.getLocation(), p);
    calculatePoint();
  }

  public int getElementUniqueId()
  {
    return element.getUniqueId();
  }

  private void calculatePercentages(Point elemLoc, Point p)
  {
    elemLoc = element.getLocation();
    int width = Math.abs(element.getMaxX() - elemLoc.x);
    int height = Math.abs(element.getMaxY() - elemLoc.y);
    if(height < 1) height = 1;
    if(width < 1) width = 1;

    percent_right = (float)((p.x - elemLoc.x)) / width;
    percent_down = (float)((p.y - elemLoc.y)) / height;
  }

  private void calculatePoint()
  {
    Point elemLoc = element.getLocation();
    // Cache values to avoid recalculation later
    elemX = elemLoc.x;
    elemY = elemLoc.y;
    elemWidth = Math.abs(element.getMaxX() - elemLoc.x);
    elemHeight = Math.abs(element.getMaxY() - elemLoc.y);

    int xCoord = elemX;
    int yCoord = elemY;

    xCoord += elemWidth * percent_right;
    yCoord += elemHeight * percent_down;

    calculated_point = new Point(xCoord, yCoord);
  }
}
