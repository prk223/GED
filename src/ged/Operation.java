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
    
    boolean stc = false;
    String stcStr = getValueFromTag(s, "static");
    if(stcStr.equals("1"))
      stc = true;
    p = Protection.valueOf(getValueFromTag(s, "protection"));
    r = getValueFromTag(s, "returnType");
    n = getValueFromTag(s, "name");
    
    Operation o = new Operation(p, r, n);
    
    String[] pieces = s.split("<parameter>");
    for(int i = 0; i < pieces.length; i++)
    {
      if(pieces[i].contains("</parameter>"))
      {
        pieces[i] = "<parameter>" + pieces[i];
        Parameter param = Parameter.fromPersistentRepresentation(pieces[i]);
        o.parameters.add(param);
      }
    }
    
    o.setStatic(stc);
    return o;
  }
  
  public Operation cloneOperation()
  {
    Operation clonedOperation = new Operation(protection_level, return_type, 
            name);
    clonedOperation.is_static = is_static;
    
    Iterator<Parameter> pIt = parameters.iterator();
    while(pIt.hasNext())
    {
      Parameter p = pIt.next();
      clonedOperation.parameters.add(p.cloneParameter());
    }
    
    return clonedOperation;
  }
  
  public boolean equivalentTo(Operation o)
  {
    if(o.protection_level != protection_level)   return false;
    if(!o.return_type.equals(return_type))       return false;
    if(!o.name.equals(name))                     return false;
    if(o.parameters.size() != parameters.size()) return false;
    
    Iterator<Parameter> myParamIt = parameters.iterator();
    Iterator<Parameter> oParamIt  = o.parameters.iterator();
    while(myParamIt.hasNext())
    {
      Parameter myP = myParamIt.next();
      Parameter oP  = oParamIt.next();
      if(!oP.equivalentTo(myP)) return false;
    }
    
    // Everything is equivalent
    return true;
  }
  
}
