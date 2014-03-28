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
  private final DiagramController diag_controller;
  private final ConfigurationManager cfg_mgr;
  private int diag_ref_x; // reference for dragging diagram
  private int diag_ref_y;
  private final double select_distance;
  
  public SelectDiagramState(JViewport v) throws IOException
  {
    super(v);
    diag_controller = DiagramController.getInstance();
    cfg_mgr = ConfigurationManager.getInstance();
    select_distance = Double.parseDouble(
            cfg_mgr.getConfigValue(ConfigurationManager.SELECT_DISTANCE));
  }
  
  public SelectDiagramState(JViewport v, MouseEvent evt) throws IOException
  {
    super(v);
    diag_controller = DiagramController.getInstance();
    cfg_mgr = ConfigurationManager.getInstance();
    select_distance = Double.parseDouble(
            cfg_mgr.getConfigValue(ConfigurationManager.SELECT_DISTANCE));
    diag_ref_x = evt.getX();
    diag_ref_y = evt.getY();
  }
  
  @Override
  public DiagramState mouseDoubleClicked(MouseEvent evt)
  {
    DiagramElement e = getNearestElement(null, evt.getX(), evt.getY());
    if(e != null)
      e.displayEditGui();
    
    return next_state;
  }
  
  @Override
  public DiagramState mousePressed(MouseEvent evt)
  {
    try
    {
      DiagramElement e = getNearestElement(null, evt.getX(), evt.getY());
      if(e != null)
        next_state = new ElementSelectedState(view_port, e, evt);
    }
    catch (IOException ex)
    {
      Logger.getLogger(SelectDiagramState.class.getName()).log(Level.SEVERE, null, ex);
    }
    diag_ref_x = evt.getX();
    diag_ref_y = evt.getY();
    
    return next_state;
  }
  
  @Override
  public DiagramState mouseDragged(MouseEvent evt)
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
  
}
