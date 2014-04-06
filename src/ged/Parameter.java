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
  
  public String getString()
  {
    return type + " " + name;
  }
  
  public String getPersistentRepresentation()
  {
    String rep = "<type>" + type + "</type>";
    rep += "<name>" + name + "</name>";
    return rep;
  }
  
  public static Parameter fromPersistentRepresentation(String s)
  {
    String t, n;
    
    t = getValueFromTag(s, "type");
    n = getValueFromTag(s, "name");
    
    return new Parameter(t, n);
  }
  
  public Parameter cloneParameter()
  {
    Parameter clonedParameter = new Parameter(type, name);
    return clonedParameter;
  }
  
  public boolean equivalentTo(Parameter p)
  {
    if(!p.type.equals(type)) return false;
    if(!p.name.equals(name)) return false;
    
    return true;
  }
}
