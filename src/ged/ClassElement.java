/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import static ged.Util.getValueFromTag;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Comp
 */
public class ClassElement implements DiagramElement
{
  private String name;
  private final ArrayList<Attribute> attributes;
  private final ArrayList<Operation> operations;
  private Point location;
  
  public ClassElement(String n, int x, int y)
  {
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
    
    g.drawRect(x, y, 50, 100);
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
  public String getStringRepresentation()
  {
    String rep = "<location>";
    rep += Integer.toString(location.x) + "," + Integer.toString(location.y);
    rep += "</location>\n";
    rep += "<name>" + name + "</name>\n";
    
    Iterator<Attribute> attrIt = attributes.iterator();
    while(attrIt.hasNext())
    {
      Attribute a = attrIt.next();
      rep += "<attribute>" + a.getStringRepresentation() + "</attribute>\n";
    }
    
    Iterator<Operation> opIt = operations.iterator();
    while(opIt.hasNext())
    {
      Operation o = opIt.next();
      rep += "<operation>" + o.getStringRepresentation() + "</operation>\n";
    }
    
    return rep;
  }
  
  public static ClassElement fromStringRepresentation(String s)
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
        Attribute a = Attribute.fromStringRepresentation(strArr[i]);
        e.addAttribute(a);
      }
      else if(strArr[i].contains("<operation>"))
      {
        Operation o = Operation.fromStringRepresentation(strArr[i]);
        e.addOperation(o);
      }
    }
    
    return e;
  }
}
