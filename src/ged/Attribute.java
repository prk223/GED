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
  private Protection protection_level;
  private String type;
  private String name;
  
  public Attribute(Protection p, String t, String n)
  {
    name = n;
    type = t;
    protection_level = p;
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
  
  public String getStringRepresentation()
  {
    String rep = "<protection>" + protection_level.name();
    rep += "</protection>";
    rep += "<type>" + type + "</type>";
    rep += "<name>" + name + "</name>";
    return rep;
  }
  
  public static Attribute fromStringRepresentation(String s)
  {
    Protection p;
    String t, n;
    
    p = Protection.valueOf(getValueFromTag(s, "protection"));
    t = getValueFromTag(s, "type");
    n = getValueFromTag(s, "name");
    
    return new Attribute(p, t, n);
  }
  
}
