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
public class ElementSelectedState extends SelectState
{
  protected final ElementDecorator selected_element;
  private final int drag_offset_x; // offset from element loc to spot clicked
  private final int drag_offset_y;
  
  public ElementSelectedState(JViewport v, DiagramElement e, MouseEvent evt) throws IOException
  {
    super(v);
    selected_element = new ElementDecorator(e, Color.BLUE);
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
    selected_element.draw(g);
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
          if(evt.isControlDown() && (e != selected_element.getElement()))
          {
            ArrayList<DiagramElement> elements = new ArrayList<>();
            elements.add(selected_element.getElement());
            elements.add(e);
            return new ElementsSelectedState(view_port, elements, evt);
          }
          else
            return new ElementSelectedState(view_port, e, evt);
        }
        else
          return new SelectState(view_port, evt);
      }
      catch (IOException ex)
      {
        Logger.getLogger(SelectState.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    
    return this;
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
      
    return this;
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
    
    return this;
  }
  
  @Override
  public DiagramState delete() throws IOException
  {
    diag_controller.removeDiagramElement(selected_element.getElement());
    return new SelectState(view_port);
  }
  
  @Override
  public DiagramState cut() throws IOException
  {
    copy();
    diag_controller.removeDiagramElement(selected_element.getElement());
    return new SelectState(view_port);
  }
  
  @Override
  public DiagramState copy() throws IOException
  {
    copied_elements = new ArrayList<>();
    copied_elements.add(selected_element.getElement().cloneElement());
    
    return this;
  }
  
}
