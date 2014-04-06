/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import static ged.Util.getValueFromTag;

/**
 *
 * @author Comp
 */
public class Attribute
{
  private boolean is_static;
  private Protection protection_level;
  private String type;
  private String name;
  
  public Attribute(Protection p, String t, String n)
  {
    name = n;
    type = t;
    protection_level = p;
    is_static = false;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getType()
  {
    return type;
  }
  
  public Protection getProtectionLevel()
  {
    return protection_level;
  }
  
  public boolean getStatic()
  {
    return is_static;
  }
  
  public void setName(String n)
  {
    name = n;
  }
  
  public void setType(String t)
  {
    type = t;
  }
  
  public void setProtectionLevel(Protection p)
  {
    protection_level = p;
  }
  
  public void setStatic(boolean stc)
  {
    is_static = stc;
  }
  
  public String getString()
  {
    String str = "";
    if(is_static)
      str = "static ";
    str += protection_level.toString().toLowerCase();
    str += " " + type;
    str += " " + name;
    
    return str;
  }
  
  public String getPersistentRepresentation()
  {
    String rep = "<static>";
    if(is_static) rep += "1";
    else rep += "0";
    rep += "</static>";
    rep += "<protection>" + protection_level.name();
    rep += "</protection>";
    rep += "<type>" + type + "</type>";
    rep += "<name>" + name + "</name>";
    return rep;
  }
  
  public static Attribute fromPersistentRepresentation(String s)
  {
    Protection p;
    String t, n;
    
    boolean stc = false;
    String stc_str = getValueFromTag(s, "static");
    if(stc_str.equals("1"))
      stc = true;
    p = Protection.valueOf(getValueFromTag(s, "protection"));
    t = getValueFromTag(s, "type");
    n = getValueFromTag(s, "name");
    
    Attribute a = new Attribute(p, t, n);
    a.setStatic(stc);
    
    return a;
  }
  
  public Attribute cloneAttribute()
  {
    Attribute clonedAttribute = new Attribute(protection_level, type, name);
    clonedAttribute.is_static = is_static;
    return clonedAttribute;
  }
  
}
