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
    
    return this;
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
            return new RelationshipSelectedState(view_port, (Relationship)e, evt);
          else
            return new ElementSelectedState(view_port, e, evt);
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
    
    return this;
  }
  
  @Override
  public DiagramState mouseReleased(MouseEvent evt)
  {
    if(evt.getButton() == MouseEvent.BUTTON1)
      left_mouse_down = false;
    
    return this;
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
      
    return this;
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
          return new RelationshipSelectedState(view_port, (Relationship)e, evt);
    }
    
    return this;
  }
  
  @Override
  public DiagramState selectAll()
  {
    Diagram diag = diag_controller.getOpenDiagram();
    ArrayList<DiagramElement> elements = diag_controller.getDiagramElements();
    try
    {
      return new ElementsSelectedState(view_port, elements);
    }
    catch(IOException e)
    {
      System.err.println("Failed to create ElementsSelectedState");
    }
    
    return this;
  }
  
  @Override
  public DiagramState paste(Point loc) throws IOException
  {
    if((copied_elements != null) && !copied_elements.isEmpty())
    {
      Iterator<DiagramElement> elIt = copied_elements.iterator();
      // Get paste offset based on first element
      DiagramElement first = elIt.next();
      
      // Set up list of elements to paste
      ArrayList<DiagramElement> pasteElements = new ArrayList<>();
      pasteElements.add(first.cloneElement());
      
      // Get center location of all elements
      int minX = first.getMinX();
      int minY = first.getMinY();
      int maxX = first.getMaxX();
      int maxY = first.getMaxY();
      while(elIt.hasNext())
      {
        DiagramElement e = elIt.next();
        pasteElements.add(e.cloneElement());
        if(e.getMinX() < minX)
          minX = e.getMinX();
        if(e.getMinY() < minY)
          minY = e.getMinY();
        if(e.getMaxX() > maxX)
          maxX = e.getMaxX();
        if(e.getMaxY() > maxY)
          maxY = e.getMaxY();
      }
      int deltaX = loc.x - ((maxX - minX) / 2) - minX;
      int deltaY = loc.y - ((maxY - minY) / 2) - minY;
      
      tetherCopiedElements(pasteElements);
      
      elIt = pasteElements.iterator();
      while(elIt.hasNext())
      {
        DiagramElement e = elIt.next();
        e.move(deltaX, deltaY);
        diag_controller.addDiagramElement(e);
      }
    }
    
    return this;
  }
  
  private static void tetherCopiedElements(ArrayList<DiagramElement> elements) throws IOException
  {
    // Get a list of relationship elements
    Iterator<DiagramElement> elIt = elements.iterator();
    ArrayList<Relationship> relationships = new ArrayList<>();
    while(elIt.hasNext())
    {
      DiagramElement e = elIt.next();
      if(e.isRelationship())
        relationships.add((Relationship)e);
    }
    
    // Tether relationships if needed
    Iterator<Relationship> relIt = relationships.iterator();
    while(relIt.hasNext())
    {
      Relationship r = relIt.next();
      int srcUid = r.getSourceClassUid();
      int destUid = r.getDestinationClassUid();
      if((srcUid > 0) || (destUid > 0))
      {
        elIt = elements.iterator();
        while(elIt.hasNext())
        {
          DiagramElement e = elIt.next();
          if((e.getElementType().equals("Class")) && 
             (e.getUniqueId() == srcUid))
          {
            r.tetherSourceToClass((ClassElement)e);
          }
          if((e.getElementType().equals("Class")) && 
             (e.getUniqueId() == destUid))
          {
            r.tetherDestinationToClass((ClassElement)e);
          }
        }
      }
    }
  }
  
}
