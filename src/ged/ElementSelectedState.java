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
    try
    {
      DiagramElement e = getNearestElement(null, evt.getX(), evt.getY());
      if(e != null)
        next_state = new ElementSelectedState(view_port, e, evt);
      else
        next_state = new SelectDiagramState(view_port, evt);
    }
    catch (IOException ex)
    {
      Logger.getLogger(SelectDiagramState.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    return next_state;
  }
  
  @Override
  public DiagramState mouseDragged(MouseEvent evt)
  {
    Point p = selected_element.getLocation();
    p.x = evt.getX() + drag_offset_x;
    p.y = evt.getY() + drag_offset_y;
    selected_element.setLocation(p);
      
    return next_state;
  }
  
  @Override
  public DiagramState mouseReleased(MouseEvent evt)
  {
    DiagramElement e = getNearestElement(selected_element, 
            evt.getX(), evt.getY());
    if(e != null)
      selected_element.setNearElement(e);
    
    return next_state;
  }
  
}
