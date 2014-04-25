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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JViewport;

/**
 *
 * @author Comp
 */
public class ElementsSelectedState extends SelectDiagramState
{
  protected final ArrayList<DiagramElement> selected_elements;
  private int drag_start_x; // offset from element loc to spot clicked
  private int drag_start_y;
  
  public ElementsSelectedState(JViewport v, ArrayList<DiagramElement> elements, 
          MouseEvent evt) throws IOException
  {
    super(v);
    selected_elements = elements;
    drag_start_x = evt.getX();
    drag_start_y = evt.getY();
  }
  
  public ElementsSelectedState(JViewport v, ArrayList<DiagramElement> elements) throws IOException
  {
    super(v);
    selected_elements = elements;
  }
  
  @Override
  public void draw(Graphics g)
  {
    Color oldColor = g.getColor();
    Color newColor = new Color(0, 0, 255);
    g.setColor(newColor);
    Iterator<DiagramElement> itEl = selected_elements.iterator();
    while(itEl.hasNext())
    {
      DiagramElement e = itEl.next();
      e.draw(g);
    }
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
        if((e != null) && (evt.isControlDown()))
        {
          if(selected_elements.contains(e))
          {
            selected_elements.remove(e);
            if(selected_elements.size() == 1)
            {
              next_state = new ElementSelectedState(view_port, 
                      selected_elements.get(0), evt);
            }
          }
          else
            selected_elements.add(e);
          drag_start_x = evt.getX();
          drag_start_y = evt.getY();
        }
        else if(e != null)
        {
          if(selected_elements.contains(e))
          {
            drag_start_x = evt.getX();
            drag_start_y = evt.getY();
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
      int deltaX = evt.getX() - drag_start_x;
      int deltaY = evt.getY() - drag_start_y ;
      drag_start_x = evt.getX();
      drag_start_y = evt.getY();
      Iterator<DiagramElement> itEl = selected_elements.iterator();
      while(itEl.hasNext())
      {
        DiagramElement e = itEl.next();
        Point p = e.getLocation();
        e.move(deltaX, deltaY);
      }
    }
      
    return next_state;
  }
  
  @Override
  public DiagramState delete() throws IOException
  {
    Iterator<DiagramElement> elIt = selected_elements.iterator();
    while(elIt.hasNext())
    {
      DiagramElement e = elIt.next();
      diag_controller.removeDiagramElement(e);
    }
    next_state = new SelectDiagramState(view_port);
    return next_state;
  }
  
  @Override
  public DiagramState cut() throws IOException
  {
    copy();
    Iterator<DiagramElement> elIt = selected_elements.iterator();
    while(elIt.hasNext())
    {
      DiagramElement e = elIt.next();
      diag_controller.removeDiagramElement(e);
    }
    next_state = new SelectDiagramState(view_port);
    
    return next_state;
  }
  
  @Override
  public DiagramState copy() throws IOException
  {
    copied_elements = new ArrayList<>();
    Iterator<DiagramElement> elIt = selected_elements.iterator();
    while(elIt.hasNext())
    {
      DiagramElement e = elIt.next();
      copied_elements.add(e.cloneElement());
    }
    
    return next_state;
  }
  
}
