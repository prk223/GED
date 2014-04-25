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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JViewport;

/**
 *
 * @author Comp
 */
public class ElementSelectedState extends SelectDiagramState
{
  protected final DiagramElement selected_element;
  private final int drag_offset_x; // offset from element loc to spot clicked
  private final int drag_offset_y;
  
  public ElementSelectedState(JViewport v, DiagramElement e, MouseEvent evt) throws IOException
  {
    super(v);
    selected_element = e;
    if(selected_element == null)
      System.err.println("ElSelState:selected_element cannot be null!");
    drag_offset_x = selected_element.getLocation().x - evt.getX();
    drag_offset_y = selected_element.getLocation().y - evt.getY();
    if(evt.getButton() == MouseEvent.BUTTON1)
      left_mouse_down = true;
  }
  
  @Override
  public void draw(Graphics g)
  {
    Color oldColor = g.getColor();
    Color newColor = new Color(0, 0, 255);
    g.setColor(newColor);
    selected_element.draw(g);
    g.setColor(oldColor);
  }
  
  @Override
  public DiagramState mousePressed(MouseEvent evt)
  {
    if(evt.getButton() == MouseEvent.BUTTON1)
    {
      left_mouse_down = true;
      try
      {
        DiagramElement e = getNearestElement(null, evt.getX(), evt.getY());
        if(e != null)
        {
          if(evt.isControlDown() && (e != selected_element))
          {
            ArrayList<DiagramElement> elements = new ArrayList<>();
            elements.add(selected_element);
            elements.add(e);
            next_state = new ElementsSelectedState(view_port, elements, evt);
          }
          else
            next_state = new ElementSelectedState(view_port, e, evt);
        }
        else
          next_state = new SelectDiagramState(view_port, evt);
      }
      catch (IOException ex)
      {
        Logger.getLogger(SelectDiagramState.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    
    return next_state;
  }
  
  @Override
  public DiagramState mouseDragged(MouseEvent evt)
  {
    if(left_mouse_down)
    {
      Point p = selected_element.getLocation();
      p.x = evt.getX() + drag_offset_x;
      p.y = evt.getY() + drag_offset_y;
      selected_element.setLocation(p);
    }
      
    return next_state;
  }
  
  @Override
  public DiagramState mouseReleased(MouseEvent evt)
  {
    if(left_mouse_down)
    {
      DiagramElement e = getNearestElement(selected_element, 
              evt.getX(), evt.getY());
      if(e != null)
        selected_element.setNearElement(e);
      left_mouse_down = false;
    }
    
    return next_state;
  }
  
  @Override
  public DiagramState delete() throws IOException
  {
    diag_controller.removeDiagramElement(selected_element);
    next_state = new SelectDiagramState(view_port);
    return next_state;
  }
  
  @Override
  public DiagramState cut() throws IOException
  {
    copy();
    diag_controller.removeDiagramElement(selected_element);
    next_state = new SelectDiagramState(view_port);
    
    return next_state;
  }
  
  @Override
  public DiagramState copy() throws IOException
  {
    copied_elements = new ArrayList<>();
    copied_elements.add(selected_element);
    
    return next_state;
  }
  
}
