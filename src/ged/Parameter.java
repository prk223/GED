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
public class Parameter
{
  private String type;
  private String name;
  
  public Parameter(String t, String n)
  {
    name = n;
    type = t;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getType()
  {
    return type;
  }
  
  public void setName(String n)
  {
    name = n;
  }
  
  public void setType(String t)
  {
    type = t;
  }
  
  public String getStringRepresentation()
  {
    String rep = "<type>" + type + "</type>\n";
    rep += "<name>" + name + "</name>\n";
    return rep;
  }
  
  public static Parameter fromStringRepresentation(String s)
  {
    String t, n;
    
    t = getValueFromTag(s, "type");
    n = getValueFromTag(s, "name");
    
    return new Parameter(t, n);
  }
}
