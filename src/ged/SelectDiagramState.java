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
import java.util.Iterator;

/**
 *
 * @author Comp
 */
public class SelectDiagramState implements DiagramState
{
  DiagramController diag_controller;
  ConfigurationManager cfg_mgr;
  DiagramElement selected_element;
  boolean mouse_on_diagram;
  boolean mouse_pressed;
  int drag_offset_x; // offset from element loc to spot clicked
  int drag_offset_y;
  
  public SelectDiagramState() throws IOException
  {
    diag_controller = DiagramController.getInstance();
    cfg_mgr = ConfigurationManager.getInstance();
    selected_element = null;
    mouse_on_diagram = false;
    mouse_pressed = false;
    drag_offset_x = 0;
    drag_offset_y = 0;
  }
  
  @Override
  public void mouseClicked(MouseEvent evt)
  {
    selected_element = null; // Clear any previous selection
    Diagram diag = diag_controller.getOpenDiagram();
    double minDistance = Double.parseDouble(
            cfg_mgr.getConfigValue(ConfigurationManager.SELECT_DISTANCE));
    Iterator<DiagramElement> it = diag.getElements().iterator();
    while(it.hasNext())
    {
      DiagramElement e = it.next();
      double distance = e.getDistanceFrom(evt.getX(), evt.getY());
      if(distance < minDistance)
      {
        minDistance = distance;
        selected_element = e;
        drag_offset_x = selected_element.getLocation().x - evt.getX();
        drag_offset_y = selected_element.getLocation().y - evt.getY();
      }
    }
    
    mouse_pressed = false;
  }
  
  @Override
  public void mouseDoubleClicked(MouseEvent evt)
  {
    mouse_pressed = false;
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
    mouseClicked(evt);
    mouse_pressed = true;
    if(selected_element != null)
    {
      drag_offset_x = selected_element.getLocation().x - evt.getX();
      drag_offset_y = selected_element.getLocation().y - evt.getY();
    }
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
  
}
