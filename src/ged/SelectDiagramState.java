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
public class SelectDiagramState implements DiagramState
{
  private final DiagramController diag_controller;
  private final ConfigurationManager cfg_mgr;
  private DiagramElement selected_element;
  private boolean mouse_on_diagram;
  private boolean mouse_pressed;
  private int drag_offset_x; // offset from element loc to spot clicked
  private int drag_offset_y;
  private int diag_ref_x; // reference for dragging diagram
  private int diag_ref_y;
  private final JViewport view_port;
  
  public SelectDiagramState(JViewport v) throws IOException
  {
    diag_controller = DiagramController.getInstance();
    cfg_mgr = ConfigurationManager.getInstance();
    selected_element = null;
    mouse_on_diagram = false;
    mouse_pressed = false;
    drag_offset_x = 0;
    drag_offset_y = 0;
    view_port = v;
  }
  
  @Override
  public void mouseClicked(MouseEvent evt)
  {
    selectNearestElement(evt.getX(), evt.getY());
    mouse_pressed = false;
  }
  
  @Override
  public void mouseDoubleClicked(MouseEvent evt)
  {
    mouse_pressed = false;
    selectNearestElement(evt.getX(), evt.getY());
    if(selected_element != null)
    {
      selected_element.displayEditGui();
    }
  }
  
  @Override
  public void mouseMoved(MouseEvent evt)
  {
    if(mouse_on_diagram && selected_element != null
            && mouse_pressed)
    {
      Point p = selected_element.getLocation();
      p.x = evt.getX() + drag_offset_x;
      p.y = evt.getY() + drag_offset_y;
      selected_element.setLocation(p);
    }
  }
  
  @Override
  public void draw(Graphics g)
  {
    if(selected_element != null && mouse_on_diagram)
    {
      Color oldColor = g.getColor();
      Color newColor = new Color(0, 0, 255);
      g.setColor(newColor);
      selected_element.draw(g);
      g.setColor(oldColor);
    }
  }
  
  @Override
  public void mouseEntered(MouseEvent evt)
  {
    mouse_on_diagram = true;
    mouse_pressed = false;
  }
  
  @Override
  public void mouseExited(MouseEvent evt)
  {
    mouse_on_diagram = false;
    mouse_pressed = false;
  }
  
  @Override
  public void mousePressed(MouseEvent evt)
  {
    selectNearestElement(evt.getX(), evt.getY());
    mouse_pressed = true;
    if(selected_element != null)
    {
      drag_offset_x = selected_element.getLocation().x - evt.getX();
      drag_offset_y = selected_element.getLocation().y - evt.getY();
    }
    diag_ref_x = evt.getX();
    diag_ref_y = evt.getY();
  }
  
  @Override
  public void mouseReleased(MouseEvent evt)
  {
    mouse_pressed = false;
  }
  
  @Override
  public void mouseDragged(MouseEvent evt)
  {
    if(mouse_on_diagram && selected_element != null)
    {
      Point p = selected_element.getLocation();
      p.x = evt.getX() + drag_offset_x;
      p.y = evt.getY() + drag_offset_y;
      selected_element.setLocation(p);
    }
    else if(mouse_on_diagram) // no selected element
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
  }
  
  @Override
  public void reset()
  {
    selected_element = null;
    mouse_on_diagram = false;
    mouse_pressed = false;
    drag_offset_x = 0;
    drag_offset_y = 0;
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
