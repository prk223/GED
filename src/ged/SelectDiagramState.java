/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Component;
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
public class SelectDiagramState extends DiagramState
{
  private final ConfigurationManager cfg_mgr;
  private int diag_ref_x; // reference for dragging diagram
  private int diag_ref_y;
  private final double select_distance;
  protected boolean left_mouse_down;
  
  public SelectDiagramState(JViewport v) throws IOException
  {
    super(v);
    cfg_mgr = ConfigurationManager.getInstance();
    select_distance = Double.parseDouble(
            cfg_mgr.getConfigValue(ConfigurationManager.SELECT_DISTANCE));
    left_mouse_down = false;
  }
  
  public SelectDiagramState(JViewport v, MouseEvent evt) throws IOException
  {
    super(v);
    cfg_mgr = ConfigurationManager.getInstance();
    select_distance = Double.parseDouble(
            cfg_mgr.getConfigValue(ConfigurationManager.SELECT_DISTANCE));
    diag_ref_x = evt.getX();
    diag_ref_y = evt.getY();
    if((evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
      left_mouse_down = true;
    else
      left_mouse_down = false;
  }
  
  @Override
  public DiagramState mouseDoubleClicked(MouseEvent evt)
  {
    DiagramElement e = getNearestElement(null, evt.getX(), evt.getY());
    if(e != null)
      e.displayEditGui(evt);
    
    return next_state;
  }
  
  @Override
  public DiagramState mousePressed(MouseEvent evt)
  {
    if(evt.getButton() == MouseEvent.BUTTON1)
    {
      try
      {
        DiagramElement e = getNearestElement(null, evt.getX(), evt.getY());
        if(e != null)
        {
          if(e.isRelationship())
            next_state = new RelationshipSelectedState(view_port, (Relationship)e, evt);
          else
            next_state = new ElementSelectedState(view_port, e, evt);
        }
      }
      catch (IOException ex)
      {
        Logger.getLogger(SelectDiagramState.class.getName()).log(Level.SEVERE, null, ex);
      }
      diag_ref_x = evt.getX();
      diag_ref_y = evt.getY();
      left_mouse_down = true;
    }
    
    return next_state;
  }
  
  @Override
  public DiagramState mouseReleased(MouseEvent evt)
  {
    if(evt.getButton() == MouseEvent.BUTTON1)
      left_mouse_down = false;
    
    return next_state;
  }
  
  @Override
  public DiagramState mouseDragged(MouseEvent evt)
  {
    if(left_mouse_down)
    {
      Component view = view_port.getView();

      int deltaX = evt.getX() - diag_ref_x;
      int deltaY = evt.getY() - diag_ref_y;

      Point viewPoint = view_port.getViewPosition();

      int viewX = viewPoint.x - deltaX;
      int viewY = viewPoint.y - deltaY;

      int maxX = view.getWidth() - view_port.getWidth();
      int maxY = view.getHeight() - view_port.getHeight();

      if(viewX > maxX)
        viewX = maxX;
      if(viewY > maxY)
        viewY = maxY;
      if(viewX < 0)
        viewX = 0;
      if(viewY < 0)
        viewY = 0;
      viewPoint = new Point(viewX, viewY);
      view_port.setViewPosition(viewPoint);

      diag_ref_x = evt.getX() - deltaX;
      diag_ref_y = evt.getY() - deltaY;
    }
      
    return next_state;
  }
  
  protected DiagramElement getNearestElement(DiagramElement excludeElement,
          int x, int y)
  {
    DiagramElement nearestElem = null; // Clear any previous selection
    Diagram diag = diag_controller.getOpenDiagram();
    double minDistance = select_distance;
    Iterator<DiagramElement> it = diag.getElements().iterator();
    while(it.hasNext())
    {
      DiagramElement e = it.next();
      if(e != excludeElement)
      {
        double distance = e.getDistanceFrom(x, y);
        if(distance < minDistance)
        {
          minDistance = distance;
          nearestElem = e;
        }
      }
    }
    
    return nearestElem;
  }
  
  @Override
  public DiagramState mouseRightClicked(MouseEvent evt) throws IOException
  {
    DiagramElement e = getNearestElement(null, evt.getX(), evt.getY());
    if(e != null)
    {
      if(e.isRelationship())
          next_state = new RelationshipSelectedState(view_port, (Relationship)e, evt);
    }
    
    return next_state;
  }
  
  @Override
  public DiagramState selectAll()
  {
    Diagram diag = diag_controller.getOpenDiagram();
    ArrayList<DiagramElement> elements = diag_controller.getDiagramElements();
    try
    {
      next_state = new ElementsSelectedState(view_port, elements);
    }
    catch(IOException e)
    {
      System.err.println("Failed to create ElementsSelectedState");
    }
    
    return next_state;
  }
  
}
