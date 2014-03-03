/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import static ged.Util.getValueFromTag;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Comp
 */
public class Operation
{
  private Protection protection_level;
  private String return_type;
  private String name;
  private final ArrayList<Parameter> parameters;
  
  public Operation(Protection p, String ret, String n)
  {
    name = n;
    return_type = ret;
    protection_level = p;
    parameters = new ArrayList<>();
  }
  
  public Operation(Protection p, String ret, String n,
          ArrayList<Parameter> args)
  {
    name = n;
    return_type = ret;
    protection_level = p;
    parameters = args;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getReturnType()
  {
    return return_type;
  }
  
  public Protection getProtectionLevel()
  {
    return protection_level;
  }
  
  public ArrayList<Parameter> getParameters()
  {
    return parameters;
  }
  
  public void setName(String n)
  {
    name = n;
  }
  
  public void setReturnType(String ret)
  {
    return_type = ret;
  }
  
  public void setProtectionLevel(Protection p)
  {
    protection_level = p;
  }
  
  public void addParameter(Parameter param)
  {
    parameters.add(param);
  }
  
  public String getStringRepresentation()
  {
    String rep = "<protection>" + protection_level.name() + "</protection>\n";
    rep += "<returnType>" + return_type + "</returnType>\n";
    rep += "<name>" + name + "</name>\n";
    
    Iterator<Parameter> it = parameters.iterator();
    while(it.hasNext())
    {
      Parameter p = it.next();
      rep += "<parameter>" + p.getStringRepresentation() + "</parameter>\n";
    }
    
    return rep;
  }
  
  public static Operation fromStringRepresentation(String s)
  {
    Protection p;
    String r;
    String n;
    ArrayList<Parameter> params = new ArrayList<>();
    
    p = Protection.valueOf(getValueFromTag(s, "protection"));
    r = getValueFromTag(s, "returnType");
    n = getValueFromTag(s, "name");
    
    String[] pieces = s.split("\n");
    for(int i = 0; i < pieces.length; i++)
    {
      if(pieces[i].contains("<parameter>"))
      {
        Parameter param = Parameter.fromStringRepresentation(pieces[i]);
        params.add(param);
      }
    }
    
    return new Operation(p, r, n, params);
  }
  
}
