/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import static ged.Util.getValueFromTag;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Comp
 */
public class ClassDiagram extends DiagramElement
{
  private final ConfigurationManager cfg_mgr;
  private String name;
  private final ArrayList<DiagramElement> elements;
  private Point location;
  private final int diag_buffer_size;
  private int unique_id;
  private int last_unique_id;
  
  public ClassDiagram(String diagName) throws IOException
  {
    location = new Point(0, 0);
    name = diagName;
    elements = new ArrayList<>();
    
    cfg_mgr = ConfigurationManager.getInstance();
    diag_buffer_size = Integer.parseInt(
            cfg_mgr.getConfigValue(ConfigurationManager.DIAGRAM_BFR_SIZE));
    
    unique_id = 0;
    last_unique_id = 0;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String n)
  {
    name = n;
  }
  
  public boolean save(String filePath)
  {
    boolean success = false;
    
    try (PrintWriter outFile = new PrintWriter(filePath))
    {
      String diagramString = getPersistentRepresentation();
      outFile.println(diagramString);
      outFile.close();
      success = true;
    }
    catch(FileNotFoundException ex)
    {
      System.err.println("ERROR:File failed to open:" + filePath);
    }
    
    return success;
  }

  public static ClassDiagram loadDiagram(String filePath)
  {
    ClassDiagram loadedDiagram = null;
    File diagFile = new File(filePath);
    if(diagFile.exists())
    {
      try (BufferedReader diagRdr = 
              new BufferedReader(new FileReader(diagFile)))
      {
        String line = diagRdr.readLine();
        String diagramString = "";
        while(line != null)
        {
          diagramString += line + "\n";
          line = diagRdr.readLine();
        }
        
        loadedDiagram = fromStringRepresentation(diagramString);
        diagRdr.close();
      }
      catch(FileNotFoundException ex)
      {
        System.err.println("Diagram:loadDiagram:ERROR:Unable to read file" 
                + filePath);
      }
      catch(IOException ex)
      {
        System.err.println("Diagram:loadDiagram:ERROR:IO Error:" + filePath);
      }
    }
    
    return loadedDiagram;
  }
  
  public void addElement(DiagramElement e)
  {
    elements.add(e);
    ensureUniqueId(e);
  }
  
  /**
   *
   * @param g
   */
  @Override
  public void draw(Graphics g)
  {
    Iterator<DiagramElement> it = elements.iterator();
    while(it.hasNext())
    {
      DiagramElement e = it.next();
      e.draw(g);
    }
  }
  
  @Override
  public void setLocation(Point loc)
  {
    location = loc;
  }
  
  @Override
  public void move(int deltaX, int deltaY)
  {
    location.x += deltaX;
    location.y += deltaY;
  }
  
  @Override
  public Point getLocation()
  {
    return location;
  }
  
  @Override
  public String getElementType()
  {
    return "Diagram";
  }
  
  @Override
  public String getPersistentRepresentation()
  {
    String rep = "<diagram>" + name + "</diagram>\n";
    Iterator<DiagramElement> it = elements.iterator();
    while(it.hasNext())
    {
      DiagramElement e = it.next();
      String type = e.getElementType();
      rep += "<element:" + type + ">";
      rep += e.getPersistentRepresentation();
      rep += "</element:" + type + ">\n";
    }
    
    return rep;
  }
  
  public static ClassDiagram fromStringRepresentation(String s) throws IOException
  {
    ClassDiagram d;
    
    String n = getValueFromTag(s, "diagram");
    d = new ClassDiagram(n);
    
    String startTag = "<element:";
    String endTag = "</element:";
    String[] strArr = s.split(startTag);
    for(int i = 0; i < strArr.length; i++)
    {
      if(strArr[i].contains(endTag))
      {
        strArr[i] = startTag + strArr[i];
        String type = "ERROR";
        int typeStart = strArr[i].indexOf(startTag) + startTag.length();
        int typeEnd = strArr[i].indexOf(">", typeStart);
        if(typeEnd > typeStart)
          type = strArr[i].substring(typeStart, typeEnd);
        DiagramElement e = null;
        switch(type)
        {
          case "Class":
            e = ClassElement.
                    fromPersistentRepresentation(strArr[i]);
            break;
          case "Relationship":
            e = Relationship.fromPersistentRepresentation(strArr[i]);
            break;
          case "Inheritance":
            e = InheritanceRelationship.fromPersistentRepresentation(strArr[i]);
            break;
          case "Aggregation":
            e = AggregationRelationship.fromPersistentRepresentation(strArr[i]);
            break;
          case "Association":
            e = AssociationRelationship.fromPersistentRepresentation(strArr[i]);
            break;
          default:
            System.err.println("ERROR:Diagram:Unknown element:" + type);
        }
        if(e != null)
        {
          d.addElement(e);
        }
      }
    }
    
    return d;
  }
  
  @Override
  public double getDistanceFrom(int x, int y)
  {
    System.out.println("Diagram does not support getDistanceFrom");
    return -10000000;
  }
  
  public ArrayList<DiagramElement> getElements()
  {
    return elements;
  }
  
  @Override
  public void displayEditGui(MouseEvent evt)
  {
    // Nothing to do here
  }
  
  @Override
  public int getMinX()
  {
    int minX = 0;
    Iterator<DiagramElement> itEl = elements.iterator();
    while(itEl.hasNext())
    {
      DiagramElement e = itEl.next();
      if(e.getMaxX() < minX)
        minX = e.getMaxX();
    }
    
    // Subtract buffer to diagram around what's already drawn
    minX -= diag_buffer_size;
    
    return minX;
  }
  
  @Override
  public int getMinY()
  {
    int minY = 0;
    
    Iterator<DiagramElement> itEl = elements.iterator();
    while(itEl.hasNext())
    {
      DiagramElement e = itEl.next();
      if(e.getMaxY() < minY)
        minY = e.getMaxY();
    }
    
    // Subtract buffer to diagram around what's already drawn
    minY -= diag_buffer_size;
    
    return minY;
  }
  
  @Override
  public int getMaxX()
  {
    int maxX = 0;
    
    Iterator<DiagramElement> itEl = elements.iterator();
    while(itEl.hasNext())
    {
      DiagramElement e = itEl.next();
      if(e.getMaxX() > maxX)
        maxX = e.getMaxX();
    }
    
    // Add buffer to diagram around what's already drawn
    maxX += diag_buffer_size;
    
    return maxX;
  }
  
  @Override
  public int getMaxY()
  {
    int maxY = 0;
    
    Iterator<DiagramElement> itEl = elements.iterator();
    while(itEl.hasNext())
    {
      DiagramElement e = itEl.next();
      if(e.getMaxY() > maxY)
        maxY = e.getMaxY();
    }
    
    // Add buffer to diagram around what's already drawn
    maxY += diag_buffer_size;
    
    return maxY;
  }
  
  @Override
  public void setUniqueId(int id)
  {
    unique_id = 0; // Always 0 for diagrams
  }
  
  @Override
  public int getUniqueId()
  {
    return unique_id;
  }
  
  public void ensureUniqueId(DiagramElement e)
  {
    if(e.getUniqueId() == 0) // Not assigned before
    {
      last_unique_id++;
      e.setUniqueId(last_unique_id);
    }
    else // Check ID is unique in diagram
    {
      Iterator<DiagramElement> eIt = elements.iterator();
      while(eIt.hasNext())
      {
        DiagramElement element = eIt.next();
        if(element != e && element.getUniqueId() == e.getUniqueId())
        {
          last_unique_id++;
          e.setUniqueId(last_unique_id);
        }
      }
    }
    if(e.getUniqueId() > last_unique_id)
      last_unique_id = e.getUniqueId();
  }
  
  @Override
  public void setNearElement(DiagramElement e)
  {
    // Nothing to do
  }
  
  @Override
  public boolean isRelationship()
  {
    return false;
  }
  
  @Override
  public void alertDestroyedElement(DiagramElement e)
  {
    // Remove from diagram
    Iterator<DiagramElement> elIt = elements.iterator();
    while(elIt.hasNext())
    {
      DiagramElement element = elIt.next();
      if(e == element)
      {
        elIt.remove();
        break;
      }
    }
  }
  
  @Override
  public DiagramElement cloneElement() throws IOException
  {
    ClassDiagram clonedDiagram = new ClassDiagram(name);
    clonedDiagram.location.x = location.x;
    clonedDiagram.location.y = location.y;
    clonedDiagram.unique_id = unique_id;
    clonedDiagram.last_unique_id = last_unique_id;
    
    Iterator<DiagramElement> elIt = elements.iterator();
    while(elIt.hasNext())
    {
      DiagramElement e = elIt.next();
      clonedDiagram.elements.add(e.cloneElement());
    }
    
    return clonedDiagram;
  }
  
  @Override
  public boolean equivalentTo(DiagramElement e)
  {
    if(e.getElementType().equals(getElementType()))
    {
      ClassDiagram d = (ClassDiagram)e;
      if(d.location.x     != location.x)        return false;
      if(d.location.y     != location.y)        return false;
      if(d.unique_id      != unique_id)         return false;
      if(d.last_unique_id != last_unique_id)    return false;
      if(elements.size()  != d.elements.size()) return false;
      
      Iterator<DiagramElement> myElIt = elements.iterator();
      Iterator<DiagramElement> dElIt  = d.elements.iterator();
      while(myElIt.hasNext())
      {
        DiagramElement myElement = myElIt.next();
        DiagramElement dElement  = dElIt.next();
        if(!myElement.equivalentTo(dElement))
          return false;
      }
    }
    else
      return false;
    
    // If we got to the end, then everything matches
    return true;
  }
  
  @Override
  public String accept(ElementCheckerVisitor visitor)
  {
    return visitor.visit(this);
  }
  
}
