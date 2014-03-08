/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import static ged.Util.getValueFromTag;
import java.awt.Graphics;
import java.awt.Point;
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
public class Diagram implements DiagramElement
{
  private final String name;
  private final ArrayList<DiagramElement> elements;
  private Point location;
  
  public Diagram(String diagName)
  {
    location = new Point(0, 0);
    name = diagName;
    elements = new ArrayList<>();
  }
  
  public String getName()
  {
    return name;
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

  public static Diagram loadDiagram(String filePath)
  {
    Diagram loadedDiagram = null;
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
  }
  
  /**
   *
   * @param g
   */
  @Override
  public void draw(Graphics g)
  {
    // TODO set bounds of draw space, only draw what's needed
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
  
  public static Diagram fromStringRepresentation(String s) throws IOException
  {
    Diagram d;
    
    String n = getValueFromTag(s, "diagram");
    d = new Diagram(n);
    
    String[] strArr = s.split("\n");
    for(int i = 0; i < strArr.length; i++)
    {
      String startTag = "<element:";
      if(strArr[i].contains(startTag))
      {
        String type = "ERROR";
        int typeStart = strArr[i].indexOf(startTag) + startTag.length();
        int typeEnd = strArr[i].indexOf(">", typeStart);
        if(typeEnd > typeStart)
          type = strArr[i].substring(typeStart, typeEnd);
        String elementString = getValueFromTag(strArr[i], "element:" + type);
        switch(type)
        {
          case "Class":
            DiagramElement e = ClassElement.fromPersistentRepresentation(strArr[i]);
            d.addElement(e);
            break;
          default:
            System.err.println("ERR:Unknown element:" + type);
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
  
}
