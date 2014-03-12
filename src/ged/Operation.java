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
  private boolean is_static;
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
    is_static = false;
  }
  
  public Operation(Protection p, String ret, String n,
          ArrayList<Parameter> args)
  {
    name = n;
    return_type = ret;
    protection_level = p;
    parameters = args;
    is_static = false;
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
  
  public boolean getStatic()
  {
    return is_static;
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
  
  public void setStatic(boolean stc)
  {
    is_static = stc;
  }
  
  public void addParameter(Parameter param)
  {
    parameters.add(param);
  }
  
  public void deleteParameter(Parameter delParam)
  {
    String pString = delParam.getString();
    Iterator<Parameter> itPrm = parameters.iterator();
    while(itPrm.hasNext())
    {
      Parameter param = itPrm.next();
      if(param.getString().equals(pString))
      {
        itPrm.remove();
        break;
      }
    }
  }
  
  public String getString()
  {
    String str = "";
    if(is_static)
      str += "static ";
    str += protection_level.toString().toLowerCase();
    str += " " + return_type;
    str += " " + name + "(";
    
    boolean first = true;
    Iterator<Parameter> it = parameters.iterator();
    while(it.hasNext())
    {
      Parameter p = it.next();
      if(first)
        first = false;
      else
        str += ", ";
      str += p.getString();
    }
    
    str += ")";
    
    return str;
  }
  
  public String getPersistentRepresentation()
  {
    String rep = "<static>";
    if(is_static)
      rep += "1";
    else
      rep += "0";
    rep += "</static>";
    rep += "<protection>" + protection_level.name() + "</protection>";
    rep += "<returnType>" + return_type + "</returnType>";
    rep += "<name>" + name + "</name>";
    
    Iterator<Parameter> it = parameters.iterator();
    while(it.hasNext())
    {
      Parameter p = it.next();
      rep += "<parameter>" + p.getPersistentRepresentation() + "</parameter>";
    }
    
    return rep;
  }
  
  public static Operation fromPersistentRepresentation(String s)
  {
    Protection p;
    String r;
    String n;
    ArrayList<Parameter> params = new ArrayList<>();
    
    boolean stc = false;
    String stcStr = getValueFromTag(s, "static");
    if(stcStr.equals("1"))
      stc = true;
    p = Protection.valueOf(getValueFromTag(s, "protection"));
    r = getValueFromTag(s, "returnType");
    n = getValueFromTag(s, "name");
    
    String[] pieces = s.split("<parameter>");
    for(int i = 0; i < pieces.length; i++)
    {
      if(pieces[i].contains("</parameter>"))
      {
        pieces[i] = "<parameter>" + pieces[i];
        Parameter param = Parameter.fromPersistentRepresentation(pieces[i]);
        params.add(param);
      }
    }
    
    Operation o = new Operation(p, r, n, params);
    o.setStatic(stc);
    return o;
  }
  
}
