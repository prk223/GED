/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import static ged.Util.getValueFromTag;
import java.awt.Font;
import java.awt.FontMetrics;
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
  
  private boolean is_interface;
  private String name;
  private final ArrayList<Attribute> attributes;
  private final ArrayList<Operation> operations;
  private Point location;
  private final EditClassDialog edit_class_dlg;
  private int width, height;
  private final int min_width, min_height;
  private final int buffer;
  private int unique_id;
  
  
  public ClassElement(String n, int x, int y) throws IOException
  {
    cfg_mgr = ConfigurationManager.getInstance();
    
    location = new Point(x, y);
    is_interface = false;
    name = n;
    attributes = new ArrayList<>();
    operations = new ArrayList<>();
    edit_class_dlg = new EditClassDialog(null, true);
    min_height = Integer.parseInt(
            cfg_mgr.getConfigValue(ConfigurationManager.MIN_CLASS_HEIGHT));
    min_width = Integer.parseInt(
            cfg_mgr.getConfigValue(ConfigurationManager.MIN_CLASS_WIDTH));
    buffer = Integer.parseInt(
            cfg_mgr.getConfigValue(ConfigurationManager.LINE_BFR_SIZE));
    
    unique_id = 0;
  }
  
  public boolean getInterface()
  {
    return is_interface;
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
  
  public void setInterface(boolean iface)
  {
    is_interface = iface;
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
  
  public void insertAttribute(Attribute a, int index)
  {
    attributes.add(index, a);
  }
  
  public void deleteAttribute(Attribute delA)
  {
    String aString = delA.getString();
    Iterator<Attribute> itAttr = attributes.iterator();
    while(itAttr.hasNext())
    {
      Attribute attr = itAttr.next();
      if(attr.getString().equals(aString))
      {
        itAttr.remove();
        break;
      }
    }
  }
  
  public void deleteOperation(Operation delO)
  {
    String oString = delO.getString();
    Iterator<Operation> itOp = operations.iterator();
    while(itOp.hasNext())
    {
      Operation op = itOp.next();
      if(op.getString().equals(oString))
      {
        itOp.remove();
        break;
      }
    }
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
    
    Font oldFont = g.getFont();
    Font newFont = oldFont;
    if(is_interface)
      newFont = oldFont.deriveFont(oldFont.getStyle() | Font.ITALIC);
    g.setFont(newFont);
    
    setWidth(g);
    setHeight(g);
    
    FontMetrics metrics = g.getFontMetrics();
    int charHeight = metrics.getHeight();
    
    int boxHeight = height;
    
    // Draw outline of entire class
    g.drawRect(x, y, width, boxHeight);
    
    boxHeight = 0;
    // Draw interface tag if applicable
    if(is_interface)
    {
      boxHeight += charHeight + buffer;
      g.drawString("<< Interface >>", x + buffer, y + boxHeight);
    }
    
    // Draw name and line after
    boxHeight += charHeight + buffer;
    g.drawString(name, x + buffer, y + boxHeight);
    boxHeight += buffer;
    g.drawRect(x, y, width, boxHeight);
    
    // Draw attributes and line after
    Iterator<Attribute> itAttr = attributes.iterator();
    while(itAttr.hasNext())
    {
      boxHeight += charHeight + buffer;
      Attribute a = itAttr.next();
      g.drawString(a.getString(), x + buffer, y + boxHeight);
    }
    boxHeight += buffer;
    g.drawRect(x, y, width, boxHeight);
    
    // Draw operations
    Iterator<Operation> itOp = operations.iterator();
    while(itOp.hasNext())
    {
      boxHeight += charHeight + buffer;
      Operation o = itOp.next();
      g.drawString(o.getString(), x + buffer, y + boxHeight);
    }
    
    g.setFont(oldFont);
  }
  
  public void setWidth(Graphics g)
  {
    FontMetrics metrics = g.getFontMetrics();
    
    width = min_width; // reset width and grow as needed
    
    // Find longest string to determine width needed
    int lineWidth = buffer + metrics.stringWidth(name) + buffer;
    if(lineWidth > width)
      width = lineWidth;
    
    Iterator<Attribute> itAttr = attributes.iterator();
    while(itAttr.hasNext())
    {
      Attribute a = itAttr.next();
      lineWidth = buffer + metrics.stringWidth(a.getString()) + buffer;
      if(lineWidth > width)
        width = lineWidth;
    }
    
    Iterator<Operation> itOp = operations.iterator();
    while(itOp.hasNext())
    {
      Operation o = itOp.next();
      lineWidth = buffer + metrics.stringWidth(o.getString()) + buffer;
      if(lineWidth > width)
        width = lineWidth;
    }
  }
  
  public void setHeight(Graphics g)
  {
    FontMetrics metrics = g.getFontMetrics();
    int charHeight = metrics.getHeight() + 2;
    
    height = 0;
    
    if(is_interface)
      height += charHeight + buffer;
    
    // Height of name portion
    height += buffer + charHeight + buffer;
    
    // Move past all attribute lines
    height += attributes.size() *(charHeight + buffer);
    
    // Move past all operation lines
    height += operations.size() * (charHeight + buffer);
    
    if(height < min_height)
      height = min_height;
  }
  
  
  @Override
  public Point getLocation()
  {
    return new Point(location.x, location.y);
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
    String rep = "<uniqueID>" + unique_id + "</uniqueID>";
    
    rep += "<location>";
    rep += Integer.toString(location.x) + "," + Integer.toString(location.y);
    rep += "</location>";
    
    rep += "<interface>";
    if(is_interface)
      rep += "1";
    else
      rep += "0";
    rep += "</interface>";
    
    rep += "<name>" + name + "</name>";
    
    Iterator<Attribute> attrIt = attributes.iterator();
    while(attrIt.hasNext())
    {
      Attribute a = attrIt.next();
      rep += "<attribute>" + a.getPersistentRepresentation() + "</attribute>";
    }
    
    Iterator<Operation> opIt = operations.iterator();
    while(opIt.hasNext())
    {
      Operation o = opIt.next();
      rep += "<operation>" + o.getPersistentRepresentation() + "</operation>";
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
    
    int uid = Integer.parseInt(getValueFromTag(s, "uniqueID"));
    e.setUniqueId(uid);
    
    String ifaceStr = getValueFromTag(s, "interface");
    if(ifaceStr.equals("1"))
      e.setInterface(true);
    else
      e.setInterface(false);
    
    String[] attr_pieces = s.split("<attribute>");
    for(int i = 0; i < attr_pieces.length; i++)
    {
      if(attr_pieces[i].contains("</attribute>"))
      {
        attr_pieces[i] = "<attribute>" + attr_pieces[i];
        Attribute a = Attribute.fromPersistentRepresentation(attr_pieces[i]);
        e.addAttribute(a);
      }
    }
    String[] op_pieces = s.split("<operation>");
    for(int i = 0; i < op_pieces.length; i++)
    {
      if(op_pieces[i].contains("</operation>"))
      {
        op_pieces[i] = "<operation>" + op_pieces[i];
        Operation o = Operation.fromPersistentRepresentation(op_pieces[i]);
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
  
  @Override
  public void displayEditGui()
  {
    edit_class_dlg.open(this);
  }
  
  @Override
  public int getMaxX()
  {
    return location.x + width;
  }
  
  @Override
  public int getMaxY()
  {
    return location.y + height;
  }
  
  @Override
  public void setUniqueId(int id)
  {
    unique_id = id;
  }
  
  @Override
  public int getUniqueId()
  {
    return unique_id;
  }
  
  @Override
  public void setNearElement(DiagramElement e)
  {
    // Nothing to do
  }
}
