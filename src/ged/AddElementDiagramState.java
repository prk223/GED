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
import javax.swing.JViewport;

/**
 *
 * @author Comp
 */
public class AddElementDiagramState extends DiagramState
{
  private final DiagramController diag_controller;
  private final DiagramElement new_element;
  private boolean mouse_on_diagram;
  
  public AddElementDiagramState(JViewport v, DiagramElement e) throws IOException
  {
    super(v);
    diag_controller = DiagramController.getInstance();
    new_element = e;
    if(new_element == null)
      System.err.println("AddElementDiagramState: e cannot be null!");
    mouse_on_diagram = false;
  }
  
  @Override
  public DiagramState mouseReleased(MouseEvent evt)
  {
    try
    {
      diag_controller.addDiagramElement(new_element);
      return new SelectDiagramState(view_port);
    }
    catch (IOException ex)
    {
      System.err.println("ERR:AddElemDiagramState: Failed to create select state");
    }
    return this;
  }
  
  @Override
  public DiagramState mouseMoved(MouseEvent evt)
  {
    new_element.setLocation(new Point(evt.getX(), evt.getY()));
    return this;
  }
  
  @Override
  public DiagramState mouseEntered(MouseEvent evt)
  {
    mouse_on_diagram = true;
    return this;
  }
  
  @Override
  public DiagramState mouseExited(MouseEvent evt)
  {
    mouse_on_diagram = false;
    return this;
  }
  
  @Override
  public DiagramState mouseDragged(MouseEvent evt)
  {
    return mouseMoved(evt);
  }
  
  @Override
  public void draw(Graphics g)
  {
    // Change the color for the class that is being placed
    Color oldColor = g.getColor();
    Color newColor = new Color(255, 0, 0);
    g.setColor(newColor);

    if(mouse_on_diagram)
      new_element.draw(g);

    // Replace the color
    g.setColor(oldColor);
  }
}
