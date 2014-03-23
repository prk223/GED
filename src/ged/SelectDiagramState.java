/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.JViewport;

/**
 *
 * @author Comp
 */
public class SelectDiagramState extends DiagramState
{
  private final DiagramController diag_controller;
  private final ConfigurationManager cfg_mgr;
  private DiagramElement selected_element;
  private int drag_offset_x; // offset from element loc to spot clicked
  private int drag_offset_y;
  private int diag_ref_x; // reference for dragging diagram
  private int diag_ref_y;
  
  public SelectDiagramState(JViewport v) throws IOException
  {
    super(v);
    diag_controller = DiagramController.getInstance();
    cfg_mgr = ConfigurationManager.getInstance();
    selected_element = null;
    drag_offset_x = 0;
    drag_offset_y = 0;
  }
  
  @Override
  public DiagramState mouseDoubleClicked(MouseEvent evt)
  {
    selectNearestElement(evt.getX(), evt.getY());
    if(selected_element != null)
    {
      selected_element.displayEditGui();
    }
    return next_state;
  }
  
  @Override
  public void draw(Graphics g)
  {
    if(selected_element != null)
    {
      Color oldColor = g.getColor();
      Color newColor = new Color(0, 0, 255);
      g.setColor(newColor);
      selected_element.draw(g);
      g.setColor(oldColor);
    }
  }
  
  @Override
  public DiagramState mousePressed(MouseEvent evt)
  {
    selectNearestElement(evt.getX(), evt.getY());
    if(selected_element != null)
    {
      drag_offset_x = selected_element.getLocation().x - evt.getX();
      drag_offset_y = selected_element.getLocation().y - evt.getY();
    }
    diag_ref_x = evt.getX();
    diag_ref_y = evt.getY();
    return next_state;
  }
  
  @Override
  public DiagramState mouseDragged(MouseEvent evt)
  {
    if(selected_element != null)
    {
      Point p = selected_element.getLocation();
      p.x = evt.getX() + drag_offset_x;
      p.y = evt.getY() + drag_offset_y;
      selected_element.setLocation(p);
    }
    else // no selected element
    {
      Component view = view_port.getView();
      
      int deltaX = evt.getX() - diag_ref_x;
      int deltaY = evt.getY() - diag_ref_y;

      Point viewPoint = view_port.getViewPosition();

      int viewX = viewPoint.x - deltaX;
      int viewY = viewPoint.y - deltaY;

      int maxX = view.getWidth() - view_port.getWidth();
      int maxY = view.getHeight() - view_port.getHeight();

      if(viewX > maxX)
        viewX = maxX;
      if(viewY > maxY)
        viewY = maxY;
      if(viewX < 0)
        viewX = 0;
      if(viewY < 0)
        viewY = 0;
      viewPoint = new Point(viewX, viewY);
      view_port.setViewPosition(viewPoint);

      diag_ref_x = evt.getX() - deltaX;
      diag_ref_y = evt.getY() - deltaY;
    }
    return next_state;
  }
  
  private void selectNearestElement(int x, int y)
  {
    selected_element = null; // Clear any previous selection
    Diagram diag = diag_controller.getOpenDiagram();
    double minDistance = Double.parseDouble(
            cfg_mgr.getConfigValue(ConfigurationManager.SELECT_DISTANCE));
    Iterator<DiagramElement> it = diag.getElements().iterator();
    while(it.hasNext())
    {
      DiagramElement e = it.next();
      double distance = e.getDistanceFrom(x, y);
      if(distance < minDistance)
      {
        minDistance = distance;
        selected_element = e;
        drag_offset_x = selected_element.getLocation().x - x;
        drag_offset_y = selected_element.getLocation().y - y;
      }
    }
  }
  
}
