/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JViewport;

/**
 *
 * @author Comp
 */
public class DiagramState
{
  protected final JViewport view_port;
  protected DiagramState next_state;
  protected final DiagramController diag_controller;
  protected static ArrayList<DiagramElement> copied_elements;
  
  @SuppressWarnings("LeakingThisInConstructor")
  public DiagramState(JViewport v) throws IOException
  {
    view_port = v;
    next_state = this;
    diag_controller = DiagramController.getInstance();
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
  
  public DiagramState addClassBtnClicked() throws IOException
  {
    ClassElement e = new ClassElement("", 0, 0);
    next_state = new AddElementDiagramState(view_port, e);
    return next_state;
  }
  
  public DiagramState addInheritanceBtnClicked() throws IOException
  {
    InheritanceRelationship r = new InheritanceRelationship(0, 0);
    next_state = new AddElementDiagramState(view_port, r);
    return next_state;
  }
  
  public DiagramState addAggregationBtnClicked() throws IOException
  {
    AggregationRelationship r = new AggregationRelationship(0,0);
    next_state = new AddElementDiagramState(view_port, r);
    return next_state;
  }
  
  public DiagramState addAssociationBtnClicked() throws IOException
  {
    AssociationRelationship r = new AssociationRelationship(0,0);
    next_state = new AddElementDiagramState(view_port, r);
    return next_state;
  }
  
  public DiagramState selectBtnClicked() throws IOException
  {
    next_state = new SelectDiagramState(view_port);
    return next_state;
  }
  
  public void draw(Graphics g)
  {
  }
  
  public DiagramState mouseRightClicked(MouseEvent evt) throws IOException
  {
    return next_state;
  }
  
  public DiagramState delete() throws IOException
  {
    return next_state;
  }
  
  public DiagramState selectAll() throws IOException
  {
    return next_state;
  }
  
  public DiagramState cut() throws IOException
  {
    return next_state;
  }
  
  public DiagramState copy() throws IOException
  {
    return next_state;
  }
  
  public DiagramState paste(Point loc) throws IOException
  {
    if((copied_elements != null) && !copied_elements.isEmpty())
    {
      Iterator<DiagramElement> elIt = copied_elements.iterator();
      // Get paste offset based on first element
      DiagramElement first = elIt.next();
      
      // Get center location of all elements
      int minX = first.getMinX();
      int minY = first.getMinY();
      int maxX = first.getMaxX();
      int maxY = first.getMaxY();
      while(elIt.hasNext())
      {
        DiagramElement e = elIt.next();
        if(e.getMinX() < minX)
          minX = e.getMinX();
        if(e.getMinY() < minY)
          minY = e.getMinY();
        if(e.getMaxX() > maxX)
          maxX = e.getMaxX();
        if(e.getMaxY() > maxY)
          maxY = e.getMaxY();
      }
      int deltaX = loc.x - ((maxX - minX) / 2);
      int deltaY = loc.y - ((maxY - minY) / 2);
      
      elIt = copied_elements.iterator();
      while(elIt.hasNext())
      {
        DiagramElement e = elIt.next().cloneElement();
        e.move(deltaX, deltaY);
        diag_controller.addDiagramElement(e);
      }
    }
    
    return next_state;
  }
}
