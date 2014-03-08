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

/**
 *
 * @author Comp
 */
public class ClassDiagramState implements DiagramState
{
  private final DiagramController diag_controller;
  private final ClassElement class_element;
  private boolean mouse_on_diagram;
  
  public ClassDiagramState() throws IOException
  {
    diag_controller = DiagramController.getInstance();
    class_element = new ClassElement("", 0, 0);
    mouse_on_diagram = false;
  }
  
  @Override
  public void mouseClicked(MouseEvent evt)
  {
    try
    {
      diag_controller.addClass(evt.getX(), evt.getY());
    }
    catch (IOException ex)
    {
      Logger.getLogger(ClassDiagramState.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  @Override
  public void mouseDoubleClicked(MouseEvent evt)
  {
    // Nothing to do here
  }
  
  @Override
  public void mousePressed(MouseEvent evt)
  {
    // Nothing to do here
  }
  
  @Override
  public void mouseReleased(MouseEvent evt)
  {
    // Nothing to do here
  }
  
  @Override
  public void mouseMoved(MouseEvent evt)
  {
    class_element.setLocation(new Point(evt.getX(), evt.getY()));
  }
  
  @Override
  public void mouseEntered(MouseEvent evt)
  {
    mouse_on_diagram = true;
  }
  
  @Override
  public void mouseExited(MouseEvent evt)
  {
    mouse_on_diagram = false;
  }
  
  @Override
  public void mouseDragged(MouseEvent evt)
  {
    mouseMoved(evt);
  }
  
  @Override
  public void draw(Graphics g)
  {
    // Change the color for the class that is being placed
    Color oldColor = g.getColor();
    Color newColor = new Color(255, 0, 0);
    g.setColor(newColor);

    if(mouse_on_diagram)
      class_element.draw(g);

    // Replace the color
    g.setColor(oldColor);
  }
  
  @Override
  public void reset()
  {
    mouse_on_diagram = false;
  }
}
