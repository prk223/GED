/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import static ged.Util.getValueFromTag;
import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Comp
 */
public class ClassElement implements DiagramElement
{
  private final ConfigurationManager cfg_mgr;
  
  private String name;
  private final ArrayList<Attribute> attributes;
  private final ArrayList<Operation> operations;
  private Point location;
  
  public ClassElement(String n, int x, int y) throws IOException
  {
    cfg_mgr = ConfigurationManager.getInstance();
    
    location = new Point(x, y);
    name = n;
    attributes = new ArrayList<>();
    operations = new ArrayList<>();
  }
  
  public String getName()
  {
    return name;
  }
  
  public ArrayList<Attribute> getAttributes()
  {
    return attributes;
  }
  
  public ArrayList<Operation> getOperations()
  {
    return operations;
  }
  
  public void setName(String n)
  {
    name = n;
  }
  
  public void addAttribute(String n, String t, Protection p)
  {
    Attribute a = new Attribute(p, t, n);
    attributes.add(a);
  }
  
  public void addAttribute(Attribute a)
  {
    attributes.add(a);
  }
  
  public void addOperation(String n, String ret, Protection p, 
          ArrayList<Parameter> args)
  {
    Operation o = new Operation(p, ret, n, args);
    operations.add(o);
  }
  
  public void addOperation(Operation o)
  {
    operations.add(o);
  }
  
  @Override
  public void draw(Graphics g)
  {
    int x = location.x;
    int y = location.y;
    int width = getWidth();
    int height = getHeight();
    int charHeight = Integer.parseInt(
            cfg_mgr.getConfigValue(ConfigurationManager.CHAR_HEIGHT));
    
    // Draw outline of entire class
    g.drawRect(x, y, width, height);
    
    // Draw name and line after
    g.drawString(name, x+1, y+1);
    height = charHeight + 1;
    g.drawRect(x, y, width, height);
    
    // Draw attributes and line after
    Iterator<Attribute> itAttr = attributes.iterator();
    while(itAttr.hasNext())
    {
      Attribute a = itAttr.next();
      g.drawString(a.getString(), x+1, height+1);
      height += charHeight;
    }
    g.drawRect(x, y, width, height);
    
    // Draw operations
    Iterator<Operation> itOp = operations.iterator();
    while(itOp.hasNext())
    {
      Operation o = itOp.next();
      g.drawString(o.getString(), x+1, height+1);
      height += charHeight;
    }
  }
  
  public int getWidth()
  {
    int minWidth = Integer.parseInt(
            cfg_mgr.getConfigValue(ConfigurationManager.MIN_CLASS_WIDTH));
    int charWidth = Integer.parseInt(
            cfg_mgr.getConfigValue(ConfigurationManager.CHAR_WIDTH));
    
    // Find longest string to determine width needed
    int width = minWidth;
    int lineWidth = name.length() * charWidth;
    if(lineWidth > width)
      width = lineWidth;
    
    Iterator<Attribute> itAttr = attributes.iterator();
    while(itAttr.hasNext())
    {
      Attribute a = itAttr.next();
      lineWidth = a.getString().length() * charWidth;
      if(lineWidth > width)
        width = lineWidth;
    }
    
    Iterator<Operation> itOp = operations.iterator();
    while(itOp.hasNext())
    {
      Operation o = itOp.next();
      lineWidth = o.getString().length() * charWidth;
      if(lineWidth > width)
        width = lineWidth;
    }
    
    return width;
  }
  
  public int getHeight()
  {
    int minHeight = Integer.parseInt(
            cfg_mgr.getConfigValue(ConfigurationManager.MIN_CLASS_HEIGHT));
    int charHeight = Integer.parseInt(
            cfg_mgr.getConfigValue(ConfigurationManager.CHAR_HEIGHT));
    
    // Find longest string to determine height needed
    int height = charHeight; // Move past name portion
    
    // Move past all attribute lines
    height += attributes.size() * charHeight;
    
    // Move past all operation lines
    height += operations.size() * charHeight;
    
    if(height < minHeight)
      height = minHeight;
    
    return height;
  }
  
  
  @Override
  public Point getLocation()
  {
    return location;
  }
  
  @Override
  public void setLocation(Point loc)
  {
    location = loc;
  }
  
  @Override
  public String getElementType()
  {
    return "Class";
  }

  @Override
  public String getPersistentRepresentation()
  {
    String rep = "<location>";
    rep += Integer.toString(location.x) + "," + Integer.toString(location.y);
    rep += "</location>\n";
    rep += "<name>" + name + "</name>\n";
    
    Iterator<Attribute> attrIt = attributes.iterator();
    while(attrIt.hasNext())
    {
      Attribute a = attrIt.next();
      rep += "<attribute>" + a.getPersistentRepresentation() + "</attribute>\n";
    }
    
    Iterator<Operation> opIt = operations.iterator();
    while(opIt.hasNext())
    {
      Operation o = opIt.next();
      rep += "<operation>" + o.getPersistentRepresentation() + "</operation>\n";
    }
    
    return rep;
  }
  
  public static ClassElement fromPersistentRepresentation(String s) throws IOException
  {
    int x = 0;
    int y = 0;
    String n;
    ClassElement e;
    
    String locStr = getValueFromTag(s, "location");
    String[] locArr = locStr.split(",");
    if(locArr.length > 1)
    {
      x = Integer.parseInt(locArr[0]);
      y = Integer.parseInt(locArr[1]);
    }
    
    n = getValueFromTag(s, "name");
    
    e = new ClassElement(n, x, y);
    
    String[] strArr = s.split("\n");
    for(int i = 0; i < strArr.length; i++)
    {
      if(strArr[i].contains("<attribute>"))
      {
        Attribute a = Attribute.fromPersistentRepresentation(strArr[i]);
        e.addAttribute(a);
      }
      else if(strArr[i].contains("<operation>"))
      {
        Operation o = Operation.fromPersistentRepresentation(strArr[i]);
        e.addOperation(o);
      }
    }
    
    return e;
  }
  
  @Override
  public double getDistanceFrom(int x, int y)
  {
    int myX = location.x;
    int myY = location.y;
    int height = getHeight();
    int width  = getWidth();
    double distance;
    
    if(x < myX) // left of me
    {
      if(y < myY) // above me
      {
        int a = myX - x;
        int b = myY - y;
        distance = Math.sqrt((a*a) + (b*b));
      }
      else if(y > (myY + height)) // below me
      {
        int a = myX - x;
        int b = y - (myY + height);
        distance = Math.sqrt((a*a) + (b*b));
      }
      else
        distance = myX - x;
    }
    else if(x > (myX + width)) // right of me
    {
      if(y < myY) // above me
      {
        int a = x - (myX + width);
        int b = myY - y;
        distance = Math.sqrt((a*a) + (b*b));
      }
      else if(y > (myY + height)) // below me
      {
        int a = x - (myX + width);
        int b = y - (myY + height);
        distance = Math.sqrt((a*a) + (b*b));
      }
      else
        distance = x - (myX + width);
    }
    else // x is lined up with me
    {
      if(y < myY) // above me
        distance = myY - y;
      else if(y > (myY + height)) // below me
        distance = y - (myY + height);
      else // inside me
        distance = 0.0;
    }
    
    return distance;
  }
}
