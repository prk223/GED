/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JViewport;

/**
 *
 * @author Comp
 */
public class DiagramState
{
  protected final JViewport view_port;
  protected DiagramState next_state;
  
  @SuppressWarnings("LeakingThisInConstructor")
  public DiagramState(JViewport v)
  {
    view_port = v;
    next_state = this;
  }
  
  public DiagramState mouseDoubleClicked(MouseEvent evt)
  {
    return next_state;
  }
  
  public DiagramState mouseDragged(MouseEvent evt)
  {
    return next_state;
  }
  
  public DiagramState mouseMoved(MouseEvent evt)
  {
    return next_state;
  }
  
  public DiagramState mouseEntered(MouseEvent evt)
  {
    return next_state;
  }
  
  public DiagramState mouseExited(MouseEvent evt)
  {
    return next_state;
  }
  
  public DiagramState mousePressed(MouseEvent evt)
  {
    return next_state;
  }
  
  public DiagramState mouseReleased(MouseEvent evt)
  {
    return next_state;
  }
  
  public DiagramState addClassBtnClicked(MouseEvent evt) throws IOException
  {
    ClassElement e = new ClassElement("", 0, 0);
    next_state = new AddElementDiagramState(view_port, e);
    return next_state;
  }
  
  public DiagramState addRelationshipBtnClicked(MouseEvent evt) throws IOException
  {
    Relationship r = new Relationship(RelationshipType.INHERITANCE, 0, 0);
    next_state = new AddElementDiagramState(view_port, r);
    return next_state;
  }
  
  public DiagramState selectBtnClicked(MouseEvent evt) throws IOException
  {
    next_state = new SelectDiagramState(view_port);
    return next_state;
  }
  
  public void draw(Graphics g)
  {
  }
}
