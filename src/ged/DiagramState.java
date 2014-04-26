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
import javax.swing.JViewport;

/**
 *
 * @author Comp
 */
public class DiagramState
{
  protected final JViewport view_port;
  protected final DiagramController diag_controller;
  protected static ArrayList<DiagramElement> copied_elements;
  
  @SuppressWarnings("LeakingThisInConstructor")
  public DiagramState(JViewport v) throws IOException
  {
    view_port = v;
    diag_controller = DiagramController.getInstance();
  }
  
  public DiagramState mouseDoubleClicked(MouseEvent evt)
  {
    return this;
  }
  
  public DiagramState mouseDragged(MouseEvent evt)
  {
    return this;
  }
  
  public DiagramState mouseMoved(MouseEvent evt)
  {
    return this;
  }
  
  public DiagramState mouseEntered(MouseEvent evt)
  {
    return this;
  }
  
  public DiagramState mouseExited(MouseEvent evt)
  {
    return this;
  }
  
  public DiagramState mousePressed(MouseEvent evt)
  {
    return this;
  }
  
  public DiagramState mouseReleased(MouseEvent evt)
  {
    return this;
  }
  
  public DiagramState addClassBtnClicked() throws IOException
  {
    ClassElement e = new ClassElement("", 0, 0);
    return new AddElementDiagramState(view_port, e);
  }
  
  public DiagramState addInheritanceBtnClicked() throws IOException
  {
    InheritanceRelationship r = new InheritanceRelationship(0, 0);
    return new AddElementDiagramState(view_port, r);
  }
  
  public DiagramState addAggregationBtnClicked() throws IOException
  {
    AggregationRelationship r = new AggregationRelationship(0,0);
    return new AddElementDiagramState(view_port, r);
  }
  
  public DiagramState addAssociationBtnClicked() throws IOException
  {
    AssociationRelationship r = new AssociationRelationship(0,0);
    return new AddElementDiagramState(view_port, r);
  }
  
  public DiagramState selectBtnClicked() throws IOException
  {
    return new SelectDiagramState(view_port);
  }
  
  public void draw(Graphics g)
  {
  }
  
  public DiagramState mouseRightClicked(MouseEvent evt) throws IOException
  {
    return this;
  }
  
  public DiagramState delete() throws IOException
  {
    return this;
  }
  
  public DiagramState selectAll() throws IOException
  {
    return this;
  }
  
  public DiagramState cut() throws IOException
  {
    return this;
  }
  
  public DiagramState copy() throws IOException
  {
    return this;
  }
  
  public DiagramState paste(Point loc) throws IOException
  {
    return this;
  }
}
