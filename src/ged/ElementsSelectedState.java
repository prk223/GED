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
  protected final ArrayList<ElementDecorator> selected_elements;
  private int drag_start_x; // offset from element loc to spot clicked
  private int drag_start_y;
  
  public ElementsSelectedState(JViewport v, ArrayList<DiagramElement> elements, 
          MouseEvent evt) throws IOException
  {
    super(v);
    selected_elements = new ArrayList<>();
    Iterator<DiagramElement> elIt = elements.iterator();
    while(elIt.hasNext())
    {
      DiagramElement e = elIt.next();
      selected_elements.add(new ElementDecorator(e, Color.BLUE));
    }
    drag_start_x = evt.getX();
    drag_start_y = evt.getY();
  }
  
  public ElementsSelectedState(JViewport v, ArrayList<DiagramElement> elements) throws IOException
  {
    super(v);
    selected_elements = new ArrayList<>();
    Iterator<DiagramElement> elIt = elements.iterator();
    while(elIt.hasNext())
    {
      DiagramElement e = elIt.next();
      selected_elements.add(new ElementDecorator(e, Color.BLUE));
    }
  }
  
  @Override
  public void draw(Graphics g)
  {
    Iterator<ElementDecorator> itEl = selected_elements.iterator();
    while(itEl.hasNext())
    {
      ElementDecorator e = itEl.next();
      e.draw(g);
    }
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
          boolean alreadySelected = false;
          Iterator<ElementDecorator> elIt = selected_elements.iterator();
          while(elIt.hasNext())
          {
            ElementDecorator decoratedElement = elIt.next();
            DiagramElement selectedElement = decoratedElement.getElement();
            if(selectedElement == e)
            {
              alreadySelected = true;
              selected_elements.remove(decoratedElement);
              break;
            }
          }
          if(alreadySelected)
          {
            if(selected_elements.size() == 1)
            {
              return new ElementSelectedState(view_port, 
                      selected_elements.get(0).getElement(), evt);
            }
          }
          else
            selected_elements.add(new ElementDecorator(e, Color.BLUE));
          drag_start_x = evt.getX();
          drag_start_y = evt.getY();
        }
        else if(e != null)
        {
          boolean alreadySelected = false;
          Iterator<ElementDecorator> elIt = selected_elements.iterator();
          while(elIt.hasNext())
          {
            ElementDecorator decoratedElement = elIt.next();
            DiagramElement selectedElement = decoratedElement.getElement();
            if(selectedElement == e)
            {
              alreadySelected = true;
              break;
            }
          }
          if(alreadySelected)
          {
            drag_start_x = evt.getX();
            drag_start_y = evt.getY();
          }
          else
            return new ElementSelectedState(view_port, e, evt);
        }
        else
          return new SelectDiagramState(view_port, evt);
      }
      catch (IOException ex)
      {
        Logger.getLogger(SelectDiagramState.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    
    return this;
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
      Iterator<ElementDecorator> itEl = selected_elements.iterator();
      while(itEl.hasNext())
      {
        DiagramElement e = itEl.next();
        e.move(deltaX, deltaY);
      }
    }
      
    return this;
  }
  
  @Override
  public DiagramState delete() throws IOException
  {
    Iterator<ElementDecorator> elIt = selected_elements.iterator();
    while(elIt.hasNext())
    {
      ElementDecorator e = elIt.next();
      diag_controller.removeDiagramElement(e.getElement());
    }
    return new SelectDiagramState(view_port);
  }
  
  @Override
  public DiagramState cut() throws IOException
  {
    copy();
    return delete();
  }
  
  @Override
  public DiagramState copy() throws IOException
  {
    copied_elements = new ArrayList<>();
    Iterator<ElementDecorator> elIt = selected_elements.iterator();
    while(elIt.hasNext())
    {
      ElementDecorator e = elIt.next();
      copied_elements.add(e.getElement().cloneElement());
    }
    
    return this;
  }
  
}
